package com.huosuapp.text.pay.ecopay;

/**
 * 每个支付都有自己的url constant class这样可以裁剪
 * */
public class EcoConstant {
	/**
	 * 易联预支付地址(sdk向sevice端请求)
	 * */
//	public static final String URL_CHARGER_PAYECO = Constants.URL_SER_SDKPRE
//			+ "/payeco/payeco.php";

	public static final String ENVIRONMENT = "01";// "00"测试环境 ."01"生产环境

	public final static String BROADCAST_PAY_END = "com.merchant.broadcast";// 广播

	/**
	 * 易联支付消息通知(这个现在已经重服务器获取数据)
	 */
//	public final static String URL_PAY_NOTIFY = Constants.URL_SER_SDKPRE
//			+ "/payeco/Return_url.php";
}
