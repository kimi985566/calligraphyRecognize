package yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo

/**
 * Created by kimi9 on 2018/3/20.
 */

interface ItemTouchHelperListener {
    //数据交换
    fun onItemMove(fromPosition: Int, toPosition: Int)

    //数据删除
    fun onItemDelete(position: Int)

    //恢复item
    fun onItemRecover(position: Int, wordInfo: WordInfo)
}
