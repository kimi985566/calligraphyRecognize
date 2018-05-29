package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean

class KNNNode {

    var id: Long = 0//类别
    var x: Double = 0.toDouble()//cenX
    var y: Double = 0.toDouble()//cenY
    var ratio: Double = 0.toDouble()//binAry
    var type: String? = null//类别

    constructor() {}

    constructor(id: Long, x: Double, y: Double, ratio: Double) {
        this.id = id
        this.x = x
        this.y = y
        this.ratio = ratio
    }

    constructor(id: Long, x: Double, y: Double, ratio: Double, type: String) {
        this.id = id
        this.x = x
        this.y = y
        this.ratio = ratio
        this.type = type
    }
}
