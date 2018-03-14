package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.jude.rollviewpager.RollPagerView;

import java.util.ArrayList;
import java.util.List;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.ResultFragmentAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.RollViewPagerAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment.ResultFragment;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.ImageProcessUtils;

/**
 * Created by kimi9 on 2018/3/6.
 */

public class ResultActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar_result;
    private TabLayout mTab_result;
    private ViewPager mVp_result;
    private RollPagerView mRpv_result;
    private String mCroppedImgPath;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private NestedScrollView mNsv_result;
    private ResultFragmentAdapter mResultFragmentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        translucentSetting();
        setContentView(R.layout.activity_result);
        initUI();
        setActionBar();
        collapsingToolbarSetting();

        List<Bitmap> bitmapList = getBitmapList();
        mRpv_result.setAdapter(new RollViewPagerAdapter(bitmapList));

        mNsv_result.setFillViewport(true);

        mTab_result.setupWithViewPager(mVp_result);
        mResultFragmentAdapter = new ResultFragmentAdapter(getSupportFragmentManager());
        mVp_result.setAdapter(mResultFragmentAdapter);
    }

    @NonNull
    private List<Bitmap> getBitmapList() {
        mCroppedImgPath = this.getIntent().getStringExtra("cropFilePath");
        Bitmap croppedImg = BitmapFactory.decodeFile(mCroppedImgPath);

        Bitmap bintemp = BitmapFactory.decodeFile(mCroppedImgPath);
        Bitmap edgetemp = BitmapFactory.decodeFile(mCroppedImgPath);
        Bitmap sketemp = BitmapFactory.decodeFile(mCroppedImgPath);

        Bitmap binImg = ImageProcessUtils.binProcess(bintemp);
        Bitmap edgeImg = ImageProcessUtils.edgeProcess(edgetemp);
        Bitmap skeletonImg = ImageProcessUtils.skeletonFromJNI(sketemp);

        List<Bitmap> bitmapList = new ArrayList<>();
        bitmapList.add(croppedImg);
        bitmapList.add(binImg);
        bitmapList.add(edgeImg);
        bitmapList.add(skeletonImg);
        return bitmapList;
    }

    private void initUI() {
        mToolbar_result = findViewById(R.id.toolBar_result);
        mTab_result = findViewById(R.id.tab_result);
        mVp_result = findViewById(R.id.viewpager_result);
        mRpv_result = findViewById(R.id.rpv_result);
        mCollapsingToolbarLayout = findViewById(R.id.ctb_result);
        mNsv_result = findViewById(R.id.nsv_result);

    }

    private void setActionBar() {
        setSupportActionBar(mToolbar_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void translucentSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //此FLAG可使状态栏透明，且当前视图在绘制时，从屏幕顶端开始即top = 0开始绘制，这也是实现沉浸效果的基础
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//可不加
        }
    }

    private void collapsingToolbarSetting() {
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
    }

}
