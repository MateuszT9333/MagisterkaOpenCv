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
import pl.mateusz.agerecognition.utils.Imshow;

public class WrinkleFeatureTest {

    final String resourcesPath = "src/resources/";
    final String testImages = resourcesPath + "testImages/";
    final String trainingImages = resourcesPath + "trainingImages/";
    final String lenaPath = testImages + "lena.png";
    final String ryjek = testImages + "ryjek.jpg";
    final String monalisa = testImages + "monalisa.jpg";

    Mat processedImage = Imgcodecs.imread(ryjek);

    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void faceDetectorTest() throws InterruptedException {

        Imshow.show(processedImage, "Oryginal");
        WrinkleFeature.faceDetector(processedImage);
        Imshow.show(processedImage, "Cropped face");

        Thread.sleep(20000);
    }

    @Test
    public void detectPairOfEyesNoseTest() throws InterruptedException {
        processedImage = WrinkleFeature.faceDetector(processedImage);
        WrinkleFeature.detectPairOfEyesAndNose(processedImage);
        System.out.println(WrinkleFeature.mapOfDetectedObjects.toString());
        System.out.println(WrinkleFeature.distanseBetweenEyes);

        Rect centerOfEyeOne = WrinkleFeature.mapOfDetectedObjects
                .get(WrinkleFeature.DetectedObjectsEnum.EYEBALLS_CENTER).get(0);

        Rect centerOfEyeTwo = WrinkleFeature.mapOfDetectedObjects
                .get(WrinkleFeature.DetectedObjectsEnum.EYEBALLS_CENTER).get(1);

        centerOfEyeOne.width += 10;
        centerOfEyeOne.height += 10;

        centerOfEyeTwo.width += 10;
        centerOfEyeTwo.height += 10;

        WrinkleFeature.drawARectangleInMat(processedImage, centerOfEyeOne);
        WrinkleFeature.drawARectangleInMat(processedImage, centerOfEyeTwo);

        Imshow.show(processedImage, "Detected features");
        Thread.sleep(20000);
    }

    @Test
    public void detectEyesTest() throws InterruptedException {
        WrinkleFeature.faceDetector(processedImage);
        WrinkleFeature.detectEyes(processedImage);

        System.out.println(WrinkleFeature.mapOfDetectedObjects.toString());
        System.out.println("Distanse between eyes " + WrinkleFeature.distanseBetweenEyes);
        //     Imshow.show(eyes);
        //  Thread.sleep(20000);
    }

    @Test
    public void detectEdgesTest() throws InterruptedException {
        WrinkleFeature.faceDetector(processedImage);
        WrinkleFeature.detectEdges(processedImage);
        Imshow.show(processedImage);
        Thread.sleep(20000);
    }

    @Test
    public void distanseFromCoordinatesTest() throws InterruptedException {
        int result = Coordinates.getDistance(new Coordinates(10,252), new Coordinates(121,2131));
        System.out.println(result);
    }

    void showImage(String imagePath, String frameTitle){
        Mat image = Imgcodecs.imread(imagePath);
        Imshow.show(image,frameTitle);
    }
}