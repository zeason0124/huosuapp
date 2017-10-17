package com.huosuapp.text.update;

/**
 * Created by 刘红亮 on 2015/11/9 18:20.
 * 启动下载的后台服务，通过广播接收下载完成的通知
 */

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.huosuapp.text.R;
import com.liang530.application.BaseApplication;
import com.liang530.log.L;
import com.liang530.utils.BaseAppUtil;

import java.io.File;

public class UpdateVersionService extends Service {
    private String url;
    /**
     * 安卓系统下载类
     **/
    DownloadManager manager;
    /**
     * 接收下载完的广播
     **/
    DownloadCompleteReceiver receiver;

    /**
     * 初始化下载器
     **/
    private void initDownManager() {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(
                Uri.parse(url));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载路径和文件名
        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getString(R.string.app_name) + ".apk");
        File publicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!publicDirectory.exists()) {//如果文件不存在则创建文件夹
            publicDirectory.mkdirs();
        }
        down.setDescription(getString(R.string.app_name) + "新版本下载");
        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        down.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        down.allowScanningByMediaScanner();
        L.d("hongliang", "准备下载apk");
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        // 将下载请求放入队列
        manager.enqueue(down);

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra("url");
        L.d("hongliang", "下载的url=" + url);
        if (!TextUtils.isEmpty(url)) {
            // 调用下载
            initDownManager();
        }
        //意外杀死后，不在重启服务
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                try {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downId);
                    Cursor c = manager.query(query);
                    if(c.moveToFirst()) {
                        //获取文件下载路径
                        String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        //如果文件路径不为空，说明已经存在了
                        if(filename != null){
                            File file= new File(filename);
                            if(file.exists()){
                                L.e("hongliang","文件名："+filename);
                                BaseAppUtil.installApk(BaseApplication.getInstance(),new File(filename));
                                return;
                            }
                        }
                    }
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //停止服务并关闭广播
                UpdateVersionService.this.stopSelf();
            }
        }
    }
}
