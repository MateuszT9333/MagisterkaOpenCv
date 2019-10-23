package pl.polsl.aei.mateusz.agerecognizer.recognize;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.polsl.aei.mateusz.agerecognizer.exceptions.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.train.Trainer;
import pl.polsl.aei.mateusz.agerecognizer.utils.HogConfig;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeatureCalculator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AgeRecognizer {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
    private static final Logger log = LogManager.getLogger("main");
    private static List<String> age2centers;
    private static float[] wrinkleCenters;
    private static float[] ageCenters;


    private static boolean originalRectangles = false;
    private final static HogConfig hogConfig = new HogConfig(true, 9, 9, false);
    private static String trainingTitle = "Original method: " + originalRectangles + ", options = [2.0 1000 1e-5 1];\n" +
            "numberOfClusters = 100, " + hogConfig.isHog();

    public static void recognizeAge(File imagePath) throws IOException {
        List<String> real2Recognized = new ArrayList<>();
        boolean isDescriptorException;
        log.info(trainingTitle);
        String filename;
        File[] images = imagePath.listFiles();
        assert images != null;
        for (File image : images) {
            WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
            try {
                wrinkleFeatureCalculator = new WrinkleFeatureCalculator(image, false, originalRectangles, hogConfig);
            } catch (WrinkleFeaturesException e) {
                e.printStackTrace();
                continue;
            }
            float wrinklesPercent = wrinkleFeatureCalculator.getWrinkleFeatures();
            int detectedAge = detectAgeFromWrinkleFeature(wrinklesPercent);

            log.info(String.format("Detected wrinkle percent for image %s: %s", image, wrinklesPercent));
            log.info(String.format("Detected age for image %s: %d", image, detectedAge));
            real2Recognized.add(Trainer.getAgeFromPath(image.getName()) + "\t" + detectedAge);
        }
        filename = propertiesLoader.getProperty("real2recognized") + System.currentTimeMillis() + ".txt";
        log.info("Wynik w pliku " + filename);
        FileWriter writer = new FileWriter(filename);
        for (String str : real2Recognized) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();
    }

    /**
     * Returning calculated age from image.
     *
     * @param wrinklesFeature
     */
    public static int detectAgeFromWrinkleFeature(float wrinklesFeature) {
        age2centers = getAge2Centers();
        obtainCenters();

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

    private static List<String> getAge2Centers() {
        List<String> lines = new ArrayList<>();
        File file = new File(propertiesLoader.getProperty("age2centers"));
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void obtainCenters() {

        ageCenters = new float[age2centers.size()];
        wrinkleCenters = new float[age2centers.size()];
        int i = 0;
        for (String line : age2centers) {
            String[] line_ = line.split("\t");
            ageCenters[i] = Float.parseFloat(line_[0]);
            wrinkleCenters[i] = Float.parseFloat(line_[1]);
            i++;
        }
    }

}
