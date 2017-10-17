package com.huosuapp.text.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huosuapp.text.R;
import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.listener.IGameLayout;
import com.huosuapp.text.ui.GameDetailActivity;
import com.liang530.application.BaseApplication;
import com.liang530.utils.BaseAppUtil;

import java.util.List;


/**
 * Created by LiuHongLiang on 2016/9/25.
 */

public class GameGridAdapter extends RecyclerView.Adapter {
    private List<GameBean> datas;

    public List<GameBean> getDatas() {
        return datas;
    }

    public void setDatas(List<GameBean> datas) {
        this.datas = datas;
    }
    int itemWidth;
    public GameGridAdapter(List<GameBean> datas) {
        this.datas = datas;
        int parentWidth = BaseAppUtil.getDeviceWidth(BaseApplication.getInstance());
        parentWidth=parentWidth-2* BaseAppUtil.dip2px(BaseApplication.getInstance(),12);
        itemWidth=parentWidth/3;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rec_grid_item,parent,false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder.itemView instanceof IGameLayout){
            IGameLayout iGameLayout= (IGameLayout) holder.itemView;
            GameBean gameBean = datas.get(position);
            gameBean.setItemPosition(position);
            iGameLayout.setGameBean(gameBean);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameDetailActivity.start(v.getContext(),datas.get(position).getGameid());
            }
        });
    }

    @Override
    public int getItemCount() {
        if(datas==null) return 0;
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ViewHolder(View view) {
            super(view);
        }
    }
}
