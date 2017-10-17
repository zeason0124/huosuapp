package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.bean.LoginBean;
import com.huosuapp.text.db.UserInfo;
import com.huosuapp.text.db.UserLoginInfodao;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.ui.dialog.RecordUserPopUtil;
import com.huosuapp.text.util.AppLoginControl;
import com.huosuapp.text.util.AuthCodeUtil;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.application.BaseActivity;
import com.liang530.log.T;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录界面 2016/7/12.
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.iv_login_return)
    ImageButton ivLoginReturn;
    @BindView(R.id.et_login_username)
    EditText etLoginUsername;
    @BindView(R.id.et_login_password)
    EditText etLoginPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_forgot_password)
    TextView tvForgotPassword;
    @BindView(R.id.tv_user_register)
    TextView tvUserRegister;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupUI();
    }

    private void setupUI() {
        if ("0".equals(BuildConfig.USE_MESSGAE)) {
            etLoginUsername.setHint("请输入用户名");
        }
        UserInfo userInfoLast = UserLoginInfodao.getInstance(this).getUserInfoLast();
        if(userInfoLast!=null){
            etLoginUsername.setText(userInfoLast.username);
            etLoginPassword.setText(userInfoLast.password);
        }
    }

    @OnClick({R.id.iv_login_return, R.id.btn_login, R.id.tv_forgot_password, R.id.tv_user_register,R.id.iv_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_login_return:
                if(BuildConfig.projectCode!=118) {//逍遥游定制，主界面登陆后才能进入
                    MainActivity.start(mContext,0);
                }
                finish();
                break;
            case R.id.tv_forgot_password:
                WebViewActivity.start(mContext, "忘记密码", AppApi.FORGET_PWD);
                break;
            case R.id.tv_user_register:
                RegisterActivity.start(mContext);
                break;
            case R.id.btn_login:
                submitLogin();
                break;
            case R.id.iv_more:
                RecordUserPopUtil.showRecordUserListPop(this,etLoginUsername,etLoginPassword);
                break;
        }
    }

    private void submitLogin() {
        delayClick(btnLogin);
        String account = etLoginUsername.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();
        Pattern pat = Pattern.compile("([a-zA-Z0-9]{6,12})");
        if (!pat.matcher(account).matches()) {
            T.s(mContext,"账号只能由6至12位英文或数字组成");
            return;
        }
        if (!pat.matcher(password).matches()) {
            T.s(mContext,"密码只能由6至12位英文或数字组成");
            return;
        }
        HttpParams httpParams = AppApi.getHttpParams(false);
        httpParams.put("username", AuthCodeUtil.authcodeEncode(account,AppApi.appkey));
        httpParams.put("password", AuthCodeUtil.authcodeEncode(password,AppApi.appkey));
        NetRequest.request(this).setParams(httpParams).
                showDialog(false).
                post(AppApi.USER_LOGIN, new HttpJsonCallBackDialog<LoginBean>() {
                    @Override
                    public void onDataSuccess(LoginBean data) {
                        AppLoginControl.createHsToken(data);
                        //存入共享区账号
                        UserLoginInfodao dao=UserLoginInfodao.getInstance(mContext);
                        dao.saveUserLoginInfo(etLoginUsername.getText().toString().trim(),etLoginPassword.getText().toString().trim());
                        T.s(getApplicationContext(), "欢迎" + etLoginUsername.getText().toString() + "回来!");
                        AppLoginControl.saveAccount(etLoginUsername.getText().toString());
                        MainActivity.start(mContext, 0);
                        finish();
                    }
                });
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onBackPressed() {
        if(BuildConfig.projectCode!=118) {//逍遥游定制，主界面登陆后才能进入
            MainActivity.start(mContext,0);
        }
        finish();
    }
}
