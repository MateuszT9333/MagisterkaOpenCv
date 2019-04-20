package pl.polsl.aei.mateusz.agerecognizer.wrinklefeature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import pl.polsl.aei.mateusz.agerecognizer.utils.Imshow;

import javax.swing.*;
import java.util.List;

public class ImageProcessing {
    static final Logger log = LogManager.getLogger("main");

    public ImageProcessing() {
    }

    protected static Mat drawARectanglesInMat(Mat source, Rect... rect) {
        Mat temp = source.clone();
        for (Rect rectItem : rect) {
            Imgproc.rectangle(temp, new Point(rectItem.x, rectItem.y), new Point(rectItem.x + rectItem.width
                    , rectItem.y + rectItem.height), new Scalar(0, 0, 0));
        }
        return temp;
    }

    protected static Mat drawARectanglesInMat(Mat source, Scalar scalar, Rect... rect) {
        Mat temp = source.clone();
        for (Rect rectItem : rect) {
            Imgproc.rectangle(temp, new Point(rectItem.x, rectItem.y), new Point(rectItem.x + rectItem.width
                    , rectItem.y + rectItem.height), scalar);
        }
        return temp;
    }

    protected static Mat generateCroppedMat(Mat processedImage, MatOfRect objectDetections) {
        Rect rectangleToCrop = null;
        for (Rect rect : objectDetections.toArray()) {
            rectangleToCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
        }
        return new Mat(processedImage, rectangleToCrop);
    }

    /**
     * @param source
     * @return image with detected edges. Edges = wrinkles
     */
    protected static Mat detectEdges(Mat source) {
        Mat destination = source.clone();
        Imgproc.Canny(source, destination, 10, 100);
        return destination;
    }

    protected static Mat generateCroppedMat(Mat processedImage, Rect rect) {

        Rect rectangleOfFaceToCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
        return new Mat(processedImage, rectangleOfFaceToCrop);
    }

    protected static void makeGrayImage(Mat oryginal) {
        Mat originalCopy = oryginal.clone();
        Imgproc.cvtColor(originalCopy, oryginal, Imgproc.COLOR_BGR2GRAY);
    }

    protected static Mat drawARectangleInMat(Mat tempImage, MatOfRect objectDetections) {
        for (Rect rect : objectDetections.toArray()) {
            Imgproc.rectangle(tempImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width
                    , rect.y + rect.height), new Scalar(0, 0, 0));
        }
        return tempImage;
    }

    protected static void drawARectangleInMat(Mat tempImage, Scalar color, List<Rect> rect) {
        for (Rect rectItem : rect) {
            Imgproc.rectangle(tempImage, new Point(rectItem.x, rectItem.y)
                    , new Point(rectItem.x + rectItem.width
                            , rectItem.y + rectItem.height), color);
        }
    }

    public static void showImage(Mat matToShow, int delay, String label) {
        Imshow.show(matToShow, label, WindowConstants.DO_NOTHING_ON_CLOSE);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            log.catching(e);
            ;
        }
    }
}