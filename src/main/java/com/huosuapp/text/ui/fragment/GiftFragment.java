package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.huosuapp.text.R;
import com.huosuapp.text.adapter.CommonVpAdapter;
import com.huosuapp.text.base.AutoLazyFragment;
import com.huosuapp.text.bean.HiddenActivateCodeUiEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by liu hong liang on 2016/9/22.
 */

public class GiftFragment extends AutoLazyFragment {

    @BindView(R.id.tab_gift)
    SlidingTabLayout tabGift;
    @BindView(R.id.vp_gift)
    ViewPager vpGift;
    CommonVpAdapter commonVpAdapter;
    private List<Fragment> fragmentList;
    private String[] titleNames={"游戏礼包","激活码"};
    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_vp_gift);
        EventBus.getDefault().register(this);
        setupUI();


    }
    private void setupUI() {
        fragmentList=new ArrayList<>();
        fragmentList.add(GiftListFragment.getInstance(GiftListFragment.TYPE_GIFT));
        fragmentList.add(GiftListFragment.getInstance(GiftListFragment.TYPE_ACTIVATE_CODE));
        commonVpAdapter = new CommonVpAdapter(getChildFragmentManager(),fragmentList,titleNames);
        vpGift.setAdapter(commonVpAdapter);
        tabGift.setViewPager(vpGift);
//        if(BuildConfig.projectCode==113){//不要激活码
//            EventBus.getDefault().postSticky(new HiddenActivateCodeUiEvent());
//        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeGift(HiddenActivateCodeUiEvent hiddenActivateCodeUiEvent){
        fragmentList.remove(1);
        commonVpAdapter.notifyDataSetChanged();
        tabGift.setVisibility(View.GONE);
    }
    public String getCatalog(){
        int currentItem = vpGift.getCurrentItem();
        if(currentItem==0){
            return "gift";
        }
        return "cdkey";
    }
    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        EventBus.getDefault().unregister(this);
    }
}
