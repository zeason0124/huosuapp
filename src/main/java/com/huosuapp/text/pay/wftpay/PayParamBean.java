package com.huosuapp.text.pay.wftpay;


import com.huosuapp.text.pay.PayBean;

import java.io.Serializable;

import com.liang530.event.NotProguard;

@NotProguard
public class PayParamBean extends PayBean implements Serializable {
	private static final long serialVersionUID = -1L;
	private String orderid = "";
	private String token = "";
	private String amount = "";

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getToken() {
		return token;
	}

	@Override
	public String toString() {
		return "PayParamBean [orderid=" + orderid + ", token=" + token
				+ ", amount=" + amount + "]";
	}

	public void setToken(String token_id) {
		this.token = token_id;
	}

	@Override
	public Object getDetailParams(Object params) {
		// TODO Auto-generated method stub
		return params;
	}

}
