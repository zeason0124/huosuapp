package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.adapter.GameImageAdapter;
import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.bean.GameDetailBean;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.pay.ChargeActivityForWap;
import com.huosuapp.text.util.AppLoginControl;
import com.huosuapp.text.view.DetailProgessLayoutView;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.application.BaseActivity;
import com.liang530.application.BaseApplication;
import com.liang530.log.T;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.utils.BaseAppUtil;
import com.liang530.utils.GlideDisplay;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 带充值，但是无返利的app游戏详情页
 */
public class GameDetailActivity extends BaseActivity {
    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_titleName)
    TextView tvTitleName;
    @BindView(R.id.iv_game_icon)
    ImageView ivGameIcon;
    @BindView(R.id.tv_game_name)
    TextView tvGameName;
    @BindView(R.id.tv_download_count)
    TextView tvDownloadCount;
    @BindView(R.id.tv_size)
    TextView tvSize;
    @BindView(R.id.ll_down_size)
    LinearLayout llDownSize;
    @BindView(R.id.tv_versions)
    TextView tvVersions;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.tv_fanli_rate)
    TextView tvFanliRate;
    @BindView(R.id.ll_fanli)
    LinearLayout llFanli;
    @BindView(R.id.rcy_game_imgs)
    RecyclerView rcyGameImgs;
    @BindView(R.id.expandable_text)
    TextView expandableText;
    @BindView(R.id.expand_collapse)
    ImageButton expandCollapse;
    @BindView(R.id.expand_text_view)
    ExpandableTextView expandTextView;
    @BindView(R.id.rl_game_introduce)
    LinearLayout rlGameIntroduce;
    @BindView(R.id.sv_content)
    ScrollView svContent;
    @BindView(R.id.detail_progressLayoutView)
    DetailProgessLayoutView detailProgressLayoutView;
    private GameBean gameBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail99);
        ButterKnife.bind(this);
        setupUI();
    }

    private void setupUI() {
        Intent intent = getIntent();
        tvTitleName.setText(getString(R.string.app_name));
        HttpParams httpParams = AppApi.getHttpParams(true);
        httpParams.put("gameid", intent.getStringExtra("gameId"));
        NetRequest.request(this).setParams(httpParams).showDialog(true).get(AppApi.URL_GAME_DETAIL, new HttpJsonCallBackDialog<GameDetailBean>() {
            @Override
            public void onDataSuccess(GameDetailBean data) {
                if (data.getData() != null) {
                    setupData(data.getData());
                }
            }
        });
    }

    private void setupData(GameBean data) {
        this.gameBean = data;
        tvTitleName.setText("" + gameBean.getGamename());
        tvGameName.setText("" + gameBean.getGamename());
        tvDownloadCount.setText("下载 : " + gameBean.getDowncnt() + " 次");
        tvSize.setText(gameBean.getSize() == "" ? "" : "大小 : " + gameBean.getSize());
        tvVersions.setText(gameBean.getVername() == "" ? "版本 : 1.0" : "版本 : " + gameBean.getVername() + "");
        tvLanguage.setText(gameBean.getLang() == "" ? "类型 : 中文" : "类型 : " + gameBean.getLang());
        expandTextView.setText(gameBean.getDisc());
        GlideDisplay.display(ivGameIcon, gameBean.getIcon());
        detailProgressLayoutView.setGameBean(gameBean, tvDownloadCount);
        //设置图片列表
        rcyGameImgs.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rcyGameImgs.setAdapter(new GameImageAdapter(gameBean.getImage()));
        tvFanliRate.setVisibility(View.GONE);
        if (gameBean.getIs_own() == 1){//1为不显示,2为显示
            llFanli.setVisibility(View.GONE);
        }else {
            llFanli.setVisibility(View.VISIBLE);
        }
        //无返利
//        if(AppLoginControl.isLogin()){
//            int rate=0;
//            try {
//                String rebate = gameBean.getRate();
//                rate=(int)(Float.parseFloat(rebate)*100);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            tvFanliRate.setVisibility(View.VISIBLE);
//            if(gameBean.getBenefit_type()!=null&&gameBean.getBenefit_type().equals("1")){//折扣
//                tvFanliRate.setText("折扣"+rate+"%");
//            }else if(gameBean.getBenefit_type()!=null&&gameBean.getBenefit_type().equals("2")) {//2为返利
//                tvFanliRate.setText("返利"+rate+"%");
//            }else{
//                tvFanliRate.setVisibility(View.GONE);
//            }
//        }
    }
    @OnClick({R.id.iv_return,R.id.ll_fanli})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.ll_fanli:
                if(AppLoginControl.isLogin()){
                    if(gameBean!=null){
                        ChargeActivityForWap.start(mContext,AppApi.CHARGE_URL,"充值",gameBean.getGameid());
                    }
                }else{
                    LoginActivity.start(mContext);
                }
                break;
        }
    }


    public static void start(Context context, String gameId) {
        if(!BaseAppUtil.isOnline(context)){
            T.s(BaseApplication.getInstance(),"网络不通，请稍后再试！");
            return;
        }
        Intent starter = new Intent(context, GameDetailHavePayActivity.class);
        starter.putExtra("gameId", gameId);
        context.startActivity(starter);
    }
}
