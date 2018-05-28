/**
 * @author Mateusz Trzeciak
 * Some tests...
 */
package test;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import pl.mateusz.agerecognition.WrinkleFeature;
import pl.mateusz.agerecognition.utils.Coordinates;
import pl.mateusz.agerecognition.utils.DetectedObjectsEnum;
import pl.mateusz.agerecognition.utils.Imshow;
import pl.mateusz.agerecognition.utils.Paths;

public class WrinkleFeatureTest {

    public final static String lenaPath = Paths.testImagesPath + "lena.png";
    public final static String ryjek = Paths.testImagesPath + "ryjek.jpg";
    public final static String monalisa = Paths.testImagesPath + "monalisa.jpg";

    Mat processedImage = Imgcodecs.imread(ryjek);

    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void faceDetectorTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = new WrinkleFeature(ryjek);
        Mat croppedToFace = wrinkleFeature.faceDetector(processedImage, true);
        Imshow.show(processedImage, "Oryginal");
        Imshow.show(croppedToFace, "Cropped face");
        Thread.sleep(20000);
    }

    @Test
    public void detectPairOfEyesNoseTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = new WrinkleFeature(ryjek);
        Mat croppedToFace = wrinkleFeature.faceDetector(processedImage, true);

        wrinkleFeature.detectPairOfEyesAndNose(croppedToFace);

        System.out.println(wrinkleFeature.mapOfDetectedObjects.toString());

        Rect centerOfEyeOne = wrinkleFeature.mapOfDetectedObjects
                .get(DetectedObjectsEnum.EYEBALLS_CENTER).get(0);

        Rect centerOfEyeTwo = wrinkleFeature.mapOfDetectedObjects
                .get(DetectedObjectsEnum.EYEBALLS_CENTER).get(1);

        centerOfEyeOne.width += 10;
        centerOfEyeOne.height += 10;

        centerOfEyeTwo.width += 10;
        centerOfEyeTwo.height += 10;

        WrinkleFeature.drawARectanglesInMat(croppedToFace, centerOfEyeOne, centerOfEyeTwo);

        Imshow.show(croppedToFace, "Detected features");
        Thread.sleep(20000);
    }

    @Test
    public void detectEyesTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = new WrinkleFeature(ryjek);
        Mat croppedToFace = wrinkleFeature.faceDetector(processedImage, true);
        wrinkleFeature.detectEyes(croppedToFace);

        System.out.println(wrinkleFeature.mapOfDetectedObjects.toString());
        Imshow.show(croppedToFace);
        Thread.sleep(20000);
    }

    @Test
    public void detectEdgesTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = new WrinkleFeature(ryjek);
        Mat croppedToFace = wrinkleFeature.faceDetector(processedImage, true);
        WrinkleFeature.detectEdges(croppedToFace);
        Imshow.show(croppedToFace);
        Thread.sleep(20000);
    }

    @Test
    public void distanseFromCoordinatesTest() {
        int result = Coordinates.getDistance(new Coordinates(10,252), new Coordinates(121,2131));
        System.out.println(result);
    }

    void showImage(String imagePath, String frameTitle){
        Mat image = Imgcodecs.imread(imagePath);
        Imshow.show(image,frameTitle);
    }

    @Test
    public void getRectOfRightCheekAreaTest() throws InterruptedException {
        WrinkleFeature wrinkleFeature = new WrinkleFeature(monalisa);
        wrinkleFeature.getRectOfForeheadAreaTest();
        wrinkleFeature.getRectOfRightCheekAreaTest();
        wrinkleFeature.getRectOfLeftCheekAreaTest();
        wrinkleFeature.getRectOfRightEyeCornerAreaTest();
        wrinkleFeature.getRectOfLeftEyeCornerAreaTest();

        Thread.sleep(20000);
    }
}