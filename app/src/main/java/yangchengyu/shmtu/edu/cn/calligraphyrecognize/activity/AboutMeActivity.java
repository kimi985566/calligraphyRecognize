package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;

/**
 * Created by kimi9 on 2018/2/24.
 */

public class AboutMeActivity extends AppCompatActivity {

    private CollapsingToolbarLayout mCollapsingToolbar;
    private Toolbar mToolBarAboutMe;
    private ImageView mIv_aboutMe;
    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        translucentSetting();
        setContentView(R.layout.activity_aboutme);

        initUI();
        toolBarSetting();
        collapsingToolbarSetting();

        //连接到我的Github
        mWebView.loadUrl("https://github.com/kimi985566");
    }

    //装载UI界面
    private void initUI() {
        mCollapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mIv_aboutMe = findViewById(R.id.detail_aboutMe);
        mToolBarAboutMe = findViewById(R.id.toolBar_aboutMe);
        mWebView = findViewById(R.id.webView);
    }

    //设置伸缩工具栏名字及样式
    private void collapsingToolbarSetting() {
        mCollapsingToolbar.setTitle("Chengyu Yang");
        mCollapsingToolbar.setExpandedTitleColor(Color.BLACK);
        mCollapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
    }

    //设置全屏
    private void translucentSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //此FLAG可使状态栏透明，且当前视图在绘制时，从屏幕顶端开始即top = 0开始绘制，这也是实现沉浸效果的基础
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//可不加
        }
    }

    //设置工具栏的返回按钮
    private void toolBarSetting() {
        setSupportActionBar(mToolBarAboutMe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}
