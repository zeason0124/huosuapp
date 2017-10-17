package com.huosuapp.text.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.game.sdk.db.impl.AgentDbDao;
import com.game.sdk.domain.AgentDbBean;
import com.game.sdk.util.ChannelNewUtil;
import com.huosuapp.text.base.HsApplication;
import com.huosuapp.text.bean.DownInstallSuccessEvent;
import com.huosuapp.text.bean.DownStatusChangeEvent;
import com.huosuapp.text.bean.InstallApkRecord;
import com.huosuapp.text.bean.TasksManagerModel;
import com.huosuapp.text.db.TasksManager;
import com.liang530.log.L;

import org.greenrobot.eventbus.EventBus;

/**
 * author janecer
 * 2014年4月18日上午11:58:45
 */
public class AppInstallReceiver extends BroadcastReceiver {
	private static final String TAG = "AppInstallReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			String packageName = intent.getDataString().substring(8);//package:com.xiaocaohy.huosuapp
			L.d(TAG,"安装成功："+packageName);
			HsApplication instance = (HsApplication) HsApplication.getInstance();
			InstallApkRecord installApkRecord = instance.getInstallingApkList().get(packageName);
			if(installApkRecord==null){
				L.d(TAG,"安装成功："+packageName+"  但不是app盒子安装的");
				return;
			}
			if(System.currentTimeMillis()-installApkRecord.getInstallTime()<=1000*60*15){
				TasksManagerModel taskModelByKeyVal = TasksManager.getImpl().getTaskModelByKeyVal(TasksManagerModel.PACKAGE_NAME, packageName);
				if(taskModelByKeyVal!=null){
					taskModelByKeyVal.setStatus(TasksManagerModel.STATUS_INSTALLED);//设置安装成功！
					TasksManager.getImpl().updateTask(taskModelByKeyVal);
					//通知对应的界面展示启动
					EventBus.getDefault().post(new DownStatusChangeEvent(taskModelByKeyVal.getId(),taskModelByKeyVal.getGameId(),null));
					//通知下载管理更新显示
					EventBus.getDefault().post(new DownInstallSuccessEvent(taskModelByKeyVal));
					L.d(TAG,"更新数据库安装成功记录："+taskModelByKeyVal.getGameName());

					//安装成功，记录到外部数据库，供sdk获取(这里需要使用加密的agent)
					AgentDbBean agentDbBean=new AgentDbBean(packageName,installApkRecord.getVersionCode()+"_0", ChannelNewUtil.getEncryptAgentBySp(context));
					AgentDbDao.getInstance(context).addOrUpdate(agentDbBean);
				}
			}else{
				L.d(TAG,"安装成功："+packageName+"  超时安装的，不做记录");
			}
		}else if(intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")){//应用删除
		}
	}
}
