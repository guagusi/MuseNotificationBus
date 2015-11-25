package com.guagusi.temple.musenotificationbus;

import java.util.LinkedHashMap;

/**
 * Created by liangwenhua on 15/11/13.
 *
 * 通知事件。
 */
public class MuseNotification {
    private long id;
    // 优先级
    private int mPriority;
    // 订阅主题
    private String mName;
    // 传输数据
    private LinkedHashMap mMap;

    public MuseNotification(int priority, String name, LinkedHashMap map) {
        if(name != null && name.length() > 0) {
            id = System.currentTimeMillis();
            mPriority = priority;
            mName = name;
            mMap = map;
        }
    }

    public MuseNotification(String name, LinkedHashMap map) {
        if(name != null && name.length() > 0) {
            id = System.currentTimeMillis();
            mPriority = 0;
            mName = name;
            mMap = map;
        }
    }

    public LinkedHashMap getMap() {
        return mMap;
    }

    public String getName() {
        return mName;
    }

    public int getPriority() {
        return mPriority;
    }
}
