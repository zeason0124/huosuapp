package com.huosuapp.text.bean;

import java.util.List;

/**
 * Created by admin on 2016/8/26.
 */
public class GiftCodeBean  {

    /**
     * code : 200
     * msg : 请求成功
     * data : {"count":3,"gift_list":[{"giftid":10,"gameid":1,"giftname":"11111111","total":2,"remain":1,"content":"asasfasfasfasfasffs","icon":"http://www.etsdk.com1466530074.jpg","starttime":"2016-07-12 22:38:00","enttime":"2016-07-28 22:38:00","scope":""},{"giftid":9,"gameid":1,
     * "giftname":"新手礼包","total":9,"remain":6,"content":"擦 ","icon":"http://www.etsdk.com1466530074.jpg","starttime":"2016-06-21 01:25:00","enttime":"2016-06-30 01:25:00","scope":""},{"giftid":8,"gameid":2,"giftname":"礼包1共10个","total":11,"remain":9,"content":"222","icon":"http://www.etsdk
     * .com1466323415.png","starttime":"2016-06-08 00:16:00","enttime":"2016-06-30 00:16:00","scope":""}]}
     */

    private int code;
    private String msg;
    /**
     * count : 3
     * gift_list : [{"giftid":10,"gameid":1,"giftname":"11111111","total":2,"remain":1,"content":"asasfasfasfasfasffs","icon":"http://www.etsdk.com1466530074.jpg","starttime":"2016-07-12 22:38:00","enttime":"2016-07-28 22:38:00","scope":""},{"giftid":9,"gameid":1,"giftname":"新手礼包","total":9,
     * "remain":6,"content":"擦 ","icon":"http://www.etsdk.com1466530074.jpg","starttime":"2016-06-21 01:25:00","enttime":"2016-06-30 01:25:00","scope":""},{"giftid":8,"gameid":2,"giftname":"礼包1共10个","total":11,"remain":9,"content":"222","icon":"http://www.etsdk.com1466323415.png",
     * "starttime":"2016-06-08 00:16:00","enttime":"2016-06-30 00:16:00","scope":""}]
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
        private int count;
        /**
         * giftid : 10
         * gameid : 1
         * giftname : 11111111
         * total : 2
         * remain : 1
         * content : asasfasfasfasfasffs
         * icon : http://www.etsdk.com1466530074.jpg
         * starttime : 2016-07-12 22:38:00
         * enttime : 2016-07-28 22:38:00
         * scope :
         */

        private List<Gift> gift_list;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<Gift> getGift_list() {
            return gift_list;
        }

        public void setGift_list(List<Gift> gift_list) {
            this.gift_list = gift_list;
        }


    }
    public static class Gift {
        private long giftid;
        private long gameid;
        private long total;
        private long remain;
        private String giftname;
        private String content;
        private String icon;
        private String starttime;
        private String enttime;
        private String scope;
        private String giftcode;//文档上说是领取的码，实际没返回
        private Integer isget=0;
        private String code;//用户领取得到的码

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public long getGiftid() {
            return giftid;
        }

        public void setGiftid(long giftid) {
            this.giftid = giftid;
        }

        public long getGameid() {
            return gameid;
        }

        public void setGameid(long gameid) {
            this.gameid = gameid;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getRemain() {
            return remain;
        }

        public void setRemain(long remain) {
            this.remain = remain;
        }

        public String getGiftname() {
            return giftname;
        }

        public void setGiftname(String giftname) {
            this.giftname = giftname;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getEnttime() {
            return enttime;
        }

        public void setEnttime(String enttime) {
            this.enttime = enttime;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getGiftcode() {
            return giftcode;
        }

        public void setGiftcode(String giftcode) {
            this.giftcode = giftcode;
        }

        public Integer getIsget() {
            return isget;
        }

        public void setIsget(Integer isget) {
            this.isget = isget;
        }
    }
}
