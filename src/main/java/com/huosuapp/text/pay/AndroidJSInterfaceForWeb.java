package com.huosuapp.text.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.game.sdk.log.T;
import com.huosuapp.text.pay.alipay.AlipayActivityForWap;
import com.huosuapp.text.pay.alipay.AlipayJsonParser;
import com.huosuapp.text.pay.wftpay.SpayJsonParser;
import com.huosuapp.text.pay.wftpay.WFTPayActivityForWap;
import com.liang530.event.NotProguard;
import com.liang530.log.L;
import com.liang530.utils.BaseAppUtil;


/**
 * author lingguihua
 * 
 * @time 2016/09/06
 */
@NotProguard
public class AndroidJSInterfaceForWeb {

	private static final String TAG = AndroidJSInterfaceForWeb.class
			.getSimpleName();
	public Activity ctx;
	public double money;
	public String orderNo;
	Handler handler=new Handler();
	public AndroidJSInterfaceForWeb(Activity ctx) {
		this.ctx = ctx;

	}

	/**
	 * 接口说明 这个接口是关闭web所依附的那个actvity
	 * */
	@JavascriptInterface
	public void closeSelfWindow() {
		ctx.finish();
	}

	/**
	 * 接口说明 这个接口是关闭web所依附的那个actvity
	 * */
	@JavascriptInterface
	public void closeSelfWindow(String toast) {
		ctx.finish();
		if (!TextUtils.isEmpty(toast))
			Toast.makeText(ctx, "" + toast, Toast.LENGTH_SHORT).show();
	}

