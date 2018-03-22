package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener;

/**
 * 用于主界面第一个页面的item绑定
 */

public class MainItemAdapter extends RecyclerView.Adapter<MainItemAdapter.ViewHolder>
        implements View.OnClickListener, ItemTouchHelperAdapter {

    private ArrayList<WordInfo> mWordInfos = new ArrayList<>();
    private OnCardViewItemListener mOnCardViewItemListener;
    private Context context;

    public MainItemAdapter(Context context, ArrayList<WordInfo> dataset) {
        this.context = context;
        this.mWordInfos = dataset;
    }

    public void updateData(ArrayList<WordInfo> dataset) {
        this.mWordInfos = dataset;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (mOnCardViewItemListener != null) {
            mOnCardViewItemListener.onCardViewItemClick(v, (int) v.getTag());
        }
    }

    public void setOnCardViewItemListener(OnCardViewItemListener listener) {
        this.mOnCardViewItemListener = listener;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mWordInfos, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDelete(int position) {
        mWordInfos.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemRecover(int position, WordInfo wordInfo) {
        mWordInfos.add(position, wordInfo);
        notifyItemInserted(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView_image;
        public TextView mTv_word;
        public TextView mTv_height;
        public TextView mTv_width;
        public TextView mTv_style;
        public TextView mTv_cv;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView_image = itemView.findViewById(R.id.iv_cardView_main_word_image);
            mTv_word = itemView.findViewById(R.id.tv_cardView_main_word);
            mTv_height = itemView.findViewById(R.id.tv_cardView_main_word_height);
            mTv_width = itemView.findViewById(R.id.tv_cardView_main_word_width);
            mTv_style = itemView.findViewById(R.id.tv_cardView_main_word_style);
            mTv_cv = itemView.findViewById(R.id.tv_cardView_main_word_cv);
        }
    }

    @Override
    public MainItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(this);
        LogUtils.i("ViewHolder init success");
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context)
                .load(mWordInfos.get(position).getPic_path())
                .into(holder.mImageView_image);
        holder.mTv_word.setText(context.getString(R.string.result_character_cardView_word) + mWordInfos.get(position).getWord());
        holder.mTv_style.setText(context.getString(R.string.result_character_cardView_style) + mWordInfos.get(position).getStyle());
        holder.mTv_width.setText(context.getString(R.string.result_character_cardView_width) + String.valueOf(mWordInfos.get(position).getWidth()));
        holder.mTv_height.setText(context.getString(R.string.result_character_cardView_height) + String.valueOf(mWordInfos.get(position).getHeight()));
        holder.mTv_cv.setText(context.getString(R.string.result_character_cardView_cv) + String.valueOf(0.9));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return this.mWordInfos.size();
    }
}
