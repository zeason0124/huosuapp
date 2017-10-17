package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.adapter.GameRcyAadapter;
import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.bean.GameListBean;
import com.huosuapp.text.http.AppApi;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.application.BaseActivity;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.views.refresh.mvc.AdvRefreshListener;
import com.liang530.views.refresh.mvc.BaseRefreshLayout;
import com.liang530.views.refresh.mvc.UltraRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

public class GameListActivity extends BaseActivity implements AdvRefreshListener {

    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_titleName)
    TextView tvTitleName;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    private BaseRefreshLayout<List<GameBean>> baseRefreshLayout;
    int category;
    int hot;
    int classify;
    String type;
    String titleName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        ButterKnife.bind(this);
        setupUI();
    }
    private void setupUI() {
        Intent intent = getIntent();
        category= intent.getIntExtra("category", -1);
        hot= intent.getIntExtra("hot", -1);
        classify= intent.getIntExtra("classify", -1);
        type= intent.getStringExtra("type");
        titleName= intent.getStringExtra("titleName");
        tvTitleName.setText(titleName);
        baseRefreshLayout=new UltraRefreshLayout(ptrRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        // 设置适配器
        baseRefreshLayout.setAdapter(new GameRcyAadapter());
        baseRefreshLayout.setAdvRefreshListener(this);
        // 加载数据
        baseRefreshLayout.refresh();
    }
    @OnClick({R.id.iv_return})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_return:
                finish();
                break;
        }
    }
    @Override
    public void getPageData(final int requestPageNo) {
        HttpParams httpParams = AppApi.getHttpParams(true);
        httpParams.put("page",requestPageNo);
        httpParams.put("offset",20);
        if(hot!=-1){
            httpParams.put("hot",hot);
        }
        if(category!=-1){
            httpParams.put("category",category);
        }
        if(classify!=-1){
            httpParams.put("classify",classify);
        }
        if(!TextUtils.isEmpty(type)){
            httpParams.put("type",type);
        }
        NetRequest.request(this).setParams(httpParams).get(AppApi.URL_GAME_LIST,new HttpJsonCallBackDialog<GameListBean>(){
            @Override
            public void onDataSuccess(GameListBean data) {
                if(data!=null&&data.getData()!=null){
                    baseRefreshLayout.resultLoadData(data.getData().getGame_list(),data.getData().getCount(),20);
                }else{
                    if(data.getCode()==AppApi.CODE_NO_DATA){
                        baseRefreshLayout.resultLoadData(new ArrayList(),requestPageNo-1);
                    }else{
                        baseRefreshLayout.resultLoadData(null,null);
                    }
                }
            }
            @Override
            public void onJsonSuccess(int code, String msg,String data) {
                baseRefreshLayout.resultLoadData(new ArrayList(),null);
            }
            @Override
            public void onFailure(int errorNo, String strMsg, String completionInfo) {
                baseRefreshLayout.resultLoadData(null,null);
            }
        });
    }
    public static void start(Context context,String titleName,String type,Integer category,Integer hot,Integer classify) {
        Intent starter = new Intent(context, GameListActivity.class);
        starter.putExtra("titleName",titleName);
        if(category!=null){
            starter.putExtra("category",category);
        }
        if(hot!=null){
            starter.putExtra("hot",hot);
        }
        if(!TextUtils.isEmpty(type)){
            starter.putExtra("type",type);
        }
        if(classify!=null){
            starter.putExtra("classify",classify);
        }
        context.startActivity(starter);
    }
}
