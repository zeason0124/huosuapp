package com.huosuapp.text.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.bean.UserCenterOptionBean;
import com.huosuapp.text.http.AppApi;
import com.huosuapp.text.pay.ChargeActivityForWap;
import com.huosuapp.text.ui.UserGiftActivity;
import com.huosuapp.text.ui.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liu hong liang on 2016/10/12.
 */

public class UserCenterOptionAdapter extends RecyclerView.Adapter<UserCenterOptionAdapter.ViewHolder> {
    private List<UserCenterOptionBean> datas=new ArrayList<>();
    private Context mContext;
    public UserCenterOptionAdapter(Context context) {
        this.mContext=context;
        datas.add(new UserCenterOptionBean("礼包",R.mipmap.libao));
        //2016-11-3 老周说要去掉个人中心的充值，在游戏详情页加入充值
        //无特殊需求则不要充值返利等
//        定制需求
//        if(BuildConfig.projectCode!=104
//                && BuildConfig.projectCode!=91
//                && BuildConfig.projectCode!=99
//                && BuildConfig.projectCode!=49){
//            datas.add(new UserCenterOptionBean(context.getString(R.string.recharge_name),R.mipmap.chongzhi));
//        }
        datas.add(new UserCenterOptionBean("客服",R.mipmap.kefu));
        datas.add(new UserCenterOptionBean("密码修改",R.mipmap.xiugai_mima));
        datas.add(new UserCenterOptionBean("密保邮箱",R.mipmap.mibao_youxiang));
        datas.add(new UserCenterOptionBean("密保手机",R.mipmap.mibao_phone));
    }

    @Override
    public UserCenterOptionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_option_user_center, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserCenterOptionAdapter.ViewHolder holder, int position) {
        holder.optionImg.setImageResource(datas.get(position).getImageId());
        holder.optionName.setText(datas.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (datas.get(holder.getAdapterPosition()).getImageId()){
                    case R.mipmap.chongzhi: // 点击充值到充值界面
                        ChargeActivityForWap.start(mContext, AppApi.CHARGE_URL, "支付充值");
                        break;
                    case R.mipmap.libao: // 点击到礼包界面
                        UserGiftActivity.start(mContext);
                        break;
                    case R.mipmap.kefu: // 进入到客服界面
                        WebViewActivity.start(mContext, "客服中心", AppApi.HELP_INDEX);
                        break;
                    case R.mipmap.xiugai_mima: // 进入到密码修改界面
                        WebViewActivity.start(mContext, "密码修改", AppApi.UPDATE_PWD);
                        break;
                    case R.mipmap.mibao_youxiang: // 进入到密保邮箱界面
                        WebViewActivity.start(mContext, "密保邮箱", AppApi.SECURITY_EMAIL);
                        break;
                    case R.mipmap.mibao_phone: // 进入到密保手机
                        WebViewActivity.start(mContext, "密保手机", AppApi.SECURITY_MOBILE);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.option_img)
        ImageView optionImg;
        @BindView(R.id.option_name)
        TextView optionName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
