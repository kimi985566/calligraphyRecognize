package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.OpenCVListViewAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.OpenCVConstants;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.OpenCVInfo;

public class OpenCVActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private Toolbar mToolbar_opencv;
    private List<OpenCVInfo> mOpenCVInfos = new ArrayList<>();
    private OpenCVListViewAdapter mOpenCVListViewAdapter;
    private String mProcessName;
    private String mProcessCMD;
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv);
        initView();
        initOpenCVLibs();
        actionBarSetting();
    }

    private void initView() {
        mToolbar_opencv = findViewById(R.id.toolbar_sub);
        mListView = findViewById(R.id.listView_opencv);
        mOpenCVListViewAdapter = new OpenCVListViewAdapter(this, mOpenCVInfos);
        mListView.setAdapter(mOpenCVListViewAdapter);
        mListView.setOnItemClickListener(this);
        mOpenCVListViewAdapter.getOpenCVInfos().addAll(OpenCVInfo.getAllList());
        mOpenCVListViewAdapter.notifyDataSetChanged();
    }

    private void initOpenCVLibs() {
        boolean isSuccess = OpenCVLoader.initDebug();
        if (isSuccess) {
            LogUtils.i("OpenCV init success");
        }
    }

    private void actionBarSetting() {
        setSupportActionBar(mToolbar_opencv);
        getSupportActionBar().setTitle("OpenCV图像处理");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = view.getTag();
        if (object instanceof OpenCVInfo) {
            OpenCVInfo openCVInfo = (OpenCVInfo) object;
            mProcessName = openCVInfo.getName();
            mProcessCMD = openCVInfo.getCommend();
        }
        processIntent();
    }

    private void processIntent() {
        if (OpenCVConstants.MANUAL_THRESH_NAME.equals(mProcessName)
                || OpenCVConstants.CANNY_NAME.equals(mProcessName)) {
            Intent intent = new Intent(OpenCVActivity.this, SeekBarProcessActivity.class);
            intent.putExtra("commend", mProcessCMD);
            intent.putExtra("name", mProcessName);
            startActivity(intent);
        } else {
            Intent intent = new Intent(OpenCVActivity.this, ProcessActivity.class);
            intent.putExtra("commend", mProcessCMD);
            intent.putExtra("name", mProcessName);
            startActivity(intent);
        }
    }
}
