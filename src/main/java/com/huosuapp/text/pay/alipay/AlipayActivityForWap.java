package com.huosuapp.text.pay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.huosuapp.text.R;
import com.huosuapp.text.pay.ChargeActivityForWap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AlipayActivityForWap extends Activity {

	private static final String TAG = AlipayActivityForWap.class
			.getSimpleName();
	private static final int RQF_PAY = 1000;
	private PayParamBean bean = null;

	private static final int SDK_PAY_FLAG = 1000;
	private static final int SDK_CANCLE_FLAG = 1000;
	private Intent data  = new Intent();;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
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
					data.putExtra("amount", Double.parseDouble(bean.getAmount()));
					data.putExtra("result", true);
					data.putExtra("attach", "支付成功");
					setResult(ChargeActivityForWap.REQUEST_CODE, data);
					AlipayActivityForWap.this.finish();
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						data.putExtra("amount", Double.parseDouble(bean.getAmount()));
						data.putExtra("result", false);
						data.putExtra("attach", "支付失败");
						AlipayActivityForWap.this.finish();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						data.putExtra("amount", Double.parseDouble(bean.getAmount()));
						data.putExtra("result", false);
						data.putExtra("attach", "取消支付");
						AlipayActivityForWap.this.finish();
					}
				}
				break;
			}
			default:
//				// 失败代码
//				data.putExtra("amount", Double.parseDouble(bean.getAmount()));
//				data.putExtra("result", false);
//				data.putExtra("attach", "支付出现未知错误");
//				AlipayActivityForWap.this.finish();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ttw_alipay_pay);

		Intent intent = getIntent();
		bean = (PayParamBean) intent.getSerializableExtra("params");
		if (bean != null) {
			payTask();
		} else {
			this.finish();
		}
	}

	/**
	 * 确定支付，将相关信息发送到支付宝服务端
	 */
	private void payTask() {
		try {
			String info = getNewOrderInfo();
			String sign = SignUtils.sign(info, Key.PRIVATE);
			// String sign = Rsa.sign(info, Key.PRIVATE);

			sign = URLEncoder.encode(sign, "UTF-8");

			// info += "&sign=\"" + sign + "\"&" + getSignType();
			String payInfo = info + "&sign=\"" + sign + "\"&" + getSignType();

			Log.i("ExternalPartner", "start pay");
			// start the pay.
			// Log.i(TAG, "info = " + info);

			final String orderInfo = payInfo;

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// 构造PayTask 对象
					PayTask alipay = new PayTask(AlipayActivityForWap.this);

					// 调用支付接口
					String result = alipay.pay(orderInfo, true);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}).start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "支付失败！" + ex, Toast.LENGTH_SHORT).show();
			this.finish();
		}
	}

	private String getNewOrderInfo() throws UnsupportedEncodingException {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + Key.DEFAULT_PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + Key.DEFAULT_SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + bean.getOrderid() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + bean.getProductname() + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + bean.getProductdesc() + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + bean.getAmount() + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\""
				+ URLEncoder.encode(bean.getNotify_url(), "UTF-8") + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		// orderInfo += "&return_url=\"m.alipay.com\"";

		return orderInfo;
	}

	private String getSignType() {

		return "sign_type=\"RSA\"";
	}
}
