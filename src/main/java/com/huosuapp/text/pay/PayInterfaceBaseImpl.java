package com.huosuapp.text.pay;

import android.text.TextUtils;


public class PayInterfaceBaseImpl implements PayInterface {

	@Override
	public Object parseObj(String json) {
		if(TextUtils.isEmpty(json)){
			return null;
		}
		return json;
	}

}
