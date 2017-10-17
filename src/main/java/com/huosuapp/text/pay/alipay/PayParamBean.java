package com.huosuapp.text.pay.alipay;


import com.huosuapp.text.pay.PayBean;

import java.io.Serializable;

import com.liang530.event.NotProguard;

@NotProguard
public class PayParamBean extends PayBean implements Serializable {
	private static final long serialVersionUID = -1L;
	private String orderid = "";
	private String productname = "";
	private String productdesc = "";
	private String amount = "";
	private String notify_url = "";

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getProductdesc() {
		return productdesc;
	}

	public void setProductdesc(String productdesc) {
		this.productdesc = productdesc;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	@Override
	public String toString() {
		return "PayParamBean [orderid=" + orderid + ", productname="
				+ productname + ", productdesc=" + productdesc + ", amount="
				+ amount + ", notify_url=" + notify_url + "]";
	}

	@Override
	public Object getDetailParams(Object params) {
		// TODO Auto-generated method stub
		return params;
	}

}
