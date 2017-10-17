package com.huosuapp.text.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by LiuHongLiang on 2016/9/25.
 */

public class CommonVpAdapter extends FragmentPagerAdapter {
    private  List<Fragment> fragmentList;
    private  String[] titleNames;

    public CommonVpAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] titleNames) {
        super(fm);
        this.fragmentList=fragmentList;
        this.titleNames=titleNames;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleNames[position];
    }
}
