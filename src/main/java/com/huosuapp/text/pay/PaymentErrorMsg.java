package com.huosuapp.text.pay;

import com.liang530.event.NotProguard;

/**
 * author janecer 2014-3-29下午4:10:41 充值失败的消息提示
 */
@NotProguard
public class PaymentErrorMsg {
	public int code;// 状态码
	public String msg;// 失败的消息提示
	public double money;// 原本充值的金额数量
}
