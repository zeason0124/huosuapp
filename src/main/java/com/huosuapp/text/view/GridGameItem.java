package com.huosuapp.text.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huosuapp.text.R;
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
import com.liang530.utils.GlideDisplay;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LiuHongLiang on 2016/9/25.
 */
public class GridGameItem extends LinearLayout implements IGameLayout {

    private static final String TAG = GridGameItem.class.getSimpleName();
    @BindView(R.id.iv_game_icon)
    ImageView ivGameIcon;
    @BindView(R.id.tv_game_name)
    TextView tvGameName;
    @BindView(R.id.btn_download)
    Button btnDownload;
    TasksManagerModel tasksManagerModel;
    private GameBean gameBean;
    public GridGameItem(Context context) {
        super(context);
        initUI();
    }

    public GridGameItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public GridGameItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(getContext()).inflate(R.layout.include_grid_game, this, true);
        ButterKnife.bind(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDwonFileStatusChange(DownStatusChangeEvent change){
        if(gameBean==null) return;
        if(gameBean.getGameid()!=null&&gameBean.getGameid().equals(change.gameId)){
            updateUI(change);
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
        if(gameBean==null) return;
        tasksManagerModel = TasksManager.getImpl().getTaskModelByGameId(gameBean.getGameid());
        //设置下载按钮
        if(tasksManagerModel==null){//没有下载过
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
        switch (status){
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
                if(BaseAppUtil.isInstallApp(BaseApplication.getInstance(),tasksManagerModel.getPackageName())&&tasksManagerModel.getStatus()==TasksManagerModel.STATUS_INSTALLED){
                    btnDownload.setText("启动");
                }else{
                    btnDownload.setText("安装");
                }
                break;
            case FileDownloadStatus.blockComplete://安装
                //判断是启动还是安装
                if(BaseAppUtil.isInstallApp(BaseApplication.getInstance(),tasksManagerModel.getPackageName())&&tasksManagerModel.getStatus()==TasksManagerModel.STATUS_INSTALLED){
                    btnDownload.setText("启动");
                }else{
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
        L.d(TAG,"EventBus register");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
            L.d(TAG,"EventBus unregister");
        }
    }


    @Override
    public void setGameBean(GameBean gameBean) {
        this.gameBean=gameBean;
        updateUI(null);
        //设置图片
        GlideDisplay.display(ivGameIcon,gameBean.getIcon());
        tvGameName.setText(gameBean.getGamename());
    }

    @Override
    public GameBean getGameBean() {
        if(gameBean==null){
            L.d(TAG,"error gameBeam is null!");
            return new GameBean();
        }
        return gameBean;
    }
    @OnClick({R.id.btn_download})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                GameViewUtil.clickDownload(tasksManagerModel,this,null);
                break;
        }
    }
}
