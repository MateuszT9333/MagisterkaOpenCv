
package pl.polsl.aei.mateusz.agerecognizer.test;

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

/**
 * @author Mateusz Trzeciak
 * Some tests...
 */
public class WrinkleFeatureTest {

    private final static File mojaTwarz = new File(new PropertiesLoader().getProperty("mojaTwarz"));

    File processedImage = mojaTwarz;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void faceDetectorTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            e.printStackTrace();
        }
        Imshow.show(wrinkleFeature.getProcessedMat(), "Oryginal", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeature.getCroppedToFace(), "Cropped face", WindowConstants.DO_NOTHING_ON_CLOSE);
//        Thread.sleep(20000);
    }

    @Test
    public void detectPairOfEyesNoseTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            e.printStackTrace();
        }
        Imshow.show(wrinkleFeature.getDetectedNoseAndEyes(), "Detected nose and eyes", WindowConstants.DO_NOTHING_ON_CLOSE);
        Thread.sleep(20000);
    }

    @Test
    public void detectEdgesTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            e.printStackTrace();
        }
        Imshow.show(wrinkleFeature.getDetectedEdges(), "Image edges", WindowConstants.DO_NOTHING_ON_CLOSE);
        Thread.sleep(20000);
    }

    void showImage(String imagePath, String frameTitle) {
        Mat image = Imgcodecs.imread(imagePath);
        Imshow.show(image, frameTitle, WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    @Test
    public void calculateWrinkleFeaturesTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(processedImage, false);
        } catch (WrinkleFeaturesException e) {
            e.printStackTrace();
        }
        wrinkleFeature.showWrinkleAreas(new Scalar(0, 0, 0));
        System.out.println("Wrinkle features " + wrinkleFeature.getWrinkleFeatures());
        Thread.sleep(20000);
    }

    @Test
    public void showGeneratedImages() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        //fileExists(processedImage);
        try {
            wrinkleFeature = new WrinkleFeature(processedImage, false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Imshow.show(wrinkleFeature.getProcessedMat(), "Oryginal", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeature.getCroppedToFace(), "Cropped face", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeature.getDetectedNoseAndEyes(), "Detected nose and eyes", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeature.getDetectedEdges(), "Detected edges", WindowConstants.DO_NOTHING_ON_CLOSE);
        wrinkleFeature.showWrinkleAreas(new Scalar(255, 0, 0));

        System.out.println("Wrinkle features " + wrinkleFeature.getWrinkleFeatures());
        System.out.println("Detected objects " + wrinkleFeature.getDetectedObjects());
        System.out.println("Calculated wrinkle areas " + wrinkleFeature.getWrinkleAreas());
        Thread.sleep(1000000);
    }

//    @Test
//    public void fileExists(File processedImage) {
//        File file = processedImage;
//        assertTrue(file.length() > 0);
//    }
}