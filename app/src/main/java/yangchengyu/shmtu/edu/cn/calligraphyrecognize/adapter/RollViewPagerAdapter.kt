package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jude.rollviewpager.adapter.StaticPagerAdapter
import java.util.*

/**
 * Created by kimi9 on 2018/3/14.
 */

class RollViewPagerAdapter(bitmapList: List<Bitmap>) : StaticPagerAdapter() {


    private var mBitmapList: List<Bitmap> = ArrayList()

    init {
        mBitmapList = bitmapList
    }

    override fun getView(container: ViewGroup, position: Int): View {
        val view = ImageView(container.context)
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        view.setImageBitmap(mBitmapList[position])
        return view
    }

    override fun getCount(): Int {
        return mBitmapList.size
    }

}
