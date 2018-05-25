/**
 * @author Mateusz Trzeciak
 * Class for detecting wrinkle features in face image...
 */
package pl.mateusz.agerecognition;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import pl.mateusz.agerecognition.utils.Coordinates;
import pl.mateusz.agerecognition.utils.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrinkleFeature {

    public final static Map<DetectedObjectsEnum, List<Rect>> mapOfDetectedObjects = new HashMap<>();
    public static int distanseBetweenEyes = 0;
    public static List<Rect> wrinkleAreas = new ArrayList<>();

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
        makeGrayImage(image);
        //Drawing a black rectangle to crop a face
        return generateMatWithDetectedObjects(Paths.lbpcascadeFrontalFaceDetectorPath, image
                , null, null
                , true);
    }

    public static void detectEyes(Mat croppedImage) {

        generateMatWithDetectedObjects(Paths.eyesDetectorPath, croppedImage, mapOfDetectedObjects
                , DetectedObjectsEnum.EYES, false);

        List<Rect> listOfEyesRectangles = mapOfDetectedObjects.get(DetectedObjectsEnum.EYES);

        if (listOfEyesRectangles.size() != 2) {
            System.err.println("Number of detected eyes is not equal 2... Are you alien?...");
            return;
        }

        Rect firstEyeRectangle = listOfEyesRectangles.get(0);
        Rect secondEyeRectangle = listOfEyesRectangles.get(1);

        List<Rect> eyeballsCenter = evaluateCenterOfEye(firstEyeRectangle, secondEyeRectangle);
        mapOfDetectedObjects.put(DetectedObjectsEnum.EYEBALLS_CENTER, eyeballsCenter);
    }

    /**
     * @param firstEye
     * @param secondEye
     * @return
     */
    private static List<Rect> evaluateCenterOfEye(Rect firstEye, Rect secondEye) {

        Coordinates eyeOne = new Coordinates();
        Coordinates eyeTwo = new Coordinates();


        eyeOne.x = firstEye.x + firstEye.width / 2;
        eyeOne.y = firstEye.y + firstEye.height / 2;

        eyeTwo.x = secondEye.x + secondEye.width / 2;
        eyeTwo.y = secondEye.y + secondEye.height / 2;

        distanseBetweenEyes = Coordinates.getDistance(eyeOne, eyeTwo);

        Rect tempFirstEye = new Rect(eyeOne.x, eyeOne.y, 0, 0);
        Rect tempSecondEye = new Rect(eyeTwo.x, eyeTwo.y, 0, 0);

        List<Rect> eyeballsCenter = new ArrayList<>();

        eyeballsCenter.add(tempFirstEye);
        eyeballsCenter.add(tempSecondEye);

        return eyeballsCenter;
    }

    /**
     * Method for detecting eyes and mouth and evaluating eyes centers and distance between them.
     *
     * @param croppedImage
     * @return Mat with detected objects as a black rectangles.
     */
    public static void detectPairOfEyesAndNose(Mat croppedImage) {

        //Detect eyes pair
        detectEyesPair(croppedImage);
        //Detect nose
        detectNose(croppedImage);
        //Detect eyes, evaluate center of eyes and distance between them
        detectEyes(croppedImage);

        //Detect mouth
//        tempCroppedImage = generateMatWithDetectedObjects(mouthDetectorPath, tempCroppedImage, null,
//                false);
    }

    private static void detectEyesPair(Mat croppedImage) {

        generateMatWithDetectedObjects(Paths.eyePairDetectorPath, croppedImage, mapOfDetectedObjects
                , DetectedObjectsEnum.EYE_PAIR, false);
    }

    private static void detectNose(Mat croppedImage) {

        generateMatWithDetectedObjects(Paths.noseDetectorPath, croppedImage, mapOfDetectedObjects
                , DetectedObjectsEnum.NOSE, false);
    }

    private static void evaluateWrinkleFeatures() {
        Rect foreheadArea = getRectOfForeheadArea();
        Rect firstCheekArea = getRectOfFirstCheekArea();
        Rect secondCheekArea = getRectOfSecondCheekArea();
        Rect firstEyeCornerArea = getRectOfFirstEyeCornerArea();
        Rect secondEyeCornerArea = getRectOfSecondEyeCornerArea();

    }

    private static Rect getRectOfSecondEyeCornerArea() {

        return null;
    }

    private static Rect getRectOfFirstEyeCornerArea() {
        return null;
    }

    private static Rect getRectOfSecondCheekArea() {
        return null;
    }

    private static Rect getRectOfFirstCheekArea() {
        return null;
    }


    private static Rect getRectOfForeheadArea() {
        return null;
    }

    /**
     * Method changing to Mat which include detected objects.
     * @param cascadeClassifierPath
     * @param image
     * @param mapOfDetectedObjects
     * @param kindOfDetectedObject
     * @param cropImageToDetectedObject
     */

    private static Mat generateMatWithDetectedObjects(String cascadeClassifierPath, Mat image
            , Map<DetectedObjectsEnum, List<Rect>> mapOfDetectedObjects, DetectedObjectsEnum kindOfDetectedObject
            , boolean cropImageToDetectedObject) {
        CascadeClassifier cascadeClassifier = new CascadeClassifier(cascadeClassifierPath);
        //Creating Mat object from image passed as parameter
        MatOfRect objectDetections = new MatOfRect();

        // cascadeClassifier.detectMultiScale(tempImage, objectDetections);
        //Detecting objects
        cascadeClassifier.detectMultiScale(image, objectDetections, 1.1
                , 10
                , Objdetect.CASCADE_FIND_BIGGEST_OBJECT
                , new org.opencv.core.Size(30, 30)
                , new org.opencv.core.Size());

        //Mapping kindOfObject e.g. nose to list of Rect which defining coordinates on image of detected objects.
        if (mapOfDetectedObjects != null) {
            mapOfDetectedObjects.put(kindOfDetectedObject, objectDetections.toList());
        }

        //Croping an image to detected object
        if (cropImageToDetectedObject) {
            return generateCroppedMat(image, objectDetections);
        } else {
            //Drawing a rectangle over detected object
            return drawARectangleInMat(image, objectDetections);
        }

    }

    public static void drawARectangleInMat(Mat tempImage, Rect rect) {
        Imgproc.rectangle(tempImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width
                , rect.y + rect.height), new Scalar(0, 0, 0));
    }

    private static Mat generateCroppedMat(Mat image, MatOfRect objectDetections) {
        Rect rectangleOfFaceToCrop = null;
        for (Rect rect : objectDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y +
                    rect.height), new Scalar(0, 0, 0));
            rectangleOfFaceToCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
        }
        return new Mat(image, rectangleOfFaceToCrop);
    }

    private static Mat drawARectangleInMat(Mat tempImage, MatOfRect objectDetections) {
        for (Rect rect : objectDetections.toArray()) {
            Imgproc.rectangle(tempImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width
                    , rect.y + rect.height), new Scalar(0, 0, 0));
        }
        return tempImage;
    }

    public static void makeGrayImage(Mat oryginal) {
        Mat originalCopy = oryginal.clone();
        Imgproc.cvtColor(originalCopy, oryginal, Imgproc.COLOR_BGR2GRAY);
    }

    /**
     * @param oryginal - must be gray image
     * @return image with detected edges
     */
    public static void detectEdges(Mat oryginal) {
        Mat originalCopy = oryginal.clone();
        Imgproc.Canny(originalCopy, oryginal, 10, 100);
    }

    public enum DetectedObjectsEnum {
        EYEBALLS_CENTER, EYES, EYE_PAIR, NOSE
    }

    public enum WrinkleRegionsEnum {
        FOREHEAD, FIRST_CHEEK, SECOND_CHEEK, FIRST_EYECORNER, SECOND_EYECORNER
    }


}
