package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean

import java.io.Serializable

/**
 * Created by kimi9 on 2018/3/18.
 */

class WordInfo : Serializable {

    var id: Int = 0
    var word: String? = null
    var height: Int = 0
    var width: Int = 0
    var xArray: Int = 0
    var yArray: Int = 0
    var style: String? = null
    var pic_path: String? = null
    var zuanScore: Float = 0.toFloat()
    var liScore: Float = 0.toFloat()
    var kaiScore: Float = 0.toFloat()
    var caoScore: Float = 0.toFloat()

    constructor()

}
