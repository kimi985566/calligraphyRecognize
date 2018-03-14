package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment.ResultFragment;

/**
 * Created by kimi9 on 2018/3/14.
 */

public class ResultFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<ResultFragment> fragments = new ArrayList<>();
    private ResultFragment currentFragment;

    public ResultFragmentAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(ResultFragment.newInstance(0));
        fragments.add(ResultFragment.newInstance(1));
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
            currentFragment = ((ResultFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public ResultFragment getCurrentFragment() {
        return currentFragment;
    }
}
