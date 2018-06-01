package pl.mateusz.agerecognition;

import pl.mateusz.agerecognition.utils.Paths;
import pl.mateusz.agerecognition.utils.WrinkleFeaturesException;
import pl.mateusz.agerecognition.wrinklefeature.WrinkleFeature;

import java.io.File;

public class AgeRecognition {
    private final static String imagePath = Paths.ageToRecognizePath;

    public static void main(String[] args) {
        recognizeAge();
    }

    private static void recognizeAge() {
        File file = new File(imagePath);
        File[] images = file.listFiles();

        for (File image : images) {
            WrinkleFeature wrinkleFeature = null;
            try {
                wrinkleFeature = new WrinkleFeature(image, false);
            } catch (WrinkleFeaturesException e) {
                e.printStackTrace();
            }
            int detectedAge = AgeClassifier.detectAgeFromWrinkleFeature(wrinkleFeature.getWrinkleFeatures());
            System.out.println(String.format("Detected age for image %s: %i", image, detectedAge));
        }
    }

}
