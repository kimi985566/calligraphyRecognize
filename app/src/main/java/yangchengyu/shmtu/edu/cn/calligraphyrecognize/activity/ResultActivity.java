package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.DB.WordDBhelper;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.RollViewPagerAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment.MainFragment;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.CNNListener;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.CaffeMobile;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.Config;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.ImageProcessUtils;

/**
 * Created by kimi9 on 2018/3/6.
 */

public class ResultActivity extends AppCompatActivity implements OnItemClickListener, CNNListener {

    private static final int RECOGNIZE_COPLETE = 1010;
    private android.support.v7.widget.Toolbar mToolbar_result;
    private Bitmap mBmp;
    private String mFromwhere;
    private String mChar_word;
    private String mCroppedImgPath;
    private static String[] IMAGENET_CLASSES;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private NestedScrollView mNsv_result;
    private RollPagerView mRpv_result;
    private int mWidth;
    private int mHeight;
    private int mX;
    private int mY;
    private int mId;
    private float[] meanValues = {183, 184, 185};
    private TextView mTextView_word;
    private TextView mTv_word_width;
    private TextView mTv_word_height;
    private TextView mTv_word_x;
    private TextView mTv_word_y;
    private TextView mTv_style;
    private CardView mCardView_character;
    private CardView mCardView_character_error;
    private CardView mCardView_style;
    private List<Bitmap> mBitmapList;
    private CaffeMobile mCaffeMobile;

    //加载动态库
    static {
        System.loadLibrary("caffe");
        System.loadLibrary("caffe_jni");
    }

