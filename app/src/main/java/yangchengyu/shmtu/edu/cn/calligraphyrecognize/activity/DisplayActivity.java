package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SnackbarUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainSelectAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.ImageInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.Config;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.JSONUtils;

public class DisplayActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnCardViewItemListener {

    private ArrayList<ImageInfo> mImageInfos = new ArrayList<>();
    private MainSelectAdapter mMainSelectAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout_select;
    private RecyclerView mRecyclerView_select;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        initUI();
    }

    private void initUI() {
        mToolbar = findViewById(R.id.toolbar_sub);
        mToolbar.setTitle("精选书法");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mSwipeRefreshLayout_select = findViewById(R.id.fragment_select_swipe_refresh_layout);
        mRecyclerView_select = findViewById(R.id.fragment_select_recycle_view);
        mSwipeRefreshLayout_select.setColorSchemeResources(
                R.color.color_tab_1, R.color.color_tab_2,
                R.color.color_tab_3, R.color.color_tab_4);

        mSwipeRefreshLayout_select.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout_select.setRefreshing(true);
                JSONUtils.getImage(Config.picAddress, getImageHandler);
                mSwipeRefreshLayout_select.setRefreshing(false);
                SnackbarUtils.with(mRecyclerView_select)
                        .setMessage("当前网络：" + String.valueOf(NetworkUtils.getNetworkType()))
                        .showSuccess();
            }
        });

        mSwipeRefreshLayout_select.setOnRefreshListener(this);
        mRecyclerView_select.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView_select.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false);
        mRecyclerView_select.setLayoutManager(gridLayoutManager);

        mMainSelectAdapter = new MainSelectAdapter(this, mImageInfos);
        mMainSelectAdapter.setOnCardViewItemListener(this);
        mRecyclerView_select.setAdapter(mMainSelectAdapter);
    }

    @SuppressLint("HandlerLeak")
    private Handler getImageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String JsonData = (String) msg.obj;
            try {
                mImageInfos.clear();
                JSONArray jsonArray = new JSONArray(JsonData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int page_id = jsonObject.getInt("page_id");
                    String style_name = jsonObject.getString("style_name");
                    String works_name = jsonObject.getString("works_name");
                    String page_path = Config.URLAddress + jsonObject.getString("page_path");
                    LogUtils.d(style_name + " " + works_name + " " + page_path);
                    mImageInfos.add(new ImageInfo(page_id, style_name, works_name, page_path));
                }
                mMainSelectAdapter.updateData(mImageInfos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONUtils.getImage(Config.picAddress, getImageHandler);
                mSwipeRefreshLayout_select.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onCardViewItemClick(View view, int position) {
        SnackbarUtils.with(view)
                .setMessage(mImageInfos.get(position).getImage_style())
                .setDuration(SnackbarUtils.LENGTH_SHORT)
                .showSuccess();
    }
}
