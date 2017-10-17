package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.huosuapp.text.R;
import com.huosuapp.text.accessibility.SmartInstallService;
import com.huosuapp.text.accessibility.SmartInstallUtil;
import com.huosuapp.text.update.VersionUpdateManager;
import com.huosuapp.text.util.AppLoginControl;
import com.liang530.application.BaseActivity;
import com.liang530.log.L;
import com.liang530.utils.BaseAppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity implements View.OnClickListener, VersionUpdateManager.VersionUpdateListener {

    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_titleName)
    TextView tvTitleName;
    @BindView(R.id.llt_gongneng)
    LinearLayout lltGongneng;
    @BindView(R.id.llt_check_version)
    LinearLayout lltCheckVersion;
    @BindView(R.id.llt_about_my)
    LinearLayout lltAboutMy;
    @BindView(R.id.llt_exit)
    LinearLayout lltExit;
    VersionUpdateManager manager = new VersionUpdateManager();
    @BindView(R.id.cb_smart_install)
    CheckBox cbSmartInstall;
    boolean accessibilitySettingsOn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setupUI();
    }

    private void setupUI() {
        tvTitleName.setText("设置中心");
    }

    @OnClick({R.id.iv_return, R.id.llt_gongneng, R.id.llt_check_version, R.id.llt_about_my, R.id.llt_exit,R.id.cb_smart_install})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.llt_gongneng:
                break;
            case R.id.llt_check_version:
                manager.checkVersionUpdate(this, this);//检查更新
                break;
            case R.id.llt_about_my:
                break;
            case R.id.llt_exit:
                showPopwindow();
                break;
            case R.id.llt_change_username:  //点击pop窗口的切换账号
                LoginActivity.start(this);
                finish();
                break;
            case R.id.llt_exit_login:  //点击pop窗口的退出登录
                AppLoginControl.saveHsToken("");
                LoginActivity.start(mContext);
                finish();
                break;
            case R.id.cb_smart_install:  //自动安装
                if(cbSmartInstall.isChecked()){//开启
                    SmartInstallUtil.setUseSmartInstall(true);
                    if (!SmartInstallUtil.isAccessibilitySettingsOn(mContext, SmartInstallService.class)) {
                        SmartInstallUtil.showSmartInstallDialog(mContext, new SmartInstallUtil.OnSmartInstallOpenListener() {
                            @Override
                            public void cancel() {
                                updateSmartInstallCb();
                            }
                        });
                    }
                }else{//关闭
                    SmartInstallUtil.setUseSmartInstall(false);
                }
                break;
        }
    }
    private void updateSmartInstallCb(){
        boolean useSmartInstall = SmartInstallUtil.getUseSmartInstall();
        accessibilitySettingsOn = SmartInstallUtil.isAccessibilitySettingsOn(mContext, SmartInstallService.class);
        if(useSmartInstall&&accessibilitySettingsOn){
            cbSmartInstall.setChecked(true);
        }else{
            cbSmartInstall.setChecked(false);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateSmartInstallCb();
    }

    private void showPopwindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View PopView = inflater.inflate(R.layout.pop_exit, null);
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        PopupWindow window = new PopupWindow(PopView,
                WindowManager.LayoutParams.MATCH_PARENT,
                BaseAppUtil.dip2px(SettingActivity.this, 130));
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        window.setAnimationStyle(R.style.dialog_bottom_up_style);
        window.showAtLocation(lltExit,
                Gravity.BOTTOM, 0, 0);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        PopView.findViewById(R.id.llt_change_username).setOnClickListener(this);
        PopView.findViewById(R.id.llt_exit_login).setOnClickListener(this);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SettingActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void gotoDown() {
    }

    @Override
    public void cancel(String msg) {
        L.d(TAG, "用户取消更新");
        if (msg != null) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
