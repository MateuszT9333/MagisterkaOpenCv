package pl.polsl.aei.mateusz.agerecognizer.wrinklefeature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import pl.polsl.aei.mateusz.agerecognizer.utils.Imshow;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ImageProcessing {
    private static final Logger log = LogManager.getLogger("main");

    static Rect getRectOfForeheadArea(Rect centerOfFirstEye, Rect centerOfSecondEye) {

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


    static Rect getRectOfRightCheekArea(Rect eyePair, Rect nose) {

        Point lowerLeftOfEyePair = new Point();
        Point centerOfLeftNoseSide = new Point();

        lowerLeftOfEyePair.x = eyePair.x;
        lowerLeftOfEyePair.y = eyePair.y + eyePair.height;

        centerOfLeftNoseSide.x = nose.x;
        centerOfLeftNoseSide.y = nose.y + nose.height / 2;

        return new Rect(lowerLeftOfEyePair, centerOfLeftNoseSide);
    }


    static Rect getRectOfLeftCheekArea(Rect eyePair, Rect nose) {

        Point lowerRightOfEyePair = new Point();
        Point centerOfRightNoseSide = new Point();

        lowerRightOfEyePair.x = eyePair.x + eyePair.width;
        lowerRightOfEyePair.y = eyePair.y + eyePair.height;

        centerOfRightNoseSide.x = nose.x + nose.width;
        centerOfRightNoseSide.y = nose.y + nose.height / 2;

        return new Rect(lowerRightOfEyePair, centerOfRightNoseSide);
    }


    static Rect getRectOfRightEyeCornerArea(Rect rightCheekArea, Rect eyePair) {

        Point startPoint = new Point();
        Point centerOfLeftSideOfRightCheekArea = new Point();

        startPoint.x = eyePair.x - rightCheekArea.width * (0.5);
        startPoint.y = eyePair.y + eyePair.height / 2;

        centerOfLeftSideOfRightCheekArea.x = rightCheekArea.x;
        centerOfLeftSideOfRightCheekArea.y = rightCheekArea.y + rightCheekArea.height / 2;

        return new Rect(startPoint, centerOfLeftSideOfRightCheekArea);
    }

    static Rect getRectOfLeftEyeCornerArea(Rect leftCheekArea, Rect eyePair) {

        Point endPoint = new Point();
        Point centerOfRightSideOfEyePair = new Point();

        centerOfRightSideOfEyePair.x = eyePair.x + eyePair.width;
        centerOfRightSideOfEyePair.y = eyePair.y + eyePair.height / 2;

        endPoint.x = leftCheekArea.x + leftCheekArea.width + leftCheekArea.width * (0.4);
        endPoint.y = leftCheekArea.y + leftCheekArea.height / 2;

        return new Rect(centerOfRightSideOfEyePair, endPoint);
    }

    static List<Rect> calculateCenterOfEye(Rect firstEye, Rect secondEye) {

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

    static Mat drawARectanglesInMat(Mat source, Rect... rect) {
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
        return new Mat(processedImage, Objects.requireNonNull(rectangleToCrop));
    }

    static Mat detectEdges(Mat source) {
        Mat destination = source.clone();
        Imgproc.Canny(source, destination, 10, 100);
        return destination;
    }

    static Mat generateCroppedMat(Mat processedImage, Rect rect) {

        Rect rectangleOfFaceToCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
        return new Mat(processedImage, rectangleOfFaceToCrop);
    }

    static void makeGrayImage(Mat oryginal) {
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

    static void drawARectangleInMat(Mat tempImage, Scalar color, List<Rect> rect) {
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
        }
    }

    int calculateArea(Rect wrinkleArea) {
        return wrinkleArea.height * wrinkleArea.width;
    }
}