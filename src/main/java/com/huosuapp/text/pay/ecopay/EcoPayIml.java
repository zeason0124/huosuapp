package com.huosuapp.text.pay.ecopay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;

import com.huosuapp.text.pay.CommonJsInterfaceForWeb;
import com.liang530.log.L;
import com.payeco.android.plugin.PayecoPluginLoadingActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liu hong liang on 2016/10/14.
 */

public class EcoPayIml {
    private  double amount;
    private Activity mContext;
    private String token, orderid;

    /**
     * @Fields payecoPayBroadcastReceiver : 易联支付插件广播
     */
    private BroadcastReceiver payecoPayBroadcastReceiver;

    public EcoPayIml(Activity mContext, String token, String orderid,double amount) {
        this.mContext = mContext;
        this.token = token;
        this.orderid = orderid;
        this.amount=amount;
    }

    public void ecoPayStart() {
        Intent intent = new Intent(mContext, PayecoPluginLoadingActivity.class);
        intent.putExtra("upPay.Req", token);
        intent.putExtra("Broadcast", EcoConstant.BROADCAST_PAY_END); // 广播接收地址
        intent.putExtra("Environment", EcoConstant.ENVIRONMENT); // 00: 测试环境, 01:// 生产环境

        Configuration configuration = mContext.getResources().getConfiguration();
        if(configuration.orientation ==Configuration.ORIENTATION_LANDSCAPE){//横屏
            intent.putExtra("orientation_mode","land");
        }
        mContext.startActivity(intent);
        // 初始化支付结果广播接收器
        initPayecoPayBroadcastReceiver();
        // 注册支付结果广播接收器
        registerPayecoPayBroadcastReceiver();
    }

    /**
     * @Title registerPayecoPayBroadcastReceiver
     * @Description 注册广播接收器
     */
    private void registerPayecoPayBroadcastReceiver() {
        if (payecoPayBroadcastReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(EcoConstant.BROADCAST_PAY_END);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            mContext.registerReceiver(payecoPayBroadcastReceiver, filter);
        }// 非空的之后才会去注册
    }


    // 初始化支付结果广播接收器
    private void initPayecoPayBroadcastReceiver() {
        payecoPayBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // 接收易联支付插件的广播回调
                String action = intent.getAction();

                if (!EcoConstant.BROADCAST_PAY_END.equals(action)) {
                    L.e("test", "接收到广播，但与注册的名称不一致[" + action + "]");
                    return;
                }
                // 商户的业务处理
                String result = intent.getExtras().getString("upPay.Rsp");
                L.i("test", "接收到广播内容：" + result);

                String notifyParams = result;
                try {
                    JSONObject json = new JSONObject(notifyParams);


                    if (json.has("respCode")) {

                        String respCode = json.getString("respCode");
                        if ("W101".equals(respCode)) { //W101订单未支付，用户主动退出插件
                            CommonJsInterfaceForWeb.setPayFail(-1,"用户取消支付");
                            mContext.finish();
                            return;
                        }
                        if (!"0000".equals(respCode)) { //非0000，订单支付响应异常
                            String respDesc = json.getString("respDesc");
                            CommonJsInterfaceForWeb.setPayFail(-1,"支付失败");
                            mContext.finish();
//                                ll.addView(getRow("错误码", respCode));
//                                ll.addView(getRow("错误消息", respDesc));
                            return;
                        }
                    }

//                        if(json.has("OrderId")){
//                            ll.addView(getRow("订单号", json.getString("OrderId")));
//                        }
//                        if(json.has("Amount")){
//                            ll.addView(getRow("金额", json.getString("Amount")+"元"));
//                        }
//                        if(json.has("PayTime")){
//                            ll.addView(getRow("交易时间", json.getString("PayTime")));
//                        }
                    if (json.has("Status")) {
                        String status = "";
                        if ("01".equals(json.getString("Status"))) {
                            status = "未支付";
                        }
                        if ("02".equals(json.getString("Status"))) {
                            status = "已支付";
                            CommonJsInterfaceForWeb.setPaySuccess("支付成功");
                            //同步服务器结果
//                            PayUtil.queryOrderResult(orderid,amount,mContext);
                            mContext.finish();
                            return;
                        }
                        if ("03".equals(json.getString("Status"))) {
                            status = "已退款(全额撤销/冲正)";
                        }
                        if ("04".equals(json.getString("Status"))) {
                            status = "已过期";
                        }
                        if ("05".equals(json.getString("Status"))) {
                            status = "已作废";
                        }
                        if ("06".equals(json.getString("Status"))) {
                            status = "支付中";
                        }
                        if ("07".equals(json.getString("Status"))) {
                            status = "退款中";
                        }
                        if ("08".equals(json.getString("Status"))) {
                            status = "已被商户撤销";
                        }
                        if ("09".equals(json.getString("Status"))) {
                            status = "已被持卡人撤销";
                        }
                        if ("10".equals(json.getString("Status"))) {
                            status = "调账-支付成功";
                            CommonJsInterfaceForWeb.setPaySuccess("支付成功");
                            //同步服务器结果
//                            PayUtil.queryOrderResult(orderid,amount,mContext);
                            mContext.finish();
                        }
                        if ("11".equals(json.getString("Status"))) {
                            status = "调账-退款成功";
                        }
                        if ("12".equals(json.getString("Status"))) {
                            status = "已退货";
                        }
//                            ll.addView(getRow("交易状态", status));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CommonJsInterfaceForWeb.setPayFail(-1,"支付失败");
                    mContext.finish();
                }
            }
        };
    }

    /**
     * @Title unRegisterPayecoPayBroadcastReceiver
     * @Description 注销广播接收器
     */
    private void unRegisterPayecoPayBroadcastReceiver() {

        if (payecoPayBroadcastReceiver != null) {
            mContext.unregisterReceiver(payecoPayBroadcastReceiver);
            payecoPayBroadcastReceiver = null;
        }
    }

    public void onDestroy() {
        unRegisterPayecoPayBroadcastReceiver();
    }
}
