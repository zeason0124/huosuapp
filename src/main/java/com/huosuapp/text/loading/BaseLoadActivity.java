package com.huosuapp.text.loading;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.huosuapp.text.R;
import com.huosuapp.text.update.VersionUpdateManager;
import com.huosuapp.text.util.AppLoginControl;

import com.liang530.application.BaseActivity;
import com.liang530.log.SP;
import com.liang530.utils.BaseAppUtil;
import com.liang530.utils.BaseTextUtil;

public class BaseLoadActivity extends BaseActivity implements VersionUpdateManager.VersionUpdateListener{
    private final static String VERSION_CODE="versionCode";
    private Handler handler=new Handler();
    private long startTime;

    protected ImageView loadIv;
    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
    }
    private void init(Config config){
        this.config=config;
        startTime=System.currentTimeMillis();
        if(config.startVersionUpdate){//开启版本更新
            VersionUpdateManager manager=new VersionUpdateManager();
            manager.checkVersionUpdate(this,this);//检查更新
        }else{
            next();
        }
    }
    private void gotoAct(final Intent intent){
        long currentTime=System.currentTimeMillis();
        long cha = currentTime - startTime;
        if(cha>=config.defaultDelayTime){//已经大于启动时间了
            startActivity(intent);
            finish();
        }else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            },config.defaultDelayTime-cha);
        }
    }
    private void next() {
        //判断是否是第一次启动
        int appVersionCode = BaseAppUtil.getAppVersionCode();
        int versionCode = SP.getInt(VERSION_CODE, 0);
        if(config.guideIntent==null){//没有设置引导页
            versionCode=appVersionCode;
        }
        if(versionCode==appVersionCode){//不是第一次启动
            if(config.forceLogin){//强制登陆
                if (BaseTextUtil.isEmpty(AppLoginControl.getHsToken())) {
                    gotoAct(config.loginIntent);
                } else {
                    gotoAct(config.mainIntent);
                }
            }else{
                gotoAct(config.mainIntent);
            }
        }else{//跳转到引导页
            SP.putInt(VERSION_CODE,appVersionCode).commit();
            gotoAct(config.guideIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 去下载去了，直接关闭
     */
    @Override
    public void gotoDown() {
        finish();
    }

    @Override
    public void cancel(String msg) {
       next();
    }
    public  class Config{
        private long defaultDelayTime=2000;//默认启动时间2000ms
        private boolean startVersionUpdate;
        private Intent guideIntent;
        private Intent mainIntent;
        private Intent loginIntent;
        private boolean forceLogin;

        /**
         * 设置是否启动版本更新
         * @param startVersionUpdate
         */
        public Config setVersionUpdate(boolean startVersionUpdate){
            this.startVersionUpdate=startVersionUpdate;
            return this;
        }

        /**
         * 设置启动延时时间
         * @param delayTime
         */
        public Config setDelayTime(long delayTime) {
            this.defaultDelayTime = delayTime;
            return this;
        }
        /**
         * 设置引导页的activity
         * @param guideIntent
         */
        public Config setGuideActivity(Intent guideIntent){
            this.guideIntent=guideIntent;
            return this;
        }
        /**
         * 设置主页的activity
         * @param mainIntent
         */
        public Config setMainActivity(Intent mainIntent){
            this.mainIntent=mainIntent;
            return this;
        }
        /**
         * 设置登陆的activity
         * @param loginIntent
         */
        public Config setLoginActivity(Intent loginIntent){
            this.loginIntent=loginIntent;
            return this;
        }

        /**
         * 应用配置
         */
        public void applyConfig() {
            init(this);
        }

        public Config setForceLogin(boolean forceLogin) {
            this.forceLogin = forceLogin;
            return this;
        }
    }
}