package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.FunctionInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener;

public class MainFragmentFunctionAdapter extends RecyclerView.Adapter<MainFragmentFunctionAdapter.ViewHolder>
        implements View.OnClickListener {

    private static final String TAG = MainFragmentFunctionAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<FunctionInfo> mFunctionInfos = new ArrayList<>();
    private OnCardViewItemListener mOnCardViewItemListener;

    public MainFragmentFunctionAdapter(Context context, ArrayList<FunctionInfo> functionInfos) {
        this.context = context;
        mFunctionInfos = functionInfos;
        LogUtils.i("MainFragmentFunctionAdapter init success");
    }

    @Override
    public void onClick(View v) {
        if (mOnCardViewItemListener != null) {
            mOnCardViewItemListener.onCardViewItemClick(v, (int) v.getTag());
        }
    }

    public void setOnCardViewItemListener(OnCardViewItemListener onCardViewItemListener) {
        mOnCardViewItemListener = onCardViewItemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mIv_fragment_function;
        private final TextView mTv_fragment_function;

        public ViewHolder(View itemView) {
            super(itemView);
            mIv_fragment_function = itemView.findViewById(R.id.iv_fragment_function);
            mTv_fragment_function = itemView.findViewById(R.id.tv_fragment_function);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_main_function, parent, false);
        ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(this);
        Log.i(TAG, "MainFragmentFunctionAdapter ViewHolder init success");
        return vh;
    }

    @Override
    public void onBindViewHolder(MainFragmentFunctionAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .asBitmap()
                .load(mFunctionInfos.get(position).getResource())
                .into(holder.mIv_fragment_function);
        holder.mTv_fragment_function.setText(mFunctionInfos.get(position).getName());
        holder.itemView.setTag(position);
        Log.i(TAG, "MainFragmentFunctionAdapter OnBindViewHolder success");
    }

    @Override
    public int getItemCount() {
        return mFunctionInfos.size();
    }
}
