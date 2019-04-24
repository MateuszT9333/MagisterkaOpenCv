package pl.polsl.aei.mateusz.agerecognizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import pl.polsl.aei.mateusz.agerecognizer.utils.Imshow;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;
import pl.polsl.aei.mateusz.agerecognizer.utils.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeature;

import javax.swing.*;
import java.io.File;
import java.util.Scanner;

/**
 * @author Mateusz Trzeciak
 * Some tests...
 */
public class WrinkleFeatureTest {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    private final static File mojaTwarz = new File(propertiesLoader.getProperty("mojaTwarz"));
    private File processedImage = mojaTwarz;
    static final Logger log = LogManager.getLogger("main");

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void faceDetectorTest() {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeature != null;
        Imshow.show(wrinkleFeature.getProcessedMat(), "Oryginal", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeature.getCroppedToFace(), "Cropped face", WindowConstants.DO_NOTHING_ON_CLOSE);
        pause();
    }

    private void pause() {
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    @Test
    public void detectPairOfEyesNoseTest() {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeature != null;
        Imshow.show(wrinkleFeature.getDetectedNoseAndEyes(), "Detected nose and eyes", WindowConstants.DO_NOTHING_ON_CLOSE);
        pause();
    }

    @Test
    public void detectEdgesTest() {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeature != null;
        Imshow.show(wrinkleFeature.getDetectedEdges(), "Image edges", WindowConstants.DO_NOTHING_ON_CLOSE);
        pause();
    }

    void showImage(String imagePath, String frameTitle) {
        Mat image = Imgcodecs.imread(imagePath);
        Imshow.show(image, frameTitle, WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    @Test
    public void calculateWrinkleFeaturesTest() {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeature != null;
        wrinkleFeature.showWrinkleAreas(new Scalar(0, 0, 0));
        log.info("Wrinkle features " + wrinkleFeature.getWrinkleFeatures());
        pause();
    }

    @Test
    public void showGeneratedImages() {
        WrinkleFeature wrinkleFeature = null;
        //fileExists(processedImage);
        try {
            wrinkleFeature = new WrinkleFeature(processedImage, false);
        } catch (Throwable e) {
            log.catching(e);
        }
        assert wrinkleFeature != null;
        Imshow.show(wrinkleFeature.getProcessedMat(), "Oryginal", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeature.getCroppedToFace(), "Cropped face", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeature.getDetectedNoseAndEyes(), "Detected nose and eyes", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeature.getDetectedEdges(), "Detected edges", WindowConstants.DO_NOTHING_ON_CLOSE);
        wrinkleFeature.showWrinkleAreas(new Scalar(255, 0, 0));

        log.info("Wrinkle features " + wrinkleFeature.getWrinkleFeatures());
        log.info("Detected objects " + wrinkleFeature.getDetectedObjects());
        log.info("Calculated wrinkle areas " + wrinkleFeature.getWrinkleAreas());
        pause();
    }

//    @Test
//    public void fileExists(File processedImage) {
//        File file = processedImage;
//        assertTrue(file.length() > 0);
//    }

    @Test
    public void name() {

    }
}