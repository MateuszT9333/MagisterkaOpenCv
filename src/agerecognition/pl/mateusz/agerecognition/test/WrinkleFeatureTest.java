
package pl.mateusz.agerecognition.test;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import pl.mateusz.agerecognition.utils.Imshow;
import pl.mateusz.agerecognition.utils.Paths;
import pl.mateusz.agerecognition.utils.WrinkleFeaturesException;
import pl.mateusz.agerecognition.wrinklefeature.WrinkleFeature;

import java.io.File;

/**
 * @author Mateusz Trzeciak
 * Some tests...
 */
public class WrinkleFeatureTest {

    private final static File obraz = new File(Paths.testImagesPath + "1_0_0_20161219194756275.jpg");
    private final static File ryjek = new File(Paths.testImagesPath + "ryjek.jpg");

    File processedImage = obraz;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void faceDetectorTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(ryjek, false);
        } catch (WrinkleFeaturesException e) {
            e.printStackTrace();
        }
        Imshow.show(wrinkleFeature.getProcessedMat(), "Oryginal");
        Imshow.show(wrinkleFeature.getCroppedToFace(), "Cropped face");
        Thread.sleep(20000);
    }

    @Test
    public void detectPairOfEyesNoseTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(ryjek, false);
        } catch (WrinkleFeaturesException e) {
            e.printStackTrace();
        }
        Imshow.show(wrinkleFeature.getDetectedNoseAndEyes(), "Detected nose and eyes");
        Thread.sleep(20000);
    }

    @Test
    public void detectEdgesTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(ryjek, false);
        } catch (WrinkleFeaturesException e) {
            e.printStackTrace();
        }
        Imshow.show(wrinkleFeature.getDetectedEdges(), "Image edges");
        Thread.sleep(20000);
    }

    void showImage(String imagePath, String frameTitle) {
        Mat image = Imgcodecs.imread(imagePath);
        Imshow.show(image, frameTitle);
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
        } catch (WrinkleFeaturesException e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Imshow.show(wrinkleFeature.getProcessedMat(), "Oryginal");
        Imshow.show(wrinkleFeature.getCroppedToFace(), "Cropped face");
        Imshow.show(wrinkleFeature.getDetectedNoseAndEyes(), "Detected nose and eyes");
        Imshow.show(wrinkleFeature.getDetectedEdges(), "Detected edges");
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