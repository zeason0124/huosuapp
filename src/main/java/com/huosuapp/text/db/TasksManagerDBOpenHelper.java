package com.huosuapp.text.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.game.sdk.log.L;
import com.huosuapp.text.bean.TasksManagerModel;

// ----------------------- model
public class TasksManagerDBOpenHelper extends SQLiteOpenHelper {
    public final static String DATABASE_NAME = "tasksmanager.db";
    public final static int DATABASE_VERSION = 5;

    public TasksManagerDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TasksManagerDBController.TABLE_NAME
                + String.format(
                "("
                        + "%s INTEGER PRIMARY KEY, " // id, download id
                        + "%s VARCHAR, " // name
                        + "%s VARCHAR, " // url
                        + "%s VARCHAR, " // path
                        + "%s VARCHAR UNIQUE, " // gameId
                        + "%s VARCHAR, " // gameName
                        + "%s VARCHAR, " // gameIcon
                        + "%s VARCHAR UNIQUE," // packageName
                        + "%s INTEGER " // status
                        + ")"
                , TasksManagerModel.ID
                , TasksManagerModel.GAME_SIZE
                , TasksManagerModel.URL
                , TasksManagerModel.PATH
                , TasksManagerModel.GAME_ID
                , TasksManagerModel.GAME_NAME
                , TasksManagerModel.GAME_ICON
                , TasksManagerModel.PACKAGE_NAME
                , TasksManagerModel.STATUS

        ));
        //创建索引
        StringBuffer buffer=new StringBuffer("CREATE UNIQUE INDEX IF NOT EXISTS gameIdIndex on ")
                .append(TasksManagerDBController.TABLE_NAME).append("(").append(TasksManagerModel.GAME_ID).append(")");
        db.execSQL(buffer.toString());

        buffer=new StringBuffer("CREATE UNIQUE INDEX IF NOT EXISTS pakageNameIndex on ")
                .append(TasksManagerDBController.TABLE_NAME).append("(").append(TasksManagerModel.PACKAGE_NAME).append(")");
        db.execSQL(buffer.toString());
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion!=oldVersion){
            db.execSQL("DROP TABLE IF  EXISTS "+TasksManagerDBController.TABLE_NAME);
            L.e("hongliang","删除了旧的数据库:");
            onCreate(db);
        }
    }
}