package yangchengyu.shmtu.edu.cn.calligraphyrecognize.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.util.ArrayList;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter.MainAdapter;


public class MainFragment extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    private static final String TAG = "MainFragment";

    private static final int STATE_PREVIEW = 0;

    private FrameLayout mFragmentMainContainer;
    private RecyclerView mFragmentMainRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private int mState = STATE_PREVIEW;

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
            View view = inflater.inflate(R.layout.fragment_content, container, false);
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

        ArrayList<String> itemsData = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            itemsData.add("Fragment " + getArguments().getInt("index", -1) + " / Item " + i);
        }

        MainAdapter mainAdapter = new MainAdapter(itemsData);
        mFragmentMainRecyclerView.setAdapter(mainAdapter);
    }

    private void initMainCamera(View view) {
        requestCameraPermission();
    }


    private void initSetting(View view) {
        //to be done
    }

    @Override
    public void onClick(View v) {

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

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    public static class ConfirmationDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }
}
