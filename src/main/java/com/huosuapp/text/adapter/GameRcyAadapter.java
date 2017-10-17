package com.huosuapp.text.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.listener.IGameLayout;
import com.huosuapp.text.ui.GameDetailActivity;
import com.huosuapp.text.view.ListGameItem;
import com.liang530.views.refresh.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu hong liang on 2016/9/26.
 */

public class GameRcyAadapter  extends RecyclerView.Adapter implements IDataAdapter<List<GameBean>> {
    List<GameBean> gameBeanList = new ArrayList<>();
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = new ListGameItem(parent.getContext());
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(layoutParams==null){
            layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
        }else{
            layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height=ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        return new GameListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GameListViewHolder gameListViewHolder= (GameListViewHolder) holder;
        final GameBean gameBean = gameBeanList.get(position);
        gameBean.setItemPosition(position);
        if(gameListViewHolder.itemView instanceof IGameLayout){
            IGameLayout iGameLayout= (IGameLayout) gameListViewHolder.itemView;
            iGameLayout.setGameBean(gameBean);
        }
        gameListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameDetailActivity.start(v.getContext(),gameBean.getGameid());
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameBeanList.size();
    }

    @Override
    public void notifyDataChanged(List<GameBean> gameBeen, boolean isRefresh) {
        if(isRefresh){
            gameBeanList.clear();
        }
        gameBeanList.addAll(gameBeen);
        notifyDataSetChanged();
    }

    @Override
    public List<GameBean> getData() {
        return gameBeanList;
    }

    @Override
    public boolean isEmpty() {
        return gameBeanList.isEmpty();
    }
    static class GameListViewHolder extends RecyclerView.ViewHolder{
        GameListViewHolder(View view) {
            super(view);
        }
    }
}
