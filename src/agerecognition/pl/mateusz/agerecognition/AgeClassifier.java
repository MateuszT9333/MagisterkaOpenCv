package pl.mateusz.agerecognition;

import pl.mateusz.agerecognition.utils.AgeToWrinkleFeature;
import pl.mateusz.agerecognition.utils.Paths;
import pl.mateusz.agerecognition.utils.WrinkleFeaturesException;
import pl.mateusz.agerecognition.wrinklefeature.WrinkleFeature;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AgeClassifier {
    private final static String trainingPath = Paths.trainingImagesPath;
    private static List<AgeToWrinkleFeature> ageToWrinkleFeatureList = new ArrayList<>();

    public static void main(String[] args) {
        trainImages();
    }

    private static void trainImages() {
        File file = new File(trainingPath);
        File[] images = file.listFiles();

        for (File image : images) {
            WrinkleFeature wrinkleFeature = null;

            if (!file.exists()) {
                System.err.println("No file with path " + file.getAbsolutePath());
            }

            try {
                wrinkleFeature = new WrinkleFeature(image.getAbsolutePath(), false);
            } catch (WrinkleFeaturesException e) {
                continue;
            } catch (NullPointerException e) {
                System.err.println("Path " + image.getAbsolutePath());
                e.printStackTrace();
            } catch (Error e) {
                System.err.println("Error: Path: " + image.getAbsolutePath());
                e.printStackTrace();
                continue;
            } catch (Exception e) {
                System.err.println("Error: Path: " + image.getAbsolutePath());
                e.printStackTrace();
                continue;
            }

            float wrinkleFeatureResult = wrinkleFeature.getWrinkleFeatures();
            int age = getAgeFromPath(image.getAbsolutePath());
            ageToWrinkleFeatureList.add(new AgeToWrinkleFeature(age, wrinkleFeatureResult));
            System.out.println(String.format("File: %s: Result(age, wrinkle feature): %d | %f", image, age, wrinkleFeatureResult));
        }
        //TODO Grupowanie danych i stworzenie klasyfikatora
    }

    private static int getAgeFromPath(String image) {
        //TODO Wyznacz wiek na podstawie sciezki zdjecia
        return new Random().nextInt();
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
