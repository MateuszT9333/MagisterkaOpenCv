
package test;

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

import static org.junit.Assert.assertTrue;

/**
 * @author Mateusz Trzeciak
 * Some tests...
 */
public class WrinkleFeatureTest {

    private final static String lenaPath = Paths.testImagesPath + "lena.png";
    private final static String ryjek = Paths.testImagesPath + "ryjek.jpg";
    private final static String monalisa = Paths.testImagesPath + "monalisa.jpg";

    String processedImage = Paths.trainingImagesPath + "797100_1917-11-02_1940.jpg";

    static{
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

    void showImage(String imagePath, String frameTitle){
        Mat image = Imgcodecs.imread(imagePath);
        Imshow.show(image,frameTitle);
    }

    @Test
    public void calculateWrinkleFeaturesTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(monalisa, false);
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
        fileExists();
        try {
            wrinkleFeature = new WrinkleFeature(monalisa, false);
        } catch (WrinkleFeaturesException e) {
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

    @Test
    public void fileExists() {
        File file = new File(processedImage);
        assertTrue(file.length() > 0);
    }
}