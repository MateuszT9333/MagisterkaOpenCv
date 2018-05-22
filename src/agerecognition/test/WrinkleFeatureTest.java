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

    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @org.junit.Test
    public void faceDetector() throws InterruptedException {
        showImage(lenaPath, "Oryginal");
        Mat croppedFace  = WrinkleFeature.faceDetector(lenaPath);
        Imshow.show(croppedFace, "Cropped face");
        Thread.sleep(2000);
    }
    void showImage(String imagePath, String frameTitle){
        Mat image = Imgcodecs.imread(imagePath);
        Imshow.show(image,frameTitle);
    }
}