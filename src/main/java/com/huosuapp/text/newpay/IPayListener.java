package com.huosuapp.text.newpay;

/**
 * Created by LiuHongLiang on 2016/10/30.
 */

public interface IPayListener {
    public void paySuccess(String orderId,double money);
    public void payFail(String orderId,double money,String msg);
}
