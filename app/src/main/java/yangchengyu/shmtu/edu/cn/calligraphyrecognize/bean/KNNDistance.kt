package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean

class KNNDistance {

    // 已知点id
    var id: Long = 0
    // 未知点id
    var nid: Long = 0
    // 二者之间的距离
    var disatance: Double = 0.toDouble()

    constructor() {}

    constructor(id: Long, nid: Long, disatance: Double) {
        this.id = id
        this.nid = nid
        this.disatance = disatance
    }
}
