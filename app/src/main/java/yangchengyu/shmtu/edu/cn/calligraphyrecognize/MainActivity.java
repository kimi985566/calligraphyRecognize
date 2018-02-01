package yangchengyu.shmtu.edu.cn.calligraphyrecognize;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public long exitTime;
    private int[] tabColors;
    private boolean useMenuResource = true;

    private MainFragment mMainFragment;
    private MainViewPagerAdapter mMainViewPagerAdapter;
    private Handler mHandler = new Handler();

    private AHBottomNavigation mBottomNavigation;
    private AHBottomNavigationAdapter navigationAdapter;
    private AHBottomNavigationViewPager mAHBottomNavigationViewPager;
    private FloatingActionButton mFloatingActionButton;
    private ArrayList<AHBottomNavigationItem> mBottomNavigationItems = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                .getBoolean("translucentNavigation", false);
        setTheme(enabledTranslucentNavigation ? R.style.AppTheme_TranslucentNavigation : R.style.AppTheme);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        mBottomNavigation = findViewById(R.id.bn_Main);
        mAHBottomNavigationViewPager = findViewById(R.id.vp_Main);
        mFloatingActionButton = findViewById(R.id.fab_Main);

        if (useMenuResource) {
            tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
            navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
            navigationAdapter.setupWithBottomNavigation(mBottomNavigation, tabColors);
            mBottomNavigation.setBehaviorTranslationEnabled(true);
        } else {
            AHBottomNavigationItem item_hot = new AHBottomNavigationItem(R.string.item_hot, R.drawable.ic_menu_hot, R.color.color_tab_1);
            AHBottomNavigationItem item_camera = new AHBottomNavigationItem(R.string.item_camera, R.drawable.ic_menu_camera, R.color.color_tab_2);
            AHBottomNavigationItem item_setting = new AHBottomNavigationItem(R.string.item_setting, R.drawable.ic_menu_setting, R.color.color_tab_3);

            mBottomNavigationItems.add(item_hot);
            mBottomNavigationItems.add(item_camera);
            mBottomNavigationItems.add(item_setting);

            mBottomNavigation.addItems(mBottomNavigationItems);
        }

        mBottomNavigation.manageFloatingActionButtonBehavior(mFloatingActionButton);
        mBottomNavigation.setTranslucentNavigationEnabled(true);
        mBottomNavigation.setColored(true);
        mBottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        mBottomNavigation.setCurrentItem(0);
        mAHBottomNavigationViewPager.setCurrentItem(0);

        mBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (mMainFragment == null) {
                    mMainFragment = mMainViewPagerAdapter.getCurrentFragment();
                }

                if (wasSelected) {
                    mMainFragment.refresh();
                    return true;
                }

                if (mMainFragment != null) {
                    mMainFragment.willBeHidden();
                }

                mAHBottomNavigationViewPager.setCurrentItem(position, false);

                if (mMainFragment == null) {
                    return true;
                }

                mMainFragment = mMainViewPagerAdapter.getCurrentFragment();
                mMainFragment.willBeDisplayed();

                if (position == 0) {
                    mAHBottomNavigationViewPager.setCurrentItem(0);
                    mFloatingActionButton.setVisibility(View.VISIBLE);
                    mFloatingActionButton.setAlpha(0f);
                    mFloatingActionButton.setScaleX(0f);
                    mFloatingActionButton.setScaleY(0f);
                    mFloatingActionButton.animate()
                            .alpha(1)
                            .scaleX(1)
                            .scaleY(1)
                            .setDuration(300)
                            .setInterpolator(new OvershootInterpolator())
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mFloatingActionButton.animate()
                                            .setInterpolator(new LinearOutSlowInInterpolator())
                                            .start();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .start();

                } else {
                    if (mFloatingActionButton.getVisibility() == View.VISIBLE) {
                        mFloatingActionButton.animate()
                                .alpha(0)
                                .scaleX(0)
                                .scaleY(0)
                                .setDuration(300)
                                .setInterpolator(new LinearOutSlowInInterpolator())
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mFloatingActionButton.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        mFloatingActionButton.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .start();
                    }
                }

                return true;
            }
        });

        mAHBottomNavigationViewPager.setOffscreenPageLimit(4);
        mMainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mAHBottomNavigationViewPager.setAdapter(mMainViewPagerAdapter);

        mMainFragment = mMainViewPagerAdapter.getCurrentFragment();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 1000) {
                Snackbar.make(mBottomNavigation, "再按一次退出程序", Snackbar.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
