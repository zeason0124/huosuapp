package com.huosuapp.text.bean;

/**
 * Created by admin on 2016/9/12.
 */
public class VersionUpdateBean  {

    /**
     * code : 200
     * msg : 请求成功
     * data : {"hasnew":1,"newverid":12,"newurl":"http://down.huosdk.com"}
     */

    private int code;
    private String msg;
    /**
     * hasnew : 1
     * newverid : 12
     * newurl : http://down.huosdk.com
     */

    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int hasnew;
        private int newverid;
        private String newurl;

        public int getHasnew() {
            return hasnew;
        }

        public void setHasnew(int hasnew) {
            this.hasnew = hasnew;
        }

        public int getNewverid() {
            return newverid;
        }

        public void setNewverid(int newverid) {
            this.newverid = newverid;
        }

        public String getNewurl() {
            return newurl;
        }

        public void setNewurl(String newurl) {
            this.newurl = newurl;
        }
    }
}
