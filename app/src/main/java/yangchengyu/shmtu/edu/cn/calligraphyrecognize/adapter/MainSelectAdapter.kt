package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.ImageInfo
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener
import java.util.*

class MainSelectAdapter(private val context: Context, private var mImageInfos: ArrayList<ImageInfo>?) : RecyclerView.Adapter<MainSelectAdapter.ViewHolder>(), View.OnClickListener {
    private var mOnCardViewItemListener: OnCardViewItemListener? = null

    init {
        LogUtils.i("MainSelectAdapter init success")
    }

    fun updateData(arrayList: ArrayList<ImageInfo>) {
        this.mImageInfos = arrayList
        notifyDataSetChanged()
        LogUtils.i("update Data success")
    }

    override fun onClick(v: View) {
        if (mOnCardViewItemListener != null) {
            mOnCardViewItemListener!!.onCardViewItemClick(v, v.tag as Int)
        }
    }

    fun setOnCardViewItemListener(onCardViewItemListener: OnCardViewItemListener) {
        this.mOnCardViewItemListener = onCardViewItemListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val mTv_select: TextView = itemView.findViewById(R.id.tv_item_fragment_select)
        internal val mIv_select: ImageView = itemView.findViewById(R.id.iv_item_fragment_select)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_activity_select, parent, false)
        val vh = ViewHolder(v)
        v.setOnClickListener(this)
        Log.i(TAG, "MainSelectAdapter ViewHolder init success")
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
                .asBitmap()
                .load(mImageInfos!![position].getImage_path())
                .thumbnail(0.1f)
                .into(holder.mIv_select)
        holder.mTv_select.text = mImageInfos!![position].getImage_work_name()
        holder.itemView.tag = position
        Log.i(TAG, "MainSelectAdapter OnBindViewHolder success")
    }

    override fun getItemCount(): Int {
        return mImageInfos!!.size
    }

    companion object {
        private val TAG = MainSelectAdapter::class.java.simpleName
    }
}
