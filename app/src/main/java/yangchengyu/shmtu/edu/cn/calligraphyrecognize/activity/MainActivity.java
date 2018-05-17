package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        AHBottomNavigation.OnTabSelectedListener, View.OnClickListener {

    private boolean mIsExit;
    private int[] tabColors;
    private boolean useMenuResource = true;

    private MainFragment mMainFragment;
    private MainFragmentAdapter mMainFragmentAdapter;
    private FloatingActionButton mFloatingActionButton;

    private AHBottomNavigation mBottomNavigation;
    private AHBottomNavigationAdapter navigationAdapter;
    private AHBottomNavigationViewPager mAHBottomNavigationViewPager;
    private ArrayList<AHBottomNavigationItem> mBottomNavigationItems = new ArrayList<>();
    private AlertDialog.Builder alertDialog;

    private Window mWindow;
    private Handler mHandler = new Handler();

    private boolean hasGotToken = false;
    private boolean isAdd = false;

    private static final int PERMISSIONS_REQUEST_CODE = 101;
    private static final int REQUEST_CODE_GENERAL = 105;
    private static final int REQUEST_CODE_GENERAL_BASIC = 106;

    private String[] mPerms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private File mCropImg;
    private RelativeLayout mFab_root_view;
    private FloatingActionButton mFab_style;
    private FloatingActionButton mFab_ocr;
    private LinearLayout mLl_01;
    private LinearLayout mLl_02;
    private AnimatorSet mAddFab_style;
    private AnimatorSet mAddFab_ocr;
    private String mTempImgPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();//状态了沉浸式主题
        setContentView(R.layout.activity_main);
        Utils.init(this);
        ask_perms();//获取系统权限
        initUI();//UI空间加载
        initAccessTokenWithAkSk();//加载OCR识别API_KEY
    }

    private void initTheme() {
        boolean enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                .getBoolean("translucentNavigation", false);
        setTheme(enabledTranslucentNavigation ? R.style.AppTheme_TranslucentNavigation : R.style.AppTheme);
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
        initView();
        initNavigation();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        alertDialog = new AlertDialog.Builder(this);
        mWindow = this.getWindow();

        mBottomNavigation = findViewById(R.id.bn_Main);
        mAHBottomNavigationViewPager = findViewById(R.id.vp_Main);
        mFloatingActionButton = findViewById(R.id.fab_Main);

        mFab_root_view = findViewById(R.id.fab_menu_root_view);
        mLl_01 = findViewById(R.id.ll01);
        mLl_02 = findViewById(R.id.ll02);
        mFab_style = findViewById(R.id.miniFab_style);
        mFab_ocr = findViewById(R.id.miniFab_ocr);

        //fab的监听
        mFloatingActionButton.setOnClickListener(this);
        mFab_style.setOnClickListener(this);
        mFab_ocr.setOnClickListener(this);

        setFABAnim();
    }

    private void initNavigation() {
        if (useMenuResource) {
            tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
            navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
            navigationAdapter.setupWithBottomNavigation(mBottomNavigation, tabColors);
            mBottomNavigation.setBehaviorTranslationEnabled(true);
        } else {
            AHBottomNavigationItem item_hot = new AHBottomNavigationItem(R.string.item_hot, R.drawable.ic_menu_hot, R.color.color_tab_1);
            AHBottomNavigationItem item_content = new AHBottomNavigationItem(R.string.item_content, R.drawable.ic_menu_content, R.color.color_tab_2);
            AHBottomNavigationItem item_setting = new AHBottomNavigationItem(R.string.item_setting, R.drawable.ic_menu_setting, R.color.color_tab_3);

            mBottomNavigationItems.add(item_hot);
            mBottomNavigationItems.add(item_content);
            mBottomNavigationItems.add(item_setting);

            mBottomNavigation.addItems(mBottomNavigationItems);
        }
        mBottomNavigation.manageFloatingActionButtonBehavior(mFloatingActionButton);
        mBottomNavigation.setTranslucentNavigationEnabled(true);
        mBottomNavigation.setColored(true);
        mBottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        mBottomNavigation.refresh();
        mBottomNavigation.setOnTabSelectedListener(this);

        initNavigationAdapter();

        mMainFragment = mMainFragmentAdapter.getCurrentFragment();
    }

    private void initNavigationAdapter() {
        mAHBottomNavigationViewPager.setOffscreenPageLimit(2);
        mMainFragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        mAHBottomNavigationViewPager.setAdapter(mMainFragmentAdapter);
    }

    @SuppressLint("ResourceType")
    private void setFABAnim() {
        mAddFab_style = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.anim.fab_pop_anim);
        mAddFab_ocr = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.anim.fab_pop_anim);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GENERAL:
                    hideFABMenu();
                    mTempImgPath = FileUtil.getSaveFile(getApplicationContext())
                            .getAbsolutePath();
                    RecognizeService.recAccurate(mTempImgPath, new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(final String result) {
                            waitForResult();
                            saveCropImg();
                            startActivityNewThread(result);
                        }
                    });
                    break;
                case REQUEST_CODE_GENERAL_BASIC:
                    hideFABMenu();
                    RecognizeService.recGeneralBasic(FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(String result) {
                                    infoPopText(result);
                                }
                            });
                    break;
                default:
                    break;
            }
        }
    }

    private void saveCropImg() {
        try {
            FileUtils.createOrExistsDir(Config.CROP_IMG);
            mCropImg = new File(Config.CROP_IMG, System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(mCropImg);
            Bitmap bitmap = BitmapFactory.decodeFile(mTempImgPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForResult() {
        try {
            Toast.makeText(MainActivity.this, "正在识别",
                    Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            Looper.prepare();
            Toast.makeText(MainActivity.this, (CharSequence) e, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    private void startActivityNewThread(final String result) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("JSON", result);
                intent.putExtra("cropImgPath", mCropImg.getPath());
                intent.putExtra(RecognizeActivity.FROMWHERE, "recognize");
                startActivity(intent);
            }
        }).start();
    }

    //双击退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        hideFABMenu();
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
    public boolean onTabSelected(int position, boolean wasSelected) {

        hideFABMenu();

        change_status_action_bar_color(position);

        if (mMainFragment == null) {
            mMainFragment = mMainFragmentAdapter.getCurrentFragment();
        }

        mAHBottomNavigationViewPager.setCurrentItem(position, true);

        if (mMainFragment == null) {
            return true;
        }

        mMainFragment = mMainFragmentAdapter.getCurrentFragment();

        if (position == 0 || position == 2) {
            mAHBottomNavigationViewPager.setCurrentItem(position);
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
                setBarColorTitle(R.string.item_hot, "#FF4081");
                break;
            case 1:
                setBarColorTitle(R.string.item_content, "#388FFF");
                break;
            case 2:
                setBarColorTitle(R.string.item_setting, "#00886A");
                break;
        }
    }

    private void setBarColorTitle(int item, String s) {
        getSupportActionBar().setTitle(item);
        int color_hot = Color.parseColor(s);
        ColorDrawable colorDrawable_hot = new ColorDrawable(color_hot);
        getSupportActionBar().setBackgroundDrawable(colorDrawable_hot);
        mWindow.setStatusBarColor(color_hot);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OCR.getInstance().release();
        isAdd = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_Main:
                mFloatingActionButton.setImageResource(isAdd ? R.drawable.ic_fab_add : R.drawable.ic_fab_close);
                isAdd = !isAdd;
                mFab_root_view.setVisibility(isAdd ? View.VISIBLE : View.GONE);
                if (isAdd) {
                    mAddFab_style.setTarget(mLl_01);
                    mAddFab_style.start();
                    mAddFab_ocr.setTarget(mLl_02);
                    mAddFab_ocr.setStartDelay(150);
                    mAddFab_ocr.start();
                }
                break;
            case R.id.miniFab_style:
                startIntentForRecognize(REQUEST_CODE_GENERAL);
                break;
            case R.id.miniFab_ocr:
                startIntentForRecognize(REQUEST_CODE_GENERAL_BASIC);
                break;
            default:
                hideFABMenu();
                break;
        }
    }

    private void startIntentForRecognize(int code) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(getApplication()).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL);
        startActivityForResult(intent, code);
    }

    private void infoPopText(final String result) {
        String finalResult = new String();
        try {
            JSONObject jsonObject = new JSONObject(result);
            int words_num = jsonObject.getInt("words_result_num");
            JSONArray word_result = jsonObject.getJSONArray("words_result");
            for (int i = 0; i < words_num; i++) {
                JSONObject wordObject = word_result.getJSONObject(i);
                String word = wordObject.getString("words");
                finalResult = finalResult + "第" + (i + 1) + "行结果为：" + word + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        alertText("识别结果", finalResult);
    }

    private void hideFABMenu() {
        mFab_root_view.setVisibility(View.GONE);
        mFloatingActionButton.setImageResource(R.drawable.ic_fab_add);
        isAdd = false;
    }
}
