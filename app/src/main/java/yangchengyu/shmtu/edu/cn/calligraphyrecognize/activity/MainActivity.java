package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainFragmentAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment.MainFragment;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.Config;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.FileUtil;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.RecognizeService;

import static yangchengyu.shmtu.edu.cn.calligraphyrecognize.R.color.color_tab_1;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private boolean mIsExit;
    private int[] tabColors;
    private boolean useMenuResource = true;

    private MainFragment mMainFragment;
    private MainFragmentAdapter mMainFragmentAdapter;
    private FloatingActionButton mFloatingActionButton;

    private AHBottomNavigation mBottomNavigation;
    private AHBottomNavigationAdapter navigationAdapter;
    private ViewPager mAHBottomNavigationViewPager;
    private ArrayList<AHBottomNavigationItem> mBottomNavigationItems = new ArrayList<>();

    private Window mWindow;
    private Handler mHandler = new Handler();

    private AlertDialog.Builder alertDialog;

    private boolean hasGotToken = false;

    public static final int PERMISSIONS_REQUEST_CODE = 101;
    public static final int TAKE_PIC_RESULT_CODE = 201;
    public static final int SELECT_PIC_RESULT_CODE = 202;

    private static final int REQUEST_CODE_GENERAL = 105;

    private String[] mPerms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private File mCropImg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                .getBoolean("translucentNavigation", false);
        setTheme(enabledTranslucentNavigation ? R.style.AppTheme_TranslucentNavigation : R.style.AppTheme);
        setContentView(R.layout.activity_main);
        Utils.init(this);
        alertDialog = new AlertDialog.Builder(this);
        mWindow = this.getWindow();
        ask_perms();//获取系统权限
        initUI();
        initAccessTokenWithAkSk();

    }

    private void ask_perms() {
        if (EasyPermissions.hasPermissions(this, mPerms)) {
            LogUtils.i(this.getClass().getSimpleName() + " : permissions are granted");
        } else {
            LogUtils.i(this.getClass().getSimpleName() + ": these permissions are denied , " +
                    "ready to request this permission");
            EasyPermissions.requestPermissions(this, "使用拍照功能需要拍照权限",
                    PERMISSIONS_REQUEST_CODE, mPerms);
        }
    }

    private void initUI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        bindView();

        if (useMenuResource) {
            tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
            navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
            navigationAdapter.setupWithBottomNavigation(mBottomNavigation, tabColors);
            mBottomNavigation.setBehaviorTranslationEnabled(true);
        } else {
            AHBottomNavigationItem item_hot = new AHBottomNavigationItem(R.string.item_hot, R.drawable.ic_menu_hot, color_tab_1);
            AHBottomNavigationItem item_content = new AHBottomNavigationItem(R.string.item_content, R.drawable.ic_menu_content, R.color.color_tab_2);
            AHBottomNavigationItem item_setting = new AHBottomNavigationItem(R.string.item_select, R.drawable.ic_menu_select, R.color.color_tab_3);

            mBottomNavigationItems.add(item_hot);
            mBottomNavigationItems.add(item_content);
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
                change_status_action_bar_color(position);

                if (mMainFragment == null) {
                    mMainFragment = mMainFragmentAdapter.getCurrentFragment();
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

                mMainFragment = mMainFragmentAdapter.getCurrentFragment();
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

            //装载页面
            private void change_status_action_bar_color(int position) {
                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle(R.string.item_hot);
                        int color_hot = Color.parseColor("#FF4081");
                        ColorDrawable colorDrawable_hot = new ColorDrawable(color_hot);
                        getSupportActionBar().setBackgroundDrawable(colorDrawable_hot);
                        mWindow.setStatusBarColor(color_hot);
                        break;
                    case 1:
                        getSupportActionBar().setTitle(R.string.item_content);
                        int color_content = Color.parseColor("#303F9F");
                        ColorDrawable colorDrawable_content = new ColorDrawable(color_content);
                        getSupportActionBar().setBackgroundDrawable(colorDrawable_content);
                        mWindow.setStatusBarColor(color_content);
                        break;
                    case 2:
                        getSupportActionBar().setTitle(R.string.item_select);
                        int color_setting = Color.parseColor("#00886A");
                        ColorDrawable colorDrawable_setting = new ColorDrawable(color_setting);
                        getSupportActionBar().setBackgroundDrawable(colorDrawable_setting);
                        mWindow.setStatusBarColor(color_setting);
                        break;
                }
            }
        });

        mAHBottomNavigationViewPager.setOffscreenPageLimit(3);
        mMainFragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        mAHBottomNavigationViewPager.setAdapter(mMainFragmentAdapter);

        mMainFragment = mMainFragmentAdapter.getCurrentFragment();

        //fab的监听
        FabListener();
    }

    private void bindView() {
        mBottomNavigation = findViewById(R.id.bn_Main);
        mAHBottomNavigationViewPager = findViewById(R.id.vp_Main);
        mFloatingActionButton = findViewById(R.id.fab_Main);
    }

    private void FabListener() {
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_GENERAL);
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GENERAL:
                    final String tempImgPath = FileUtil.getSaveFile(getApplicationContext())
                            .getAbsolutePath();
                    RecognizeService.recAccurate(tempImgPath, new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(final String result) {
                            Toast.makeText(MainActivity.this, "正在识别",
                                    Toast.LENGTH_SHORT)
                                    .show();
                            saveCropImg();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                    intent.putExtra("JSON", result);
                                    intent.putExtra("cropImgPath", mCropImg.getPath());
                                    intent.putExtra(MainFragment.FROMWHERE, "recognize");
                                    startActivity(intent);
                                }
                            }).start();
                        }

                        private void saveCropImg() {
                            try {
                                FileUtils.createOrExistsDir(Config.CROP_IMG);
                                mCropImg = new File(Config.CROP_IMG, System.currentTimeMillis() + ".jpg");
                                FileOutputStream fos = new FileOutputStream(mCropImg);
                                Bitmap bitmap = BitmapFactory.decodeFile(tempImgPath);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    //双击退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.i(this.getClass().getSimpleName() + ": onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();
            } else {
                SnackbarUtils.with(mFloatingActionButton)
                        .setMessage("再按一次退出程序")
                        .setDuration(SnackbarUtils.LENGTH_SHORT)
                        .showWarning();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtils.i("EasyPermission CallBack onPermissionsGranted() : " + perms.get(0) +
                " request granted , to do something...");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtils.i("EasyPermission CallBack onPermissionsDenied():" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
                LogUtils.d("init Access Token AK SK");
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
                LogUtils.e("Error in get Token AK SK");
            }
        }, getApplicationContext(), Config.API_KEY, Config.SECRET_KEY);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OCR.getInstance().release();
        mHandler.removeCallbacksAndMessages(null);
    }
}
