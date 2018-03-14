package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.jude.rollviewpager.RollPagerView;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;

/**
 * Created by kimi9 on 2018/3/6.
 */

public class ResultActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar_result;
    private TabLayout mTab_result;
    private ViewPager mVp_result;
    private RollPagerView mRpv_result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initUI();
        translucentSetting();
        setActionBar();
    }

    private void initUI() {
        mToolbar_result = findViewById(R.id.toolBar_result);
        mTab_result = findViewById(R.id.tab_result);
        mVp_result = findViewById(R.id.viewpager_result);
        mRpv_result = findViewById(R.id.rpv_result);
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


}
