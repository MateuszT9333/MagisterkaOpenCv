package test;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import pl.mateusz.agerecognition.Imshow;
import pl.mateusz.agerecognition.WrinkleFeature;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.*;

public class WrinkleFeatureTest {

    final String resourcesPath = "src/resources/";
    final String testImages = resourcesPath + "testImages/";
    final String trainingImages = resourcesPath + "trainingImages/";
    final String lenaPath = testImages + "lena.png";
    final String facetPath = testImages + "facet.jpg";

    private Mat croppedFace = null;

    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void faceDetectorTest() throws InterruptedException {
        showImage(lenaPath, "Oryginal");
        croppedFace = WrinkleFeature.faceDetector(Imgcodecs.imread(lenaPath));
        Imshow.show(croppedFace, "Cropped face");
        Thread.sleep(20000);
    }
    @Test
    public void detectEyesMouthNoseTest() throws InterruptedException {
        croppedFace = WrinkleFeature.faceDetector(Imgcodecs.imread(facetPath));
        Mat eyesMouthNose = WrinkleFeature.detectEyesMouthNose(croppedFace);
        Imshow.show(eyesMouthNose, "Detected features");
        Thread.sleep(20000);
    }
    @Test
    public void detectEdgesTest() throws InterruptedException {

        Mat edges = WrinkleFeature.detectEdges(Imgcodecs.imread(lenaPath));
        Imshow.show(edges);
        Thread.sleep(10000);
    }

    void showImage(String imagePath, String frameTitle){
        Mat image = Imgcodecs.imread(imagePath);
        Imshow.show(image,frameTitle);
    }
}