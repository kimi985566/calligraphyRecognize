package yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo;

/**
 * Created by kimi9 on 2018/3/20.
 */

public interface ItemTouchHelperListener {
    //数据交换
    void onItemMove(int fromPosition, int toPosition);

    //数据删除
    void onItemDissmiss(int position);

    //恢复item
    public void onItemRecover(int position, WordInfo wordInfo);

}
