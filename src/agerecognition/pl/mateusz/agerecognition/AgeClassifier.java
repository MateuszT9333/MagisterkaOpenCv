package pl.mateusz.agerecognition;

import com.google.gson.Gson;
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
    private static String jsonPathPrefix = "json";
    private static PrintStream log;
    private static PrintStream json;


    public static void main(String[] args) {
        trainImages();
    }

    private static void trainImages() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String suffixOfFile = timeStamp + ".txt";

        try {
            log = new PrintStream(new FileOutputStream(new File(logPathPrefix + suffixOfFile)));
            json = new PrintStream(new FileOutputStream(new File(jsonPathPrefix + suffixOfFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        long startTime = System.currentTimeMillis();
        File file = new File(trainingPath);
        File[] images = file.listFiles();
        List<String> imagesNotProcessed = new ArrayList<>();
        List<String> imageProcessed = new ArrayList<>();
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
        objectsToJSON(ageToWrinkleFeatureList);
        System.out.println("\n\nCorrectly processed images: " + validProcessedImages);
        System.out.println("Incorrectly processed images: " + invalidProcessedImages);
        System.out.println("Sum of processed images: " + (invalidProcessedImages + validProcessedImages));
        System.out.println("Execution time: " + ((float) (System.currentTimeMillis() - startTime) / 1000) + " s");
    }

    private static void objectsToJSON(Object... objects) {
        Gson gson = new Gson();

        for (Object object : objects) {
            String jsonString = gson.toJson(object);
            json.println(jsonString);
        }
    }


    private static byte getAgeFromPath(String image) {
        //TODO Wyznacz wiek na podstawie sciezki zdjecia
        return -1;
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
