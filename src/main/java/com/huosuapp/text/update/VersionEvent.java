package com.huosuapp.text.update;


import com.liang530.event.NotProguard;

/**
 * com.kufeng.hj.cancer.event
 * JAYE on 2016/2/24 10:13
 */
@NotProguard
public class VersionEvent  {
    private int isConstraint;
    private double versionNo;
    private String url;
    private String note;

    public void setIsConstraint(int isConstraint) {
        this.isConstraint = isConstraint;
    }

    public void setVersionNo(double versionNo) {
        this.versionNo = versionNo;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIsConstraint() {
        return isConstraint;
    }

    public double getVersionNo() {
        return versionNo;
    }

    public String getUrl() {
        return url;
    }

    public String getNote() {
        return note;
    }

}
