package pl.polsl.aei.mateusz.agerecognizer.wrinklefeature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.objdetect.Objdetect;
import pl.polsl.aei.mateusz.agerecognizer.exceptions.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.utils.HogConfig;
import pl.polsl.aei.mateusz.agerecognizer.utils.Imshow;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.DetectedObjectsEnum.*;

/**
 * @author Mateusz Trzeciak
 * Class for detecting wrinkle features in face image.
 */
public class WrinkleFeatureCalculator {
    private static final Logger log = LogManager.getLogger("main");
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        log.info("OpenCv Core loaded");
    }

    private final Map<DetectedObjectsEnum, List<Rect>> detectedObjects = new HashMap<>();
    private final List<Rect> wrinkleAreas = new ArrayList<>();
    private final ImageProcessing imageProcessing = new ImageProcessing();
    private final boolean originalRectangles;
    private final HogConfig hogConfig;
    private File path;
    private Mat processedMat;
    private float wrinkleFeatures;
    private Mat grayProcessedMat = null;
    private Mat croppedToFace;
    private Mat detectedNoseAndEyes;
    private Mat faceWithDetectedEdges;


    /**
     * Detecting face in image and calculating wrinkle featurer
     *  @param path            path to processed file
     * @param isCroppedToFace If image is cropped to face. Especially for training algorithm
     * @param hogConfig
     */
    public WrinkleFeatureCalculator(File path, boolean isCroppedToFace, boolean originalRectangles, HogConfig hogConfig) throws WrinkleFeaturesException {
        this.originalRectangles = originalRectangles;
        this.hogConfig = hogConfig;
        this.path = path;
        processedMat = Imgcodecs.imread(path.getAbsolutePath());
        if (hogConfig.isHog()) {
            WrinkleFeatureCalculatorWithHog();
            return;
        }
        if (isCroppedToFace) {
            WrinkleFeatureCalculatorFromCroppedFace();
            return;
        }

        grayProcessedMat = faceDetector(processedMat);
        croppedToFace = cropToFace(grayProcessedMat);
        detectedNoseAndEyes = detectPairOfEyesAndNose(croppedToFace);
        wrinkleFeatures = calculateWrinkleFeatures();

    }

    private void WrinkleFeatureCalculatorWithHog() throws WrinkleFeaturesException {
        grayProcessedMat = faceDetector(processedMat);
        croppedToFace = cropToFace(grayProcessedMat);
        detectedNoseAndEyes = detectPairOfEyesAndNose(croppedToFace);
        wrinkleFeatures = calculateWrinkleFeaturesFromHog();
    }

    private void WrinkleFeatureCalculatorFromCroppedFace() throws WrinkleFeaturesException {
        ImageProcessing.makeGrayImage(processedMat);
        croppedToFace = processedMat;
        detectedNoseAndEyes = detectPairOfEyesAndNose(croppedToFace);
        calculateWrinkleFeatures();
    }

    /**
     * Adding faces rectangles to detectedObjectMap, initializes this.grayProcessedMat field (makes gray oryginal image)
     */
    private Mat faceDetector(Mat image) throws WrinkleFeaturesException {

        if (processedMat.channels() != 1) {
            ImageProcessing.makeGrayImage(image);
        }
        //Drawing a black rectangle to crop a face
        addDetectedObjects(propertiesLoader.getProperty("frontalFaceDetectorPath")
                , image
                , FACES);
        return image;

    }


    private Mat cropToFace(Mat grayImage) throws WrinkleFeaturesException {
        int detectedFaces = detectedObjects.get(FACES).size();

        if (detectedFaces > 1) {
            throw new WrinkleFeaturesException(this.path.toString() + ": To much faces detected.");
        } else {
            Rect detectedFace = detectedObjects.get(FACES).get(0);
            return ImageProcessing.generateCroppedMat(grayImage, detectedFace);
        }
    }

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

    private float calculateWrinkleFeaturesFromHog() throws WrinkleFeaturesException {
        createWrinkleAreas();
        float wrinkleFactor = 0;
        for (Rect wrinkleArea : wrinkleAreas) {
            wrinkleFactor += calculateSumOfHogDescriptors(wrinkleArea);
        }
        if (Float.isNaN(wrinkleFactor)) {
            throw new WrinkleFeaturesException("NaN");
        }
        return wrinkleFactor;
    }

    private float calculateSumOfHogDescriptors(Rect wrinkleArea) {
        double cellSizeInPx = this.hogConfig.getCellSizeInPx();
        Size cellSize = new Size(cellSizeInPx, cellSizeInPx);
        Size blockSize = new Size(cellSizeInPx, cellSizeInPx);
        Size blockStride = new Size(blockSize.width, blockSize.height);
        Size winSize = new Size(getWindowDimension(wrinkleArea.width), getWindowDimension(wrinkleArea.height));

        Mat wrinkleAreaMat = ImageProcessing.generateCroppedMat(croppedToFace.clone(), wrinkleArea);
        HOGDescriptor hog = new HOGDescriptor(
                winSize, //winSize
                blockSize, //blocksize
                blockStride, //blockStride,
                cellSize, //cellSize,
                this.hogConfig.getNbins()); //nbins

        MatOfFloat descriptors = new MatOfFloat();
        hog.compute(wrinkleAreaMat, descriptors);
        float wrinkleFactor = 0;
        for (float i : descriptors.toArray()) {
            wrinkleFactor += i;
        }
        int cellNumbers = (int) ((winSize.width / cellSizeInPx) * (winSize.height / cellSizeInPx));
        wrinkleFactor = wrinkleFactor / cellNumbers; //normalization
        return wrinkleFactor;
    }

    private float calculateWrinkleFeatures() throws WrinkleFeaturesException {

        createWrinkleAreas();

        float wrinkleFactor = 0;
        for (Rect wrinkleArea : wrinkleAreas) {
            float whitePixels = calculateWhitePixels(wrinkleArea);
            float allPixels = imageProcessing.calculateArea(wrinkleArea);
            wrinkleFactor += whitePixels / allPixels;
        }
        if (Float.isNaN(wrinkleFactor)) {
            throw new WrinkleFeaturesException("NaN");
        }
        return wrinkleFactor;

    }

    private double getWindowDimension(int originalDimension) {
        int dimension = originalDimension;
        while (dimension % this.hogConfig.getCellSizeInPx() != 0) {
            dimension--;
        }
        return dimension;
    }

    private void createWrinkleAreas() {
        Rect eyePair = detectedObjects.get(DetectedObjectsEnum.EYE_PAIR).get(0);
        Rect nose = detectedObjects.get(DetectedObjectsEnum.NOSE).get(0);

        Rect centerOfFirstEye = detectedObjects.get(DetectedObjectsEnum.EYEBALLS_CENTER).get(0);
        Rect centerOfSecondEye = detectedObjects.get(DetectedObjectsEnum.EYEBALLS_CENTER).get(1);

        Rect foreheadArea = ImageProcessing.getRectOfForeheadArea(centerOfFirstEye, centerOfSecondEye);
        Rect leftCheekArea = ImageProcessing.getRectOfLeftCheekArea(eyePair, nose);
        Rect rightCheekArea = ImageProcessing.getRectOfRightCheekArea(eyePair, nose);
        Rect leftEyeCornerArea = ImageProcessing.getRectOfLeftEyeCornerArea(leftCheekArea, eyePair);
        Rect rightEyeCornerArea = ImageProcessing.getRectOfRightEyeCornerArea(rightCheekArea, eyePair);
        if (!hogConfig.isHog()) {
            faceWithDetectedEdges = ImageProcessing.detectEdges(croppedToFace.clone());
        }

        wrinkleAreas.add(foreheadArea);
        wrinkleAreas.add(leftCheekArea);
        wrinkleAreas.add(rightCheekArea);
        wrinkleAreas.add(leftEyeCornerArea);
        wrinkleAreas.add(rightEyeCornerArea);

        if (this.originalRectangles) {
            Rect betweenEyesArea = ImageProcessing.getRectOfBetweenEyesArea(foreheadArea);
            wrinkleAreas.add(betweenEyesArea);
        }
    }


    private void makeMatsAndSomeFieldsNotNull() {
        String nullJpg = propertiesLoader.getProperty("nullJpg");
        this.grayProcessedMat = Imgcodecs.imread(nullJpg);
        this.croppedToFace = Imgcodecs.imread(nullJpg);
        this.faceWithDetectedEdges = Imgcodecs.imread(nullJpg);
        this.detectedNoseAndEyes = Imgcodecs.imread(nullJpg);
    }


    private void detectEyes(Mat croppedImage) throws WrinkleFeaturesException {
        addDetectedObjects(propertiesLoader.getProperty("eyesDetectorPath"), croppedImage
                , DetectedObjectsEnum.EYES);

        List<Rect> listOfEyesRectangles = detectedObjects.get(DetectedObjectsEnum.EYES);

        Rect firstEyeRectangle = listOfEyesRectangles.get(0);
        Rect secondEyeRectangle = listOfEyesRectangles.get(1);

        List<Rect> eyeballsCenter = ImageProcessing.calculateCenterOfEye(firstEyeRectangle, secondEyeRectangle);
        detectedObjects.put(DetectedObjectsEnum.EYEBALLS_CENTER, eyeballsCenter);

    }

    private void detectEyesPair(Mat croppedImage) throws WrinkleFeaturesException {
        addDetectedObjects(propertiesLoader.getProperty("eyePairDetectorPath"), croppedImage
                , DetectedObjectsEnum.EYE_PAIR);

    }


    private void detectNose(Mat croppedImage) throws WrinkleFeaturesException {
        addDetectedObjects(propertiesLoader.getProperty("noseDetectorPath"), croppedImage
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
            throw new WrinkleFeaturesException(this.path.toString() + ": Invalid number of detected nose or eye pair.");
        }
        // If detected number of eyes and center of eyes is not equal to throw WrinkleFeaturesException
        if ((kindOfDetectedObject == DetectedObjectsEnum.EYES)
                && (sizeOfObjectDetectionList != 2)) {
            throw new WrinkleFeaturesException(this.path.toString() + ": Invalid number of detected eyes.");
        }

        if (kindOfDetectedObject == DetectedObjectsEnum.FACES && sizeOfObjectDetectionList == 0) {
            throw new WrinkleFeaturesException(this.path.toString() + ": No face detected.");
        }

        //Mapping kindOfObject e.g. nose to list of Rect which defining coordinates on image of detected objects.
        detectedObjects.put(kindOfDetectedObject, objectDetections.toList());
    }


    public void showWrinkleAreas(Scalar scalar) {
        Mat temp = faceWithDetectedEdges.clone();
        ImageProcessing.drawARectangleInMat(temp, scalar, wrinkleAreas);
        Imshow.show(temp, "Wrinkle areas", WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    private float calculateWhitePixels(Rect wrinkleArea) {
        Mat croppedWrinkleArea = ImageProcessing.generateCroppedMat(croppedToFace.clone(), wrinkleArea);
        return Core.countNonZero(croppedWrinkleArea);
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

    public Mat getFaceWithDetectedEdges() {
        return faceWithDetectedEdges;
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
}
