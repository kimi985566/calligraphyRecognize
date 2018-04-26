package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.DB.WordDBhelper;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.RollViewPagerAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.KNNDistance;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.KNNNode;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.CNNListener;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.CaffeMobile;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.CompareClass;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.Config;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.ImageProcessUtils;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.KNNUtils;

import static yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.KNNUtils.computeP;
import static yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.KNNUtils.maxP;
import static yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.KNNUtils.oudistance;

/**
 * 检验结果显示页面，用以展示书法识别结果
 */

public class ResultActivity extends AppCompatActivity implements OnItemClickListener, CNNListener {

    private static final int RECOGNIZE_COMPLETE = 1010;
    private android.support.v7.widget.Toolbar mToolbar_result;
    private Bitmap mBmp;
    private Bitmap mBinImg;
    private Bitmap mEdgeImg;
    private Bitmap mSkeletonImg;
    private Bitmap mCroppedImg;
    private String mFromWhere;
    private String mChar_word;
    private String mCroppedImgPath;
    private String mStyle;
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
    private TextView mTv_word_width_height;
    private TextView mTv_word_x_y;
    private TextView mTv_style;
    private CardView mCardView_character;
    private CardView mCardView_character_error;
    private CardView mCardView_style;
    private List<Bitmap> mBitmapList;
    private CaffeMobile mCaffeMobile;
    private ProgressDialog mProgressDialog;
    private BarChart mBarChart;
    private ArrayList<Map<Integer, Float>> mPair;
    private BarDataSet mBardataSet;
    private float mZuanScore;
    private float mLiScore;
    private float mKaiScore;
    private float mCaoScore;

    //加载动态库
    static {
        System.loadLibrary("caffe");
        System.loadLibrary("caffe_jni");
    }

