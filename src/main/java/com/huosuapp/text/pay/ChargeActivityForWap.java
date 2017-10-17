package com.huosuapp.text.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.bean.DeviceBean;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.util.AppLoginControl;
import com.huosuapp.text.util.DeviceUtil;
import com.liang530.application.BaseApplication;
import com.liang530.log.L;
import com.liang530.log.T;
import com.liang530.utils.BaseAppUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * @author lingguihua
 * @time 2016/09/08
 * @说明： 这个activity要接受两个参数，一个是mem_id(也就是账号，或者是账号的变体),另外一个是token(也就是密码，或者说密码的变体)
 * */
public class ChargeActivityForWap extends Activity implements OnClickListener {
	private static final String TAG = "ChargeActivityForWap";
	private WebView wv;
	private TextView tv_back, tv_charge_title;
	private ImageView iv_cancel;
	private String url, title;
	private String timestamp, hstoken;
	private double amount;
	private String toast;
	HashMap<String, String> header = new HashMap<String, String>();
	StringBuilder postDate = new StringBuilder();


	private static WeakReference<OnPaymentListener> paymentListener;// 充值接口监听
	public boolean isPaySus = false;// 支付jar包的回执
	public static final int REQUEST_CODE = 200;
	private String gameId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sdk_float_web);
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		title = intent.getStringExtra("titleName");
		gameId = intent.getStringExtra("gameId");

		// timestamp = intent.getStringExtra("timestamp");
		// hstoken = intent.getStringExtra("hs-token");

		wv = (WebView) findViewById(R.id.wv_content);

		tv_back = (TextView) findViewById(R.id.tv_back);

		iv_cancel = (ImageView) findViewById(R.id.iv_cancel);

		tv_charge_title = (TextView) findViewById(R.id.tv_charge_title);
		tv_charge_title.setText(title);
		tv_back.setOnClickListener(this);
		iv_cancel.setOnClickListener(this);

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

					// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
					if (url.contains("wpa")) {
						Uri uri = Uri.parse(url);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
						return true;
					}
					view.loadUrl(url);
					return true;
			}
		});

		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setLoadsImagesAutomatically(true);
		wv.getSettings().setAppCacheEnabled(true);
		wv.getSettings().setDomStorageEnabled(true);
		wv.getSettings().setDefaultTextEncodingName("UTF-8");
		wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		AndroidJSInterfaceForWeb js = new AndroidJSInterfaceForWeb(this);
		wv.addJavascriptInterface(js, "huosdk");
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
//				if (dialog == null) {
//					dialog = new CustomProgressDialog(ChargeActivityForWap.this,
//							"正在加载中.....", R.anim.donghua_frame);
//				}
//				dialog.show();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				if (url.contains("wpa")) {
					Uri uri = Uri.parse(url);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
					return true;
				}
				view.loadUrl(url);
				return true;
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
//				dialog.dismiss();
				webviewCompat(wv);
			}

		});
		wv.setWebChromeClient(new WebChromeClient());
		webviewCompat(wv);
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();// 移除
		header.put("timestamp", AppLoginControl.getTimestamp());
		String token = AppLoginControl.getHsToken();
		header.put("hs-token", token + "");
		String clientid = AppApi.clientid;
		String appid = AppApi.appid;
		String agent = AppApi.agent;
		String from = AppApi.from;
		if(gameId==null){
			gameId="";
		}
		DeviceBean deviceBean = DeviceUtil.getDeviceBean(BaseApplication.getInstance());
		url = url + "?clientid=" + clientid + "&appid=" + appid + "&agent="
				+ agent + "&from=" + from +"&gameid="+gameId+"&deviceid="+deviceBean.getDevice_id();
		wv.loadUrl(url, header);
		L.d("ChargeRequest", url + "====" + header.toString());
		// wv.postUrl(url, EncodingUtils.getBytes(postDate.toString(),
		// "UTF-8"));

	}
	public static WeakReference<OnPaymentListener> getPaymentListener() {
		return paymentListener;
	}

	public static void setPaymentListener(OnPaymentListener paymentListener) {
		WeakReference<OnPaymentListener> onPaymentListenerWeakReference=new WeakReference<OnPaymentListener>(paymentListener);
		ChargeActivityForWap.paymentListener = onPaymentListenerWeakReference;
	}
	/**
	 * 同步一下cookie
	 */
	public static void synCookies(Context context, String url) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();// 移除
		// cookieManager.setCookie(url, cookies);//指定要修改的cookies
		CookieSyncManager.getInstance().sync();
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == tv_back.getId()) {
			if (wv.canGoBack()) {
				wv.goBack();
				return;
			}else{
				finish();
			}

			handleResult();
		}
		if (v.getId() == iv_cancel.getId()) {
			handleResult();
		}

	}

	/**
	 * 一些版本特性操作，需要适配、
	 * 
	 * @date 6/3
	 * @param mWebView
	 * @reason 在微蓝项目的时候遇到了 返回键 之后 wv显示错误信息
	 * */
	private void webviewCompat(WebView mWebView) {
		if (BaseAppUtil.isOnline(mWebView.getContext())) {
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {
			mWebView.getSettings().setCacheMode(
					WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (wv.canGoBack()) {
			wv.goBack();
		} else {
			handleResult();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		L.e("hongliang","支付结果回来:data="+data);
		if (requestCode == REQUEST_CODE) {
			if (data != null) {
				amount = data.getDoubleExtra("amount", 0);
				boolean sus = data.getBooleanExtra("result", false);
				toast = data.getStringExtra("attach");
				isPaySus = sus;
			}
//			String param = "false";
//			if (isPaySus) {
//				param = "true";
//			}
//			wv.loadUrl("javascpript: updateWindow ('" + amount + "');");
			if(isPaySus){
				T.s(this,"支付成功");
			}else{
				T.s(this,"支付失败");
			}
			wv.reload();
			handleResult();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void handleResult() {
		if (isPaySus) {
			PaymentCallbackInfo payInfo = new PaymentCallbackInfo();
			payInfo.msg = toast == null ? "支付成功" : toast;
			payInfo.money = amount;
			if(paymentListener!=null&&paymentListener.get()!=null){
				paymentListener.get().paymentSuccess(payInfo);
				finish();
			}
		} else {
			PaymentErrorMsg errorMsg = new PaymentErrorMsg();
			errorMsg.code = -1;
			errorMsg.money = amount;
			errorMsg.msg = toast == null ? "支付失败" : toast;
			if(paymentListener!=null&&paymentListener.get()!=null){
				paymentListener.get().paymentError(errorMsg);
			}
		}
	}
	public static void start(Context context,String url,String titleName) {
	    Intent starter = new Intent(context, ChargeActivityForWap.class);
	    starter.putExtra("url",url);
	    starter.putExtra("titleName",titleName);
	    context.startActivity(starter);
	}

	/**
	 * 外部设置监听
	 * @param context
	 * @param url
	 * @param titleName
     */
	public static void start(Context context,String url,String titleName,String gameId) {
		Intent starter = new Intent(context, ChargeActivityForWap.class);
		starter.putExtra("url",url);
		starter.putExtra("titleName",titleName);
		if(!TextUtils.isEmpty(gameId)){//非null加入游戏id
			starter.putExtra("gameId",gameId);
		}
		context.startActivity(starter);
	}
}
