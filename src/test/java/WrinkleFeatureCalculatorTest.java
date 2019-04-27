import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import pl.polsl.aei.mateusz.agerecognizer.exceptions.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.utils.Imshow;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeatureCalculator;

import javax.swing.*;
import java.io.File;
import java.util.Scanner;

/**
 * @author Mateusz Trzeciak
 * Some tests...
 */
public class WrinkleFeatureCalculatorTest {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    private final static File mojaTwarz = new File(propertiesLoader.getProperty("mojaTwarz"));
    private final File processedImage = mojaTwarz;
    private static final Logger log = LogManager.getLogger("main");

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void faceDetectorTest() {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
        try {
            wrinkleFeatureCalculator = new WrinkleFeatureCalculator(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeatureCalculator != null;
        Imshow.show(wrinkleFeatureCalculator.getProcessedMat(), "Oryginal", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeatureCalculator.getCroppedToFace(), "Cropped face", WindowConstants.DO_NOTHING_ON_CLOSE);
        pause();
    }

    private void pause() {
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    @Test
    public void detectPairOfEyesNoseTest() {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
        try {
            wrinkleFeatureCalculator = new WrinkleFeatureCalculator(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeatureCalculator != null;
        Imshow.show(wrinkleFeatureCalculator.getDetectedNoseAndEyes(), "Detected nose and eyes", WindowConstants.DO_NOTHING_ON_CLOSE);
        pause();
    }

    @Test
    public void detectEdgesTest() {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
        try {
            wrinkleFeatureCalculator = new WrinkleFeatureCalculator(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeatureCalculator != null;
        Imshow.show(wrinkleFeatureCalculator.getDetectedEdges(), "Image edges", WindowConstants.DO_NOTHING_ON_CLOSE);
        pause();
    }

    void showImage(String imagePath, String frameTitle) {
        Mat image = Imgcodecs.imread(imagePath);
        Imshow.show(image, frameTitle, WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    @Test
    public void calculateWrinkleFeaturesTest() {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
        try {
            wrinkleFeatureCalculator = new WrinkleFeatureCalculator(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeatureCalculator != null;
        wrinkleFeatureCalculator.showWrinkleAreas(new Scalar(0, 0, 0));
        log.info("Wrinkle features " + wrinkleFeatureCalculator.getWrinkleFeatures());
        pause();
    }

    @Test
    public void showGeneratedImages() {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
        //fileExists(processedImage);
        try {
            wrinkleFeatureCalculator = new WrinkleFeatureCalculator(processedImage, false);
        } catch (Throwable e) {
            log.catching(e);
        }
        assert wrinkleFeatureCalculator != null;
        Imshow.show(wrinkleFeatureCalculator.getProcessedMat(), "Oryginal", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeatureCalculator.getCroppedToFace(), "Cropped face", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeatureCalculator.getDetectedNoseAndEyes(), "Detected nose and eyes", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeatureCalculator.getDetectedEdges(), "Detected edges", WindowConstants.DO_NOTHING_ON_CLOSE);
        wrinkleFeatureCalculator.showWrinkleAreas(new Scalar(255, 0, 0));

        log.info("Wrinkle features " + wrinkleFeatureCalculator.getWrinkleFeatures());
        log.info("Detected objects " + wrinkleFeatureCalculator.getDetectedObjects());
        log.info("Calculated wrinkle areas " + wrinkleFeatureCalculator.getWrinkleAreas());
        pause();
    }

//    @Test
//    public void fileExists(File processedImage) {
//        File file = processedImage;
//        assertTrue(file.length() > 0);
//    }

}