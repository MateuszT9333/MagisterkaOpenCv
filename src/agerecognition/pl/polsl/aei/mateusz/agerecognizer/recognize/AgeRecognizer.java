package pl.polsl.aei.mateusz.agerecognizer.recognize;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.polsl.aei.mateusz.agerecognizer.exceptions.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.utils.AgeSection;
import pl.polsl.aei.mateusz.agerecognizer.utils.WrinkleToAgeFunction;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeatureCalculator;

import java.io.File;

public class AgeRecognizer {
    private static final Logger log = LogManager.getLogger("main");


    public static void recognizeAge(File imagePath) {
        File[] images = imagePath.listFiles();
        boolean originalMethod = true;
        assert images != null;
        for (File image : images) {
            WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
            try {
                wrinkleFeatureCalculator = new WrinkleFeatureCalculator(image, false, originalMethod);
            } catch (WrinkleFeaturesException e) {
                e.printStackTrace();
            }
            float wrinklesPercent = wrinkleFeatureCalculator.getWrinkleFeatures();
            int detectedAge = detectAgeFromWrinkleFeature(wrinklesPercent);
            String detectedAgeSection = detectAgeSectionFromWrinkleFeature(wrinklesPercent);

            log.info(String.format("Detected wrinkle percent for image %s: %s", image, wrinklesPercent));
            log.info(String.format("Detected age for image %s: %d", image, detectedAge));
            log.info(String.format("Detected age section for image %s: %s", image, detectedAgeSection));
        }
    }

    private static String detectAgeSectionFromWrinkleFeature(float wrinklesPercent) {
        return AgeSection.getAgeSectionFromWrinkleFeature(wrinklesPercent);

    }

    /**
     * Returning calculated age from image.
     *
     * @param wrinklesPercent
     */
    private static int detectAgeFromWrinkleFeature(float wrinklesPercent) {

        return (int) WrinkleToAgeFunction.getAgeFromWrinkleFeatureNonLinearFunction(wrinklesPercent);
//        return (int) WrinkleToAgeFunction.getAgeFromWrinkleFeatureLinearFunction(wrinklesPercent);
    }
}
