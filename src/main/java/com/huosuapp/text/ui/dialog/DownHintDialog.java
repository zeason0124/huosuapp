package com.huosuapp.text.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.liang530.utils.BaseAppUtil;

/**
 * Created by 刘红亮 on 2015/10/30 14:31.
 */

public class DownHintDialog {
    private Dialog downHintDialog;
    private ConfirmDialogListener mlistener;
    public void showDialog(Context context,boolean showCancel,String content,ConfirmDialogListener listener){
        dismiss();
        this.mlistener=listener;
        View dialogview = LayoutInflater.from(context).inflate(R.layout.dialog_down_hint, null);
        downHintDialog = new Dialog(context,R.style.dialog_bg_style);
        //设置view
        downHintDialog.setContentView(dialogview);
        downHintDialog.setCanceledOnTouchOutside(false);
        //dialog默认是环绕内容的
        //通过window来设置位置、高宽
        Window window = downHintDialog.getWindow();
        WindowManager.LayoutParams windowparams = window.getAttributes();
////        window.setGravity(Gravity.BOTTOM);
//        windowparams.height = ;
        windowparams.width = BaseAppUtil.getDeviceWidth(context)-2* BaseAppUtil.dip2px(context, 16);
//        设置背景透明,但是那个标题头还是在的，只是看不见了
        //注意：不设置背景，就不能全屏
//        window.setBackgroundDrawableResource(android.R.color.transparent);

        TextView btok = (TextView) dialogview.findViewById(R.id.confirm_tv);
        TextView btcancel = (TextView) dialogview.findViewById(R.id.cancel_tv);
        TextView text= (TextView) dialogview.findViewById(R.id.content_text);
        if(content!=null){
            text.setText(content);
        }

        if(showCancel){
            btcancel.setVisibility(View.VISIBLE);
        }else{
            btcancel.setVisibility(View.GONE);
        }
        btok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mlistener!=null){
                    mlistener.ok();
                }
                dismiss();
            }
        });
        btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mlistener!=null){
                    mlistener.cancel();
                }
                dismiss();
            }
        }
        );
        downHintDialog.show();
    }
    public void dismiss(){
        if(downHintDialog !=null){
            downHintDialog.dismiss();
            mlistener=null;
        }
    }
    public interface ConfirmDialogListener{
        void ok();
        void cancel();

    }
}
