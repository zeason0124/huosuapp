package com.huosuapp.text.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.bean.InformationBean;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.ui.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.liang530.utils.BaseAppUtil;
import com.liang530.views.refresh.mvc.IDataAdapter;

/**
 * Created by LiuHongLiang on 2016/9/21.
 */
public class InformationRcyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<List<InformationBean.Information>> {
    private List<InformationBean.Information> datas=new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_information, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        //94飞摩游戏的定制图片大小
        if(BuildConfig.projectCode==94){
            ViewGroup.LayoutParams layoutParams = viewHolder.ivInfoImg.getLayoutParams();
            if(layoutParams==null){
                layoutParams=new ViewGroup.LayoutParams(BaseAppUtil.dip2px(viewHolder.ivInfoImg.getContext(),100),
                        BaseAppUtil.dip2px(viewHolder.ivInfoImg.getContext(),57));
                viewHolder.ivInfoImg.setLayoutParams(layoutParams);
            }else{
                layoutParams.width= BaseAppUtil.dip2px(viewHolder.ivInfoImg.getContext(),100);
                layoutParams.height= BaseAppUtil.dip2px(viewHolder.ivInfoImg.getContext(),57);
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh= (ViewHolder) holder;
        vh.tvInforTitle.setText(datas.get(position).getTitle());
        vh.tvInfotTime.setText(datas.get(position).getPudate());
        Glide.with(vh.ivInfoImg.getContext()).load(datas.get(position).getImg())
                    .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(vh.ivInfoImg);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=AppApi.NEWS_WEBDETAIL_ID+datas.get( vh.getAdapterPosition()).getId();
                WebViewActivity.start(v.getContext(),"资讯详情",url );
            }
        });
    }
    @Override
    public int getItemCount() {
        return datas.size();

    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void notifyDataChanged(List<InformationBean.Information> informations, boolean isRefresh) {
        if(isRefresh){
            datas.clear();
        }
        datas.addAll(informations);
        notifyDataSetChanged();
    }
    @Override
    public List<InformationBean.Information> getData() {
        return datas;
    }

    @Override
    public boolean isEmpty() {
        return datas.isEmpty();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_infoImg)
        ImageView ivInfoImg;
        @BindView(R.id.tv_infor_title)
        TextView tvInforTitle;
        @BindView(R.id.tv_infot_time)
        TextView tvInfotTime;
        @BindView(R.id.information_type)
        TextView informationType;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
