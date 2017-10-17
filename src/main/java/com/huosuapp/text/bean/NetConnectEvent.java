package com.huosuapp.text.bean;

/**
 * Created by liu hong liang on 2016/11/23.
 */

public class NetConnectEvent {
    public static final int TYPE_START=1;
    public static final int TYPE_STOP=0;
    public int type=1;

    public NetConnectEvent(int type) {
        this.type = type;
    }
}
