package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.Toast
import com.blankj.utilcode.util.FileUtils
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.DB.WordDBhelper
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainRecognizeItemAdapter
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.WordInfo
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.ItemTouchHelperListener
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener
import java.util.*

/**
 * 识别记录显示页面
 *
 * 用以显示过往的检测记录，采用recycleView
 *
 * */
class RecognizeActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, OnCardViewItemListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognize)
        initUI()
    }

    private fun initUI() {
        mToolbar = findViewById(R.id.toolbar_sub)
        mToolbar!!.title = "识别记录"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        mSwipeRefreshLayout = findViewById(R.id.swipe_main_container)
        mSwipeRefreshLayout!!.setColorSchemeResources(
                R.color.color_tab_1, R.color.color_tab_2,
                R.color.color_tab_3, R.color.color_tab_4)
        mSwipeRefreshLayout!!.setOnRefreshListener(this)
        mRecyclerView = findViewById(R.id.fragment_main_recycle_view)
        mRecyclerView!!.itemAnimator = DefaultItemAnimator()
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)

        getDBData()

        mMainCardViewItemAdapter = MainRecognizeItemAdapter(this, mWordInfo)
        mMainCardViewItemAdapter!!.setOnCardViewItemListener(this)
        val callback = myItemTouchHelperCallBack(mMainCardViewItemAdapter!!)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(mRecyclerView)
        mRecyclerView!!.adapter = mMainCardViewItemAdapter
    }

    //从数据库中获取数据
    private fun getDBData() {
        mWordDBhelper = WordDBhelper(this)
        mWordInfo = mWordDBhelper!!.allWord
    }

    //刷新界面
    override fun onRefresh() {
        Handler().postDelayed({
            mWordInfo = mWordDBhelper!!.allWord
            mMainCardViewItemAdapter!!.updateData(mWordInfo)
            mSwipeRefreshLayout!!.isRefreshing = false
        }, 2000)
    }

    //点击卡片，跳转页面
    override fun onCardViewItemClick(view: View, position: Int) {
        Toast.makeText(this, "加载中", Toast.LENGTH_SHORT).show()
        Thread(Runnable {
            val wordInfo = mWordInfo[position]
            val intent = Intent(view.context, ResultActivity::class.java)
            intent.putExtra(WORD, wordInfo.word)
            intent.putExtra(WIDTH, wordInfo.width)
            intent.putExtra(HEIGHT, wordInfo.height)
            intent.putExtra(X_ARRAY, wordInfo.xArray)
            intent.putExtra(Y_ARRAY, wordInfo.yArray)
            intent.putExtra(PIC_PATH, wordInfo.pic_path)
            intent.putExtra(STYLE, wordInfo.style)
            intent.putExtra(ZUAN, wordInfo.zuanScore)
            intent.putExtra(LI, wordInfo.liScore)
            intent.putExtra(KAI, wordInfo.kaiScore)
            intent.putExtra(CAO, wordInfo.caoScore)
            intent.putExtra(FROMWHERE, "main")
            startActivity(intent)
        }).start()
    }

    //点击事件回调
    internal inner class myItemTouchHelperCallBack(private val mItemTouchHelperListener: ItemTouchHelperListener) : ItemTouchHelper.Callback() {

        private var mPosition: Int = 0

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            //允许上下拖动
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            //允许从右向左滑动
            val swipeFlags = ItemTouchHelper.LEFT
            return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            //onItemMove接口里的方法
            mItemTouchHelperListener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //onItemDelete接口里的方法
            mPosition = viewHolder.adapterPosition
            mWordInfoTemp = mWordInfo[mPosition]
            mItemTouchHelperListener.onItemDelete(mPosition)

            val snackBar = Snackbar.make(mRecyclerView!!, "是否撤销删除", Snackbar.LENGTH_LONG)
            snackBar.setAction("Yes") { mItemTouchHelperListener.onItemRecover(mPosition, mWordInfoTemp!!) }
            snackBar.addCallback(MySnackBarCallBack())
            snackBar.show()
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                //滑动时改变Item的透明度，以实现滑动过程中实现渐变效果
                val alpha = 1 - Math.abs(dX) / viewHolder.itemView.width.toFloat()
                viewHolder.itemView.alpha = alpha
                viewHolder.itemView.translationX = dX
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        override fun isLongPressDragEnabled(): Boolean {
            //该方法返回值为true时，表示支持长按ItemView拖动
            return true
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            //该方法返回true时，表示如果用户触摸并且左滑了view，那么可以执行滑动删除操作，就是可以调用onSwiped()方法
            return true
        }
    }

    //SnackBar的回调
    private inner class MySnackBarCallBack : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            if (event == Snackbar.Callback.DISMISS_EVENT_SWIPE
                    || event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT
                    || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {
                FileUtils.deleteFile(mWordInfoTemp!!.pic_path)
                mWordDBhelper!!.deleteWord(mWordInfoTemp!!)
            }
        }
    }

    companion object {
        const val WORD = "word"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val X_ARRAY = "xArray"
        const val Y_ARRAY = "yArray"
        const val PIC_PATH = "path"
        const val STYLE = "style"
        const val ZUAN = "zuan"
        const val LI = "li"
        const val KAI = "KAI"
        const val CAO = "CAO"
        const val FROMWHERE = "fromwhere"

        private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
        private var mRecyclerView: RecyclerView? = null
        private var mWordDBhelper: WordDBhelper? = null
        private var mWordInfoTemp: WordInfo? = null
        private var mMainCardViewItemAdapter: MainRecognizeItemAdapter? = null
        private var mWordInfo = ArrayList<WordInfo>()
        private var mToolbar: Toolbar? = null
    }
}
