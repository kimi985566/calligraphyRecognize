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
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.FunctionInfo
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener
import java.util.*

class MainFragmentFunctionAdapter(private val context: Context, functionInfos: ArrayList<FunctionInfo>) : RecyclerView.Adapter<MainFragmentFunctionAdapter.ViewHolder>(), View.OnClickListener {
    private var mFunctionInfos = ArrayList<FunctionInfo>()
    private var mOnCardViewItemListener: OnCardViewItemListener? = null

    init {
        mFunctionInfos = functionInfos
        LogUtils.i("MainFragmentFunctionAdapter init success")
    }

    override fun onClick(v: View) {
        if (mOnCardViewItemListener != null) {
            mOnCardViewItemListener!!.onCardViewItemClick(v, v.tag as Int)
        }
    }

    fun setOnCardViewItemListener(onCardViewItemListener: OnCardViewItemListener) {
        mOnCardViewItemListener = onCardViewItemListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val mIv_fragment_function: ImageView
        internal val mTv_fragment_function: TextView

        init {
            mIv_fragment_function = itemView.findViewById(R.id.iv_fragment_function)
            mTv_fragment_function = itemView.findViewById(R.id.tv_fragment_function)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_fragment_main_function, parent, false)
        val vh = ViewHolder(v)
        v.setOnClickListener(this)
        Log.i(TAG, "MainFragmentFunctionAdapter ViewHolder init success")
        return vh
    }

    override fun onBindViewHolder(holder: MainFragmentFunctionAdapter.ViewHolder, position: Int) {
        Glide.with(context)
                .asBitmap()
                .load(mFunctionInfos[position].resource)
                .into(holder.mIv_fragment_function)
        holder.mTv_fragment_function.text = mFunctionInfos[position].name
        holder.itemView.tag = position
        Log.i(TAG, "MainFragmentFunctionAdapter OnBindViewHolder success")
    }

    override fun getItemCount(): Int {
        return mFunctionInfos.size
    }

    companion object {

        private val TAG = MainFragmentFunctionAdapter::class.java.simpleName
    }
}
