package com.huosuapp.text.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.bean.ClassifyListBean;
import com.huosuapp.text.ui.GameListActivity;
import com.liang530.views.refresh.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liu hong liang on 2016/9/26.
 */

public class ClassifyRcyGridAdapter extends RecyclerView.Adapter implements IDataAdapter<List<ClassifyListBean.ClassifyBean>> {
    private List<ClassifyListBean.ClassifyBean> datas = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_classify_grid, parent, false);
        return new ClassifyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ClassifyViewHolder classifyViewHolder = (ClassifyViewHolder) holder;
        classifyViewHolder.tvClassifyName.setText(datas.get(position).getTypename());
        classifyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClassifyListBean.ClassifyBean classifyBean = datas.get(classifyViewHolder.getAdapterPosition());
                GameListActivity.start(v.getContext(),
                        classifyBean.getTypename(), classifyBean.getTypeid() + "", null, null, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void notifyDataChanged(List<ClassifyListBean.ClassifyBean> classifyBeen, boolean isRefresh) {
        if (isRefresh) {
            datas.clear();
        }
        datas.addAll(classifyBeen);
        notifyDataSetChanged();
    }

    @Override
    public List<ClassifyListBean.ClassifyBean> getData() {
        return datas;
    }

    @Override
    public boolean isEmpty() {
        return datas.isEmpty();
    }

    static class ClassifyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_classifyName)
        TextView tvClassifyName;

        ClassifyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
