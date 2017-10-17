package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.huosuapp.text.R;
import com.huosuapp.text.adapter.GiftRcyAdapter;
import com.huosuapp.text.base.AutoBaseFragment;
import com.huosuapp.text.bean.GiftCodeBean;
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
 * Created by liu hong liang on 2016/9/24.
 */

public class GiftListFragment extends AutoBaseFragment implements AdvRefreshListener{
    public final static String TYPE="type";
    public final static int TYPE_GIFT =0;
    public final static int TYPE_ACTIVATE_CODE=1;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    private int type;
    private BaseRefreshLayout<List<GiftCodeBean.Gift>> baseRefreshLayout;
    private GiftRcyAdapter giftRcyAdapter;

    public static Fragment getInstance(int type){
        GiftListFragment giftListFragment=new GiftListFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(TYPE,type);
        giftListFragment.setArguments(bundle);
        return giftListFragment;
    }
    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_list_gift);
        setupUI();
    }

    private void setupUI() {
        Bundle arguments = getArguments();
        if(arguments!=null){
            type = arguments.getInt(TYPE, TYPE_GIFT);
        }
        baseRefreshLayout=new UltraRefreshLayout(ptrRefresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        // 设置适配器
        giftRcyAdapter = new GiftRcyAdapter(type, getContext(), this);
        baseRefreshLayout.setAdapter(giftRcyAdapter);
        baseRefreshLayout.setAdvRefreshListener(this,this.getClass().getName()+type);
        // 加载数据
        baseRefreshLayout.refresh();
        ptrRefresh.autoRefresh(false);
    }

    @Override
    public void getPageData(final int requestPageNo) {
        String url;
        HttpParams httpParams;
        if(type== TYPE_GIFT){//礼包
            url=AppApi.GIFT_LIST;
            httpParams = AppApi.getHttpParams(false);
        }else{//激活码
            url=AppApi.CDKEY_LIST;
            httpParams = AppApi.getHttpParams(false);
        }
        httpParams.put("page",requestPageNo);
        httpParams.put("offset",20);
        NetRequest.request(this).setParams(httpParams).get(url,new HttpJsonCallBackDialog<GiftCodeBean>(){
            @Override
            public void onDataSuccess(GiftCodeBean data) {
                if(data!=null&&data.getData()!=null){
                    baseRefreshLayout.resultLoadData(data.getData().getGift_list(),data.getData().getCount(),20);
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
