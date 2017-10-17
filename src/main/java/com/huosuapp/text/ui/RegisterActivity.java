package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.adapter.CommonVpAdapter;
import com.huosuapp.text.ui.fragment.PhoneRegisterFragment;
import com.huosuapp.text.ui.fragment.UserNameRegisterFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_titleName)
    TextView tvTitleName;
    @BindView(R.id.tab_register)
    SlidingTabLayout tabRegister;
    @BindView(R.id.vp_register)
    ViewPager vpRegister;
    private List<Fragment> fragmentList;
    private String[] titleNames = {"手机注册", "用户名注册"};
    private List<String> titleNameList=new ArrayList<>();
    CommonVpAdapter commonVpAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setupUI();
    }

    private void setupUI() {
        tvTitleName.setText("账号注册");
        fragmentList = new ArrayList<>();
        if("1".equals(BuildConfig.USE_MESSGAE)){
            fragmentList.add(new PhoneRegisterFragment());
            fragmentList.add(new UserNameRegisterFragment());
            titleNameList.add("手机注册");
            titleNameList.add("用户名注册");
        }else{
            fragmentList.add(new UserNameRegisterFragment());
            titleNameList.add("用户名注册");
        }
        commonVpAdapter = new CommonVpAdapter(getSupportFragmentManager(), fragmentList, titleNameList.toArray(new String[0]));
        vpRegister.setAdapter(commonVpAdapter);
        tabRegister.setViewPager(vpRegister);
    }

    @OnClick(R.id.iv_return)
    public void onClick() {
        finish();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterActivity.class);
        context.startActivity(starter);
    }
}
