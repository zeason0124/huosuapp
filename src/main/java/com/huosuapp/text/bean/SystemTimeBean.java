package com.huosuapp.text.bean;

/**
 * Created by liu hong liang on 2016/9/20.
 */
public class SystemTimeBean  {
    private int code;
    private String msg;
    private SystemTime data;
    public static  class SystemTime{
        private long time;
        public long getTime() {
            return time;
        }
        public void setTime(long time) {
            this.time = time;
        }
    }
    public int getCode() {
        return code;
    }

    public SystemTime getData() {
        return data;
    }

    public void setData(SystemTime data) {
        this.data = data;
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
}
