package com.huosuapp.text.bean;

/**
 * Created by admin on 2016/9/7.
 */
public class ApplyGiftBean  {

    /**
     * code : 201
     * msg : 领取成功
     * data : {"giftcode":"11111111111111333"}
     */

    private Integer code;
    private String msg;
    /**
     * giftcode : 11111111111111333
     */

    private DataBean data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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
        private String giftcode;

        public String getGiftcode() {
            return giftcode;
        }

        public void setGiftcode(String giftcode) {
            this.giftcode = giftcode;
        }
    }
}
