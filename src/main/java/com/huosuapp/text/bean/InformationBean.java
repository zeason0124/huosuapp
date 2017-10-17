package com.huosuapp.text.bean;

import java.util.List;

/**
 * Created by admin on 2016/8/27.
 */
public class InformationBean  {

    /**
     * code : 200
     * msg : 请求成功
     * data : {"count":9,"news_list":[{"id":121,"title":"test","gameid":60216,"img":"kdyglm_1470016892.jpg","pudate":"2016-08-01","author":1,"commentcnt":0,"likecnt":0,"type":1},{"id":120,"title":"test","gameid":60216,"img":"kdyglm_1470016883.jpg","pudate":"2016-08-01","author":1,"commentcnt":0,
     * "likecnt":0,"type":1},{"id":119,"title":"test","gameid":60216,"img":"kdyglm_1470016875.jpg","pudate":"2016-08-01","author":1,"commentcnt":0,"likecnt":0,"type":1},{"id":118,"title":"《传奇世界之仗剑天涯H5》V11.0版本更新活动公告","gameid":60219,"img":"cqsj_zjtyh5_1470016321.png","pudate":"2016-08-01",
     * "author":1,"commentcnt":0,"likecnt":0,"type":1},{"id":117,"title":"《传奇世界之仗剑天涯H5》V11.0版本更新预告","gameid":60219,"img":"cqsj_zjtyh5_1469798290.png","pudate":"2016-07-29","author":1,"commentcnt":0,"likecnt":0,"type":1},{"id":107,"title":"《传奇世界之仗剑天涯H5》新手通用指南","gameid":60219,
     * "img":"cqsj_zjtyh5_1469672666.png","pudate":"2016-07-28","author":1,"commentcnt":0,"likecnt":0,"type":2},{"id":108,"title":"《传奇世界之仗剑天涯H5》平民玩家如何玩转手机页游","gameid":60219,"img":"cqsj_zjtyh5_1469672601.png","pudate":"2016-07-28","author":1,"commentcnt":0,"likecnt":0,"type":2},{"id":112,
     * "title":"《传奇世界之仗剑天涯H5》7月22日活动与更新内容公告","gameid":60219,"img":"cqsj_zjtyh5_1469600872.png","pudate":"2016-07-27","author":1,"commentcnt":0,"likecnt":0,"type":1},{"id":113,"title":"《热血球球》新手攻略","gameid":60220,"img":"rxqq_1469247198.png","pudate":"2016-07-26","author":1,"commentcnt":0,
     * "likecnt":0,"type":2},{"id":114,"title":"《口袋妖怪联盟》老司机带路","gameid":60216,"img":"kdyglm_1469261502.png","pudate":"2016-07-26","author":1,"commentcnt":0,"likecnt":0,"type":2}]}
     */

    private int code;
    private String msg;
    /**
     * count : 9
     * news_list : [{"id":121,"title":"test","gameid":60216,"img":"kdyglm_1470016892.jpg","pudate":"2016-08-01","author":1,"commentcnt":0,"likecnt":0,"type":1},{"id":120,"title":"test","gameid":60216,"img":"kdyglm_1470016883.jpg","pudate":"2016-08-01","author":1,"commentcnt":0,"likecnt":0,
     * "type":1},{"id":119,"title":"test","gameid":60216,"img":"kdyglm_1470016875.jpg","pudate":"2016-08-01","author":1,"commentcnt":0,"likecnt":0,"type":1},{"id":118,"title":"《传奇世界之仗剑天涯H5》V11.0版本更新活动公告","gameid":60219,"img":"cqsj_zjtyh5_1470016321.png","pudate":"2016-08-01","author":1,
     * "commentcnt":0,"likecnt":0,"type":1},{"id":117,"title":"《传奇世界之仗剑天涯H5》V11.0版本更新预告","gameid":60219,"img":"cqsj_zjtyh5_1469798290.png","pudate":"2016-07-29","author":1,"commentcnt":0,"likecnt":0,"type":1},{"id":107,"title":"《传奇世界之仗剑天涯H5》新手通用指南","gameid":60219,"img":"cqsj_zjtyh5_1469672666
     * .png","pudate":"2016-07-28","author":1,"commentcnt":0,"likecnt":0,"type":2},{"id":108,"title":"《传奇世界之仗剑天涯H5》平民玩家如何玩转手机页游","gameid":60219,"img":"cqsj_zjtyh5_1469672601.png","pudate":"2016-07-28","author":1,"commentcnt":0,"likecnt":0,"type":2},{"id":112,"title":"《传奇世界之仗剑天涯H5》7月22日活动与更新内容公告",
     * "gameid":60219,"img":"cqsj_zjtyh5_1469600872.png","pudate":"2016-07-27","author":1,"commentcnt":0,"likecnt":0,"type":1},{"id":113,"title":"《热血球球》新手攻略","gameid":60220,"img":"rxqq_1469247198.png","pudate":"2016-07-26","author":1,"commentcnt":0,"likecnt":0,"type":2},{"id":114,
     * "title":"《口袋妖怪联盟》老司机带路","gameid":60216,"img":"kdyglm_1469261502.png","pudate":"2016-07-26","author":1,"commentcnt":0,"likecnt":0,"type":2}]
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
         * id : 121
         * title : test
         * gameid : 60216
         * img : kdyglm_1470016892.jpg
         * pudate : 2016-08-01
         * author : 1
         * commentcnt : 0
         * likecnt : 0
         * type : 1
         */
        private List<Information> news_list;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<Information> getNews_list() {
            return news_list;
        }

        public void setNews_list(List<Information> news_list) {
            this.news_list = news_list;
        }
    }
    public static class Information {
        private int id;
        private String title;
        private int gameid;
        private String img;
        private String pudate;
        private long author;
        private long commentcnt;
        private long likecnt;
        private String type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getGameid() {
            return gameid;
        }

        public void setGameid(int gameid) {
            this.gameid = gameid;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getPudate() {
            return pudate;
        }

        public void setPudate(String pudate) {
            this.pudate = pudate;
        }

        public long getAuthor() {
            return author;
        }

        public void setAuthor(long author) {
            this.author = author;
        }

        public long getCommentcnt() {
            return commentcnt;
        }

        public void setCommentcnt(long commentcnt) {
            this.commentcnt = commentcnt;
        }

        public long getLikecnt() {
            return likecnt;
        }

        public void setLikecnt(long likecnt) {
            this.likecnt = likecnt;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
