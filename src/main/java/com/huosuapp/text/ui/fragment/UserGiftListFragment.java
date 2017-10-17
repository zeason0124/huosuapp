package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.huosuapp.text.R;
import com.huosuapp.text.adapter.UserGiftRcyAdapter;
import com.huosuapp.text.base.AutoLazyFragment;
import com.huosuapp.text.bean.GiftCodeBean;
import com.huosuapp.text.bean.HiddenActivateCodeUiEvent;
import com.huosuapp.text.http.AppApi;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.views.refresh.mvc.AdvRefreshListener;
import com.liang530.views.refresh.mvc.BaseRefreshLayout;
import com.liang530.views.refresh.mvc.UltraRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by liu hong liang on 2016/9/24.
 */

public class UserGiftListFragment extends AutoLazyFragment implements AdvRefreshListener{
    public final static String TYPE="type";
    public final static int TYPE_GIFT =0;
    public final static int TYPE_ACTIVATE_CODE=1;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    private int type;
    private BaseRefreshLayout<List<GiftCodeBean.Gift>> baseRefreshLayout;
    private UserGiftRcyAdapter giftRcyAdapter;

    public static Fragment getInstance(int type){
        UserGiftListFragment giftListFragment=new UserGiftListFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(TYPE,type);
        giftListFragment.setArguments(bundle);
        return giftListFragment;
    }
    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
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
        giftRcyAdapter = new UserGiftRcyAdapter(type, getContext(), baseRefreshLayout);
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
            url=AppApi.USER_GIFT_LIST;
            httpParams = AppApi.getHttpParams(true);
        }else{//激活码
            url=AppApi.USER_CDKEY_LIST;
            httpParams = AppApi.getHttpParams(true);
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
                if(giftRcyAdapter.getData()==null||giftRcyAdapter.getData().size()==0){
                    EventBus.getDefault().postSticky(new HiddenActivateCodeUiEvent());
                }
            }
            @Override
            public void onJsonSuccess(int code, String msg,String data) {
                baseRefreshLayout.resultLoadData(new ArrayList(),null);
            }

            @Override
            public void onFailure(int errorNo, String strMsg, String completionInfo) {
                baseRefreshLayout.resultLoadData(null,10);
            }
        });
    }
}
