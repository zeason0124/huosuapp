package com.huosuapp.text.pay.alipay;


import com.huosuapp.text.pay.PayInterface;
import com.ta.utdid2.android.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class AlipayJsonParser implements PayInterface {

	@Override
	public Object parseObj(String json) {
		if (StringUtils.isEmpty(json)) {
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
				return parseJsonForAli(payParamsOjb);
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
	private Object parseJsonForAli(JSONObject payParamsOjb) {
		PayParamBean bean = new PayParamBean();
		if (payParamsOjb != null && payParamsOjb.length() > 0) {
			try {
				bean.setOrderid(payParamsOjb.has("orderid") ? payParamsOjb
						.getString("orderid") : "");
				bean.setProductname(payParamsOjb.has("productname") ? payParamsOjb
						.getString("productname") : "");
				bean.setProductdesc(payParamsOjb.has("productdesc") ? payParamsOjb
						.getString("productdesc") : "");
				bean.setAmount(payParamsOjb.has("amount") ? payParamsOjb
						.getString("amount") : "");
				bean.setNotify_url(payParamsOjb.has("notify_url") ? payParamsOjb
						.getString("notify_url") : "");
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		return bean;
	}

}
