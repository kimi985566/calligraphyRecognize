package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean;

public class KNNNode {

    private long id;//类别
    private double x;//cenX
    private double y;//cenY
    private double ratio;//binAry
    private String type;//类别

    public KNNNode() {
    }

    public KNNNode(long id, double x, double y, double ratio) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.ratio = ratio;
    }

    public KNNNode(long id, double x, double y, double ratio, String type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.ratio = ratio;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
