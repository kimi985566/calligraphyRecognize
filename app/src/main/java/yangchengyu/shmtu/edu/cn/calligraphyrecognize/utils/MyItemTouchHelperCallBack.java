package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.ItemTouchHelperListener;

/**
 * Created by kimi9 on 2018/3/20.
 */

public class MyItemTouchHelperCallBack extends ItemTouchHelper.Callback {

    private ItemTouchHelperListener mItemTouchHelperAdapter;
    private ArrayList<WordInfo> mWordInfos;

    public MyItemTouchHelperCallBack() {
    }

    public MyItemTouchHelperCallBack(ItemTouchHelperListener itemTouchHelperAdapter, ArrayList<WordInfo> wordInfos) {
        mItemTouchHelperAdapter = itemTouchHelperAdapter;
        mWordInfos = wordInfos;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //允许上下拖动
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //允许从右向左滑动
        int swipeFlags = ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mItemTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
