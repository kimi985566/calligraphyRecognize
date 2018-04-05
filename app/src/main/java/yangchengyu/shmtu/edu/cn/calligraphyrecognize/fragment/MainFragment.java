package yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.rollviewpager.RollPagerView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity.DisplayActivity;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity.RecognizeActivity;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainFragmentFunctionAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.RollViewPagerAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.FunctionInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.ImageProcessUtils;

import static android.app.Activity.RESULT_OK;


//装在多个Fragment

public class MainFragment extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        RadioGroup.OnCheckedChangeListener, OnCardViewItemListener {

    public static final int SELECT_PIC_RESULT_CODE = 202;
    private int maxSize = 1024;

    private Bitmap mBmp;
    private Bitmap mTemp;
    private Button mBtn_content_select;
    private Button mBtn_content_process;
    private TextView mTv_test;
    private ImageView mIv_content;
    private RadioGroup mRg_content;
    private RollPagerView mRpv_fragment_main;
    private RecyclerView mRv_fragment_main;

    private ArrayList<FunctionInfo> mFunctionInfos = new ArrayList<>();
    private MainFragmentFunctionAdapter mMainFragmentFunctionAdapter;

    //单例模式
    public static MainFragment newInstance(int index) {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //装在主界面第一页
        if (getArguments().getInt("index", 0) == 0) {
            View view = inflater.inflate(R.layout.fragment_main_select, container, false);
            initMainSelect(view);
            return view;
            //装在第二页
        } else if (getArguments().getInt("index", 0) == 1) {
            View view = inflater.inflate(R.layout.fragment_main_content, container, false);
            initMainContent(view);
            return view;
            //装在第三页
        } else {
            View view = inflater.inflate(R.layout.fragment_setting, container, false);
            initSetting(view);
            return view;
        }
    }

    //第一页的加载
    private void initMainSelect(View view) {
        mRpv_fragment_main = view.findViewById(R.id.rpv_fragment_main_select);
        mRv_fragment_main = view.findViewById(R.id.recycleview_fragment_main_select);

        setRollViewPager();
        setFunctionRecycle();
    }

    private void setFunctionRecycle() {
        setFunctionList();
        setHorizonFunc();
    }

    private void setHorizonFunc() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRv_fragment_main.setLayoutManager(linearLayoutManager);
        mMainFragmentFunctionAdapter = new MainFragmentFunctionAdapter(this.getContext(), mFunctionInfos);
        mMainFragmentFunctionAdapter.setOnCardViewItemListener(this);
        mRv_fragment_main.setAdapter(mMainFragmentFunctionAdapter);
    }

    private void setFunctionList() {
        FunctionInfo functionInfo_recognize = new FunctionInfo(R.drawable.ic_function_pic, "识别记录");
        FunctionInfo functionInfo_select = new FunctionInfo(R.drawable.ic_function_select, "精选书法");
        mFunctionInfos.add(functionInfo_recognize);
        mFunctionInfos.add(functionInfo_select);
    }

    private void setRollViewPager() {
        List<Bitmap> bitmapList = new ArrayList<>();
        Bitmap smu = BitmapFactory.decodeResource(getResources(), R.drawable.ic_smu_banner);
        Bitmap android = BitmapFactory.decodeResource(getResources(), R.drawable.ic_android_banner);
        bitmapList.add(smu);
        bitmapList.add(android);
        mRpv_fragment_main.setAdapter(new RollViewPagerAdapter(bitmapList));
    }

    //第二页的加载

    private void initMainContent(View view) {
        mTv_test = view.findViewById(R.id.tv_content);
        mIv_content = view.findViewById(R.id.iv_content);
        mRg_content = view.findViewById(R.id.rg_content);
        mBtn_content_select = view.findViewById(R.id.btn_content_select);
        mBtn_content_select.setOnClickListener(this);
        mBtn_content_process = view.findViewById(R.id.btn_content_process);
        mBtn_content_process.setOnClickListener(this);
        mRg_content.setOnCheckedChangeListener(this);
    }

    //第三页的加载
    private void initSetting(View view) {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_content_select:
                selectImage();
                break;
            case R.id.btn_content_process:
                switch (mRg_content.getCheckedRadioButtonId()) {
                    case R.id.rb_bin:
                        mTemp = ImageProcessUtils.binProcess(mBmp);
                        mIv_content.setImageBitmap(mTemp);
                        break;
                    case R.id.rb_edge:
                        mTemp = ImageProcessUtils.edgeProcess(mBmp);
                        mIv_content.setImageBitmap(mTemp);
                        break;
                    case R.id.rb_ske:
                        mTemp = ImageProcessUtils.skeletonFromJNI(mBmp);
                        mIv_content.setImageBitmap(mTemp);
                        break;
                }
                break;
        }
    }

    private void selectImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickIntent, "Browser Image……"),
                SELECT_PIC_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PIC_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            initImageView(data);
        }
    }

    private void initImageView(Intent data) {
        Uri uri = data.getData();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, options);

            int height = options.outHeight;
            int width = options.outWidth;
            int sampleSize = 1;
            int max = Math.max(height, width);

            //压缩图片，防止OOM
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

            mBmp = BitmapFactory.decodeStream(getActivity().getContentResolver().
                    openInputStream(uri), null, options);
            mIv_content.setImageBitmap(mBmp);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getCheckedRadioButtonId()) {
            case R.id.rb_bin:
                mBtn_content_process.setText(R.string.binProcess);
                mTv_test.setText(R.string.binProcess);
                break;
            case R.id.rb_edge:
                mBtn_content_process.setText(R.string.edgeProcess);
                mTv_test.setText(R.string.edgeProcess);
                break;
            case R.id.rb_ske:
                mBtn_content_process.setText(R.string.skeProcess);
                mTv_test.setText(R.string.skeProcess);
                break;
        }
    }

    @Override
    public void onCardViewItemClick(View view, int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(this.getContext(), RecognizeActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(this.getContext(), DisplayActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}