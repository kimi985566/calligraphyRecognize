package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils;

import android.os.Environment;

import java.io.File;

public class Config {

    //SD卡中文件的根目录
    public static final File SD_CARD_DIR = Environment.getExternalStorageDirectory();
    public static final String MODEL_DIR = SD_CARD_DIR + "/CalligraphyRecognize";

    //图片相关文件夹
    public static final String ORIGINAL_IMG = MODEL_DIR + "/Capture";
    public static final String CROP_IMG = MODEL_DIR + "/Crop";
    public static final String BINARY_IMG = MODEL_DIR + "/Binary";
    public static final String EDGE_IMG = MODEL_DIR + "/EDGE";
    public static final String SKELETON_IMG = MODEL_DIR + "/Skeleton";
    public static final String BASE64_IMG = MODEL_DIR + "/BASE64";

    //Caffe的相关路径
    public static final String CAFFE_PATH = MODEL_DIR + "/Caffe";
    public static final String modelProto = CAFFE_PATH + "/deploy.prototxt";
    public static final String modelBinary = CAFFE_PATH + "/recognize.caffemodel";

    //OCR识别的API_KEY等参数
    public static final String API_KEY = "VvaKsj8Gc0sGjnHNH7IBRmfF";
    public static final String SECRET_KEY = "VIAPb8byovY4FrGsF7s1YSSlPcwW2vbO";

    //精选页面的网址
    public static final String URLAddress = "http://192.168.3.100";
    public static final String picAddress = URLAddress + "/Calligraphy/getCalligraphyJSON.php";

}
