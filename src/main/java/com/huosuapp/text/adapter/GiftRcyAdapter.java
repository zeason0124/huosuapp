package com.huosuapp.text.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.bean.ApplyGiftBean;
import com.huosuapp.text.bean.GiftCodeBean;
import com.huosuapp.text.bean.HiddenActivateCodeUiEvent;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.ui.GameDetailActivity;
import com.huosuapp.text.ui.dialog.GiftApplyDialogUtil;
import com.huosuapp.text.ui.fragment.GiftListFragment;
import com.kymjs.rxvolley.client.HttpParams;
import com.liang530.log.T;
import com.liang530.rxvolley.HttpJsonCallBackDialog;
import com.liang530.rxvolley.NetRequest;
import com.liang530.utils.GlideDisplay;
import com.liang530.views.refresh.mvc.AdvRefreshListener;
import com.liang530.views.refresh.mvc.IDataAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by LiuHongLiang on 2016/9/25.
 */
public class GiftRcyAdapter extends RecyclerView.Adapter implements IDataAdapter<List<GiftCodeBean.Gift>> {
    private final AdvRefreshListener advRefreshListener;
    private List<GiftCodeBean.Gift> datas = new ArrayList<>();
    private int type;
    private Context context;
    public GiftRcyAdapter(int type, Context context, AdvRefreshListener advRefreshListener) {
        this.type=type;
        this.context=context;
        this.advRefreshListener=advRefreshListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_gift_code, parent, false);
        return new GiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final GiftViewHolder vh = (GiftViewHolder ) holder;
        final GiftCodeBean.Gift gift = datas.get(position);
        vh.tvGiftName.setText(gift.getGiftname());
        vh.tvStartTime.setText("起止时间" + gift.getStarttime() + " " + gift.getEnttime());
        //礼包内容
        vh.tvGiftDetail.setText(gift.getContent());
        //礼包剩余数量
        vh.tvGiftCode.setText(gift.getRemain()+"");
        vh.tvCopyCode.setText("领取");
        if(type== GiftListFragment.TYPE_GIFT){
            vh.rlGiftDetail.setVisibility(View.VISIBLE);
        }else{
            vh.rlGiftDetail.setVisibility(View.GONE);
        }
        vh.tvCopyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = vh.getAdapterPosition();
                apply(adapterPosition);
            }
        });
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameDetailActivity.start(context,gift.getGameid()+"");
            }
        });
        GlideDisplay.dispalyWithNoFade(vh.ivGiftIcon,gift.getIcon());
    }
    private void apply(int position){
        HttpParams params=  AppApi.getHttpParams(true);
        params.put("giftid",datas.get(position).getGiftid()+"");
        final String url,codeName;
        if(type== GiftListFragment.TYPE_GIFT){
            url= AppApi.GIFT_ADD;
            codeName="礼包";
        }else{
            url = AppApi.CDKEY_ADD;
            codeName="激活码";
        }
        NetRequest.request(context).setParams(params).post(url,new HttpJsonCallBackDialog<ApplyGiftBean>(){
            @Override
            public void onDataSuccess(ApplyGiftBean data) {
                if(data.getData()!=null&& !TextUtils.isEmpty(data.getData().getGiftcode())){
                    GiftApplyDialogUtil.showApplyDialog(context,codeName,data.getData().getGiftcode());
                    if(advRefreshListener!=null){
                        advRefreshListener.getPageData(1);
                    }
                }else{
                    T.s(context,data.getMsg());
                }
            }
        });
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
        if(datas.isEmpty()&&type==GiftListFragment.TYPE_ACTIVATE_CODE&&
                BuildConfig.projectCode==94){//94号项目特殊定制，在首页没有获取到激活码的时候隐藏所有激活码
            EventBus.getDefault().postSticky(new HiddenActivateCodeUiEvent());
        }
        return datas.isEmpty();
    }

    static class GiftViewHolder extends RecyclerView.ViewHolder{
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
        @BindView(R.id.tv_start_time)
        TextView tvStartTime;
        @BindView(R.id.tv_copy_code)
        TextView tvCopyCode;
        @BindView(R.id.rl_gift_detail)
        RelativeLayout rlGiftDetail;

        GiftViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
