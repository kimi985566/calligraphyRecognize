package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.widget.ImageView
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R

/**
 * Created by kimi9 on 2018/2/24.
 * 个人主页，用以显示个人信息，如Github页面
 */

class AboutMeActivity : AppCompatActivity() {

    private var mCollapsingToolbar: CollapsingToolbarLayout? = null
    private var mToolBarAboutMe: Toolbar? = null
    private var mIvAboutMe: ImageView? = null
    private var mWebView: WebView? = null

    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i(TAG, "onCreate")

        super.onCreate(savedInstanceState)

        translucentSetting()

        setContentView(R.layout.activity_aboutme)

        initUI()

        toolBarSetting()

        collapsingToolbarSetting()

        //连接到我的Github
        Log.i(TAG, "load url: https://github.com/kimi985566")
        mWebView!!.loadUrl("https://github.com/kimi985566")
    }

    //装载UI界面
    private fun initUI() {
        mCollapsingToolbar = findViewById(R.id.collapsing_toolbar)
        mIvAboutMe = findViewById(R.id.detail_aboutMe)
        mToolBarAboutMe = findViewById(R.id.toolBar_aboutMe)
        mWebView = findViewById(R.id.webView)
    }

    //设置伸缩工具栏名字及样式
    private fun collapsingToolbarSetting() {
        mCollapsingToolbar!!.title = "Chengyu Yang"
        mCollapsingToolbar!!.setExpandedTitleColor(Color.BLACK)
        mCollapsingToolbar!!.setCollapsedTitleTextColor(Color.WHITE)
    }

    //设置全屏
    @SuppressLint("ObsoleteSdkInt")
    private fun translucentSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //此FLAG可使状态栏透明，且当前视图在绘制时，从屏幕顶端开始即top = 0开始绘制，这也是实现沉浸效果的基础
            this.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)//可不加
        }
    }

    //设置工具栏的返回按钮
    private fun toolBarSetting() {
        setSupportActionBar(mToolBarAboutMe)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
    }
}
