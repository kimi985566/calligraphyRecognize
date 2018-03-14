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

    //读取本地库文件，获取Native层的处理方法
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    //全局变量，mat容器
    private static Mat sSrc = new Mat();
    private static Mat sDst = new Mat();
    private static Mat sStrElement;

    //二值化图片
    public static Bitmap binProcess(Bitmap bitmap) {
        Bitmap result = bitmap;
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);//将bitmap转化为mat
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY);//灰度化
        Imgproc.threshold(sSrc, sDst, 0, 255,
                Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);//二值化
        org.opencv.android.Utils.matToBitmap(sDst, result);//转回bitmap
        sSrc.release();//释放mat
        sDst.release();
        return result;
    }

    /**
     * These things should be in Imgproc, but I couldn't find them.
     * So these figures are based on official website of OpenCV.
     * <p>
     * public static final int	BORDER_DEFAULT	4
     * public static final int	BORDER_ISOLATED	16
     * public static final int	BORDER_REFLECT	2
     * public static final int	BORDER_REFLECT_101	4
     * public static final int	BORDER_REFLECT101	4
     * public static final int	BORDER_REPLICATE	1
     * public static final int	BORDER_TRANSPARENT	5
     * public static final int	BORDER_WRAP	3
     **/

    //边缘选取
    public static Bitmap edgeProcess(Bitmap bitmap) {
        Bitmap result = bitmap;
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);
        Imgproc.GaussianBlur(sSrc, sSrc, new Size(3, 3), 0, 0, 4);
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.Canny(sSrc, sDst, 185, 185 * 2, 3, false);
        Core.convertScaleAbs(sDst, sDst);
        Imgproc.threshold(sDst, sDst, 0, 255, Imgproc.THRESH_BINARY_INV);
        org.opencv.android.Utils.matToBitmap(sDst, result);
        sSrc.release();
        sDst.release();
        return result;
    }

    //Native层方法骨架化
    public static Bitmap skeletonFromJNI(Bitmap bitmap) {
        Bitmap result = bitmap;
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc);
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.threshold(sSrc, sSrc, 0, 255,
                Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        gThin(sSrc.getNativeObjAddr(), sDst.getNativeObjAddr());
        Imgproc.threshold(sDst, sDst, 0, 255,
                Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        org.opencv.android.Utils.matToBitmap(sDst, result);
        sSrc.release();
        sDst.release();
        return result;
    }

    //Java层的骨架化，备用方案
    public static Bitmap skeletonProcess(Bitmap bitmap) {
        Bitmap result = bitmap;
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
        org.opencv.android.Utils.matToBitmap(ske, result);

        ske.release();
        temp.release();
        erode.release();
        sStrElement.release();
        sSrc.release();
        return result;
    }

    public static Bitmap getStrokes(Bitmap bitmap) {
        Bitmap result = bitmap;
        Bitmap binBitmap = binProcess(bitmap);//二值化图像
        Bitmap edgeBitmap = edgeProcess(bitmap);//书法字边缘图像
        Bitmap skeletonBitmap = skeletonFromJNI(bitmap);//骨架化图像
        //TODO: pick strokes from Bitmap

        return result;
    }

    //Native方法：骨架化具体实现
    public static native void gThin(long matSrcAddr, long matDstAddr);

}