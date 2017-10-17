package com.huosuapp.text.pay.wftpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.huosuapp.text.R;
import com.huosuapp.text.pay.ChargeActivityForWap;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;

import java.util.List;

public class WFTPayActivityForWap extends Activity {
	String TAG = "WFTPayActivityForWap";
	private Intent data  = new Intent();
	PayParamBean bean = null;
	private Context atx;

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle intent) {
		super.onCreate(intent);
		setContentView(R.layout.ttw_alipay_pay);
		this.atx = this;
		Intent data = getIntent();
		bean = (PayParamBean) data.getSerializableExtra("params");
		pay();
	}

	/**
	 * 发起支付
	 * */
	private void pay() {
		payByHuoService();
	}

	private void payByHuoService() {
		if (!isWeixinAvilible(atx)) {
			return;
		}
		RequestMsg msg = new RequestMsg();
		msg.setMoney(Double.parseDouble(bean.getAmount()));
		msg.setTokenId(bean.getToken());
		msg.setOutTradeNo(bean.getOrderid());
		// 微信wap支付
		msg.setTradeType(MainApplication.PAY_WX_WAP);
		PayPlugin.unifiedH5Pay(WFTPayActivityForWap.this, msg);

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
	String payOrderNo;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (data == null) {
			this.finish();
			return;
		}

		String respCode = data.getExtras().getString("resultCode");
		if (!TextUtils.isEmpty(respCode)
				&& respCode.equalsIgnoreCase("success")) {
			data.putExtra("amount", Double.parseDouble(bean.getAmount()));
			data.putExtra("result", true);
			data.putExtra("attach", "支付成功");
			setResult(ChargeActivityForWap.REQUEST_CODE, data);
			this.finish();
		} else { // 其他状态NOPAY状态：取消支付，未支付等状态
			// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
			data.putExtra("amount", Double.parseDouble(bean.getAmount()));
			data.putExtra("result", false);
			data.putExtra("attach", "取消支付");
			setResult(ChargeActivityForWap.REQUEST_CODE, data);
			this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
