package com.huosuapp.text.newpay.alipay;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.huosuapp.text.newpay.IHuoPay;
import com.huosuapp.text.newpay.IPayListener;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by liu hong liang on 2016/10/14.
 */

public class AliPayIml implements IHuoPay {
    private Activity activity;
    private String orderId;
    private double money;
    private IPayListener iPayListener;

    @Override
    public void startPay(IPayListener listener, double money, String payinfo) {
        this.iPayListener=listener;
        this.money=money;
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(payinfo);
            String alipayparam=jsonObject.optString("alipayparam");
            this.orderId=jsonObject.optString("orderid");
            //异步任务调用
            // 构造PayTask 对象
            PayTask alipay = new PayTask(activity);
            // 调用支付接口
            String result = alipay.pay(alipayparam, true);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity,"支付参数读取失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestory() {
        //销毁异步任务
    }
}
