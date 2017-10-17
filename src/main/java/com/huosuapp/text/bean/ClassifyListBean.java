package com.huosuapp.text.bean;

import java.util.List;

/**
 * Created by admin on 2016/8/25.
 */
public class ClassifyListBean {

    /**
     * code : 200
     * msg : 请求成功
     * data : [{"typeid":1,"typename":"角色","icon":"http://www.etsdk.com"},{"typeid":2,"typename":"格斗","icon":"http://www.etsdk.com"},{"typeid":3,"typename":"休闲","icon":"http://www.etsdk.com"},{"typeid":4,"typename":"竞速","icon":"http://www.etsdk.com"},{"typeid":5,"typename":"策略","icon":"http://www
     * .etsdk.com"},{"typeid":6,"typename":"射击","icon":"http://www.etsdk.com"},{"typeid":7,"typename":"其它","icon":"http://www.etsdk.com"}]
     */

    private int code;
    private String msg;
    /**
     * typeid : 1
     * typename : 角色
     * icon : http://www.etsdk.com
     */

    private List<ClassifyBean> data;

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

    public List<ClassifyBean> getData() {
        return data;
    }

    public void setData(List<ClassifyBean> data) {
        this.data = data;
    }

    public static class ClassifyBean {
        private int typeid;
        private String typename;
        private String icon;

        public int getTypeid() {
            return typeid;
        }

        public void setTypeid(int typeid) {
            this.typeid = typeid;
        }

        public String getTypename() {
            return typename;
        }

        public void setTypename(String typename) {
            this.typename = typename;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
