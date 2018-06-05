package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.baidu.ocr.ui.camera.CameraActivity
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.blankj.utilcode.util.Utils
import org.json.JSONException
import org.json.JSONObject
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainFragmentAdapter
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment.MainFragment
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.Config
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.FileUtil
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.RecognizeService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, AHBottomNavigation.OnTabSelectedListener, View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreated")
        super.onCreate(savedInstanceState)
        initTheme()
        setContentView(R.layout.activity_main)
        Utils.init(this)
        askPerms()
        initUI()
        initAccessTokenWithAkSk()//加载OCR识别API_KEY
    }

    //状态了沉浸式主题
    private fun initTheme() {
        val enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                .getBoolean("translucentNavigation", false)
        setTheme(if (enabledTranslucentNavigation) R.style.AppTheme_TranslucentNavigation else R.style.AppTheme)
    }

    //获取系统权限
    private fun askPerms() {
        if (EasyPermissions.hasPermissions(this, *mPerms)) {
            Log.i(TAG, "permissions are granted")
        } else {
            Log.e(TAG, "These permissions are denied , " + "ready to request this permission")
            //回调再次获取权限
            EasyPermissions.requestPermissions(this, "使用拍照功能需要拍照权限",
                    PERMISSIONS_REQUEST_CODE, *mPerms)
        }
    }

    //UI界面加载
    private fun initUI() {
        initView()
        initNavigation()
    }

    //装载实际内容
    private fun initView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        //alertView
        alertDialog = AlertDialog.Builder(this)
        mWindow = this.window

        mBottomNavigation = findViewById(R.id.bn_Main)
        mAHBottomNavigationViewPager = findViewById(R.id.vp_Main)
        mFloatingActionButton = this.findViewById(R.id.fab_Main)

        //fab弹出的两个小按钮
        mFab_root_view = findViewById(R.id.fab_menu_root_view)
        mLl_01 = findViewById(R.id.ll01)
        mLl_02 = findViewById(R.id.ll02)
        mFab_style = findViewById(R.id.miniFab_style)
        mFab_ocr = findViewById(R.id.miniFab_ocr)

        //fab的监听
        mFloatingActionButton!!.setOnClickListener(this)
        mFab_style!!.setOnClickListener(this)
        mFab_ocr!!.setOnClickListener(this)

        setFABAnim()
    }

    //设置底部导航栏的属性
    private fun initNavigation() {
        tabColors = applicationContext.resources.getIntArray(R.array.tab_colors)
        navigationAdapter = AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3)
        navigationAdapter!!.setupWithBottomNavigation(mBottomNavigation, tabColors)
        mBottomNavigation!!.isBehaviorTranslationEnabled = true
        mBottomNavigation!!.manageFloatingActionButtonBehavior(mFloatingActionButton!!)
        mBottomNavigation!!.isTranslucentNavigationEnabled = true
        mBottomNavigation!!.isColored = true
        mBottomNavigation!!.titleState = AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE
        mBottomNavigation!!.refresh()
        mBottomNavigation!!.setOnTabSelectedListener(this)

        initNavigationAdapter()

        mMainFragment = mMainFragmentAdapter!!.currentFragment
    }

    private fun initNavigationAdapter() {
        mAHBottomNavigationViewPager!!.offscreenPageLimit = 2
        mMainFragmentAdapter = MainFragmentAdapter(supportFragmentManager)
        mAHBottomNavigationViewPager!!.adapter = mMainFragmentAdapter
    }

    @SuppressLint("ResourceType")
    private fun setFABAnim() {
        mAddFab_style = AnimatorInflater.loadAnimator(this, R.anim.fab_pop_anim) as AnimatorSet
        mAddFab_ocr = AnimatorInflater.loadAnimator(this, R.anim.fab_pop_anim) as AnimatorSet
    }

    //kotlin会进行空值判断，因此在这里的data需要可空，即添加“？”解决问题
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
            //书法风格识别
                REQUEST_CODE_GENERAL -> {
                    hideFABMenu()
                    mTempImgPath = FileUtil.getSaveFile(applicationContext)
                            .absolutePath
                    RecognizeService.recAccurate(mTempImgPath) { result ->
                        waitForResult()
                        saveCropImg()
                        startActivityNewThread(result)
                    }
                }
            //文字识别
                REQUEST_CODE_GENERAL_BASIC -> {
                    hideFABMenu()
                    RecognizeService.recGeneralBasic(FileUtil.getSaveFile(applicationContext).absolutePath
                    ) { result -> infoPopText(result) }
                }
                else -> {
                }
            }
        }
    }

    //保存裁剪的识别图片
    private fun saveCropImg() {
        try {
            FileUtils.createOrExistsDir(Config.CROP_IMG)
            mCropImg = File(Config.CROP_IMG, System.currentTimeMillis().toString() + ".jpg")
            val fos = FileOutputStream(mCropImg!!)
            val bitmap = BitmapFactory.decodeFile(mTempImgPath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //识别等待提示
    private fun waitForResult() {
        try {
            Toast.makeText(this@MainActivity, "正在识别", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Looper.prepare()
            Toast.makeText(this@MainActivity, e as CharSequence, Toast.LENGTH_SHORT).show()
            Looper.loop()
        }

    }

    //在新线程下打开结果页面
    private fun startActivityNewThread(result: String) {
        Thread(Runnable {
            val intent = Intent(this@MainActivity, ResultActivity::class.java)
            intent.putExtra("JSON", result)
            intent.putExtra("cropImgPath", mCropImg!!.path)
            intent.putExtra(RecognizeActivity.FROMWHERE, "recognize")
            startActivity(intent)
        }).start()
    }

    //双击退出
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.i(TAG, "onKeyDown")
        hideFABMenu()
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish()
            } else {
                SnackbarUtils.with(mFloatingActionButton!!)
                        .setMessage("再按一次退出程序")
                        .setDuration(SnackbarUtils.LENGTH_SHORT)
                        .showWarning()
                mIsExit = true
                Handler().postDelayed({ mIsExit = false }, 2000)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    //获取权限
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //被授予权限后
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.i(TAG, "EasyPermission CallBack onPermissionsGranted() : " + perms[0] +
                " request granted , to do something...")
    }

    //权限被拒绝后再次请求权限
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.w(TAG, "EasyPermission CallBack onPermissionsDenied():" + requestCode + ":" + perms.size)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    //alertView
    private fun alertText(title: String, message: String) {
        this.runOnUiThread {
            alertDialog!!.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("确定", null)
                    .show()
        }
    }

    //获取Baidu Api token
    private fun initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken) {
                val token = result.accessToken
                hasGotToken = true
                Log.i(TAG, "init Access Token AK SK")
            }

            override fun onError(error: OCRError) {
                error.printStackTrace()
                alertText("AK，SK方式获取token失败", error.message!!)
                Log.e(TAG, "Error in get Token AK SK")
            }
        }, applicationContext, Config.API_KEY, Config.SECRET_KEY)
    }

    //设置底部导航栏在被选中后的变化的监听接口
    override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {

        hideFABMenu()

        changeStatusBarColor(position)

        if (mMainFragment == null) {
            mMainFragment = mMainFragmentAdapter!!.currentFragment
        }

        mAHBottomNavigationViewPager!!.setCurrentItem(position, true)

        if (mMainFragment == null) {
            return true
        }

        mMainFragment = mMainFragmentAdapter!!.currentFragment

        if (position == 0 || position == 2) {
            mAHBottomNavigationViewPager!!.currentItem = position
            mFloatingActionButton!!.visibility = View.VISIBLE
            mFloatingActionButton!!.alpha = 0f
            mFloatingActionButton!!.scaleX = 0f
            mFloatingActionButton!!.scaleY = 0f
            mFloatingActionButton!!.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .setInterpolator(OvershootInterpolator())
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            mFloatingActionButton!!.animate()
                                    .setInterpolator(LinearOutSlowInInterpolator())
                                    .start()
                        }

                        override fun onAnimationCancel(animation: Animator) {

                        }

                        override fun onAnimationRepeat(animation: Animator) {

                        }
                    })
                    .start()

        } else {
            if (mFloatingActionButton!!.visibility == View.VISIBLE) {
                mFloatingActionButton!!.animate()
                        .alpha(0f)
                        .scaleX(0f)
                        .scaleY(0f)
                        .setDuration(300)
                        .setInterpolator(LinearOutSlowInInterpolator())
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {

                            }

                            override fun onAnimationEnd(animation: Animator) {
                                mFloatingActionButton!!.visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator) {
                                mFloatingActionButton!!.visibility = View.GONE
                            }

                            override fun onAnimationRepeat(animation: Animator) {

                            }
                        })
                        .start()
            }
        }
        return true
    }

    //装载页面
    private fun changeStatusBarColor(position: Int) {
        when (position) {
            0 -> setBarColorTitle(R.string.item_hot, "#FF4081")
            1 -> setBarColorTitle(R.string.item_content, "#388FFF")
            2 -> setBarColorTitle(R.string.item_setting, "#00886A")
        }
    }

    //设置状态栏颜色，达到切换即可变色
    private fun setBarColorTitle(item: Int, s: String) {
        supportActionBar!!.setTitle(item)
        val color = Color.parseColor(s)
        val colorDrawable = ColorDrawable(color)
        supportActionBar!!.setBackgroundDrawable(colorDrawable)
        mWindow!!.statusBarColor = color
    }

    //退出时的一些操作
    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
        OCR.getInstance().release()
        isAdd = false
        mHandler.removeCallbacksAndMessages(null)
    }

    //点击事件
    override fun onClick(v: View) {
        when (v.id) {
        //弹出小的fab
            R.id.fab_Main -> {
                mFloatingActionButton!!.setImageResource(if (isAdd) R.drawable.ic_fab_add else R.drawable.ic_fab_close)
                isAdd = !isAdd
                mFab_root_view!!.visibility = if (isAdd) View.VISIBLE else View.GONE
                if (isAdd) {
                    mAddFab_style!!.setTarget(mLl_01)
                    mAddFab_style!!.start()
                    mAddFab_ocr!!.setTarget(mLl_02)
                    mAddFab_ocr!!.startDelay = 150
                    mAddFab_ocr!!.start()
                }
            }
        //书法风格识别
            R.id.miniFab_style -> startIntentForRecognize(REQUEST_CODE_GENERAL)
        //文字识别
            R.id.miniFab_ocr -> startIntentForRecognize(REQUEST_CODE_GENERAL_BASIC)
            else -> hideFABMenu()
        }
    }

    //跳转到拍照页面进行识别
    private fun startIntentForRecognize(code: Int) {
        val intent = Intent(this@MainActivity, CameraActivity::class.java)
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(application).absolutePath)
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL)
        startActivityForResult(intent, code)
    }

    private fun infoPopText(result: String) {
        var finalResult = String()
        try {
            val jsonObject = JSONObject(result)
            val wordsNum = jsonObject.getInt("words_result_num")
            val wordResult = jsonObject.getJSONArray("words_result")
            for (i in 0 until wordsNum) {
                val wordObject = wordResult.getJSONObject(i)
                val word = wordObject.getString("words")
                finalResult = finalResult + "第" + (i + 1) + "行结果为：" + word + "\n"
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        alertText("识别结果", finalResult)
    }

    private fun hideFABMenu() {
        mFab_root_view!!.visibility = View.GONE
        mFloatingActionButton!!.setImageResource(R.drawable.ic_fab_add)
        isAdd = false
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 101
        private const val REQUEST_CODE_GENERAL = 105
        private const val REQUEST_CODE_GENERAL_BASIC = 106

        private val TAG = this.javaClass.simpleName

        private var mIsExit: Boolean = false
        private var tabColors: IntArray? = null
        private val useMenuResource = true

        private var mMainFragment: MainFragment? = null
        private var mMainFragmentAdapter: MainFragmentAdapter? = null
        private var mFloatingActionButton: FloatingActionButton? = null

        private var mBottomNavigation: AHBottomNavigation? = null
        private var navigationAdapter: AHBottomNavigationAdapter? = null
        private var mAHBottomNavigationViewPager: AHBottomNavigationViewPager? = null
        private val mBottomNavigationItems = ArrayList<AHBottomNavigationItem>()
        private var alertDialog: AlertDialog.Builder? = null

        private var mWindow: Window? = null
        private val mHandler = Handler()

        private var hasGotToken = false
        private var isAdd = false

        private val mPerms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        private var mCropImg: File? = null
        private var mFab_root_view: RelativeLayout? = null
        private var mFab_style: FloatingActionButton? = null
        private var mFab_ocr: FloatingActionButton? = null
        private var mLl_01: LinearLayout? = null
        private var mLl_02: LinearLayout? = null
        private var mAddFab_style: AnimatorSet? = null
        private var mAddFab_ocr: AnimatorSet? = null
        private var mTempImgPath: String? = null
    }
}
