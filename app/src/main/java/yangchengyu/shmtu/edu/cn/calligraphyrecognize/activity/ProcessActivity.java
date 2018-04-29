package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.OpenCVConstants;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.CvImgProcessUtils;

public class ProcessActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private String processName;
    private Button mBtnSelect;
    private Button mBtnProcess;
    private ImageView mIvProcess;
    private Bitmap mBitmap;
    public static final int SELECT_PIC_RESULT_CODE = 202;
    private int maxSize = 1024;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_process);

        initView();
        actionBarSetting();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar_sub);
        mBtnSelect = findViewById(R.id.btn_select);
        mBtnProcess = findViewById(R.id.btn_process);
        mIvProcess = findViewById(R.id.iv_process);
        processName = this.getIntent().getStringExtra("name");
        mBtnProcess.setText(processName);

        mBtnProcess.setOnClickListener(this);
        mBtnSelect.setOnClickListener(this);

        mBitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_image_test);

        mIvProcess.setImageBitmap(mBitmap);
    }

    private void actionBarSetting() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(processName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_process:
                select2Process();
                break;
            case R.id.btn_select:
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickIntent, "Browser Image……"),
                        SELECT_PIC_RESULT_CODE);
                break;
        }
    }

    private void select2Process() {
        Bitmap temp = mBitmap.copy(mBitmap.getConfig(), true);
        if (OpenCVConstants.GRAY_TEST_NAME.equals(processName)) {
            temp = CvImgProcessUtils.covert2Gray(temp);
        } else if (OpenCVConstants.MAT_PIXEL_INVERT_NAME.equals(processName)) {
            temp = CvImgProcessUtils.invertMat(temp);
        } else if (OpenCVConstants.BITMAP_PIXEL_INVERT_NAME.equals(processName)) {
            temp = CvImgProcessUtils.invertBitmap(temp);
        } else if (OpenCVConstants.CONTRAST_RATIO_BRIGHTNESS_NAME.equals(processName)) {
            CvImgProcessUtils.contrast_ratio_adjust(temp);
        } else if (OpenCVConstants.IMAGE_CONTAINER_MAT_NAME.equals(processName)) {
            CvImgProcessUtils.mat_operation(temp);
        } else if (OpenCVConstants.GET_ROI_NAME.equals(processName)) {
            temp = CvImgProcessUtils.getRoi(temp);
        } else if (OpenCVConstants.BOX_BLUR_IMAGE_NAME.equals(processName)) {
            CvImgProcessUtils.boxBlur(temp);
        } else if (OpenCVConstants.GAUSSIAN_BLUR_IMAGE_NAME.equals(processName)) {
            CvImgProcessUtils.gaussianBlur(temp);
        } else if (OpenCVConstants.BILATERAL_BLUR_IMAGE_NAME.equals(processName)) {
            CvImgProcessUtils.bilBlur(temp);
        } else if (OpenCVConstants.CUSTOM_BLUR_NAME.equals(processName)
                || OpenCVConstants.CUSTOM_EDGE_NAME.equals(processName)
                || OpenCVConstants.CUSTOM_SHARPEN_NAME.equals(processName)) {
            CvImgProcessUtils.customFilter(processName, temp);
        } else if (OpenCVConstants.ERODE_NAME.equals(processName)
                || OpenCVConstants.DILATE_NAME.equals(processName)) {
            CvImgProcessUtils.erodeOrDilate(processName, temp);
        } else if (OpenCVConstants.OPEN_OPERATION_NAME.equals(processName)
                || OpenCVConstants.CLOSE_OPERATION_NAME.equals(processName)) {
            CvImgProcessUtils.openOrClose(processName, temp);
        } else if (OpenCVConstants.MORPH_LINE_OPERATION_NAME.equals(processName)) {
            CvImgProcessUtils.lineDetection(temp);
        } else if (OpenCVConstants.THRESH_BINARY_NAME.equals(processName)
                || OpenCVConstants.THRESH_BINARY_INV_NAME.equals(processName)
                || OpenCVConstants.THRESH_TRUNCAT_NAME.equals(processName)
                || OpenCVConstants.THRESH_ZERO_NAME.equals(processName)) {
            CvImgProcessUtils.thresholdImg(processName, temp);
        } else if (OpenCVConstants.ADAPTIVE_THRESH_MEAN_NAME.equals(processName)
                || OpenCVConstants.ADAPTIVE_THRESH_GAUSSIAN_NAME.equals(processName)) {
            CvImgProcessUtils.adaptiveThresholdImg(processName, temp);
        } else if (OpenCVConstants.HISTOGRAM_EQ_NAME.equals(processName)) {
            CvImgProcessUtils.histogramEq(temp);
        } else if (OpenCVConstants.GRADIENT_SOBEL_X_NAME.equals(processName)
                || OpenCVConstants.GRADIENT_SOBEL_Y_NAME.equals(processName)) {
            CvImgProcessUtils.gradientProcess(processName, temp);
        } else if (OpenCVConstants.GRADIENT_IMG_NAME.equals(processName)) {
            CvImgProcessUtils.gradientXY(temp);
        }

        mIvProcess.setImageBitmap(temp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PIC_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(inputStream, null, options);

                int height = options.outHeight;
                int width = options.outWidth;
                int sampleSize = 1;
                int max = Math.max(height, width);

                if (max > maxSize) {
                    int nw = width / 2;
                    int nh = height / 2;
                    while ((nw / sampleSize) > maxSize || (nh / sampleSize) > maxSize) {
                        sampleSize *= 2;
                    }
                }

                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                mBitmap = BitmapFactory.decodeStream(getContentResolver().
                        openInputStream(uri), null, options);
                mIvProcess.setImageBitmap(mBitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
