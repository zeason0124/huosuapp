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

import com.huosuapp.text.BuildConfig;
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
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.utils.GlideDisplay;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameDetailHavePayActivity extends BaseActivity {


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
    @BindView(R.id.tv_show_pay_text)
    TextView tvShowPayText;
    @BindView(R.id.tv_fanli_tag)
    TextView tvFanliTag;
    private GameBean gameBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail99);
        ButterKnife.bind(this);
        setupUI();
    }

    private void setupUI() {
        tvTitleName.setText(getString(R.string.app_name));
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpParams httpParams = AppApi.getHttpParams(true);
        Intent intent = getIntent();
        httpParams.put("gameid", intent.getStringExtra("gameId"));
        NetRequest.request(this).setParams(httpParams).get(AppApi.URL_GAME_DETAIL, new HttpJsonCallBackDialog<GameDetailBean>() {
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
        String downCnt = gameBean.getDowncnt();
        if(downCnt.length()>=5){
            downCnt = (Integer.parseInt(downCnt)/10000)+"万";
        }
        tvDownloadCount.setText("下载 : " + downCnt + "次");
        tvSize.setText(gameBean.getSize() == "" ? "" : "大小 : " + gameBean.getSize());
        tvVersions.setText(gameBean.getVername() == "" ? "版本 : 1.0" : "版本 : " + gameBean.getVername() + "");
        tvLanguage.setText(gameBean.getLang() == "" ? "类型 : 中文" : "类型 : " + gameBean.getLang());
        expandTextView.setText(gameBean.getDisc());
        GlideDisplay.display(ivGameIcon, gameBean.getIcon());
        detailProgressLayoutView.setGameBean(gameBean, tvDownloadCount);
        //设置图片列表
        rcyGameImgs.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rcyGameImgs.setAdapter(new GameImageAdapter(gameBean.getImage()));
        if (gameBean.getImage() == null || gameBean.getImage().size() == 0) {
            rcyGameImgs.setVisibility(View.GONE);
        } else {
            rcyGameImgs.setVisibility(View.VISIBLE);
        }
        tvFanliRate.setVisibility(View.GONE);
        if (AppLoginControl.isLogin()) {
            int rate = 0;
            try {
                String rebate = gameBean.getRate();
                rate = (int) (Float.parseFloat(rebate) * 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvFanliRate.setVisibility(View.VISIBLE);
            if (gameBean.getBenefit_type() != null && gameBean.getBenefit_type().equals("1")) {//折扣
                tvFanliRate.setText("折扣" + rate + "%");
            } else if (gameBean.getBenefit_type() != null && gameBean.getBenefit_type().equals("2")) {//2为返利
                if (BuildConfig.projectCode == 137) {//137
                    tvFanliRate.setText("赠送" + rate + "%");
                } else {
                    tvFanliRate.setText("返利" + rate + "%");
                }
            } else {
                tvFanliRate.setVisibility(View.GONE);
            }
            if(BuildConfig.projectCode == 147){//147- 返利折扣标签
                if ("2".equals(gameBean.getBenefit_type())) {//有返利
                    tvFanliTag.setVisibility(View.VISIBLE);
                    if(rate!=0) {
                        tvFanliTag.setText("  返利" + rate + "%");
                    }else{
                        tvFanliTag.setText("  返利");
                    }
                }
                if ("1".equals(gameBean.getBenefit_type())) {//有折扣
                    tvFanliTag.setVisibility(View.VISIBLE);
                    if(rate!=0) {
                        tvFanliTag.setText("  "+(rate/10f)+"折");
                    }else{
                        tvFanliTag.setText("  折扣");
                    }
                }
                tvFanliRate.setVisibility(View.GONE);//147- 不显示充值下方的折扣
            }
            if(BuildConfig.projectCode == 105 && "1".equals(gameBean.getDistype())){//105首充续充
                tvFanliRate.setVisibility(View.GONE);//充值下方文字
                int firstDiscount = (int)(gameBean.getFirst_discount()*100);
                int discount = (int)(Float.parseFloat(gameBean.getDiscount())*100);
                tvFanliTag.setVisibility(View.VISIBLE);
                tvFanliTag.setBackgroundResource(R.drawable.shape_circle_rect_yellow);
                tvFanliTag.setText("  首充"+(firstDiscount/10f)+"折,续充"+(discount/10f)+"折");
            }
        }else if(BuildConfig.projectCode == 147 || BuildConfig.projectCode == 105){//147，105- 未登录也显示
            int rate = 0;
            try {
                String rebate = gameBean.getRate();
                rate = (int) (Float.parseFloat(rebate) * 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if ("2".equals(gameBean.getBenefit_type())) {//有返利
                tvFanliTag.setVisibility(View.VISIBLE);
                if(rate!=0) {
                    tvFanliTag.setText("  返利" + rate + "%");
                }else{
                    tvFanliTag.setText("  返利");
                }
            }
            if ("1".equals(gameBean.getBenefit_type()) || (BuildConfig.projectCode == 105 && "1".equals(gameBean.getDistype()))) {//有折扣
                tvFanliTag.setVisibility(View.VISIBLE);
                if(rate!=0) {
                    if(BuildConfig.projectCode == 147) {
                        tvFanliTag.setText("  " + (rate / 10f) + "折");
                    }else if(BuildConfig.projectCode == 105){
                        int firstDiscount = (int)(gameBean.getFirst_discount()*100);
                        int discount = (int)(Float.parseFloat(gameBean.getDiscount())*100);
                        tvFanliTag.setVisibility(View.VISIBLE);
                        tvFanliTag.setBackgroundResource(R.drawable.shape_circle_rect_yellow);
                        tvFanliTag.setText("首充"+(firstDiscount/10f)+"折,续充"+(discount/10f)+"折");
                    }
                }else{
                    if(BuildConfig.projectCode == 105){//105使用discount为续充
                        int firstDiscount = (int)(gameBean.getFirst_discount()*100);
                        int discount = (int)(Float.parseFloat(gameBean.getDiscount())*100);
                        tvFanliTag.setVisibility(View.VISIBLE);
                        tvFanliTag.setBackgroundResource(R.drawable.shape_circle_rect_yellow);
                        tvFanliTag.setText("首充"+(firstDiscount/10f)+"折,续充"+(discount/10f)+"折");
                    }else {//105以外的，使用rate作折扣
                        tvFanliTag.setText("  折扣");
                    }
                }
            }
            tvFanliRate.setVisibility(View.GONE);
        }
        if (BuildConfig.projectCode == 118||BuildConfig.projectCode==150) {//150不要充值
            llFanli.setVisibility(View.GONE);
        } else {
            llFanli.setVisibility(View.VISIBLE);
        }
        // 137  只显示折扣或者返利，不需要显示充值
        if (BuildConfig.projectCode == 137) {
//            tvShowPayText.setVisibility(View.VISIBLE);//不显示充值
            if (tvFanliRate.getVisibility() == View.GONE) {//隐藏了，整个都需要隐藏
                llFanli.setVisibility(View.GONE);
            }
            //设置不可点击
//            llFanli.setEnabled(false);
//            llFanli.setClickable(false);
        }
    }

    @OnClick({R.id.iv_return, R.id.ll_fanli})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.ll_fanli:
                if (AppLoginControl.isLogin()) {
                    if (gameBean != null) {
                        ChargeActivityForWap.start(mContext, AppApi.CHARGE_URL, "充值", gameBean.getGameid());
                    }
                } else {
                    LoginActivity.start(mContext);
                }
                break;
        }
    }


    public static void start(Context context, String gameId) {
        Intent starter = new Intent(context, GameDetailHavePayActivity.class);
        starter.putExtra("gameId", gameId);
        context.startActivity(starter);
    }
}
