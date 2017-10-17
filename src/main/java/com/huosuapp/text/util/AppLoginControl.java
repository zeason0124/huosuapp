package com.huosuapp.text.util;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.huosuapp.text.bean.LoginBean;
import com.huosuapp.text.http.AppApi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.liang530.control.LoginControl;
import com.liang530.log.L;

/**
 * Created by liu hong liang on 2016/9/20.
 */
public class AppLoginControl extends LoginControl {
    public static final String TIMESTAMP="timestamp";
    public static final String HS_TOKEN="hs-token";
    public static final String TOKEN_DATA="token-data";

    private static String timestamp;
    private static String hsToken;
    private static String tokenData;

    public static void createHsToken(LoginBean loginBean){
        if(loginBean!=null&&loginBean.getData()!=null){
            String usernameEcode = AuthCodeUtil.authcodeEncode(loginBean.getData().getIdentifier(), AppApi.appkey);
            String passwordEcode = AuthCodeUtil.authcodeEncode(loginBean.getData().getAccesstoken(), AppApi.appkey);
            JsonObject jsonObject = new JsonObject();
            if (usernameEcode != null) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(usernameEcode);
                usernameEcode = m.replaceAll("");
            }
            if (passwordEcode != null) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(passwordEcode);
                passwordEcode = m.replaceAll("");
            }
            jsonObject.addProperty("identify", usernameEcode);
            jsonObject.addProperty("accesstoken", passwordEcode);
            String jsonEncode = jsonObject.toString();
            hsToken = AuthCodeUtil.authcodeEncode(jsonEncode, AppApi.appkey);
            hsToken = hsToken.replaceAll("\n", "");
            L.d("createHsToken","hs-token="+hsToken);
            saveHsToken(hsToken);
        }
    }
    public static void saveTimestamp(String value){
        timestamp = value;
        login_sp.edit().putString(TIMESTAMP, value).commit();
    }
    public static String getTimestamp() {
        if(!TextUtils.isEmpty(timestamp)){
            return timestamp;
        }
        timestamp = login_sp.getString(TIMESTAMP, "");
        return timestamp;
    }
    public static void saveHsToken(String value){
        hsToken = value;
        login_sp.edit().putString(HS_TOKEN, value).commit();
    }
    public static String getHsToken() {
        if(!TextUtils.isEmpty(hsToken)){
            return hsToken;
        }
        hsToken = login_sp.getString(HS_TOKEN, "");
        return hsToken;
    }

    public static void saveTokenData(String value){
        tokenData = value;
        login_sp.edit().putString(TOKEN_DATA, value).commit();
    }
    public static String getTokenData() {
        if(!TextUtils.isEmpty(tokenData)){
            return tokenData;
        }
        tokenData = login_sp.getString(TOKEN_DATA, "");
        return tokenData;
    }
    //false 不为空
    public static boolean isLogin() {
        if (TextUtils.isEmpty(getHsToken())) {
            return false;
        } else {
            return true;
        }
    }
}
