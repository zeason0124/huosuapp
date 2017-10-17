package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.adapter.GameRcyAadapter;
import com.huosuapp.text.adapter.GiftRcyAdapter;
import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.bean.GameListBean;
import com.huosuapp.text.bean.GiftCodeBean;
import com.huosuapp.text.bean.OldSearchKeyListBean;
import com.huosuapp.text.bean.SearchKeyListBean;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.ui.fragment.GiftListFragment;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.application.BaseActivity;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.utils.BaseAppUtil;
import com.liang530.utils.BaseTextUtil;
import com.liang530.views.refresh.mvc.AdvRefreshListener;
import com.liang530.views.refresh.mvc.BaseRefreshLayout;
import com.liang530.views.refresh.mvc.UltraRefreshLayout;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

public class SearchActivity extends BaseActivity implements AdvRefreshListener {
    @BindView(R.id.btn_return)
    ImageButton btnReturn;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.rec_flowlayout)
    TagFlowLayout recFlowlayout;
    @BindView(R.id.hot_flowlayout)
    TagFlowLayout hotFlowlayout;
    @BindView(R.id.ll_searchUI)
    LinearLayout llSearchUI;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    @BindView(R.id.ll_searchResult)
    LinearLayout llSearchResult;
    @BindView(R.id.activity_search)
    LinearLayout activitySearch;
    private BaseRefreshLayout<List<GameBean>> baseRefreshLayout;
    private BaseRefreshLayout<List<GiftCodeBean.Gift>> giftRefreshLayout;
    private String searchKey="";
    private String catalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setupUI();
    }

    private void setupUI() {
        catalog=getIntent().getStringExtra("catalog");
        getSearchKey();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        // 设置适配器
        if("game".equals(catalog)){
            baseRefreshLayout=new UltraRefreshLayout(ptrRefresh);
            baseRefreshLayout.setAdapter(new GameRcyAadapter());
            baseRefreshLayout.setAdvRefreshListener(this);
        }else if("gift".equals(catalog)){
            giftRefreshLayout=new UltraRefreshLayout(ptrRefresh);
            giftRefreshLayout.setAdapter(new GiftRcyAdapter(GiftListFragment.TYPE_GIFT,this,this));
            giftRefreshLayout.setAdvRefreshListener(this);
        }else if("cdkey".equals(catalog)){
            giftRefreshLayout=new UltraRefreshLayout(ptrRefresh);
            giftRefreshLayout.setAdapter(new GiftRcyAdapter(GiftListFragment.TYPE_ACTIVATE_CODE,this,this));
            giftRefreshLayout.setAdvRefreshListener(this);
        }

        if (BuildConfig.projectCode==125){
            etSearch.setHint("");
        }
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    llSearchUI.setVisibility(View.VISIBLE);
                    llSearchResult.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    return;
                }
                llSearchUI.setVisibility(View.GONE);
                llSearchResult.setVisibility(View.VISIBLE);
                searchKey=s.toString();
                getPageData(1);
            }
        });

        //// 输入完后按键盘上的搜索键
        etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                return false;
            }
        });

    }
    private void getSearchKey() {
        HttpParams httpParams = AppApi.getHttpParams(false);
        httpParams.put("page", 1);
        httpParams.put("offset", 50);
        NetRequest.request(this).setParams(httpParams).get(AppApi.RECOMMEND_LIST, new HttpJsonCallBackDialog<SearchKeyListBean>() {
            @Override
            public void onDataSuccess(SearchKeyListBean data) {
                if (data != null && data.getData() != null && data.getData().getList() != null) {
                    updateRecKey(data.getData().getList());
                }
            }

            /**
             * 服务器有的数据改了格式，有的没改
             * @param code
             * @param msg
             * @param data
             */
            @Override
            public void onJsonSuccess(int code, String msg, String data) {
                try {
                    OldSearchKeyListBean oldSearchKeyListBean = new Gson().fromJson(data, OldSearchKeyListBean.class);
                    if(oldSearchKeyListBean!=null&&oldSearchKeyListBean.getData()!=null){
                        updateRecKey(oldSearchKeyListBean.getData());
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        NetRequest.request(this).setParams(httpParams).get(AppApi.HOTWORD_LIST, new HttpJsonCallBackDialog<SearchKeyListBean>() {
            @Override
            public void onDataSuccess(SearchKeyListBean data) {
                if (data != null && data.getData() != null && data.getData().getList() != null) {
                    updateHotKey(data.getData().getList());
                }
            }

            @Override
            public void onJsonSuccess(int code, String msg, String data) {
                try {
                    OldSearchKeyListBean oldSearchKeyListBean = new Gson().fromJson(data, OldSearchKeyListBean.class);
                    if(oldSearchKeyListBean!=null&&oldSearchKeyListBean.getData()!=null){
                        updateRecKey(oldSearchKeyListBean.getData());
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateRecKey(final List<String> recKeyList) {
        recFlowlayout.setAdapter(new TagAdapter<String>(recKeyList) {
            @Override
            public View getView(FlowLayout parent, int position, String key) {

                View view =  LayoutInflater.from(mContext).inflate(R.layout.adapter_search_rec,
                        recFlowlayout, false);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width= (BaseAppUtil.getDeviceWidth(mContext)- BaseAppUtil.dip2px(mContext,70))/3;
                TextView textView = (TextView) view.findViewById(R.id.tv_key_name);
                textView.setText(key);
                int drawable = mContext.getResources().getIdentifier("icon" + (position % 12 + 1), "drawable", getPackageName());
                textView.setBackgroundResource(drawable);
                return view;
            }
        });
        recFlowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                searchKey=recKeyList.get(position);
                BaseTextUtil.setTextAndCursorEnd(etSearch,searchKey);
                return true;
            }
        });
    }

    public void updateHotKey(final List<String> hotKeyList) {
        hotFlowlayout.setAdapter(new TagAdapter<String>(hotKeyList) {
            @Override
            public View getView(FlowLayout parent, int position, String key) {

                View view =  LayoutInflater.from(mContext).inflate(R.layout.adapter_search_rec,
                        recFlowlayout, false);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width= (BaseAppUtil.getDeviceWidth(mContext)- BaseAppUtil.dip2px(mContext,70))/3;
                TextView textView = (TextView) view.findViewById(R.id.tv_key_name);
                textView.setText(key);
                int drawable = mContext.getResources().getIdentifier("icon" + (position % 12 + 1), "drawable", getPackageName());
                textView.setBackgroundResource(drawable);
                return view;
            }
        });
        hotFlowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                searchKey=hotKeyList.get(position);
                BaseTextUtil.setTextAndCursorEnd(etSearch,searchKey);
                return true;
            }
        });
    }

    @OnClick({R.id.btn_return,R.id.et_search})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.et_search:
                break;
            case R.id.btn_return:
                finish();
                break;
        }
    }
    public static void start(Context context,String catalog) {
        Intent starter = new Intent(context, SearchActivity.class);
        starter.putExtra("catalog",catalog);
        context.startActivity(starter);
    }

    @Override
    public void getPageData(final int requestPageNo) {
        HttpParams httpParams = AppApi.getHttpParams(false);
        httpParams.put("catalog", catalog );
        httpParams.put("q",searchKey);
        httpParams.put("page",requestPageNo);
        httpParams.put("offset",20);
        NetRequest.request(this).setParams(httpParams).get(AppApi.URL_SEARCH_LIST,new HttpJsonCallBackDialog<GameListBean>(){
            @Override
            public void onDataSuccess(GameListBean data) {
                if(data!=null&&data.getData()!=null){
                    if("game".equals(catalog)){
                        baseRefreshLayout.resultLoadData(data.getData().getGame_list(),data.getData().getCount(),20);
                    }else{
                        if("gift".equals(catalog)){
                            giftRefreshLayout.resultLoadData(data.getData().getGift_list(),data.getData().getCount(),20);
                        }else if("cdkey".equals(catalog)){
                            giftRefreshLayout.resultLoadData(data.getData().getCdkey_list(),data.getData().getCount(),20);
                        }
                    }
                }else{
                    if(data.getCode()==AppApi.CODE_NO_DATA){
                        if("game".equals(catalog)){
                            baseRefreshLayout.resultLoadData(new ArrayList(),requestPageNo-1);
                        }else{
                            giftRefreshLayout.resultLoadData(new ArrayList(),requestPageNo-1);
                        }
                    }else{
                        if("game".equals(catalog)){
                            baseRefreshLayout.resultLoadData(null,null);
                        }else{
                            giftRefreshLayout.resultLoadData(null,null);
                        }
                    }
                }
            }
            @Override
            public void onJsonSuccess(int code, String msg,String data) {
                if("game".equals(catalog)){
                    baseRefreshLayout.resultLoadData(new ArrayList(),null);
                }else{
                    giftRefreshLayout.resultLoadData(new ArrayList(),null);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg, String completionInfo) {
                if("game".equals(catalog)){
                    baseRefreshLayout.resultLoadData(null,null);
                }else{
                    giftRefreshLayout.resultLoadData(null,null);
                }
            }
        });
    }
}
