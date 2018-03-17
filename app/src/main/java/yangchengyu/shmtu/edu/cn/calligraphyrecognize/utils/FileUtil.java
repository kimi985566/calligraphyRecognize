package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by kimi9 on 2018/3/17.
 */

public class FileUtil {

    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }
}
