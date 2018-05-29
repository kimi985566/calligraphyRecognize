package yangchengyu.shmtu.edu.cn.calligraphyrecognize.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SnackbarUtils
import org.json.JSONArray
import org.json.JSONException
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainSelectAdapter
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.ImageInfo
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.listener.OnCardViewItemListener
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.Config
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils.JSONUtils
import java.util.*

class DisplayActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, OnCardViewItemListener {

    private val mImageInfos = ArrayList<ImageInfo>()
    private var mMainSelectAdapter: MainSelectAdapter? = null
    private var mSwipeRefreshLayout_select: SwipeRefreshLayout? = null
    private var mRecyclerView_select: RecyclerView? = null
    private var mToolbar: Toolbar? = null

    @SuppressLint("HandlerLeak")
    private val getImageHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val JsonData = msg.obj as String
            try {
                mImageInfos.clear()
                val jsonArray = JSONArray(JsonData)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val page_id = jsonObject.getInt("page_id")
                    val style_name = jsonObject.getString("style_name")
                    val works_name = jsonObject.getString("works_name")
                    val page_path = Config.URLAddress + jsonObject.getString("page_path")
                    LogUtils.d("$style_name $works_name $page_path")
                    mImageInfos.add(ImageInfo(page_id, style_name, works_name, page_path))
                }
                mMainSelectAdapter!!.updateData(mImageInfos)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)
        initUI()
    }

    private fun initUI() {

        initView()

        initToolBar()

        initSwipeRefreshLayout()

        initRecycleView()
    }

    //获取界面
    private fun initView() {
        mToolbar = findViewById(R.id.toolbar_sub)
        mSwipeRefreshLayout_select = findViewById(R.id.fragment_select_swipe_refresh_layout)
        mRecyclerView_select = findViewById(R.id.fragment_select_recycle_view)
    }

    //设置列表
    private fun initRecycleView() {
        mRecyclerView_select!!.itemAnimator = DefaultItemAnimator()
        mRecyclerView_select!!.setHasFixedSize(true)

        val gridLayoutManager = GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false)
        mRecyclerView_select!!.layoutManager = gridLayoutManager

        mMainSelectAdapter = MainSelectAdapter(this, mImageInfos)
        mMainSelectAdapter!!.setOnCardViewItemListener(this)
        mRecyclerView_select!!.adapter = mMainSelectAdapter
    }

    //设置下拉刷新样式
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout_select!!.setColorSchemeResources(
                R.color.color_tab_1, R.color.color_tab_2,
                R.color.color_tab_3, R.color.color_tab_4)
        mSwipeRefreshLayout_select!!.post {
            mSwipeRefreshLayout_select!!.isRefreshing = true
            JSONUtils.getImage(Config.picAddress, getImageHandler)
            mSwipeRefreshLayout_select!!.isRefreshing = false
            SnackbarUtils.with(mRecyclerView_select!!)
                    .setMessage("当前网络：" + NetworkUtils.getNetworkType().toString())
                    .showSuccess()
        }
        mSwipeRefreshLayout_select!!.setOnRefreshListener(this)
    }

    private fun initToolBar() {
        mToolbar!!.title = "精选书法"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    override fun onRefresh() {
        Handler().postDelayed({
            JSONUtils.getImage(Config.picAddress, getImageHandler)
            mSwipeRefreshLayout_select!!.isRefreshing = false
        }, 2000)
    }

    override fun onCardViewItemClick(view: View, position: Int) {
        SnackbarUtils.with(view)
                .setMessage(mImageInfos[position].getImage_style())
                .setDuration(SnackbarUtils.LENGTH_SHORT)
                .showSuccess()
    }
}
