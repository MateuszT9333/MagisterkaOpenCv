package pl.polsl.aei.mateusz.agerecognizer.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;

//
// Detects faces in an image, draws boxes around them, and writes the results
// to "faceDetection.png".
//
class DetectFaceDemo {
    final String resourcesPath = "src/resources";
    final String savedImages = "savedImages";

    public void run() {
        System.out.println("\nRunning DetectFaceDemo");

        // Create a face detector from the cascade file in the resources
        // directory.
        CascadeClassifier faceDetector = new CascadeClassifier(resourcesPath + "/xml/lbpcascade_frontalface.xml");
        Mat image = Imgcodecs.imread(resourcesPath + "/testImages/lena.png");
        Imshow.show(image, "Lena", WindowConstants.DO_NOTHING_ON_CLOSE);

        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

        // Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }
        Imshow.show(image, "Cudowna Lena", WindowConstants.DO_NOTHING_ON_CLOSE);
        // Save the visualized detection.
        String filename = savedImages + "/faceDetection.png";
        System.out.println(String.format("Writing %s", filename));
        Imgcodecs.imwrite(filename, image);
    }
}

public class Demo {
    static final Logger log = LogManager.getLogger("main");

    public static void main(String[] args) throws InterruptedException {

        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        while (true) {
            log.trace("It works");
        }
//        new DetectFaceDemo().run();
    }
}