package pl.mateusz.agerecognition;

import com.google.gson.Gson;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import pl.mateusz.agerecognition.utils.AgeToWrinkleFeature;
import pl.mateusz.agerecognition.utils.Paths;
import pl.mateusz.agerecognition.utils.WrinkleFeaturesException;
import pl.mateusz.agerecognition.wrinklefeature.WrinkleFeature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgeClassifier {
    private final static String trainingPath = Paths.trainingImagesPath;
    private static List<AgeToWrinkleFeature> ageToWrinkleFeatureList = new ArrayList<>();
    private static String logPathPrefix = "log";
    private static String ageToWrinkleJsonPath = "ageToWrinkleFeaturesJson";
    private static String clusteredJsonPath = "clusteredDataJson";
    private static PrintStream log;
    private static PrintStream ageToWrinkleJson;
    private static PrintStream clusteredJson;


    public static void main(String[] args) {
        trainImages();
    }

    private static void trainImages() {
        long startTime = System.currentTimeMillis();

        initializeOutputFilesWriters();

        File file = new File(trainingPath);
        File[] images = file.listFiles();

        int invalidProcessedImages = 0;
        int validProcessedImages = 0;

        for (File image : images) {
            WrinkleFeature wrinkleFeature = null;

            try {
                wrinkleFeature = new WrinkleFeature(image, false);
            } catch (WrinkleFeaturesException e) {
                invalidProcessedImages++;
                String wrinkleFeatureExceptionMessage = e.getMessage();
                log.println(wrinkleFeatureExceptionMessage);
                // System.err.println(wrinkleFeatureExceptionMessage);
                continue;
            } catch (Error e) {
                invalidProcessedImages++;
                String errorMessage = "Error: Path: " + image.getName();
                log.println(errorMessage);
                //System.err.println(errorMessage);
                continue;
            } catch (Exception e) {
                invalidProcessedImages++;
                String exceptionMessage = "Exception: Path: " + image.getName();
                log.println(exceptionMessage);
                //System.err.println(exceptionMessage);
                continue;
            }
            validProcessedImages++;
            float wrinkleFeatureResult = wrinkleFeature.getWrinkleFeatures();
            byte age = getAgeFromPath(image.getName());

            ageToWrinkleFeatureList.add(new AgeToWrinkleFeature(age, wrinkleFeatureResult));

            String succesfullProcessedMessage =
                    String.format("Success!: Path: %s: (age: %d | feature: %f"
                            , image.getName()
                            , age
                            , wrinkleFeatureResult);
            log.println(succesfullProcessedMessage);
            System.out.println(succesfullProcessedMessage);
        }

        generateStats(startTime, invalidProcessedImages, validProcessedImages);

        objectsToJSON(ageToWrinkleFeatureList, ageToWrinkleJson);
        clusterData();
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

        log.println(correctlyProcessedImages);
        log.println(incorrectlyProcessedImages);
        log.println(processedImages);
        log.println(exexutionTime);
    }

    private static void initializeOutputFilesWriters() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String suffixOfFile = timeStamp + ".txt";

        try {
            log = new PrintStream(new FileOutputStream(new File(logPathPrefix + suffixOfFile)));
            ageToWrinkleJson = new PrintStream(new FileOutputStream(new File(ageToWrinkleJsonPath + suffixOfFile)));
            clusteredJson = new PrintStream(new FileOutputStream(new File(clusteredJsonPath + suffixOfFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void clusterData() {
        FuzzyKMeansClusterer fuzzyKMeansClusterer = new FuzzyKMeansClusterer(5, 10);
        fuzzyKMeansClusterer.cluster(ageToWrinkleFeatureList);
        List<CentroidCluster> listOfCentroids = fuzzyKMeansClusterer.getClusters();
        objectsToJSON(listOfCentroids, clusteredJson);
    }

    private static void objectsToJSON(Object object, PrintStream json) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(object);
        json.println(jsonString);
    }


    private static byte getAgeFromPath(String image) {
        try {
            return Byte.parseByte(image.subSequence(0, 2).toString());
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
        return 0;
    }

}
