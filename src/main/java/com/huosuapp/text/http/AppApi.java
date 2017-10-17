package com.huosuapp.text.http;

import android.content.Context;
import android.text.TextUtils;

import com.game.sdk.SdkConstant;
import com.game.sdk.db.impl.AgentDbDao;
import com.game.sdk.so.NativeListener;
import com.game.sdk.so.SdkNative;
import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.bean.SystemTimeBean;
import com.huosuapp.text.util.AppLoginControl;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.log.L;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;

/**
 * Created by liu hong liang on 2016/9/20.
 */
public class AppApi {
    public static final int CODE_NO_DATA=404;

    //公共参数
    public static final String APP_ID="appid";
    public static final String CLIENT_ID="clientid";
    public static final String FROM="from";
    public static final String AGENT="agent";
    public static  String BASE_URL = BuildConfig.BASE_URL;//基础地址 http://sdk.huosdk.com  或者 http://192.168.0.1
    public static String APP_URL_SUFFIX="/appapi/public/index.php/v1";
    public static String appid="";
    public static String agent="";
    public static String clientid="";
    public static String clientkey="";
    public static String from="3";//3是android
    public static String appkey = "";
//    static {
//        if(BuildConfig.projectCode==118){//逍遥游地址变动
//            URL_BASE_URL=BASE_URL+"/api/v1";
//        }
//    }
    /**
     * 客服中心
     */
    public static final String HELP_INDEX=String.format("%s/float.php/Mobile/Help/index",BASE_URL);
    /**
     * 密码修改
     */
    public static final String UPDATE_PWD=String.format("%s/float.php/Mobile/Password/uppwd",BASE_URL);
    /**
     * 密保邮箱
     */
    public static final String SECURITY_EMAIL=String.format("%s/float.php/Mobile/Security/email",BASE_URL);

    /**
     * 密保手机
     */
    public static final String SECURITY_MOBILE=String.format("%s/float.php/Mobile/Security/mobile",BASE_URL);
    /**
     * 钱包
     */
    public static final String WALLET=String.format("%s/float.php/Mobile/Wallet/charge",BASE_URL);
    /**
     * 充值记录
     */
    public static final String CHARGE_DETAIL=String.format("%s/float.php/Mobile/Wallet/charge_detail",BASE_URL);
    /**
     * 消费记录
     */
    public static final String PAY_DETAIL=String.format("%s/float.php/Mobile/Wallet/pay_detail",BASE_URL);
    /**
     * 忘记密码
     */
    public static final String FORGET_PWD=String.format("%s/float.php/Mobile/Forgetpwd/index",BASE_URL);

    /**
     * 支付url
     */
    public static final String CHARGE_URL=String.format("%s/float.php/Mobile/Wallet/charge",BASE_URL);



    /**
     * 游戏列表
     */
    public static final String URL_GAME_LIST=String.format("%s%s/game/list",BASE_URL,APP_URL_SUFFIX);

