package com.huosuapp.text.ui.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.huosuapp.text.R;
import com.huosuapp.text.adapter.RecordUserAdapter;
import com.huosuapp.text.db.UserInfo;

/**
 * Created by liu hong liang on 2016/9/27.
 */

public class RecordUserPopUtil {
    public static void showRecordUserListPop(Context context, final EditText user_name, final EditText userPwd){
        View contentView  = LayoutInflater.from(context).inflate(R.layout.pop_show_user_list, null);
        final PopupWindow popupWindow=new PopupWindow(contentView,user_name.getWidth(),ViewGroup.LayoutParams.WRAP_CONTENT);
        final ListView lvRecordUser = (ListView) contentView.findViewById(R.id.lv_user_record);
        lvRecordUser.setAdapter(new RecordUserAdapter(user_name.getContext()));
        lvRecordUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = lvRecordUser.getAdapter().getItem(position);
                if(item instanceof UserInfo){
                    UserInfo userInfo= (UserInfo) item;
                    user_name.setText(userInfo.username);
                    userPwd.setText(userInfo.password);
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        contentView.setFocusable(true);
        contentView.setFocusableInTouchMode(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(user_name);
    }
}
