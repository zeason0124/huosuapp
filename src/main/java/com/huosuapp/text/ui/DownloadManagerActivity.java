package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.huosuapp.text.R;
import com.huosuapp.text.adapter.CommonVpAdapter;
import com.huosuapp.text.ui.fragment.DownloadListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.liang530.application.BaseActivity;

public class DownloadManagerActivity extends BaseActivity {
    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_titleName)
    TextView tvTitleName;
    @BindView(R.id.tab_down)
    SlidingTabLayout tabDown;
    @BindView(R.id.vp_down)
    ViewPager vpDown;
    private List<Fragment> fragmentList;
    private String[] titleNames = {"下载队列", "已安装游戏"};
    CommonVpAdapter commonVpAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        ButterKnife.bind(this);
        setupUI();
    }
    private void setupUI() {
        tvTitleName.setText("下载管理");
        fragmentList = new ArrayList<>();
        fragmentList.add(DownloadListFragment.getInstance(DownloadListFragment.TYPE_UNINSTALL));
        fragmentList.add(DownloadListFragment.getInstance(DownloadListFragment.TYPE_INSTALLED));
        commonVpAdapter = new CommonVpAdapter(getSupportFragmentManager(), fragmentList, titleNames);
        vpDown.setAdapter(commonVpAdapter);
        tabDown.setViewPager(vpDown);
    }
    @OnClick(R.id.iv_return)
    public void onClick() {
        finish();
    }
    public static void start(Context context) {
        Intent starter = new Intent(context, DownloadManagerActivity.class);
        context.startActivity(starter);
    }
}
