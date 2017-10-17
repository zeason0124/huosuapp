package com.huosuapp.text.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.adapter.UserCenterOptionAdapter;
import com.huosuapp.text.bean.UserWalletBean;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.pay.ChargeActivityForWap;
import com.huosuapp.text.pay.OnPaymentListener;
import com.huosuapp.text.pay.PaymentCallbackInfo;
import com.huosuapp.text.pay.PaymentErrorMsg;
import com.huosuapp.text.util.AppLoginControl;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.application.BaseActivity;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.utils.BaseTextUtil;
import com.liang530.views.imageview.CircleImageView;
import com.liang530.views.recyclerview.swipe.RecycleViewDivider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人中心页
 */
public class UserCenterActivity extends BaseActivity {
    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_titleName)
    TextView tvTitleName;
    @BindView(R.id.user_headImg)
    CircleImageView userHeadImg;
    @BindView(R.id.nicheng)
    TextView nicheng;
    @BindView(R.id.xingbie)
    TextView xingbie;
    @BindView(R.id.qianming)
    TextView qianming;
    @BindView(R.id.shengri)
    TextView shengri;
    @BindView(R.id.llt_userinformation)
    LinearLayout lltUserinformation;
    @BindView(R.id.tv_userName)
    TextView tvUserName;
    @BindView(R.id.tv_wallet_num)
    TextView tvWalletNum;
    @BindView(R.id.usercenter_igb)
    ImageButton usercenterIgb;
    @BindView(R.id.llt_pet_name)
    LinearLayout lltPetName;
    @BindView(R.id.llt_giftdetails)
    LinearLayout lltGiftdetails;
    @BindView(R.id.llt_recharge)
    LinearLayout lltRecharge;
    @BindView(R.id.llt_kefu)
    LinearLayout lltKefu;
    @BindView(R.id.llt_mimaxiugai)
    LinearLayout lltMimaxiugai;
    @BindView(R.id.llt_mibaoyouxiang)
    LinearLayout lltMibaoyouxiang;
    @BindView(R.id.llt_mibaoshouji)
    LinearLayout lltMibaoshouji;
    @BindView(R.id.llt_qianbao)
    LinearLayout lltQianbao;
    @BindView(R.id.llt_chongzhijilu)
    LinearLayout lltChongzhijilu;
    @BindView(R.id.llt_xiaofeijilu)
    LinearLayout lltXiaofeijilu;
    @BindView(R.id.enter_setting)
    LinearLayout enterSetting;
    @BindView(R.id.sv_content)
    ScrollView svContent;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.option_recyclerView)
    RecyclerView optionRecyclerView;
    @BindView(R.id.ll_show_money)
    LinearLayout llShowMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);
        ButterKnife.bind(this);
        setupUI();
    }

    protected void setupUI() {
        if ("0".equals(BuildConfig.USE_MESSGAE)) {
            lltMibaoshouji.setVisibility(View.INVISIBLE);
        }
//        if(BuildConfig.projectCode==91){//91炎尚有定制不显示
//            llShowMoney.setVisibility(View.INVISIBLE);
//        }
        //2016-11-3 老周说那个全部不显示游戏币
        llShowMoney.setVisibility(View.INVISIBLE);

        tvUserName.setText(AppLoginControl.getAccount());
        optionRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        optionRecyclerView.setAdapter(new UserCenterOptionAdapter(mContext));
        optionRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, GridLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.bg_common)));
        optionRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, GridLayoutManager.VERTICAL, 1, getResources().getColor(R.color.bg_common)));
        //设置充值的回调
        OnPaymentListener onPaymentListener = new OnPaymentListener() {
            @Override
            public void paymentSuccess(PaymentCallbackInfo callbackInfo) {
                getUserWallet();
            }

            @Override
            public void paymentError(PaymentErrorMsg errorMsg) {
                getUserWallet();
            }
        };
        ChargeActivityForWap.setPaymentListener(onPaymentListener);
    }

    /**
     * 本页的点击事件
     */
    @OnClick({R.id.iv_return, R.id.enter_setting, R.id.llt_recharge, R.id.llt_giftdetails
            , R.id.llt_kefu, R.id.llt_mimaxiugai, R.id.llt_mibaoyouxiang, R.id.llt_mibaoshouji
            , R.id.llt_chongzhijilu, R.id.llt_xiaofeijilu})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                finish();
                break;
            case R.id.enter_setting: // 点击进入到设置中心
                SettingActivity.start(mContext);
                break;
//            case R.id.llt_recharge: // 点击充值到充值界面
//                ChargeActivityForWap.paymentListener = new OnPaymentListener() {
//                    @Override
//                    public void paymentSuccess(PaymentCallbackInfo callbackInfo) {
//                        getUserWallet();
//                    }
//
//                    @Override
//                    public void paymentError(PaymentErrorMsg errorMsg) {
//                        getUserWallet();
//                    }
//                };
//                String url = AppApi.BASE + "/float.php/Mobile/Wallet/charge";
//                ChargeActivityForWap.start(mContext, url, "支付充值");
//                break;
            case R.id.llt_giftdetails: // 点击到礼包界面
                UserGiftActivity.start(mContext);
                break;
            case R.id.llt_kefu: // 进入到客服界面
                WebViewActivity.start(mContext, "客服中心", AppApi.HELP_INDEX);
                break;
            case R.id.llt_mimaxiugai: // 进入到密码修改界面
                WebViewActivity.start(mContext, "密码修改", AppApi.UPDATE_PWD);
                break;
            case R.id.llt_mibaoyouxiang: // 进入到密保邮箱界面
                WebViewActivity.start(mContext, "密保邮箱", AppApi.SECURITY_EMAIL);
                break;
            case R.id.llt_mibaoshouji: // 进入到密保手机
                WebViewActivity.start(mContext, "密保手机", AppApi.SECURITY_MOBILE);
                break;

            case R.id.llt_chongzhijilu: // 充值记录
                WebViewActivity.start(mContext, "充值记录", AppApi.CHARGE_DETAIL);
                break;
            case R.id.llt_xiaofeijilu: // 消费记录
                WebViewActivity.start(mContext, "消费记录", AppApi.PAY_DETAIL);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserWallet();
    }

    private void getUserWallet() { // 刷新平台币
        HttpParams httpParams = AppApi.getHttpParams(true);
        NetRequest.request().
                setParams(httpParams).
                get(AppApi.URL_USER_WALLET, new HttpJsonCallBackDialog<UserWalletBean>() {
                    @Override
                    public void onDataSuccess(UserWalletBean data) {
                        if (data != null && data.getData() != null) {
                            BaseTextUtil.setText(tvWalletNum, data.getData().getRemain());
                        }
                    }
                });
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, UserCenterActivity.class);
        context.startActivity(starter);
    }
}
