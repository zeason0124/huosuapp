package com.huosuapp.text.sms;

import android.app.Activity;

import com.huosuapp.text.bean.ValidateCodeBean;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.util.AuthCodeUtil;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;

/**
 * Created by liu hong liang on 2016/11/8.
 */

public class DefaultSmsIml implements ISms {
    @Override
    public void sendSms(Activity activity, String phone, SmsCallBack callBack, String... flag) {
        HttpParams params= AppApi.getHttpParams(false);
        params.put("type",1);
        params.put("mobile", AuthCodeUtil.authcodeEncode(phone,AppApi.appkey));
        NetRequest.request(this).setParams(params).showDialog(true).post(AppApi.SMSCODE_SEND,new HttpJsonCallBackDialog<ValidateCodeBean>(){
            @Override
            public void onDataSuccess(ValidateCodeBean data) {
                if(data!=null&&data.getData()!=null){
//                    sessionId=data.getData().getSessionid();
//                    changeCodeBtn(60);
                }
//                else if(data!=null&& !TextUtils.isEmpty(data.getMsg())){
//                    T.s(activity,data.getMsg());
//                }
            }
        });
    }

    @Override
    public void onDestory() {

    }
}
