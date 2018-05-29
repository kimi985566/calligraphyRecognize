package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils

import android.os.Environment

import java.io.File
import java.text.DecimalFormat

object Config {

    //SD卡中文件的根目录
    val SD_CARD_DIR = Environment.getExternalStorageDirectory()
    val MODEL_DIR = SD_CARD_DIR.toString() + "/CalligraphyRecognize"

    //图片相关文件夹
    val ORIGINAL_IMG = "$MODEL_DIR/Capture"
    val CROP_IMG = "$MODEL_DIR/Crop"
    val BINARY_IMG = "$MODEL_DIR/Binary"
    val EDGE_IMG = "$MODEL_DIR/EDGE"
    val SKELETON_IMG = "$MODEL_DIR/Skeleton"
    val BASE64_IMG = "$MODEL_DIR/BASE64"

    //Caffe的相关路径
    val CAFFE_PATH = "$MODEL_DIR/Caffe"
    val modelProto = "$CAFFE_PATH/deploy.prototxt"
    val modelBinary = "$CAFFE_PATH/recognize.caffemodel"

    //OCR识别的API_KEY等参数
    val API_KEY = "VvaKsj8Gc0sGjnHNH7IBRmfF"
    val SECRET_KEY = "VIAPb8byovY4FrGsF7s1YSSlPcwW2vbO"

    //精选页面的网址
    val URLAddress = "http://119.28.224.38"//Tencent Cloud
    val picAddress = "$URLAddress/Calligraphy/getCalligraphyJSON.php"

    /**
     * double转String,保留小数点后两位
     *
     * @param num
     * @return
     */
    fun doubleToString(num: Double): String {
        //使用0.000不足位补0，#.###仅保留有效位
        return DecimalFormat("0.000").format(num)
    }

}
