package com.huosuapp.text.bean;

import java.util.List;

/**
 * Created by LiuHongLiang on 2016/9/25.
 */

public class GameListBean  {
    private int code;
    private String msg;
    private DataBean data;
    public static class DataBean {
        private int count;
        private String searchtype;//搜索类型game,gift,cdkey
        private List<GameBean> game_list;
        private List<GiftCodeBean.Gift> gift_list;//供搜索界面使用的礼包列表
        private List<GiftCodeBean.Gift> cdkey_list;//供搜索界面使用的激活码列表
        public int getCount() {
            return count;
        }
        public void setCount(int count) {
            this.count = count;
        }
        public List<GameBean> getGame_list() {
            return game_list;
        }
        public void setGame_list(List<GameBean> game_list) {
            this.game_list = game_list;
        }

        public String getSearchtype() {
            return searchtype;
        }

        public void setSearchtype(String searchtype) {
            this.searchtype = searchtype;
        }

        public List<GiftCodeBean.Gift> getGift_list() {
            return gift_list;
        }

        public void setGift_list(List<GiftCodeBean.Gift> gift_list) {
            this.gift_list = gift_list;
        }

        public List<GiftCodeBean.Gift> getCdkey_list() {
            return cdkey_list;
        }

        public void setCdkey_list(List<GiftCodeBean.Gift> cdkey_list) {
            this.cdkey_list = cdkey_list;
        }
    }
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
}
