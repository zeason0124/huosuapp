package com.huosuapp.text.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.db.UserInfo;
import com.huosuapp.text.db.UserLoginInfodao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu hong liang on 2016/9/27.
 */

public class RecordUserAdapter extends BaseAdapter {
    List<UserInfo> userLoginInfo;
    UserLoginInfodao dao;

    public RecordUserAdapter(Context context) {
        dao= UserLoginInfodao.getInstance(context);
        userLoginInfo= dao.getUserLoginInfo();
        if(userLoginInfo==null){
            userLoginInfo=new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return userLoginInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return userLoginInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(parent.getContext()).inflate( R.layout.adapter_record_user,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.tvUserName= (TextView) convertView.findViewById(R.id.tv_user_name);
            viewHolder.ivDelete= (ImageView) convertView.findViewById(R.id.iv_delete);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.deleteUserLoginByName(userLoginInfo.get(position).username);
                userLoginInfo.remove(position);
                notifyDataSetChanged();
            }
        });
        viewHolder.tvUserName.setText(userLoginInfo.get(position).username);
        return convertView;
    }
    public static class ViewHolder{
        public TextView tvUserName;
        public ImageView ivDelete;
    }
}
