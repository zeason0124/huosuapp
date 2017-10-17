package com.huosuapp.text.pay.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.huosuapp.text.pay.CommonJsInterfaceForWeb;
import com.huosuapp.text.pay.ThreadPoolManager;


/**
 * Created by liu hong liang on 2016/10/14.
 */

public class AlipayIml {
    private final double amount;
    private Activity mContext;
    private String orderid;
    private String alipayparam;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            PayResult payResult = new PayResult((String) msg.obj);
            /**
             * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
             * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
             * docType=1) 建议商户依赖异步通知
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息

            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
            if (TextUtils.equals(resultStatus, "9000")) {
                CommonJsInterfaceForWeb.setPayFail(-1, "充值成功，等待服务器接收充值结果");
                mContext.finish();
            } else {
                // 判断resultStatus 为非"9000"则代表可能支付失败
                // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                if (TextUtils.equals(resultStatus, "8000")) {
                    CommonJsInterfaceForWeb.setPayFail(8000, "支付失败");
                    mContext.finish();
                } else {
                    CommonJsInterfaceForWeb.setPayFail(-1, "用户取消支付");
                    mContext.finish();
                }
            }
        }
    };

    public AlipayIml(Activity mContext, String orderid, double amount) {
        this.mContext = mContext;
        this.orderid = orderid;
        this.amount=amount;
    }

    public void alipayStart(final String params) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mContext);
                // 调用支付接口
                String result = alipay.pay(params, true);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
    }

}
