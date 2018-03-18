package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity;

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

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.jude.rollviewpager.RollPagerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.RollViewPagerAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.ImageProcessUtils;

/**
 * Created by kimi9 on 2018/3/6.
 */

public class ResultActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        translucentSetting();
        setContentView(R.layout.activity_result);
        Utils.init(this);

        initUI();
        setActionBar();
        collapsingToolbarSetting();

        initRollViewPager();

        initCharRecognize();


    }

    private void initCharRecognize() {
        decodeJSON();

        mTextView_word.setText(mChar_word);
        mTv_word_width.setText(String.valueOf(mWidth));
        mTv_word_height.setText(String.valueOf(mHeight));
        mTv_word_x.setText(String.valueOf(mX));
        mTv_word_y.setText(String.valueOf(mY));
    }

    private void initRollViewPager() {
        List<Bitmap> bitmapList = getBitmapList();
        mRpv_result.setAdapter(new RollViewPagerAdapter(bitmapList));
        mNsv_result.setFillViewport(true);
    }

    private void decodeJSON() {
        String JSON = this.getIntent().getStringExtra("JSON");
        LogUtils.json(JSON);
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            int direction = jsonObject.getInt("direction");
            JSONArray word_result = jsonObject.getJSONArray("words_result");
            JSONArray char_detail = word_result.getJSONObject(0).getJSONArray("chars");
            JSONObject chars = char_detail.getJSONObject(0);
            mChar_word = chars.getString("char");
            JSONObject location = chars.getJSONObject("location");
            mWidth = location.getInt("width");
            mHeight = location.getInt("height");
            mX = location.getInt("left");
            mY = location.getInt("top");
            LogUtils.i(mChar_word, mWidth, mHeight, mX, mY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        mToolbar_result = findViewById(R.id.toolBar_result);
        mRpv_result = findViewById(R.id.rpv_result);
        mCollapsingToolbarLayout = findViewById(R.id.ctb_result);
        mNsv_result = findViewById(R.id.nsv_result);

        mCardView_character = findViewById(R.id.cardView_character);
        mTextView_word = findViewById(R.id.tv_result_word_recognize);
        mTv_word_width = findViewById(R.id.tv_result_char_width);
        mTv_word_height = findViewById(R.id.tv_result_char_height);
        mTv_word_x = findViewById(R.id.tv_result_char_left);
        mTv_word_y = findViewById(R.id.tv_result_char_top);

    }

    @NonNull
    private List<Bitmap> getBitmapList() {

        List<Bitmap> bitmapList = new ArrayList<>();
        mCroppedImgPath = this.getIntent().getStringExtra("cropImgPath");

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
}
