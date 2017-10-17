package com.huosuapp.text.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.huosuapp.text.newpay.IHuoPay;
import com.huosuapp.text.newpay.IPayListener;
import com.liang530.log.L;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by liu hong liang on 2016/10/13.
 */

public class CommonJsInterfaceForWeb {
    public static OnPaymentListener paymentListener;// 充值接口监听
    private static PaymentErrorMsg paymentErrorMsg;//支付失败的回调信息
    private static PaymentCallbackInfo paymentCallbackInfo;//支付成功的回调信息
    private Activity context;
    private static double  chargeMoney;
    private IPayListener iPayListener;
    IHuoPay iHuoPay;
    public CommonJsInterfaceForWeb(Activity context, double charge_money, IPayListener iPayListener) {
        this.context = context;
        this.chargeMoney=charge_money;
        this.iPayListener=iPayListener;
    }

    @JavascriptInterface
    public void huoPay(String payinfo){
        L.e("CommonJsInterfaceForWeb","data="+payinfo);
        try {
            JSONObject jsonObject=new JSONObject(payinfo);
            String paytype = jsonObject.optString("paytype");
            InputStream open = context.getAssets().open("huo_pay_list.json");
            BufferedReader reader=new BufferedReader(new InputStreamReader(open));
            StringBuffer buffer=new StringBuffer();
            String line=null;
            while ((line=reader.readLine())!=null){
                buffer.append(line);
            }
            JSONArray payJsonArray=new JSONArray(buffer.toString());
            for(int i=0;i<payJsonArray.length();i++){
                JSONObject payJsonObject = payJsonArray.optJSONObject(i);
                String tempPaytype = payJsonObject.optString("paytype");
                String className = payJsonObject.optString("className");
                if(paytype.equals(tempPaytype)){
                    iHuoPay = (IHuoPay) Class.forName("com.huosuapp.text.newpay." + className).newInstance();
                    iHuoPay.startPay(iPayListener,chargeMoney,payinfo);
                    return;
                }
            }
            if("gamepay".equals(paytype)){
                String orderid = jsonObject.optString("orderid");
                String info = jsonObject.optString("info");
                int status = jsonObject.optInt("status");
                if(status==1){
                    iPayListener.payFail(orderid,chargeMoney,"未支付");
                }else if(status==2){
                    iPayListener.paySuccess(orderid,chargeMoney);
                    setPaySuccess("支付成功");
                }else if(status==3){
                    iPayListener.payFail(orderid,chargeMoney,"支付失败");
                }
            }else{
                Toast.makeText(context,"暂不支持此支付方式！",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,"支付参数读取失败",Toast.LENGTH_SHORT).show();
        }

    }

    @JavascriptInterface
    public void closeWeb(){
        context.finish();
    }
    @JavascriptInterface
    public void changeAccount(){

    }
    /**
     * 拨打电话，跳转到拨号界面
     *
     * @param context
     * @param phoneNumber
     */
    public static void callDial(Context context, String phoneNumber) {
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
    }
    @JavascriptInterface
    public void openQq(String qq){
        String url="mqqwpa://im/chat?chat_type=wpa&uin="+qq;
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
    @JavascriptInterface
    public void openPhone(String phone){
        callDial(context,phone);
    }
    @JavascriptInterface
    public void joinQqgroup(String key){
        joinQQGroup(key);
    }

    /****************
     *
     * 发起添加群流程。群号：测试群(594245585) 的 key 为： n62NA_2zzhPfmNicq-sZLioBGiN2v7Oq
     * 调用 joinQQGroup(n62NA_2zzhPfmNicq-sZLioBGiN2v7Oq) 即可发起手Q客户端申请加群 测试群(594245585)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            Toast.makeText(context,"未安装手Q或安装的版本不支持",Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public static void setPaySuccess(String msg) {
        PaymentCallbackInfo paymentCallbackInfo=new PaymentCallbackInfo();
        paymentCallbackInfo.money=chargeMoney;
        paymentCallbackInfo.msg=msg;
        CommonJsInterfaceForWeb.paymentCallbackInfo = paymentCallbackInfo;
        CommonJsInterfaceForWeb.paymentErrorMsg = null;
    }



    public static void setPayFail(int code,String msg) {
        CommonJsInterfaceForWeb.paymentErrorMsg = new PaymentErrorMsg();
        CommonJsInterfaceForWeb.paymentErrorMsg.money=chargeMoney;
        CommonJsInterfaceForWeb.paymentErrorMsg.msg=msg;
        CommonJsInterfaceForWeb.paymentErrorMsg.code=code;
        CommonJsInterfaceForWeb.paymentCallbackInfo = null;
    }

    public void onDestory(){
        if(iHuoPay!=null){
            iHuoPay.onDestory();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(iHuoPay!=null){
            iHuoPay.onActivityResult(requestCode,resultCode,data);
        }
    }
}
