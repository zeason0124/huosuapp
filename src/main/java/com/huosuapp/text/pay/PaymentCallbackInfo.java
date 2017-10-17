package com.huosuapp.text.pay;

import com.liang530.event.NotProguard;

/**
 * author janecer 2014-3-29下午4:10:11 用户充值成功回调消息体
 */
@NotProguard
public class PaymentCallbackInfo {
	public String msg;// 回调消息提示

	public double money;// 充值金额数
}
