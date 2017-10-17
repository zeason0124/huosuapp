package com.huosuapp.text.update;

import android.app.Activity;
import android.content.Intent;

import com.huosuapp.text.bean.VersionUpdateBean;
import com.huosuapp.text.http.AppApi;
import com.kymjs.rxvolley.client.HttpParams;

import com.liang530.log.T;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.utils.BaseAppUtil;

/**
 * Created by 刘红亮 on 2016/4/1.
 * 版本更新管理
 */
public class VersionUpdateManager {
    private Activity mContext;
    private VersionUpdateListener mlistener;
    public void checkVersionUpdate(Activity context, VersionUpdateListener listener) {
        this.mContext = context;
        this.mlistener = listener;
        HttpParams params = AppApi.getHttpParams(true);
        params.put("verid", BaseAppUtil.getAppVersionCode());
        params.put("vername", BaseAppUtil.getAppVersionName());
        NetRequest.request(mContext).setParams(params).setFlag("checkVersion").get(AppApi.URL_HAS_NEW_VERSION, new HttpJsonCallBackDialog<VersionUpdateBean>(){
            @Override
            public void onDataSuccess(final VersionUpdateBean data) {
                if(data!=null&&data.getData()!=null&&data.getData().getHasnew()==1){
                    new UpdateVersionDialog().showDialog(mContext, true, "有新版本发布了，是否需要下载更新？", new UpdateVersionDialog.ConfirmDialogListener() {
                        @Override
                        public void ok() {
                            Intent intent = new Intent(mContext, UpdateVersionService.class);
                                    intent.putExtra("url", data.getData().getNewurl());
                            mContext.startService(intent);
                            T.s(mContext, "开始下载,请在下载完成后确认安装！");
                            if (mlistener != null) {
                                mlistener.gotoDown();
                            }
                        }
                        @Override
                        public void cancel() {
                            if (mlistener != null) {
                                mlistener.cancel(null);
                            }
                        }
                    });
                }else{
                    mlistener.cancel("已是最新版本！");
                }
            }

            @Override
            public void onJsonSuccess(int code, String msg, String data) {
                mlistener.cancel(""+msg);
            }
        });
    }

    public interface VersionUpdateListener {
        /**
         * 获取到更新去下载去了
         */
        void gotoDown();

        /**
         * 无更新或者用户取消了更新
         */
        void cancel(String msg);
    }
}
