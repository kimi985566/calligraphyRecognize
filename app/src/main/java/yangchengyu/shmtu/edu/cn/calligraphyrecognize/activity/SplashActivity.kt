package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent

/**
 * 启动页面
 *
 * 用来显示启动时logo的背景，从而达到禁止启动白屏的效果
 */

class SplashActivity : AppCompatActivity() {

    internal val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash);
        //由于设置了启动加载背景，故不必设置
        mHandler.postDelayed({ gotoStartActivity() }, 1000)

    }

    private fun gotoStartActivity() {
        val intent = Intent(this@SplashActivity, StartActivity::class.java)
        startActivity(intent)
        finish()
        //取消界面跳转时的动画，使启动页的logo图片与注册、登录主页的logo图片完美衔接
        overridePendingTransition(0, 0)
    }

    /**
     * 屏蔽物理返回键
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        mHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
