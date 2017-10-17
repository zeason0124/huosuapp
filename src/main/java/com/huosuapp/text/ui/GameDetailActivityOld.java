package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
@Deprecated
public class GameDetailActivityOld extends BaseActivity {

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
    @BindView(R.id.tv_versions)
    TextView tvVersions;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.rcy_game_imgs)
    RecyclerView rcyGameImgs;
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
        setContentView(R.layout.activity_game_detail);
        ButterKnife.bind(this);
        setupUI();
    }

    private void setupUI() {
        Intent intent = getIntent();
        tvTitleName.setText(getString(R.string.app_name));
        HttpParams httpParams = AppApi.getHttpParams(false);
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
        detailProgressLayoutView.setGameBean(gameBean,tvDownloadCount);
        //设置图片列表
        rcyGameImgs.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));

        rcyGameImgs.setAdapter(new GameImageAdapter(gameBean.getImage()));
    }

    @OnClick({R.id.iv_return})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                finish();
                break;
        }
    }
    public static void start(Context context, String gameId) {
        if(BuildConfig.projectCode==99||BuildConfig.projectCode==91||BuildConfig.projectCode==49) {//99和91带充值的游戏详情页
            Intent starter = new Intent(context, GameDetailHavePayActivity.class);
            starter.putExtra("gameId", gameId);
            context.startActivity(starter);
        }else{
            Intent starter = new Intent(context, GameDetailActivityOld.class);
            starter.putExtra("gameId", gameId);
            context.startActivity(starter);
        }
    }
}
