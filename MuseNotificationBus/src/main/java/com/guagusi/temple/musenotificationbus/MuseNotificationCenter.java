package com.guagusi.temple.musenotificationbus;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by liangwenhua on 15/11/13.
 */
public class MuseNotificationCenter {
    private static final String TAG = MuseNotificationCenter.class.getSimpleName();

    private static final int MESSAGE_TAG = 0x000212;
    public static final int ASAP_PRIORITY = 0x0000002;
    public static final int NORMAL_PRIORITY = 0x000000;
    public static final int IDLE_PRIORITY = 0x000001;

    private boolean mWorking = true;

    private static MuseNotificationCenter sMuseNotificationCenter = new MuseNotificationCenter();
    private MuseObserverQueue mObserverQueue;
    private MuseNotificationQueue mNotifications;
    private Handler mUIHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "handleMessage");
            if(msg.what == MESSAGE_TAG && msg.obj != null && msg.obj instanceof MuseObserver) {
                MuseObserver observer = (MuseObserver) msg.obj;
                observer.invokeCallback();
            }
        }
    };
    private WorkerThread mWorkerThread = new WorkerThread() {
        @Override
        public Object construct() {
            while(mWorking) {
                MuseNotification notification = mNotifications.poll();
                if(notification != null) {
                    String name = notification.getName();
                    MuseObserver observer = mObserverQueue.get(name);
                    if(observer == null) {
                        continue;
                    } else {
                        observer.setNotificationParams(notification.getMap());
                        Message msg = Message.obtain();
                        msg.what = MESSAGE_TAG;
                        msg.obj = observer;
                        mUIHandler.sendMessage(msg);
                    }
                }
            }
            return null;
        }

        @Override
        public void finished(Object value) {
            super.finished(value);
            mNotifications.clear();
            mObserverQueue.clear();
            mObserverQueue = null;
            mNotifications = null;
        }
    };

    private MuseNotificationCenter() {
        mObserverQueue = MuseObserverQueue.newInstance();
        mNotifications = MuseNotificationQueue.newInstance();
        mWorking = true;
        mWorkerThread.start();
    }

    public static MuseNotificationCenter instanceMuse() {
        return sMuseNotificationCenter;
    }

    /**
     * 注册观察者
     * @param obj 观察者 not null
     * @param name  订阅主题  not null
     * @param priority  优先级 默认 NORMAL
     * @param selector 回调方法  not null
     */
    public void registerObserver(Object obj, String name, int priority, String selector) {
        registerObserver(obj, name, priority, selector, null);
    }

    /**
     * 注册观察者
     * @param obj 观察者 not null
     * @param name  订阅主题  not null
     * @param priority  优先级 默认 NORMAL
     * @param selector 回调方法  not null
     * @param paramsMap  回调方法参数
     */
    public void registerObserver(Object obj, String name, int priority, String selector,
                                 LinkedHashMap<String, Class> paramsMap) {
        if(obj == null || name == null || name.length() < 0 || selector == null || selector.length() < 0) {
            return;
        }
        MuseObserver observer = new MuseObserver(priority, obj, name, selector, paramsMap);
        mObserverQueue.add(name, observer);
    }

    /**
     * 反注册
     * @param obj
     */
    public void unregisterObserver(Object obj) {
        if(obj != null) {
            mObserverQueue.remove(obj);
        }
    }

    /**
     * 反注册
     * @param name
     */
    public void unregisterObserver(String name) {
        if(name != null && name.length() > 0) {
            mObserverQueue.remove(name);
        }
    }

    /**
     * 发布通知
     * @param name  订阅主题
     */
    public void postNotification(String name) {
        postNotification(name, null, 0);
    }

    /**
     * 发布通知
     * @param name  订阅主题
     * @param params   参数
     */
    public void postNotification(String name, LinkedHashMap params) {
        postNotification(name, params, 0);
    }

    /**
     * 发布通知
     * @param name  订阅主题
     * @param params  参数
     * @param priority   优先级
     */
    public void postNotification(String name, LinkedHashMap params, int priority) {
        MuseNotification notification = new MuseNotification(priority, name, params);
        mNotifications.add(notification);
    }
}
