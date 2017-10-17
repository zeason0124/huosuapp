package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.adapter.ClassifyRcyGridAdapter;
import com.huosuapp.text.adapter.ClassifyRcyGrid_MoguAdapter;
import com.huosuapp.text.base.AutoLazyFragment;
import com.huosuapp.text.bean.ClassifyListBean;
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

public class ClassifyFragment extends AutoLazyFragment implements AdvRefreshListener{
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    private BaseRefreshLayout<List<ClassifyListBean.ClassifyBean>> baseRefreshLayout;
    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_classify);
        setupUI();
    }

    private void setupUI() {
        baseRefreshLayout=new UltraRefreshLayout(ptrRefresh);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
        // 设置适配器
        if (BuildConfig.projectCode == 113){
            baseRefreshLayout.setAdapter(new ClassifyRcyGrid_MoguAdapter());
        }else {
            baseRefreshLayout.setAdapter(new ClassifyRcyGridAdapter());
        }
        baseRefreshLayout.setAdvRefreshListener(this);
        baseRefreshLayout.setCanLoadMore(false);
        // 加载数据
        baseRefreshLayout.refresh();
    }

    @Override
    public void getPageData(final int requestPageNo) {
        HttpParams httpParams = AppApi.getHttpParams(false);
        httpParams.put("page",requestPageNo);
        httpParams.put("offset",20);
        NetRequest.request(this).setParams(httpParams).get(AppApi.TYPE_LIST,new HttpJsonCallBackDialog<ClassifyListBean>(){
            @Override
            public void onDataSuccess(ClassifyListBean data) {
                if(data!=null&&data.getData()!=null){
                    baseRefreshLayout.resultLoadData(data.getData(),null);
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
