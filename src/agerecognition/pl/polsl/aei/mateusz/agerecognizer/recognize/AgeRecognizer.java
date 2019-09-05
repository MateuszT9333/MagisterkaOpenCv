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
    private static float[] wrinkleCentersOriginalMethod = {0.2208f,0.6008f,1.2694f,0.4562f,1.0134f,1.8540f,0.3345f,0.7478f,1.4942f,0.1051f};
    private static float[] ageCentersOriginalMethod = {30.491f,34.592f,63.723f,36.716f,52.652f,60.382f,32.755f,45.834f,58.551f,25.219f};

    private static float[] wrinkleCentersCustomMethod = {0.2663f,1.5699f,0.1723f,0.0776f,0.6121f,0.7963f,1.0737f,0.3641f,0.4720f,1.3270f};
    private static float[] ageCentersCustomMethod = {26.389f,65.444f,26.366f,21.836f,34.086f,40.008f,54.692f,29.243f,30.444f,52f};
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
