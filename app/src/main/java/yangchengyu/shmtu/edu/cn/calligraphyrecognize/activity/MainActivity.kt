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
import com.blankj.utilcode.util.LogUtils
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTheme()//状态了沉浸式主题
        setContentView(R.layout.activity_main)
        Utils.init(this)
        ask_perms()//获取系统权限
        initUI()//UI空间加载
        initAccessTokenWithAkSk()//加载OCR识别API_KEY
    }

    private fun initTheme() {
        val enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                .getBoolean("translucentNavigation", false)
        setTheme(if (enabledTranslucentNavigation) R.style.AppTheme_TranslucentNavigation else R.style.AppTheme)
    }

    private fun ask_perms() {
        if (EasyPermissions.hasPermissions(this, *mPerms)) {
            LogUtils.i(this.javaClass.simpleName + " : permissions are granted")
        } else {
            LogUtils.i(this.javaClass.simpleName + ": these permissions are denied , " +
                    "ready to request this permission")
            EasyPermissions.requestPermissions(this, "使用拍照功能需要拍照权限",
                    PERMISSIONS_REQUEST_CODE, *mPerms)
        }
    }

    private fun initUI() {
        initView()
        initNavigation()
    }

    private fun initView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        alertDialog = AlertDialog.Builder(this)
        mWindow = this.window

        mBottomNavigation = findViewById(R.id.bn_Main)
        mAHBottomNavigationViewPager = findViewById(R.id.vp_Main)
        mFloatingActionButton = findViewById(R.id.fab_Main)

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

    private fun initNavigation() {
        if (useMenuResource) {
            tabColors = applicationContext.resources.getIntArray(R.array.tab_colors)
            navigationAdapter = AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3)
            navigationAdapter!!.setupWithBottomNavigation(mBottomNavigation, tabColors)
            mBottomNavigation!!.isBehaviorTranslationEnabled = true
        } else {
            val item_hot = AHBottomNavigationItem(R.string.item_hot, R.drawable.ic_menu_hot, R.color.color_tab_1)
            val item_content = AHBottomNavigationItem(R.string.item_content, R.drawable.ic_menu_content, R.color.color_tab_2)
            val item_setting = AHBottomNavigationItem(R.string.item_setting, R.drawable.ic_menu_setting, R.color.color_tab_3)

            mBottomNavigationItems.add(item_hot)
            mBottomNavigationItems.add(item_content)
            mBottomNavigationItems.add(item_setting)

            mBottomNavigation!!.addItems(mBottomNavigationItems)
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
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

    private fun waitForResult() {
        try {
            Toast.makeText(this@MainActivity, "正在识别",
                    Toast.LENGTH_SHORT)
                    .show()
        } catch (e: Exception) {
            Looper.prepare()
            Toast.makeText(this@MainActivity, e as CharSequence, Toast.LENGTH_SHORT).show()
            Looper.loop()
        }

    }

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
        hideFABMenu()
        LogUtils.i(this.javaClass.simpleName + ": onKeyDown")
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        LogUtils.i("EasyPermission CallBack onPermissionsGranted() : " + perms[0] +
                " request granted , to do something...")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        LogUtils.i("EasyPermission CallBack onPermissionsDenied():" + requestCode + ":" + perms.size)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private fun alertText(title: String, message: String) {
        this.runOnUiThread {
            alertDialog!!.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("确定", null)
                    .show()
        }
    }

    private fun initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken) {
                val token = result.accessToken
                hasGotToken = true
                LogUtils.d("init Access Token AK SK")
            }

            override fun onError(error: OCRError) {
                error.printStackTrace()
                alertText("AK，SK方式获取token失败", error.message!!)
                LogUtils.e("Error in get Token AK SK")
            }
        }, applicationContext, Config.API_KEY, Config.SECRET_KEY)
    }

    override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {

        hideFABMenu()

        change_status_action_bar_color(position)

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
    private fun change_status_action_bar_color(position: Int) {
        when (position) {
            0 -> setBarColorTitle(R.string.item_hot, "#FF4081")
            1 -> setBarColorTitle(R.string.item_content, "#388FFF")
            2 -> setBarColorTitle(R.string.item_setting, "#00886A")
        }
    }

    private fun setBarColorTitle(item: Int, s: String) {
        supportActionBar!!.setTitle(item)
        val color_hot = Color.parseColor(s)
        val colorDrawable_hot = ColorDrawable(color_hot)
        supportActionBar!!.setBackgroundDrawable(colorDrawable_hot)
        mWindow!!.statusBarColor = color_hot
    }

    override fun onDestroy() {
        super.onDestroy()
        OCR.getInstance().release()
        isAdd = false
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onClick(v: View) {
        when (v.id) {
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
            R.id.miniFab_style -> startIntentForRecognize(REQUEST_CODE_GENERAL)
            R.id.miniFab_ocr -> startIntentForRecognize(REQUEST_CODE_GENERAL_BASIC)
            else -> hideFABMenu()
        }
    }

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
            val words_num = jsonObject.getInt("words_result_num")
            val word_result = jsonObject.getJSONArray("words_result")
            for (i in 0 until words_num) {
                val wordObject = word_result.getJSONObject(i)
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

        private val PERMISSIONS_REQUEST_CODE = 101
        private val REQUEST_CODE_GENERAL = 105
        private val REQUEST_CODE_GENERAL_BASIC = 106
    }
}
