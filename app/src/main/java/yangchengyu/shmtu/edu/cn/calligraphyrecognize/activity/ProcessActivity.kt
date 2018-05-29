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
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.OpenCVConstants
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.CvImgProcessUtils
import java.io.FileNotFoundException

class ProcessActivity : AppCompatActivity(), View.OnClickListener {

    private var mToolbar: Toolbar? = null
    private var processName: String? = null
    private var mBtnSelect: Button? = null
    private var mBtnProcess: Button? = null
    private var mIvProcess: ImageView? = null
    private var mBitmap: Bitmap? = null
    private val maxSize = 1024

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_process)

        initView()
        actionBarSetting()
    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar_sub)
        mBtnSelect = findViewById(R.id.btn_select)
        mBtnProcess = findViewById(R.id.btn_process)
        mIvProcess = findViewById(R.id.iv_process)
        processName = this.intent.getStringExtra("name")
        mBtnProcess!!.text = processName

        mBtnProcess!!.setOnClickListener(this)
        mBtnSelect!!.setOnClickListener(this)

        mBitmap = BitmapFactory.decodeResource(this.resources,
                R.drawable.ic_image_test)

        mIvProcess!!.setImageBitmap(mBitmap)
    }

    private fun actionBarSetting() {
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = processName
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_process -> select2Process()
            R.id.btn_select -> {
                val pickIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickIntent.type = "image/*"
                pickIntent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(pickIntent, "Browser Image……"),
                        SELECT_PIC_RESULT_CODE)
            }
        }
    }

    private fun select2Process() {
        var temp = mBitmap!!.copy(mBitmap!!.config, true)
        if (OpenCVConstants.GRAY_TEST_NAME == processName) {
            temp = CvImgProcessUtils.covert2Gray(temp)
        } else if (OpenCVConstants.MAT_PIXEL_INVERT_NAME == processName) {
            temp = CvImgProcessUtils.invertMat(temp)
        } else if (OpenCVConstants.BITMAP_PIXEL_INVERT_NAME == processName) {
            temp = CvImgProcessUtils.invertBitmap(temp)
        } else if (OpenCVConstants.CONTRAST_RATIO_BRIGHTNESS_NAME == processName) {
            CvImgProcessUtils.contrast_ratio_adjust(temp)
        } else if (OpenCVConstants.IMAGE_CONTAINER_MAT_NAME == processName) {
            CvImgProcessUtils.mat_operation(temp)
        } else if (OpenCVConstants.GET_ROI_NAME == processName) {
            temp = CvImgProcessUtils.getRoi(temp)
        } else if (OpenCVConstants.BOX_BLUR_IMAGE_NAME == processName) {
            CvImgProcessUtils.boxBlur(temp)
        } else if (OpenCVConstants.GAUSSIAN_BLUR_IMAGE_NAME == processName) {
            CvImgProcessUtils.gaussianBlur(temp)
        } else if (OpenCVConstants.BILATERAL_BLUR_IMAGE_NAME == processName) {
            CvImgProcessUtils.bilBlur(temp)
        } else if (OpenCVConstants.CUSTOM_BLUR_NAME == processName
                || OpenCVConstants.CUSTOM_EDGE_NAME == processName
                || OpenCVConstants.CUSTOM_SHARPEN_NAME == processName) {
            CvImgProcessUtils.customFilter(processName, temp)
        } else if (OpenCVConstants.ERODE_NAME == processName || OpenCVConstants.DILATE_NAME == processName) {
            CvImgProcessUtils.erodeOrDilate(processName, temp)
        } else if (OpenCVConstants.OPEN_OPERATION_NAME == processName || OpenCVConstants.CLOSE_OPERATION_NAME == processName) {
            CvImgProcessUtils.openOrClose(processName, temp)
        } else if (OpenCVConstants.MORPH_LINE_OPERATION_NAME == processName) {
            CvImgProcessUtils.lineDetection(temp)
        } else if (OpenCVConstants.THRESH_BINARY_NAME == processName
                || OpenCVConstants.THRESH_BINARY_INV_NAME == processName
                || OpenCVConstants.THRESH_TRUNCAT_NAME == processName
                || OpenCVConstants.THRESH_ZERO_NAME == processName) {
            CvImgProcessUtils.thresholdImg(processName, temp)
        } else if (OpenCVConstants.ADAPTIVE_THRESH_MEAN_NAME == processName || OpenCVConstants.ADAPTIVE_THRESH_GAUSSIAN_NAME == processName) {
            CvImgProcessUtils.adaptiveThresholdImg(processName, temp)
        } else if (OpenCVConstants.HISTOGRAM_EQ_NAME == processName) {
            CvImgProcessUtils.histogramEq(temp)
        } else if (OpenCVConstants.GRADIENT_SOBEL_X_NAME == processName || OpenCVConstants.GRADIENT_SOBEL_Y_NAME == processName) {
            CvImgProcessUtils.gradientProcess(processName, temp)
        } else if (OpenCVConstants.GRADIENT_IMG_NAME == processName) {
            CvImgProcessUtils.gradientXY(temp)
        }

        mIvProcess!!.setImageBitmap(temp)
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
                mIvProcess!!.setImageBitmap(mBitmap)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }
    }

    companion object {
        val SELECT_PIC_RESULT_CODE = 202
    }
}
