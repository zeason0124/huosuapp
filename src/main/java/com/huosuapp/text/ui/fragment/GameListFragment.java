package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.huosuapp.text.R;
import com.huosuapp.text.adapter.GameRcyAadapter;
import com.huosuapp.text.base.AutoLazyFragment;
import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.bean.GameListBean;
import com.huosuapp.text.http.AppApi;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.views.refresh.mvc.AdvRefreshListener;
import com.liang530.views.refresh.mvc.BaseRefreshLayout;
import com.liang530.views.refresh.mvc.UltraRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by liu hong liang on 2016/9/26.
 */

public class GameListFragment extends AutoLazyFragment implements AdvRefreshListener{
    public final static String TYPE="type";
    public final static int TYPE_LINE=0;
    public final static int TYPE_OUT_LINE=1;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    private int type;
    private BaseRefreshLayout<List<GameBean>> baseRefreshLayout;
    public static Fragment getInstance(int type){
        GameListFragment gameListFragment=new GameListFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(TYPE,type);
        gameListFragment.setArguments(bundle);
        return gameListFragment;
    }
    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_list_game);
        setupUI();
    }

    private void setupUI() {
        Bundle arguments = getArguments();
        if(arguments!=null){
            type = arguments.getInt(TYPE, TYPE_LINE);
        }
        baseRefreshLayout=new UltraRefreshLayout(ptrRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        // 设置适配器
        baseRefreshLayout.setAdapter(new GameRcyAadapter());
        baseRefreshLayout.setAdvRefreshListener(this);
        // 加载数据
        baseRefreshLayout.refresh();
    }

    @Override
    public void getPageData(final int requestPageNo) {
        HttpParams httpParams = AppApi.getHttpParams(true);
        if(type==TYPE_LINE){//网游
            httpParams.put("category", 2 );
        }else{//单机
            httpParams.put("category", 1 );
        }
        httpParams.put("page",requestPageNo);
        httpParams.put("offset",20);
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
}
