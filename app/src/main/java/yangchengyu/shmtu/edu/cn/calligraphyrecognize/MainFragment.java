package yangchengyu.shmtu.edu.cn.calligraphyrecognize;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public class MainFragment extends Fragment {

    private FrameLayout mFragmentMainContainer;
    private RecyclerView mFragmentMainRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    public static MainFragment newInstance(int index) {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments().getInt("index", 0) == 0) {
            View view = inflater.inflate(R.layout.fragment_list_view, container, false);
            initMainList(view);
            return view;
        } else if (getArguments().getInt("index", 0) == 1) {
            View view = inflater.inflate(R.layout.fragment_camera, container, false);
            initMainCamera(view);
            return view;
        } else {
            View view = inflater.inflate(R.layout.fragment_setting, container, false);
            initSetting(view);
            return view;
        }
    }

    private void initMainList(View view) {
        mFragmentMainContainer = view.findViewById(R.id.fragment_main_container);
        mFragmentMainRecyclerView = view.findViewById(R.id.fragment_main_recycle_view);
        mFragmentMainRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentMainRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<String> itemsData  = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            itemsData.add("Fragment " + getArguments().getInt("index", -1) + " / Item " + i);
        }

        RecycleViewMainAdapter mainAdapter = new RecycleViewMainAdapter(itemsData);
        mFragmentMainRecyclerView.setAdapter(mainAdapter);
    }

    private void initMainCamera(View view) {


    }

    private void initSetting(View view) {
        //to be done
    }

    public void refresh() {
        if (getArguments().getInt("index", 0) > 0 && mFragmentMainRecyclerView != null) {
            mFragmentMainRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void willBeDisplayed() {
        if (mFragmentMainContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            mFragmentMainContainer.startAnimation(fadeIn);
        }
    }

    public void willBeHidden() {
        if (mFragmentMainContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            mFragmentMainContainer.startAnimation(fadeOut);
        }
    }
}
