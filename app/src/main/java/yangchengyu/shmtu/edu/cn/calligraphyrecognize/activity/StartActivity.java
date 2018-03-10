package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;

public class StartActivity extends AppCompatActivity {

    private ImageView mivLogo;
    private TextView mtvAppName;
    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initViews();
        initAnimations();
    }

    private void initViews() {
        mtvAppName = findViewById(R.id.tv_app_name);
        mivLogo = findViewById(R.id.iv_logo);
        Typeface typeface;
        //判断系统
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            typeface = getResources().getFont(R.font.lixukefonts);
        } else {
            typeface = Typeface.createFromAsset(getAssets(), "lixukefonts.ttf");
        }
        mtvAppName.setTypeface(typeface);
    }

    private void initAnimations() {
        //初始化底部App Name
        //以控件自身所在的位置为原点，从下方距离原点200像素的位置移动到原点
        ObjectAnimator tranAppName = ObjectAnimator.ofFloat(mtvAppName, "translationY", 200, 0);
        //将注册、登录的控件alpha属性从0变到1
        ObjectAnimator alphaAppName = ObjectAnimator.ofFloat(mtvAppName, "alpha", 0, 1);
        final AnimatorSet bottomAnim = new AnimatorSet();
        bottomAnim.setDuration(1200);
        //同时执行控件平移和alpha渐变动画
        bottomAnim.play(tranAppName).with(alphaAppName);

        //获取屏幕高度
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (manager != null) {
            manager.getDefaultDisplay().getMetrics(metrics);
        }
        int screenHeight = metrics.heightPixels;

        //通过测量，获取ivLogo的高度
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mivLogo.measure(w, h);
        int logoHeight = mivLogo.getMeasuredHeight();

        //初始化ivLogo的移动和缩放动画
        float transY = (screenHeight - logoHeight) * 0.28f;
        //ivLogo向上移动 transY 的距离
        ObjectAnimator tranLogo = ObjectAnimator.ofFloat(mivLogo, "translationY", 0, -transY);
        //ivLogo在X轴和Y轴上都缩放0.75倍
        ObjectAnimator scaleXLogo = ObjectAnimator.ofFloat(mivLogo, "scaleX", 1f, 0.8f);
        ObjectAnimator scaleYLogo = ObjectAnimator.ofFloat(mivLogo, "scaleY", 1f, 0.8f);
        AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.setDuration(1200);
        logoAnim.play(tranLogo).with(scaleXLogo).with(scaleYLogo);
        logoAnim.start();
        logoAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //待ivLogo的动画结束后,开始播放底部注册、登录按钮的动画
                bottomAnim.start();
                gotoMainActivity();
            }
        });
    }

    private void gotoMainActivity() {
        //跳转到主界面
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1200);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //若启动中点击返回，则退出
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.exit(0);
        }
        return super.onKeyDown(keyCode, event);
    }
}
