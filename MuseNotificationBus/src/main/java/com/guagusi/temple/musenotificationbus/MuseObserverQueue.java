package com.guagusi.temple.musenotificationbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MuseObserver 队列
 * Created by liangwenhua on 15/11/13.
 */
public class MuseObserverQueue {
    private static final String TAG = MuseObserverQueue.class.getSimpleName();

    private static final Object SLOCK = new Object();
    private static MuseObserverQueue sObserverQueue;

    private Map<String, MuseObserver> mASAPObservers;
    private Map<String, MuseObserver> mNORMALObservers;
    private Map<String, MuseObserver> mIDLEObservers;

    private MuseObserverQueue() {
        mASAPObservers = new HashMap<>();
        mNORMALObservers = new HashMap<>();
        mIDLEObservers = new HashMap<>();
    }

    /**
     * 单例
     * @return
     */
    public static MuseObserverQueue newInstance() {
        if(sObserverQueue == null) {
            synchronized (SLOCK) {
                if(sObserverQueue == null) {
                    sObserverQueue = new MuseObserverQueue();
                }
            }
        }
        return sObserverQueue;
    }

    /**
     * 根据主题 name 获取观察者
     * @param name  主题
     * @return
     */
    public MuseObserver get(String name) {
        if(name != null && name.length() > 0) {
            synchronized (SLOCK) {
                if (mASAPObservers.containsKey(name)) {
                    return mASAPObservers.get(name);
                }
                if (mNORMALObservers.containsKey(name)) {
                    return mIDLEObservers.get(name);
                }
                if (mIDLEObservers.containsKey(name)) {
                    return mIDLEObservers.get(name);
                }
            }
        }
        return null;
    }

    /**
     * 添加观察者
     * @param name  订阅主题
     * @param observer  观察者
     */
    public void add(String name, MuseObserver observer) {
        if(name != null && name.length() > 0 && observer != null) {
            synchronized (SLOCK) {
                int priority = observer.getPriority();
                if (priority == MuseNotificationCenter.ASAP_PRIORITY) {
                    mASAPObservers.put(name, observer);
                } else if (priority == MuseNotificationCenter.IDLE_PRIORITY) {
                    mIDLEObservers.put(name, observer);
                } else {
                    mNORMALObservers.put(name, observer);
                }
            }
        }
    }

    /**
     *
     * @param name
     * @return
     */
    public MuseObserver remove(String name) {
        if(name != null && name.length() > 0) {
            synchronized (SLOCK) {
                if (mASAPObservers.containsKey(name)) {
                    return mASAPObservers.remove(name);
                }
                if (mNORMALObservers.containsKey(name)) {
                    return mIDLEObservers.remove(name);
                }
                if (mIDLEObservers.containsKey(name)) {
                    return  mIDLEObservers.remove(name);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param observer
     * @return
     */
    public MuseObserver remove(MuseObserver observer) {
        if(observer != null) {
            synchronized (SLOCK) {
                if (mASAPObservers.containsValue(observer)) {
                    return mASAPObservers.remove(observer.getName());
                }
                if (mNORMALObservers.containsValue(observer)) {
                    return mIDLEObservers.remove(observer.getName());
                }
                if (mIDLEObservers.containsValue(observer)) {
                    return  mIDLEObservers.remove(observer.getName());
                }
            }
        }
        return null;
    }

    /**
     *
     * @param obj
     * @return
     */
    public MuseObserver remove(Object obj) {
        if(obj != null) {
            synchronized (SLOCK) {
                List<MuseObserver> observers = observerList();
                for(MuseObserver observer :observers) {
                    if(observer.getClazz().getName().equalsIgnoreCase(obj.getClass().getName())) {
                        int priority = observer.getPriority();
                        if(priority == MuseNotificationCenter.ASAP_PRIORITY) {
                            return mASAPObservers.remove(observer.getName());
                        } else if(priority == MuseNotificationCenter.NORMAL_PRIORITY) {
                            return mNORMALObservers.remove(observer.getName());
                        } else if(priority == MuseNotificationCenter.IDLE_PRIORITY) {
                            return mIDLEObservers.remove(observer.getName());
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 是否存在
     * @param observer
     * @return
     */
    public boolean containObserver(MuseObserver observer) {
        if(observer != null) {
            int priority = observer.getPriority();
            if(priority == MuseNotificationCenter.ASAP_PRIORITY) {
                return mASAPObservers.containsValue(observer);
            } else if(priority == MuseNotificationCenter.IDLE_PRIORITY) {
                return mIDLEObservers.containsValue(observer);
            } else if(priority == MuseNotificationCenter.NORMAL_PRIORITY){
                return mNORMALObservers.containsValue(observer);
            }
        }
        return false;
    }

    /**
     * 是否存在
     * @param key
     * @return
     */
    public boolean containKey(String key) {
        if(key != null && key.length() > 0) {
            return mASAPObservers.containsKey(key) | mNORMALObservers.containsKey(key) |
                    mIDLEObservers.containsKey(key);
        }
        return false;
    }

    /**
     * 返回所有主题。按优先级排列
     * @return
     */
    public List<String> nameList() {
        synchronized (SLOCK) {
            List<String> names = new ArrayList<>();
            names.addAll(mASAPObservers.keySet());
            names.addAll(mNORMALObservers.keySet());
            names.addAll(mIDLEObservers.keySet());
            return names;
        }
    }

    /**
     * 返回主题列表
     * @param priority
     * @return
     */
    public List<String> nameTypeList(int priority) {
        synchronized (SLOCK) {
            List<String> names = null;
            if (priority == MuseNotificationCenter.ASAP_PRIORITY) {
                names = new ArrayList<>();
                names.addAll(mASAPObservers.keySet());
            } else if (priority == MuseNotificationCenter.IDLE_PRIORITY) {
                names = new ArrayList<>();
                names.addAll(mIDLEObservers.keySet());
            } else if (priority == MuseNotificationCenter.NORMAL_PRIORITY) {
                names = new ArrayList<>();
                names.addAll(mNORMALObservers.keySet());
            }
            return names;
        }
    }

    /**
     * 返回所有观察者。按优先级排列
     * @return
     */
    public List<MuseObserver> observerList() {
        synchronized (SLOCK) {
            List<MuseObserver> observers = new ArrayList<>();
            observers.addAll(mASAPObservers.values());
            observers.addAll(mNORMALObservers.values());
            observers.addAll(mIDLEObservers.values());
            return observers;
        }
    }

    /**
     * 返回观察者列表
     * @param priority
     * @return
     */
    public List<MuseObserver> observerTypeList(int priority) {
        synchronized (SLOCK) {
            List<MuseObserver> observers = null;
            if (priority == MuseNotificationCenter.ASAP_PRIORITY) {
                observers = new ArrayList<>();
                observers.addAll(mASAPObservers.values());
            } else if (priority == MuseNotificationCenter.IDLE_PRIORITY) {
                observers = new ArrayList<>();
                observers.addAll(mIDLEObservers.values());
            } else if (priority == MuseNotificationCenter.NORMAL_PRIORITY) {
                observers = new ArrayList<>();
                observers.addAll(mNORMALObservers.values());
            }
            return observers;
        }
    }

    /**
     * 清空
     */
    public void clear() {
        synchronized (SLOCK) {
            mASAPObservers.clear();
            mNORMALObservers.clear();
            mIDLEObservers.clear();
        }
    }
}
