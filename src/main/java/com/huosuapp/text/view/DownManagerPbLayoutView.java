package com.huosuapp.text.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huosuapp.text.R;
import com.huosuapp.text.adapter.DownLoadListAdapter;
import com.huosuapp.text.bean.DownStatusChangeEvent;
import com.huosuapp.text.bean.DownTaskDeleteEvent;
import com.huosuapp.text.bean.GameBean;
import com.huosuapp.text.bean.NetConnectEvent;
import com.huosuapp.text.bean.TasksManagerModel;
import com.huosuapp.text.db.TasksManager;
import com.huosuapp.text.listener.IGameLayout;
import com.huosuapp.text.ui.fragment.DownloadListFragment;
import com.huosuapp.text.util.GameViewUtil;
import com.liang530.application.BaseApplication;
import com.liang530.log.L;
import com.liang530.utils.BaseAppUtil;
import com.liang530.utils.BaseFileUtil;
import com.liang530.utils.GlideDisplay;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
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
 * Created by liu hong liang on 2016/10/7.
 */

public class DownManagerPbLayoutView extends FrameLayout implements IGameLayout {
    @BindView(R.id.iv_game_icon)
    ImageView ivGameIcon;
    @BindView(R.id.tv_game_name)
    TextView tvGameName;
    @BindView(R.id.btn_download)
    TextView btnDownload;
    @BindView(R.id.tv_game_size)
    TextView tvGameSize;
    @BindView(R.id.tv_net_speed)
    TextView tvNetSpeed;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.rl_down_item)
    RelativeLayout rlDownItem;
    @BindView(R.id.ll_delete)
    LinearLayout llDelete;
    private String TAG = DownManagerPbLayoutView.class.getSimpleName();
    private TasksManagerModel tasksManagerModel;
    private GameBean gameBean;
    private DownLoadListAdapter adapter;

    public DownManagerPbLayoutView(Context context) {
        super(context);
        initUI();
    }

    public DownManagerPbLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public DownManagerPbLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(getContext()).inflate(R.layout.include_download_pb_layout, this, true);
        ButterKnife.bind(this);
    }

    public void setTasksManagerModel(TasksManagerModel tasksManagerModel,int type) {
        this.tasksManagerModel = tasksManagerModel;
        updateUI(null);
        tvGameName.setText(tasksManagerModel.getGameName());
        GlideDisplay.display(ivGameIcon, tasksManagerModel.getGameIcon());
        long total = TasksManager.getImpl().getTotal(tasksManagerModel.getId());
        if (total == 0) {
            tvGameSize.setText("未知大小");
        } else {
            tvGameSize.setText(BaseFileUtil.formatFileSize(total));
        }
        progressBar.setProgress(TasksManager.getImpl().getProgress(tasksManagerModel.getId()));
        tvNetSpeed.setText("0k/s");
        restoreDownloadProgressListener();
        if(type== DownloadListFragment.TYPE_INSTALLED){//已经安装的，隐藏下载进度条和下载速度
            progressBar.setVisibility(INVISIBLE);
            tvNetSpeed.setVisibility(INVISIBLE);
        }
        llDelete.setVisibility(GONE);
    }

    private void restoreDownloadProgressListener() {
        if (tasksManagerModel != null) {//没有下载过，不用恢复
            byte status = FileDownloader.getImpl().getStatus(tasksManagerModel.getUrl(), tasksManagerModel.getPath());
            L.d(TAG, tasksManagerModel.getId() + " 恢复下载进度：" + "  进度：" + TasksManager.getImpl().getTotal(tasksManagerModel.getId()) + "--> " + TasksManager.getImpl().getSoFar(tasksManagerModel.getId()));
            if(status==FileDownloadStatus.completed){
                progressBar.setProgress(100);
            }else{
                progressBar.setProgress(TasksManager.getImpl().getProgress(tasksManagerModel.getId()));
            }
            switch (status) {
                case FileDownloadStatus.pending://等待
                case FileDownloadStatus.started://下载中
                case FileDownloadStatus.connected://下载中
                case FileDownloadStatus.progress://下载中
                case FileDownloadStatus.warn://已经在下载或者在队列中了，丢失下载进度和状态记录，进行重新连接
                    FileDownloader.getImpl().replaceListener(tasksManagerModel.getId(), downloadListener);
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDwonFileStatusChange(DownStatusChangeEvent change) {
        if (tasksManagerModel == null) return;
        if (tasksManagerModel.getGameId() != null && tasksManagerModel.getGameId().equals(change.gameId)) {
            updateUI(change);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN,priority = 1)
    public void onNetConnectEvent(NetConnectEvent event) {
        if (gameBean == null) return;
        tasksManagerModel = TasksManager.getImpl().getTaskModelByGameId(gameBean.getGameid());
        if(tasksManagerModel==null){
            return;
        }
        byte status = FileDownloader.getImpl().getStatus(tasksManagerModel.getUrl(), tasksManagerModel.getPath());
        if(event.type==NetConnectEvent.TYPE_START){
            if(status==FileDownloadStatus.error||status==FileDownloadStatus.paused){
//                GameViewUtil.clickDownload(tasksManagerModel, this, null);
                L.e("start","恢复下载："+tasksManagerModel.getGameName());
                GameViewUtil.start(this,downloadListener);
            }
        }else{
            if(status==FileDownloadStatus.progress||status==FileDownloadStatus.started||status==FileDownloadStatus.connected){
                L.e("start","暂停下载："+tasksManagerModel.getGameName());
                FileDownloader.getImpl().pause(tasksManagerModel.getId());
            }
        }
    }

    private void updateUI(DownStatusChangeEvent change) {
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
        if(status==FileDownloadStatus.completed){//下载完成后不显示速度
            tvNetSpeed.setVisibility(GONE);
        }
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
        if (tasksManagerModel != null) {
            //移除监听器
            FileDownloader.getImpl().replaceListener(tasksManagerModel.getId(), null);
        }
    }

    @Override
    public void setGameBean(GameBean gameBean) {
    }

    @Override
    public GameBean getGameBean() {
        if (gameBean == null) {
            L.d(TAG, "warm gameBeam is null!");
            gameBean = new GameBean();
            gameBean.setGameid(tasksManagerModel.getGameId());
            gameBean.setUrl(tasksManagerModel.getUrl());
            gameBean.setGamename(tasksManagerModel.getGameName());
            gameBean.setIcon(tasksManagerModel.getGameIcon());
            gameBean.setDowncnt("0");
        }
        return gameBean;
    }

    @OnClick({R.id.btn_download, R.id.ll_delete, R.id.rl_down_item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download:
                GameViewUtil.clickDownload(tasksManagerModel, this, downloadListener);
                break;
            case R.id.ll_delete://删除操作

                TasksManager.getImpl().deleteTaskByModel(tasksManagerModel);
                //通知刷新
                EventBus.getDefault().post(new DownStatusChangeEvent(tasksManagerModel.getId(),tasksManagerModel.getGameId(),null));
                EventBus.getDefault().post(new DownTaskDeleteEvent(tasksManagerModel));
                break;
            case R.id.rl_down_item:
                if (llDelete.getVisibility() == View.VISIBLE) {
                    llDelete.setVisibility(GONE);
                } else {
                    llDelete.setVisibility(VISIBLE);
                }
                break;
        }
    }

    /**
     * 下载进度监听器
     */
    FileDownloadListener downloadListener = new FileDownloadSampleListener() {
        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if (progressBar != null) {
                progressBar.setProgress(TasksManager.getImpl().getProgress(tasksManagerModel.getId()));
                tvNetSpeed.setText(task.getSpeed() + "k/s");
            }
            L.d(TAG, "下载中：" + task.getId() + "  进度：" + task.getLargeFileSoFarBytes() + "--> " + task.getLargeFileSoFarBytes());
        }
    };

    public void setAdapter(DownLoadListAdapter adapter) {
        this.adapter = adapter;
    }
}
