package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean

/**
 * Created by kimi9 on 2018/2/22.
 */

interface OpenCVConstants {
    companion object {

        //灰度图形
        val GRAY_TEST_NAME = "灰度图形"
        val GREY_TEST_COM = "Gray Level Image"

        //Mat图像反色
        val MAT_PIXEL_INVERT_NAME = "Mat图像反色"
        val MAT_PIXEL_INVERT_COM = "Convert image in Mat"

        //Bitmap图像反色
        val BITMAP_PIXEL_INVERT_NAME = "Bitmap图像反色"
        val BITMAP_PIXEL_INVERT_COM = "Convert image in Bitmap"

        //调整亮度、对比度
        val CONTRAST_RATIO_BRIGHTNESS_NAME = "调整亮度、对比度"
        val CONTRAST_RATIO_BRIGHTNESS_COM = "Adjust contrast ratio and brightness"

        //创建mat空白图像
        val IMAGE_CONTAINER_MAT_NAME = "创建mat空白图像"
        val IMAGE_CONTAINER_MAT_COM = "Image Container: Mat, create a new image of mat"

        //子图操作
        val GET_ROI_NAME = "子图操作"
        val GET_ROI_COM = "Get sub image"

        //均值滤波
        val BOX_BLUR_IMAGE_NAME = "均值滤波"
        val BOX_BLUR_IMAGE_COM = "Blur learning: Box Blur"

        //高斯滤波
        val GAUSSIAN_BLUR_IMAGE_NAME = "高斯滤波"
        val GAUSSIAN_BLUR_IMAGE_COM = "Blur learning: Gaussian Blur"

        //双边滤波
        val BILATERAL_BLUR_IMAGE_NAME = "双边滤波"
        val BILATERAL_BLUR_IMAGE_COM = "Blur learning: Bilateral Blur"

        //自定义模糊
        val CUSTOM_BLUR_NAME = "自定义模糊"
        val CUSTOM_BLUR_COM = "Blur Operation"

        //自定义边缘提取
        val CUSTOM_EDGE_NAME = "自定义边缘提取"
        val CUSTOM_EDGE_COM = "Edge Operation"

        //自定义锐化处理
        val CUSTOM_SHARPEN_NAME = "自定义锐化处理"
        val CUSTOM_SHARPEN_COM = "Sharpen Operation"

        //腐蚀
        val ERODE_NAME = "腐蚀"
        val ERODE_COM = "Learning Erode"

        //膨胀
        val DILATE_NAME = "膨胀"
        val DILATE_COM = "Learning Dilate"

        //开操作
        val OPEN_OPERATION_NAME = "开操作"
        val OPEN_OPERATION_COM = "Learning Open Operation"

        //闭操作
        val CLOSE_OPERATION_NAME = "闭操作"
        val CLOSE_OPERATION_COM = "Learning Close Operation"

        //形态学直线检测
        val MORPH_LINE_OPERATION_NAME = "形态学直线检测"
        val MORPH_LINE_OPERATION_COM = "Line Detection"

        //阈值二值化
        val THRESH_BINARY_NAME = "阈值二值化"
        val THRESH_BINARY_COM = "Thresh Binary Operation"

        //阈值反二值化
        val THRESH_BINARY_INV_NAME = "阈值反二值化"
        val THRESH_BINARY_INV_COM = "Thresh Binary Inverse Operation"

        //阈值截断
        val THRESH_TRUNCAT_NAME = "阈值截断"
        val THRESH_TRUNCAT_COM = "Thresh Truncat Operation"

        //阈值取零
        val THRESH_ZERO_NAME = "阈值取零"
        val THRESH_ZERO_COM = "Thresh Zero Operation"

        //人工阈值
        val MANUAL_THRESH_NAME = "人工阈值"
        val MANUAL_THRESH_COM = "Manual Thresh Operation"

        //自适应阈值
        val ADAPTIVE_THRESH_MEAN_NAME = "自适应阈值"
        val ADAPTIVE_THRESH_MEAN_COM = "Adaptive Thresh Mean Operation"

        //自适应阈值
        val ADAPTIVE_THRESH_GAUSSIAN_NAME = "自适应阈值"
        val ADAPTIVE_THRESH_GAUSSIAN_COM = "Adaptive Thresh Gaussian Operation"

        //直方图均衡化
        val HISTOGRAM_EQ_NAME = "直方图均衡化"
        val HISTOGRAM_EQ_COM = "Histogram EQ Operation"

        //图像梯度：Sobel算子X方向
        val GRADIENT_SOBEL_X_NAME = "图像梯度：Sobel算子X方向"
        val GRADIENT_SOBEL_X_COM = "Gradient Sobel X"

        //图像梯度：Sobel算子Y方向
        val GRADIENT_SOBEL_Y_NAME = "图像梯度：Sobel算子Y方向"
        val GRADIENT_SOBEL_Y_COM = "Gradient Sobel Y"

        //图像梯度
        val GRADIENT_IMG_NAME = "图像梯度"
        val GRADIENT_IMG_COM = "Gradient image"

        //边缘提取
        val CANNY_NAME = "边缘提取"
        val CANNY_COM = "Canny Edge"
    }
}
