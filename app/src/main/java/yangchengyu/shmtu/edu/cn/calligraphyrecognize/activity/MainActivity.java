package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.Manifest;
import android.animation.Animator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainFragmentAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment.MainFragment;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.Config;

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
    private AHBottomNavigationViewPager mAHBottomNavigationViewPager;
    private ArrayList<AHBottomNavigationItem> mBottomNavigationItems = new ArrayList<>();

    private Window mWindow;
    private Handler mHandler = new Handler();

    private Uri mProviderUri;
    private Uri mUri;

    public static final int PERMISSIONS_REQUEST_CODE = 101;
    public static final int TAKE_PIC_RESULT_CODE = 201;
    public static final int SELECT_PIC_RESULT_CODE = 202;

    private String[] mPerms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private File mOriginalFile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                .getBoolean("translucentNavigation", false);
        setTheme(enabledTranslucentNavigation ? R.style.AppTheme_TranslucentNavigation : R.style.AppTheme);
        setContentView(R.layout.activity_main);
        Utils.init(this);
        mWindow = this.getWindow();
        ask_perms();//获取系统权限
        initUI();
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
            AHBottomNavigationItem item_camera = new AHBottomNavigationItem(R.string.item_content, R.drawable.ic_camera, R.color.color_tab_2);
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
                        getSupportActionBar().setTitle(R.string.item_setting);
                        int color_setting = Color.parseColor("#00886A");
                        ColorDrawable colorDrawable_setting = new ColorDrawable(color_setting);
                        getSupportActionBar().setBackgroundDrawable(colorDrawable_setting);
                        mWindow.setStatusBarColor(color_setting);
                        break;
                }
            }
        });

        mAHBottomNavigationViewPager.setOffscreenPageLimit(2);
        mMainFragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        mAHBottomNavigationViewPager.setAdapter(mMainFragmentAdapter);

        mMainFragment = mMainFragmentAdapter.getCurrentFragment();

        //fab的监听
        FabListener();
    }

    //选择图片
    private void selectImg() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, SELECT_PIC_RESULT_CODE);
        LogUtils.i("select image");
    }

    //拍照
    private void openCamera() {
        FileUtils.createOrExistsDir(Config.ORIGINAL_IMG);
        mOriginalFile = new File(Config.ORIGINAL_IMG, System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            mProviderUri = FileProvider.getUriForFile(this.getApplicationContext(),
                    "yangchengyu.shmtu.edu.cn.calligraphyrecognize.fileprovider", mOriginalFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mProviderUri);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            mUri = Uri.fromFile(mOriginalFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        }
        try {
            startActivityForResult(intent, TAKE_PIC_RESULT_CODE);
        } catch (ActivityNotFoundException anf) {
            ToastUtils.showShort("摄像头未准备好！");
        }
        LogUtils.i("open camera");
    }

    //裁剪图片
    public void cropRawPhoto(Uri uri) {
        UCrop.Options options = new UCrop.Options();
        // 修改标题栏颜色
        options.setToolbarColor(getResources().getColor(R.color.color_tab_5));
        // 修改状态栏颜色
        options.setStatusBarColor(getResources().getColor(R.color.color_tab_5));
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        // 图片格式
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        // 设置图片压缩质量
        options.setCompressionQuality(100);
        // 是否让用户调整范围(默认false)，如果开启，可能会造成剪切的图片的长宽比不是设定的
        // 如果不开启，用户不能拖动选框，只能缩放图片
        options.setFreeStyleCropEnabled(false);
        // 不显示网格线
        options.setShowCropGrid(false);

        FileUtils.createOrExistsDir(Config.CROP_IMG);

        File file = new File(Config.CROP_IMG, System.currentTimeMillis() + ".jpg");
        // 设置源uri及目标uri
        UCrop.of(uri, Uri.fromFile(file))
                // 长宽比
                .withAspectRatio(1, 1)
                // 图片大小
                .withMaxResultSize(1024, 1024)
                // 配置参数
                .withOptions(options)
                .start(this);
        LogUtils.i(file.getName() + " has cropped");
    }

    private void bindView() {
        mBottomNavigation = findViewById(R.id.bn_Main);
        mAHBottomNavigationViewPager = findViewById(R.id.vp_Main);
        mFloatingActionButton = findViewById(R.id.fab_Main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UCrop.RESULT_ERROR) {
            Snackbar.make(mBottomNavigation, String.valueOf(UCrop.getError(data)), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PIC_RESULT_CODE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        cropRawPhoto(mProviderUri);
                    } else {
                        cropRawPhoto(mUri);
                    }
                    break;
                case SELECT_PIC_RESULT_CODE:
                    cropRawPhoto(data.getData());
                    break;
                case UCrop.REQUEST_CROP:
                    Snackbar.make(mBottomNavigation, String.valueOf(UCrop.getOutput(data)), Snackbar.LENGTH_LONG).show();
                    LogUtils.i(String.valueOf(UCrop.getOutput(data)));
                    if (mOriginalFile.exists()) {
                        mOriginalFile.delete();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void FabListener() {
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet);
                bottomSheetDialog.show();
                bottomSheetDialog.findViewById(R.id.bs_bt_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.findViewById(R.id.bs_bt_camera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCamera();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.findViewById(R.id.bs_bt_album).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImg();
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
