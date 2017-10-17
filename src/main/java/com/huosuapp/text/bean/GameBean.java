package com.huosuapp.text.bean;

import java.util.ArrayList;

/**
 * Created by liu hong liang on 2016/9/18.
 */
public class GameBean  {
    private String gameid;
    private String icon;
    private String gamename;
    private String type;
    private String runtime;
    private String category;
    private String hot;
    private String downcnt;
    private String score;
    private String distype; //0 无折扣 1 折扣 2 返利
    private String discount;//折扣；牛刀：续充折扣
    private float first_discount;//牛刀：首充折扣
    private String rebate;//返利比例，是小数
    private String likecnt;
    private String sharecnt;
    private String downlink;
    private String oneword;
    private String size;
    private String lang;
    private String sys;
    private String disc;
    private String verid;
    private String vername;
    private String giftcnt;//礼包数量
    private String newscnt;
    private int is_own;//判断充值是否显示


//    2016.10.26 欧忠富沟通的那个用户需要显示的充值按钮是否显示折扣返利
//    benefit_type跟rate，benefit_type为0表示没有充值跟折扣，1为折扣，2为返利，rate为比率

    private String benefit_type;
    private String rate;


    private String url;
    private ArrayList<String> image;

    private int itemPosition;//列表中的位置，用于判别图标

    public float getFirst_discount() {
        return first_discount;
    }

    public void setFirst_discount(float first_discount) {
        this.first_discount = first_discount;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    public String getNewscnt() {
        return newscnt;
    }

    public void setNewscnt(String newscnt) {
        this.newscnt = newscnt;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGamename() {
        return gamename;
    }

    public void setGamename(String gamename) {
        this.gamename = gamename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHot() {
        return hot;
    }

    public void setHot(String hot) {
        this.hot = hot;
    }

    public String getDowncnt() {
        return downcnt;
    }

    public void setDowncnt(String downcnt) {
        this.downcnt = downcnt;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDistype() {
        return distype;
    }

    public void setDistype(String distype) {
        this.distype = distype;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getRebate() {
        return rebate;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
    }

    public String getLikecnt() {
        return likecnt;
    }

    public void setLikecnt(String likecnt) {
        this.likecnt = likecnt;
    }

    public String getSharecnt() {
        return sharecnt;
    }

    public void setSharecnt(String sharecnt) {
        this.sharecnt = sharecnt;
    }

    public String getDownlink() {
        return downlink;
    }

    public void setDownlink(String downlink) {
        this.downlink = downlink;
    }

    public String getOneword() {
        return oneword;
    }

    public void setOneword(String oneword) {
        this.oneword = oneword;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public String getVerid() {
        return verid;
    }

    public void setVerid(String verid) {
        this.verid = verid;
    }

    public String getVername() {
        return vername;
    }

    public void setVername(String vername) {
        this.vername = vername;
    }

    public String getGiftcnt() {
        return giftcnt;
    }

    public void setGiftcnt(String giftcnt) {
        this.giftcnt = giftcnt;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBenefit_type() {
        return benefit_type;
    }

    public void setBenefit_type(String benefit_type) {
        this.benefit_type = benefit_type;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public int getIs_own() {
        return is_own;
    }

    public void setIs_own(int is_own) {
        this.is_own = is_own;
    }
}
