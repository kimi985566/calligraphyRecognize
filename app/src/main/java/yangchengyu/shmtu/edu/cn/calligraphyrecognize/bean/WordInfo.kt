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
    var x_array: Int = 0
    var y_array: Int = 0
    var style: String? = null
    var pic_path: String? = null
    var zuanScore: Float = 0.toFloat()
    var liScore: Float = 0.toFloat()
    var kaiScore: Float = 0.toFloat()
    var caoScore: Float = 0.toFloat()

    constructor() {}

    constructor(id: Int, word: String, height: Int, width: Int, x_array: Int, y_array: Int, style: String, pic_path: String) {
        this.id = id
        this.word = word
        this.height = height
        this.width = width
        this.x_array = x_array
        this.y_array = y_array
        this.style = style
        this.pic_path = pic_path
    }
}