    private Integer mX_native = new Integer(0);
    private Integer mY_native = new Integer(0);
    private TextView mTv_native_gravity;
    private TextView mTv_native_ratio;
    private TextView mTv_native_wh_ratio;
    private double mBinRatio;
    private double mWhRatio;
    private double mCenx;
    private double mCeny;
    private double mTempWidth;
    private double mTempHeight;
    private TextView mTv_native_cen_ratio;
    private TextView mTv_alg_style;
    private double mMostKNNPoints;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentSetting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Utils.init(this);
        LogUtils.i("onCreate");
        initUI();
        if (mFromWhere.equals("recognize")) {
            initCharRecognize();
        } else if (mFromWhere.equals("main")) {
            initDetailFromFragment();
        }
        initRollViewPager();
        initNativeConfigure();
    }

    //载入UI界面设置等
    private void initUI() {
        mFromWhere = this.getIntent().getStringExtra(RecognizeActivity.FROMWHERE);
        initContent();
        setMyActionBar();
        collapsingToolbarSetting();
    }

    //通过解析intent加载详情的文字信息
    private void initDetailFromFragment() {
        initIntentExtra();
        getWords();
        initBarChartDataFromMain();
        initWordCard();
        initBarChart();
    }

    private void initIntentExtra() {
        mChar_word = this.getIntent().getStringExtra(RecognizeActivity.WORD);
        mWidth = this.getIntent().getIntExtra(RecognizeActivity.WIDTH, 100);
        mHeight = this.getIntent().getIntExtra(RecognizeActivity.HEIGHT, 100);
        mX = this.getIntent().getIntExtra(RecognizeActivity.X_ARRAY, 0);
        mY = this.getIntent().getIntExtra(RecognizeActivity.Y_ARRAY, 0);
        mStyle = this.getIntent().getStringExtra(RecognizeActivity.STYLE);
        mCroppedImgPath = this.getIntent().getStringExtra(RecognizeActivity.PIC_PATH);
        mZuanScore = this.getIntent().getFloatExtra(RecognizeActivity.ZUAN, 0f);
        mLiScore = this.getIntent().getFloatExtra(RecognizeActivity.LI, 0f);
        mKaiScore = this.getIntent().getFloatExtra(RecognizeActivity.KAI, 0f);
        mCaoScore = this.getIntent().getFloatExtra(RecognizeActivity.CAO, 0f);
    }

    private void initBarChartDataFromMain() {
        float[] floats = new float[]{mZuanScore, mLiScore, mKaiScore, mCaoScore};

        mPair = new ArrayList<>();
        Map<Integer, Float> map = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            map.put(i, floats[i]);
            mPair.add(map);
        }
    }

    //加载回调的检验结果
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

    //识别文字信息
    private void recognizeImg() {
        setProgressBar();
        initCaffe();
        executeImg();
    }

    private void setProgressBar() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//转盘
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("正在识别中");
        mProgressDialog.show();
    }

    //设置TextView文字信息
    private void initWordCard() {
        mTextView_word.setText(mChar_word);
        mTv_word_width_height.setText(getString(R.string.result_character_recognize_width) + String.valueOf(mWidth) + "*" + String.valueOf(mHeight));
        mTv_word_x_y.setText(getString(R.string.result_character_recognize_x) + "(" + String.valueOf(mX) + "," + String.valueOf(mY) + ")");
        mTv_style.setText(getString(R.string.result_character_style) + mStyle);
    }

    //加载滚动显示页面
    private void initRollViewPager() {
        mBitmapList = getBitmapList();
        mRpv_result.setAdapter(new RollViewPagerAdapter(mBitmapList));
        mNsv_result.setFillViewport(true);
    }

    //解析JSON字符串
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

    //findViewById
    private void initContent() {
        mToolbar_result = findViewById(R.id.toolBar_result);
        mRpv_result = findViewById(R.id.rpv_result);
        mRpv_result.setOnItemClickListener(this);
        mCollapsingToolbarLayout = findViewById(R.id.ctb_result);
        mNsv_result = findViewById(R.id.nsv_result);
        mBarChart = findViewById(R.id.barchart_result);

        mCardView_character = findViewById(R.id.cardView_character);
        mCardView_character_error = findViewById(R.id.cardView_character_error);
        mCardView_style = findViewById(R.id.cardView_style);
        mTextView_word = findViewById(R.id.tv_result_word_recognize);
        mTv_word_width_height = findViewById(R.id.tv_result_char_width);
        mTv_word_x_y = findViewById(R.id.tv_result_char_left);
        mTv_style = findViewById(R.id.tv_result_style);
        mTv_alg_style = findViewById(R.id.tv_result_alg_style);

        mTv_native_gravity = findViewById(R.id.tv_result_native_gravity);
        mTv_native_ratio = findViewById(R.id.tv_result_native_ratio);
        mTv_native_wh_ratio = findViewById(R.id.tv_result_native_wh_ratio);
        mTv_native_cen_ratio = findViewById(R.id.tv_result_native_cen_ratio);
    }

    private void initBarChartData() {
        ArrayList<BarEntry> barEntriesData = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            barEntriesData.add(new BarEntry(i, mPair.get(i - 1).get(i - 1)));
        }

        mBardataSet = new BarDataSet(barEntriesData, "");

        ArrayList<IBarDataSet> iBarDataSets = new ArrayList<>();
        iBarDataSets.add(mBardataSet);

        BarData barData = new BarData(iBarDataSets);

        mBarChart.setData(barData);
    }

    private void initBarChart() {

        initBarChartData();

        mBarChart.setDescription(null);
        mBarChart.setPinchZoom(false);
        mBarChart.setFitBars(true);
        mBarChart.setFitsSystemWindows(true);
        mBarChart.setMaxVisibleValueCount(4);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        xAxis.setTextSize(11f);
        xAxis.setLabelCount(4, false);
        xAxis.setValueFormatter(new XFormattedValue(ResultActivity.this));

        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setLabelCount(4, true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        mBarChart.getAxisRight().setEnabled(false);
        Legend legend = mBarChart.getLegend();
        legend.setEnabled(false);

        mBarChart.setTouchEnabled(true);
        mBarChart.animateY(3000);

        mBarChart.invalidate();
    }

    //处理图片并显示
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

    private void initNativeConfigure() {

        Bitmap gravityTemp = BitmapFactory.decodeFile(mCroppedImgPath);
        Bitmap binRatioTemp = BitmapFactory.decodeFile(mCroppedImgPath);

        //求重心
        ImageProcessUtils.imageGravityJava(gravityTemp, mX_native, mY_native);
        //求黑白像素比
        mBinRatio = ImageProcessUtils.imageBinaryRatio(binRatioTemp);
        //求图像宽高比
        mWhRatio = ImageProcessUtils.imageWHRatio(mCroppedImg);
        //求重心比
        getCenXY(gravityTemp);

        mTv_native_gravity.setText("通过算法求得文字重心：(" + mX_native + "," + mY_native + ")");
        mTv_native_ratio.setText("黑白像素比：" + Config.doubleToString(mBinRatio));
        mTv_native_wh_ratio.setText("高宽比(高/宽)：" + Config.doubleToString(mWhRatio));
        mTv_native_cen_ratio.setText("重心占图像比例：(" + Config.doubleToString(mCenx) + "," + Config.doubleToString(mCeny) + ")");

        getAlgStyle();
    }

    /**
     * KNN步骤
     * <p>
     * 1. 输入所有已知点
     * 2. 输入未知点
     * 3. 计算所有已知点到未知点的欧式距离，并根据距离对所有已知点排序
     * 4. 选取最近的k个点
     * 5. 计算k个点所在分类出现的频率
     */

    private void getAlgStyle() {

        ArrayList<KNNNode> dataList = KNNUtils.getCatList();

        KNNNode x = new KNNNode(5, mCenx, mCeny, mBinRatio);

        Set<KNNDistance> distanceSet = getKNNDistances(dataList, x);

        mMostKNNPoints = 4;

        // 1、计算每个分类所包含的点的个数
        ArrayList<KNNDistance> distanceList = new ArrayList<>(distanceSet);
        Map<String, Integer> map = KNNUtils.getNumberOfType(distanceList, dataList, mMostKNNPoints);

        // 2、计算频率
        Map<String, Double> p = computeP(map, mMostKNNPoints);

        x.setType(maxP(p));

        LogUtils.i("KNN result：" + x.getType());

        mTv_alg_style.setText("KNN分类的书法结果：" + x.getType());
    }

    @NonNull
    private Set<KNNDistance> getKNNDistances(ArrayList<KNNNode> dataList, KNNNode x) {
        CompareClass compare = new CompareClass();
        Set<KNNDistance> distanceSet = new TreeSet<>(compare);
        for (KNNNode point : dataList) {
            distanceSet.add(new KNNDistance(point.getId(), x.getId(), oudistance(point, x)));
        }
        return distanceSet;
    }

    private void getCenXY(Bitmap gravityTemp) {
        Mat src = new Mat();
        org.opencv.android.Utils.bitmapToMat(gravityTemp, src);
        mTempWidth = src.width();
        mTempHeight = src.height();

        mCenx = mX_native / mTempWidth;
        mCeny = mY_native / mTempHeight;
    }

    //设置ActionBar样式
    private void setMyActionBar() {
        setSupportActionBar(mToolbar_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    //保存图片
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
                wordInfo.setZuanScore(mPair.get(0).get(0));
                wordInfo.setLiScore(mPair.get(1).get(1));
                wordInfo.setKaiScore(mPair.get(2).get(2));
                wordInfo.setCaoScore(mPair.get(3).get(3));
                WordDBhelper dBhelper = new WordDBhelper(getApplicationContext());
                dBhelper.addWord(wordInfo);
            }
        }).start();
    }

    //加载Caffe库文件
    private void initCaffe() {
        mCaffeMobile = new CaffeMobile();
        mCaffeMobile.setNumThreads(4);
        mCaffeMobile.loadModel(Config.modelProto, Config.modelBinary);
        mCaffeMobile.setMean(meanValues);

        getWords();
    }

    //读取分类信息文档
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

    //分析识别图片
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

    //状态栏透明设置
    private void translucentSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    //收缩状态栏的设置
    private void collapsingToolbarSetting() {
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    //显示图片信息
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
            float[] floats = mCaffeMobile.getConfidenceScore(strings[0]);

            mPair = new ArrayList<>();
            Map<Integer, Float> map = new HashMap<>();
            for (int i = 0; i < 4; i++) {
                int num = mCaffeMobile.predictImage(strings[0])[i];
                map.put(num, floats[num]);
                mPair.add(map);
            }
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
        message.what = RECOGNIZE_COMPLETE;
        mHandler.sendMessage(message);
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECOGNIZE_COMPLETE:
                    if (!mChar_word.equals("1001")) {
                        saveWord();
                    }
                    initBarChart();
                    break;
            }
        }
    };

    class XFormattedValue implements IAxisValueFormatter {

        private Context mContext;

        public XFormattedValue(Context context) {
            mContext = context;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value == 1) {
                return IMAGENET_CLASSES[0];
            } else if (value == 2) {
                return IMAGENET_CLASSES[1];
            } else if (value == 3) {
                return IMAGENET_CLASSES[2];
            } else {
                return IMAGENET_CLASSES[3];
            }
        }
    }
}