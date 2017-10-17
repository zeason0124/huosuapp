package com.huosuapp.text.sharesdk;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import com.liang530.log.L;


/**
 * Created by min on 2015/7/27.
 */
public class ShareUtil {


    public  void oneKeyShare(Context context,  ShareDataEvent shareDataEvent, PlatformActionListener listener){
        L.d(shareDataEvent.toString());
        if(shareDataEvent.bitmap!=null){
            saveBitmapAndShare(context,  shareDataEvent, listener, shareDataEvent.bitmap);
        } else if(!TextUtils.isEmpty(shareDataEvent.imagePath)||!TextUtils.isEmpty(shareDataEvent.imageURL)) {
            oneKeyShareAndShow(context,  shareDataEvent, listener);
        }else if(shareDataEvent.view!=null){
            oneKeyShareByView(context,  shareDataEvent, listener);
        }else if(shareDataEvent.resouceId!=0){
            oneKeyShareByResourceId(context, shareDataEvent, listener);
        }
    }
    private void oneKeyShareByResourceId(Context context,ShareDataEvent shareDataEvent,PlatformActionListener listener){
        oneKeyShareAndShowByResourceId(context,  shareDataEvent, listener);
    }
    private void oneKeyShareByView(Context context,ShareDataEvent shareDataEvent,PlatformActionListener listener){
        oneKeyShareAndShowByView(context,shareDataEvent, listener);
    }

    private void oneKeyShareAndShow(Context context, ShareDataEvent shareDataEvent, PlatformActionListener listener){
        //采用弱引用
        WeakReference<PlatformActionListener> wrfListener=new WeakReference<PlatformActionListener>(listener);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(shareDataEvent.title == null ? "" : shareDataEvent.title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(shareDataEvent.titleUrl == null ? "http://hejingkeji.cn/" : shareDataEvent.titleUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(shareDataEvent.text == null ? "" : shareDataEvent.text);
        if(TextUtils.isEmpty(shareDataEvent.imageURL)) {
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            oks.setImagePath(shareDataEvent.imagePath == null ? "" : shareDataEvent.imagePath);//确保SDcard下面存在此张图片
        }else{
            //分享网络图片
            oks.setImageUrl(shareDataEvent.imageURL);
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(shareDataEvent.url == null ? "http://baidu.com/" : shareDataEvent.url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(shareDataEvent.comment == null ? "" : shareDataEvent.comment);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(shareDataEvent.site == null ? "http://baidu.com/" : shareDataEvent.site);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(shareDataEvent.siteUrl == null ? "http://baidu.com/" : shareDataEvent.siteUrl);
        if(wrfListener.get()!=null){
            L.d("设置了回调");
            oks.setCallback(listener);
        }
//        // 启动分享GUI
        oks.show(context);
    }
    /**
     * 分享某个资源文件图片
     * @param context
     * @param listener
     */
    private void oneKeyShareAndShowByResourceId(Context context, ShareDataEvent shareDataEvent, PlatformActionListener listener){
        L.d("TAG", "oneKeyShareAndShow");
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), shareDataEvent.resouceId);
        L.d("TAG", "oneKeyShare111" + bitmap);
        saveBitmapAndShare(context, shareDataEvent, listener, bitmap);
    }

    /**
     * 保存图片并且分享
     * @param context
     * @param shareDataEvent
     * @param listener
     * @param bitmap
     */
    private void saveBitmapAndShare(Context context, ShareDataEvent shareDataEvent, PlatformActionListener listener, Bitmap bitmap) {
        if(bitmap == null){
            throw new RuntimeException("不存在该资源id");
        }
        File file = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)){
            file = new File(Environment.getDownloadCacheDirectory().getAbsolutePath()+ File.separator+"temp.jpg");
        }else{
            File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"fastshopping");
            if(!file1.exists()){
                file1.mkdirs();
            }
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"fastshopping"+ File.separator+"temp.jpg");
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            shareDataEvent.imagePath = file.getAbsolutePath();
            oneKeyShareAndShow(context, shareDataEvent, listener);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 分享某个资源文件图片
     * @param context
     * @param listener
     */
    private void oneKeyShareAndShowByView(Context context, ShareDataEvent shareDataEvent, PlatformActionListener listener){
        if(shareDataEvent.view == null){
            throw new NullPointerException("view can not be null");
        }
        shareDataEvent.view.setDrawingCacheEnabled(true);
        shareDataEvent.view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap drawingCache = shareDataEvent.view.getDrawingCache();
        saveBitmapAndShare(context, shareDataEvent, listener, drawingCache);
    }
}
