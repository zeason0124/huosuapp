package com.huosuapp.text.util;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.webkit.WebView;

import com.huosuapp.text.bean.DeviceBean;

import java.io.UnsupportedEncodingException;

/**
 * Created by liu hong liang on 2016/11/19.
 */

public class DeviceUtil {
    public static DeviceBean getDeviceBean(Context context){
        // 设备管理器
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        DeviceBean deviceBean=new DeviceBean();
        deviceBean.setDevice_id(telephonyManager.getDeviceId());
        deviceBean.setUserua(getUserUa(context));
        deviceBean.setDeviceinfo(telephonyManager.getLine1Number() + "||android" + Build.VERSION.RELEASE);
        return deviceBean;
    }
    /**
     * 获取ua信息
     *
     * @throws UnsupportedEncodingException
     */
    public static String getUserUa(Context context) {
        WebView webview = new WebView(context);
        webview.layout(0, 0, 0, 0);
        String str = webview.getSettings().getUserAgentString();
        return str;
    }
}
