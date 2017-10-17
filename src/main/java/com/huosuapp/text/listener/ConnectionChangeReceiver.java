package com.huosuapp.text.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.huosuapp.text.bean.NetConnectEvent;
import com.huosuapp.text.bean.TasksManagerModel;
import com.huosuapp.text.db.TasksManager;
import com.liang530.log.L;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author Javen
 * 
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
    private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {//在此监听wifi有无
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    L.e("start","wifi WIFI_STATE_DISABLED");
                    EventBus.getDefault().post(new NetConnectEvent(NetConnectEvent.TYPE_STOP));
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    L.e("start","wifi WIFI_STATE_ENABLING");
                    break;

                case WifiManager.WIFI_STATE_UNKNOWN:
                    break;
            }
        }
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(wifiNetInfo.isConnected()){
                //获取所有下载状态为error状态的进行恢复下载
                List<TasksManagerModel> allTasks = TasksManager.getImpl().getAllTasks();
                if(allTasks==null) return;
                EventBus.getDefault().post(new NetConnectEvent(NetConnectEvent.TYPE_START));
                L.e("start","发送了恢复通知");
            }
        }
    }
}