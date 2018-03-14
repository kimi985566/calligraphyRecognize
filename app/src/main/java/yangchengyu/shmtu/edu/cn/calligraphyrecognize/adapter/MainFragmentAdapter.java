package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment.MainFragment;

/**
 * 用于主界面里的fragment的添加与绑定
 */

public class MainFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<MainFragment> fragments = new ArrayList<>();
    private MainFragment currentFragment;

    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(MainFragment.newInstance(0));
        fragments.add(MainFragment.newInstance(1));
        fragments.add(MainFragment.newInstance(2));
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((MainFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public MainFragment getCurrentFragment() {
        return currentFragment;
    }

}
