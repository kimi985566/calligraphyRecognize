package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo;

/**
 * Created by kimi9 on 2018/3/20.
 */

public interface ItemTouchHelperAdapter {
    //数据交换
    void onItemMove(int fromPosition, int toPosition);

    //数据删除
    void onItemDelete(int position);

    //恢复item
    void onItemRecover(int position, WordInfo wordInfo);

}
