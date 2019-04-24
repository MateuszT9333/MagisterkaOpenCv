package pl.polsl.aei.mateusz.agerecognizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.polsl.aei.mateusz.agerecognizer.train.AgeClassifier;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;
import pl.polsl.aei.mateusz.agerecognizer.utils.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeature;

import java.io.File;

/**
 * Recognizing Age from selected image
 */
public class AgeRecognition {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    private final static String imagePath = propertiesLoader.getProperty("ageToRecognizePath");
    static final Logger log = LogManager.getLogger("main");

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
                log.catching(e);
            }
            assert wrinkleFeature != null;
            int detectedAge = AgeClassifier.detectAgeFromWrinkleFeature(wrinkleFeature.getWrinkleFeatures());
            log.info(String.format("Detected age for image %s: %d", image, detectedAge));
        }
    }

}
