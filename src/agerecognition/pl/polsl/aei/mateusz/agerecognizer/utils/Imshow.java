/*
 * Author: ATUL
 * Thanks to Daniel Baggio , Jan Monterrubio and sutr90 for improvements
 * This code can be used as an alternative to imshow of OpenCV for JAVA-OpenCv
 * Make sure OpenCV Java is in your Build Path
 * Usage :
 * -------
 * Imshow ims = new Imshow("Title");
 * ims.showImage(Mat image);
 * Check Example for usage with Webcam Live Video Feed
 */
package pl.polsl.aei.mateusz.agerecognizer.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;


public class Imshow {
    private static final Logger log = LogManager.getLogger("main");

    private final JFrame Window;
    private final ImageIcon image;
    private final JLabel label;
    private final Boolean SizeCustom;
    private int Height, Width;

    public Imshow(String title) {
        Window = new JFrame();
        image = new ImageIcon();
        label = new JLabel();
        label.setIcon(image);
        Window.getContentPane().add(label);
        Window.setResizable(false);
        Window.setTitle(title);
        SizeCustom = false;
        setCloseOption();
    }

    private Imshow(String title, int height, int width) {
        SizeCustom = true;
        Height = height;
        Width = width;

        Window = new JFrame();
        image = new ImageIcon();
        label = new JLabel();
        label.setIcon(image);
        Window.getContentPane().add(label);
        Window.setResizable(false);
        Window.setTitle(title);
        setCloseOption();

    }

    /**
     * Displays the given {@link Mat} in a new instance of {@link Imshow}
     *
     * @param mat the {@link Mat} to display
     */
    public static void show(Mat mat) {
        show(mat, new Dimension(mat.rows(), mat.cols()), "", false,
                WindowConstants.EXIT_ON_CLOSE);
    }

    // CREDITS TO DANIEL: http://danielbaggio.blogspot.com.br/ for the improved
    // version !

    /**
     * Displays the given {@link Mat} in a new instance of {@link Imshow} with
     * the given title as the title for the window
     *
     * @param mat        the {@link Mat} to display
     * @param frameTitle the title for the frame
     */
    public static void show(Mat mat, String frameTitle, int closeOp) {
        show(mat, new Dimension(mat.rows(), mat.cols()), frameTitle, false,
                closeOp);
    }

    /**
     * Displays the given {@link Mat} in a new instance of {@link Imshow} with
     * the given title as the title for the window and determines whether the
     * frame is resizable or not
     *
     * @param mat        the {@link Mat} to display
     * @param frameTitle the title for the frame
     * @param resizable  whether the frame should be resizable or not
     */
    public static void show(Mat mat, String frameTitle, boolean resizable) {
        show(mat, new Dimension(mat.rows(), mat.cols()), frameTitle, resizable,
                WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Displays the given {@link Mat} in a new instance of {@link Imshow} with a
     * set size
     *
     * @param mat       the {@link Mat} to display
     * @param frameSize the size for the frame
     */
    public static void show(Mat mat, Dimension frameSize) {
        show(mat, frameSize, "", false, WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Displays the given {@link Mat} in a new instance of {@link Imshow} with a
     * set size and given title
     *
     * @param mat        the {@link Mat} to display
     * @param frameSize  the size for the frame
     * @param frameTitle the title for the frame
     */
    public static void show(Mat mat, Dimension frameSize, String frameTitle) {
        show(mat, frameSize, frameTitle, false, WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Displays the given {@link Mat} in a new instance of {@link Imshow} with a
     * set size and given title and whether it is resizable or not
     *
     * @param mat        the {@link Mat} to display
     * @param frameSize  the size for the frame
     * @param frameTitle the title for the frame
     */
    public static void show(Mat mat, Dimension frameSize, String frameTitle,
                            boolean resizable) {
        show(mat, frameSize, frameTitle, resizable,
                WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Displays the given {@link Mat} in a new instance of {@link Imshow} with a
     * set size and given title and whether it is resizable or not, and with the
     * close operation set
     *
     * @param mat            the {@link Mat} to display
     * @param frameSize      the size for the frame
     * @param frameTitle     the title for the frame
     * @param resizable      wether the frame is resizable or not
     * @param closeOperation the constant for the default close operation of the frame
     */
    private static void show(Mat mat, Dimension frameSize, String frameTitle,
                             boolean resizable, int closeOperation) {
        Imshow frame = new Imshow(frameTitle, frameSize.height, frameSize.width);
        frame.setResizable(resizable);

        /*
         * This is a bad way to access the window, but due to legacy stuff I
         * won't change the access patterns
         */
        frame.Window.setDefaultCloseOperation(closeOperation);
        frame.showImage(mat);
    }

    private void showImage(Mat img) {
        if (SizeCustom) {
            Imgproc.resize(img, img, new Size(Height, Width));
        }
        BufferedImage bufImage = null;
        try {
            bufImage = toBufferedImage(img);
            image.setImage(bufImage);
            Window.pack();
            label.updateUI();
            Window.setVisible(true);
        } catch (Exception e) {
            log.catching(e);
        }
    }

    private BufferedImage toBufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster()
                .getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;

    }

    private void setCloseOption() {

        Window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Sets whether this window should be resizable or not, by default it is not
     * resizable
     *
     * @param resizable <code>true</code> if the window should be resizable,
     *                  <code>false</code> otherwise
     */
    private void setResizable(boolean resizable) {
        Window.setResizable(resizable);
    }

}