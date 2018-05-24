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

    private Mat croppedFace = null;

    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void faceDetectorTest() throws InterruptedException {
        showImage(ryjek, "Oryginal");
        croppedFace = WrinkleFeature.faceDetector(Imgcodecs.imread(ryjek));
        Imshow.show(croppedFace, "Cropped face");
        Thread.sleep(20000);
    }

    @Test
    public void detectPairOfEyesNoseTest() throws InterruptedException {
        croppedFace = WrinkleFeature.faceDetector(Imgcodecs.imread(ryjek));
        Mat eyesMouthNose = WrinkleFeature.detectPairOfEyesAndNose(croppedFace);
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

        eyesMouthNose = WrinkleFeature.drawARectangleInMat(eyesMouthNose, centerOfEyeOne);
        eyesMouthNose = WrinkleFeature.drawARectangleInMat(eyesMouthNose, centerOfEyeTwo);

        Imshow.show(eyesMouthNose, "Detected features");
        Thread.sleep(20000);
    }

    @Test
    public void detectEyesTest() throws InterruptedException {
        croppedFace = WrinkleFeature.faceDetector(Imgcodecs.imread(ryjek));
        Mat eyes = WrinkleFeature.detectEyes(croppedFace);
        System.out.println(WrinkleFeature.mapOfDetectedObjects.toString());
        System.out.println("Distanse between eyes " + WrinkleFeature.distanseBetweenEyes);
        //     Imshow.show(eyes);
        //  Thread.sleep(20000);
    }

    @Test
    public void detectEdgesTest() throws InterruptedException {
        croppedFace = WrinkleFeature.faceDetector(Imgcodecs.imread(ryjek));
        Mat edges = WrinkleFeature.detectEdges(croppedFace);
        Imshow.show(edges);
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