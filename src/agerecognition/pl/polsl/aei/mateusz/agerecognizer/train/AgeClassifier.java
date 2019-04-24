package pl.polsl.aei.mateusz.agerecognizer.train;

import com.google.gson.Gson;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.polsl.aei.mateusz.agerecognizer.utils.AgeToWrinkleFeature;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;
import pl.polsl.aei.mateusz.agerecognizer.utils.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.utils.files.FileFactory;
import pl.polsl.aei.mateusz.agerecognizer.utils.files.FileProduct;
import pl.polsl.aei.mateusz.agerecognizer.utils.files.FileType;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeature;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgeClassifier {

    private static final Logger log = LogManager.getLogger("main");
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
    private static PrintStream clusteredJson;
    private static PrintStream mergedJson;
    private static PrintStream matlabDat;
    private static int invalidProcessedImages = 0;
    private static int validProcessedImages = 0;

    public static void generateDataFromImages(String trainingSetPrefix, String subPathOfImages) {
        long startTime = System.currentTimeMillis();

        File files = new File(propertiesLoader.getProperty("trainingImagesPath") + subPathOfImages);
        File[] imagesInDir = files.listFiles(pathname -> pathname.getName().contains(".")); //only files
        if (imagesInDir.length == 0) {
            log.error("No files in this subfolder!");
        }

//        initializeOutputFilesWriters(trainingSetPrefix, subPathOfImages);
        //create file for results
        FileProduct ageToWrinkleJson = FileFactory.create(FileType.ageToWrinkleJson);
        int integerCounter = ageToWrinkleJson.nextIntegerCounter(); //next integer counter in suffix in file name
        String suffixOfFile = String.format("%s_%d", trainingSetPrefix, integerCounter);
        ageToWrinkleJson.createFileWithSuffix(suffixOfFile);

        for (File image : imagesInDir) {
            WrinkleFeature wrinkleFeature;

            try {
                wrinkleFeature = new WrinkleFeature(image, false);
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
                String exceptionMessage = "Exception: Path: " + image.getName();
                log.error(exceptionMessage);
                continue;
            }
            //Succesfully processed wrinkle feature
            validProcessedImages++;
            float wrinkleFeatureResult = wrinkleFeature.getWrinkleFeatures();
            byte age = getAgeFromPath(image.getName());
            ageToWrinkleJson.writeln(new AgeToWrinkleFeature(age, wrinkleFeatureResult)); //write result to file

            log.info(String.format("Success!: Path: %s: (age: %d | feature: %f)", image.getName(), age, wrinkleFeatureResult));
        }
        generateStats(startTime);
    }

    private static void generateStats(long startTime) {
        String correctlyProcessedImages = "\n\nCorrectly processed images: " + validProcessedImages;
        String incorrectlyProcessedImages = "Incorrectly processed images: " + invalidProcessedImages;
        String processedImages = "Sum of processed images: " + (invalidProcessedImages + validProcessedImages);
        String exexutionTime = "Execution time: " + ((float) (System.currentTimeMillis() - startTime) / 1000) + " s";

        log.info("%s\n%s\n%s\n%s", correctlyProcessedImages, incorrectlyProcessedImages, processedImages, exexutionTime);
    }

    private static void clusterDataByFuzzyKMeans(List<AgeToWrinkleFeature> ageToWrinkleFeatureList) {
        FuzzyKMeansClusterer fuzzyKMeansClusterer = new FuzzyKMeansClusterer(80, 10);
        if (ageToWrinkleFeatureList.size() == 0) {
            log.error("No data!");
            return;
        }
        fuzzyKMeansClusterer.cluster(ageToWrinkleFeatureList);
        List<CentroidCluster> listOfCentroids = fuzzyKMeansClusterer.getClusters();
        clusteredJson.println(objectToJSON(listOfCentroids));
    }

    private static String objectToJSON(Object object) {
        return new Gson().toJson(object);
    }


    private static byte getAgeFromPath(String image) {
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
     * Returning calculated age.
     *
     * @param wrinkleFeature wrinkle feature float from image.
     * @return
     */
    public static int detectAgeFromWrinkleFeature(float wrinkleFeature) {
        //TODO Wykrywanie wieku na podstawie klasyfikatora
        return (int) (1 / (Math.pow(wrinkleFeature, 2) * 1.98531412899040e-05
                + 0.000912187996975040 * wrinkleFeature
                + 0.000963979767558933));
    }

    /**
     * Cluster data by age to wrinkle features set
     *
     * @param mergedAgeToWrinkleFeatureJsonPath
     * @info Program zawiesza się, więc obliczanie clusterow odbywa się za pomocą Matlaba
     */
    public static void clusterDataByFuzzyKMeans(String mergedAgeToWrinkleFeatureJsonPath) {
        List<AgeToWrinkleFeature> ageToWrinkleFeatures = new ArrayList<>();
        Gson gson = new Gson();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        try {
            clusteredJson = new PrintStream(new FileOutputStream( //TODO fileproduct
                    new File(propertiesLoader.getProperty("clusteredJsonData") + timeStamp + ".txt")));
        } catch (FileNotFoundException e) {
            log.catching(e);
        }
        //First line of .txt
        clusteredJson.println("Clustered from: " + mergedAgeToWrinkleFeatureJsonPath);

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
        try {
            mergedJson = new PrintStream(new FileOutputStream( //TODO fileproduct
                    new File(propertiesLoader.getProperty("mergedAgeToWrinkleFeaturesData") + trainingSetPath + ".txt")));
            matlabDat = new PrintStream(new FileOutputStream(
                    new File(propertiesLoader.getProperty("mergedAgeToWrinkleFeaturesData") + trainingSetPath + ".dat")));

        } catch (FileNotFoundException e) {
            log.catching(e);
        }
        File file = new File(propertiesLoader.getProperty("pathToData"));

        File[] listOfFiles = file.listFiles(pathname -> pathname.getName()
                .contains("ageToWrinkleFeaturesJson" + trainingSetPath));
        assert listOfFiles != null;
        for (File json : listOfFiles) {

            try (BufferedReader br = new BufferedReader(new FileReader(json))) {
                String line;
                while ((line = br.readLine()) != null) {
                    mergedJson.println(line);
                    matlabDat.println(matlabRecordFromJson(line));
                }
            } catch (IOException e) {
                log.catching(e);
            }
        }
    }

    private static String matlabRecordFromJson(String line) {
        Gson gson = new Gson();

        AgeToWrinkleFeature ageToWrinkleFeature = gson.fromJson(line, AgeToWrinkleFeature.class);

        return String.format("   %s   %s"
                , ageToWrinkleFeature.age
                , ageToWrinkleFeature.wrinkleFeature);
    }
}
