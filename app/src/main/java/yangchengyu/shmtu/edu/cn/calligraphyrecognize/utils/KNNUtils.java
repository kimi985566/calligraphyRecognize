package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.KNNDistance;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.KNNNode;

public class KNNUtils {

    // 欧式距离计算
    public static double oudistance(KNNNode point1, KNNNode point2) {
        double temp = Math.pow(point1.getX() - point2.getX(), 2)
                + Math.pow(point1.getY() - point2.getY(), 2)
                + Math.pow(point1.getRatio() - point2.getRatio(), 2);
        return Math.sqrt(temp);
    }

    // 找出最大频率
    public static String maxP(Map<String, Double> map) {
        String key = null;
        double value = 0.0;
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            if (entry.getValue() > value) {
                key = entry.getKey();
                value = entry.getValue();
            }
        }
        return key;
    }

    // 计算频率
    public static Map<String, Double> computeP(Map<String, Integer> map, double k) {
        Map<String, Double> p = new HashMap<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            p.put(entry.getKey(), entry.getValue() / k);
        }
        return p;
    }

    // 计算每个分类包含的点的个数
    public static Map<String, Integer> getNumberOfType(
            ArrayList<KNNDistance> listDistance, ArrayList<KNNNode> listPoint, double k) {
        Map<String, Integer> map = new HashMap<>();
        int i = 0;
        for (KNNDistance distance : listDistance) {
            long id = distance.getId();
            // 通过id找到所属类型,并存储到HashMap中
            for (KNNNode point : listPoint) {
                if (point.getId() == id) {
                    if (map.get(point.getType()) != null)
                        map.put(point.getType(), map.get(point.getType()) + 1);
                    else {
                        map.put(point.getType(), 1);
                    }
                }
            }
            i++;
            if (i >= k)
                break;
        }
        return map;
    }

    public static ArrayList<KNNNode> getCatList() {

        KNNNode zuanNode = new KNNNode(0, 0.4890355, 0.4571371, 0.2915483, "篆书");
        KNNNode liNode = new KNNNode(1, 0.4768815, 0.479564, 0.374045, "隶书");
        KNNNode kaiNode = new KNNNode(2, 0.4962728, 0.4954426, 0.2994022, "楷体");
        KNNNode caoNode = new KNNNode(3, 0.5077533, 0.498588, 0.2952345, "草书");

        ArrayList<KNNNode> list = new ArrayList<>();

        list.add(zuanNode);
        list.add(liNode);
        list.add(kaiNode);
        list.add(caoNode);

        return list;

    }
}
