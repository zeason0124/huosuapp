package com.huosuapp.text.pay.wftpay;


import android.text.TextUtils;

import com.huosuapp.text.pay.PayInterface;

import org.json.JSONException;
import org.json.JSONObject;


public class SpayJsonParser implements PayInterface {

	@Override
	public Object parseObj(String json) {
		if (TextUtils.isEmpty(json)) {
			return null;
		}
		// 模式一：
		// orderid
		// productname
		// productdesc
		// amount
		// notify_url 回调地址
		// DEFAULT_PARTNER 合作者id(native)
		// DEFAULT_SELLER 账号(native)
		try {
			JSONObject payParamsOjb = new JSONObject(json);
			if (payParamsOjb != null && payParamsOjb.length() > 0) {
				return parseJsonForSpay(payParamsOjb);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param payParamsOjb
	 *            用于参数
	 * */
	private Object parseJsonForSpay(JSONObject payParamsOjb) {
		PayParamBean bean = new PayParamBean();
		if (payParamsOjb != null && payParamsOjb.length() > 0) {
			try {
				bean.setOrderid(payParamsOjb.has("orderid") ? payParamsOjb
						.getString("orderid") : "");
				bean.setToken(payParamsOjb.has("token") ? payParamsOjb
						.getString("token") : "");
				bean.setAmount(payParamsOjb.has("amount") ? payParamsOjb
						.getString("amount") : "");
			
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		return bean;
	}

}
