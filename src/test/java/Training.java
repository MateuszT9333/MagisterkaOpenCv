import org.junit.Test;
import pl.polsl.aei.mateusz.agerecognizer.train.Trainer;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;

import java.io.File;
import java.util.Objects;

public class Training {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    @Test
    public void generateDataFromImagesManual() {
        Trainer.generateDataFromImages("1", null);
    }

    @Test
    public void generateDataFromImagesAuto() throws InterruptedException {
        boolean process = false;
        File images = new File(propertiesLoader.getProperty("trainingImagesPath"));
        String startFrom = "40";
        for (File imagePath : Objects.requireNonNull(images.listFiles())) {

            if (imagePath.getName().contains(startFrom)) {
                process = true;
            }

            if (!process) {
                continue;
            }

            Trainer.generateDataFromImages("2", imagePath);
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
