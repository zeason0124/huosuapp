package com.huosuapp.text.bean;

import com.huosuapp.text.db.TasksManager;

import com.liang530.log.L;

/**
 * Created by liu hong liang on 2016/9/14.
 */
public class DownStatusChangeEvent {
    public Integer id;
    public String gameId;
    public String downcnt;
    public DownStatusChangeEvent(Integer id, String gamenId, String downcnt) {
        if(id!=null&&gamenId==null){
            TasksManagerModel taskModelById = TasksManager.getImpl().getTaskModelById(id);
            if(taskModelById==null){
                L.d("DownStatusChangeEvent","error=no get TasksManagerModel by id="+id);
            }else{
                gamenId=taskModelById.getGameId();
            }
        }else if(id==null&&gamenId!=null){
            TasksManagerModel taskModelByGameId = TasksManager.getImpl().getTaskModelByGameId(gamenId);
            if(taskModelByGameId==null){
                L.d("DownStatusChangeEvent","error=no get TasksManagerModel by gamenId="+gamenId);
            }else{
                id=taskModelByGameId.getId();
            }
        }else if(id==null&&gamenId==null){
            L.d("DownStatusChangeEvent","error=no id  no gameId");
        }
        this.id=id;
        this.gameId=gamenId;
        this.downcnt = downcnt;
    }
}
