package pl.polsl.aei.mateusz.agerecognizer;

import pl.polsl.aei.mateusz.agerecognizer.train.AgeClassifier;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;
import pl.polsl.aei.mateusz.agerecognizer.utils.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeature;

import java.io.File;

/**
 * Recognizing Age from selected image
 */
public class AgeRecognition {
    private final static String imagePath = new PropertiesLoader().getProperty("ageToRecognizePath");

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
            assert wrinkleFeature != null;
            int detectedAge = AgeClassifier.detectAgeFromWrinkleFeature(wrinkleFeature.getWrinkleFeatures());
            System.out.println(String.format("Detected age for image %s: %d", image, detectedAge));
        }
    }

}
