package pl.mateusz.agerecognition;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import pl.mateusz.agerecognition.utils.Coordinates;

import java.util.*;

public class WrinkleFeature {
    final static String resourcesPath = "src/resources/";
    final static String testImagesPath = resourcesPath + "testImages/";
    final static String trainingImagesPath = resourcesPath + "trainingImages/";
    final static String xmlPath = resourcesPath + "xml/";
    final static String lbpcascadeFrontalFaceDetectorPath = xmlPath + "lbpcascade_frontalface.xml";
    final static String mouthDetectorPath = xmlPath + "haarcascade_mouth.xml";
    final static String noseDetectorPath = xmlPath + "haarcascade_nose.xml";
    final static String eyePairDetectorPath = xmlPath + "haarcascade_eye_pair_big.xml";
    final static String eyesDetectorPath = xmlPath + "haarcascade_eyes.xml";



    //Only one instance of mapOfDetectecObjects. This map has List of rectangles of detectec object e.g. nose.
    public final static Map<String, List<Rect>> mapOfDetectedObjects = new HashMap<>();
    public static int distanseBetweenEyes = 0;

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
        Mat faceImage = getMatWithDetectedObjects(lbpcascadeFrontalFaceDetectorPath, grayImage, null, "Detected %s faces"
                , true);
        //Drawing a black rectangle to crop a face
        return faceImage;
    }

    /**
     * Method for detecting eyes and mouth.
     * @param croppedImage
     * @return Mat with detected objects as a black rectangles.
     */
    public static Mat detectPairOfEyesAndNose(Mat croppedImage) {
        Mat tempCroppedImage = croppedImage;

        //Detect eyes
        tempCroppedImage = getMatWithDetectedObjects(eyePairDetectorPath, tempCroppedImage, mapOfDetectedObjects
                , "EyePair", false);
        //Detect nose
        tempCroppedImage = getMatWithDetectedObjects(noseDetectorPath, tempCroppedImage, mapOfDetectedObjects
                , "Nose",  false);
        tempCroppedImage = detectEyes(tempCroppedImage);
        //Detect mouth
//        tempCroppedImage = getMatWithDetectedObjects(mouthDetectorPath, tempCroppedImage, null,
//                false);
        return tempCroppedImage;

    }
    public static Mat detectEyes(Mat croppedImage){
        //Reference copy
        Mat tempCroppedImage = croppedImage;

        tempCroppedImage = getMatWithDetectedObjects(eyesDetectorPath, tempCroppedImage, mapOfDetectedObjects
                , "Eyes", false);

        List<Rect> listOfEyesRectangles = mapOfDetectedObjects.get("Eyes");
        if(listOfEyesRectangles.size() != 2){
            System.err.println("Number of detected eyes is not equal 2... Are you alien?...");
            return tempCroppedImage;
        }
        Rect firstEyeRectangle = listOfEyesRectangles.get(0);
        Rect secondEyeRectangle = listOfEyesRectangles.get(1);

        List<Rect> centerOfEyes  = evaluateCenterOfEye(firstEyeRectangle, secondEyeRectangle);
        mapOfDetectedObjects.put("Center of Eyes", centerOfEyes);
     return tempCroppedImage;
    }


    private static List<Rect> evaluateCenterOfEye(Rect firstEye, Rect secondEye) {
        //Reference copy
        Rect tempFirstEye = firstEye;
        Rect tempSecondEye = secondEye;

        Coordinates eyeOne = new Coordinates();
        Coordinates eyeTwo = new Coordinates();

        List<Rect> centerOfEyes = new ArrayList<>();

        eyeOne.x = tempFirstEye.x + tempFirstEye.width/2;
        eyeOne.y = tempFirstEye.y + tempFirstEye.height/2;

        eyeTwo.x = tempSecondEye.x + tempSecondEye.width/2;
        eyeTwo.y = tempSecondEye.y + tempSecondEye.height/2;

        distanseBetweenEyes = Coordinates.getDistance(eyeOne, eyeTwo);

        tempFirstEye = new Rect(eyeOne.x, eyeOne.y, 0, 0);
        tempSecondEye = new Rect(eyeTwo.x, eyeTwo.y, 0, 0);

        centerOfEyes.add(tempFirstEye);
        centerOfEyes.add(tempSecondEye);

        return centerOfEyes;
    }

    /**
     * Method generating Mat which include detected objects.
     * @param cascadeClassifierPath
     * @param image
     * @param mapOfDetectedObjects object which maps name of object to list of Rect.
     * @param kindOfDetectedObject
     * @param cropImageToDetectedObject
     * @return Mat with cropped detected area or with drew detected area.
     */
    private static Mat getMatWithDetectedObjects(String cascadeClassifierPath, Mat image, Map<String, List<Rect>> mapOfDetectedObjects, String kindOfDetectedObject, boolean cropImageToDetectedObject) {
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
        //Mapping kindOfObject e.g. nose to list of Rect which defining coordinates on image of detected objects.
        if(mapOfDetectedObjects != null) {
            mapOfDetectedObjects.put(kindOfDetectedObject, objectDetections.toList());
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
