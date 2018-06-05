package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean;

public class FunctionInfo {

    int resource;
    String name;

    public FunctionInfo(int resource, String name) {
        this.resource = resource;
        this.name = name;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
