package com.huosuapp.text.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huosuapp.text.BuildConfig;
import com.huosuapp.text.R;
import com.huosuapp.text.base.HsApplication;
import com.huosuapp.text.bean.ClassifyListBean;
import com.huosuapp.text.bean.DownStatusChangeEvent;
import com.huosuapp.text.bean.DownTaskDeleteEvent;
import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.bean.NetConnectEvent;
import com.huosuapp.text.bean.TasksManagerModel;
import com.huosuapp.text.db.TasksManager;
import com.huosuapp.text.listener.IGameLayout;
import com.huosuapp.text.util.GameViewUtil;
import com.liang530.application.BaseApplication;
import com.liang530.log.L;
import com.liang530.utils.BaseAppUtil;
import com.liang530.utils.BaseTextUtil;
import com.liang530.utils.GlideDisplay;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LiuHongLiang on 2016/9/25.
 */

public class ListGameItem extends LinearLayout implements IGameLayout {
    private static final String TAG = ListGameItem.class.getSimpleName();
    @BindView(R.id.iv_game_icon)
    ImageView ivGameIcon;
    @BindView(R.id.tv_game_name)
    TextView tvGameName;
    @BindView(R.id.tv_type_count)
    TextView tvTypeCount;
    @BindView(R.id.tv_game_desc)
    TextView tvGameDesc;
    @BindView(R.id.btn_download)
    Button btnDownload;
    TasksManagerModel tasksManagerModel;
    @BindView(R.id.ll_game_tag)
    LinearLayout llGameTag;
    private GameBean gameBean;

    public ListGameItem(Context context) {
        super(context);
        initUI();
    }

