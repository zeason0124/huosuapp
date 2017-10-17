package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.huosuapp.text.R;
import com.huosuapp.text.adapter.InformationRcyAdapter;
import com.huosuapp.text.base.AutoLazyFragment;
import com.huosuapp.text.bean.InformationBean;
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
 * Created by LiuHongLiang on 2016/9/21.
 */
public class InformationFragment extends AutoLazyFragment implements AdvRefreshListener{
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    private BaseRefreshLayout<List<InformationBean.Information>> baseRefreshLayout;
    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_information);
        setupUI();
    }

    private void setupUI() {
        baseRefreshLayout=new UltraRefreshLayout(ptrRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        // 设置适配器
        baseRefreshLayout.setAdapter(new InformationRcyAdapter());
        baseRefreshLayout.setAdvRefreshListener(this);
        // 加载数据
        baseRefreshLayout.refresh();
    }

    @Override
    public void getPageData(final int requestPageNo) {
        HttpParams httpParams = AppApi.getHttpParams(false);
        httpParams.put("catalog",0);
        httpParams.put("page",requestPageNo);
        httpParams.put("offset",20);

        NetRequest.request(this).setParams(httpParams).get(AppApi.NEWS_LIST,new HttpJsonCallBackDialog<InformationBean>(){
            @Override
            public void onDataSuccess(InformationBean data) {
                if(data!=null&&data.getData()!=null){
                    baseRefreshLayout.resultLoadData(data.getData().getNews_list(),data.getData().getCount(),20);
                }else{
                    if(data.getCode()==AppApi.CODE_NO_DATA){
                        baseRefreshLayout.resultLoadData(new ArrayList<InformationBean.Information>(),requestPageNo-1);
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