	/***
	 * 这个接口之前就有，为了方便，我不改变接口名字 ：是复制礼包码的接口
	 * */
	@JavascriptInterface
	public void goToGift(String code) {

		if (android.os.Build.VERSION.SDK_INT > 11) {
			android.content.ClipboardManager mClipboard = (android.content.ClipboardManager) ctx
					.getSystemService(ctx.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData
					.newPlainText("simple text", code);
			mClipboard.setPrimaryClip(clip);
			Toast.makeText(ctx, "复制成功，请尽快使用", Toast.LENGTH_LONG).show();
		} else {
			ClipboardManager Mclipboard = (ClipboardManager) ctx
					.getSystemService(ctx.CLIPBOARD_SERVICE);

			Mclipboard.setText(code);

			Toast.makeText(ctx, "复制成功，请尽快使用", Toast.LENGTH_LONG).show();
		}
	}

	/***
	 * @param payway
	 *            支付方式，见说明
	 * @param params
	 *            参数(一个json字符串，为什么这么定义，因为每个支付都有不同的参数，我需要一个json字符串，用json传参)
	 * @throws Exception
	 *             会抛出异常，这个要求代码使用者，不要习惯传参数，没有初始化成员变量（想了想还是捕捉回来好一点）
	 * @说明： 
	 *      这个函数目前支持的支付有：ptbpay,gamepay,alipay,spay,unionpay,payeco,heepay,shengpay
	 *      ,shengpayh5
	 * 
	 * */
	@JavascriptInterface
	public void callNativePay(String payway, String params) {
		L.d("callNativePay", "" + params);
		if (ctx == null) {
			try {
				throw new Exception("ctx==null");
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		L.d("callNativePay", "ctx is not null");
		if (TextUtils.isEmpty(params) || TextUtils.isEmpty(payway)) {
			Toast.makeText(
					ctx,
					"服务端出现了问题:====>"
							+ "call native method[callNativePay],params is null",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!payway.equals("ptbpay") && !payway.equals("gamepay")
				&& !payway.equals("alipay") && !payway.equals("spay")
				&& !payway.equals("unionpay") && !payway.equals("payeco")
				&& !payway.equals("heepay") && !payway.equals("shengpay")
				&& !payway.equals("shengpayh5")) {
			return;
		}
		L.d("callNativePay", "params is ok");
		PayInterface parser = null;
		PayBean bean = null;
		Intent intent = null;
		if (payway.equals("ptbpay")) {// 平台币
			return;
		}
		if (payway.equals("gamepay")) {// 游戏币

			return;
		}
		if (payway.equals("alipay")) {// 支付宝
			L.d("callNativePay", "call alipay");
			parser = new AlipayJsonParser();
			bean = (PayBean) parser.parseObj(params);
			intent = new Intent(ctx, AlipayActivityForWap.class);
			intent.putExtra("params", bean);
			ctx.startActivityForResult(intent,
					ChargeActivityForWap.REQUEST_CODE);
			return;
		}
		if (payway.equals("spay")) {// 威富通微信支付
			parser =(PayInterface) new SpayJsonParser();
			bean = (PayBean) parser.parseObj(params);
			intent = new Intent(ctx, WFTPayActivityForWap.class);
			intent.putExtra("params", bean);
			ctx.startActivityForResult(intent,
					ChargeActivityForWap.REQUEST_CODE);
			return;
		}
		if (payway.equals("unionpay")) {// 银行卡（纯银联）
			// mode
			// token_id
			// orderid
			return;
		}
		if (payway.equals("payeco")) {// 银行卡 易联支付
			// mode
			// token (uppayReq)
			// orderid
			return;
		}
		if (payway.equals("heepay")) {// 汇付宝微信支付(30)
			// heepayagentid
			// orderid
			// tokenid
			return;
		}
		if (payway.equals("heepayali")) {// 汇付宝支付宝(22)
			// heepayagentid
			// orderid
			// tokenid
			return;
		}
		if (payway.equals("shnegpay")) {// 盛付通收银台
			// orderJson (需要urlencode)
			// orderid
			// mode
			return;
		}
		if (payway.equals("shnegpayh5")) {// shengpay H5 是卡类支付收银台
			// orderJson (需要urlencode)
			// orderid
			return;
		}
		return;
	}
	@JavascriptInterface
	public void closeWeb(){
		handler.post(new Runnable() {
			@Override
			public void run() {
				ctx.finish();
			}
		});
	}

	/**
	 * 拨打电话，跳转到拨号界面
	 *
	 * @param ctx
	 * @param phoneNumber
	 */
	public static void callDial(Context ctx, String phoneNumber) {
		try {
			ctx.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@JavascriptInterface
	public void openQq(final String qq){
		handler.post(new Runnable() {
			@Override
			public void run() {
				try{
					String url="mqqwpa://im/chat?chat_type=wpa&uin="+qq;
					ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				}catch (Exception e){
					// 未安装手Q或安装的版本不支持
					Toast.makeText(ctx,"未安装手Q或安装的版本不支持",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	@JavascriptInterface
	public void openPhone(final String phone){
		handler.post(new Runnable() {
			@Override
			public void run() {
				callDial(ctx,phone);
			}
		});
	}
	@JavascriptInterface
	public void joinQqgroup(final String key){
		handler.post(new Runnable() {
			@Override
			public void run() {
				joinQQGroup(key);
			}
		});

	}

	/**
	 * 复制字符串
	 * @param data 要复制的文字
	 */
	@JavascriptInterface
	public void copyString(final String data){
		handler.post(new Runnable() {
			@Override
			public void run() {
				BaseAppUtil.copyToSystem(ctx,data);
				T.s(ctx,"复制成功");
			}
		});
	}
	/**
	 * 外部浏览器打开网页
	 * @param url 要打开的网址
	 */
	@JavascriptInterface
	public void outWeb(final String url){
		if(!TextUtils.isEmpty(url)){
			handler.post(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(url);
					intent.setData(content_url);
					ctx.startActivity(intent);
				}
			});
		}
	}


	/****************
	 *
	 * 发起添加群流程。群号：测试群(594245585) 的 key 为： n62NA_2zzhPfmNicq-sZLioBGiN2v7Oq
	 * 调用 joinQQGroup(n62NA_2zzhPfmNicq-sZLioBGiN2v7Oq) 即可发起手Q客户端申请加群 测试群(594245585)
	 *
	 * @param key 由官网生成的key
	 * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	 ******************/
	public boolean joinQQGroup(String key) {
		Intent intent = new Intent();
		intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
		// 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		try {
			ctx.startActivity(intent);
			return true;
		} catch (Exception e) {
			// 未安装手Q或安装的版本不支持
			Toast.makeText(ctx,"未安装手Q或安装的版本不支持",Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}
