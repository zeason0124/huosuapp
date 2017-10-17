package com.huosuapp.text.jsinterface;

import android.webkit.JavascriptInterface;

import com.liang530.application.BaseApplication;
import com.liang530.log.T;

/**
 * Created by LiuHongLiang on 2016/10/18.
 */

public class CommJsIml {
    @JavascriptInterface
    public void test(String str){
        T.s(BaseApplication.getInstance(),"结界："+str);
    }
}
