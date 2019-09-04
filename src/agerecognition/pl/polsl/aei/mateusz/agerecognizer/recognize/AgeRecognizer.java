package pl.polsl.aei.mateusz.agerecognizer.recognize;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.polsl.aei.mateusz.agerecognizer.exceptions.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeatureCalculator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AgeRecognizer {
    private static final Logger log = LogManager.getLogger("main");
    private static float[] wrinkleCentersOriginalMethod = {1, 2, 3, 4, 5};
    private static float[] ageCentersOriginalMethod = {10, 20, 30, 40, 50};

    private static float[] wrinkleCentersCustomMethod = {1, 2, 3, 4, 5};
    private static float[] ageCentersCustomMethod = {10, 20, 30, 40, 50};
    private static boolean originalMethod = true;

    public static void recognizeAge(File imagePath) {
        File[] images = imagePath.listFiles();
        assert images != null;
        for (File image : images) {
            WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
            try {
                wrinkleFeatureCalculator = new WrinkleFeatureCalculator(image, false, originalMethod);
            } catch (WrinkleFeaturesException e) {
                e.printStackTrace();
                continue;
            }
            float wrinklesPercent = wrinkleFeatureCalculator.getWrinkleFeatures();
            int detectedAge = detectAgeFromWrinkleFeature(wrinklesPercent);

            log.info(String.format("Detected wrinkle percent for image %s: %s", image, wrinklesPercent));
            log.info(String.format("Detected age for image %s: %d", image, detectedAge));
        }
    }

    /**
     * Returning calculated age from image.
     *
     * @param wrinklesFeature
     */
    public static int detectAgeFromWrinkleFeature(float wrinklesFeature) {
        float[] wrinkleCenters;
        float[] ageCenters;

        wrinkleCenters = originalMethod ? wrinkleCentersOriginalMethod : wrinkleCentersCustomMethod;
        ageCenters = originalMethod ? ageCentersOriginalMethod : ageCentersCustomMethod;

        List<Float> pij = new ArrayList<>(); // membership values
        for (int i = 0; i < wrinkleCenters.length; i++) {
            float sum = 0;
            for (int j = 0; j < wrinkleCenters.length; j++) {
                float num = wrinklesFeature - wrinkleCenters[i];
                float den = wrinklesFeature - wrinkleCenters[j];
                if (den == 0) den = (float) 0.00001;
                sum += Math.pow((num / den), 2);
            }
            pij.add(1 / sum);
        }

        float age = 0;
        for (int i = 0; i < ageCenters.length; i++) {
            if (pij.get(i) > 1) pij.set(i, 1f);
            age += pij.get(i) * ageCenters[i];
        }
        return (int) age;
    }
}
