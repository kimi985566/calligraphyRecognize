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
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.ImageInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener;

public class MainSelectAdapter extends RecyclerView.Adapter<MainSelectAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = MainSelectAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<ImageInfo> mImageInfos = new ArrayList<>();
    private OnCardViewItemListener mOnCardViewItemListener;

    public MainSelectAdapter(Context context, ArrayList<ImageInfo> imageInfos) {
        this.context = context;
        this.mImageInfos = imageInfos;
        LogUtils.i("MainSelectAdapter init success");
    }

    public void updateData(ArrayList<ImageInfo> arrayList) {
        this.mImageInfos = arrayList;
        notifyDataSetChanged();
        LogUtils.i("update Data success");
    }

    @Override
    public void onClick(View v) {
        if (mOnCardViewItemListener != null) {
            mOnCardViewItemListener.onCardViewItemClick(v, (int) v.getTag());
        }
    }

    public void setOnCardViewItemListener(OnCardViewItemListener onCardViewItemListener) {
        this.mOnCardViewItemListener = onCardViewItemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTv_select;
        private final ImageView mIv_select;

        public ViewHolder(View itemView) {
            super(itemView);
            mTv_select = itemView.findViewById(R.id.tv_item_fragment_select);
            mIv_select = itemView.findViewById(R.id.iv_item_fragment_select);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_select, parent, false);
        ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(this);
        Log.i(TAG, "MainSelectAdapter ViewHolder init success");
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context)
                .asBitmap()
                .load(mImageInfos.get(position).getImage_path())
                .thumbnail(0.1f)
                .into(holder.mIv_select);
        holder.mTv_select.setText(mImageInfos.get(position).getImage_work_name());
        holder.itemView.setTag(position);
        Log.i(TAG, "MainSelectAdapter OnBindViewHolder success");
    }

    @Override
    public int getItemCount() {
        return mImageInfos.size();
    }
}
