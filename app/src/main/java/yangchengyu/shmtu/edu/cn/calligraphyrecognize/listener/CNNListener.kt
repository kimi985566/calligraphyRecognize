package yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener


interface CNNListener {
    //深度学习完成后
    fun onTaskCompleted(result: Int)
}
