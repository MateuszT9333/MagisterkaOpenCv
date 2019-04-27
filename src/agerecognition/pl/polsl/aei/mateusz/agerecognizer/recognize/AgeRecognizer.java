package pl.polsl.aei.mateusz.agerecognizer.recognize;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.polsl.aei.mateusz.agerecognizer.exceptions.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeatureCalculator;

import java.io.File;

public class AgeRecognizer {
    private static final Logger log = LogManager.getLogger("main");


    public static void recognizeAge(File imagePath) {
        File[] images = imagePath.listFiles();

        assert images != null;
        for (File image : images) {
            int detectedAge = 0;
            try {
                detectedAge = AgeRecognizer.detectAgeFromWrinkleFeature(image);
            } catch (WrinkleFeaturesException e) {
                log.error(e.getMessage());
            }
            log.info(String.format("Detected age for image %s: %d", image, detectedAge));
        }
    }

    /**
     * Returning calculated age from image.
     */
    private static int detectAgeFromWrinkleFeature(File image) throws WrinkleFeaturesException {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = new WrinkleFeatureCalculator(image, false);

        float wrinklesPercent = wrinkleFeatureCalculator.getWrinkleFeatures();
        //TODO Wykrywanie wieku na podstawie klasyfikatora
        return (int) (1 / (Math.pow(wrinklesPercent, 2) * 1.98531412899040e-05
                + 0.000912187996975040 * wrinklesPercent
                + 0.000963979767558933));
    }
}
