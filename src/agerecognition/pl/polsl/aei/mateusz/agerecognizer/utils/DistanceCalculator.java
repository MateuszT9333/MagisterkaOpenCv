/**
 * @author Mateusz Trzeciak
 */
package pl.polsl.aei.mateusz.agerecognizer.utils;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class DistanceCalculator {

    public static int getDistanceFromPoint(Point c1, Point c2) {
        return (int) Math.sqrt(Math.pow((c1.x - c2.x), 2) + Math.pow((c1.y - c2.y), 2));
    }

    public static int getDistanceFromRect(Rect centerOfFirstEye, Rect centerOfSecondEye) {
        return (int) Math.sqrt(Math.pow((centerOfFirstEye.x - centerOfSecondEye.x), 2)
                + Math.pow((centerOfFirstEye.y - centerOfSecondEye.y), 2));
    }
}
