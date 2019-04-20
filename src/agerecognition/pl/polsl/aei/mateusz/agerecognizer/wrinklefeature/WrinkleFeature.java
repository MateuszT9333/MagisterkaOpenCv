package pl.polsl.aei.mateusz.agerecognizer.wrinklefeature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import pl.polsl.aei.mateusz.agerecognizer.utils.*;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.polsl.aei.mateusz.agerecognizer.utils.DetectedObjectsEnum.*;

/**
 * @author Mateusz Trzeciak
 * Class for detecting wrinkle features in face image.
 */
public class WrinkleFeature {
    static final Logger log = LogManager.getLogger("main");


    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        log.info("OpenCv Core loaded");
    }

    private final Map<DetectedObjectsEnum, List<Rect>> detectedObjects = new HashMap<>();
    private final List<Rect> wrinkleAreas = new ArrayList<>();
    private File path;
    private Mat processedMat;
    private float wrinkleFeatures;
    private Mat grayProcessedMat = null;
    private Mat croppedToFace;
    private Mat detectedNoseAndEyes;
    private Mat detectedEdges;

    /**
     * Detecting face in image and calculating wrinkle featurer
     *
     * @param path            path to processed file
     * @param isCroppedToFace If image is cropped to face. Especially for training algorithm
     */
    public WrinkleFeature(File path, boolean isCroppedToFace) throws WrinkleFeaturesException {

        this.path = path;
        processedMat = Imgcodecs.imread(path.getAbsolutePath());

//        if (isCroppedToFace) {
//            croppedToFace = processedMat;
//            new WrinkleFeature();
//            return;
//        }
        grayProcessedMat = faceDetector(processedMat);
        croppedToFace = cropToFace(grayProcessedMat);
        detectedNoseAndEyes = detectPairOfEyesAndNose(croppedToFace);
        calculateWrinkleFeatures();

    }

    private WrinkleFeature() throws WrinkleFeaturesException {
        detectedNoseAndEyes = detectPairOfEyesAndNose(croppedToFace);
        calculateWrinkleFeatures();

    }

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

    public Mat getGrayProcessedMat() {
        return grayProcessedMat;
    }

    public Mat getCroppedToFace() {
        return croppedToFace;
    }

    public Mat getProcessedMat() {
        return processedMat;
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

    public List<Rect> getWrinkleAreas() {
        return wrinkleAreas;
    }

    public float getWrinkleFeatures() {
        return wrinkleFeatures;
    }

    /**
     * Adding faces rectangles to detectedObjectMap, initializes this.grayProcessedMat field (makes gray oryginal image)
     * @return
     */
    private Mat faceDetector(Mat image) throws WrinkleFeaturesException {

        grayProcessedMat = image;
        if (processedMat.channels() != 1) {
            ImageProcessing.makeGrayImage(grayProcessedMat);
        }
        //Drawing a black rectangle to crop a face
        addDetectedObjects(new PropertiesLoader().getProperty("frontalFaceDetectorPath")
                , grayProcessedMat
                , FACES);
        return grayProcessedMat;

    }


    private void makeMatsAndSomeFieldsNotNull() {
        String nullJpg = new PropertiesLoader().getProperty("nullJpg");
        this.grayProcessedMat = Imgcodecs.imread(nullJpg);
        this.croppedToFace = Imgcodecs.imread(nullJpg);
        this.detectedEdges = Imgcodecs.imread(nullJpg);
        this.detectedNoseAndEyes = Imgcodecs.imread(nullJpg);
    }

    /**
     * Initialiaze this.croppedToFace field
     *
     * @throws WrinkleFeaturesException when no face is detected
     * @return
     */
    private Mat cropToFace(Mat grayImage) throws WrinkleFeaturesException {
        int detectedFaces = detectedObjects.get(FACES).size();

        if (detectedFaces > 1) {
            throw new WrinkleFeaturesException(this.path.getName() + ": To much faces detected.");
        } else {
            Rect detectedFace = detectedObjects.get(FACES).get(0);
            return ImageProcessing.generateCroppedMat(grayImage, detectedFace);
        }
    }

    /**
     * Detecting pair of eyes an Noses and initialize  this.detectedNoseAndEyes field
     * @return
     */
    private Mat detectPairOfEyesAndNose(Mat image) throws WrinkleFeaturesException {
        //Detect eyes pair
        detectEyesPair(image);
        //Detect nose
        detectNose(image);
        //Detect eyes, calculate center of eyes and distance between them
        detectEyes(image);

        //Detect mouth
//        tempCroppedImage = addDetectedObjects(mouthDetectorPath, tempCroppedImage, null,
//                false);
        Rect centerOfEyeOne = detectedObjects
                .get(DetectedObjectsEnum.EYEBALLS_CENTER).get(0);

        Rect centerOfEyeTwo = detectedObjects
                .get(DetectedObjectsEnum.EYEBALLS_CENTER).get(1);

        centerOfEyeOne.width += 10;
        centerOfEyeOne.height += 10;

        centerOfEyeTwo.width += 10;
        centerOfEyeTwo.height += 10;

        return ImageProcessing.drawARectanglesInMat(image
                , detectedObjects.get(EYE_PAIR).get(0)
                , detectedObjects.get(NOSE).get(0)
                , centerOfEyeOne
                , centerOfEyeTwo);
    }

    private void calculateWrinkleFeatures() throws WrinkleFeaturesException {

        Rect eyePair = detectedObjects.get(DetectedObjectsEnum.EYE_PAIR).get(0);
        Rect nose = detectedObjects.get(DetectedObjectsEnum.NOSE).get(0);

        Rect centerOfFirstEye = detectedObjects.get(DetectedObjectsEnum.EYEBALLS_CENTER).get(0);
        Rect centerOfSecondEye = detectedObjects.get(DetectedObjectsEnum.EYEBALLS_CENTER).get(1);

        Rect foreheadArea = getRectOfForeheadArea(centerOfFirstEye, centerOfSecondEye);
        Rect leftCheekArea = getRectOfLeftCheekArea(eyePair, nose);
        Rect rightCheekArea = getRectOfRightCheekArea(eyePair, nose);
        Rect leftEyeCornerArea = getRectOfLeftEyeCornerArea(leftCheekArea, eyePair);
        Rect rightEyeCornerArea = getRectOfRightEyeCornerArea(rightCheekArea, eyePair);

        detectedEdges = ImageProcessing.detectEdges(croppedToFace.clone());

        wrinkleAreas.add(foreheadArea);
        wrinkleAreas.add(leftCheekArea);
        wrinkleAreas.add(rightCheekArea);
        wrinkleAreas.add(leftEyeCornerArea);
        wrinkleAreas.add(rightEyeCornerArea);

        int whitePixelsInWrinkleAreas = 0;
        int areaOfAllWrinkleAreas = 0;

        for (Rect wrinkleArea : wrinkleAreas) {
            whitePixelsInWrinkleAreas += calculateWhitePixels(wrinkleArea);
            areaOfAllWrinkleAreas += calculateArea(wrinkleArea);
        }
        wrinkleFeatures = (float) whitePixelsInWrinkleAreas / areaOfAllWrinkleAreas;
    }

    private int calculateArea(Rect wrinkleArea) {
        return wrinkleArea.height * wrinkleArea.width;
    }

    private void detectEyes(Mat croppedImage) throws WrinkleFeaturesException {
        addDetectedObjects(new PropertiesLoader().getProperty("eyesDetectorPath"), croppedImage
                , DetectedObjectsEnum.EYES);

        List<Rect> listOfEyesRectangles = detectedObjects.get(DetectedObjectsEnum.EYES);

        Rect firstEyeRectangle = listOfEyesRectangles.get(0);
        Rect secondEyeRectangle = listOfEyesRectangles.get(1);

        List<Rect> eyeballsCenter = calculateCenterOfEye(firstEyeRectangle, secondEyeRectangle);
        detectedObjects.put(DetectedObjectsEnum.EYEBALLS_CENTER, eyeballsCenter);

    }

    private void detectEyesPair(Mat croppedImage) throws WrinkleFeaturesException {
        addDetectedObjects(new PropertiesLoader().getProperty("eyePairDetectorPath"), croppedImage
                , DetectedObjectsEnum.EYE_PAIR);

    }


    private void detectNose(Mat croppedImage) throws WrinkleFeaturesException {
        addDetectedObjects(new PropertiesLoader().getProperty("noseDetectorPath"), croppedImage
                , DetectedObjectsEnum.NOSE);

    }


    /**
     * Method changing to Mat which include detected objects.
     *
     * @param cascadeClassifierPath cascadeClassifierPath
     * @param image                 image
     * @param kindOfDetectedObject  kindOfDetectedObject
     * @throws WrinkleFeaturesException when invalid number of detected objects
     */

    private void addDetectedObjects(String cascadeClassifierPath
            , Mat image
            , DetectedObjectsEnum kindOfDetectedObject) throws WrinkleFeaturesException {

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

        //If detected pair of eyes or nose is not equal one then throw WrinkleFeaturesException
        if (((kindOfDetectedObject == DetectedObjectsEnum.EYE_PAIR)
                || (kindOfDetectedObject == DetectedObjectsEnum.NOSE))
                && sizeOfObjectDetectionList != 1) {
            throw new WrinkleFeaturesException(this.path.getName() + ": Invalid number of detected nose or eye pair.");
        }
        // If detected number of eyes and center of eyes is not equal to throw WrinkleFeaturesException
        if ((kindOfDetectedObject == DetectedObjectsEnum.EYES)
                && (sizeOfObjectDetectionList != 2)) {
            throw new WrinkleFeaturesException(this.path.getName() + ": Invalid number of detected eyes.");
        }

        if (kindOfDetectedObject == DetectedObjectsEnum.FACES && sizeOfObjectDetectionList == 0) {
            throw new WrinkleFeaturesException(this.path.getName() + ": No face detected.");
        }

        //Mapping kindOfObject e.g. nose to list of Rect which defining coordinates on image of detected objects.
        detectedObjects.put(kindOfDetectedObject, objectDetections.toList());
    }


    public void showWrinkleAreas(Scalar scalar) {
        Mat temp = detectedEdges.clone();
        ImageProcessing.drawARectangleInMat(temp, scalar, wrinkleAreas);
        Imshow.show(temp, "Wrinkle areas", WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    private float calculateWhitePixels(Rect wrinkleArea) {
        Mat croppedWrinkleArea = new Mat();
        Mat croppedMat = ImageProcessing.generateCroppedMat(detectedEdges, wrinkleArea);
        Core.extractChannel(croppedMat, croppedWrinkleArea, 0);
        return Core.countNonZero(croppedWrinkleArea);
    }

    private Rect getRectOfForeheadArea(Rect centerOfFirstEye, Rect centerOfSecondEye) {

        Point rightEyeCenter;
        int distanseBetweenEyes = DistanceCalculator.getDistanceFromRect(centerOfFirstEye, centerOfSecondEye);

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
