package pl.polsl.aei.mateusz.agerecognizer.train;

import com.google.gson.Gson;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.polsl.aei.mateusz.agerecognizer.exceptions.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.files.AgeToWrinkleJsonFile;
import pl.polsl.aei.mateusz.agerecognizer.files.FileFactory;
import pl.polsl.aei.mateusz.agerecognizer.files.FileProduct;
import pl.polsl.aei.mateusz.agerecognizer.files.FileType;
import pl.polsl.aei.mateusz.agerecognizer.utils.HogConfig;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.AgeToWrinkleFeature;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.AgeToWrinkleFeatureHogKnn;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeatureCalculator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trainer {

    final static HogConfig hogConfig = new HogConfig(true, 9, 9, true);
    static boolean originalRectangles = false; //true - method from document, false - without area between eyes
    static boolean isCroppedToFace = false; //true - method from document, false - without area between eyes

    private static final Logger log = LogManager.getLogger("main");
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
    private static final Logger heapLog = LogManager.getLogger("heap");
    private static FileProduct clusteredJson;

    public static void generateDataFromImages(String trainingSetPrefix, File imagesPath, boolean cropOnlyToFace) {
        long startTime = System.currentTimeMillis();
        int invalidProcessedImages = 0;
        int validProcessedImages = 0;
        Runtime r = Runtime.getRuntime();

        File[] imagesInDir = imagesPath.listFiles(pathname -> pathname.getName().contains(".")); //only files
        assert imagesInDir != null;
        if (imagesInDir.length == 0) {
            log.error("No files in this subfolder!");
        }
        log.debug("Starting training... originalRectangles: " + originalRectangles + " " + hogConfig);
//        initializeOutputFilesWriters(trainingSetPrefix, subPathOfImages);
        //create file for results
        FileProduct ageToWrinkleJson = FileFactory.create(FileType.ageToWrinkleJson);
        int integerCounter = AgeToWrinkleJsonFile.nextIntegerCounter(); //next integer counter in suffix of file name
        String suffixOfFile = String.format("%s_%d", trainingSetPrefix, integerCounter);
        ageToWrinkleJson.createFileWithSuffix(suffixOfFile);

        for (File image : imagesInDir) {
            WrinkleFeatureCalculator wrinkleFeatureCalculator;
//            heapLog.info("Heap size in %: " + r.totalMemory() * 100 / r.maxMemory());
            try {
                if (!cropOnlyToFace) {
                    wrinkleFeatureCalculator = new WrinkleFeatureCalculator(image, isCroppedToFace, originalRectangles,
                            hogConfig);
                } else {
                    wrinkleFeatureCalculator = new WrinkleFeatureCalculator(image);
                }
            } catch (WrinkleFeaturesException e) {
                invalidProcessedImages++;
                String wrinkleFeatureExceptionMessage = e.getMessage();
                log.error(wrinkleFeatureExceptionMessage);
                continue;
            } catch (Error e) {
                invalidProcessedImages++;
                String errorMessage = "Error: Path: " + image.getName();
                log.error(errorMessage);
                continue;
            } catch (Exception e) {
                invalidProcessedImages++;
                String exceptionMessage = "Exception: Path: " + image.getAbsolutePath();
                log.error(exceptionMessage);
                continue;
            }
            //Succesfully processed wrinkle feature
            validProcessedImages++;
            float wrinkleFeatureResult = wrinkleFeatureCalculator.getWrinkleFeatures();
            float[] wrinkleFeaturesFromHogKnn = wrinkleFeatureCalculator.getWrinkleFeaturesKNN();
            byte age = getAgeFromPath(image.getName());
            if (hogConfig.isHogKnn()) {
                ageToWrinkleJson.writeln(new AgeToWrinkleFeatureHogKnn(age, wrinkleFeaturesFromHogKnn,
                        image.getName()));
                log.info(String.format("Success!: Path: %s: (age: %d | feature: %s)", image.getName(), age,
                        Arrays.toString(wrinkleFeaturesFromHogKnn)));
            } else {
                ageToWrinkleJson.writeln(new AgeToWrinkleFeature(age, wrinkleFeatureResult, image.getName())); //write result to file
                log.info(String.format("Success!: Path: %s: (age: %d | feature: %f)", image.getName(), age, wrinkleFeatureResult));
            }
            log.info("Detected objects " + wrinkleFeatureCalculator.getDetectedObjects());
            log.info("Wrinkle areas " + wrinkleFeatureCalculator.getWrinkleAreas());
        }
        generateStats(startTime, invalidProcessedImages, validProcessedImages);
    }

    private static void generateStats(long startTime, int invalidProcessedImages, int validProcessedImages) {
        String correctlyProcessedImages = "Correctly processed images: " + validProcessedImages;
        String incorrectlyProcessedImages = "Incorrectly processed images: " + invalidProcessedImages;
        String processedImages = "Sum of processed images: " + (invalidProcessedImages + validProcessedImages);
        String executionTime = "Execution time: " + ((float) (System.currentTimeMillis() - startTime) / 1000) + " s";

        log.info(correctlyProcessedImages);
        log.info(incorrectlyProcessedImages);
        log.info(processedImages);
        log.info(executionTime);
    }

    private static void clusterDataByFuzzyKMeans(List<AgeToWrinkleFeature> ageToWrinkleFeatureList) {
        FuzzyKMeansClusterer fuzzyKMeansClusterer = new FuzzyKMeansClusterer(80, 10);
        if (ageToWrinkleFeatureList.size() == 0) {
            log.error("No data!");
            return;
        }
        fuzzyKMeansClusterer.cluster(ageToWrinkleFeatureList);
        List<CentroidCluster> listOfCentroids = fuzzyKMeansClusterer.getClusters();
        Gson gson = new Gson();
        clusteredJson.writeln(gson.toJson(listOfCentroids));
    }


    public static byte getAgeFromPath(String image) {
        try {
            String processedString = image.subSequence(0, 3).toString();
            String[] numbers = processedString.split("_");

            if (numbers.length == 0) {
                return -1;
            } else {
                return Byte.parseByte(numbers[0]);
            }

        } catch (NumberFormatException e) {
            log.error("NumberFormatException: " + image);
            return -1;
        }
    }

    /**
     * Cluster data by age to wrinkle features set
     * Program zawiesza się, więc obliczanie clusterow odbywa się za pomocą Matlaba
     */
    public static void clusterDataByFuzzyKMeans(String mergedAgeToWrinkleFeatureJsonPath) {
        List<AgeToWrinkleFeature> ageToWrinkleFeatures = new ArrayList<>();
        Gson gson = new Gson();

        clusteredJson = FileFactory.create(FileType.clusteredJson);
        clusteredJson.createFileWithSuffix(mergedAgeToWrinkleFeatureJsonPath);
        //First line of .txt
        clusteredJson.writeln("Clustered from: " + mergedAgeToWrinkleFeatureJsonPath);

        try (BufferedReader br = new BufferedReader(new FileReader(mergedAgeToWrinkleFeatureJsonPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                ageToWrinkleFeatures.add(gson.fromJson(line, AgeToWrinkleFeature.class));
            }

        } catch (IOException e) {
            log.catching(e);
        }

        clusterDataByFuzzyKMeans(ageToWrinkleFeatures);
    }

    public static void mergeAgeToWrinkleFeaturesFromJsons(String trainingSetPath) {

        FileProduct mergedJson = FileFactory.create(FileType.mergedJson);
        FileProduct matlabDat = FileFactory.create(FileType.matlabDat);
        mergedJson.createFileWithSuffix(trainingSetPath);
        matlabDat.createFileWithSuffix(trainingSetPath);

        File file = new File(propertiesLoader.getProperty("pathToData"));

        File[] listOfFiles = file.listFiles(pathname -> pathname.getName()
                .contains("ageToWrinkleFeaturesJson" + trainingSetPath));
        assert listOfFiles != null;
        for (File json : listOfFiles) {

            try (BufferedReader br = new BufferedReader(new FileReader(json))) {
                String line;
                while ((line = br.readLine()) != null) {
                    mergedJson.writeln(line);
                    matlabDat.writeln(line);
                }
            } catch (IOException e) {
                log.catching(e);
            }
        }
    }

    public static void mergeAgeToWrinkleFeaturesFromJsonsHogKnn(String trainingSetPath) {
        FileProduct mergedJson = FileFactory.create(FileType.mergedJson);
        FileProduct matlabDatHogKnn = FileFactory.create(FileType.matlabDatHogKnn);
        mergedJson.createFileWithSuffix(trainingSetPath);
        matlabDatHogKnn.createFileWithSuffix(trainingSetPath);

        File file = new File(propertiesLoader.getProperty("pathToData"));

        File[] listOfFiles = file.listFiles(pathname -> pathname.getName()
                .contains("ageToWrinkleFeaturesJson" + trainingSetPath));
        assert listOfFiles != null;
        for (File json : listOfFiles) {

            try (BufferedReader br = new BufferedReader(new FileReader(json))) {
                String line;
                while ((line = br.readLine()) != null) {
                    mergedJson.writeln(line);
                    matlabDatHogKnn.writeln(line);
                }
            } catch (IOException e) {
                log.catching(e);
            }
        }
    }
}
