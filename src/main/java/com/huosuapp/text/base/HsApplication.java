package com.huosuapp.text.base;


import android.database.sqlite.SQLiteDatabase;

import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.bean.ClassifyListBean;
import com.huosuapp.text.bean.InstallApkRecord;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.ui.LoginActivity;
import com.liang530.application.BaseApplication;
import com.liang530.log.L;
import com.liang530.log.SP;
import com.liang530.rxvolley.NetRequest;
import com.liang530.system.SystemBarTintManager;
import com.liang530.utils.GlideDisplay;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 刘红亮 on 2015/9/21 13:57.
 */
public class HsApplication extends BaseApplication {
    private List<ClassifyListBean.ClassifyBean> classifyList;
    private Map<String,InstallApkRecord> installingApkList=new HashMap<>();
    @Override
    public void onCreate() {
        super.onCreate();
        //设置同时最大下载数量
        FileDownloader.init(getApplicationContext());
        FileDownloader.getImpl().setMaxNetworkThreadCount(8);
        L.init(BuildConfig.LOG_DEBUG);
        SP.init(this);
        initNet();
        initDeFaultBg();
        initStatusColor();
    }
    private void initStatusColor(){
        //设置状态栏变色
        SystemBarTintManager.SystemBarTintConfig config=new SystemBarTintManager.SystemBarTintConfig();
        config.setDarkmode(false).setStatusColor(getResources().getColor(R.color.black1));
        setStatusColorConfig(config);
    }
    @Override
    public Class getLoginClass() {
        return LoginActivity.class;
    }

    private void initNet() {
        NetRequest.setIsDebug(BuildConfig.LOG_DEBUG);
        AppApi.initAgentAndAppid(getApplicationContext());
    }

    private void initDeFaultBg(){
        GlideDisplay.BG_DEF = R.mipmap.ic_launcher;
    }
//    public static UCrop.Options getDefaultOption(){
//        UCrop.Options options=new UCrop.Options();
//        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
//        options.setHideBottomControls(false);
//        options.setToolbarColor(ContextCompat.getColor(BaseApplication.getInstance(), R.color.bg_color_def));
//        options.setStatusBarColor(ContextCompat.getColor(BaseApplication.getInstance(), R.color.bg_color_def));
//        options.setActiveWidgetColor(ContextCompat.getColor(BaseApplication.getInstance(), R.color.white));
//        options.setToolbarWidgetColor(ContextCompat.getColor(BaseApplication.getInstance(), R.color.white));
//        return options;
//    }

    @Override
    public File getDatabasePath(String name) {
        return super.getDatabasePath(name);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return super.openOrCreateDatabase(name, mode, factory);
    }

    public List<ClassifyListBean.ClassifyBean> getClassifyList() {
        return classifyList;
    }

    public void setClassifyList(List<ClassifyListBean.ClassifyBean> classifyList) {
        this.classifyList = classifyList;
    }

    public Map<String, InstallApkRecord> getInstallingApkList() {
        return installingApkList;
    }

}
