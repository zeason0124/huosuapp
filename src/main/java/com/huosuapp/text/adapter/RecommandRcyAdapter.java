package com.huosuapp.text.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.bean.BannerListBean;
import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.listener.IGameLayout;
import com.huosuapp.text.ui.GameDetailActivity;
import com.huosuapp.text.ui.GameListActivity;
import com.huosuapp.text.ui.WebViewActivity;
import com.huosuapp.text.ui.fragment.RecommandFragment;
import com.huosuapp.text.view.CusConvenientBanner;
import com.huosuapp.text.view.ListGameItem;
import com.liang530.application.BaseApplication;
import com.liang530.log.L;
import com.liang530.utils.BaseAppUtil;
import com.liang530.utils.GlideDisplay;
import com.liang530.views.convenientbanner.holder.CBViewHolderCreator;
import com.liang530.views.convenientbanner.holder.Holder;
import com.liang530.views.convenientbanner.listener.OnItemClickListener;
import com.liang530.views.grid.GridLayoutList;
import com.liang530.views.refresh.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by LiuHongLiang on 2016/9/25.
 */

public class RecommandRcyAdapter extends RecyclerView.Adapter implements IDataAdapter<List<GameBean>> {

    List<GameBean> currentHotList;
    List<BannerListBean.Banner> bannerList;
    List<GameBean> hotOutLineList;
    List<GameBean> hotLineList;
    List<GameBean> gameBeanList = new ArrayList<>();
    int deviceWidth, bannerHeight;
    public final static int TYPE_CUR_HOT = 0;
    public final static int TYPE_BANNER = 1;
    public final static int TYPE_OUT_LINE = 2;
    public final static int TYPE_LINE = 3;
    public final static int TYPE_GAME_LIST = 4;
    int currentHotSize, bannerSize, hotOutLineSize, hotLineSize;
    private RecommandFragment recommandFragment;
    private Integer bannerAdapterPosition = null;//banner在adapter中的位置
    private PtrClassicFrameLayout mPtrFrame;
    public RecommandRcyAdapter(RecommandFragment recommandFragment,PtrClassicFrameLayout mPtrFrame) {
        deviceWidth = BaseAppUtil.getDeviceWidth(BaseApplication.getInstance());
        bannerHeight = (int) ((deviceWidth-BaseAppUtil.dip2px(BaseApplication.getInstance(),12)*2) * 95. / 375);
        this.recommandFragment = recommandFragment;
        this.mPtrFrame=mPtrFrame;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case TYPE_CUR_HOT:
                view = layoutInflater.inflate(R.layout.adapter_rec_gridlyout, parent, false);
                return new GridLayoutViewHolder(view);
            case TYPE_BANNER:
                view = layoutInflater.inflate(R.layout.adapter_common_banner, parent, false);
                BannerViewHolder bannerViewHolder = new BannerViewHolder(view);
                ViewGroup.LayoutParams bannerLayoutParams = bannerViewHolder.convenientBanner.getLayoutParams();//设置banner高度
                if (bannerLayoutParams == null) {
                    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bannerHeight));
                } else {
                    bannerLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    bannerLayoutParams.height = bannerHeight;
                }
                bannerViewHolder.convenientBanner.setParentView(mPtrFrame);
                return bannerViewHolder;
            case TYPE_OUT_LINE:
                view = layoutInflater.inflate(R.layout.adapter_rec_grid, parent, false);
                return new OutLineGridViewHolder(view);
            case TYPE_LINE:
                view = layoutInflater.inflate(R.layout.adapter_rec_grid, parent, false);
                return new LineGridViewHolder(view);
            case TYPE_GAME_LIST:
                view = new ListGameItem(parent.getContext());
                view.setBackgroundResource(R.color.bg_common);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    view.setLayoutParams(layoutParams);
                } else {
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
                return new GameListViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GameListViewHolder) {
            int realPosition = position - currentHotSize - hotLineSize - bannerSize - hotOutLineSize;
            final GameBean gameBean = gameBeanList.get(realPosition);
            gameBean.setItemPosition(realPosition);//设置在列表中的位置
            GameListViewHolder gameListViewHolder = (GameListViewHolder) holder;
            if (gameListViewHolder.itemView instanceof IGameLayout) {
                IGameLayout iGameLayout = (IGameLayout) gameListViewHolder.itemView;
                iGameLayout.setGameBean(gameBean);
                L.e("hongliang","gameListView onbind "+gameBean.getGamename());
            }
            gameListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameDetailActivity.start(v.getContext(), gameBean.getGameid());
                }
            });
        } else if (holder instanceof GridLayoutViewHolder) {
            GridLayoutViewHolder gridLayoutViewHolder = (GridLayoutViewHolder) holder;
            gridLayoutViewHolder.gdlList.setColumnCount(1);
            gridLayoutViewHolder.ivHotPhoto.setBackgroundResource(R.mipmap.shixiaremen);
            gridLayoutViewHolder.gdlList.setAdapter(new GameListAdapter(currentHotList));
            L.e("hongliang","时下热门 onbind ");
            gridLayoutViewHolder.igvRefresh.setOnClickListener(new RefreshHeadDataClickListener(TYPE_CUR_HOT));
            gridLayoutViewHolder.ivHotPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameListActivity.start(v.getContext(), "时下热门", null, null, 1, null);
                }
            });
        } else if (holder instanceof LineGridViewHolder) {
            LineGridViewHolder lineGridViewHolder = (LineGridViewHolder) holder;
            lineGridViewHolder.ivHotPhoto.setBackgroundResource(R.mipmap.remenwangyou);
            lineGridViewHolder.recyclerView.setAdapter(new GameGridAdapter(hotLineList));
            L.e("hongliang","热门网游 onbind ");
            lineGridViewHolder.igvRefresh.setOnClickListener(new RefreshHeadDataClickListener(TYPE_LINE));
            lineGridViewHolder.ivHotPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameListActivity.start(v.getContext(), "热门网游", null, 2, 1, null);
                }
            });
        } else if (holder instanceof OutLineGridViewHolder) {
            OutLineGridViewHolder outLineGridViewHolder = (OutLineGridViewHolder) holder;
            String classifyName="热门单机";
            if (BuildConfig.projectCode == 49) {//49宴门定制图标
                outLineGridViewHolder.ivHotPhoto.setBackgroundResource(R.mipmap.danjiapp);
                classifyName="单机应用";
            } else if(BuildConfig.projectCode==118){
                outLineGridViewHolder.ivHotPhoto.setBackgroundResource(R.mipmap.remenbt);
                classifyName="BT游戏";
            } else {
                outLineGridViewHolder.ivHotPhoto.setBackgroundResource(R.mipmap.remendanji);
                classifyName="热门单机";
            }
            outLineGridViewHolder.recyclerView.setAdapter(new GameGridAdapter(hotOutLineList));
            L.e("hongliang","热门单机 onbind ");
            outLineGridViewHolder.igvRefresh.setOnClickListener(new RefreshHeadDataClickListener(TYPE_OUT_LINE));
            final String finalClassifyName = classifyName;
            outLineGridViewHolder.ivHotPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameListActivity.start(v.getContext(), finalClassifyName, null, 1, 1, null);
                }
            });
        } else if (holder instanceof BannerViewHolder) {
            BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
            bannerViewHolder.convenientBanner.setPages(new CBViewHolderCreator<NetImageHolderView>() {
                @Override
                public NetImageHolderView createHolder() {
                    return new NetImageHolderView();
                }
            }, bannerList).setPageIndicator(new int[]{R.mipmap.dot_normal, R.mipmap.dot_focus})
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            BannerListBean.Banner banner = bannerList.get(position);
                            if (!TextUtils.isEmpty(banner.getGameid()) && !"0".equals(banner.getGameid())) {
                                GameDetailActivity.start(recommandFragment.getContext(), banner.getGameid());
                            } else {
                                WebViewActivity.start(recommandFragment.getContext(), null, banner.getUrl());
                            }
                        }
                    });
            if (!bannerViewHolder.convenientBanner.isTurning()) {
                if (bannerList.size() > 1) {
                    bannerViewHolder.convenientBanner.startTurning(3000);
                } else {
                    bannerViewHolder.convenientBanner.stopTurning();
                }
            }
            bannerAdapterPosition = bannerViewHolder.getAdapterPosition();

        }
    }

    public class RefreshHeadDataClickListener implements View.OnClickListener {
        private int type;

        public RefreshHeadDataClickListener(int type) {
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            recommandFragment.getHeadGameData(type);
        }
    }

    public class NetImageHolderView implements Holder<BannerListBean.Banner> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, BannerListBean.Banner banner) {
            GlideDisplay.displayDefaulNoPlaceHolder(imageView, banner.getImage());//不要占位图
//            GlideDisplay.displayDefaul();
        }
    }

    /**
     * 获得banner在adapter中的位置
     *
     * @return
     */
    public Integer getBannerAdapterPosition() {
        if (bannerList == null) return null;
        return bannerAdapterPosition;
    }

    @Override
    public int getItemCount() {
        currentHotSize = (currentHotList == null ? 0 : 1);
        bannerSize = (bannerList == null || bannerList.isEmpty() ? 0 : 1);
        hotOutLineSize = (hotOutLineList == null ? 0 : 1);
        hotLineSize = (hotLineList == null ? 0 : 1);
        return currentHotSize + bannerSize + hotLineSize + hotOutLineSize + gameBeanList.size();
    }

    @Override
    public int getItemViewType(int position) {
        currentHotSize = (currentHotList == null ? 0 : 1);
        if (position < currentHotSize) {
            return TYPE_CUR_HOT;
        }
        bannerSize = (bannerList == null || bannerList.isEmpty() ? 0 : 1);
        if (position < currentHotSize + bannerSize) {
            return TYPE_BANNER;
        }
        hotOutLineSize = (hotOutLineList == null ? 0 : 1);
        if (position < currentHotSize + bannerSize + hotOutLineSize) {
            return TYPE_OUT_LINE;
        }
        hotLineSize = (hotLineList == null ? 0 : 1);
        if (position < currentHotSize + bannerSize + hotOutLineSize + hotLineSize) {
            return TYPE_LINE;
        }
        return TYPE_GAME_LIST;
    }

    public void notifyDataChanged(List dataList, int type) {
        switch (type) {
            case TYPE_CUR_HOT:
                this.currentHotList = dataList;
                break;
            case TYPE_BANNER:
                this.bannerList = dataList;
                break;
            case TYPE_OUT_LINE:
                this.hotOutLineList = dataList;
                break;
            case TYPE_LINE:
                this.hotLineList = dataList;
                break;
        }
        if(recommandFragment!=null){
            //更新当前显示中的数据，没有显示的数据会再次执行onbind时更新数据
            LinearLayoutManager layoutManager = (LinearLayoutManager) recommandFragment.getRecyclerView().getLayoutManager();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if(firstVisibleItemPosition>=0&&lastVisibleItemPosition-firstVisibleItemPosition+1>0){
                for(;firstVisibleItemPosition<=lastVisibleItemPosition;firstVisibleItemPosition++){
                    RecyclerView.ViewHolder viewHolder = recommandFragment.getRecyclerView().findViewHolderForAdapterPosition(firstVisibleItemPosition);
                    if(type==TYPE_CUR_HOT&&viewHolder instanceof GridLayoutViewHolder){//时下热门
                        notifyItemChanged(firstVisibleItemPosition);
                        break;
                    }else if(type==TYPE_BANNER&&viewHolder instanceof BannerViewHolder){
                        notifyItemChanged(firstVisibleItemPosition);
                        break;
                    }else if(type==TYPE_OUT_LINE&&viewHolder instanceof OutLineGridViewHolder){
                        notifyItemChanged(firstVisibleItemPosition);
                        break;
                    }else if(type==TYPE_LINE&&viewHolder instanceof LineGridViewHolder){
                        notifyItemChanged(firstVisibleItemPosition);
                        break;
                    }
                }
                if(firstVisibleItemPosition>lastVisibleItemPosition){//没有在当前显示的列表中，需要添加
                    notifyDataSetChanged();
                }
            }
        }
    }
    @Override
    public void notifyDataChanged(List<GameBean> gameBeen, boolean isRefresh) {
        if (isRefresh) {
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
        int size = 0;
        if (currentHotList != null) {
            size++;
        }
        if (bannerList != null && !bannerList.isEmpty()) {
            size++;
        }
        if (hotOutLineList != null) {
            size++;
        }
        if (hotLineList != null) {
            size++;
        }
        if (gameBeanList != null && gameBeanList.size() > 0) {
            size++;
        }
        return size == 0;
    }

    static class GridLayoutViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_hot_photo)
        ImageView ivHotPhoto;
        @BindView(R.id.igv_refresh)
        ImageView igvRefresh;
        @BindView(R.id.gdl_list)
        GridLayoutList gdlList;

        GridLayoutViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class OutLineGridViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_hot_photo)
        ImageView ivHotPhoto;
        @BindView(R.id.igv_refresh)
        ImageView igvRefresh;
        @BindView(R.id.rec_game_grid)
        RecyclerView recyclerView;

        OutLineGridViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        }
    }

    static class LineGridViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_hot_photo)
        ImageView ivHotPhoto;
        @BindView(R.id.igv_refresh)
        ImageView igvRefresh;
        @BindView(R.id.rec_game_grid)
        RecyclerView recyclerView;

        LineGridViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        }
    }

    static class GameListViewHolder extends RecyclerView.ViewHolder {
        GameListViewHolder(View view) {
            super(view);
        }
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.convenientBanner)
        public CusConvenientBanner convenientBanner;

        BannerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
