package com.huosuapp.text.sms;

import android.app.Activity;

/**
 * Created by liu hong liang on 2016/11/8.
 * 发送短信通用接口
 */

public interface ISms {
    void sendSms( Activity activity,String phone, SmsCallBack callBack,String...flag);
    void onDestory();
    /**
     * 内部的短信注册借口回调
     * */
    interface SmsCallBack {
        void onSmsSuccess();
        void onSmsFail(String code,String msg);
    }
}
