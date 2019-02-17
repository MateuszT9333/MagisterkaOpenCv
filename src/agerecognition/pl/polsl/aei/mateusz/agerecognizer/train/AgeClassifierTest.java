package pl.polsl.aei.mateusz.agerecognizer.train;

import org.junit.Test;
import pl.polsl.aei.mateusz.agerecognizer.utils.Paths;

import java.io.File;

public class AgeClassifierTest {
    @Test
    public void generateDataFromImagesManual() {
        AgeClassifier.generateDataFromImages("1", "41");
    }

    @Test
    public void generateDataFromImagesAuto() throws InterruptedException {
        boolean process = false;
        File images = new File(Paths.trainingImagesPath);
        String startFrom = "19";
        for (File image : images.listFiles()) {

            if (image.getName().contains(startFrom)) {
                process = true;
            }

            if (process == false) {
                continue;
            }

            AgeClassifier.generateDataFromImages("1", image.getName());
            Thread.sleep(10000);
        }
    }

    @Test
    public void mergeAgeToWrinkleFeatureJson() {
        AgeClassifier.mergeAgeToWrinkleFeaturesFromJsons("1");
    }

    @Test
    public void clusterDataByFuzzyKMeans() {
        //   AgeClassifier.clusterDataByFuzzyKMeans("dump\\mergedAgeToWrinkleFeatureJson1.txt");

    }
}
