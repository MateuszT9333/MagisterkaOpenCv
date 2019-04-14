package pl.polsl.aei.mateusz.agerecognizer.train;

import com.google.gson.Gson;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import pl.polsl.aei.mateusz.agerecognizer.utils.AgeToWrinkleFeature;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;
import pl.polsl.aei.mateusz.agerecognizer.utils.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeature;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgeClassifier {

    private static PrintStream log;
    private static PrintStream ageToWrinkleJson;
    private static PrintStream clusteredJson;
    private static PrintStream mergedJson;
    private static PrintStream matlabDat;


    public static void generateDataFromImages(String trainingSetPrefix, String subPathOfImages) {

        long startTime = System.currentTimeMillis();

        File file = new File(new PropertiesLoader().getProperty("trainingImagesPath") + subPathOfImages);
        File[] images = file.listFiles(pathname -> pathname.getName().contains("."));
        if (images.length == 0) {
            System.err.println("No files in this subfolder!");
        }

        int invalidProcessedImages = 0;
        int validProcessedImages = 0;

        initializeOutputFilesWriters(trainingSetPrefix, subPathOfImages);

        for (File image : images) {
            WrinkleFeature wrinkleFeature = null;

            try {
                wrinkleFeature = new WrinkleFeature(image, false);
            } catch (WrinkleFeaturesException e) {
                invalidProcessedImages++;
                String wrinkleFeatureExceptionMessage = e.getMessage();
                log.println(wrinkleFeatureExceptionMessage); //TODO log4j
                // System.err.println(wrinkleFeatureExceptionMessage);
                continue;
            } catch (Error e) {
                invalidProcessedImages++;
                String errorMessage = "Error: Path: " + image.getName();
                log.println(errorMessage);//TODO log4j
                //System.err.println(errorMessage);
                continue;
            } catch (Exception e) {
                invalidProcessedImages++;
                String exceptionMessage = "Exception: Path: " + image.getName();
                log.println(exceptionMessage);//TODO log4j
                //System.err.println(exceptionMessage);
                continue;
            }
            //Succesfully processed wrinkle feature on face
            validProcessedImages++;
            float wrinkleFeatureResult = wrinkleFeature.getWrinkleFeatures();
            byte age = getAgeFromPath(image.getName());
            objectToJSON(new AgeToWrinkleFeature(age, wrinkleFeatureResult), ageToWrinkleJson); //TODO Metoda Wytworcza - tworzenie stringa na podstawie danych. String pozniej jest wpierdalany do pliku

            String succesfullProcessedMessage =
                    String.format("Success!: Path: %s: (age: %d | feature: %f)"
                            , image.getName()
                            , age
                            , wrinkleFeatureResult);
            log.println(succesfullProcessedMessage);//TODO log4j
            System.out.println(succesfullProcessedMessage);
        }

        generateStats(startTime, invalidProcessedImages, validProcessedImages);
        //objectToJSON(ageToWrinkleFeatureList, ageToWrinkleJson);
    }

    private static void generateStats(long startTime, int invalidProcessedImages, int validProcessedImages) {
        String correctlyProcessedImages = "\n\nCorrectly processed images: " + validProcessedImages;
        String incorrectlyProcessedImages = "Incorrectly processed images: " + invalidProcessedImages;
        String processedImages = "Sum of processed images: " + (invalidProcessedImages + validProcessedImages);
        String exexutionTime = "Execution time: " + ((float) (System.currentTimeMillis() - startTime) / 1000) + " s";

        System.out.println(correctlyProcessedImages);
        System.out.println(incorrectlyProcessedImages);
        System.out.println(processedImages);
        System.out.println(exexutionTime);

        log.println(correctlyProcessedImages);//TODO log4j
        log.println(incorrectlyProcessedImages);
        log.println(processedImages);
        log.println(exexutionTime);
    }

    private static void initializeOutputFilesWriters(String trainingSetPrefix, String subPathOfImages) {//TODO log4j
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String suffixOfFile = trainingSetPrefix + "_" + timeStamp + "_" + subPathOfImages + ".txt";

        try {
            log = new PrintStream(new FileOutputStream(new File(new PropertiesLoader().getProperty("logPathPrefix") + suffixOfFile)));
            ageToWrinkleJson = new PrintStream(new FileOutputStream(new File(new PropertiesLoader().getProperty("ageToWrinkleJsonPath") + suffixOfFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        log.println("Subdirectory: " + subPathOfImages);
    }

    private static void clusterDataByFuzzyKMeans(List<AgeToWrinkleFeature> ageToWrinkleFeatureList) {
        FuzzyKMeansClusterer fuzzyKMeansClusterer = new FuzzyKMeansClusterer(80, 10);
        if (ageToWrinkleFeatureList.size() == 0) {
            System.err.println("No data!");
            return;
        }
        fuzzyKMeansClusterer.cluster(ageToWrinkleFeatureList);
        List<CentroidCluster> listOfCentroids = fuzzyKMeansClusterer.getClusters();
        objectToJSON(listOfCentroids, clusteredJson);
    }

    private static void objectToJSON(Object object, PrintStream jsonOutput) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(object);
        jsonOutput.println(jsonString);
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
            System.err.println("NumberFormatException: " + image);
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
            clusteredJson = new PrintStream(new FileOutputStream(
                    new File(new PropertiesLoader().getProperty("clusteredJsonPath") + timeStamp + ".txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //First line of .txt
        clusteredJson.println("Clustered from: " + mergedAgeToWrinkleFeatureJsonPath);

        try (BufferedReader br = new BufferedReader(new FileReader(mergedAgeToWrinkleFeatureJsonPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                ageToWrinkleFeatures.add(gson.fromJson(line, AgeToWrinkleFeature.class));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        clusterDataByFuzzyKMeans(ageToWrinkleFeatures);
    }

    public static void mergeAgeToWrinkleFeaturesFromJsons(String trainingSetPath) {
        try {
            mergedJson = new PrintStream(new FileOutputStream(
                    new File(new PropertiesLoader().getProperty("mergedAgeToWrinkleFeaturesPath") + trainingSetPath + ".txt")));
            matlabDat = new PrintStream(new FileOutputStream(
                    new File(new PropertiesLoader().getProperty("mergedAgeToWrinkleFeaturesPath") + trainingSetPath + ".dat")));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File file = new File(new PropertiesLoader().getProperty("pathToDump"));

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
                e.printStackTrace();
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
