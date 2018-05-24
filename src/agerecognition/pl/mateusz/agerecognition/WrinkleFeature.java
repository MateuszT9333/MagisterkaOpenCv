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

    private enum DetectedObjectsEnum{
        CENTER_OF_EYES, EYES, EYE_PAIR, NOSE
    }

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
        Mat faceImage = getMatWithDetectedObjects(Paths.lbpcascadeFrontalFaceDetectorPath, grayImage
                , null, null
                , true);
        //Drawing a black rectangle to crop a face
        return faceImage;
    }

    /**
     * Method for detecting eyes and mouth and evaluating eyes centers and distance between them.
     *
     * @param croppedImage
     * @return Mat with detected objects as a black rectangles.
     */
    public static Mat detectPairOfEyesAndNose(Mat croppedImage) {
        //Reference copy
        Mat tempCroppedImage = croppedImage;

        //Detect eyes pair
        tempCroppedImage = detectEyesPair(tempCroppedImage);
        //Detect nose
        tempCroppedImage = detectNose(tempCroppedImage);
        //Detect eyes, evaluate center of eyes and distance between them
        tempCroppedImage = detectEyes(tempCroppedImage);

        //Detect mouth
//        tempCroppedImage = getMatWithDetectedObjects(mouthDetectorPath, tempCroppedImage, null,
//                false);
        return tempCroppedImage;

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

    private static Mat detectEyesPair(Mat croppedImage) {
        //Reference copy
        Mat tempCroppedImage = croppedImage;

        tempCroppedImage = getMatWithDetectedObjects(Paths.eyePairDetectorPath, tempCroppedImage, mapOfDetectedObjects
                , DetectedObjectsEnum.EYE_PAIR, false);
        return tempCroppedImage;
    }

    private static Mat detectNose(Mat croppedImage) {
        //Reference copy
        Mat tempCroppedImage = croppedImage;

        tempCroppedImage = getMatWithDetectedObjects(Paths.noseDetectorPath, tempCroppedImage, mapOfDetectedObjects
                , DetectedObjectsEnum.NOSE, false);
        return tempCroppedImage;
    }

    public static Mat detectEyes(Mat croppedImage) {
        //Reference copy
        Mat tempCroppedImage = croppedImage;

        tempCroppedImage = getMatWithDetectedObjects(Paths.eyesDetectorPath, tempCroppedImage, mapOfDetectedObjects
                , DetectedObjectsEnum.EYES, false);

        List<Rect> listOfEyesRectangles = mapOfDetectedObjects.get(DetectedObjectsEnum.EYES);
        if (listOfEyesRectangles.size() != 2) {
            System.err.println("Number of detected eyes is not equal 2... Are you alien?...");
            return tempCroppedImage;
        }
        Rect firstEyeRectangle = listOfEyesRectangles.get(0);
        Rect secondEyeRectangle = listOfEyesRectangles.get(1);

        List<Rect> centerOfEyes = evaluateCenterOfEye(firstEyeRectangle, secondEyeRectangle);
        mapOfDetectedObjects.put(DetectedObjectsEnum.CENTER_OF_EYES, centerOfEyes);
        return tempCroppedImage;
    }

    /**
     * @param firstEye
     * @param secondEye
     * @return
     */
    private static List<Rect> evaluateCenterOfEye(Rect firstEye, Rect secondEye) {
        //Reference copy
        Rect tempFirstEye = firstEye;
        Rect tempSecondEye = secondEye;

        Coordinates eyeOne = new Coordinates();
        Coordinates eyeTwo = new Coordinates();

        List<Rect> centerOfEyes = new ArrayList<>();

        eyeOne.x = tempFirstEye.x + tempFirstEye.width / 2;
        eyeOne.y = tempFirstEye.y + tempFirstEye.height / 2;

        eyeTwo.x = tempSecondEye.x + tempSecondEye.width / 2;
        eyeTwo.y = tempSecondEye.y + tempSecondEye.height / 2;

        distanseBetweenEyes = Coordinates.getDistance(eyeOne, eyeTwo);

        tempFirstEye = new Rect(eyeOne.x, eyeOne.y, 0, 0);
        tempSecondEye = new Rect(eyeTwo.x, eyeTwo.y, 0, 0);

        centerOfEyes.add(tempFirstEye);
        centerOfEyes.add(tempSecondEye);

        return centerOfEyes;
    }

    /**
     * Method generating Mat which include detected objects.
     *
     * @param cascadeClassifierPath
     * @param image
     * @param mapOfDetectedObjects      object which maps name of object to list of Rect.
     * @param kindOfDetectedObject
     * @param cropImageToDetectedObject
     * @return Mat with cropped detected area or with drew detected area.
     */
    private static Mat getMatWithDetectedObjects(String cascadeClassifierPath, Mat image
            , Map<DetectedObjectsEnum, List<Rect>> mapOfDetectedObjects, DetectedObjectsEnum kindOfDetectedObject
            , boolean cropImageToDetectedObject) {
        Mat tempImage = image;
        CascadeClassifier cascadeClassifier = new CascadeClassifier(cascadeClassifierPath);
        //Creating Mat object from image passed as parameter
        MatOfRect objectDetections = new MatOfRect();
        //Detecting objects
        // cascadeClassifier.detectMultiScale(tempImage, objectDetections);
        cascadeClassifier.detectMultiScale(tempImage, objectDetections, 1.1, 10,
                Objdetect.CASCADE_FIND_BIGGEST_OBJECT
                , new org.opencv.core.Size(30, 30),
                new org.opencv.core.Size());
        //Mapping kindOfObject e.g. nose to list of Rect which defining coordinates on image of detected objects.
        if (mapOfDetectedObjects != null) {
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

    public static Mat makeGrayImage(Mat oryginal) {
        Mat gray = new Mat();
        Imgproc.cvtColor(oryginal, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }

    /**
     * @param oryginal - must be gray image
     * @return image with detected edges
     */
    public static Mat detectEdges(Mat oryginal) {
        Mat canny = new Mat();
        Imgproc.Canny(oryginal, canny, 10, 100);
        return canny;
    }


}
