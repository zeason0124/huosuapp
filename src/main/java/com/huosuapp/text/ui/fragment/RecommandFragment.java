package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.adapter.RecommandRcyAdapter;
import com.huosuapp.text.base.AutoLazyFragment;
import com.huosuapp.text.bean.BannerListBean;
import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.bean.GameListBean;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.util.RecyclerViewNoAnimator;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.views.refresh.mvc.AdvRefreshListener;
import com.liang530.views.refresh.mvc.BaseRefreshLayout;
import com.liang530.views.refresh.mvc.UltraRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by liu hong liang on 2016/9/24.
 */

public class RecommandFragment extends AutoLazyFragment implements AdvRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    @BindView(R.id.iv_gotoTop)
    ImageView ivGotoTop;
    private BaseRefreshLayout<List<GameBean>> baseRefreshLayout;
    RecommandRcyAdapter recommandRcyAdapter;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_recommand);
        setupUI();
    }

    private void setupUI() {
        ptrRefresh.disableWhenHorizontalMove(true);
        baseRefreshLayout = new UltraRefreshLayout(ptrRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new RecyclerViewNoAnimator());
        recommandRcyAdapter = new RecommandRcyAdapter(this,ptrRefresh);
        // 设置适配器
        baseRefreshLayout.setAdapter(recommandRcyAdapter);
        baseRefreshLayout.setAdvRefreshListener(this);
        // 加载数据
        baseRefreshLayout.refresh();
        //回到顶部按钮设置
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //如果滑动到了最后5个的时候，显示回到顶部
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int itemCount = recyclerView.getAdapter().getItemCount();
                if(lastVisibleItemPosition>3&&lastVisibleItemPosition+5>=itemCount){
                    ivGotoTop.setVisibility(View.VISIBLE);
                }else{
                    ivGotoTop.setVisibility(View.GONE);
                }
            }
        });
    }
    @OnClick({R.id.iv_gotoTop})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_gotoTop:
                if(recyclerView.getAdapter().getItemCount()>0) {
                    recyclerView.scrollToPosition(0);
                }
                break;
        }
    }
    @Override
    protected void onFragmentStartLazy() {
        Integer bannerAdapterPosition = recommandRcyAdapter.getBannerAdapterPosition();
        if(bannerAdapterPosition!=null){
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(bannerAdapterPosition);
            if(viewHolder instanceof RecommandRcyAdapter.BannerViewHolder){
                RecommandRcyAdapter.BannerViewHolder bannerViewHolder = (RecommandRcyAdapter.BannerViewHolder)viewHolder;
                bannerViewHolder.convenientBanner.startTurning(3000);
            }
        }
        //刷新下数据，解决eventbus有时候没有接收到的情况
        recommandRcyAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onFragmentStopLazy() {
        Integer bannerAdapterPosition = recommandRcyAdapter.getBannerAdapterPosition();
        if(bannerAdapterPosition!=null){
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(bannerAdapterPosition);
            if(viewHolder instanceof RecommandRcyAdapter.BannerViewHolder){
                RecommandRcyAdapter.BannerViewHolder bannerViewHolder = (RecommandRcyAdapter.BannerViewHolder)viewHolder;
                bannerViewHolder.convenientBanner.stopTurning();
            }
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    @Override
    public void getPageData(int requestPageNo) {
        if(BuildConfig.projectCode==137){//137 龙川奇点定制需求，只要游戏列表
            getGameListData(requestPageNo);
            return;
        }
        if(requestPageNo==1){
            getHeadGameData(RecommandRcyAdapter.TYPE_CUR_HOT);
            getBannerData();
            getHeadGameData(RecommandRcyAdapter.TYPE_OUT_LINE);
            getHeadGameData(RecommandRcyAdapter.TYPE_LINE);
            getGameListData(1);
        }else{
            getGameListData(requestPageNo);
        }
    }
    private void getGameListData(final int requestPageNo) {
        HttpParams httpParams=AppApi.getHttpParams(true);
        httpParams.put("page",requestPageNo);
        httpParams.put("offset",20);

        NetRequest.request(this).setParams(httpParams).get(AppApi.URL_GAME_LIST,new HttpJsonCallBackDialog<GameListBean>(){
            @Override
            public void onDataSuccess(GameListBean data) {
                if(data!=null&&data.getData()!=null){
                    baseRefreshLayout.resultLoadData(data.getData().getGame_list(),data.getData().getCount(),20);
                }else{
                    baseRefreshLayout.resultLoadData(null,null);
                }
            }
            @Override
            public void onJsonSuccess(int code, String msg,String data) {
                baseRefreshLayout.resultLoadData(new ArrayList<GameBean>(),0);
            }
            @Override
            public void onFailure(int errorNo, String strMsg, String completionInfo) {
                baseRefreshLayout.resultLoadData(null,null);
            }
        });
    }


    public void getHeadGameData(final int type) {
        HttpParams httpParams = AppApi.getHttpParams(true);
        httpParams.put("hot",1);
        if(type==RecommandRcyAdapter.TYPE_LINE){
            httpParams.put("category",2);
        }else if(type==RecommandRcyAdapter.TYPE_OUT_LINE){
            httpParams.put("category",1);
        }
        httpParams.put("cnt",3);
        httpParams.put("rand",1);
        httpParams.put("page",1);
        httpParams.put("offset",3);
        NetRequest.request(this).setParams(httpParams).setShowErrorToast(false).get(AppApi.URL_GAME_LIST,new HttpJsonCallBackDialog<GameListBean>(){
            @Override
            public void onDataSuccess(GameListBean data) {
                if(data!=null&&data.getData()!=null){
                    recommandRcyAdapter.notifyDataChanged(data.getData().getGame_list(),type);
                }else{
                    if(data.getCode()==AppApi.CODE_NO_DATA){
                        recommandRcyAdapter.notifyDataChanged(null,type);
                    }else{
                        recommandRcyAdapter.notifyDataChanged(null,type);
                    }
                }
            }
        });
    }
    private void getBannerData() {
        NetRequest.request(this).setParams(AppApi.getHttpParams(false)).setShowErrorToast(false).get(AppApi.SLIDE_LIST,new HttpJsonCallBackDialog<BannerListBean>(){
            @Override
            public void onDataSuccess(BannerListBean data) {
                if(data!=null&&data.getData()!=null){
                    recommandRcyAdapter.notifyDataChanged(data.getData().getList(),RecommandRcyAdapter.TYPE_BANNER);
                }else{
                    if(data.getCode()==AppApi.CODE_NO_DATA){
                        recommandRcyAdapter.notifyDataChanged(null,RecommandRcyAdapter.TYPE_BANNER);
                    }else{
                        recommandRcyAdapter.notifyDataChanged(null,RecommandRcyAdapter.TYPE_BANNER);
                    }
                }
            }
        });
    }
}
