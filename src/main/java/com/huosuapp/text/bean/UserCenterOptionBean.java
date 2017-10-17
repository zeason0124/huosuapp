package com.huosuapp.text.bean;

/**
 * Created by liu hong liang on 2016/10/12.
 */

public class UserCenterOptionBean {
    private String name;
    private int imageId;

    public UserCenterOptionBean(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
