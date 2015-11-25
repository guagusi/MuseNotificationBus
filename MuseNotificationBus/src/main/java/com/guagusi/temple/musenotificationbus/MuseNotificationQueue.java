package com.guagusi.temple.musenotificationbus;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by liangwenhua on 15/11/14.
 */
public class MuseNotificationQueue {
    private static final Object SLOCK = new Object();
    private static MuseNotificationQueue sInstance;
    private PriorityBlockingQueue<MuseNotification> mNotificationQueue;

    private MuseNotificationQueue() {
        mNotificationQueue = new PriorityBlockingQueue<>(30, priorityComparator);
    }

    /**
     * 单例
     * @return
     */
    public static MuseNotificationQueue newInstance() {
        if(sInstance == null) {
            synchronized (SLOCK) {
                if(sInstance == null) {
                    sInstance = new MuseNotificationQueue();
                }
            }
        }
        return sInstance;
    }

    /**
     * 实际存储的优先队列
     * @return
     */
    public PriorityBlockingQueue<MuseNotification> getNotificationQueue() {
        return mNotificationQueue;
    }

    public void add(MuseNotification notification) {
        mNotificationQueue.add(notification);
    }

    public MuseNotification poll() {
        return mNotificationQueue.poll();
    }

    public void clear() {
        mNotificationQueue.clear();
    }

    /**
     * 比较器。使用MuseNotification 的 priority 属性比较
     */
    public static Comparator<MuseNotification> priorityComparator = new Comparator<MuseNotification>() {
        @Override
        public int compare(MuseNotification lhs, MuseNotification rhs) {
            return lhs.getPriority() - rhs.getPriority();
        }
    };
}
