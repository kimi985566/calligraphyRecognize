package yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;

/**
 * Created by kimi9 on 2018/3/14.
 */

public class ResultFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

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
        if (getArguments().getInt("index", 0) == 0) {
            View view = inflater.inflate(R.layout.fragment_result_detail, container, false);
            initDetail();
            return view;
        } else if (getArguments().getInt("index", 0) == 1) {
            View view = inflater.inflate(R.layout.fragment_result_similar, container, false);
            initSimilar();
            return view;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initDetail() {
        //TODO
    }

    private void initSimilar() {
        //TODO
    }

    @Override
    public void onClick(View v) {

    }
}
