package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.KNNDistance
import java.util.*

class CompareClass : Comparator<KNNDistance> {
    override fun compare(o1: KNNDistance, o2: KNNDistance): Int {
        return if (o1.distance > o2.distance) 20 else -1
    }
}
