package pl.mateusz.agerecognition;

import pl.mateusz.agerecognition.utils.Paths;
import pl.mateusz.agerecognition.wrinklefeature.WrinkleFeature;

import java.io.File;

public class AgeRecognition {
    private final static String imagePath = Paths.ageToRecognizePath;

    public static void main(String[] args) {
        recognizeAge();
    }

    private static void recognizeAge() {
        File file = new File(imagePath);
        String[] images = file.list();

        for (String image : images) {
            WrinkleFeature wrinkleFeature = new WrinkleFeature(image);
            int detectedAge = AgeClassifier.detectAgeFromWrinkleFeature(wrinkleFeature.getWrinkleFeatures());
            System.out.println(String.format("Detected age for image %s: %i", image, detectedAge));
        }
    }

}