    public ListGameItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public ListGameItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(getContext()).inflate(R.layout.include_common_list_game, this, true);
        ButterKnife.bind(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDwonFileStatusChange(DownStatusChangeEvent change) {
        if (gameBean == null) return;
        if (gameBean.getGameid() != null && gameBean.getGameid().equals(change.gameId)) {
            updateUI(change);
            L.e("start","收到状态改变："+gameBean.getGamename());
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetConnectEvent(NetConnectEvent event) {
        if (gameBean == null) return;
        tasksManagerModel = TasksManager.getImpl().getTaskModelByGameId(gameBean.getGameid());
        if(tasksManagerModel==null){
            return;
        }
        byte status = FileDownloader.getImpl().getStatus(tasksManagerModel.getUrl(), tasksManagerModel.getPath());
        L.e("start","收到通知："+tasksManagerModel.getGameName());
        if(event.type==NetConnectEvent.TYPE_START){
            if(status==FileDownloadStatus.error||status==FileDownloadStatus.paused){
                L.e("start","恢复下载："+tasksManagerModel.getGameName());
                GameViewUtil.start(this,null);
            }
        }else{
            if(status==FileDownloadStatus.progress||status==FileDownloadStatus.started||status==FileDownloadStatus.connected){
                L.e("start","暂停下载："+tasksManagerModel.getGameName());
                FileDownloader.getImpl().pause(tasksManagerModel.getId());
            }
        }
    }

    private void updateUI(DownStatusChangeEvent change) {
        if (gameBean == null) return;
        if (change != null) {
            if(!TextUtils.isEmpty(change.downcnt)&&!"0".equals(change.downcnt)){//不是null不是0
                gameBean.setDowncnt(change.downcnt);
                String downloadNum = BaseTextUtil.isEmpty(gameBean.getDowncnt())?"0":gameBean.getDowncnt();
                tvTypeCount.setText(getTypeStr(gameBean.getType()) + " | " + downloadNum+ "次下载");
            }
        }
        tasksManagerModel = TasksManager.getImpl().getTaskModelByGameId(gameBean.getGameid());
        //设置下载按钮
        if (tasksManagerModel == null) {//没有下载过
            btnDownload.setText("下载");
            return;
        }

        if(tasksManagerModel.getStatus()==TasksManagerModel.STATUS_INSTALLED){//标记安装过了
            if(BaseAppUtil.isInstallApp(getContext(),tasksManagerModel.getPackageName())){//手机中还存在，可以直接启动
                btnDownload.setText("启动");
            }else{//被卸载了
                File file= new File(tasksManagerModel.getPath());
                if(file.exists()){//安装包还存在，可以安装
                    //更新为未安装
                    tasksManagerModel.setStatus(0);
                    TasksManager.getImpl().updateTask(tasksManagerModel);
                    btnDownload.setText("安装");
                }else{//安装包被删除了，删除数据库记录
                    TasksManager.getImpl().deleteTaskByModel(tasksManagerModel);
                    //通知刷新
                    EventBus.getDefault().post(new DownStatusChangeEvent(tasksManagerModel.getId(),tasksManagerModel.getGameId(),null));
                    EventBus.getDefault().post(new DownTaskDeleteEvent(tasksManagerModel));
                }
            }
            return;
        }

        byte status = FileDownloader.getImpl().getStatus(tasksManagerModel.getUrl(), tasksManagerModel.getPath());
        switch (status) {
            case FileDownloadStatus.pending://等待
                btnDownload.setText("等待");
                break;
            case FileDownloadStatus.INVALID_STATUS://等待
                btnDownload.setText("等待");
                break;
            case FileDownloadStatus.started://下载中
                btnDownload.setText("下载中");
                break;
            case FileDownloadStatus.connected://下载中
                btnDownload.setText("下载中");
                break;
            case FileDownloadStatus.progress://下载中
                btnDownload.setText("下载中");
                break;
            case FileDownloadStatus.paused://暂停
                btnDownload.setText("暂停");
                break;
            case FileDownloadStatus.completed://安装
                //判断是启动还是安装
                if (BaseAppUtil.isInstallApp(BaseApplication.getInstance(), tasksManagerModel.getPackageName()) && tasksManagerModel.getStatus() == TasksManagerModel.STATUS_INSTALLED) {
                    btnDownload.setText("启动");
                } else {
                    btnDownload.setText("安装");
                }
                break;
            case FileDownloadStatus.blockComplete://安装
                //判断是启动还是安装
                if (BaseAppUtil.isInstallApp(BaseApplication.getInstance(), tasksManagerModel.getPackageName()) && tasksManagerModel.getStatus() == TasksManagerModel.STATUS_INSTALLED) {
                    btnDownload.setText("启动");
                } else {
                    btnDownload.setText("安装");
                }
                break;
            case FileDownloadStatus.retry://重试
                btnDownload.setText("重试");
                break;
            case FileDownloadStatus.error://重试
                btnDownload.setText("重试");
                break;

            case FileDownloadStatus.warn://已经在下载或者在队列中了，丢失下载进度和状态记录，进行重新连接
                btnDownload.setText("下载中");
                break;
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
        //重新显示到窗口，更新ui
        updateUI(null);
        L.d(TAG, "EventBus register");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
            L.d(TAG, "EventBus unregister");
        }
    }

    @Override
    public void setGameBean(GameBean gameBean) {
        this.gameBean = gameBean;
        if (gameBean == null) return;
        updateUI(null);
        //设置图片
        GlideDisplay.display(ivGameIcon, gameBean.getIcon());
        tvGameName.setText(gameBean.getGamename());
        tvGameDesc.setText(gameBean.getOneword());
        //类型和下载次数
        tvTypeCount.setBackgroundResource(getTypeCountResourceId(gameBean.getItemPosition()));
        String downloadNum = BaseTextUtil.isEmpty(gameBean.getDowncnt())?"0":gameBean.getDowncnt();
        tvTypeCount.setText(getTypeStr(gameBean.getType()) + " | " + downloadNum + "次下载");
//        if (BuildConfig.projectCode == 99||BuildConfig.projectCode==91) {//99和91礼包和折扣标签
//            setGameTag();
//        }
        setGameTag();
    }

    public void setGameTag() {
        llGameTag.setVisibility(VISIBLE);
        llGameTag.removeAllViews();
        if ("2".equals(gameBean.getDistype())) {//有返利
            int rate = 0;
            try {
                String rebate = gameBean.getRebate();
                rate = (int) (Float.parseFloat(rebate) * 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(rate!=0){
                TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.add_list_game_tag, llGameTag, false);
                if(BuildConfig.projectCode==147){ //147-返利折扣标签不同背景
                    textView.setBackgroundResource(R.mipmap.bg_fanli);
                }else{
                    textView.setBackgroundResource(R.drawable.shape_circle_rect_yellow);
                }
                if(BuildConfig.projectCode==137){//137
                    textView.setText("赠送" + rate + "%");
                }else if(BuildConfig.projectCode==147){//147-
                    textView.setText("  返利" + rate + "%");
                }else{
                    textView.setText("返利" + rate + "%");
                }
                llGameTag.addView(textView);
            }else if(BuildConfig.projectCode==147){//147-
                TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.add_list_game_tag, llGameTag, false);
                textView.setBackgroundResource(R.mipmap.bg_fanli);
                textView.setText("  返利");
                llGameTag.addView(textView);
            }
        }
        if ("1".equals(gameBean.getDistype())) {//有折扣
            int rate = 0;
            try {
                String rebate = gameBean.getDiscount();
                rate = (int) (Float.parseFloat(rebate) * 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(rate!=0){
                TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.add_list_game_tag, llGameTag, false);
                if(BuildConfig.projectCode==147){ //147- 返利折扣标签不同背景
                    textView.setBackgroundResource(R.mipmap.bg_fanli);
                }else{
                    textView.setBackgroundResource(R.drawable.shape_circle_rect_yellow);
                }
                if(BuildConfig.projectCode==147) {//147-
                    textView.setText("  "+(rate/10f)+"折");
                }else if(BuildConfig.projectCode==105){//105，首充续充
                    int firstDiscount = (int)(gameBean.getFirst_discount()*100);
                    textView.setMaxWidth(Integer.MAX_VALUE);
                    textView.setText("首充"+(firstDiscount/10f)+"折,续充"+(rate/10f)+"折");
                }else {
                    textView.setText("折扣" + rate + "%");
                }
                llGameTag.addView(textView);
            }else if(BuildConfig.projectCode==147){//147-
                TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.add_list_game_tag, llGameTag, false);
                textView.setBackgroundResource(R.mipmap.bg_fanli);
                textView.setText("  折扣");
                llGameTag.addView(textView);
            }
        }

        if (!"0".equals(gameBean.getGiftcnt())&&!TextUtils.isEmpty(gameBean.getGiftcnt())) {//有礼包
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.add_list_game_tag, llGameTag, false);
            textView.setBackgroundResource(R.drawable.shape_circle_rect_blue);
            textView.setText("礼包");
            llGameTag.addView(textView);
        }
        if (!"0".equals(gameBean.getNewscnt())&&!TextUtils.isEmpty(gameBean.getNewscnt())) {//有攻略
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.add_list_game_tag, llGameTag, false);
            textView.setBackgroundResource(R.drawable.shape_circle_rect_green);
            textView.setText("攻略");
            llGameTag.addView(textView);
        }
    }

    public int getTypeCountResourceId(int itemPosition) {
        return itemPosition%2 == 0 ? R.drawable.shape_game_type_circle_rect_red : R.drawable.shape_game_type_circle_rect_blue;
    }

    public static String getTypeStr(String typeStr) {
        HsApplication instance = (HsApplication) HsApplication.getInstance();
        StringBuffer textContent = new StringBuffer();
        String content = "";
        if(TextUtils.isEmpty(typeStr)){
            return "其它";
        }
        List<ClassifyListBean.ClassifyBean> classifyList = instance.getClassifyList();
        if (classifyList!= null && classifyList.size() > 0) {
            String[] typeIndexs = typeStr.split(",");
            for (int i = 0; i < typeIndexs.length && i<3; i++) {//取三个
                try {
                    int index = Integer.parseInt(typeIndexs[i]);
                    for(int k=0;k<classifyList.size();k++){
                        if(classifyList.get(k).getTypeid()==index){
                            textContent.append(classifyList.get(k).getTypename()+"|");
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            content=textContent.toString().trim();

        } else {
            String[] typeArr = {"", "角色", "格斗", "休闲", "竞速", "策略", "射击", "其它"};

            String[] typeIndexs = typeStr.split(",");
            try {
                for (int i = 0; i < typeIndexs.length && i<3; i++) {
                    int index = Integer.parseInt(typeIndexs[i]);
                    textContent.append(typeArr[index] + "|");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            content = textContent.toString().trim();
        }
        if (content.endsWith("|")) {
            content = content.substring(0, content.length() - 1);
        }
        if (TextUtils.isEmpty(content)) {
            content = "其它";
        }
        return content;
    }

    @Override
    public GameBean getGameBean() {
        if (gameBean == null) {
            L.d(TAG, "error gameBeam is null!");
            return new GameBean();
        }
        return gameBean;
    }


    @OnClick({R.id.btn_download})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                GameViewUtil.clickDownload(tasksManagerModel, this, null);
                break;
        }
    }
}
