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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.ImageProcessUtils;

import static android.app.Activity.RESULT_OK;


//装在多个Fragment

public class MainFragment extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private static final int STATE_PREVIEW = 0;
    public static final int SELECT_PIC_RESULT_CODE = 202;
    private int maxSize = 1024;

    private FrameLayout mFragmentMainContainer;
    private RecyclerView mFragmentMainRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    //装在JNI方法
    static {
        System.loadLibrary("native-lib");
    }

    private ImageView mIv_content;
    private Bitmap mBmp;
    private Button mBtn_content_process;
    private Button mBtn_content_select;

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
            View view = inflater.inflate(R.layout.fragment_list_view, container, false);
            initMainList(view);
            return view;
            //装在第二页
        } else if (getArguments().getInt("index", 0) == 1) {
            View view = inflater.inflate(R.layout.fragment_content, container, false);
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
    private void initMainList(View view) {
        mFragmentMainContainer = view.findViewById(R.id.fragment_main_container);
        mFragmentMainRecyclerView = view.findViewById(R.id.fragment_main_recycle_view);
        mFragmentMainRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentMainRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<String> itemsData = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            itemsData.add("Fragment " + getArguments().getInt("index", -1) + " / Item " + i);
        }

        MainAdapter mainAdapter = new MainAdapter(itemsData);
        mFragmentMainRecyclerView.setAdapter(mainAdapter);
    }

    //第二页的加载
    private void initMainContent(View view) {
        TextView tv_test = view.findViewById(R.id.tv_content);
        tv_test.setText(stringFromJNI());
        mBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_image_calligraph);
        mIv_content = view.findViewById(R.id.iv_content);
        mIv_content.setImageBitmap(mBmp);

        mBtn_content_process = view.findViewById(R.id.btn_content_process);
        mBtn_content_select = view.findViewById(R.id.btn_content_select);
        mBtn_content_process.setOnClickListener(this);
        mBtn_content_select.setOnClickListener(this);
    }


    private void initSetting(View view) {
        //TODO：设置页
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_content_process:
                mBmp = ImageProcessUtils.skeletonFromJNI(mBmp);
                mIv_content.setImageBitmap(mBmp);
                break;
            case R.id.btn_content_select:
                selectImage();
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

    public void refresh() {
        if (getArguments().getInt("index", 0) > 0 && mFragmentMainRecyclerView != null) {
            mFragmentMainRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void willBeDisplayed() {
        if (mFragmentMainContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            mFragmentMainContainer.startAnimation(fadeIn);
        }
    }

    public void willBeHidden() {
        if (mFragmentMainContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            mFragmentMainContainer.startAnimation(fadeOut);
        }
    }

    public native String stringFromJNI();//will be remove

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
}
