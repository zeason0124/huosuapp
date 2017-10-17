package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.pay.AndroidJSInterfaceForWeb;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.application.BaseActivity;
import com.liang530.log.L;
import com.liang530.log.T;
import com.liang530.utils.BaseAppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by admin on 2016/8/31.
 */
public class WebViewActivity extends BaseActivity {
    public final static String TITLE_NAME = "titleName";
    public final static String URL = "url";
    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_titleName)
    TextView tvTitleName;
    @BindView(R.id.webView)
    WebView webView;
    String titleName;
    String url;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    //    private CustomProgressDialog dialog;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        setupUI();
    }

    private void setupUI() {
        if (getIntent() != null) {
            titleName = getIntent().getStringExtra(TITLE_NAME);
            url = getIntent().getStringExtra(URL);
        }
        if (!TextUtils.isEmpty(titleName)) {
            tvTitleName.setText(titleName);
        }
        if (TextUtils.isEmpty(url)) {
            T.s(getApplicationContext(), "无效的请求地址");
        } else {
            getWebViewData();
        }
        ptrRefresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                webView.reload();
                ptrRefresh.refreshComplete();
            }
        });
    }

    @OnClick({R.id.iv_return})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                finish();
                break;
        }
    }

    //    private CustomProgressDialog dialog;
//
    private void getWebViewData() {
        // WebView加载web资源
        webSettings = webView.getSettings();
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // //支持通过JS打开新窗口

        webView.requestFocusFromTouch();

        webSettings.setSupportZoom(true); // 支持缩放
        AndroidJSInterfaceForWeb js = new AndroidJSInterfaceForWeb(this);
        webView.addJavascriptInterface(js, "huosdk");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                if (url.contains("wpa")) {//调用QQ
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(titleName)) {
                    tvTitleName.setText(titleName);
                }
            }
        });
        HttpParams httpParams = AppApi.getHttpParams(true);
        StringBuilder urlParams = httpParams.getUrlParams();
        webView.loadUrl(url + urlParams.toString(), httpParams.getHeaderMap());
        L.d(TAG, url + urlParams.toString() + "====" + httpParams.getHeaderMap().toString());
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();// goBack()表示返回WebView的上一页面
            return true;
        }
        finish();
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {//防止内存泄漏
            webView.setVisibility(View.GONE);
            webView.removeAllViews();
            ViewParent parent = webView.getParent();
            if(parent instanceof ViewGroup){
                ((ViewGroup) parent).removeView(webView);
            }
            webView.destroy();
            webView = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (webSettings != null) {
            webSettings.setJavaScriptEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webSettings != null) {
            webSettings.setJavaScriptEnabled(true);
        }
    }

    public static void start(Context context, String titleName, String url) {
        if(!BaseAppUtil.isOnline(context)){
            T.s(context,"网络不通，请稍后再试！");
            return;
        }
        Intent starter = new Intent(context, WebViewActivity.class);
        starter.putExtra(TITLE_NAME, titleName);
        starter.putExtra(URL, url);
        context.startActivity(starter);
    }
}
