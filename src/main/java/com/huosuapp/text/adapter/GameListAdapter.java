package com.huosuapp.text.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.listener.IGameLayout;
import com.huosuapp.text.ui.GameDetailActivity;
import com.huosuapp.text.view.ListGameItem;
import com.liang530.views.grid.GridLayoutAdapter;

import java.util.List;

/**
 * Created by LiuHongLiang on 2016/9/25.
 */

public class GameListAdapter extends GridLayoutAdapter {
    private List<GameBean> datas;

    public List<GameBean> getDatas() {
        return datas;
    }

    public void setDatas(List<GameBean> datas) {
        this.datas = datas;
    }

    public GameListAdapter(List<GameBean> datas) {
        this.datas = datas;
    }

    @Override
    protected int getCount() {
        if(datas==null) return 0;
        return datas.size();
    }

    @Override
    protected View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=new ListGameItem(parent.getContext());
            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            if(layoutParams==null){
                layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                convertView.setLayoutParams(layoutParams);
            }else{
                layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height=ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        if(holder.view instanceof IGameLayout){
            IGameLayout iGameLayout= (IGameLayout) holder.view;
            GameBean gameBean = datas.get(position);
            gameBean.setItemPosition(position);
            iGameLayout.setGameBean(gameBean);
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameDetailActivity.start(v.getContext(),datas.get(position).getGameid());
            }
        });
        return convertView;
    }
    static class ViewHolder {
        private View view;
        ViewHolder(View view) {
            this.view=view;
        }
    }
}
