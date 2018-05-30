package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.ItemTouchHelperListener
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener
import java.util.*

/**
 * 用于主界面第一个页面的item绑定
 */

class MainRecognizeItemAdapter(private val context: Context, private var mWordInfos: ArrayList<WordInfo>?) : RecyclerView.Adapter<MainRecognizeItemAdapter.ViewHolder>(), View.OnClickListener, ItemTouchHelperListener {
    private var mOnCardViewItemListener: OnCardViewItemListener? = null

    fun updateData(dataset: ArrayList<WordInfo>) {
        this.mWordInfos = dataset
        notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        if (mOnCardViewItemListener != null) {
            mOnCardViewItemListener!!.onCardViewItemClick(v, v.tag as Int)
        }
    }

    fun setOnCardViewItemListener(listener: OnCardViewItemListener) {
        this.mOnCardViewItemListener = listener
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mWordInfos!!, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDelete(position: Int) {
        this.mWordInfos!!.removeAt(position)
        notifyItemRemoved(position)
        if (position != mWordInfos!!.size) { // 如果移除的是最后一个，忽略
            notifyItemRangeChanged(position, mWordInfos!!.size - position)
        }
    }

    override fun onItemRecover(position: Int, wordInfo: WordInfo) {
        mWordInfos!!.add(position, wordInfo)
        notifyItemInserted(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mImageView_image: ImageView = itemView.findViewById(R.id.iv_cardView_main_word_image)
        var mTv_word: TextView = itemView.findViewById(R.id.tv_cardView_main_word)
        var mTv_height: TextView = itemView.findViewById(R.id.tv_cardView_main_word_height)
        var mTv_width: TextView = itemView.findViewById(R.id.tv_cardView_main_word_width)
        var mTv_style: TextView = itemView.findViewById(R.id.tv_cardView_main_word_style)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRecognizeItemAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_activity_recognize, parent, false)
        val vh = ViewHolder(v)
        v.setOnClickListener(this)
        LogUtils.i("ViewHolder init success")
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
                .load(mWordInfos!![position].pic_path)
                .into(holder.mImageView_image)
        holder.mTv_word.text = context.getString(R.string.result_character_cardView_word) + mWordInfos!![position].word!!
        holder.mTv_style.text = context.getString(R.string.result_character_cardView_style) + mWordInfos!![position].style!!
        holder.mTv_width.text = context.getString(R.string.result_character_cardView_width) + mWordInfos!![position].width.toString()
        holder.mTv_height.text = context.getString(R.string.result_character_cardView_height) + mWordInfos!![position].height.toString()
        holder.itemView.tag = position
    }

    override fun getItemCount(): Int {
        return this.mWordInfos!!.size
    }
}
