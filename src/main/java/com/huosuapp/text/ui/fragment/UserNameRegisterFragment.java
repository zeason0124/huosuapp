package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.base.AutoLazyFragment;
import com.huosuapp.text.bean.LoginBean;
import com.huosuapp.text.db.UserLoginInfodao;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.ui.MainActivity;
import com.huosuapp.text.util.AppLoginControl;
import com.huosuapp.text.util.AuthCodeUtil;
import com.huosuapp.text.util.CodeCreateUtil;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.log.T;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liu hong liang on 2016/10/6.
 */

public class UserNameRegisterFragment extends AutoLazyFragment {
    @BindView(R.id.et_reg_usname)
    EditText etRegUsname;
    @BindView(R.id.et_reg_uspwd)
    EditText etRegUspwd;
    @BindView(R.id.et_repeat_uspsd)
    EditText etRepeatUspsd;
    @BindView(R.id.et_yanzhengma)
    EditText etYanzhengma;
    @BindView(R.id.iv_showCode)
    ImageView ivShowCode;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.tv_register_protocol)
    TextView tvRegisterProtocol;
    private String realCode;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_uasename_register);
        setupUI();
    }

    private void setupUI() {

        tvRegisterProtocol.setText(String.format(getString(R.string.register_protocol), getString(R.string.app_name)));
        //将验证码用图片的形式显示出来
        ivShowCode.setImageBitmap(CodeCreateUtil.getInstance().createBitmap());
        realCode = CodeCreateUtil.getInstance().getCode().toLowerCase();
    }

    @OnClick({R.id.iv_showCode, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_showCode:
                ivShowCode.setImageBitmap(CodeCreateUtil.getInstance().createBitmap());
                realCode = CodeCreateUtil.getInstance().getCode().toLowerCase();
                break;
            case R.id.btn_register:
                gotoRegister();
                break;

        }
    }

    private void gotoRegister() {
        delayClick(btnRegister);
        String phone = etRegUsname.getText().toString().trim();
        String password = etRegUspwd.getText().toString().trim();
        String authCode = etYanzhengma.getText().toString().trim();
        String repeatUspsd = etRepeatUspsd.getText().toString().trim();
        Pattern p = Pattern.compile("([a-zA-Z0-9]{6,12})");
        if (!p.matcher(phone).matches()) {
            T.s(mContext, "账号只能由6至12位英文或数字组成");
            return;
        }
        if (TextUtils.isEmpty(realCode)) {
            T.s(mContext, "请先获取验证码！");
            return;
        }
        if(!realCode.equalsIgnoreCase(authCode)){
            T.s(mContext,"请输入正确的验证码");
            return;
        }

        if (!p.matcher(password).matches()) {
            T.s(mContext, "密码只能由6至12位英文或数字组成");
            return;
        }
        if(!password.equals(repeatUspsd)){
            T.s(mContext, "两次输入密码必须一致");
            return;
        }
        HttpParams params = AppApi.getHttpParams(false);
        params.put("type", 2);
        params.put("username", AuthCodeUtil.authcodeEncode(phone, AppApi.appkey));
        params.put("password", AuthCodeUtil.authcodeEncode(password, AppApi.appkey));
        NetRequest.request(this).setParams(params).
                showDialog(false).
                post(AppApi.USER_ADD, new HttpJsonCallBackDialog<LoginBean>() {
                    @Override
                    public void onDataSuccess(LoginBean data) {
                        if(data.getData()!=null){
                            AppLoginControl.createHsToken(data);
                            //存入共享区账号
                            UserLoginInfodao dao = UserLoginInfodao.getInstance(mContext);
                            dao.saveUserLoginInfo(etRegUsname.getText().toString().trim(), etRegUspwd.getText().toString().trim());
                            T.s(getApplicationContext(), "欢迎" + etRegUsname.getText().toString() + "回来!");
                            AppLoginControl.saveAccount(etRegUsname.getText().toString());
                            MainActivity.start(mContext, 0);
                            getActivity().finish();
                        }else if(!TextUtils.isEmpty(data.getMsg())){
                            T.s(getApplicationContext(), data.getMsg());
                        }
                    }
                });

    }
}
