package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimi9 on 2018/3/14.
 */

public class ResultFragmentAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragments = new ArrayList<>();

    public ResultFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    public ResultFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
