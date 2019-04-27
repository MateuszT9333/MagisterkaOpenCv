import org.junit.Test;
import pl.polsl.aei.mateusz.agerecognizer.train.Trainer;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;

import java.io.File;

public class Training {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    @Test
    public void generateDataFromImagesManual() {
        Trainer.generateDataFromImages("1", "41");
    }

    @Test
    public void generateDataFromImagesAuto() throws InterruptedException {
        boolean process = false;
        File images = new File(propertiesLoader.getProperty("trainingImagesPath"));
        String startFrom = "19";
        for (File image : images.listFiles()) {

            if (image.getName().contains(startFrom)) {
                process = true;
            }

            if (process == false) {
                continue;
            }

            Trainer.generateDataFromImages("1", image.getName());
            Thread.sleep(10000);
        }
    }

    @Test
    public void mergeAgeToWrinkleFeatureJson() {
        Trainer.mergeAgeToWrinkleFeaturesFromJsons("1");
    }

    @Test
    public void clusterDataByFuzzyKMeans() {
        //   Trainer.clusterDataByFuzzyKMeans("dump\\mergedAgeToWrinkleFeatureJson1.txt");

    }
}
