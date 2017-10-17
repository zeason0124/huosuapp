package com.huosuapp.text.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.huosuapp.text.bean.TasksManagerModel;
import com.liang530.application.BaseApplication;
import com.liang530.log.L;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.util.ArrayList;
import java.util.List;

public class TasksManagerDBController {
    public final static String TABLE_NAME = "tasksmanger";
    private final SQLiteDatabase db;

    public TasksManagerDBController() {
        TasksManagerDBOpenHelper openHelper = new TasksManagerDBOpenHelper(BaseApplication.getInstance());
        db = openHelper.getWritableDatabase();
    }

    /**
     * 可以从数据库中获得点击了下载的任务，再根据id到下载管理里面去判断下载状态
     * @return
     */
    public List<TasksManagerModel> getAllTasks() {
        final Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        final List<TasksManagerModel> list = new ArrayList<>();
        try {
            if (!c.moveToLast()) {
                return list;
            }
            do {
                TasksManagerModel model = new TasksManagerModel();
                model.setId(c.getInt(c.getColumnIndex(TasksManagerModel.ID)));
                model.setGameSize(c.getString(c.getColumnIndex(TasksManagerModel.GAME_SIZE)));
                model.setUrl(c.getString(c.getColumnIndex(TasksManagerModel.URL)));
                model.setPath(c.getString(c.getColumnIndex(TasksManagerModel.PATH)));
                model.setGameId(c.getString(c.getColumnIndex(TasksManagerModel.GAME_ID)));
                model.setGameName(c.getString(c.getColumnIndex(TasksManagerModel.GAME_NAME)));
                model.setGameIcon(c.getString(c.getColumnIndex(TasksManagerModel.GAME_ICON)));
                model.setPackageName(c.getString(c.getColumnIndex(TasksManagerModel.PACKAGE_NAME)));
                model.setStatus(c.getInt(c.getColumnIndex(TasksManagerModel.STATUS)));
                list.add(model);
            } while (c.moveToPrevious());
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return list;
    }
    public TasksManagerModel getTaskModelByGameId(String gameId){
        if(gameId==null) return null;
        StringBuffer sql=new StringBuffer("select * from ").append(TABLE_NAME).append(" where gameId=?");
        Cursor c = db.rawQuery(sql.toString(), new String[]{gameId});
        TasksManagerModel model =null;
        while (c.moveToNext()){
            model=new TasksManagerModel();
            model.setId(c.getInt(c.getColumnIndex(TasksManagerModel.ID)));
            model.setGameSize(c.getString(c.getColumnIndex(TasksManagerModel.GAME_SIZE)));
            model.setUrl(c.getString(c.getColumnIndex(TasksManagerModel.URL)));
            model.setPath(c.getString(c.getColumnIndex(TasksManagerModel.PATH)));
            model.setGameId(c.getString(c.getColumnIndex(TasksManagerModel.GAME_ID)));
            model.setGameName(c.getString(c.getColumnIndex(TasksManagerModel.GAME_NAME)));
            model.setGameIcon(c.getString(c.getColumnIndex(TasksManagerModel.GAME_ICON)));
            model.setPackageName(c.getString(c.getColumnIndex(TasksManagerModel.PACKAGE_NAME)));
            model.setStatus(c.getInt(c.getColumnIndex(TasksManagerModel.STATUS)));
        }
        return model;
    }
    public TasksManagerModel getTaskModelByKeyVal(String key,String value){
        if(value==null) return null;
        StringBuffer sql=new StringBuffer("select * from ")
                .append(TABLE_NAME)
                .append(" where ")
                .append(key).append("=?");
        L.d("TasksManagerDBController","getTaskModelByKeyVal-sql="+sql.toString());
        Cursor c = db.rawQuery(sql.toString(), new String[]{value});
        TasksManagerModel model =null;
        while (c.moveToNext()){
            model=new TasksManagerModel();
            model.setId(c.getInt(c.getColumnIndex(TasksManagerModel.ID)));
            model.setGameSize(c.getString(c.getColumnIndex(TasksManagerModel.GAME_SIZE)));
            model.setUrl(c.getString(c.getColumnIndex(TasksManagerModel.URL)));
            model.setPath(c.getString(c.getColumnIndex(TasksManagerModel.PATH)));
            model.setGameId(c.getString(c.getColumnIndex(TasksManagerModel.GAME_ID)));
            model.setGameName(c.getString(c.getColumnIndex(TasksManagerModel.GAME_NAME)));
            model.setGameIcon(c.getString(c.getColumnIndex(TasksManagerModel.GAME_ICON)));
            model.setPackageName(c.getString(c.getColumnIndex(TasksManagerModel.PACKAGE_NAME)));
            model.setStatus(c.getInt(c.getColumnIndex(TasksManagerModel.STATUS)));
        }
        return model;
    }

    /**
     * 添加新的下载任务
     * @param gameId
     * @param gameName
     * @param gameIcon
     * @param url
     * @param path
     * @return
     */
    public TasksManagerModel addTask( String gameId, String gameName, String gameIcon, String url,  String path) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
            return null;
        }
        // have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
        final int id = FileDownloadUtils.generateId(url, path);

        TasksManagerModel model = new TasksManagerModel();
        model.setId(id);
        model.setUrl(url);
        model.setPath(path);
        model.setGameId(gameId);
        model.setGameName(gameName);
        model.setGameIcon(gameIcon);
        model.setStatus(0);
        final boolean succeed = db.insert(TABLE_NAME, null, model.toContentValues()) != -1;
        return succeed ? model : null;
    }

    /**
     * 添加新的下载任务，保存到默认目录
     * @param gameId
     * @param gameName
     * @param gameIcon
     * @param url
     * @return
     */
    public TasksManagerModel addTask( String gameId, String gameName, String gameIcon, String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return addTask(gameId,gameName,gameIcon,url,FileDownloadUtils.getDefaultSaveFilePath(url));
    }
    public boolean updateTask(TasksManagerModel tasksManagerModel){
        String[] args={String.valueOf(tasksManagerModel.getId())};
        return db.update(TABLE_NAME, tasksManagerModel.toContentValues(), "id=?", args)>0;
    }
    public boolean deleteTaskById(int id){
        String[] args={String.valueOf(id)};
        return db.delete(TABLE_NAME,"id=?",args)>0;
    }
    public boolean deleteTaskByGameId(String gameId){
        String[] args={gameId};
        return db.delete(TABLE_NAME,"gameId=?",args)>0;
    }

}