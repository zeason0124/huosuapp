package com.huosuapp.text.db;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.huosuapp.text.bean.TasksManagerModel;
import com.huosuapp.text.listener.GlobalMonitor;
import com.liang530.log.L;
import com.liang530.utils.BaseAppUtil;
import com.liang530.utils.BaseFileUtil;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloadMonitor;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.List;

public class TasksManager {
    private static final String TAG = TasksManager.class.getSimpleName();
    private final static class HolderClass {
        private final static TasksManager INSTANCE = new TasksManager();
    }
    private static SparseArray<String> statusMap=new SparseArray();
    static {
        statusMap.put(FileDownloadStatus.INVALID_STATUS,"失效");
        statusMap.put(FileDownloadStatus.pending,"等待");
        statusMap.put(FileDownloadStatus.connected,"下载中");
        statusMap.put(FileDownloadStatus.progress,"下载中");
        statusMap.put(FileDownloadStatus.started,"下载中");
        statusMap.put(FileDownloadStatus.blockComplete,"安装");
        statusMap.put(FileDownloadStatus.retry,"重试");
        statusMap.put(FileDownloadStatus.completed,"安装");
        statusMap.put(FileDownloadStatus.paused,"暂停");
        statusMap.put(FileDownloadStatus.error,"错误");
        statusMap.put(FileDownloadStatus.warn,"警告");
        statusMap.put(TasksManagerModel.STATUS_INSTALLED,"启动");
    }
    private TasksManagerDBController dbController;
    private FileDownloadConnectListener listener;
    public static TasksManager getImpl() {
        return HolderClass.INSTANCE;
    }
    private TasksManager() {
        dbController = new TasksManagerDBController();
    }

    /**
     * 初始化下载服务，不可以在application中调用，可在启动的第一个activity调用
     */
    public void init() {
        if (!FileDownloader.getImpl().isServiceConnected()) {
            FileDownloader.getImpl().bindService();
            if (listener != null) {
                FileDownloader.getImpl().removeServiceConnectListener(listener);
            }
            listener = new FileDownloadConnectListener() {
                @Override
                public void connected() {
                    L.d(TAG,"下载服务已经连接成功！" );
                }

                @Override
                public void disconnected() {
                    L.d(TAG,"下载服务解除连接！" );
                }
            };
            FileDownloader.getImpl().addServiceConnectListener(listener);
        }

        FileDownloadUtils.setDefaultSaveRootPath(BaseFileUtil.getApplicationPath(FileDownloadHelper.getAppContext(),"1tsdkDownload"));
        //注册全局的下载监听
        if(FileDownloadMonitor.getMonitor()==null){
            FileDownloadMonitor.setGlobalMonitor(GlobalMonitor.getImpl());
        }
    }

    /**
     * 根据文件下载地址返回一个下载id
     * @param url
     * @return
     */
    public int getDownloadId(String url){
        return FileDownloadUtils.generateId(url, FileDownloadUtils.getDefaultSaveFilePath(url));
    }
    /**
     * 解除服务监听
     */
    private void unregisterServiceConnectionListener() {
        FileDownloader.getImpl().removeServiceConnectListener(listener);
        listener = null;
    }

    /**
     * 下载服务是否已经连接上，只有连接上了，才能获取下载过的文件状态和下载信息
     * @return
     */
    public boolean isReady() {
        return FileDownloader.getImpl().isServiceConnected();
    }

    /**
     * 判断是否下载完成
     * @param status
     * @return
     */
    public boolean isDownloaded( int status) {
        return status == FileDownloadStatus.completed;
    }

    /**
     * 是否是下载成功，并且安装了
     * @param context
     * @param gameId
     * @return
     */
    public boolean isDownOkAndInstalled(Context context,String gameId){
        TasksManagerModel taskModelByGame= dbController.getTaskModelByGameId(gameId);
        int id = taskModelByGame.getId();
        byte status = FileDownloader.getImpl().getStatus(id, taskModelByGame.getPath());
        if(status==FileDownloadStatus.completed){
            if(BaseAppUtil.isInstallApp(context,taskModelByGame.getPackageName())){
                return true;
            }
        }
        return false;
    }
    public TasksManagerModel getTaskModelByKeyVal(String key,String value){
        return dbController.getTaskModelByKeyVal(key,value);
    }
    public TasksManagerModel getTaskModelById(int id){
        return dbController.getTaskModelByKeyVal(TasksManagerModel.ID,id+"");
    }
    public TasksManagerModel addTask( String gameId, String gameName, String gameIcon, String url){
        try {
            return dbController.addTask(gameId,gameName,gameIcon,url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public TasksManagerModel getTaskModelByGameId(String gameId){
        return dbController.getTaskModelByGameId(gameId);
    }
    public List<TasksManagerModel> getAllTasks() {
        return dbController.getAllTasks();
    }
    public boolean updateTask(TasksManagerModel tasksManagerModel){
        try {
            return dbController.updateTask(tasksManagerModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTaskByModel(TasksManagerModel tasksManagerModel){
        FileDownloader.getImpl().clear(tasksManagerModel.getId(),tasksManagerModel.getPath());
        return dbController.deleteTaskById(tasksManagerModel.getId());
    }
    public boolean deleteDbTaskaById(int id){
        String[] args={String.valueOf(id)};
        return dbController.deleteTaskById(id);
    }
    public boolean deleteDbTaskByGameId(String gameId){
        return dbController.deleteTaskByGameId(gameId);
    }

    public int getStatus(final int id, String path) {
        return FileDownloader.getImpl().getStatus(id, path);
    }
    public long getTotal(final int id) {
        long total = FileDownloader.getImpl().getTotal(id);
        if(total==0){
            TasksManagerModel taskModelById = TasksManager.getImpl().getTaskModelById(id);
            if(taskModelById!=null&&taskModelById.getGameSize()!=null){
                try {
                    return Long.parseLong(taskModelById.getGameSize());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }else if(taskModelById!=null){
                File file = new File(taskModelById.getPath());
                if(file.exists()&&file.isFile()){
                    return file.length();
                }
            }
        }
        return total;
    }
    public int getProgress(final int id) {
        long total = FileDownloader.getImpl().getTotal(id);
        long soFar = FileDownloader.getImpl().getSoFar(id);
        if(total<=0) return 0;
        return (int)(soFar*100./total);
    }
    public long getSoFar(final int id) {
        return FileDownloader.getImpl().getSoFar(id);
//        BaseDownloadTask.IRunningTask task = FileDownloadList.getImpl().get(id);
//        if (task == null) {
//            return FileDownloadServiceProxy.getImpl().getSofar(id);
//        }
//
//        return task.getOrigin().getSmallFileSoFarBytes();
    }
    public String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return FileDownloadUtils.getDefaultSaveFilePath(url);
    }
}