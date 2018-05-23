package pl.mateusz.agerecognition;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

public class WrinkleFeature {
    final static String resourcesPath = "src/resources/";
    final static String testImagesPath = resourcesPath + "testImages/";
    final static String trainingImagesPath = resourcesPath + "trainingImages/";
    final static String xmlPath = resourcesPath + "xml/";
    final static String lbpcascadeFrontalFaceDetectorPath = xmlPath + "lbpcascade_frontalface.xml";
    final static String mouthDetectorPath = xmlPath + "haarcascade_mouth.xml";
    final static String noseDetectorPath = xmlPath + "haarcascade_nose.xml";
    final static String eyeDetectorPath = xmlPath + "haarcascade_eye_big.xml";

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("OpenCv Core loaded");
    }

    /**
     * Returning cropped face
     *
     * @param image
     * @return
     */
    public static Mat faceDetector(Mat image) {
        //Make a Mat with cropped face area...
        Mat grayImage = makeGrayImage(image);
        Mat faceImage = getMatWithDetectedObjects(lbpcascadeFrontalFaceDetectorPath, grayImage, null,
                true);
        //Drawing a black rectangle to crop a face
        return faceImage;
    }

    public static Mat detectEyesMouthNose(Mat croppedImage) {
        Mat tempCroppedImage = croppedImage;
        //Detect eyes
        tempCroppedImage = getMatWithDetectedObjects(eyeDetectorPath, tempCroppedImage, null,
               false);
        //Detect nose
        tempCroppedImage = getMatWithDetectedObjects(noseDetectorPath, tempCroppedImage, null,
              false);
        //Detect mouth
//        tempCroppedImage = getMatWithDetectedObjects(mouthDetectorPath, tempCroppedImage, null,
//                false);
        return tempCroppedImage;

    }

    /**
     * @param cascadeClassifierPath
     * @param image
     * @param message               if message is null it will display "Detected x objects".
     *                              If you want to display another message type "Detected %s [typeOfObjects]"
     *                              In [typeOfObjects] you have to write a type of objects you currently detecting.
     *                              Example: "Detected %s faces".
     *                              %s is required because it represents a number of detected objects e.g. faces in image.
     * @return
     */
    private static Mat getMatWithDetectedObjects(String cascadeClassifierPath, Mat image, String message,
                                                 boolean cropImageToDetectedObject) {
        Mat tempImage = image;
        CascadeClassifier cascadeClassifier = new CascadeClassifier(cascadeClassifierPath);
        //Creating Mat object from image passed as parameter
        MatOfRect objectDetections = new MatOfRect();
        //Detecting objects
       // cascadeClassifier.detectMultiScale(tempImage, objectDetections);
        cascadeClassifier.detectMultiScale(tempImage, objectDetections,1.1, 10,
                Objdetect.CASCADE_FIND_BIGGEST_OBJECT
                , new org.opencv.core.Size(30, 30),
                new org.opencv.core.Size());

        if (message == null) {
            System.out.println(String.format("Detected %s objects", objectDetections.toArray().length));

        } else if (!message.contains("%s")) {

            System.out.println("Wrong format of message parameter... I'll write default string");
            System.out.println(String.format("Detected %s objects", objectDetections.toArray().length));

        } else {
            System.out.println(String.format(message, objectDetections.toArray().length));
        }

        //Croping an image to detected object
        if (cropImageToDetectedObject) {
            return getCroppedMat(tempImage, objectDetections);
        } else {
            //Drawing a black rectangle over detected object
            return drawARectangleInMat(tempImage, objectDetections);
        }

    }

    private static Mat drawARectangleInMat(Mat tempImage, MatOfRect objectDetections) {
        for (Rect rect : objectDetections.toArray()) {
            Imgproc.rectangle(tempImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y +
                    rect.height), new Scalar(0, 0, 0));
        }
        return tempImage;
    }

    private static Mat getCroppedMat(Mat tempImage, MatOfRect objectDetections) {
        Rect croppedFaceRectangle = null;
        for (Rect rect : objectDetections.toArray()) {
            Imgproc.rectangle(tempImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y +
                    rect.height), new Scalar(0, 0, 0));
            croppedFaceRectangle = new Rect(rect.x, rect.y, rect.width, rect.height);
        }
        Mat croppedImage = new Mat(tempImage, croppedFaceRectangle);
        return croppedImage;
    }

    public static Mat makeGrayImage(Mat oryginal){
        Mat gray = new Mat();
        Imgproc.cvtColor(oryginal, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }

    /**
     *
     * @param oryginal - must be gray image
     * @return image with detected edges
     */
    public static Mat detectEdges(Mat oryginal){
        Mat canny = new Mat();
        Imgproc.Canny(oryginal, canny, 10, 100);
        return canny;
    }


}
