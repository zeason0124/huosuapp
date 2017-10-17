/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huosuapp.text.listener;

import android.util.Log;

import com.game.sdk.util.ChannelNewUtil;
import com.huosuapp.text.base.HsApplication;
import com.huosuapp.text.bean.DownStatusChangeEvent;
import com.huosuapp.text.bean.InstallApkRecord;
import com.huosuapp.text.bean.TasksManagerModel;
import com.huosuapp.text.db.TasksManager;
import com.liang530.application.BaseApplication;
import com.liang530.log.L;
import com.liang530.utils.BaseAppUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadMonitor;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import org.greenrobot.eventbus.EventBus;

import java.io.File;



/**
 * Created by liu hong liang on 2016/9/29.
 * 全局的文件下载监听器,将在非主线程回调
 */
public class GlobalMonitor implements FileDownloadMonitor.IMonitor {
    public final static int TAG_GAME_ID=0;
    public final static int TAG_GAME_DOWNLOAD_COUNT=1;

    private final static class HolderClass {
        private final static GlobalMonitor INSTANCE = new GlobalMonitor();
    }

    public static GlobalMonitor getImpl() {
        return HolderClass.INSTANCE;
    }

    private final static String TAG = "GlobalMonitor";

    /**
     * 队列方式启动下载
     * @param count
     * @param serial
     * @param lis
     */
    @Override
    public void onRequestStart(int count, boolean serial, FileDownloadListener lis) {

        Log.d(TAG, String.format("on request start %d %B", count, serial));
    }

    /**
     * 单个任务启动下载
     * @param task
     */
    @Override
    public void onRequestStart(BaseDownloadTask task) {
        L.d(TAG,"任务启动："+task.getTag(TAG_GAME_ID));

    }

    /**
     * 将会在内部接收并开始task的时候回调这个方法(会在pending回调之前)
     * @param task
     */
    @Override
    public void onTaskBegin(BaseDownloadTask task) {
        L.d(TAG,"任务等待下载:"+task.getTag(TAG_GAME_ID)+" status="+task.getStatus());
        Object tagId = task.getTag(TAG_GAME_ID);
        Object tagCount = task.getTag(TAG_GAME_DOWNLOAD_COUNT);
        if(tagId!=null&& tagId instanceof String &&tagCount!=null&& tagCount instanceof String){
            EventBus.getDefault().post(new DownStatusChangeEvent(task.getId(),(String)tagId,(String)tagCount));
        }
    }

    /**
     * 将会在task结束pending开始task的runnable的时候回调该方法
     * @param task
     */
    @Override
    public void onTaskStarted(BaseDownloadTask task) {
        Object tagId = task.getTag(TAG_GAME_ID);
        Object tagCount = task.getTag(TAG_GAME_DOWNLOAD_COUNT);
        L.d(TAG,"任务开始下载: "+tagId); //重新下载的时候一定会走，6
        //获得下载的文件大小：
        L.e(TAG,"任务开始下载: "+tagId+" 文件大小为："+ TasksManager.getImpl().getTotal(task.getId())); //重新下载的时候一定会走，6
        if(tagId!=null&& tagId instanceof String &&tagCount!=null&& tagCount instanceof String){
            EventBus.getDefault().post(new DownStatusChangeEvent(task.getId(),(String)tagId,(String)tagCount));
        }
    }

    /**
     * 将会在task走完所有生命周期是回调这个方法
     * //任务停下来的时候一定会走，暂停-2，下载成功-2，-3两次   警告：-4
     * @param task
     */
    @Override
    public void onTaskOver(BaseDownloadTask task) {

        L.d(TAG,"任务结束："+task.getStatus());
        L.e(TAG,"任务结束: "+task.getId()+" 文件大小为："+ TasksManager.getImpl().getTotal(task.getId()));
        Object tagId = task.getTag(TAG_GAME_ID);
        Object tagCount = task.getTag(TAG_GAME_DOWNLOAD_COUNT);
        if(tagId!=null&& tagId instanceof String &&tagCount!=null&& tagCount instanceof String){

            byte status = task.getStatus();
            if(status== FileDownloadStatus.completed){
                //处理包名，存入数据库
                TasksManagerModel taskModelById = TasksManager.getImpl().getTaskModelById(task.getId());
                if(taskModelById!=null){
                    taskModelById.setPackageName(BaseAppUtil.getPackageNameByApkFile(BaseApplication.getInstance(),task.getPath()));
                    taskModelById.setGameSize(task.getLargeFileTotalBytes()+"");
                    TasksManager.getImpl().updateTask(taskModelById);
                    L.d("更新后的游戏大小="+TasksManager.getImpl().getTaskModelById(taskModelById.getId()).getGameSize());
                    L.d(TAG,taskModelById.getGameName()+" 保存包名成功="+ BaseAppUtil.getPackageNameByApkFile(BaseApplication.getInstance(),task.getPath()));
                }else{
                    L.d(TAG,"error not find task by id="+task.getId());
                }
                //进行安装
                //记录是从app盒子安装的
                HsApplication instance = (HsApplication) HsApplication.getInstance();
                String packageNameByApkFile = BaseAppUtil.getPackageNameByApkFile(instance, taskModelById.getPath());
                int versionCodeFromApkFile = ChannelNewUtil.getVersionCodeFromApkFile(instance, taskModelById.getPath());
                InstallApkRecord installApkRecord=new InstallApkRecord(versionCodeFromApkFile,System.currentTimeMillis());
                instance.getInstallingApkList().put(packageNameByApkFile,installApkRecord);
                L.e("hongliang","记录的版本号为："+versionCodeFromApkFile);
                BaseAppUtil.installApk(BaseApplication.getInstance(),new File(task.getPath()));
            }
            EventBus.getDefault().post(new DownStatusChangeEvent(task.getId(),(String)tagId,(String)tagCount));
            L.d(TAG,"任务结束:"+tagId);
        }
    }
}
