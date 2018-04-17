package yangchengyu.shmtu.edu.cn.calligraphyrecognize.bean;

import java.io.Serializable;

/**
 * Created by kimi9 on 2018/3/18.
 */

public class WordInfo implements Serializable {

    private int id;
    private String word;
    private int height;
    private int width;
    private int x_array;
    private int y_array;
    private String style;
    private String pic_path;
    private float zuanScore;
    private float liScore;
    private float kaiScore;
    private float caoScore;

    public WordInfo() {
    }

    public WordInfo(int id, String word, int height, int width, int x_array, int y_array, String style, String pic_path) {
        this.id = id;
        this.word = word;
        this.height = height;
        this.width = width;
        this.x_array = x_array;
        this.y_array = y_array;
        this.style = style;
        this.pic_path = pic_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX_array() {
        return x_array;
    }

    public void setX_array(int x_array) {
        this.x_array = x_array;
    }

    public int getY_array() {
        return y_array;
    }

    public void setY_array(int y_array) {
        this.y_array = y_array;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }

    public float getZuanScore() {
        return zuanScore;
    }

    public void setZuanScore(float zuanScore) {
        this.zuanScore = zuanScore;
    }

    public float getLiScore() {
        return liScore;
    }

    public void setLiScore(float liScore) {
        this.liScore = liScore;
    }

    public float getKaiScore() {
        return kaiScore;
    }

    public void setKaiScore(float kaiScore) {
        this.kaiScore = kaiScore;
    }

    public float getCaoScore() {
        return caoScore;
    }

    public void setCaoScore(float caoScore) {
        this.caoScore = caoScore;
    }
}
