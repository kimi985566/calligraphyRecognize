package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils;

import java.util.Comparator;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.KNNDistance;

public class CompareClass implements Comparator<KNNDistance> {
    @Override
    public int compare(KNNDistance o1, KNNDistance o2) {
        return o1.getDisatance() > o2.getDisatance() ? 20 : -1;
    }
}
