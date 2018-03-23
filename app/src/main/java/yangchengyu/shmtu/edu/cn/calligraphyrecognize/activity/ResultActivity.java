package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.Utils;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.DB.WordDBhelper;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.RollViewPagerAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment.MainFragment;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.ImageProcessUtils;

/**
 * Created by kimi9 on 2018/3/6.
 */

public class ResultActivity extends AppCompatActivity implements OnItemClickListener {

    private android.support.v7.widget.Toolbar mToolbar_result;
    private RollPagerView mRpv_result;
    private String mCroppedImgPath;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private NestedScrollView mNsv_result;
    private CardView mCardView_character;
    private TextView mTextView_word;
    private String mChar_word;
    private int mWidth;
    private int mHeight;
    private int mX;
    private int mY;
    private TextView mTv_word_width;
    private TextView mTv_word_height;
    private TextView mTv_word_x;
    private TextView mTv_word_y;
    private CardView mCardView_character_error;
    private int mId;
    private String mFromwhere;
    private List<Bitmap> mBitmapList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("onCreate");
        translucentSetting();
        setContentView(R.layout.activity_result);
        Utils.init(this);
        initUI();
        mFromwhere = this.getIntent().getStringExtra(MainFragment.FROMWHERE);
        if (mFromwhere.equals("recognize")) {
            initCharRecognize();
        } else if (mFromwhere.equals("main")) {
            initDetailFromFragment();
        }
        initRollViewPager();
    }

    private void initDetailFromFragment() {
        mChar_word = this.getIntent().getStringExtra(MainFragment.WORD);
        mWidth = this.getIntent().getIntExtra(MainFragment.WIDTH, 100);
        mHeight = this.getIntent().getIntExtra(MainFragment.HEIGHT, 100);
        mX = this.getIntent().getIntExtra(MainFragment.X_ARRAY, 0);
        mX = this.getIntent().getIntExtra(MainFragment.Y_ARRAY, 0);
        mCroppedImgPath = this.getIntent().getStringExtra(MainFragment.PIC_PATH);
        initWordCard();
    }

    private void initUI() {
        initContent();
        setActionBar();
        collapsingToolbarSetting();
    }

    private void initCharRecognize() {
        decodeJSON();
        if (mChar_word.equals("1001")) {
            mCardView_character.setVisibility(View.GONE);
            mCardView_character_error.setVisibility(View.VISIBLE);
            LogUtils.i("Nothing get from JSON");
        } else {
            initWordCard();
            saveWord();
        }
    }

    private void initWordCard() {
        mTextView_word.setText(mChar_word);
        mTv_word_width.setText(getString(R.string.result_character_recognize_width) + String.valueOf(mWidth));
        mTv_word_height.setText(getString(R.string.result_character_recognize_height) + String.valueOf(mHeight));
        mTv_word_x.setText(getString(R.string.result_character_recognize_x) + String.valueOf(mX));
        mTv_word_y.setText(getString(R.string.result_character_recognize_y) + String.valueOf(mY));
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
        mRpv_result.setOnItemClickListener((OnItemClickListener) this);
        mCollapsingToolbarLayout = findViewById(R.id.ctb_result);
        mNsv_result = findViewById(R.id.nsv_result);

        mCardView_character = findViewById(R.id.cardView_character);
        mCardView_character_error = findViewById(R.id.cardView_character_error);
        mTextView_word = findViewById(R.id.tv_result_word_recognize);
        mTv_word_width = findViewById(R.id.tv_result_char_width);
        mTv_word_height = findViewById(R.id.tv_result_char_height);
        mTv_word_x = findViewById(R.id.tv_result_char_left);
        mTv_word_y = findViewById(R.id.tv_result_char_top);
    }

    @NonNull
    private List<Bitmap> getBitmapList() {
        final List<Bitmap> bitmapList = new ArrayList<>();

        LogUtils.i(mCroppedImgPath);

        Bitmap croppedImg = BitmapFactory.decodeFile(mCroppedImgPath);
        Bitmap bintemp = BitmapFactory.decodeFile(mCroppedImgPath);
        Bitmap edgetemp = BitmapFactory.decodeFile(mCroppedImgPath);
        Bitmap sketemp = BitmapFactory.decodeFile(mCroppedImgPath);

        Bitmap binImg = ImageProcessUtils.binProcess(bintemp);
        Bitmap edgeImg = ImageProcessUtils.edgeProcess(edgetemp);
        Bitmap skeletonImg = ImageProcessUtils.skeletonFromJNI(sketemp);

        bitmapList.add(croppedImg);
        bitmapList.add(binImg);
        bitmapList.add(edgeImg);
        bitmapList.add(skeletonImg);

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
                //TODO: get the real style
                wordInfo.setStyle("楷体");
                WordDBhelper dBhelper = new WordDBhelper(getApplicationContext());
                dBhelper.addWord(wordInfo);
            }
        }).start();
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
                Toast.makeText(this, "原图", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, "二值化图像", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "轮廓图像", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this, "骨架化图像", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
