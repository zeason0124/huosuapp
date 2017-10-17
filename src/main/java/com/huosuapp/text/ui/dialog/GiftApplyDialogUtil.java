package com.huosuapp.text.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huosuapp.text.R;

import com.liang530.log.T;
import com.liang530.utils.BaseAppUtil;

/**
 * Created by liu hong liang on 2016/10/11.
 */

public class GiftApplyDialogUtil {
    public static void showApplyDialog(final Context context, String codeName, final String codeContent){
        View contentView  = LayoutInflater.from(context).inflate(R.layout.dialog_gift_code, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((BaseAppUtil.getDeviceWidth(context)- BaseAppUtil.dip2px(context,50)), ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tvCopy = (TextView) contentView.findViewById(R.id.tv_copy);
        TextView tvHint = (TextView) contentView.findViewById(R.id.tv_hint);
        TextView tvDialogClose = (TextView) contentView.findViewById(R.id.tv_dialog_close);
        TextView tvCodeName = (TextView) contentView.findViewById(R.id.tv_codeName);
        String content="%s领取成功,\r\n请在%s有效期间，在游戏内使用。";
        tvHint.setText(String.format(content,codeName,codeName));
        TextView tvCodeContent = (TextView) contentView.findViewById(R.id.tv_codeContent);
        tvCodeContent.setText(codeContent);
        if(codeName.contains("码")){
            tvCodeName.setText(codeName+":");
        }else{
            tvCodeName.setText(codeName+"码:");
        }
        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAppUtil.copyToSystem(context,codeContent);
                T.s(context,"复制成功");
            }
        });
        final Dialog dialog=new Dialog(context,R.style.dialog_bg_style);
        tvDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(contentView, lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


}
