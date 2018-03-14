package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.adapter.StaticPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimi9 on 2018/3/14.
 */

public class RollViewPagerAdapter extends StaticPagerAdapter {


    List<Bitmap> mBitmapList = new ArrayList<>();

    public RollViewPagerAdapter(List<Bitmap> bitmapList) {
        mBitmapList = bitmapList;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setImageBitmap(mBitmapList.get(position));
        return view;
    }

    @Override
    public int getCount() {
        return mBitmapList.size();
    }

}
