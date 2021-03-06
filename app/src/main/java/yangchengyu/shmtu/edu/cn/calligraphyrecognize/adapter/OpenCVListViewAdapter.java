package yangchengyu.shmtu.edu.cn.calligraphyrecognize.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.R;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.OpenCVInfo;

/**
 * Created by kimi9 on 2018/2/21.
 */

public class OpenCVListViewAdapter extends BaseAdapter {

    private List<OpenCVInfo> mOpenCVInfos;
    private Context mContext;

    public OpenCVListViewAdapter(Context context, List<OpenCVInfo> openCVInfos) {
        mOpenCVInfos = openCVInfos;
        mContext = context;
    }

    public List<OpenCVInfo> getOpenCVInfos() {
        return this.mOpenCVInfos;
    }

    @Override
    public int getCount() {
        return mOpenCVInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mOpenCVInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mOpenCVInfos.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_activity_opencv, parent, false);
        TextView item_tv_name = view.findViewById(R.id.item_tv_name);
        TextView item_tv_commend = view.findViewById(R.id.item_tv_commend);
        item_tv_name.setText(mOpenCVInfos.get(position).getName());
        item_tv_commend.setText(mOpenCVInfos.get(position).getCommend());
        view.setTag(mOpenCVInfos.get(position));
        return view;
    }
}