    /**
     * 游戏详情
     */
    public static final String URL_GAME_DETAIL=String.format("%s%s/game/detail",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取游戏评论列表
     */
    public static final String URL_COMMENT_LIST=String.format("%s%s/game/comment_list",BASE_URL,APP_URL_SUFFIX);

    /**
     * 添加游戏评论
     */
    public static final String URL_COMMENT_ADD=String.format("%s%s/game/comment_add",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取游戏类型列表
     */
    public static final String TYPE_LIST=String.format("%s%s/game/type_list",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取礼包列表
     */
    public static final String GIFT_LIST=String.format("%s%s/gift/list",BASE_URL,APP_URL_SUFFIX);
    /**
     * 激活码列表
     */
    public static final String CDKEY_LIST=String.format("%s%s/cdkey/list",BASE_URL,APP_URL_SUFFIX);

    /**
     * 用户注册
     * */
    public static final String USER_ADD=String.format("%s%s/user/add",BASE_URL,APP_URL_SUFFIX);

    /**
     * 用户登录
     */
    public static final String USER_LOGIN=String.format("%s%s/user/login",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取礼包用户列表
     * */
    public static final String USER_GIFT_LIST=String.format("%s%s/user/gift/list",BASE_URL,APP_URL_SUFFIX);
    /**
     * 获取激活码用户列表
     * */
    public static final String USER_CDKEY_LIST=String.format("%s%s/user/cdkey/list",BASE_URL,APP_URL_SUFFIX);


    /**
     * 领取礼包
     */
    public static final String GIFT_ADD=String.format("%s%s/user/gift/add",BASE_URL,APP_URL_SUFFIX);
    /**
     * 领取激活码
     */
    public static final String CDKEY_ADD=String.format("%s%s/user/cdkey/add",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取发送手机验证码
     */
    public static final String SMSCODE_SEND=String.format("%s%s/smscode/send",BASE_URL,APP_URL_SUFFIX);

    /**
     * 验证短信验证码
     */
    public static final String SMSCODE_CHECK=String.format("%s%s/smscode/check",BASE_URL,APP_URL_SUFFIX);


    /**
     * 获取客服信息
     */
    public static final String GET_HELP_INFO=String.format("%s%s/system/get_help_info",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取服务器时间
     */
    public static final String GET_SERVER_TIME=String.format("%s%s/system/get_server_time",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取开机闪屏图
     */
    public static final String URL_SEARCH_FIND=String.format("%s%s/system/get_splash",BASE_URL,APP_URL_SUFFIX);

    /**
     *打开APP
     */
    public static final String URL_OPENAPP=String.format("%s%s/system/openapp",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取版本信息
     */
    public static final String URL_GET_VERSION_INFO=String.format("%s%s/game/get_version_info",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取是否有新版本
     */
    public static final String URL_HAS_NEW_VERSION=String.format("%s%s/system/has_new_version",BASE_URL,APP_URL_SUFFIX);


    /**
     * 搜索列表
     */
    public static final String URL_SEARCH_LIST=String.format("%s%s/search/list",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取推荐搜索
     */
    public static final String RECOMMEND_LIST=String.format("%s%s/search/recommend_list",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取搜索热词
     */
    public static final String HOTWORD_LIST=String.format("%s%s/search/hotword_list",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取资讯列表
     */
    public static final String NEWS_LIST=String.format("%s%s/news/list",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取资讯详情
     */
    public static final String NES_GETDETAIL=String.format("%s%snews/getdetail",BASE_URL,APP_URL_SUFFIX);

    /**
     * 资讯详情页
     */
    public static final String NEWS_WEBDETAIL_ID=String.format("%s%s/news/webdetail/",BASE_URL,APP_URL_SUFFIX);

    /**
     * 游戏下载
     */
    public static final String GAME_DOWN=String.format("%s%s/game/down",BASE_URL,APP_URL_SUFFIX);

    /**
     * 获取轮播图
     */
    public static final String SLIDE_LIST=String.format("%s%s/slide/list",BASE_URL,APP_URL_SUFFIX);
    /**
     * 获取用户钱包金额
     */
    public static final String URL_USER_WALLET=String.format("%s%s/user/wallet/get_balance",BASE_URL,APP_URL_SUFFIX);
//    public static final String URL_USER_WALLET="http://192.168.1.102:8090/pc/mlogin/testData.do";


    public static HttpParams getHttpParams(boolean requestLogin){
        HttpParams httpParams=new HttpParams();
        httpParams.put(APP_ID,appid);
        httpParams.put(CLIENT_ID,clientid);
        httpParams.put(FROM,from);
        if(agent!=null){
            httpParams.put(AGENT,agent);
        }
        if(requestLogin){
            httpParams.putHeaders(AppLoginControl.HS_TOKEN,AppLoginControl.getHsToken());
            httpParams.putHeaders(AppLoginControl.TIMESTAMP, AppLoginControl.getTimestamp());
            httpParams.putHeaders(AppLoginControl.TOKEN_DATA, AppLoginControl.getTokenData());
        }
        //统一添加设备码
        if(SdkConstant.deviceBean!=null&& !TextUtils.isEmpty(SdkConstant.deviceBean.getDevice_id())){
            httpParams.put("deviceid",SdkConstant.deviceBean.getDevice_id() );
            httpParams.put("deviceinfo",SdkConstant.deviceBean.getDeviceinfo());
        }
        return httpParams;
    }

    /**
     * 获取清单文件数据，初始化常量
     * @param context
     */
    public static void initAgentAndAppid(final Context context) {
        SdkNative.soInit(context);
        new Thread(){
            @Override
            public void run() {
                try {
                    getRsaPublicKey(context,1);
                    initTimestamp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void getRsaPublicKey(final Context context, final int count){
        if(count<=3){
            //初始化本地c配置
            if(SdkNative.initLocalConfig(context,SdkNative.TYPE_APP)){
                sdkToSdk(context);//把appid这些注入进去
                SdkNative.initNetConfig(context, new NativeListener() {
                    @Override
                    public void onSuccess() {
                        sdkToSdk(context);
                    }
                    @Override
                    public void onFail(int code, String msg) {
                        L.e("hongliang","执行失败："+msg);
                        getRsaPublicKey(context,count+1);
                    }
                });
            }else{
                sdkToSdk(context);
            }
        }
    }
    private static void sdkToSdk(Context context){
        AppApi.appid= SdkConstant.HS_APPID;
        AppApi.clientid= SdkConstant.HS_CLIENTID;
        AppApi.clientkey= SdkConstant.HS_CLIENTKEY;
        AppApi.agent=SdkConstant.HS_AGENT;
        AppApi.appkey=SdkConstant.HS_APPKEY;
        AppApi.BASE_URL=SdkConstant.BASE_URL;//转换基础请求地质
        L.e("hongliang","执行成功：agent"+AppApi.agent);
        L.e("hongliang","执行成功：mid_BASE_URL_url"+AppApi.BASE_URL);
        L.e("hongliang","执行成功：Rsa"+SdkConstant.RSA_PUBLIC_KEY);
        //使用前先获取agentDbDao内部有检查数据库表结构是否完整，不完整将自动创建
        //解决之前分包升级数据库造成的用户数据表没有创建的问题
        AgentDbDao.getInstance(context);
    }
    /**
     * 获得服务器时间
     */
    private static void initTimestamp(){
        final long requestTime=System.currentTimeMillis();
        NetRequest.request()
                .setParams(getHttpParams(false))
                .setShowErrorToast(false)
                .get(AppApi.GET_SERVER_TIME, new HttpJsonCallBackDialog<SystemTimeBean>(){
                    @Override
                    public void onDataSuccess(SystemTimeBean data) {
                        long timeStamp;
                        if(data.getData()==null){
                            timeStamp=0;
                        }else{
                            timeStamp=requestTime-data.getData().getTime();
                        }
                        AppLoginControl.saveTimestamp(""+timeStamp);
                    }
                });
    }
}
