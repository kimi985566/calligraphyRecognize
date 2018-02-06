package yangchengyu.shmtu.edu.cn.calligraphyrecognize;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

/**
 * Created by kimi9 on 2018/1/22.
 */

public class SplashActivity extends AppCompatActivity {

    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);
        //由于设置了启动加载背景，故不必设置
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoLogin();
            }
        }, 1000);

    }

    private void gotoLogin() {
        Intent intent = new Intent(SplashActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
        //取消界面跳转时的动画，使启动页的logo图片与注册、登录主页的logo图片完美衔接
        overridePendingTransition(0, 0);
    }

    /**
     * 屏蔽物理返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            //If token is null, all callbacks and messages will be removed.
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
