package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils;

import android.graphics.Bitmap;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by kimi9 on 2018/2/13.
 */

public class ImageProcessUtils {

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    private static Mat sSrc = new Mat();
    private static Mat sDst = new Mat();
    private static Mat sKernel;
    private static Mat sStrElement;

    private static int sWidth; //width
    private static int sHeight;  //height
    private static int sRow; //Row--height
    private static int sCol; //col--width
    private static int sPixel = 0;
    private static int sIndex;

    //ARGB values
    private static int sA = 0;
    private static int sR = 0;
    private static int sG = 0;
    private static int sB = 0;

    private static int[] sPixels;

    public static void binImg(String command, Bitmap bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.threshold(sSrc, sDst, 0, 255,
                Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        org.opencv.android.Utils.matToBitmap(sDst, bitmap);
        sSrc.release();
        sDst.release();
    }

    public static void cannyProcess(Bitmap bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);
        Imgproc.GaussianBlur(sSrc, sSrc, new Size(3, 3), 0, 0, 4);
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.Canny(sSrc, sDst, 185, 185 * 2, 3, false);
        Core.convertScaleAbs(sDst, sDst);
        Imgproc.threshold(sDst, sDst, 0, 255, Imgproc.THRESH_BINARY_INV);
        org.opencv.android.Utils.matToBitmap(sDst, bitmap);
        sSrc.release();
        sDst.release();
    }

    public static void skeletonProcess(Bitmap bitmap, int value) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.threshold(sSrc, sSrc, 0, 255,
                Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        Mat ske = new Mat(sSrc.size(), CvType.CV_8UC1, new Scalar(0, 0, 0));
        Mat temp = new Mat(sSrc.size(), CvType.CV_8UC1);
        Mat erode = new Mat();

        sStrElement = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3, 3));

        boolean done;
        do {
            Imgproc.erode(sSrc, erode, sStrElement);
            Imgproc.dilate(erode, temp, sStrElement);
            Core.subtract(sSrc, temp, temp);
            Core.bitwise_or(ske, temp, ske);
            erode.copyTo(sSrc);
            done = (Core.countNonZero(sSrc) == 0);
        } while (!done);

        Imgproc.GaussianBlur(ske, ske, new Size(5, 5), 0, 0, 4);
        Imgproc.threshold(ske, ske, 0, 255,
                Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        org.opencv.android.Utils.matToBitmap(ske, bitmap);

        ske.release();
        temp.release();
        erode.release();
        sStrElement.release();
        sSrc.release();
    }

    public static Bitmap grayPicFromJNI(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] result = grayPic(pixels, width, height);
        Bitmap resultImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        resultImg.setPixels(result, 0, width, 0, 0, width, height);
        return resultImg;
    }

    public static native int[] grayPic(int[] Pixel, int w, int h);
}