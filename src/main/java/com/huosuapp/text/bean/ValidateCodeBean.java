package com.huosuapp.text.bean;

/**
 * Created by admin on 2016/8/29.
 */
public class ValidateCodeBean  {

    /**
     * code : 200
     * msg : 发送成功
     * data : {"sessionid":"7"}
     */

    private String code;
    private String msg;
    /**
     * sessionid : 7
     */

    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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
        private String sessionid;

        public String getSessionid() {
            return sessionid;
        }

        public void setSessionid(String sessionid) {
            this.sessionid = sessionid;
        }
    }
}