    private String mStyle;
    private ProgressDialog mProgressDialog;
    private Bitmap mBinImg;
    private Bitmap mEdgeImg;
    private Bitmap mSkeletonImg;
    private Bitmap mCroppedImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        translucentSetting();
        setContentView(R.layout.activity_result);
        Utils.init(this);
        LogUtils.i("onCreate");
        initUI();
        if (mFromwhere.equals("recognize")) {
            initCharRecognize();
        } else if (mFromwhere.equals("main")) {
            initDetailFromFragment();
        }
        initRollViewPager();
    }

    private void initUI() {
        mFromwhere = this.getIntent().getStringExtra(MainFragment.FROMWHERE);
        initContent();
        setActionBar();
        collapsingToolbarSetting();
    }

    private void initDetailFromFragment() {
        mChar_word = this.getIntent().getStringExtra(MainFragment.WORD);
        mWidth = this.getIntent().getIntExtra(MainFragment.WIDTH, 100);
        mHeight = this.getIntent().getIntExtra(MainFragment.HEIGHT, 100);
        mX = this.getIntent().getIntExtra(MainFragment.X_ARRAY, 0);
        mX = this.getIntent().getIntExtra(MainFragment.Y_ARRAY, 0);
        mStyle = this.getIntent().getStringExtra(MainFragment.STYLE);
        mCroppedImgPath = this.getIntent().getStringExtra(MainFragment.PIC_PATH);
        initWordCard();
    }

    private void initCharRecognize() {
        decodeJSON();
        if (mChar_word.equals("1001")) {
            mCardView_character.setVisibility(View.GONE);
            mCardView_character_error.setVisibility(View.VISIBLE);
            LogUtils.i("Nothing get from JSON");
            recognizeImg();
        } else {
            initWordCard();
            recognizeImg();
        }
    }

    private void recognizeImg() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//转盘
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("正在识别中");
        mProgressDialog.show();
        initCaffe();
        executeImg();
    }

    private void initWordCard() {
        mTextView_word.setText(mChar_word);
        mTv_word_width.setText(getString(R.string.result_character_recognize_width) + String.valueOf(mWidth));
        mTv_word_height.setText(getString(R.string.result_character_recognize_height) + String.valueOf(mHeight));
        mTv_word_x.setText(getString(R.string.result_character_recognize_x) + String.valueOf(mX));
        mTv_word_y.setText(getString(R.string.result_character_recognize_y) + String.valueOf(mY));
        mTv_style.setText(getString(R.string.result_character_style) + mStyle);
    }

    private void initRollViewPager() {
        mBitmapList = getBitmapList();
        mRpv_result.setAdapter(new RollViewPagerAdapter(mBitmapList));
        mNsv_result.setFillViewport(true);
    }

    private void decodeJSON() {
        mCroppedImgPath = this.getIntent().getStringExtra("cropImgPath");
        String JSON = this.getIntent().getStringExtra("JSON");
        LogUtils.json(JSON);
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            mId = jsonObject.getInt("log_id");
            JSONArray word_result = jsonObject.getJSONArray("words_result");
            JSONArray char_detail = word_result.getJSONObject(0).getJSONArray("chars");
            JSONObject chars = char_detail.getJSONObject(0);
            mChar_word = chars.getString("char");
            JSONObject location = chars.getJSONObject("location");
            mWidth = location.getInt("width");
            mHeight = location.getInt("height");
            mX = location.getInt("left");
            mY = location.getInt("top");
            LogUtils.i(mId, mChar_word, mWidth, mHeight, mX, mY);
        } catch (JSONException e) {
            e.printStackTrace();
            mChar_word = String.valueOf(1001);
        }
    }

    private void initContent() {
        mToolbar_result = findViewById(R.id.toolBar_result);
        mRpv_result = findViewById(R.id.rpv_result);
        mRpv_result.setOnItemClickListener(this);
        mCollapsingToolbarLayout = findViewById(R.id.ctb_result);
        mNsv_result = findViewById(R.id.nsv_result);

        mCardView_character = findViewById(R.id.cardView_character);
        mCardView_character_error = findViewById(R.id.cardView_character_error);
        mCardView_style = findViewById(R.id.cardView_style);
        mTextView_word = findViewById(R.id.tv_result_word_recognize);
        mTv_word_width = findViewById(R.id.tv_result_char_width);
        mTv_word_height = findViewById(R.id.tv_result_char_height);
        mTv_word_x = findViewById(R.id.tv_result_char_left);
        mTv_word_y = findViewById(R.id.tv_result_char_top);
        mTv_style = findViewById(R.id.tv_result_style);
    }

    @NonNull
    private List<Bitmap> getBitmapList() {
        final List<Bitmap> bitmapList = new ArrayList<>();

        LogUtils.i(mCroppedImgPath);

        mCroppedImg = BitmapFactory.decodeFile(mCroppedImgPath);
        Bitmap bintemp = BitmapFactory.decodeFile(mCroppedImgPath);
        Bitmap edgetemp = BitmapFactory.decodeFile(mCroppedImgPath);
        Bitmap sketemp = BitmapFactory.decodeFile(mCroppedImgPath);

        mBinImg = ImageProcessUtils.binProcess(bintemp);
        mEdgeImg = ImageProcessUtils.edgeProcess(edgetemp);
        mSkeletonImg = ImageProcessUtils.skeletonFromJNI(sketemp);

        bitmapList.add(mCroppedImg);
        bitmapList.add(mBinImg);
        bitmapList.add(mEdgeImg);
        bitmapList.add(mSkeletonImg);

        return bitmapList;
    }

    private void setActionBar() {
        setSupportActionBar(mToolbar_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void saveWord() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WordInfo wordInfo = new WordInfo();
                wordInfo.setId(mId);
                wordInfo.setWord(mChar_word);
                wordInfo.setWidth(mWidth);
                wordInfo.setHeight(mHeight);
                wordInfo.setX_array(mX);
                wordInfo.setY_array(mY);
                wordInfo.setPic_path(mCroppedImgPath);
                wordInfo.setStyle(mStyle);
                WordDBhelper dBhelper = new WordDBhelper(getApplicationContext());
                dBhelper.addWord(wordInfo);
            }
        }).start();
    }

    private void initCaffe() {
        mCaffeMobile = new CaffeMobile();
        mCaffeMobile.setNumThreads(4);
        mCaffeMobile.loadModel(Config.modelProto, Config.modelBinary);
        mCaffeMobile.setMean(meanValues);

        getWords();
    }

    private void getWords() {
        AssetManager am = this.getAssets();
        try {
            InputStream is = am.open("words.txt");
            Scanner sc = new Scanner(is);
            List<String> lines = new ArrayList<>();
            while (sc.hasNextLine()) {
                final String temp = sc.nextLine();
                lines.add(temp.substring(temp.indexOf(" ") + 1));
            }
            IMAGENET_CLASSES = lines.toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeImg() {
        File imgFile = new File(mCroppedImgPath);
        mBmp = BitmapFactory.decodeFile(imgFile.getPath());
        CNNTask cnnTask = new CNNTask(ResultActivity.this);
        if (imgFile.exists()) {
            cnnTask.execute(imgFile.getPath());
        } else {
            LogUtils.e("file is not exist");
        }
    }

    private void translucentSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //此FLAG可使状态栏透明，且当前视图在绘制时，从屏幕顶端开始即top = 0开始绘制，这也是实现沉浸效果的基础
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//可不加
        }
    }

    private void collapsingToolbarSetting() {
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                SnackbarUtils.with(mCardView_character)
                        .setMessage("原图")
                        .setDuration(SnackbarUtils.LENGTH_SHORT)
                        .showSuccess();
                break;
            case 1:
                SnackbarUtils.with(mCardView_character)
                        .setMessage("二值化图像")
                        .setDuration(SnackbarUtils.LENGTH_SHORT)
                        .showSuccess();
                break;
            case 2:
                SnackbarUtils.with(mCardView_character)
                        .setMessage("轮廓图像")
                        .setDuration(SnackbarUtils.LENGTH_SHORT)
                        .showSuccess();
                break;
            case 3:
                SnackbarUtils.with(mCardView_character)
                        .setMessage("骨架化图像")
                        .setDuration(SnackbarUtils.LENGTH_SHORT)
                        .showSuccess();
                break;
        }
    }

    private class CNNTask extends AsyncTask<String, Void, Integer> {

        private CNNListener listener;
        private long startTime;

        public CNNTask(CNNListener listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            startTime = SystemClock.uptimeMillis();
            return mCaffeMobile.predictImage(strings[0])[0];
        }

        @Override
        protected void onPostExecute(Integer integer) {
            LogUtils.i(String.format("elapsed wall time: %d ms", SystemClock.uptimeMillis() - startTime));
            listener.onTaskCompleted(integer);
            super.onPostExecute(integer);
        }
    }

    @Override
    public void onTaskCompleted(int result) {
        mStyle = IMAGENET_CLASSES[result];
        mTv_style.setText(getString(R.string.result_character_style) + mStyle);
        mProgressDialog.dismiss();
        Message message = new Message();
        message.what = RECOGNIZE_COPLETE;
        mHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECOGNIZE_COPLETE:
                    if (!mChar_word.equals("1001")) {
                        saveWord();
                    }
                    break;
            }
        }
    };
}
