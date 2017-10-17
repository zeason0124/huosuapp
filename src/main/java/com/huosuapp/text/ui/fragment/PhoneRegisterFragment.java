package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.base.AutoLazyFragment;
import com.huosuapp.text.bean.LoginBean;
import com.huosuapp.text.bean.ValidateCodeBean;
import com.huosuapp.text.db.UserLoginInfodao;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.ui.MainActivity;
import com.huosuapp.text.util.AppLoginControl;
import com.huosuapp.text.util.AuthCodeUtil;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.log.T;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.utils.BaseTextUtil;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liu hong liang on 2016/10/6.
 */

public class PhoneRegisterFragment extends AutoLazyFragment {
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_auth_code)
    EditText etAuthCode;
    @BindView(R.id.btn_getCode)
    Button btnGetCode;
    @BindView(R.id.et_phone_password)
    EditText etPhonePassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.tv_protocol)
    TextView tvProtocol;
    private String sessionId;
    Handler handler=new Handler();
    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_phone_register);
        setupUI();
    }

    private void setupUI() {
        tvProtocol.setText(String.format(getString(R.string.register_protocol),getString(R.string.app_name)));
    }
    @OnClick({R.id.btn_getCode,R.id.btn_register})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_getCode:
                getAuthCode();
                break;
            case R.id.btn_register:
                gotoRegister();
                break;

        }
    }

    private void gotoRegister() {
        delayClick(btnRegister);
        String phone = etPhone.getText().toString().trim();
        String password = etPhonePassword.getText().toString().trim();
        String authCode = etAuthCode.getText().toString().trim();
        if(TextUtils.isEmpty(sessionId)){
            T.s(mContext,"请先获取验证码！");
            return;
        }
        if(!BaseTextUtil.isMobileNumber(phone)){
            T.s(mContext,"手机号有误");
            return;
        }
        Pattern p = Pattern.compile("([a-zA-Z0-9]{6,12})");
        if (!p.matcher(password).matches()) {
            T.s(mContext,"密码只能由6至12位英文或数字组成");
            return;
        }
        if (TextUtils.isEmpty(authCode) && authCode.length() != 4) {
            T.s(mContext,"请输入4位的短信验证码");
            return;
        }
        HttpParams params= AppApi.getHttpParams(false);
        params.put("type",1);
        params.put("username", AuthCodeUtil.authcodeEncode(phone,AppApi.appkey));
        params.put("mobile", AuthCodeUtil.authcodeEncode(phone,AppApi.appkey));
        params.put("smscode", authCode);
        params.put("sessionid", sessionId);
        params.put("password", AuthCodeUtil.authcodeEncode(password,AppApi.appkey));
        NetRequest.request(this).setParams(params).
                showDialog(false).
                post(AppApi.USER_ADD, new HttpJsonCallBackDialog<LoginBean>() {
                    @Override
                    public void onDataSuccess(LoginBean data) {
                        if(data.getData()!=null){
                            AppLoginControl.createHsToken(data);
                            //存入共享区账号
                            UserLoginInfodao dao=UserLoginInfodao.getInstance(mContext);
                            dao.saveUserLoginInfo(etPhone.getText().toString().trim(),etPhonePassword.getText().toString().trim());
                            T.s(getApplicationContext(), "欢迎" + etPhone.getText().toString() + "回来!");
                            AppLoginControl.saveAccount(etPhone.getText().toString());
                            MainActivity.start(mContext, 0);
                            getActivity().finish();
                        }else if(!TextUtils.isEmpty(data.getMsg())){
                            T.s(getApplicationContext(), data.getMsg());
                        }
                    }
                });

    }

    private void getAuthCode(){
        String phone = etPhone.getText().toString().trim();
        if(!BaseTextUtil.isMobileNumber(phone)){
            T.s(mContext,"手机号有误");
            return;
        }
        HttpParams params= AppApi.getHttpParams(false);
        params.put("type",1);
        params.put("mobile", AuthCodeUtil.authcodeEncode(phone,AppApi.appkey));
        NetRequest.request(this).setParams(params).showDialog(true).post(AppApi.SMSCODE_SEND,new HttpJsonCallBackDialog<ValidateCodeBean>(){
            @Override
            public void onDataSuccess(ValidateCodeBean data) {
                if(data!=null&&data.getData()!=null){
                    sessionId=data.getData().getSessionid();
                    changeCodeBtn(60);
                }else if(data!=null&& !TextUtils.isEmpty(data.getMsg())){
                    T.s(mContext,data.getMsg());
                }
            }
        });
    }

    private void changeCodeBtn(int delayTime){
        btnGetCode.setTag(delayTime);
        if(delayTime<=0){
            btnGetCode.setEnabled(true);
            btnGetCode.setText("重新发送");
            return;
        }else{
            btnGetCode.setText("获取验证码(" + delayTime + ")");
            btnGetCode.setEnabled(false);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int delayTime = (int) btnGetCode.getTag();
                changeCodeBtn(--delayTime);
            }
        },1000);
    }

    /**
     * 延时任务移除
     */
    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        handler.removeCallbacksAndMessages(null);
    }
}
