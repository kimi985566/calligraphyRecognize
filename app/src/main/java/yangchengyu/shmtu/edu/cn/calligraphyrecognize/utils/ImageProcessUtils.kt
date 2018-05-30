package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils

import android.graphics.Bitmap
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

/**
 * Created by kimi9 on 2018/2/13.
 */

object ImageProcessUtils {

    //全局变量，mat容器
    private var sStrElement: Mat? = null

    //读取本地库文件，获取Native层的处理方法
    init {
        System.loadLibrary("native-lib")
        System.loadLibrary("opencv_java3")
    }

    //二值化图片
    fun binProcess(bitmap: Bitmap): Bitmap {
        val src = Mat()
        val dst = Mat()

        Thread(Runnable {
            getBinaryImage(bitmap, src, dst)
            org.opencv.android.Utils.matToBitmap(dst, bitmap)//转回bitmap
        }).start()

        src.release()//释放mat
        dst.release()
        return bitmap
    }

    /**
     * These things should be in Imgproc, but I couldn't find them.
     * So these figures are based on official website of OpenCV.
     *
     *
     * public static final int	BORDER_DEFAULT	4
     * public static final int	BORDER_ISOLATED	16
     * public static final int	BORDER_REFLECT	2
     * public static final int	BORDER_REFLECT_101	4
     * public static final int	BORDER_REFLECT101	4
     * public static final int	BORDER_REPLICATE	1
     * public static final int	BORDER_TRANSPARENT	5
     * public static final int	BORDER_WRAP	3
     */

    //边缘选取
    fun edgeProcess(bitmap: Bitmap): Bitmap {
        val src = Mat()
        val dst = Mat()

        Thread(Runnable {
            org.opencv.android.Utils.bitmapToMat(bitmap, src)
            Imgproc.GaussianBlur(src, src, Size(3.0, 3.0), 0.0, 0.0, 4)
            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2GRAY)
            Imgproc.Canny(src, dst, 185.0, (185 * 2).toDouble(), 3, false)
            Core.convertScaleAbs(dst, dst)
            Imgproc.threshold(dst, dst, 0.0, 255.0, Imgproc.THRESH_BINARY_INV)
            org.opencv.android.Utils.matToBitmap(dst, bitmap)
        }).start()

        src.release()
        dst.release()
        return bitmap
    }

    //Native层方法骨架化
    fun skeletonFromJNI(bitmap: Bitmap): Bitmap {
        val src = Mat()
        val dst = Mat()

        Thread(Runnable {
            getBinaryInvImage(bitmap, src, src)
            gThin(src.nativeObjAddr, dst.nativeObjAddr)
            Imgproc.threshold(dst, dst, 0.0, 255.0,
                    Imgproc.THRESH_BINARY_INV or Imgproc.THRESH_OTSU)
            org.opencv.android.Utils.matToBitmap(dst, bitmap)
        }).start()

        src.release()
        dst.release()
        return bitmap
    }

    fun imageGravityJava(bitmap: Bitmap, x: Int?, y: Int?) {
        val src = Mat()
        val dst = Mat()

        getBinaryInvImage(bitmap, src, dst)

        nativeGravity(dst.nativeObjAddr, x, y)

        src.release()//释放mat
        dst.release()
    }

    fun imageBinaryRatio(bitmap: Bitmap): Double {
        val src = Mat()
        val dst = Mat()

        getBinaryImage(bitmap, src, dst)

        val ratio = nativeBinaryRatio(dst.nativeObjAddr)

        src.release()//释放mat
        dst.release()
        return ratio
    }

    fun imageWHRatio(bitmap: Bitmap): Double {
        val src = Mat()
        org.opencv.android.Utils.bitmapToMat(bitmap, src)
        val width = src.width().toDouble()
        val height = src.height().toDouble()

        val ratio = width / height

        src.release()

        return ratio
    }

    //Java层的骨架化，备用方案
    fun skeletonProcess(bitmap: Bitmap): Bitmap {
        val src = Mat()

        getBinaryInvImage(bitmap, src, src)

        val ske = Mat(src.size(), CvType.CV_8UC1, Scalar(0.0, 0.0, 0.0))
        val temp = Mat(src.size(), CvType.CV_8UC1)
        val erode = Mat()

        sStrElement = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, Size(3.0, 3.0))

        var done: Boolean
        do {
            Imgproc.erode(src, erode, sStrElement!!)
            Imgproc.dilate(erode, temp, sStrElement!!)
            Core.subtract(src, temp, temp)
            Core.bitwise_or(ske, temp, ske)
            erode.copyTo(src)
            done = Core.countNonZero(src) == 0
        } while (!done)

        Imgproc.GaussianBlur(ske, ske, Size(5.0, 5.0), 0.0, 0.0, 4)
        Imgproc.threshold(ske, ske, 0.0, 255.0,
                Imgproc.THRESH_BINARY_INV or Imgproc.THRESH_OTSU)
        org.opencv.android.Utils.matToBitmap(ske, bitmap)

        ske.release()
        temp.release()
        erode.release()
        sStrElement!!.release()
        src.release()

        return bitmap
    }

    private fun getBinaryImage(bitmap: Bitmap, src: Mat, dst: Mat) {
        org.opencv.android.Utils.bitmapToMat(bitmap, src)//将bitmap转化为mat
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGRA2GRAY)//灰度化
        Imgproc.threshold(dst, dst, 0.0, 255.0,
                Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)//二值化
    }

    private fun getBinaryInvImage(bitmap: Bitmap, src: Mat, dst: Mat) {
        org.opencv.android.Utils.bitmapToMat(bitmap, src)//将bitmap转化为mat
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGRA2GRAY)//灰度化
        Imgproc.threshold(dst, dst, 0.0, 255.0,
                Imgproc.THRESH_BINARY_INV or Imgproc.THRESH_OTSU)//二值化
    }

    //Native方法：骨架化具体实现
    private external fun gThin(matSrcAddr: Long, matDstAddr: Long)

    //Native方法：获取图像重心
    private external fun nativeGravity(matSrcAddr: Long, x: Int?, y: Int?): Int

    //Native方法：统计黑白像素比
    private external fun nativeBinaryRatio(matSrcAddr: Long): Double
}