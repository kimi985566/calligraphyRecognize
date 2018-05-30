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

/**
 * 书法欣赏页面
 * */

class DisplayActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, OnCardViewItemListener {

    private val mImageInfos = ArrayList<ImageInfo>()
    private var mMainSelectAdapter: MainSelectAdapter? = null
    private var mSwipeRefreshLayoutSelect: SwipeRefreshLayout? = null
    private var mRecyclerViewSelect: RecyclerView? = null
    private var mToolbar: Toolbar? = null

    //处理消息，显示对应功能
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
                    val pageId = jsonObject.getInt("page_id")
                    val styleName = jsonObject.getString("style_name")
                    val worksName = jsonObject.getString("works_name")
                    val pagePath = Config.URLAddress + jsonObject.getString("page_path")
                    LogUtils.d("$styleName $worksName $pagePath")
                    mImageInfos.add(ImageInfo(pageId, styleName, worksName, pagePath))
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
        mSwipeRefreshLayoutSelect = findViewById(R.id.fragment_select_swipe_refresh_layout)
        mRecyclerViewSelect = findViewById(R.id.fragment_select_recycle_view)
    }

    //设置列表
    private fun initRecycleView() {
        mRecyclerViewSelect!!.itemAnimator = DefaultItemAnimator()
        mRecyclerViewSelect!!.setHasFixedSize(true)
        //设置显示样式为一排两行显示方式
        val gridLayoutManager = GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false)
        mRecyclerViewSelect!!.layoutManager = gridLayoutManager

        mMainSelectAdapter = MainSelectAdapter(this, mImageInfos)
        mMainSelectAdapter!!.setOnCardViewItemListener(this)
        mRecyclerViewSelect!!.adapter = mMainSelectAdapter
    }

    //设置下拉刷新样式
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayoutSelect!!.setColorSchemeResources(
                R.color.color_tab_1, R.color.color_tab_2,
                R.color.color_tab_3, R.color.color_tab_4)
        mSwipeRefreshLayoutSelect!!.post {
            mSwipeRefreshLayoutSelect!!.isRefreshing = true
            JSONUtils.getImage(Config.picAddress, getImageHandler)
            mSwipeRefreshLayoutSelect!!.isRefreshing = false
            SnackbarUtils.with(mRecyclerViewSelect!!)
                    .setMessage("当前网络：" + NetworkUtils.getNetworkType().toString())
                    .showSuccess()
        }
        mSwipeRefreshLayoutSelect!!.setOnRefreshListener(this)
    }

    //设置工具栏样式
    private fun initToolBar() {
        mToolbar!!.title = "精选书法"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    //刷新
    override fun onRefresh() {
        Handler().postDelayed({
            JSONUtils.getImage(Config.picAddress, getImageHandler)
            mSwipeRefreshLayoutSelect!!.isRefreshing = false
        }, 2000)
    }

    //点击卡片，显示其所属的书法风格
    override fun onCardViewItemClick(view: View, position: Int) {
        SnackbarUtils.with(view)
                .setMessage(mImageInfos[position].getImage_style())
                .setDuration(SnackbarUtils.LENGTH_SHORT)
                .showSuccess()
    }
}
