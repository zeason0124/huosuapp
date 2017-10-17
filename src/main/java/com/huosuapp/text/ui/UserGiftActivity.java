package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.huosuapp.text.R;
import com.huosuapp.text.adapter.CommonVpAdapter;
import com.huosuapp.text.bean.HiddenActivateCodeUiEvent;
import com.huosuapp.text.ui.fragment.UserGiftListFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserGiftActivity extends AppCompatActivity {
    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_titleName)
    TextView tvTitleName;
    @BindView(R.id.tab_gift)
    SlidingTabLayout tabGift;
    @BindView(R.id.vp_gift)
    ViewPager vpGift;
    CommonVpAdapter commonVpAdapter;
    private List<Fragment> fragmentList;
    private String[] titleNames={"已领取礼包","已领取激活码"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_gift);
        ButterKnife.bind(this);
        setupUI();
    }
    private void setupUI() {
        fragmentList=new ArrayList<>();
        fragmentList.add(UserGiftListFragment.getInstance(UserGiftListFragment.TYPE_GIFT));
        fragmentList.add(UserGiftListFragment.getInstance(UserGiftListFragment.TYPE_ACTIVATE_CODE));
        commonVpAdapter = new CommonVpAdapter(getSupportFragmentManager(),fragmentList,titleNames);
        vpGift.setAdapter(commonVpAdapter);
        tabGift.setViewPager(vpGift);
        //94号项目特殊定制，在首页没有获取到激活码的时候隐藏所有激活码
        HiddenActivateCodeUiEvent stickyEvent = EventBus.getDefault().getStickyEvent(HiddenActivateCodeUiEvent.class);
        if(stickyEvent!=null){
            onChangeGift(stickyEvent);
        }
    }
    public void onChangeGift(HiddenActivateCodeUiEvent hiddenActivateCodeUiEvent){
        fragmentList.remove(1);
        commonVpAdapter.notifyDataSetChanged();
        tabGift.notifyDataSetChanged();
        tabGift.setVisibility(View.GONE);
    }
    @OnClick(R.id.iv_return)
    public void onClick() {
        finish();
    }
    public static void start(Context context) {
        Intent starter = new Intent(context, UserGiftActivity.class);
        context.startActivity(starter);
    }
}
