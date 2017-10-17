package com.huosuapp.text.bean;

/**
 * Created by admin on 2016/8/30.
 */
public class LoginBean  {
    private int code;
    private String msg;

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
        private String identifier;
        private String accesstoken;
        private int expaire_time;

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getAccesstoken() {
            return accesstoken;
        }

        public void setAccesstoken(String accesstoken) {
            this.accesstoken = accesstoken;
        }

        public int getExpaire_time() {
            return expaire_time;
        }

        public void setExpaire_time(int expaire_time) {
            this.expaire_time = expaire_time;
        }
    }
}
