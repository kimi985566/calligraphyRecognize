package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean;

public class SettingInfo {

    String title;
    String subTitle;
    int type;

    public SettingInfo() {
    }

    public SettingInfo(String title, String subTitle, int type) {
        this.title = title;
        this.subTitle = subTitle;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
