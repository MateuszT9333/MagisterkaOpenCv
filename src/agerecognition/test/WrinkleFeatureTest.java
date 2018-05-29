
package test;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import pl.mateusz.agerecognition.WrinkleFeature;
import pl.mateusz.agerecognition.utils.Imshow;
import pl.mateusz.agerecognition.utils.NumberOfDetectedObjectsException;
import pl.mateusz.agerecognition.utils.Paths;

/**
 * @author Mateusz Trzeciak
 * Some tests...
 */
public class WrinkleFeatureTest {

    private final static String lenaPath = Paths.testImagesPath + "lena.png";
    private final static String ryjek = Paths.testImagesPath + "ryjek.jpg";
    private final static String monalisa = Paths.testImagesPath + "monalisa.jpg";

    Mat processedImage = Imgcodecs.imread(ryjek);

    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void faceDetectorTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(ryjek);
        } catch (NumberOfDetectedObjectsException e) {
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
            wrinkleFeature = new WrinkleFeature(ryjek);
        } catch (NumberOfDetectedObjectsException e) {
            e.printStackTrace();
        }
        Imshow.show(wrinkleFeature.getDetectedNoseAndEyes(), "Detected nose and eyes");
        Thread.sleep(20000);
    }

    @Test
    public void detectEdgesTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(ryjek);
        } catch (NumberOfDetectedObjectsException e) {
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
            wrinkleFeature = new WrinkleFeature(monalisa);
        } catch (NumberOfDetectedObjectsException e) {
            e.printStackTrace();
        }
        wrinkleFeature.showWrinkleAreas(new Scalar(0, 0, 0));
        System.out.println("Wrinkle features " + wrinkleFeature.getWrinkleFeatures());
        Thread.sleep(20000);
    }

    @Test
    public void showGeneratedImages() throws InterruptedException {
        WrinkleFeature wrinkleFeature = null;
        try {
            wrinkleFeature = new WrinkleFeature(ryjek);
        } catch (NumberOfDetectedObjectsException e) {
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

}