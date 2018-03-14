package yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kimi9 on 2018/3/14.
 */

public class ResultFragment extends android.support.v4.app.Fragment {

    //单例模式
    public static ResultFragment newInstance(int index) {
        Bundle args = new Bundle();
        ResultFragment fragment = new ResultFragment();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
