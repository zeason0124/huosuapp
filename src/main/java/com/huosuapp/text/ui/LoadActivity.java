package com.huosuapp.text.ui;

import android.content.Intent;
import android.os.Bundle;

import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.loading.BaseLoadActivity;

/**
 * Created by hongliang on 16-6-20.
 */
public class LoadActivity extends BaseLoadActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config config = new Config().setDelayTime(2000)
                .setLoginActivity(new Intent(mContext, LoginActivity.class));
//                .setGuideActivity(new Intent(mContext, GuidActivity.class))
        if(BuildConfig.projectCode==118){//逍遥游定制，登陆了才能进入主界面
            config.setMainActivity(new Intent(mContext, LoginActivity.class));
        }else {
            config.setMainActivity(new Intent(mContext, MainActivity.class));
        }
        config.setVersionUpdate(false)
                .setForceLogin(false)
                .applyConfig();

    }
}