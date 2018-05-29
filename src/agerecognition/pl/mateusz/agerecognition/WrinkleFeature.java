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

import static pl.mateusz.agerecognition.utils.DetectedObjectsEnum.*;

public class WrinkleFeature {

    private final Map<DetectedObjectsEnum, List<Rect>> detectedObjects = new HashMap<>();
    private final List<Rect> wrinkleAreas = new ArrayList<>();
    private final String path;
    private final Mat processedMat;
    public float wrinkleFeatures;
    private Mat grayProcessedMat;
    private Mat croppedToFace;
    private Mat detectedNoseAndEyes;
    private Mat detectedEdges;

    /**
     * Constructor detecting face in image and calculating wrinkle featurer
     *
     * @param path
     * @throws NumberOfDetectedObjectsException if detected faces < 1
     */
    public WrinkleFeature(String path) throws NumberOfDetectedObjectsException {
        this.path = path;
        this.processedMat = Imgcodecs.imread(path);
        // this.croppedToFace = faceDetector(true);
        faceDetector();
        cropToFace();
        detectPairOfEyesAndNose();
        calculateWrinkleFeatures();

//        this.detectedNoseAndEyes = detectPairOfEyesAndNose(this.croppedToFace);
//        this.wrinkleFeatures = calculateWrinkleFeatures();
    }



    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("OpenCv Core loaded");
    }

    private static Mat drawARectanglesInMat(Mat source, Rect... rect) {
        Mat temp = source.clone();
        for (Rect rectItem : rect) {
            Imgproc.rectangle(temp, new Point(rectItem.x, rectItem.y), new Point(rectItem.x + rectItem.width
                    , rectItem.y + rectItem.height), new Scalar(0, 0, 0));
        }
        return temp;
    }

    private static Mat drawARectanglesInMat(Mat source, Scalar scalar, Rect... rect) {
        Mat temp = source.clone();
        for (Rect rectItem : rect) {
            Imgproc.rectangle(temp, new Point(rectItem.x, rectItem.y), new Point(rectItem.x + rectItem.width
                    , rectItem.y + rectItem.height), scalar);
        }
        return temp;
    }

    private static Mat generateCroppedMat(Mat processedImage, MatOfRect objectDetections) {
        Rect rectangleToCrop = null;
        for (Rect rect : objectDetections.toArray()) {
            rectangleToCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
        }
        return new Mat(processedImage, rectangleToCrop);
    }

    /**
     * @param source
     * @return image with detected edges
     */
    private static Mat detectEdges(Mat source) {
        Mat destination = source.clone();
        Imgproc.Canny(source, destination, 10, 100);
        return destination;
    }

    /**
     * Initialiaze this.croppedToFace and this.croppedToFaceWorkCopy fields
     *
     * @throws NumberOfDetectedObjectsException
     */
    private void cropToFace() throws NumberOfDetectedObjectsException {
        int detectedFaces = detectedObjects.get(FACES).size();

        if (detectedFaces < 1) {
            throw new NumberOfDetectedObjectsException(this.path);
        } else if (detectedFaces > 1) {
            //TODO
        } else {
            Rect detectedFace = detectedObjects.get(FACES).get(0);
            this.croppedToFace = generateCroppedMat(this.grayProcessedMat, detectedFace);
        }
    }

    public Mat getCroppedToFace() {
        return croppedToFace;
    }

    public Mat getProcessedMat() {
        return processedMat;
    }

    /**
     * @param firstEye
     * @param secondEye
     * @return
     */
    private static List<Rect> calculateCenterOfEye(Rect firstEye, Rect secondEye) {

        Point eyeOne = new Point();
        Point eyeTwo = new Point();


        eyeOne.x = firstEye.x + firstEye.width / 2;
        eyeOne.y = firstEye.y + firstEye.height / 2;

        eyeTwo.x = secondEye.x + secondEye.width / 2;
        eyeTwo.y = secondEye.y + secondEye.height / 2;

        Rect tempFirstEye = new Rect((int) eyeOne.x, (int) eyeOne.y, 0, 0);
        Rect tempSecondEye = new Rect((int) eyeTwo.x, (int) eyeTwo.y, 0, 0);

        List<Rect> eyeballsCenter = new ArrayList<>();

        eyeballsCenter.add(tempFirstEye);
        eyeballsCenter.add(tempSecondEye);

        return eyeballsCenter;
    }

    public Mat getDetectedNoseAndEyes() {
        return detectedNoseAndEyes;
    }

    public Mat getDetectedEdges() {
        return detectedEdges;
    }

    public Map<DetectedObjectsEnum, List<Rect>> getDetectedObjects() {
        return detectedObjects;
    }

    private static Mat generateCroppedMat(Mat processedImage, Rect rect) {

        Rect rectangleOfFaceToCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
        return new Mat(processedImage, rectangleOfFaceToCrop);
    }

    public List<Rect> getWrinkleAreas() {
        return wrinkleAreas;
    }

    /**
     * Adding faces rectangles to detectedObjectMap, initialize this.grayProcessedMat field
     *
     */
    private void faceDetector() {
        this.grayProcessedMat = this.processedMat.clone();
        makeGrayImage(grayProcessedMat);
        //Drawing a black rectangle to crop a face
        try {
            addDetectedObjects(Paths.lbpcascadeFrontalFaceDetectorPath
                    , grayProcessedMat
                    , FACES);
        } catch (NumberOfDetectedObjectsException e) {
            e.printStackTrace();
        }
    }

    private static void makeGrayImage(Mat oryginal) {
        Mat originalCopy = oryginal.clone();
        Imgproc.cvtColor(originalCopy, oryginal, Imgproc.COLOR_BGR2GRAY);
    }

    private void detectEyes(Mat croppedImage) {
        try {
            addDetectedObjects(Paths.eyesDetectorPath, croppedImage
                    , DetectedObjectsEnum.EYES);
        } catch (NumberOfDetectedObjectsException e) {
            e.printStackTrace();
        }

        List<Rect> listOfEyesRectangles = detectedObjects.get(DetectedObjectsEnum.EYES);

        Rect firstEyeRectangle = listOfEyesRectangles.get(0);
        Rect secondEyeRectangle = listOfEyesRectangles.get(1);

        List<Rect> eyeballsCenter = calculateCenterOfEye(firstEyeRectangle, secondEyeRectangle);
        detectedObjects.put(DetectedObjectsEnum.EYEBALLS_CENTER, eyeballsCenter);

    }

    private void detectEyesPair(Mat croppedImage) {
        try {
            addDetectedObjects(Paths.eyePairDetectorPath, croppedImage
                    , DetectedObjectsEnum.EYE_PAIR);
        } catch (NumberOfDetectedObjectsException e) {
            e.printStackTrace();
        }
    }

    private void detectNose(Mat croppedImage) {
        Mat temp = croppedImage;
        try {
            addDetectedObjects(Paths.noseDetectorPath, temp
                    , DetectedObjectsEnum.NOSE);
        } catch (NumberOfDetectedObjectsException e) {
            e.printStackTrace();
        }
    }

    private static Mat drawARectangleInMat(Mat tempImage, MatOfRect objectDetections) {
        for (Rect rect : objectDetections.toArray()) {
            Imgproc.rectangle(tempImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width
                    , rect.y + rect.height), new Scalar(0, 0, 0));
        }
        return tempImage;
    }


    private void drawARectangleInMat(Mat tempImage, Scalar color, List<Rect> rect) {
        for (Rect rectItem : rect) {
            Imgproc.rectangle(tempImage, new Point(rectItem.x, rectItem.y)
                    , new Point(rectItem.x + rectItem.width
                            , rectItem.y + rectItem.height), color);
        }
    }

    /**
     * Detecting pair of eyes an Noses and initialize  this.detectedNoseAndEyes field
     */
    private void detectPairOfEyesAndNose() {
        Mat temp = this.croppedToFace.clone();
        //Detect eyes pair
        detectEyesPair(temp);
        //Detect nose
        detectNose(temp);
        //Detect eyes, calculate center of eyes and distance between them
        detectEyes(temp);

        //Detect mouth
//        tempCroppedImage = addDetectedObjects(mouthDetectorPath, tempCroppedImage, null,
//                false);
        Rect centerOfEyeOne = this.detectedObjects
                .get(DetectedObjectsEnum.EYEBALLS_CENTER).get(0);

        Rect centerOfEyeTwo = this.detectedObjects
                .get(DetectedObjectsEnum.EYEBALLS_CENTER).get(1);

        centerOfEyeOne.width += 10;
        centerOfEyeOne.height += 10;

        centerOfEyeTwo.width += 10;
        centerOfEyeTwo.height += 10;

        this.detectedNoseAndEyes = drawARectanglesInMat(temp
                , detectedObjects.get(EYE_PAIR).get(0)
                , detectedObjects.get(NOSE).get(0)
                , centerOfEyeOne
                , centerOfEyeTwo);
    }

    /**
     * Method changing to Mat which include detected objects.
     * @param cascadeClassifierPath
     * @param image
     * @param kindOfDetectedObject
     * @param
     */

    private void addDetectedObjects(String cascadeClassifierPath
            , Mat image
            , DetectedObjectsEnum kindOfDetectedObject) throws NumberOfDetectedObjectsException {

        //Detecting objects
        CascadeClassifier cascadeClassifier = new CascadeClassifier(cascadeClassifierPath);
        MatOfRect objectDetections = new MatOfRect();

        // cascadeClassifier.detectMultiScale(tempImage, objectDetections);

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
        if ((kindOfDetectedObject == DetectedObjectsEnum.EYES)
                && (sizeOfObjectDetectionList != 2)) {
            throw new NumberOfDetectedObjectsException(this.path);
        }

        //Mapping kindOfObject e.g. nose to list of Rect which defining coordinates on image of detected objects.
        if (detectedObjects != null) {
            detectedObjects.put(kindOfDetectedObject, objectDetections.toList());
        }
    }

    private void calculateWrinkleFeatures() {

        Rect eyePair = detectedObjects.get(DetectedObjectsEnum.EYE_PAIR).get(0);
        Rect nose = detectedObjects.get(DetectedObjectsEnum.NOSE).get(0);

        Rect centerOfFirstEye = detectedObjects.get(DetectedObjectsEnum.EYEBALLS_CENTER).get(0);
        Rect centerOfSecondEye = detectedObjects.get(DetectedObjectsEnum.EYEBALLS_CENTER).get(1);

        Rect foreheadArea = getRectOfForeheadArea(centerOfFirstEye, centerOfSecondEye);
        Rect leftCheekArea = getRectOfLeftCheekArea(eyePair, nose);
        Rect rightCheekArea = getRectOfRightCheekArea(eyePair, nose);
        Rect leftEyeCornerArea = getRectOfLeftEyeCornerArea(leftCheekArea, eyePair);
        Rect rightEyeCornerArea = getRectOfRightEyeCornerArea(rightCheekArea, eyePair);

        this.detectedEdges = detectEdges(this.croppedToFace.clone());

        wrinkleAreas.add(foreheadArea);
        wrinkleAreas.add(leftCheekArea);
        wrinkleAreas.add(rightCheekArea);
        wrinkleAreas.add(leftEyeCornerArea);
        wrinkleAreas.add(rightEyeCornerArea);

        float sumOfWrinkleFeatures = 0;

        for (Rect wrinkleArea : wrinkleAreas) {
            sumOfWrinkleFeatures += calculateWhiteToAllPixelsRatio(wrinkleArea);
        }
        this.wrinkleFeatures = sumOfWrinkleFeatures;
    }

    private float calculateWhiteToAllPixelsRatio(Rect wrinkleArea) {
        Mat croppedWrinkleArea = new Mat();
        Mat croppedMat = generateCroppedMat(this.detectedEdges, wrinkleArea);
        Core.extractChannel(croppedMat, croppedWrinkleArea, 0);
        int whitePixels = Core.countNonZero(croppedWrinkleArea);
        return (float) whitePixels / (croppedWrinkleArea.rows() * croppedWrinkleArea.cols());
    }

    public void showWrinkleAreas(Scalar scalar) {
        Mat temp = this.detectedEdges.clone();
        drawARectangleInMat(temp, scalar, wrinkleAreas);
        Imshow.show(temp, "Wrinkle areas");
    }
    private Rect getRectOfForeheadArea(Rect centerOfFirstEye, Rect centerOfSecondEye) {

        Point rightEyeCenter;
        int distanseBetweenEyes = DistanceCalculator.getDistance(centerOfFirstEye, centerOfSecondEye);

        if (centerOfFirstEye.x < centerOfSecondEye.x) {
            rightEyeCenter = new Point(centerOfFirstEye.x, centerOfFirstEye.y);
        } else {
            rightEyeCenter = new Point(centerOfSecondEye.x, centerOfSecondEye.y);
        }

        Point startPointOfForehead = new Point(rightEyeCenter.x, rightEyeCenter.y - distanseBetweenEyes * 0.9);
        Size sizeOfForehead = new Size(distanseBetweenEyes, distanseBetweenEyes * 0.5);

        return new Rect(startPointOfForehead, sizeOfForehead);
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


    private Rect getRectOfLeftCheekArea(Rect eyePair, Rect nose) {

        Point lowerRightOfEyePair = new Point();
        Point centerOfRightNoseSide = new Point();

        lowerRightOfEyePair.x = eyePair.x + eyePair.width;
        lowerRightOfEyePair.y = eyePair.y + eyePair.height;

        centerOfRightNoseSide.x = nose.x + nose.width;
        centerOfRightNoseSide.y = nose.y + nose.height / 2;

        return new Rect(lowerRightOfEyePair, centerOfRightNoseSide);
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

    private Rect getRectOfLeftEyeCornerArea(Rect leftCheekArea, Rect eyePair) {

        Point endPoint = new Point();
        Point centerOfRightSideOfEyePair = new Point();

        centerOfRightSideOfEyePair.x = eyePair.x + eyePair.width;
        centerOfRightSideOfEyePair.y = eyePair.y + eyePair.height / 2;

        endPoint.x = leftCheekArea.x + leftCheekArea.width + leftCheekArea.width * (0.4);
        endPoint.y = leftCheekArea.y + leftCheekArea.height / 2;

        return new Rect(centerOfRightSideOfEyePair, endPoint);
    }

}
