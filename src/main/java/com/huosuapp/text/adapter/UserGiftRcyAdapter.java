package com.huosuapp.text.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.bean.GiftCodeBean;
import com.huosuapp.text.ui.fragment.GiftListFragment;
import com.liang530.log.T;
import com.liang530.utils.BaseAppUtil;
import com.liang530.utils.GlideDisplay;
import com.liang530.views.refresh.mvc.BaseRefreshLayout;
import com.liang530.views.refresh.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by LiuHongLiang on 2016/9/25.
 */
public class UserGiftRcyAdapter extends RecyclerView.Adapter implements IDataAdapter<List<GiftCodeBean.Gift>> {
    private final BaseRefreshLayout<List<GiftCodeBean.Gift>> baseRefreshLayout;

    private List<GiftCodeBean.Gift> datas = new ArrayList<>();
    private int type;
    private Context context;

    public UserGiftRcyAdapter(int type, Context context, BaseRefreshLayout<List<GiftCodeBean.Gift>> baseRefreshLayout) {
        this.type = type;
        this.context = context;
        this.baseRefreshLayout = baseRefreshLayout;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_gift_code, parent, false);
        return new GiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final GiftViewHolder vh = (GiftViewHolder) holder;
        final GiftCodeBean.Gift gift = datas.get(position);
        vh.tvGiftName.setText(gift.getGiftname());
        vh.tvStartTime.setText("起止时间" + gift.getStarttime() + " " + gift.getEnttime());
        //礼包内容
        vh.tvGiftDetail.setText(gift.getContent());
        //礼包剩余数量
        vh.tvGiftCode.setText(gift.getCode());
        vh.tvCopyCode.setText("复制");
        if (type == GiftListFragment.TYPE_GIFT) {
            vh.rlGiftDetail.setVisibility(View.VISIBLE);
        } else {
            vh.rlGiftDetail.setVisibility(View.GONE);
        }
        vh.tvCopyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseAppUtil.copyToSystem(v.getContext(),gift.getCode());
                T.s(v.getContext(),"复制成功");
            }
        });
        GlideDisplay.dispalyWithNoFade(vh.ivGiftIcon,gift.getIcon());
    }
    @Override
    public int getItemCount() {
        return datas.size();
    }
    @Override
    public void notifyDataChanged(List<GiftCodeBean.Gift> gifts, boolean isRefresh) {
        if (isRefresh) {
            datas.clear();
        }
        datas.addAll(gifts);
        notifyDataSetChanged();
    }

    @Override
    public List<GiftCodeBean.Gift> getData() {
        return datas;
    }

    @Override
    public boolean isEmpty() {
        return datas.isEmpty();
    }

    static class GiftViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_gift_icon)
        ImageView ivGiftIcon;
        @BindView(R.id.tv_gift_name)
        TextView tvGiftName;
        @BindView(R.id.tv_count_hint)
        TextView tvCountHint;
        @BindView(R.id.tv_gift_code)
        TextView tvGiftCode;
        @BindView(R.id.ll_code_show)
        LinearLayout llCodeShow;
        @BindView(R.id.tv_gift_content)
        TextView tvGiftContent;
        @BindView(R.id.tv_gift_detail)
        TextView tvGiftDetail;
        @BindView(R.id.rl_gift_detail)
        RelativeLayout rlGiftDetail;
        @BindView(R.id.tv_start_time)
        TextView tvStartTime;
        @BindView(R.id.tv_copy_code)
        TextView tvCopyCode;

        GiftViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
