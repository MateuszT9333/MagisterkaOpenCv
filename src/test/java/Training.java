import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import pl.polsl.aei.mateusz.agerecognizer.recognize.AgeRecognizer;
import pl.polsl.aei.mateusz.agerecognizer.train.Trainer;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;

import java.io.File;
import java.util.Objects;

public class Training {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
    private static final Logger log = LogManager.getLogger("main");

    @Test
    public void generateDataFromImagesManual() {
        Trainer.generateDataFromImages("1", null, false);
    }

    @Test
    public void generateDataFromImagesAuto() throws InterruptedException {
        boolean process = false;
        long startTime = System.currentTimeMillis();
        File images = new File(propertiesLoader.getProperty("croppedImagesPath"));
        String startFrom = "00";
        try {
            for (File imagePath : Objects.requireNonNull(images.listFiles())) {

                if (imagePath.getName().contains(startFrom)) {
                    process = true;
                }

                if (!process) {
                    continue;
                }

                Trainer.generateDataFromImages("99", imagePath, false);
                Thread.sleep(1000);
//            if(System.currentTimeMillis() - startTime > 180000){
//                System.exit(0);
//                log.warn("Stop after 180 seconds");
//            }
            }
        } catch (RuntimeException e) {
            System.exit(0);
        }
    }

    //    @Test
//    public void cropFacesFromImagesAuto() throws InterruptedException {
//        boolean process = false;
//        long startTime = System.currentTimeMillis();
//        File images = new File(propertiesLoader.getProperty("trainingImagesPath"));
//        String startFrom = "21";
//        try {
//            for (File imagePath : Objects.requireNonNull(images.listFiles())) {
//
//                if (imagePath.getName().contains(startFrom)) {
//                    process = true;
//                }
//
//                if (!process) {
//                    continue;
//                }
//
//                Trainer.generateDataFromImages("3", imagePath, false);
//                Thread.sleep(1000);
////            if(System.currentTimeMillis() - startTime > 180000){
////                System.exit(0);
////                log.warn("Stop after 180 seconds");
////            }
//            }
//        } catch (RuntimeException e) {
//            System.exit(0);
//        }
//    }
    @Test
    public void mergeAgeToWrinkleFeatureJson() {
        Trainer.mergeAgeToWrinkleFeaturesFromJsons("10");
    }

    @Test
    public void mergeAgeToWrinkleFeatureJsonHogKnn() {
        Trainer.mergeAgeToWrinkleFeaturesFromJsonsHogKnn("99");
    }

    @Test
    public void clusterDataByFuzzyKMeans() {
        //   Trainer.clusterDataByFuzzyKMeans("dump\\mergedAgeToWrinkleFeatureJson1.txt");

    }

    @Test
    public void testAge() {
        System.out.println(AgeRecognizer.detectAgeFromWrinkleFeature((float) 2));
    }
}
