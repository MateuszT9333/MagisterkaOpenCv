/**
 * @author Mateusz Trzeciak
 * Class for detecting wrinkle features in face image...
 */
package pl.mateusz.agerecognition;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import pl.mateusz.agerecognition.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrinkleFeature {

    public final Map<DetectedObjectsEnum, List<Rect>> mapOfDetectedObjects = new HashMap<>();
    public final List<Rect> wrinkleAreas = new ArrayList<>();
    private final String path;
    private final Mat processedMat;
    private final Mat croppedToFace;

    public WrinkleFeature(String path) {
        this.path = path;
        this.processedMat = Imgcodecs.imread(path);
        this.croppedToFace = faceDetector(processedMat, true);
        detectPairOfEyesAndNose(this.croppedToFace);
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("OpenCv Core loaded");
    }

    /**
     * @param firstEye
     * @param secondEye
     * @return
     */
    private static List<Rect> calculateCenterOfEye(Rect firstEye, Rect secondEye) {

        Coordinates eyeOne = new Coordinates();
        Coordinates eyeTwo = new Coordinates();


        eyeOne.x = firstEye.x + firstEye.width / 2;
        eyeOne.y = firstEye.y + firstEye.height / 2;

        eyeTwo.x = secondEye.x + secondEye.width / 2;
        eyeTwo.y = secondEye.y + secondEye.height / 2;

        Rect tempFirstEye = new Rect(eyeOne.x, eyeOne.y, 0, 0);
        Rect tempSecondEye = new Rect(eyeTwo.x, eyeTwo.y, 0, 0);

        List<Rect> eyeballsCenter = new ArrayList<>();

        eyeballsCenter.add(tempFirstEye);
        eyeballsCenter.add(tempSecondEye);

        return eyeballsCenter;
    }

    public static void drawARectanglesInMat(Mat tempImage, Rect... rect) {
        for (Rect rectItem : rect) {
            Imgproc.rectangle(tempImage, new Point(rectItem.x, rectItem.y), new Point(rectItem.x + rectItem.width
                    , rectItem.y + rectItem.height), new Scalar(0, 0, 0));
        }
    }

    private static Mat generateCroppedMat(Mat processedImage, MatOfRect objectDetections) {
        Rect rectangleOfFaceToCrop = null;
        for (Rect rect : objectDetections.toArray()) {
            rectangleOfFaceToCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
        }
        return new Mat(processedImage, rectangleOfFaceToCrop);
    }

    /**
     * Returning detected face area (cropped or in rectangle, depends on cropImage boolean parameter)
     *
     * @param imageToProcess
     * @return detected face area (cropped or in rectangle, depends on cropImage boolean parameter)
     */
    public Mat faceDetector(Mat imageToProcess, boolean cropImage) {
        makeGrayImage(imageToProcess);
        //Drawing a black rectangle to crop a face
        try {
            return generateMatWithDetectedObjects(Paths.lbpcascadeFrontalFaceDetectorPath, imageToProcess
                    , null, null
                    , cropImage);
        } catch (NumberOfDetectedObjectsException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void detectEyes(Mat croppedImage) {

        try {
            generateMatWithDetectedObjects(Paths.eyesDetectorPath, croppedImage, mapOfDetectedObjects
                    , DetectedObjectsEnum.EYES, false);
        } catch (NumberOfDetectedObjectsException e) {
            e.printStackTrace();
        }

        List<Rect> listOfEyesRectangles = mapOfDetectedObjects.get(DetectedObjectsEnum.EYES);

        Rect firstEyeRectangle = listOfEyesRectangles.get(0);
        Rect secondEyeRectangle = listOfEyesRectangles.get(1);

        List<Rect> eyeballsCenter = calculateCenterOfEye(firstEyeRectangle, secondEyeRectangle);
        mapOfDetectedObjects.put(DetectedObjectsEnum.EYEBALLS_CENTER, eyeballsCenter);
    }

    /**
     * 
     *
     * @param croppedImage
     * @return Mat with detected objects as a black rectangles.
     */
    public void detectPairOfEyesAndNose(Mat croppedImage) {

        //Detect eyes pair
        detectEyesPair(croppedImage);
        //Detect nose
        detectNose(croppedImage);
        //Detect eyes, calculate center of eyes and distance between them
        detectEyes(croppedImage);

        //Detect mouth
//        tempCroppedImage = generateMatWithDetectedObjects(mouthDetectorPath, tempCroppedImage, null,
//                false);
    }

    private void detectEyesPair(Mat croppedImage) {
        try {
            generateMatWithDetectedObjects(Paths.eyePairDetectorPath, croppedImage, mapOfDetectedObjects
                    , DetectedObjectsEnum.EYE_PAIR, false);
        } catch (NumberOfDetectedObjectsException e) {
            e.printStackTrace();
        }
    }

    private void detectNose(Mat croppedImage) {

        try {
            generateMatWithDetectedObjects(Paths.noseDetectorPath, croppedImage, mapOfDetectedObjects
                    , DetectedObjectsEnum.NOSE, false);
        } catch (NumberOfDetectedObjectsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method changing to Mat which include detected objects.
     * @param cascadeClassifierPath
     * @param image
     * @param mapOfDetectedObjects
     * @param kindOfDetectedObject
     * @param
     */

    private Mat generateMatWithDetectedObjects(String cascadeClassifierPath, Mat image
            , Map<DetectedObjectsEnum, List<Rect>> mapOfDetectedObjects, DetectedObjectsEnum kindOfDetectedObject
            , boolean cropImage) throws NumberOfDetectedObjectsException {

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

        int sizeOfObjectDetectionList = objectDetections.toList().size();

        //If detected pair of eyes or nose is not equal one then throw NumberOfDetectedObjectsException
        if (((kindOfDetectedObject == DetectedObjectsEnum.EYE_PAIR)
                || (kindOfDetectedObject == DetectedObjectsEnum.NOSE))
                && sizeOfObjectDetectionList != 1) {
            throw new NumberOfDetectedObjectsException(this.path);
        }
        // If detected number of eyes and center of eyes is not equal to throw NumberOfDetectedObjectsException
        if (((kindOfDetectedObject == DetectedObjectsEnum.EYES) ||
                (kindOfDetectedObject == DetectedObjectsEnum.EYEBALLS_CENTER))
                && (sizeOfObjectDetectionList != 2)) {
            throw new NumberOfDetectedObjectsException(this.path);
        }
        //Mapping kindOfObject e.g. nose to list of Rect which defining coordinates on image of detected objects.
        if (mapOfDetectedObjects != null) {
            mapOfDetectedObjects.put(kindOfDetectedObject, objectDetections.toList());
        }
        //Croping an image to detected object
        if (cropImage == true) {
            return generateCroppedMat(image, objectDetections);
        } else {
            //Drawing a rectangle over detected object
            return drawARectangleInMat(image, objectDetections);
        }

    }

    private static Mat drawARectangleInMat(Mat tempImage, MatOfRect objectDetections) {
        for (Rect rect : objectDetections.toArray()) {
            Imgproc.rectangle(tempImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width
                    , rect.y + rect.height), new Scalar(0, 0, 0));
        }
        return tempImage;
    }

    private void drawARectangleInMat(Mat tempImage, Rect rect, Scalar color) {
        Imgproc.rectangle(tempImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width
                , rect.y + rect.height), color);
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

    private void calculateWrinkleFeatures() {
        Rect eyePair = mapOfDetectedObjects.get(DetectedObjectsEnum.EYE_PAIR).get(0);
        Rect nose = mapOfDetectedObjects.get(DetectedObjectsEnum.NOSE).get(0);

        Rect centerOfFirstEye = mapOfDetectedObjects.get(DetectedObjectsEnum.EYEBALLS_CENTER).get(0);
        Rect centerOfSecondEye = mapOfDetectedObjects.get(DetectedObjectsEnum.EYEBALLS_CENTER).get(1);

        Rect foreheadArea = getRectOfForeheadArea(centerOfFirstEye, centerOfSecondEye);
        Rect leftCheekArea = getRectOfLeftCheekArea(eyePair, nose);
        Rect rightCheekArea = getRectOfRightCheekArea(eyePair, nose);
        Rect leftEyeCornerArea = getRectOfLeftEyeCornerArea(leftCheekArea, eyePair);
        Rect rightEyeCornerArea = getRectOfRightEyeCornerArea(rightCheekArea, eyePair);

    }

    private Rect getRectOfForeheadArea(Rect centerOfFirstEye, Rect centerOfSecondEye) {

        Point rightEyeCenter;
        int distanseBetweenEyes = Coordinates.getDistance(centerOfFirstEye, centerOfSecondEye);

        if (centerOfFirstEye.x < centerOfSecondEye.x) {
            rightEyeCenter = new Point(centerOfFirstEye.x, centerOfFirstEye.y);
        } else {
            rightEyeCenter = new Point(centerOfSecondEye.x, centerOfSecondEye.y);
        }

        Point startPointOfForehead = new Point(rightEyeCenter.x, rightEyeCenter.y - distanseBetweenEyes * 0.9);
        Size sizeOfForehead = new Size(distanseBetweenEyes, distanseBetweenEyes * 0.5);

        return new Rect(startPointOfForehead, sizeOfForehead);
    }

    //Test
    public void getRectOfForeheadAreaTest() {

        Rect centerOfFirstEye = mapOfDetectedObjects.get(DetectedObjectsEnum.EYEBALLS_CENTER).get(0);
        Rect centerOfSecondEye = mapOfDetectedObjects.get(DetectedObjectsEnum.EYEBALLS_CENTER).get(1);

        Rect foreHead = getRectOfForeheadArea(centerOfFirstEye, centerOfSecondEye);
        drawARectangleInMat(this.croppedToFace, foreHead, new Scalar(255, 0, 0));
        Imshow.show(croppedToFace, "RightCheekArea");
    }

    private Rect getRectOfRightCheekArea(Rect eyePair, Rect nose) {

        Point lowerLeftOfEyePair = new Point();
        Point centerOfLeftNoseSide = new Point();

        lowerLeftOfEyePair.x = eyePair.x;
        lowerLeftOfEyePair.y = eyePair.y + eyePair.height;

        centerOfLeftNoseSide.x = nose.x;
        centerOfLeftNoseSide.y = nose.y + nose.height / 2;

        return new Rect(lowerLeftOfEyePair, centerOfLeftNoseSide);
    }

    //Test
    public void getRectOfRightCheekAreaTest() {

        Rect eyePair = mapOfDetectedObjects.get(DetectedObjectsEnum.EYE_PAIR).get(0);
        Rect nose = mapOfDetectedObjects.get(DetectedObjectsEnum.NOSE).get(0);

        Rect rightCheekArea = getRectOfRightCheekArea(eyePair, nose);
        drawARectangleInMat(this.croppedToFace, rightCheekArea, new Scalar(255, 0, 0));
        Imshow.show(croppedToFace, "RightCheekArea");
    }


    private Rect getRectOfLeftCheekArea(Rect eyePair, Rect nose) {

        Point lowerRightOfEyePair = new Point();
        Point centerOfRightNoseSide = new Point();

        lowerRightOfEyePair.x = eyePair.x + eyePair.width;
        lowerRightOfEyePair.y = eyePair.y + eyePair.height;

        centerOfRightNoseSide.x = nose.x + nose.width;
        centerOfRightNoseSide.y = nose.y + nose.height / 2;

        return new Rect(lowerRightOfEyePair, centerOfRightNoseSide);
    }

    //Test
    public void getRectOfLeftCheekAreaTest() {

        Rect eyePair = mapOfDetectedObjects.get(DetectedObjectsEnum.EYE_PAIR).get(0);
        Rect nose = mapOfDetectedObjects.get(DetectedObjectsEnum.NOSE).get(0);

        Rect leftCheekArea = getRectOfLeftCheekArea(eyePair, nose);
        drawARectangleInMat(this.croppedToFace, leftCheekArea, new Scalar(255, 0, 0));
        Imshow.show(croppedToFace, "Left Cheek Area");
    }

    private Rect getRectOfRightEyeCornerArea(Rect rightCheekArea, Rect eyePair) {

        Point startPoint = new Point();
        Point centerOfLeftSideOfRightCheekArea = new Point();

        startPoint.x = eyePair.x - rightCheekArea.width * (0.5);
        startPoint.y = eyePair.y + eyePair.height / 2;

        centerOfLeftSideOfRightCheekArea.x = rightCheekArea.x;
        centerOfLeftSideOfRightCheekArea.y = rightCheekArea.y + rightCheekArea.height / 2;

        return new Rect(startPoint, centerOfLeftSideOfRightCheekArea);
    }

    //Test
    public void getRectOfRightEyeCornerAreaTest() {

        Rect eyePair = mapOfDetectedObjects.get(DetectedObjectsEnum.EYE_PAIR).get(0);
        Rect nose = mapOfDetectedObjects.get(DetectedObjectsEnum.NOSE).get(0);
        Rect rightCheekArea = getRectOfRightCheekArea(eyePair, nose);

        Rect rightCornerArea = getRectOfRightEyeCornerArea(rightCheekArea, eyePair);
        drawARectangleInMat(this.croppedToFace, rightCornerArea, new Scalar(255, 0, 0));
        Imshow.show(croppedToFace, "Right Eyecorner Area");
    }

    private Rect getRectOfLeftEyeCornerArea(Rect leftCheekArea, Rect eyePair) {

        Point endPoint = new Point();
        Point centerOfRightSideOfEyePair = new Point();

        centerOfRightSideOfEyePair.x = eyePair.x + eyePair.width;
        centerOfRightSideOfEyePair.y = eyePair.y + eyePair.height / 2;

        endPoint.x = leftCheekArea.x + leftCheekArea.width + leftCheekArea.width * (0.4);
        endPoint.y = leftCheekArea.y + leftCheekArea.height / 2;

        return new Rect(centerOfRightSideOfEyePair, endPoint);
    }

    //Test
    public void getRectOfLeftEyeCornerAreaTest() {

        Rect eyePair = mapOfDetectedObjects.get(DetectedObjectsEnum.EYE_PAIR).get(0);
        Rect nose = mapOfDetectedObjects.get(DetectedObjectsEnum.NOSE).get(0);
        Rect leftCheekArea = getRectOfLeftCheekArea(eyePair, nose);

        Rect leftCornerArea = getRectOfLeftEyeCornerArea(leftCheekArea, eyePair);
        drawARectangleInMat(this.croppedToFace, leftCornerArea, new Scalar(255, 0, 0));
        Imshow.show(croppedToFace, "Left Eyecorner Area");
    }

}
