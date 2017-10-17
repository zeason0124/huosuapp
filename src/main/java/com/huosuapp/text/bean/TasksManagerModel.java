package com.huosuapp.text.bean;

import android.content.ContentValues;

/**
 * 任务下载模型
 */
public class TasksManagerModel  {
    public final static String ID = "id";//id由FileDownLoader生成
    public final static String GAME_SIZE = "gameSize";//游戏大小
    public final static String URL = "url";
    public final static String PATH = "path";
    public final static String GAME_ID="gameId";//游戏id
    public final static String GAME_NAME="gameName";//游戏名字
    public final static String GAME_ICON = "gameIcon";//游戏icon
    public final static String PACKAGE_NAME = "packageName";//包名
    public final static String STATUS = "status";//下载状态，8：安装成功



    public static final int STATUS_INSTALLED=8;//安装成功

    private int id;
    private String gameSize;
    private String url;
    private String path;
    private String gameId;
    private String gameName;
    private String gameIcon;
    private String packageName;
    private int status;//下载状态

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGameSize() {
        return gameSize;
    }

    public void setGameSize(String gameSize) {
        this.gameSize = gameSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameIcon() {
        return gameIcon;
    }

    public void setGameIcon(String gameIcon) {
        this.gameIcon = gameIcon;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(GAME_SIZE,gameSize);
        cv.put(URL, url);
        cv.put(PATH, path);
        cv.put(GAME_ID, gameId);
        cv.put(GAME_NAME, gameName);
        cv.put(GAME_ICON, gameIcon);
        cv.put(PACKAGE_NAME, packageName);
        cv.put(STATUS, status);
        return cv;
    }
}