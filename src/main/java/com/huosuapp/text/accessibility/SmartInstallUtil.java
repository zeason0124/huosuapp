package com.huosuapp.text.accessibility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.ui.dialog.DownHintDialog;
import com.liang530.application.BaseApplication;
import com.liang530.manager.AppManager;
import com.liang530.utils.BaseAppUtil;

/**
 * Created by liu hong liang on 2016/12/22.
 */

public class SmartInstallUtil {

    private static final String TAG = SmartInstallUtil.class.getSimpleName();
    private final static String OPEN_COUNT = "open_count";
    private final static String USE_SMART_INSTALL="use_smart_install";//标记用户是否使用辅助自动安装功能
    private Dialog downHintDialog;
    private DownHintDialog.ConfirmDialogListener mlistener;


    public static void showSmartInstallDialog(Context context, final OnSmartInstallOpenListener smartInstallOpenListener) {
        View dialogview = LayoutInflater.from(context).inflate(R.layout.dialog_open_smart_install, null);
        final Dialog smartInstallHintDialog = new Dialog(context, R.style.dialog_bg_style);
        //设置view
        smartInstallHintDialog.setContentView(dialogview);
        smartInstallHintDialog.setCanceledOnTouchOutside(true);
        //通过window来设置位置、高宽
        Window window = smartInstallHintDialog.getWindow();
        WindowManager.LayoutParams windowparams = window.getAttributes();
        windowparams.width = BaseAppUtil.getDeviceWidth(context) - 2 * BaseAppUtil.dip2px(context, 16);
//        设置背景透明,但是那个标题头还是在的，只是看不见了
        //注意：不设置背景，就不能全屏
//        window.setBackgroundDrawableResource(android.R.color.transparent);
        TextView btok = (TextView) dialogview.findViewById(R.id.tv_confirm);
        TextView btcancel = (TextView) dialogview.findViewById(R.id.tv_cancel);
        btok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForwardToAccessibility();
                smartInstallHintDialog.dismiss();
            }
        });
        btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(smartInstallOpenListener!=null){
                    smartInstallOpenListener.cancel();
                }
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
                int openCount = sharedPreferences.getInt(OPEN_COUNT, 0);
                sharedPreferences.edit().putInt(OPEN_COUNT, openCount + 1).commit();
                smartInstallHintDialog.dismiss();

            }
        });
        smartInstallHintDialog.show();
    }

    public static boolean requestAccessibilityService() {

        Activity activity = AppManager.getAppManager().currentActivity();
        if (activity != null) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
            int openCount = sharedPreferences.getInt(OPEN_COUNT, 0);
            if (openCount > 0) {
                return false;
            }
            if (!isAccessibilitySettingsOn(activity, SmartInstallService.class)) {
                showSmartInstallDialog(activity,null);
                return true;
            }
        }
        return false;
    }

    /**
     * 开启辅助服务功能
     */
    public static void onForwardToAccessibility() {
        try{
            Activity activity = AppManager.getAppManager().currentActivity();
            if (activity != null) {
                SharedPreferences sharedPreferences = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE);
                int openCount = sharedPreferences.getInt(OPEN_COUNT, 0);
                sharedPreferences.edit().putInt(OPEN_COUNT, openCount + 1).commit();
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                activity.startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 判断服务是否可用
     *
     * @param mContext
     * @param AccessibilityServiceClazz
     * @return
     */
    public static boolean isAccessibilitySettingsOn(Context mContext, Class AccessibilityServiceClazz) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + AccessibilityServiceClazz.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }
    public static void setUseSmartInstall(boolean useSmartInstall){
        SharedPreferences sharedPreferences = BaseApplication.getInstance().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(USE_SMART_INSTALL,useSmartInstall).commit();
    }
    public static boolean getUseSmartInstall(){
        SharedPreferences sharedPreferences = BaseApplication.getInstance().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        boolean aBoolean = sharedPreferences.getBoolean(USE_SMART_INSTALL, true);
        return aBoolean;
    }
    public static interface OnSmartInstallOpenListener{
        void cancel();
    }
}
