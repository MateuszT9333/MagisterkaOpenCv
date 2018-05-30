package pl.mateusz.agerecognition;

import pl.mateusz.agerecognition.utils.AgeToWrinkleFeature;
import pl.mateusz.agerecognition.utils.Paths;
import pl.mateusz.agerecognition.wrinklefeature.WrinkleFeature;

import java.io.File;
import java.util.List;
import java.util.Random;

public class AgeClassifier {
    private final static String trainingPath = Paths.trainingImagesPath;
    private static List<AgeToWrinkleFeature> ageToWrinkleFeatureList;

    public static void main(String[] args) {
        trainImages();
    }

    private static void trainImages() {
        File file = new File(trainingPath);
        String[] images = file.list();

        for (String image : images) {
            WrinkleFeature wrinkleFeature = new WrinkleFeature(image);
            ageToWrinkleFeatureList.add(new AgeToWrinkleFeature(getAgeFromPath(image), wrinkleFeature.getWrinkleFeatures()));
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
