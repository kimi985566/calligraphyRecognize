package yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SnackbarUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.DB.WordDBhelper;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity.ResultActivity;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainItemAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainSelectAdapter;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.ImageInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.ItemTouchHelperListener;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.Config;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.ImageProcessUtils;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.JSONUtils;

import static android.app.Activity.RESULT_OK;


//装在多个Fragment

public class MainFragment extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,
        RadioGroup.OnCheckedChangeListener, SwipeRefreshLayout.OnRefreshListener,
        OnCardViewItemListener {

    public static final int SELECT_PIC_RESULT_CODE = 202;
    public static final String WORD = "word";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String X_ARRAY = "x_array";
    public static final String Y_ARRAY = "y_array";
    public static final String PIC_PATH = "path";
    public static final String STYLE = "style";
    public static final String FROMWHERE = "fromwhere";
    private int maxSize = 1024;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private Bitmap mBmp;
    private Bitmap mTemp;
    private Button mBtn_content_select;
    private Button mBtn_content_process;
    private TextView mTv_test;
    private ImageView mIv_content;
    private RadioGroup mRg_content;
    private WordDBhelper mWordDBhelper;
    private ArrayList<WordInfo> mWordInfo = new ArrayList<>();
    private ArrayList<ImageInfo> mImageInfos = new ArrayList<>();
    private MainItemAdapter mMainCardViewItemAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout_select;
    private RecyclerView mRecyclerView_select;
    private MainSelectAdapter mMainSelectAdapter;
    private WordInfo mWordInfoTemp;

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
            View view = inflater.inflate(R.layout.fragment_main_content, container, false);
            initMainContent(view);
            return view;
            //装在第三页
        } else {
            View view = inflater.inflate(R.layout.fragment_select, container, false);
            initSelect(view);
            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //第一页的加载
    @SuppressLint("ResourceAsColor")
    private void initMainList(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_main_container);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.color_tab_1, R.color.color_tab_2,
                R.color.color_tab_3, R.color.color_tab_4);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = view.findViewById(R.id.fragment_main_recycle_view);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        getDBData(view);

        mMainCardViewItemAdapter = new MainItemAdapter(view.getContext(), mWordInfo);
        mMainCardViewItemAdapter.setOnCardViewItemListener(this);
        ItemTouchHelper.Callback callback = new myItemTouchHelperCallBack(mMainCardViewItemAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mMainCardViewItemAdapter);

    }

    private void getDBData(View view) {
        mWordDBhelper = new WordDBhelper(view.getContext());
        mWordInfo = mWordDBhelper.getALLWord();
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
    private void initSelect(View view) {
        mSwipeRefreshLayout_select = view.findViewById(R.id.fragment_select_swipe_refresh_layout);
        mRecyclerView_select = view.findViewById(R.id.fragment_select_recycle_view);
        mSwipeRefreshLayout_select.setColorSchemeResources(
                R.color.color_tab_1, R.color.color_tab_2,
                R.color.color_tab_3, R.color.color_tab_4);

        mSwipeRefreshLayout_select.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout_select.setRefreshing(true);
                JSONUtils.getImage(Config.picAddress, getImageHandler);
                mSwipeRefreshLayout_select.setRefreshing(false);
            }
        }, 3000);

        mSwipeRefreshLayout_select.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        JSONUtils.getImage(Config.picAddress, getImageHandler);
                        mSwipeRefreshLayout_select.setRefreshing(false);
                    }
                }, 2000);

            }
        });
        mRecyclerView_select.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView_select.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2,
                GridLayoutManager.VERTICAL, false);
        mRecyclerView_select.setLayoutManager(gridLayoutManager);

        mMainSelectAdapter = new MainSelectAdapter(view.getContext(), mImageInfos);
        mMainSelectAdapter.setOnCardViewItemListener(new OnCardViewItemListener() {
            @Override
            public void onCardViewItemClick(View view, int position) {
                SnackbarUtils.with(view)
                        .setMessage(mImageInfos.get(position).getImage_style())
                        .setDuration(SnackbarUtils.LENGTH_SHORT)
                        .showSuccess();
            }
        });
        mRecyclerView_select.setAdapter(mMainSelectAdapter);
    }

    @SuppressLint("HandlerLeak")
    private Handler getImageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String JsonData = (String) msg.obj;
            try {
                mImageInfos.clear();
                JSONArray jsonArray = new JSONArray(JsonData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int page_id = jsonObject.getInt("page_id");
                    String style_name = jsonObject.getString("style_name");
                    String works_name = jsonObject.getString("works_name");
                    String page_path = Config.URLAddress + jsonObject.getString("page_path");
                    LogUtils.d(style_name + " " + works_name + " " + page_path);
                    mImageInfos.add(new ImageInfo(page_id, style_name, works_name, page_path));
                }
                mMainSelectAdapter.updateData(mImageInfos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

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

    public void refresh() {
        if (getArguments().getInt("index", 0) > 0 && mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void willBeDisplayed() {
        if (mSwipeRefreshLayout != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            mSwipeRefreshLayout.startAnimation(fadeIn);
        }
    }

    public void willBeHidden() {
        if (mSwipeRefreshLayout != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            mSwipeRefreshLayout.startAnimation(fadeOut);
        }
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
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWordInfo = mWordDBhelper.getALLWord();
                mMainCardViewItemAdapter.updateData(mWordInfo);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onCardViewItemClick(final View view, final int position) {
        Toast.makeText(getContext(), "加载中", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                WordInfo wordInfo = mWordInfo.get(position);
                Intent intent = new Intent(view.getContext(), ResultActivity.class);
                intent.putExtra(WORD, wordInfo.getWord());
                intent.putExtra(WIDTH, wordInfo.getWidth());
                intent.putExtra(HEIGHT, wordInfo.getHeight());
                intent.putExtra(X_ARRAY, wordInfo.getX_array());
                intent.putExtra(Y_ARRAY, wordInfo.getY_array());
                intent.putExtra(PIC_PATH, wordInfo.getPic_path());
                intent.putExtra(STYLE, wordInfo.getStyle());
                intent.putExtra(FROMWHERE, "main");
                startActivity(intent);
            }
        }).start();
    }

    class myItemTouchHelperCallBack extends ItemTouchHelper.Callback {

        private int mPosition;
        private ItemTouchHelperListener mItemTouchHelperListener;

        public myItemTouchHelperCallBack(ItemTouchHelperListener itemTouchHelperListener) {
            this.mItemTouchHelperListener = itemTouchHelperListener;
        }

        public void setItemTouchHelperListener(ItemTouchHelperListener itemTouchHelperListener) {
            mItemTouchHelperListener = itemTouchHelperListener;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            //允许上下拖动
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            //允许从右向左滑动
            int swipeFlags = ItemTouchHelper.LEFT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //onItemMove接口里的方法
            mItemTouchHelperListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //onItemDelete接口里的方法
            mPosition = viewHolder.getAdapterPosition();
            mWordInfoTemp = mWordInfo.get(mPosition);
            mItemTouchHelperListener.onItemDelete(mPosition);

            Snackbar snackbar = Snackbar.make(mRecyclerView, "是否撤销删除", Snackbar.LENGTH_LONG);
            snackbar.setAction("Yes", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemTouchHelperListener.onItemRecover(mPosition, mWordInfoTemp);
                }
            });
            snackbar.addCallback(new MySnackBarCallBack());
            snackbar.show();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                //滑动时改变Item的透明度，以实现滑动过程中实现渐变效果
                final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

        }

        @Override
        public boolean isLongPressDragEnabled() {
            //该方法返回值为true时，表示支持长按ItemView拖动
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            //该方法返回true时，表示如果用户触摸并且左滑了view，那么可以执行滑动删除操作，就是可以调用onSwiped()方法
            return true;
        }
    }

    private class MySnackBarCallBack extends Snackbar.Callback {
        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            if (event == DISMISS_EVENT_SWIPE
                    || event == DISMISS_EVENT_TIMEOUT
                    || event == DISMISS_EVENT_CONSECUTIVE) {
                mWordDBhelper.deleteWord(mWordInfoTemp);
            }
        }
    }
}