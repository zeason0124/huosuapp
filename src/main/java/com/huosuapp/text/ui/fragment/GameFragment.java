package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.adapter.CommonVpAdapter;
import com.huosuapp.text.base.AutoLazyFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by liu hong liang on 2016/9/26.
 */

public class GameFragment extends AutoLazyFragment {

    CommonVpAdapter commonVpAdapter;
    @BindView(R.id.tab_game)
    SlidingTabLayout tabGame;
    @BindView(R.id.vp_game)
    ViewPager vpGame;
    private List<Fragment> fragmentList;
    private String[] titleNames = {"网游", "单机"};

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_vp_game);
        setupUI();
    }
    private void setupUI() {
        fragmentList = new ArrayList<>();
        if(BuildConfig.projectCode==49){//定制标题
            titleNames[1]="单机应用";
        }
        if(BuildConfig.projectCode==118){//逍遥游定制标题
            titleNames[0]="BT游戏";
            titleNames[1]="热门游戏";
            fragmentList.add(GameListFragment.getInstance(GameListFragment.TYPE_OUT_LINE));
            fragmentList.add(GameListFragment.getInstance(GameListFragment.TYPE_LINE));
        }else{
            fragmentList.add(GameListFragment.getInstance(GameListFragment.TYPE_LINE));
            fragmentList.add(GameListFragment.getInstance(GameListFragment.TYPE_OUT_LINE));
        }

        //蘑菇互娱有定制
//        if(BuildConfig.projectCode==113){//不要单机
//            tabGame.setVisibility(View.GONE);
//            fragmentList.remove(1);
//        }
        commonVpAdapter = new CommonVpAdapter(getChildFragmentManager(), fragmentList, titleNames);
        vpGame.setAdapter(commonVpAdapter);
        tabGame.setViewPager(vpGame);
    }
}
