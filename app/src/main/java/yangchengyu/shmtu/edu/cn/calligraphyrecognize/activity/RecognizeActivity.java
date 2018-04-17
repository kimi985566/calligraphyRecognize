package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;

import java.util.ArrayList;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.DB.WordDBhelper;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainRecognizeItemAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.ItemTouchHelperListener;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.FileUtil;

public class RecognizeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        OnCardViewItemListener {

    public static final String WORD = "word";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String X_ARRAY = "x_array";
    public static final String Y_ARRAY = "y_array";
    public static final String PIC_PATH = "path";
    public static final String STYLE = "style";
    public static final String ZUAN = "zuan";
    public static final String LI = "li";
    public static final String KAI = "KAI";
    public static final String CAO = "CAO";
    public static final String FROMWHERE = "fromwhere";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private WordDBhelper mWordDBhelper;
    private WordInfo mWordInfoTemp;
    private MainRecognizeItemAdapter mMainCardViewItemAdapter;
    private ArrayList<WordInfo> mWordInfo = new ArrayList<>();
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);
        initUI();
    }

    private void initUI() {
        mToolbar = findViewById(R.id.toolbar_sub);
        mToolbar.setTitle("识别记录");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mSwipeRefreshLayout = findViewById(R.id.swipe_main_container);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.color_tab_1, R.color.color_tab_2,
                R.color.color_tab_3, R.color.color_tab_4);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = findViewById(R.id.fragment_main_recycle_view);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getDBData();

        mMainCardViewItemAdapter = new MainRecognizeItemAdapter(this, mWordInfo);
        mMainCardViewItemAdapter.setOnCardViewItemListener(this);
        ItemTouchHelper.Callback callback = new myItemTouchHelperCallBack(mMainCardViewItemAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mMainCardViewItemAdapter);
    }

    private void getDBData() {
        mWordDBhelper = new WordDBhelper(this);
        mWordInfo = mWordDBhelper.getALLWord();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWordInfo = mWordDBhelper.getALLWord();
                mMainCardViewItemAdapter.updateData(mWordInfo);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onCardViewItemClick(final View view, final int position) {
        Toast.makeText(this, "加载中", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                WordInfo wordInfo = mWordInfo.get(position);
                Intent intent = new Intent(view.getContext(), ResultActivity.class);
                intent.putExtra(WORD, wordInfo.getWord());
                intent.putExtra(WIDTH, wordInfo.getWidth());
                intent.putExtra(HEIGHT, wordInfo.getHeight());
                intent.putExtra(X_ARRAY, wordInfo.getX_array());
                intent.putExtra(Y_ARRAY, wordInfo.getY_array());
                intent.putExtra(PIC_PATH, wordInfo.getPic_path());
                intent.putExtra(STYLE, wordInfo.getStyle());
                intent.putExtra(ZUAN, wordInfo.getZuanScore());
                intent.putExtra(LI, wordInfo.getLiScore());
                intent.putExtra(KAI, wordInfo.getKaiScore());
                intent.putExtra(CAO, wordInfo.getCaoScore());
                intent.putExtra(FROMWHERE, "main");
                startActivity(intent);
            }
        }).start();
    }

    class myItemTouchHelperCallBack extends ItemTouchHelper.Callback {

        private int mPosition;
        private ItemTouchHelperListener mItemTouchHelperListener;

        public myItemTouchHelperCallBack(ItemTouchHelperListener itemTouchHelperListener) {
            this.mItemTouchHelperListener = itemTouchHelperListener;
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
            //onItemMove接口里的方法
            mItemTouchHelperListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //onItemDelete接口里的方法
            mPosition = viewHolder.getAdapterPosition();
            mWordInfoTemp = mWordInfo.get(mPosition);
            mItemTouchHelperListener.onItemDelete(mPosition);

            Snackbar snackbar = Snackbar.make(mRecyclerView, "是否撤销删除", Snackbar.LENGTH_LONG);
            snackbar.setAction("Yes", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemTouchHelperListener.onItemRecover(mPosition, mWordInfoTemp);
                }
            });
            snackbar.addCallback(new MySnackBarCallBack());
            snackbar.show();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                //滑动时改变Item的透明度，以实现滑动过程中实现渐变效果
                final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

        }

        @Override
        public boolean isLongPressDragEnabled() {
            //该方法返回值为true时，表示支持长按ItemView拖动
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            //该方法返回true时，表示如果用户触摸并且左滑了view，那么可以执行滑动删除操作，就是可以调用onSwiped()方法
            return true;
        }
    }

    private class MySnackBarCallBack extends Snackbar.Callback {
        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            if (event == DISMISS_EVENT_SWIPE
                    || event == DISMISS_EVENT_TIMEOUT
                    || event == DISMISS_EVENT_CONSECUTIVE) {
                FileUtils.deleteFile(mWordInfoTemp.getPic_path());
                mWordDBhelper.deleteWord(mWordInfoTemp);
            }
        }
    }
}
