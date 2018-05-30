package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean

import java.util.ArrayList

/**
 * Created by kimi9 on 2018/2/21.
 */

class OpenCVInfo(var id: Long, name: String, commend: String) : OpenCVConstants {

    var name: String? = name
    var commend: String? = commend

    companion object {

        val allList: List<OpenCVInfo>
            get() {

                val list = ArrayList<OpenCVInfo>()

                list.add(OpenCVInfo(1, OpenCVConstants.GRAY_TEST_NAME, OpenCVConstants.GREY_TEST_COM))
                list.add(OpenCVInfo(2, OpenCVConstants.MAT_PIXEL_INVERT_NAME, OpenCVConstants.MAT_PIXEL_INVERT_COM))
                list.add(OpenCVInfo(3, OpenCVConstants.BITMAP_PIXEL_INVERT_NAME, OpenCVConstants.BITMAP_PIXEL_INVERT_COM))
                list.add(OpenCVInfo(4, OpenCVConstants.CONTRAST_RATIO_BRIGHTNESS_NAME, OpenCVConstants.CONTRAST_RATIO_BRIGHTNESS_COM))
                list.add(OpenCVInfo(5, OpenCVConstants.IMAGE_CONTAINER_MAT_NAME, OpenCVConstants.IMAGE_CONTAINER_MAT_COM))
                list.add(OpenCVInfo(6, OpenCVConstants.GET_ROI_NAME, OpenCVConstants.GET_ROI_COM))
                list.add(OpenCVInfo(7, OpenCVConstants.BOX_BLUR_IMAGE_NAME, OpenCVConstants.BOX_BLUR_IMAGE_COM))
                list.add(OpenCVInfo(8, OpenCVConstants.GAUSSIAN_BLUR_IMAGE_NAME, OpenCVConstants.GAUSSIAN_BLUR_IMAGE_COM))
                list.add(OpenCVInfo(9, OpenCVConstants.BILATERAL_BLUR_IMAGE_NAME, OpenCVConstants.BILATERAL_BLUR_IMAGE_COM))
                list.add(OpenCVInfo(10, OpenCVConstants.CUSTOM_BLUR_NAME, OpenCVConstants.CUSTOM_BLUR_COM))
                list.add(OpenCVInfo(11, OpenCVConstants.CUSTOM_EDGE_NAME, OpenCVConstants.CUSTOM_EDGE_COM))
                list.add(OpenCVInfo(12, OpenCVConstants.CUSTOM_SHARPEN_NAME, OpenCVConstants.CUSTOM_SHARPEN_COM))
                list.add(OpenCVInfo(13, OpenCVConstants.ERODE_NAME, OpenCVConstants.ERODE_COM))
                list.add(OpenCVInfo(14, OpenCVConstants.DILATE_NAME, OpenCVConstants.DILATE_COM))
                list.add(OpenCVInfo(15, OpenCVConstants.OPEN_OPERATION_NAME, OpenCVConstants.OPEN_OPERATION_COM))
                list.add(OpenCVInfo(16, OpenCVConstants.CLOSE_OPERATION_NAME, OpenCVConstants.CLOSE_OPERATION_COM))
                list.add(OpenCVInfo(17, OpenCVConstants.MORPH_LINE_OPERATION_NAME, OpenCVConstants.MORPH_LINE_OPERATION_COM))
                list.add(OpenCVInfo(18, OpenCVConstants.THRESH_BINARY_NAME, OpenCVConstants.THRESH_BINARY_COM))
                list.add(OpenCVInfo(19, OpenCVConstants.THRESH_BINARY_INV_NAME, OpenCVConstants.THRESH_BINARY_INV_COM))
                list.add(OpenCVInfo(20, OpenCVConstants.THRESH_TRUNCAT_NAME, OpenCVConstants.THRESH_TRUNCAT_COM))
                list.add(OpenCVInfo(21, OpenCVConstants.THRESH_ZERO_NAME, OpenCVConstants.THRESH_ZERO_COM))
                list.add(OpenCVInfo(22, OpenCVConstants.MANUAL_THRESH_NAME, OpenCVConstants.MANUAL_THRESH_COM))
                list.add(OpenCVInfo(23, OpenCVConstants.ADAPTIVE_THRESH_MEAN_NAME, OpenCVConstants.ADAPTIVE_THRESH_MEAN_COM))
                list.add(OpenCVInfo(24, OpenCVConstants.HISTOGRAM_EQ_NAME, OpenCVConstants.HISTOGRAM_EQ_COM))
                list.add(OpenCVInfo(25, OpenCVConstants.GRADIENT_SOBEL_X_NAME, OpenCVConstants.GRADIENT_SOBEL_X_COM))
                list.add(OpenCVInfo(26, OpenCVConstants.GRADIENT_SOBEL_Y_NAME, OpenCVConstants.GRADIENT_SOBEL_Y_COM))
                list.add(OpenCVInfo(27, OpenCVConstants.GRADIENT_IMG_NAME, OpenCVConstants.GRADIENT_IMG_COM))
                list.add(OpenCVInfo(28, OpenCVConstants.CANNY_NAME, OpenCVConstants.CANNY_COM))

                return list
            }
    }
}
