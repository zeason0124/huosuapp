package com.huosuapp.text.newpay.wftpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.huosuapp.text.newpay.IHuoPay;
import com.huosuapp.text.newpay.IPayListener;
import com.huosuapp.text.pay.CommonJsInterfaceForWeb;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by liu hong liang on 2016/10/14.
 */

public class WftPayIml implements IHuoPay {
    private Activity activity;
    private String orderId;
    private double money;
    private IPayListener iPayListener;
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void startPay(IPayListener listener,double money, String payinfo) {
        this.money = money;
        this.iPayListener = listener;
        if (!isWeixinAvilible(activity)) {
            Toast.makeText(activity,"未安装微信",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject jsonObject=new JSONObject(payinfo);
            this.orderId = jsonObject.optString("orderid");
            int amount = jsonObject.optInt("amount");
            String token = jsonObject.optString("token");
            RequestMsg msg = new RequestMsg();
            msg.setOutTradeNo(orderId);
            msg.setMoney(money);
            msg.setTokenId(token);
            // 微信wap支付
            msg.setTradeType(MainApplication.PAY_WX_WAP);
            PayPlugin.unifiedH5Pay(activity, msg);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity,"支付参数读取失败",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 微付通支付结果回调
     * @param requestCode
     * @param resultCode
     * @param data
     */

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            if(iPayListener!=null){
                iPayListener.payFail(orderId,money,"支付失败");
            }
            return;
        }
        String respCode = data.getExtras().getString("resultCode");
        if (!TextUtils.isEmpty(respCode)
                && respCode.equalsIgnoreCase("success")) {
            if(iPayListener!=null){
                CommonJsInterfaceForWeb.setPaySuccess("支付成功");
            }

        } else { // 其他状态NOPAY状态：取消支付，未支付等状态
            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
            if(iPayListener!=null){
                iPayListener.payFail(orderId,money,"支付失败");
            }
        }
    }
    @Override
    public void onDestory() {
        iPayListener=null;
    }
}
