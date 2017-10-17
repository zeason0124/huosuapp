package com.huosuapp.text.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.liang530.utils.BaseTextUtil;
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
 * Created by liu hong liang on 2016/9/30.
 */

public class DetailProgessLayoutView extends FrameLayout implements IGameLayout {
    private static final String TAG = DetailProgessLayoutView.class.getSimpleName();
    @BindView(R.id.pb_download)
    ProgressBar pbDownload;
    @BindView(R.id.tv_download_tip)
    TextView tvDownloadTip;
    @BindView(R.id.iv_download)
    ImageView ivDownload;
    private GameBean gameBean;
    private TextView tvDownloadCount;
    private TasksManagerModel tasksManagerModel;

    public DetailProgessLayoutView(Context context) {
        super(context);
        initUI();
    }

    public DetailProgessLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public DetailProgessLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(getContext()).inflate(R.layout.include_detail_progress_layout, this, true);
        ButterKnife.bind(this);
    }

    public void setGameBean(GameBean gameBean, TextView tvDownloadCount) {
        this.tvDownloadCount = tvDownloadCount;
        String downCnt = gameBean.getDowncnt();
        if(downCnt.length()>=5){
            downCnt = (Integer.parseInt(downCnt)/10000)+"万";
        }
        tvDownloadCount.setText("下载 : " + downCnt + "次");
        pbDownload.setProgress(0);
        pbDownload.setMax(100);
        setGameBean(gameBean);
        restoreDownloadProgressListener();
    }

    private void restoreDownloadProgressListener() {
        if(tasksManagerModel!=null){//没有下载过，不用恢复
            byte status = FileDownloader.getImpl().getStatus(tasksManagerModel.getUrl(), tasksManagerModel.getPath());
            L.d(TAG,tasksManagerModel.getId()+" 恢复下载进度："+"  进度："+TasksManager.getImpl().getTotal(tasksManagerModel.getId())+"--> "+TasksManager.getImpl().getSoFar(tasksManagerModel.getId()));
            pbDownload.setProgress(TasksManager.getImpl().getProgress(tasksManagerModel.getId()));
            switch (status){
                case FileDownloadStatus.pending://等待
                case FileDownloadStatus.started://下载中
                case FileDownloadStatus.connected://下载中
                case FileDownloadStatus.progress://下载中
                case FileDownloadStatus.warn://已经在下载或者在队列中了，丢失下载进度和状态记录，进行重新连接
                    FileDownloader.getImpl().replaceListener(tasksManagerModel.getId(),downloadListener);
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDwonFileStatusChange(DownStatusChangeEvent change) {
        if (gameBean == null) return;
        if (gameBean.getGameid() != null && gameBean.getGameid().equals(change.gameId)) {
            updateUI(change);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN,priority = 1)//设置优先接收事件
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
        if(gameBean==null) return;
        if (change != null) {
            tvDownloadCount.setText("下载 : " + change.downcnt + " 次");
        }
        tasksManagerModel = TasksManager.getImpl().getTaskModelByGameId(gameBean.getGameid());
        //设置下载按钮
        if (tasksManagerModel == null) {//没有下载过
            String downSize = BaseTextUtil.isEmpty(gameBean.getSize())?"0":gameBean.getSize()+"";
            tvDownloadTip.setText("下载("+downSize+")");
            return;
        }
        if(tasksManagerModel.getStatus()==TasksManagerModel.STATUS_INSTALLED){//标记安装过了
            if(BaseAppUtil.isInstallApp(getContext(),tasksManagerModel.getPackageName())){//手机中还存在，可以直接启动
                tvDownloadTip.setText("启动");
            }else{//被卸载了
                File file= new File(tasksManagerModel.getPath());
                if(file.exists()){//安装包还存在，可以安装
                    //更新为未安装
                    tasksManagerModel.setStatus(0);
                    TasksManager.getImpl().updateTask(tasksManagerModel);
                    tvDownloadTip.setText("安装");
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
                tvDownloadTip.setText("等待");
                break;
            case FileDownloadStatus.INVALID_STATUS://等待
                tvDownloadTip.setText("等待");
                break;
            case FileDownloadStatus.started://下载中
                tvDownloadTip.setText("下载中("+TasksManager.getImpl().getProgress(tasksManagerModel.getId())+"%)");
                break;
            case FileDownloadStatus.connected://下载中
                tvDownloadTip.setText("下载中("+TasksManager.getImpl().getProgress(tasksManagerModel.getId())+"%)");
                break;
            case FileDownloadStatus.progress://下载中
                tvDownloadTip.setText("下载中("+TasksManager.getImpl().getProgress(tasksManagerModel.getId())+"%)");
                break;
            case FileDownloadStatus.paused://暂停
                tvDownloadTip.setText("暂停");
                break;
            case FileDownloadStatus.completed://安装
                //判断是启动还是安装
                if (BaseAppUtil.isInstallApp(BaseApplication.getInstance(), tasksManagerModel.getPackageName()) && tasksManagerModel.getStatus() == TasksManagerModel.STATUS_INSTALLED) {
                    tvDownloadTip.setText("启动");
                } else {
                    tvDownloadTip.setText("安装");
                }
                break;
            case FileDownloadStatus.blockComplete://安装
                //判断是启动还是安装
                if (BaseAppUtil.isInstallApp(BaseApplication.getInstance(), tasksManagerModel.getPackageName()) && tasksManagerModel.getStatus() == TasksManagerModel.STATUS_INSTALLED) {
                    tvDownloadTip.setText("启动");
                } else {
                    tvDownloadTip.setText("安装");
                }
                break;
            case FileDownloadStatus.retry://重试
                tvDownloadTip.setText("重试");
                break;
            case FileDownloadStatus.error://重试
                tvDownloadTip.setText("重试");
                break;
            case FileDownloadStatus.warn://已经在下载或者在队列中了，丢失下载进度和状态记录，进行重新连接
                tvDownloadTip.setText("下载中("+TasksManager.getImpl().getProgress(tasksManagerModel.getId())+"%)");
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
        if(tasksManagerModel!=null){
            //移除监听器
            FileDownloader.getImpl().replaceListener(tasksManagerModel.getId(),null);
        }
    }

    @Override
    public void setGameBean(GameBean gameBean) {
        this.gameBean = gameBean;
        updateUI(null);
    }

    @Override
    public GameBean getGameBean() {
        if (gameBean == null) {
            L.d(TAG, "error gameBeam is null!");
            return new GameBean();
        }
        return gameBean;
    }
    @OnClick({R.id.pb_download})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pb_download:
                GameViewUtil.clickDownload(tasksManagerModel, this, downloadListener);
                break;
        }
    }

    /**
     * 下载进度监听器
     */
    FileDownloadListener downloadListener=new FileDownloadSampleListener(){
        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if(pbDownload!=null){
                pbDownload.setProgress(TasksManager.getImpl().getProgress(tasksManagerModel.getId()));
                tvDownloadTip.setText("下载中("+TasksManager.getImpl().getProgress(tasksManagerModel.getId())+"%)");
            }

            L.d(TAG,"下载中："+task.getId()+"  进度："+task.getLargeFileSoFarBytes()+"--> "+task.getLargeFileSoFarBytes());
        }
    };

}
