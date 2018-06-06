/**
 * @author Mateusz Trzeciak
 * Paths to files in resources
 */
package pl.mateusz.agerecognition.utils;

public class Paths {
    public final static String resourcesPath = "src/resources/";
    public final static String testImagesPath = resourcesPath + "testImages/";
    public final static String trainingImagesPath = resourcesPath + "trainingImages/";
    public final static String xmlPath = resourcesPath + "xml/";
    public final static String lbpcascadeFrontalFaceDetectorPath = xmlPath + "lbpcascade_frontalface.xml";
    public final static String frontalFaceDetectorPath = xmlPath + "haarcascade_frontalface_default.xml";
    public final static String mouthDetectorPath = xmlPath + "haarcascade_mouth.xml";
    public final static String noseDetectorPath = xmlPath + "haarcascade_nose.xml";
    public final static String eyePairDetectorPath = xmlPath + "haarcascade_eye_pair_big.xml";
    public final static String eyesDetectorPath = xmlPath + "haarcascade_eyes.xml";
    public final static String ageToRecognizePath = resourcesPath + "ageToRecognize/";
    public final static String nullJpg = resourcesPath + "utils/null.jpg";
    public final static String pathToDump = "dump/";
    public static final String logPathPrefix = pathToDump + "log";
    public static final String ageToWrinkleJsonPath = pathToDump + "ageToWrinkleFeaturesJson";
    public static final String clusteredJsonPath = pathToDump + "clusteredDataJson";
    public static final String mergedAgeToWrinkleFeaturesPath = pathToDump + "mergedAgeToWrinkleFeatureJson";
}