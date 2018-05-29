package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment.MainFragment
import java.util.*

/**
 * 用于主界面里的fragment的添加与绑定
 */

class MainFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragments = ArrayList<MainFragment>()
    var currentFragment: MainFragment? = null
        private set

    init {
        fragments.clear()
        fragments.add(MainFragment.newInstance(0))
        fragments.add(MainFragment.newInstance(1))
        fragments.add(MainFragment.newInstance(2))
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        if (currentFragment !== `object`) {
            currentFragment = `object` as MainFragment
        }
        super.setPrimaryItem(container, position, `object`)
    }

}
