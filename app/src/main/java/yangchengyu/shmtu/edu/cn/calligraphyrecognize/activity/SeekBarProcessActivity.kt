package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.OpenCVConstants
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.CvImgProcessUtils
import java.io.FileNotFoundException

/**
 *带有拖动条的显示页面
 *
 * */
class SeekBarProcessActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seek_bar_process)
        initView()
        actionBarSetting()
    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar_sub)
        mBtnSelect = findViewById(R.id.btn_thresh_select)
        mBtnProcess = findViewById(R.id.btn_thresh_process)
        mThreshSeekBar = findViewById(R.id.thresh_seek_bar)
        mIvThreshProcess = findViewById(R.id.iv_thresh_process)
        mTvThresh = findViewById(R.id.tv_thresh)

        processName = this.intent.getStringExtra("name")

        mBtnSelect!!.setOnClickListener(this)
        mBtnProcess!!.setOnClickListener(this)

        mBtnProcess!!.text = processName
        mBitmap = BitmapFactory.decodeResource(this.resources, R.drawable.ic_image_test)
        mThreshSeekBar!!.setOnSeekBarChangeListener(this)//进度条
    }

    private fun actionBarSetting() {
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = processName
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    //选择对应的操作方式
    private fun select2Process(value: Int) {
        val temp = mBitmap!!.copy(mBitmap!!.config, true)
        if (OpenCVConstants.MANUAL_THRESH_NAME == processName) {
            CvImgProcessUtils.manualThresholdImg(value, temp)
        } else if (OpenCVConstants.CANNY_NAME == processName) {
            CvImgProcessUtils.cannyProcess(value, temp)
        }
        mIvThreshProcess!!.setImageBitmap(temp)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_thresh_select -> {
                //选择图片
                val pickIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickIntent.type = "image/*"
                pickIntent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(pickIntent, "Browser Image……"),
                        SELECT_PIC_RESULT_CODE)
            }
            R.id.btn_thresh_process -> {
                mTvThresh!!.text = "当前阈值：$mValue"
                select2Process(mValue)
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        mValue = mThreshSeekBar!!.progress
        mTvThresh!!.text = "当前阈值：$mValue"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PIC_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                BitmapFactory.decodeStream(inputStream, null, options)

                val height = options.outHeight
                val width = options.outWidth
                var sampleSize = 1
                val max = Math.max(height, width)

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

                mBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)
                mIvThreshProcess!!.setImageBitmap(mBitmap)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }
    }

    companion object {
        const val SELECT_PIC_RESULT_CODE = 202
        private var mToolbar: Toolbar? = null
        private var mBtnSelect: Button? = null
        private var mBtnProcess: Button? = null
        private var mThreshSeekBar: SeekBar? = null
        private var mIvThreshProcess: ImageView? = null
        private var mTvThresh: TextView? = null
        private const val maxSize = 1024
        private var processName: String? = null
        private var mValue: Int = 0
        private var mBitmap: Bitmap? = null
    }
}
