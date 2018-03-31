package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils;

import android.os.Environment;

import java.io.File;

public class Config {

    /**
     * 图片路径
     */

    public static final File SD_CARD_DIR = Environment.getExternalStorageDirectory();
    public static final String MODEL_DIR = SD_CARD_DIR + "/CalligraphyRecognize";

    public static final String ORIGINAL_IMG = MODEL_DIR + "/Capture";
    public static final String CROP_IMG = MODEL_DIR + "/Crop";
    public static final String BINARY_IMG = MODEL_DIR + "/Binary";
    public static final String EDGE_IMG = MODEL_DIR + "/EDGE";
    public static final String SKELETON_IMG = MODEL_DIR + "/Skeleton";
    public static final String BASE64_IMG = MODEL_DIR + "/BASE64";

    public static final String API_KEY = "VvaKsj8Gc0sGjnHNH7IBRmfF";
    public static final String SECRET_KEY = "VIAPb8byovY4FrGsF7s1YSSlPcwW2vbO";

    public static final String URLAddress = "http://192.168.3.100";
    public static final String picAddress = URLAddress + "/Calligraphy/getCalligraphyJSON.php";

}
