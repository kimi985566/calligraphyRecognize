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
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.OpenCVConstants;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.CvImgProcessUtils;

public class SeekBarProcessActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private Toolbar mToolbar;
    private Button mBtnSelect;
    private Button mBtnProcess;
    private SeekBar mThreshSeekBar;
    private ImageView mIvThreshProcess;
    private TextView mTvThresh;
    public static final int SELECT_PIC_RESULT_CODE = 202;
    private int maxSize = 1024;
    private String processName;
    private int mValue;
    private Bitmap mBitmap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_bar_process);

        initView();
        actionBarSetting();
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar_sub);
        mBtnSelect = findViewById(R.id.btn_thresh_select);
        mBtnProcess = findViewById(R.id.btn_thresh_process);
        mThreshSeekBar = findViewById(R.id.thresh_seek_bar);
        mIvThreshProcess = findViewById(R.id.iv_thresh_process);
        mTvThresh = findViewById(R.id.tv_thresh);

        processName = this.getIntent().getStringExtra("name");

        mBtnSelect.setOnClickListener(this);
        mBtnProcess.setOnClickListener(this);

        mBtnProcess.setText(processName);
        mBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_image_test);
        mThreshSeekBar.setOnSeekBarChangeListener(this);
    }

    private void actionBarSetting() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(processName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void select2Process(int value) {
        Bitmap temp = mBitmap.copy(mBitmap.getConfig(), true);
        if (OpenCVConstants.MANUAL_THRESH_NAME.equals(processName)) {
            CvImgProcessUtils.manualThresholdImg(value, temp);
        } else if (OpenCVConstants.CANNY_NAME.equals(processName)) {
            CvImgProcessUtils.cannyProcess(value, temp);
        }
        mIvThreshProcess.setImageBitmap(temp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_thresh_select:
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickIntent, "Browser Image……"),
                        SELECT_PIC_RESULT_CODE);
                break;
            case R.id.btn_thresh_process:
                mTvThresh.setText("当前阈值：" + mValue);
                select2Process(mValue);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mValue = mThreshSeekBar.getProgress();
        mTvThresh.setText("当前阈值：" + mValue);
        select2Process(mValue);
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
                mIvThreshProcess.setImageBitmap(mBitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
