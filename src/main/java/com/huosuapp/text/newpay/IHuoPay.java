package com.huosuapp.text.newpay;

import android.content.Intent;

/**
 * Created by LiuHongLiang on 2016/10/30.
 */

public interface IHuoPay {
    public void startPay(IPayListener listener,double money,String payinfo);
    public void onActivityResult(int requestCode, int resultCode, Intent data);
    public void onDestory();
}
