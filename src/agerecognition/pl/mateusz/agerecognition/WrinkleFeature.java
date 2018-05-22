package pl.mateusz.agerecognition;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class WrinkleFeature {
    final static String resourcesPath = "src/resources/";
    final static String testImages = resourcesPath + "testImages/";
    final static String trainingImages = resourcesPath + "trainingImages/";
    final static String cascadeClassifierPath = resourcesPath + "lbpcascade_frontalface.xml";
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("OpenCv Core loaded");
    }

    /**
     * Returning cropped face
     * @param imagePath
     * @return
     */
    public static Mat faceDetector(String imagePath){
        //Classifier for detecting face
        CascadeClassifier faceDetector = new CascadeClassifier(cascadeClassifierPath);
        //Creating Mat object from image passed as parameter
        Mat faceImage = Imgcodecs.imread(imagePath);
        MatOfRect faceDetections = new MatOfRect();
        //Detecting face
        faceDetector.detectMultiScale(faceImage, faceDetections);
        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

        //Drawing a black rectangle to crop a face
        Rect croppedFaceRectangle = null;
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(faceImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 0));
            croppedFaceRectangle = new Rect(rect.x, rect.y, rect.width, rect.height);
        }

        Mat croppedFace = new Mat(faceImage, croppedFaceRectangle);
        return croppedFace;
    }
}
