package com.huosuapp.text.sharesdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

/**
 * 图文分享
 * imagePath 图片的路径
 * text  text是分享文本，所有平台都需要这个字段
 * titleUrl titleUrl是标题的网络链接，仅在人人网和QQ空间使用
 * title title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
 * url  url仅在微信（包括好友和朋友圈）中使用
 * comment comment是我对这条分享的评论，仅在人人网和QQ空间使用
 * site site是分享此内容的网站名称，仅在QQ空间使用
 * Created by min on 2015/7/18.
 */
public class ShareDataEvent{
    public String imagePath;
    public String text;
    public String titleUrl;
    public String title;
    public String url;
    public String comment;
    public String site;
    public String siteUrl;
    public int resouceId;
    public View view;
    public Activity activity;
    public Context context;
    public String imageURL;
    public Bitmap bitmap;
    public ShareResultListener listener;
    @Override
    public String toString() {
        return "ShareDataEvent{" +
                "imagePath='" + imagePath + '\'' +
                ", text='" + text + '\'' +
                ", titleUrl='" + titleUrl + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", comment='" + comment + '\'' +
                ", site='" + site + '\'' +
                ", siteUrl='" + siteUrl + '\'' +
                ", resouceId=" + resouceId +
                ", view=" + view +
                ", activity=" + activity +
                ", context=" + context +
                ", imageURL='" + imageURL + '\'' +
                ", bitmap=" + bitmap +
                ", com.kufeng.hj.cancer.listener=" + listener +
                '}';
    }
}
