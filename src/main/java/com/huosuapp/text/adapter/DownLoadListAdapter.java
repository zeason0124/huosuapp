package com.huosuapp.text.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huosuapp.text.R;
import com.huosuapp.text.bean.TasksManagerModel;
import com.huosuapp.text.view.DownManagerPbLayoutView;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.liang530.views.refresh.mvc.IDataAdapter;

/**
 * Created by liu hong liang on 2016/10/7.
 */

public class DownLoadListAdapter extends RecyclerView.Adapter implements IDataAdapter<List<TasksManagerModel>> {
    List<TasksManagerModel> datas = new LinkedList<>();
    private int type;
    public DownLoadListAdapter(int type) {
        this.type=type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_down_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder= (ViewHolder) holder;
        viewHolder.downManagerPbView.setTasksManagerModel(datas.get(position),type);
        viewHolder.downManagerPbView.setAdapter(this);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public List<TasksManagerModel> getDatas() {
        return datas;
    }


    @Override
    public void notifyDataChanged(List<TasksManagerModel> tasksManagerModels, boolean isRefresh) {
        datas.clear();
        datas.addAll(tasksManagerModels);
        notifyDataSetChanged();
    }

    @Override
    public List<TasksManagerModel> getData() {
        return datas;
    }

    @Override
    public boolean isEmpty() {
        return datas.isEmpty();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.downManagerPbView)
        DownManagerPbLayoutView downManagerPbView;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
