package com.huosuapp.text.newpay.ecopay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.widget.Toast;

import com.huosuapp.text.newpay.IHuoPay;
import com.huosuapp.text.newpay.IPayListener;
import com.huosuapp.text.pay.ecopay.EcoConstant;
import com.liang530.log.L;
import com.payeco.android.plugin.PayecoPluginLoadingActivity;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by liu hong liang on 2016/10/14.
 */

public class EcoPayIml implements IHuoPay {
    public static final String ENVIRONMENT = "01";// "00"测试环境 ."01"生产环境
    public final static String BROADCAST_PAY_END = "com.merchant.broadcast";// 广播
    /**
     * @Fields payecoPayBroadcastReceiver : 易联支付插件广播
     */
    private BroadcastReceiver payecoPayBroadcastReceiver;
    private Activity activity;
    private String orderId;
    private double money;
    private IPayListener iPayListener;
    @Override
    public void startPay(IPayListener listener, double money, String payinfo) {
        this.money = money;
        this.iPayListener = listener;
        try {
            JSONObject jsonObject=new JSONObject(payinfo);
            this.orderId = jsonObject.optString("orderid");
            String token = jsonObject.optString("token");
            Intent intent = new Intent(activity, PayecoPluginLoadingActivity.class);
            intent.putExtra("upPay.Req", token);
            intent.putExtra("Broadcast", EcoConstant.BROADCAST_PAY_END); // 广播接收地址
            intent.putExtra("Environment", EcoConstant.ENVIRONMENT); // 00: 测试环境, 01:// 生产环境
            Configuration configuration = activity.getResources().getConfiguration();
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
                intent.putExtra("orientation_mode", "land");
            }
            activity.startActivity(intent);
            // 初始化支付结果广播接收器
            initPayecoPayBroadcastReceiver();
            // 注册支付结果广播接收器
            registerPayecoPayBroadcastReceiver();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity,"支付参数读取失败",Toast.LENGTH_SHORT).show();
        }
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
            activity.registerReceiver(payecoPayBroadcastReceiver, filter);
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
                            if (iPayListener != null) {
                                iPayListener.payFail(orderId, money, "用户取消支付");
                            }
                            return;
                        }
                        if (!"0000".equals(respCode)) { //非0000，订单支付响应异常
                            String respDesc = json.getString("respDesc");
                            if (iPayListener != null) {
                                iPayListener.payFail(orderId, money, "支付失败");
                            }
                            return;
                        }
                    }
                    if (json.has("Status")) {
                        String status = "";
                        if ("01".equals(json.getString("Status"))) {
                        }
                        if ("02".equals(json.getString("Status"))) {
                            if (iPayListener != null) {
                                iPayListener.paySuccess(orderId, money);
                            }
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
                            if (iPayListener != null) {
                                iPayListener.paySuccess(orderId, money);
                            }
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
                }
                if (iPayListener != null) {
                    iPayListener.payFail(orderId, money, "支付失败");
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
            activity.unregisterReceiver(payecoPayBroadcastReceiver);
            payecoPayBroadcastReceiver = null;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestory() {
        //销毁异步任务
        unRegisterPayecoPayBroadcastReceiver();
        iPayListener=null;
    }
}
