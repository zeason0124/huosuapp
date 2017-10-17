package com.huosuapp.text.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.huosuapp.text.R;
import com.huosuapp.text.adapter.DownLoadListAdapter;
import com.huosuapp.text.base.AutoLazyFragment;
import com.huosuapp.text.bean.DownInstallSuccessEvent;
import com.huosuapp.text.bean.DownTaskDeleteEvent;
import com.huosuapp.text.bean.TasksManagerModel;
import com.huosuapp.text.db.TasksManager;
import com.liang530.utils.BaseAppUtil;
import com.liang530.views.refresh.mvc.AdvRefreshListener;
import com.liang530.views.refresh.mvc.BaseRefreshLayout;
import com.liang530.views.refresh.mvc.UltraRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;

/**
 * Created by LiuHongLiang on 2016/10/5.
 */

public class DownloadListFragment extends AutoLazyFragment implements AdvRefreshListener {
    public final static String TYPE="type";
    public final static int TYPE_UNINSTALL=0;
    public final static int TYPE_INSTALLED=1;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    private int type;
    private BaseRefreshLayout<List<TasksManagerModel>> baseRefreshLayout;
    List<TasksManagerModel> allTasks;
    private DownLoadListAdapter downLoadListAdapter;

    public static Fragment getInstance(int type){
        DownloadListFragment downloadListFragment=new DownloadListFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(TYPE,type);
        downloadListFragment.setArguments(bundle);
        return downloadListFragment;
    }
    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_list_down);
        EventBus.getDefault().register(this);
        setupUI();

    }

    private void setupUI() {
        Bundle arguments = getArguments();
        if(arguments!=null){
            type = arguments.getInt(TYPE, TYPE_UNINSTALL);
        }
        baseRefreshLayout=new UltraRefreshLayout(ptrRefresh);
        baseRefreshLayout.setCanLoadMore(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        // 设置适配器
        downLoadListAdapter = new DownLoadListAdapter(type);
        baseRefreshLayout.setAdapter(downLoadListAdapter);
        baseRefreshLayout.setAdvRefreshListener(this);
        // 加载数据
        baseRefreshLayout.refresh();
    }
    //下载安装了  启动
    //卸载了：安装
    //卸载了，并且安装包被删除了，重新下载
    //缺失更新？

    @Override
    public void getPageData(final int requestPageNo) {
        allTasks = TasksManager.getImpl().getAllTasks();
        if(type==TYPE_INSTALLED){
            for(int i=allTasks.size()-1;i>=0;i--){
                TasksManagerModel tasksManagerModel = allTasks.get(i);
                if(tasksManagerModel.getStatus()!= TasksManagerModel.STATUS_INSTALLED){//没有安装过
                    allTasks.remove(tasksManagerModel);
                }else if(TextUtils.isEmpty(tasksManagerModel.getPackageName())
                        || !BaseAppUtil.isInstallApp(mContext,tasksManagerModel.getPackageName())){//安装过，已经被卸载了
                    allTasks.remove(tasksManagerModel);
                }
            }
        }else{
            for(int i=allTasks.size()-1;i>=0;i--){
                TasksManagerModel tasksManagerModel = allTasks.get(i);
                if(tasksManagerModel.getStatus()== TasksManagerModel.STATUS_INSTALLED&&
                        BaseAppUtil.isInstallApp(mContext,tasksManagerModel.getPackageName())){//安装过
                    allTasks.remove(tasksManagerModel);
                }
            }
        }
        baseRefreshLayout.resultLoadData(allTasks,1);
    }

    /**
     * 接收删除事件
     * @param deleteEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownTaskDeleteEvent(DownTaskDeleteEvent deleteEvent) {
        downLoadListAdapter.getData().remove(deleteEvent.tasksManagerModel);
        downLoadListAdapter.notifyDataSetChanged();
    }
    /**
     * 接收安装成功事件
     * @param installSuccessEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownInstallSuccessEvent(DownInstallSuccessEvent installSuccessEvent) {
        if(type==TYPE_UNINSTALL){//未安装列表移除
            for(TasksManagerModel tempTask:downLoadListAdapter.getData()){
                if(tempTask.getGameId()!=null&&tempTask.getGameId().equals(installSuccessEvent.tasksManagerModel.getGameId())){
                    downLoadListAdapter.getData().remove(tempTask);
                    break;
                }
            }
            downLoadListAdapter.notifyDataSetChanged();
        }else{//已安装列表添加
            downLoadListAdapter.getData().add(installSuccessEvent.tasksManagerModel);
            downLoadListAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
