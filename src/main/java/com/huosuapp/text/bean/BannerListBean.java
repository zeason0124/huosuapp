package com.huosuapp.text.bean;

import java.util.List;

/**
 * Created by admin on 2016/9/3.
 */
public class BannerListBean  {

    /**
     * code : 200
     * msg : 获取轮播图成功
     * data : {"count":2,"list":[{"name":"test","gameid":0,"image":"http://demo.1tsdk.com/upload/ads/20160902/57c8f2d808a06.jpg","url":"http://admin.etsdk.com/admin.php/Admin/slide/add.html","des":"http://admin.etsdk.com/admin.php/Admin/slide/add.html","content":"http://admin.etsdk.com/admin
     * .php/Admin/slide/add.html"},{"name":"http://admin.etsdk.com/admin.php/Index/index.html","gameid":0,"image":"http://demo.1tsdk.com/upload/ads/20160902/57c9340756bb6.png","url":"http://admin.etsdk.com/admin.php/Index/index.html","des":"http://admin.etsdk.com/admin.php/Index/index.html",
     * "content":"http://admin.etsdk.com/admin.php/Index/index.html"}]}
     */

    private int code;
    private String msg;
    /**
     * count : 2
     * list : [{"name":"test","gameid":0,"image":"http://demo.1tsdk.com/upload/ads/20160902/57c8f2d808a06.jpg","url":"http://admin.etsdk.com/admin.php/Admin/slide/add.html","des":"http://admin.etsdk.com/admin.php/Admin/slide/add.html","content":"http://admin.etsdk.com/admin.php/Admin/slide/add
     * .html"},{"name":"http://admin.etsdk.com/admin.php/Index/index.html","gameid":0,"image":"http://demo.1tsdk.com/upload/ads/20160902/57c9340756bb6.png","url":"http://admin.etsdk.com/admin.php/Index/index.html","des":"http://admin.etsdk.com/admin.php/Index/index.html","content":"http://admin
     * .etsdk.com/admin.php/Index/index.html"}]
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
        private List<Banner> list;
        public int getCount() {
            return count;
        }
        public void setCount(int count) {
            this.count = count;
        }
        public List<Banner> getList() {
            return list;
        }
        public void setList(List<Banner> list) {
            this.list = list;
        }

    }
    public static class Banner {
        private String name;
        private String gameid;
        private String image;
        private String url;
        private String des;
        private String content;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGameid() {
            return gameid;
        }

        public void setGameid(String gameid) {
            this.gameid = gameid;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
