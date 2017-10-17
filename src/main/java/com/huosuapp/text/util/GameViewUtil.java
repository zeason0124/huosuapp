package com.huosuapp.text.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.game.sdk.util.ChannelNewUtil;
import com.huosuapp.text.accessibility.SmartInstallUtil;
import com.huosuapp.text.base.HsApplication;
import com.huosuapp.text.bean.DeviceBean;
import com.huosuapp.text.bean.DownLoadUrlBean;
import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.bean.InstallApkRecord;
import com.huosuapp.text.bean.TasksManagerModel;
import com.huosuapp.text.db.TasksManager;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.listener.GlobalMonitor;
import com.huosuapp.text.listener.IGameLayout;
import com.huosuapp.text.ui.dialog.DownHintDialog;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.application.BaseApplication;
import com.liang530.log.L;
import com.liang530.log.T;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.utils.BaseAppUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadList;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by liu hong liang on 2016/9/29.
 */

public class GameViewUtil {

    private static final  String TAG =GameViewUtil.class.getSimpleName() ;
    private static void confirmDown(final IGameLayout iGameLayout, final FileDownloadListener downloadListener){
        if(!BaseAppUtil.isWifiConnected(BaseApplication.getInstance())){//弹出提示是否下载
            DownHintDialog downHintDialog=new DownHintDialog();
            if(iGameLayout instanceof View){
                Context context = ((View) iGameLayout).getContext();
                downHintDialog.showDialog(context, true, null, new DownHintDialog.ConfirmDialogListener() {
                    @Override
                    public void ok() {
                        start(iGameLayout,downloadListener);
                    }
                    @Override
                    public void cancel() {

                    }
                });
            }
        }else{
            start(iGameLayout,downloadListener);
        }
    }
    /**
     * 下载按钮点击
     */
    public static synchronized void clickDownload(TasksManagerModel tasksManagerModel, IGameLayout iGameLayout, FileDownloadListener downloadListener) {
        //判断当前状态：
        if (tasksManagerModel == null) {//没有下载过，去下载
            if(SmartInstallUtil.requestAccessibilityService()){//先进行开启辅助功能提示
                return;
            }
//            start(iGameLayout,downloadListener);
            if(!BaseAppUtil.isOnline(BaseApplication.getInstance())){
                T.s(BaseApplication.getInstance(),"网络不通，请稍后再试！");
            }else{
                confirmDown(iGameLayout,downloadListener);
            }
            return;
        }
        if(tasksManagerModel.getStatus()==TasksManagerModel.STATUS_INSTALLED){//标记安装过了
            if(BaseAppUtil.isInstallApp(BaseApplication.getInstance(),tasksManagerModel.getPackageName())){//手机中还存在，可以直接启动
//                btnDownload.setText("启动");
                installOrOpen(tasksManagerModel);
            }else{//被卸载了
                File file= new File(tasksManagerModel.getPath());
                if(file.exists()){//安装包还存在，可以安装
                    installOrOpen(tasksManagerModel);
                }else{//安装包被删除了，删除数据库记录，重新下载
                    confirmDown(iGameLayout,downloadListener);
                }
            }
            return;
        }



        byte status = FileDownloader.getImpl().getStatus(tasksManagerModel.getId(), tasksManagerModel.getPath());
        switch (status) {
            case FileDownloadStatus.pending://等待
//                start(iGameLayout,downloadListener);
                confirmDown(iGameLayout,downloadListener);
                break;
            case FileDownloadStatus.started://下载中
                pause(tasksManagerModel.getId());
                break;
            case FileDownloadStatus.connected://下载中
                pause(tasksManagerModel.getId());
                break;
            case FileDownloadStatus.progress://下载中
                pause(tasksManagerModel.getId());
                break;
            case FileDownloadStatus.paused://暂停
                confirmDown(iGameLayout,downloadListener);
//                start(iGameLayout,downloadListener);
                break;
            case FileDownloadStatus.completed://安装
                installOrOpen(tasksManagerModel);
                break;
            case FileDownloadStatus.blockComplete://安装
                installOrOpen(tasksManagerModel);
                break;
            case FileDownloadStatus.retry://重试
//                start(iGameLayout,downloadListener);
                confirmDown(iGameLayout,downloadListener);
                break;
            case FileDownloadStatus.error://重试
                confirmDown(iGameLayout,downloadListener);
//                start(iGameLayout,downloadListener);
                break;
            case FileDownloadStatus.warn://已经在下载或者在队列中了，丢失下载进度和状态记录，进行重新连接
//                start(iGameLayout,downloadListener);
                confirmDown(iGameLayout,downloadListener);
                break;
            case FileDownloadStatus.INVALID_STATUS:
                confirmDown(iGameLayout,downloadListener);
//                start(iGameLayout,downloadListener);
                break;
        }
    }
    private static void installOrOpen(TasksManagerModel tasksManagerModel){
        //判断包名是否已经存入，可能由于下载成功时意外没有收到回调，导致包名没有设置成功，需要重新设置
        if(TextUtils.isEmpty(tasksManagerModel.getPackageName())
                &&!TextUtils.isEmpty(tasksManagerModel.getPath())){
            File file=new File(tasksManagerModel.getPath());
            if(file.exists()){//文件存在
                String packageNameByApkFile = BaseAppUtil.getPackageNameByApkFile(BaseApplication.getInstance(), tasksManagerModel.getPath());
                tasksManagerModel.setPackageName(packageNameByApkFile);
                TasksManager.getImpl().updateTask(tasksManagerModel);
            }
        }
        //判断是启动还是安装
        if(BaseAppUtil.isInstallApp(BaseApplication.getInstance(),tasksManagerModel.getPackageName())&&tasksManagerModel.getStatus()==TasksManagerModel.STATUS_INSTALLED){
            BaseAppUtil.openAppByPackageName(BaseApplication.getInstance(),tasksManagerModel.getPackageName());
        }else{
            //进行安装
            //记录是从app盒子安装的
            HsApplication instance = (HsApplication) HsApplication.getInstance();
            String packageNameByApkFile = BaseAppUtil.getPackageNameByApkFile(instance, tasksManagerModel.getPath());
            int versionCodeFromApkFile = ChannelNewUtil.getVersionCodeFromApkFile(instance, tasksManagerModel.getPath());
            InstallApkRecord installApkRecord=new InstallApkRecord(versionCodeFromApkFile,System.currentTimeMillis());
            instance.getInstallingApkList().put(packageNameByApkFile,installApkRecord);
            L.e("hongliang","记录的版本号为："+versionCodeFromApkFile);
            BaseAppUtil.installApk(BaseApplication.getInstance(),new File(tasksManagerModel.getPath()));
        }
    }
    private static void pause(int id) {
        FileDownloader.getImpl().pause(id);
    }
    public static synchronized void start(IGameLayout iGameLayout,FileDownloadListener downloadListener){
        try {
            TasksManagerModel taskModelByGameId = TasksManager.getImpl().getTaskModelByGameId(iGameLayout.getGameBean().getGameid());
            if(taskModelByGameId!=null){//已经下载过了
                iGameLayout.getGameBean().setUrl(taskModelByGameId.getUrl());
                if(iGameLayout.getGameBean().getUrl()==null){//url不空
                    getUrlAndDownload(iGameLayout.getGameBean(),downloadListener);
                }else{
                    createDownTask(iGameLayout.getGameBean(),downloadListener).start();
                }
            }else{//没有下载过，或者已经被删除了
                if(iGameLayout.getGameBean().getUrl()==null){//url不空
                    getUrlAndDownload(iGameLayout.getGameBean(),downloadListener);
                }else{//需要重新插入下载数据到数据库
                    GameBean gameBean = iGameLayout.getGameBean();
                    taskModelByGameId= TasksManager.getImpl().addTask(gameBean.getGameid(), gameBean.getGamename(), gameBean.getIcon(), gameBean.getUrl());
                    if(taskModelByGameId!=null){
                        createDownTask(iGameLayout.getGameBean(),downloadListener).start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建一个下载任务
     * @param gameBean
     * @return
     */
    private static BaseDownloadTask createDownTask(GameBean gameBean,FileDownloadListener downloadListener){

        int downloadId = TasksManager.getImpl().getDownloadId(gameBean.getUrl());
        BaseDownloadTask.IRunningTask task = FileDownloadList.getImpl().get(downloadId);
        BaseDownloadTask baseDownloadTask=null;
        if(task!=null &&task instanceof BaseDownloadTask){//如果已经在队列中，取出来
            baseDownloadTask = (BaseDownloadTask) task;
            L.d(TAG,"队列中已有");
        }else{
            baseDownloadTask= FileDownloader.getImpl().create(gameBean.getUrl());
            baseDownloadTask.setCallbackProgressTimes(100);
            baseDownloadTask.setMinIntervalUpdateSpeed(400);
            baseDownloadTask.setTag(GlobalMonitor.TAG_GAME_ID,gameBean.getGameid());
            //设置下载文件放置路径
            baseDownloadTask.setPath(FileDownloadUtils.getDefaultSaveFilePath(gameBean.getUrl()));
            if(TextUtils.isEmpty(gameBean.getDiscount())){
                gameBean.setDowncnt("0");
            }
            baseDownloadTask.setTag(GlobalMonitor.TAG_GAME_DOWNLOAD_COUNT,gameBean.getDowncnt());
        }
        if(downloadListener!=null){
            baseDownloadTask.setListener(downloadListener);
        }
        return baseDownloadTask;
    }

    private static void getUrlAndDownload(GameBean gameBean, FileDownloadListener downloadListener) {
        final WeakReference<FileDownloadListener> listenerWk=new WeakReference<FileDownloadListener>(downloadListener);
        final WeakReference<GameBean> gameBeanWk=new WeakReference<GameBean>(gameBean);
        HttpParams httpParams = AppApi.getHttpParams(false);
        DeviceBean deviceBean = DeviceUtil.getDeviceBean(BaseApplication.getInstance());

        httpParams.put("verid", BaseAppUtil.getAppVersionCode());
        httpParams.put("gameid", gameBean.getGameid());
        httpParams.put("openudid", "");
        httpParams.put("devicetype", "");
        httpParams.put("idfa", "");
        httpParams.put("idfv", "");
        httpParams.put("mac", "");
        httpParams.put("resolution", "1024*768");
        httpParams.put("network", "WIFI");
        httpParams.put("userua", deviceBean.getUserua());
        NetRequest.request().setParams(httpParams).get(AppApi.GAME_DOWN,new HttpJsonCallBackDialog<DownLoadUrlBean>(){
            @Override
            public void onDataSuccess(DownLoadUrlBean data) {
                if(data.getData()!=null){
                    String url=data.getData().getUrl().trim();
                    String downloadCount=data.getData().getDowncnt();
                    if(gameBeanWk.get()!=null){
                        GameBean gameBean = gameBeanWk.get();
                        if(gameBean!=null){
                            gameBean.setUrl(url);
                            gameBean.setDowncnt(downloadCount);
                            //存入数据库
                            TasksManagerModel tasksManagerModel = TasksManager.getImpl().addTask(gameBean.getGameid(), gameBean.getGamename(), gameBean.getIcon(), gameBean.getUrl());
                            if(tasksManagerModel!=null){
                                createDownTask(gameBean,listenerWk.get()).start();
                            }else{
                                L.d(TAG,"error save fail");
                            }
                        }
                    }
                }
            }
        });
    }
}
