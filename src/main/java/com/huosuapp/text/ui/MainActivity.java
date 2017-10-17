package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.base.HsApplication;
import com.huosuapp.text.bean.ClassifyListBean;
import com.huosuapp.text.bean.DeviceBean;
import com.huosuapp.text.bean.DownLoadUrlBean;
import com.huosuapp.text.db.TasksManager;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.ui.fragment.ClassifyFragment;
import com.huosuapp.text.ui.fragment.GameFragment;
import com.huosuapp.text.ui.fragment.GiftFragment;
import com.huosuapp.text.ui.fragment.InformationFragment;
import com.huosuapp.text.ui.fragment.RecommandFragment;
import com.huosuapp.text.update.VersionUpdateManager;
import com.huosuapp.text.util.AppLoginControl;
import com.huosuapp.text.util.DeviceUtil;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.application.BaseActivity;
import com.liang530.application.BaseApplication;
import com.liang530.log.L;
import com.liang530.log.SP;
import com.liang530.manager.AppManager;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.utils.BaseAppUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class MainActivity extends BaseActivity implements VersionUpdateManager.VersionUpdateListener {

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.goto_ucenter)
    TextView gotoUcenter;
    @BindView(R.id.fl_left)
    FrameLayout flLeft;
    @BindView(R.id.iv_downManager)
    ImageView ivDownManager;
    @BindView(R.id.et_search)
    TextView etSearch;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.ll_search)
    RelativeLayout llSearch;
    @BindView(R.id.tab_home)
    SlidingTabLayout tabHome;
    @BindView(R.id.home_pager)
    ViewPager homePager;
    private String titleName[] = new String[]{"推荐", "游戏", "分类", "礼包", "资讯"};
    private ArrayList<Fragment> fragments;
    VersionUpdateManager manager = new VersionUpdateManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        TasksManager.getImpl().init();
        manager.checkVersionUpdate(this, this);//检查更新
        uploadNewInstall();
        setupUI();
        getGameTypeData();
//        NetRequest.request(this).post("http://v7.1tsdk.com/sdk/init.php",new HttpJsonCallBackDialog(){
//            @Override
//            public void onSuccess(String t) {
//                super.onSuccess(t);
//                Log.e("hongliang","数据:"+t);
//            }
//        });
        if (BuildConfig.projectCode==125){
            etSearch.setHint("");
        }
    }

    /**
     * 报告新安装app
     */
    private void uploadNewInstall() {
        int open_cnt = SP.getInt("open_cnt", 1);
        if(open_cnt!=1){//不是第一次不统计
            return;
        }
        SP.putInt("open_cnt", open_cnt+1).apply();
        HttpParams httpParams = AppApi.getHttpParams(false);
        DeviceBean deviceBean = DeviceUtil.getDeviceBean(BaseApplication.getInstance());
        httpParams.put("verid", BaseAppUtil.getAppVersionCode());
        httpParams.put("gameid", AppApi.appid);
        httpParams.put("openudid", "");
        httpParams.put("devicetype", "");
        httpParams.put("idfa", "");
        httpParams.put("idfv", "");
        httpParams.put("mac", "");
        httpParams.put("resolution", "1024*768");
        httpParams.put("network", "WIFI");
        httpParams.put("userua", deviceBean.getUserua());
        NetRequest.request().setParams(httpParams).get(AppApi.GAME_DOWN,new HttpJsonCallBackDialog<DownLoadUrlBean>(){
            @Override
            public void onDataSuccess(DownLoadUrlBean data) {
            }
            @Override
            public void onSuccess(String t) {
            }
            @Override
            public void onFailure(int errorNo, String strMsg, String completionInfo) {
            }
        });
    }
    private void getGameTypeData() {
        HttpParams httpParams = AppApi.getHttpParams(false);
        httpParams.put("page", 1);
        httpParams.put("offset", 40);
        NetRequest.request(this).setParams(httpParams).get(AppApi.TYPE_LIST, new HttpJsonCallBackDialog<ClassifyListBean>() {
            @Override
            public void onDataSuccess(ClassifyListBean data) {
                if (data != null && data.getData() != null) {
                    HsApplication instance = (HsApplication) HsApplication.getInstance();
                    instance.setClassifyList(data.getData());
                }
            }

            @Override
            public void onJsonSuccess(int code, String msg, String data) {

            }

            @Override
            public void onFailure(int errorNo, String strMsg, String completionInfo) {

            }
        });
    }

    private void setupUI() {
        //保证主界面是最底层界面
        AppManager.getAppManager().finishOtherActivity(this);
        fragments = new ArrayList<Fragment>();
        if(BuildConfig.projectCode==137){//137 龙川奇点定制需求，推荐，分类，礼包
            titleName= new String[]{"推荐","分类","礼包"};
            fragments.add(new RecommandFragment());
//            fragments.add(new GameFragment());
            fragments.add(new ClassifyFragment());
            fragments.add(new GiftFragment());
//            fragments.add(new InformationFragment());
        }else{
            fragments.add(new RecommandFragment());
            fragments.add(new GameFragment());
            fragments.add(new ClassifyFragment());
            fragments.add(new GiftFragment());
            fragments.add(new InformationFragment());
        }
        tabHome.setViewPager(homePager, titleName, this, fragments);
        Intent intent = getIntent();
        if (intent != null) {
            int position = intent.getIntExtra("position", 0);
            tabHome.setCurrentTab(position);
            homePager.setCurrentItem(position, false);
        }
        homePager.setOffscreenPageLimit(4);
        ivLogo.setVisibility(View.VISIBLE);
        gotoUcenter.setVisibility(View.GONE);
        //蘑菇互娱定制
        if (BuildConfig.projectCode == 113) {
            ivLogo.setVisibility(View.GONE);
            gotoUcenter.setVisibility(View.GONE);
        }
        if(BuildConfig.projectCode==137){//137龙川奇点定制
            ivLogo.setImageResource(R.mipmap.ic_user_login);
        }
    }

    @OnClick({R.id.fl_left, R.id.iv_downManager, R.id.et_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_left:
                if (AppLoginControl.isLogin()) {
                    UserCenterActivity.start(mContext);
                } else {
                    LoginActivity.start(mContext);
                }
                break;
            case R.id.iv_downManager:
                DownloadManagerActivity.start(mContext);
                break;
            case R.id.et_search:
                SearchActivity.start(mContext,getCatalog());
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            int position = intent.getIntExtra("position", 0);
            tabHome.setCurrentTab(position);
            homePager.setCurrentItem(position, false);
        }
    }

    public static void start(Context context, int position) {
        Intent starter = new Intent(context, MainActivity.class);
        starter.putExtra("position", position);
        context.startActivity(starter);
    }

    @Override
    public void gotoDown() {
    }

    @Override
    public void cancel(String msg) {
        L.d(TAG, "用户取消更新");
    }

    /**
     * 获取搜索的类别 礼包界面搜索礼包和激活码
     * @return
     */
    public String getCatalog(){
        if(BuildConfig.projectCode==91){//91定制搜索
            int currentItem = homePager.getCurrentItem();
            if(currentItem==3){//礼包页面
                GiftFragment fragment = (GiftFragment) fragments.get(currentItem);
                return fragment.getCatalog();
            }
        }
        return "game";
    }
}
