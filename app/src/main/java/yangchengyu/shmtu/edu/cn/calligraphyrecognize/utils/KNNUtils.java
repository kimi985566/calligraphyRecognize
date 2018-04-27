package yangchengyu.shmtu.edu.cn.calligraphyrecognize.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.KNNDistance;
import yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean.KNNNode;

/**
 * KNN原理：
 *
 * 存在一个样本数据集合，并且样本集中每个数据都存在标签。
 * 输入没有标签的新数据后，将新数据的每一个特征与样本集中数据对应的特征进行比较
 * 通过算法提取最相似样本（最紧邻）的分类标签
 *
 * 一般流程：
 * 1. 收集数据
 * 2. 准备数据
 * 3. 分析数据
 * 4. 训练算法
 * 5. 测试算法
 * 6. 使用算法
 *
 * */

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
    public static Map<String, Integer> getNumberOfType(ArrayList<KNNDistance> listDistance, ArrayList<KNNNode> listPoint, double k) {
        Map<String, Integer> map = new HashMap<>();
        int i = 0;
        for (KNNDistance distance : listDistance) {
            long id = distance.getId();//获取已知点的id
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

        KNNNode zuanNode1 = new KNNNode(0, 0.4890355, 0.4571371, 0.2915483, "篆书");
        KNNNode zuanNode2 = new KNNNode(1, 0.494, 0.495, 0.386, "篆书");
        KNNNode zuanNode3 = new KNNNode(2, 0.574, 0.547, 0.253, "篆书");
        KNNNode zuanNode4 = new KNNNode(3, 0.471, 0.509, 0.387, "篆书");

        KNNNode liNode1 = new KNNNode(4, 0.4768815, 0.479564, 0.374045, "隶书");
        KNNNode liNode2 = new KNNNode(5, 0.5, 0.427, 0.334, "隶书");
        KNNNode liNode3 = new KNNNode(6, 0.517, 0.454, 0.343, "隶书");
        KNNNode liNode4 = new KNNNode(7, 0.568, 0.474, 0.363, "隶书");

        KNNNode kaiNode1 = new KNNNode(8, 0.4962728, 0.4954426, 0.2994022, "楷体");
        KNNNode kaiNode2 = new KNNNode(9, 0.506, 0.483, 0.54, "楷体");
        KNNNode kaiNode3 = new KNNNode(10, 0.429, 0.435, 0.316, "楷体");
        KNNNode kaiNode4 = new KNNNode(11, 0.395, 0.413, 0.233, "楷体");

        KNNNode caoNode1 = new KNNNode(12, 0.5077533, 0.498588, 0.2952345, "草书");
        KNNNode caoNode2 = new KNNNode(13, 0.432, 0.452, 0.332, "草书");
        KNNNode caoNode3 = new KNNNode(14, 0.588, 0.526, 0.253, "草书");
        KNNNode caoNode4 = new KNNNode(15, 0.477, 0.405, 0.249, "草书");

        ArrayList<KNNNode> list = new ArrayList<>();

        list.add(zuanNode1);
        list.add(zuanNode2);
        list.add(zuanNode3);
        list.add(zuanNode4);

        list.add(liNode1);
        list.add(liNode2);
        list.add(liNode3);
        list.add(liNode4);

        list.add(kaiNode1);
        list.add(kaiNode2);
        list.add(kaiNode3);
        list.add(kaiNode4);

        list.add(caoNode1);
        list.add(caoNode2);
        list.add(caoNode3);
        list.add(caoNode4);

        return list;

    }
}
