package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean;

public class ImageInfo {

    public int image_id;
    public String image_style;
    public String image_work_name;
    public String image_path;

    public ImageInfo(int image_id, String image_style, String image_work_name, String image_path) {
        this.image_id = image_id;
        this.image_style = image_style;
        this.image_work_name = image_work_name;
        this.image_path = image_path;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getImage_style() {
        return image_style;
    }

    public void setImage_style(String image_style) {
        this.image_style = image_style;
    }

    public String getImage_work_name() {
        return image_work_name;
    }

    public void setImage_work_name(String image_work_name) {
        this.image_work_name = image_work_name;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
}
