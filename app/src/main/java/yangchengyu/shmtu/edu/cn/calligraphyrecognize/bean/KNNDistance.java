package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean;

public class KNNDistance {

    // 已知点id
    private long id;
    // 未知点id
    private long nid;
    // 二者之间的距离
    private double disatance;

    public KNNDistance() {
    }

    public KNNDistance(long id, long nid, double disatance) {
        this.id = id;
        this.nid = nid;
        this.disatance = disatance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNid() {
        return nid;
    }

    public void setNid(long nid) {
        this.nid = nid;
    }

    public double getDisatance() {
        return disatance;
    }

    public void setDisatance(double disatance) {
        this.disatance = disatance;
    }
}
