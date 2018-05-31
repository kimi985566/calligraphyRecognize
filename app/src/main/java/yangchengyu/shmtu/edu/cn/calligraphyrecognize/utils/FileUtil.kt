package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils

import android.content.Context

import java.io.File

/**
 * Created by kimi9 on 2018/3/17.
 */

object FileUtil {
    //保存图片
    fun getSaveFile(context: Context): File {
        return File(context.filesDir, "pic.jpg")
    }
}
