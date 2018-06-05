package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils

import android.graphics.Bitmap
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.OpenCVConstants

/**
 * Created by kimi9 on 2018/2/23.
 */

object CvImgProcessUtils {

    private val sSrc = Mat()
    private var sDst = Mat()
    private var sKernel: Mat? = null
    private var sStrElement: Mat? = null

    private var sWidth: Int = 0 //width
    private var sHeight: Int = 0  //height
    private var sRow: Int = 0 //Row--height
    private var sCol: Int = 0 //col--width
    private var sPixel = 0
    private var sIndex: Int = 0

    //ARGB values
    private var sA = 0
    private var sR = 0
    private var sG = 0
    private var sB = 0

    private var sPixels: IntArray? = null

    fun covert2Gray(bitmap: Bitmap): Bitmap {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)//convert Bitmap to mat
        Imgproc.cvtColor(sSrc, sDst, Imgproc.COLOR_BGRA2GRAY)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sSrc.release()
        sDst.release()
        return bitmap
    }

    fun invertMat(bitmap: Bitmap): Bitmap {

        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        //pixel operation
        //width of mat
        sWidth = sSrc.cols()
        //height of mat
        sHeight = sSrc.rows()
        val cnum = sSrc.channels()//Get channel

        val bgra = ByteArray(cnum)//ARGB(Bitmap)-->BGRA(mat)

        sRow = 0
        while (sRow < sHeight) {
            sCol = 0
            while (sCol < sWidth) {
                sSrc.get(sRow, sCol, bgra)
                sIndex = 0
                while (sIndex < cnum) {
                    bgra[sIndex] = (255 - bgra[sIndex] and 0xff).toByte()
                    sIndex++
                }
                sSrc.put(sRow, sCol, bgra)
                sCol++
            }
            sRow++
        }
        org.opencv.android.Utils.matToBitmap(sSrc, bitmap)
        sSrc.release()

        return bitmap
    }

    fun invertBitmap(bitmap: Bitmap): Bitmap {
        sWidth = bitmap.width
        sHeight = bitmap.height
        sPixels = IntArray(sWidth * sHeight)
        bitmap.getPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight)

        sIndex = 0
        sRow = 0
        while (sRow < sHeight) {
            sIndex = sRow * sWidth
            sCol = 0
            while (sCol < sWidth) {
                sPixel = sPixels!![sIndex]
                sA = sPixel shr 24 and 0xff
                sR = sPixel shr 16 and 0xff
                sG = sPixel shr 8 and 0xff
                sB = sPixel and 0xff

                sR = 255 - sR
                sG = 255 - sG
                sB = 255 - sB

                sPixel = sA and 0xff shl 24 or (sR and 0xff shl 16) or (sG and 0xff shl 8) or (sB and 0xff)

                sPixels!![sIndex] = sPixel

                sIndex++
                sCol++
            }
            sRow++
        }
        bitmap.setPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight)
        return bitmap
    }

    fun contrastRatioAdjust(bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        //This operation enables you to adjust CM in a float number.
        sSrc.convertTo(sSrc, CvType.CV_32F)
        val whiteImage = Mat(sSrc.size(), sSrc.type(), Scalar.all(1.25))//Contrast Ratio
        val bwImage = Mat(sSrc.size(), sSrc.type(), Scalar.all(30.0))//Brightness+30
        Core.multiply(whiteImage, sSrc, sSrc)
        Core.add(bwImage, sSrc, sSrc)
        sSrc.convertTo(sSrc, CvType.CV_8U)
        org.opencv.android.Utils.matToBitmap(sSrc, bitmap)
        //Don't forget to release.
        bwImage.release()
        whiteImage.release()
        sSrc.release()
    }

    fun matOperation(bitmap: Bitmap) {
        sDst = Mat(bitmap.height, bitmap.width, CvType.CV_8UC4, Scalar.all(127.0))
        //learn CV_8UC4,CV_8UC3,8UC1 and other figures
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sDst.release()
    }

    fun getRoi(bitmap: Bitmap): Bitmap {
        val roi = Rect(200, 150, 200, 300)
        val roiMap = Bitmap.createBitmap(roi.width, roi.height, Bitmap.Config.ARGB_8888)
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        val roiMat = sSrc.submat(roi)
        val roiDstMat = Mat()
        Imgproc.cvtColor(roiMat, roiDstMat, Imgproc.COLOR_BGRA2GRAY)
        org.opencv.android.Utils.matToBitmap(roiDstMat, roiMap)

        roiDstMat.release()
        roiMat.release()
        sSrc.release()
        return roiMap

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

    fun boxBlur(bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)//convert Bitmap to mat
        Imgproc.blur(sSrc, sDst, Size(15.0, 15.0), Point(-1.0, -1.0), 4)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sSrc.release()
        sDst.release()
    }

    fun gaussianBlur(bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)//convert Bitmap to mat
        Imgproc.GaussianBlur(sSrc, sDst, Size(5.0, 5.0), 0.0, 0.0, 4)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sSrc.release()
        sDst.release()
    }

    fun bilBlur(bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)//convert Bitmap to mat
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2BGR)
        Imgproc.bilateralFilter(sSrc, sDst, 15, 150.0, 15.0, 4)
        sKernel = Mat(3, 3, CvType.CV_16S)
        sKernel!!.put(0, 0, 0.0, -1.0, 0.0, -1.0, 5.0, -1.0, 0.0, -1.0, 0.0)
        Imgproc.filter2D(sDst, sDst, -1, sKernel!!, Point(-1.0, -1.0), 0.0, 4)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sKernel!!.release()
        sSrc.release()
        sDst.release()
    }

    fun customFilter(command: String, bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        sKernel = getCustomOperator(command)
        Imgproc.filter2D(sSrc, sDst, -1, sKernel!!, Point(-1.0, -1.0), 0.0, 4)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sKernel!!.release()
        sSrc.release()
        sDst.release()
    }

    private fun getCustomOperator(command: String): Mat {
        sKernel = Mat(3, 3, CvType.CV_32FC1)
        when (command) {
            OpenCVConstants.CUSTOM_BLUR_NAME -> sKernel!!.put(0, 0, 1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0,
                    1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0,
                    1.0 / 9.0, 1.0 / 9.0, 1.0 / 9.0)
            OpenCVConstants.CUSTOM_EDGE_NAME -> sKernel!!.put(0, 0, -1.0, -1.0, -1.0, -1.0, 8.0, -1.0, -1.0, -1.0, -1.0)
            OpenCVConstants.CUSTOM_SHARPEN_NAME -> sKernel!!.put(0, 0, -1.0, -1.0, -1.0, -1.0, 9.0, -1.0, -1.0, -1.0, -1.0)
        }
        return sKernel!!
    }

    fun erodeOrDilate(command: String, bitmap: Bitmap) {
        val isErode = OpenCVConstants.ERODE_NAME == command
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        val strElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                Size(3.0, 3.0), Point(-1.0, -1.0))
        if (isErode) {
            Imgproc.erode(sSrc, sDst, strElement, Point(-1.0, -1.0), 3)
        } else {
            Imgproc.dilate(sSrc, sDst, strElement, Point(-1.0, -1.0), 3)
        }
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        strElement.release()
        sSrc.release()
        sDst.release()
    }

    fun openOrClose(command: String, bitmap: Bitmap) {
        val isOpen = OpenCVConstants.OPEN_OPERATION_NAME == command
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        sStrElement = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,
                Size(3.0, 3.0), Point(-1.0, -1.0))
        if (isOpen) {
            Imgproc.morphologyEx(sSrc, sDst, Imgproc.MORPH_OPEN, sStrElement!!)
        } else {
            Imgproc.morphologyEx(sSrc, sDst, Imgproc.MORPH_CLOSE, sStrElement!!)
        }
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sStrElement!!.release()
        sSrc.release()
        sDst.release()
    }

    fun lineDetection(bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY)
        Imgproc.threshold(sSrc, sSrc, 0.0, 255.0,
                Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)
        sStrElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                Size(35.0, 1.0), Point(-1.0, -1.0))
        Imgproc.morphologyEx(sSrc, sDst, Imgproc.MORPH_OPEN, sStrElement!!)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sStrElement!!.release()
        sSrc.release()
        sDst.release()
    }

    fun thresholdImg(command: String, bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY)
        Imgproc.threshold(sSrc, sDst, 0.0, 255.0, getType(command))
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sSrc.release()
        sDst.release()
    }

    private fun getType(command: String): Int {
        return when (command) {
            OpenCVConstants.THRESH_BINARY_NAME -> Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
            OpenCVConstants.THRESH_BINARY_INV_NAME -> Imgproc.THRESH_BINARY_INV or Imgproc.THRESH_OTSU
            OpenCVConstants.THRESH_TRUNCAT_NAME -> Imgproc.THRESH_TRUNC or Imgproc.THRESH_OTSU
            OpenCVConstants.THRESH_ZERO_NAME -> Imgproc.THRESH_TOZERO or Imgproc.THRESH_OTSU
            else -> Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
        }
    }

    fun manualThresholdImg(t: Int, bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY)
        Imgproc.threshold(sSrc, sDst, t.toDouble(), 255.0, Imgproc.THRESH_BINARY)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sSrc.release()
        sDst.release()
    }

    fun adaptiveThresholdImg(command: String, bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY)
        Imgproc.adaptiveThreshold(sSrc, sDst, 255.0, getAdaptiveThreshold(command),
                Imgproc.THRESH_BINARY, 109, 0.0)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sSrc.release()
        sDst.release()
    }

    private fun getAdaptiveThreshold(command: String): Int {
        return when (command) {
            OpenCVConstants.ADAPTIVE_THRESH_MEAN_NAME -> Imgproc.ADAPTIVE_THRESH_MEAN_C
            OpenCVConstants.ADAPTIVE_THRESH_GAUSSIAN_NAME -> Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C
            else -> Imgproc.ADAPTIVE_THRESH_MEAN_C
        }
    }

    fun histogramEq(bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY)
        Imgproc.equalizeHist(sSrc, sDst)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sSrc.release()
        sDst.release()
    }

    fun gradientProcess(command: String, bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY)
        getGradientProcess(command)
        Core.convertScaleAbs(sDst, sDst)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sSrc.release()
        sDst.release()
    }

    private fun getGradientProcess(command: String) {
        if (OpenCVConstants.GRADIENT_SOBEL_X_NAME == command) {
            Imgproc.Sobel(sSrc, sDst, CvType.CV_16S, 1, 0)
        } else if (OpenCVConstants.GRADIENT_SOBEL_Y_NAME == command) {
            Imgproc.Sobel(sSrc, sDst, CvType.CV_16S, 0, 1)
        }
    }

    fun gradientXY(bitmap: Bitmap) {
        val xGrad = Mat()
        val yGrad = Mat()
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY)
        Imgproc.Sobel(sSrc, xGrad, CvType.CV_16S, 0, 1)
        Imgproc.Sobel(sSrc, yGrad, CvType.CV_16S, 0, 1)
        Core.convertScaleAbs(xGrad, xGrad)
        Core.convertScaleAbs(yGrad, yGrad)
        Core.addWeighted(xGrad, 0.5, yGrad, 0.5, 30.0, sDst)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        xGrad.release()
        yGrad.release()
        sSrc.release()
        sDst.release()
    }

    fun cannyProcess(value: Int, bitmap: Bitmap) {
        org.opencv.android.Utils.bitmapToMat(bitmap, sSrc)
        Imgproc.GaussianBlur(sSrc, sSrc, Size(3.0, 3.0), 0.0, 0.0, 4)
        Imgproc.cvtColor(sSrc, sSrc, Imgproc.COLOR_BGRA2GRAY)
        Imgproc.Canny(sSrc, sDst, value.toDouble(), (value * 2).toDouble(), 3, false)
        Core.convertScaleAbs(sDst, sDst)
        Imgproc.threshold(sDst, sDst, 0.0, 255.0, Imgproc.THRESH_BINARY_INV)
        org.opencv.android.Utils.matToBitmap(sDst, bitmap)
        sSrc.release()
        sDst.release()
    }
}
