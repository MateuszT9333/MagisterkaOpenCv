import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.HOGDescriptor;
import pl.polsl.aei.mateusz.agerecognizer.exceptions.WrinkleFeaturesException;
import pl.polsl.aei.mateusz.agerecognizer.utils.HogConfig;
import pl.polsl.aei.mateusz.agerecognizer.utils.Imshow;
import pl.polsl.aei.mateusz.agerecognizer.utils.PropertiesLoader;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.ImageProcessing;
import pl.polsl.aei.mateusz.agerecognizer.wrinklefeature.WrinkleFeatureCalculator;

import javax.swing.*;
import java.io.File;
import java.util.Scanner;

/**
 * @author Mateusz Trzeciak
 * Some tests...
 */
public class WrinkleFeatureCalculatorTest {
    private static final PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    private final static File TEST = new File(propertiesLoader.getProperty("test"));
    private final static File MOJATWARZ = new File(propertiesLoader.getProperty("mojaTwarz"));
    private final static File MAT = new File(propertiesLoader.getProperty("matImage"));
    private static final Logger log = LogManager.getLogger("main");

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final File processedImage = MOJATWARZ;
    boolean originalRectangles = false;
    final static HogConfig hogConfig = new HogConfig(true, 8, 9);

    private static void exportImgFeatures(Mat grayMat) {

//        int rows = 10;
//        int cols = 10;
//        double[][] data = new double[10][10];
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 10; j++) {
//                if (i == 0 && j == 0) {
//                    data[i][j] = 128;
//                }
//                if (i == 0 && j == 1) {
//                    data[i][j] = 255;
//                }
//                if (i == 0 && j == 2) {
//                    data[i][j] = 64;
//                }
//            }
//        }
//
//        Mat mat_ = new Mat(rows, cols, CvType.CV_8U);
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//
//                mat_.put(i, j, data[i][j]);
//            }
//        }
//        Imgcodecs.imwrite(MAT.getAbsolutePath(), mat_);
        Size matInPx = new Size(4, 4);

        Size cellInPx = new Size(2, 2);
        Size blockStride = cellInPx;

        HOGDescriptor hog = new HOGDescriptor(
                new Size(4, 4), //winSize
                new Size(2, 2), //blocksize
                new Size(1, 1), //blockStride,
                new Size(1, 1), //cellSize,
                4); //nbins

//        MatOfFloat descriptors = new MatOfFloat();
        Mat grad = new Mat();
        Mat angle = new Mat();
        hog.computeGradient(grayMat, grad, angle);
        MatOfFloat descriptors = new MatOfFloat();
        hog.compute(grayMat, descriptors);
        Imgcodecs.imwrite(MAT.getAbsolutePath(), descriptors);

//        float[] descArr = descriptors.toArray();
//        double retArr[] = new double[descArr.length];
//        for (int i = 0; i < descArr.length; i++) {
//            retArr[i] = descArr[i];
//        }
//        return retArr;

        System.out.println("");
    }

    @Test
    public void faceDetectorTest() {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
        try {
            wrinkleFeatureCalculator = new WrinkleFeatureCalculator(processedImage, false, originalRectangles, hogConfig);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeatureCalculator != null;
        Imshow.show(wrinkleFeatureCalculator.getProcessedMat(), "Oryginal", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeatureCalculator.getCroppedToFace(), "Cropped face", WindowConstants.DO_NOTHING_ON_CLOSE);
        pause();
    }

    private void pause() {
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    @Test
    public void detectPairOfEyesNoseTest() {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
        try {
            wrinkleFeatureCalculator = new WrinkleFeatureCalculator(processedImage, false, originalRectangles, hogConfig);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeatureCalculator != null;
        Imshow.show(wrinkleFeatureCalculator.getDetectedNoseAndEyes(), "Detected nose and eyes", WindowConstants.DO_NOTHING_ON_CLOSE);
        pause();
    }

    @Test
    public void detectEdgesTest() {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
        try {
            wrinkleFeatureCalculator = new WrinkleFeatureCalculator(processedImage, false, originalRectangles, hogConfig);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeatureCalculator != null;
        Imshow.show(wrinkleFeatureCalculator.getFaceWithDetectedEdges(), "Image edges", WindowConstants.DO_NOTHING_ON_CLOSE);
        pause();
    }

    void showImage(String imagePath, String frameTitle) {
        Mat image = Imgcodecs.imread(imagePath);
        Imshow.show(image, frameTitle, WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    @Test
    public void calculateWrinkleFeaturesTest() {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
        try {
            wrinkleFeatureCalculator = new WrinkleFeatureCalculator(processedImage, false, originalRectangles, hogConfig);
        } catch (WrinkleFeaturesException e) {
            log.catching(e);
        }
        assert wrinkleFeatureCalculator != null;
//        wrinkleFeatureCalculator.showWrinkleAreas(new Scalar(0, 0, 0));
        log.info("Wrinkle features " + wrinkleFeatureCalculator.getWrinkleFeatures());
        pause();
    }

    @Test
    public void showGeneratedImages() {
        WrinkleFeatureCalculator wrinkleFeatureCalculator = null;
        //fileExists(processedImage);
        try {
            wrinkleFeatureCalculator = new WrinkleFeatureCalculator(processedImage, false, originalRectangles, hogConfig);
        } catch (Throwable e) {
            log.catching(e);
        }
        assert wrinkleFeatureCalculator != null;
        Imshow.show(wrinkleFeatureCalculator.getProcessedMat(), "Oryginal", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeatureCalculator.getCroppedToFace(), "Cropped face", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeatureCalculator.getDetectedNoseAndEyes(), "Detected nose and eyes", WindowConstants.DO_NOTHING_ON_CLOSE);
        Imshow.show(wrinkleFeatureCalculator.getFaceWithDetectedEdges(), "Detected edges", WindowConstants.DO_NOTHING_ON_CLOSE);
        wrinkleFeatureCalculator.showWrinkleAreas(new Scalar(255, 0, 0));

        log.info("Wrinkle features " + wrinkleFeatureCalculator.getWrinkleFeatures());
        log.info("Detected objects " + wrinkleFeatureCalculator.getDetectedObjects());
        log.info("Calculated wrinkle areas " + wrinkleFeatureCalculator.getWrinkleAreas());
        pause();
    }

    @Test
    public void hogTest() {
        //TODO test
        Mat grayMat = Imgcodecs.imread(TEST.getAbsolutePath());
        ImageProcessing.makeGrayImage(grayMat);
        exportImgFeatures(grayMat);
    }

//    @Test
//    public void fileExists(File processedImage) {
//        File file = processedImage;
//        assertTrue(file.length() > 0);
//    }

}