package com.huosuapp.text.pay.wftpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.huosuapp.text.pay.CommonJsInterfaceForWeb;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;

import java.util.List;

/**
 * Created by liu hong liang on 2016/10/14.
 */

public class WftPayIml {
    private  double amount;
    private Activity mContext;
    private String   orderid;

    public WftPayIml(Activity mContext, String orderid, double amount) {
        this.mContext = mContext;
        this.orderid = orderid;
        this.amount=amount;
    }

    public void wftPayStart(String orderid, int amount, String token) {
        if (!isWeixinAvilible(mContext)) {
            Toast.makeText(mContext,"未安装微信",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestMsg msg = new RequestMsg();
        msg.setMoney(amount);
        msg.setTokenId(token);
        msg.setOutTradeNo(orderid);
        // 微信wap支付
        msg.setTradeType(MainApplication.PAY_WX_WAP);
        PayPlugin.unifiedH5Pay(mContext, msg);
    }
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
    /**
     * 微付通支付结果回调
     * @param requestCode
     * @param resultCode
     * @param data
     */

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            mContext.finish();
            return;
        }
        String respCode = data.getExtras().getString("resultCode");
        if (!TextUtils.isEmpty(respCode)
                && respCode.equalsIgnoreCase("success")) {
            CommonJsInterfaceForWeb.setPaySuccess("支付成功");
            //同步服务器结果
//            PayUtil.queryOrderResult(orderid,amount,mContext);
            mContext.finish();

        } else { // 其他状态NOPAY状态：取消支付，未支付等状态
            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
            CommonJsInterfaceForWeb.setPayFail(-1,"支付失败");
            mContext.finish();
        }
    }
}
