package yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.blankj.utilcode.util.CleanUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.jude.rollviewpager.RollPagerView
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.jetbrains.anko.support.v4.toast
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity.AboutMeActivity
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity.DisplayActivity
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity.OpenCVActivity
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity.RecognizeActivity
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainFragmentFunctionAdapter
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.RollViewPagerAdapter
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.FunctionInfo
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.ImageProcessUtils
import java.io.FileNotFoundException
import java.util.*


//装载多个Fragment

class MainFragment : Fragment(), View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback, RadioGroup.OnCheckedChangeListener, OnCardViewItemListener, CompoundButton.OnCheckedChangeListener {
    private val maxSize = 1024

    private var mBmp: Bitmap? = null
    private var mTemp: Bitmap? = null
    private var mBtn_content_select: Button? = null
    private var mBtn_content_process: Button? = null
    private var mTv_test: TextView? = null
    private var mIv_content: ImageView? = null
    private var mRg_content: RadioGroup? = null
    private var mRpv_fragment_main: RollPagerView? = null
    private var mRv_fragment_main: RecyclerView? = null

    private val mFunctionInfos = ArrayList<FunctionInfo>()
    private var mMainFragmentFunctionAdapter: MainFragmentFunctionAdapter? = null
    private var mIv_setting_background: ImageView? = null
    private var mIv_setting_avater: ImageView? = null
    private var mClearItem: View? = null
    private var mSwicth_night: Switch? = null
    private var enableNightMode = false
    private var mSharedPreferences: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //装在主界面第一页
        if (arguments!!.getInt("index", 0) == 0) {
            val view = inflater.inflate(R.layout.fragment_main_select, container, false)
            initMainSelect(view)
            return view
            //装在第二页
        } else if (arguments!!.getInt("index", 0) == 1) {
            val view = inflater.inflate(R.layout.fragment_main_content, container, false)
            initMainContent(view)
            return view
            //装在第三页
        } else {
            val view = inflater.inflate(R.layout.fragment_main_setting, container, false)
            initSetting(view)
            return view
        }
    }

    //第一页的加载
    private fun initMainSelect(view: View) {
        mRpv_fragment_main = view.findViewById(R.id.rpv_fragment_main_select)
        mRv_fragment_main = view.findViewById(R.id.recycleview_fragment_main_select)

        setRollViewPager()
        setFunctionRecycle()
    }

    private fun setFunctionRecycle() {
        setFunctionList()
        setHorizonFunc()
    }

    private fun setHorizonFunc() {
        val gridLayoutManager = GridLayoutManager(this.context, 2,
                GridLayoutManager.VERTICAL, false)
        mRv_fragment_main!!.layoutManager = gridLayoutManager
        mMainFragmentFunctionAdapter = MainFragmentFunctionAdapter(this.context!!, mFunctionInfos)
        mMainFragmentFunctionAdapter!!.setOnCardViewItemListener(this)
        mRv_fragment_main!!.adapter = mMainFragmentFunctionAdapter
    }

    private fun setFunctionList() {
        val functionInfo_recognize = FunctionInfo(R.drawable.ic_function_pic, "识别记录")
        val functionInfo_select = FunctionInfo(R.drawable.ic_function_select, "精选书法")
        val functionInfo_opencv = FunctionInfo(R.drawable.ic_function_opencv, "OpenCV学习")
        mFunctionInfos.add(functionInfo_recognize)
        mFunctionInfos.add(functionInfo_select)
        mFunctionInfos.add(functionInfo_opencv)
    }

    private fun setRollViewPager() {
        val bitmapList = ArrayList<Bitmap>()
        val smu = BitmapFactory.decodeResource(resources, R.drawable.ic_smu_banner)
        val smu_towel = BitmapFactory.decodeResource(resources, R.drawable.ic_school_banner)
        bitmapList.add(smu)
        bitmapList.add(smu_towel)
        mRpv_fragment_main!!.setAdapter(RollViewPagerAdapter(bitmapList))
    }

    //第二页的加载

    private fun initMainContent(view: View) {
        mTv_test = view.findViewById(R.id.tv_content)
        mIv_content = view.findViewById(R.id.iv_content)
        mRg_content = view.findViewById(R.id.rg_content)
        mBtn_content_select = view.findViewById(R.id.btn_content_select)
        mBtn_content_select!!.setOnClickListener(this)
        mBtn_content_process = view.findViewById(R.id.btn_content_process)
        mBtn_content_process!!.setOnClickListener(this)
        mRg_content!!.setOnCheckedChangeListener(this)
    }

    //第三页的加载
    private fun initSetting(view: View) {
        initTopPic(view)

        mSwicth_night = view.findViewById(R.id.sw_item_setting_switch)
        mClearItem = view.findViewById(R.id.view_fragment_clear)
        mSharedPreferences = activity!!.getSharedPreferences("myPreference", Context.MODE_PRIVATE)
        enableNightMode = mSharedPreferences!!.getBoolean(ISNIGHT, false)
        mSwicth_night!!.isChecked = enableNightMode
        mSwicth_night!!.setOnCheckedChangeListener(this)
        mClearItem!!.setOnClickListener(this)

    }

    private fun initTopPic(view: View) {
        mIv_setting_background = view.findViewById(R.id.iv_setting_blur)
        mIv_setting_avater = view.findViewById(R.id.iv_setting_avatar)
        initPic()
        mIv_setting_avater!!.setOnClickListener(this)
    }

    private fun initPic() {
        Glide.with(this).load(R.drawable.ic_image_aboutme)
                .apply(bitmapTransform(BlurTransformation(25)))
                .into(mIv_setting_background!!)

        Glide.with(this).load(R.drawable.ic_image_aboutme)
                .apply(bitmapTransform(CropCircleTransformation()))
                .into(mIv_setting_avater!!)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_content_select -> selectImage()
            R.id.btn_content_process -> when (mRg_content!!.checkedRadioButtonId) {
                R.id.rb_bin -> {
                    mTemp = ImageProcessUtils.binProcess(mBmp!!)
                    mIv_content!!.setImageBitmap(mTemp)
                }
                R.id.rb_edge -> {
                    mTemp = ImageProcessUtils.edgeProcess(mBmp!!)
                    mIv_content!!.setImageBitmap(mTemp)
                }
                R.id.rb_ske -> {
                    mTemp = ImageProcessUtils.skeletonFromJNI(mBmp!!)
                    mIv_content!!.setImageBitmap(mTemp)
                }
                R.id.rb_ske_java -> {
                    mTemp = ImageProcessUtils.skeletonProcess(mBmp!!)
                    mIv_content!!.setImageBitmap(mTemp)
                }
            }
            R.id.iv_setting_avatar -> {
                val intent = Intent(this.context, AboutMeActivity::class.java)
                startActivity(intent)
            }
            R.id.view_fragment_clear -> {
                CleanUtils.cleanInternalCache()
                CleanUtils.cleanExternalCache()
                SnackbarUtils.with(mClearItem!!).setMessage("清除缓存成功").showSuccess()
            }
        }
    }

    private fun selectImage() {
        val pickIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        pickIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(pickIntent, "Browser Image……"),
                SELECT_PIC_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PIC_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            initImageView(data)
        }
    }

    private fun initImageView(data: Intent) {
        val uri = data.data
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            val inputStream = activity!!.contentResolver.openInputStream(uri!!)
            BitmapFactory.decodeStream(inputStream, null, options)

            val height = options.outHeight
            val width = options.outWidth
            var sampleSize = 1
            val max = Math.max(height, width)

            //压缩图片，防止OOM
            if (max > maxSize) {
                val nw = width / 2
                val nh = height / 2
                while (nw / sampleSize > maxSize || nh / sampleSize > maxSize) {
                    sampleSize *= 2
                }
            }

            options.inSampleSize = sampleSize
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            mBmp = BitmapFactory.decodeStream(activity!!.contentResolver.openInputStream(uri), null, options)
            mIv_content!!.setImageBitmap(mBmp)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        when (group.checkedRadioButtonId) {
            R.id.rb_bin -> {
                mBtn_content_process!!.setText(R.string.binProcess)
                mTv_test!!.setText(R.string.binProcess)
            }
            R.id.rb_edge -> {
                mBtn_content_process!!.setText(R.string.edgeProcess)
                mTv_test!!.setText(R.string.edgeProcess)
            }
            R.id.rb_ske -> {
                mBtn_content_process!!.setText(R.string.skeProcess)
                mTv_test!!.setText(R.string.skeProcess)
            }
            R.id.rb_ske_java -> {
                mBtn_content_process!!.setText(R.string.skeProcess_java)
                mTv_test!!.setText(R.string.skeProcess_java)
            }
        }
    }

    override fun onCardViewItemClick(view: View, position: Int) {
        when (position) {
            0 -> {
                var intent = Intent(this.context, RecognizeActivity::class.java)
                startActivity(intent)
            }
            1 -> {
                var intent = Intent(this.context, DisplayActivity::class.java)
                startActivity(intent)
            }
            2 -> {
                var intent = Intent(this.context, OpenCVActivity::class.java)
                startActivity(intent)
            }
            else -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.sw_item_setting_switch -> if (isChecked) {
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES, "夜间模式", true)
            } else {
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO, "正常模式", false)
            }
        }
    }

    private fun setDayNightMode(modeNightYes: Int, modeText: String, b: Boolean) {
        AppCompatDelegate.setDefaultNightMode(modeNightYes)
        activity!!.recreate()
        toast(modeText)
        Thread(Runnable {
            mSharedPreferences = activity!!.getSharedPreferences("myPreference", Context.MODE_PRIVATE)
            mSharedPreferences!!.edit().putBoolean(ISNIGHT, b).apply()
        }).start()
    }

    companion object {

        val SELECT_PIC_RESULT_CODE = 202
        private val ISNIGHT = "isNight"

        //单例模式
        fun newInstance(index: Int): MainFragment {
            val args = Bundle()
            val fragment = MainFragment()
            args.putInt("index", index)
            fragment.arguments = args
            return fragment
        }
    }
}