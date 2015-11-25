# MuseNotificationBus
一个Android平台的简单的事件总线库，用于UI线程中各个组件，模块间的通信。
version 1.0 - 2015-11-13 12:53 am
## 基本架构搭建
类似观察者模式，使用反射回调。

## 生命周期
(*Service 生命周期比 MuseNotificationCenter 长时，Service将接收不到事件通知(Service 中订阅，Service 中接收) *)
结合Activity，Fragment，Service的生命周期，.e.g. 在Activity的 onResume 中订阅，onPause 中取消订阅。

## 优先级
通知事件处理优先级分为三级：ASAP，NORMAL，IDLE
* ASAP(as soon as possible) 最高级，通知事件会排在同级别事件的后面
* NORMAL 普通级，通知事件会排在同级别事件的后面，NORMAL级别的事件排在ASAP级后面
* IDLE 最低级，通知事件会排在同级别事件的后面， IDLE级别的事件排在ASAP级后面
为了避免事件处理失败导致整个阻塞，采用快速失败返回

## 回调调用(*TODO 区分UI线程，非UI线程回调*)
在UI线程回调

## 使用说明
###注册监听
    
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
    
####调用示例


    LinkedHashMap<String, Class> paramMap = new LinkedHashMap();
    paramMap.put("what", Integer.class);
    paramMap.put("msg", String.class);
    paramMap.put("data", List.class);
    // 监听对象，观察主题，优先级，回调方法，回调方法参数
    MuseNotificationCenter.instanceMuse().registerObserver(this, "test", 1, "onMessage", paramMap);

###post消息
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
####调用示例
    LinkedHashMap paramMap = new LinkedHashMap();
    paramMap.put("what", 2);
    paramMap.put("msg", "message");
    List list = new ArrayList<>();
    list.add("good");
    list.add("day");
    paramMap.put("data", list);
    MuseNotificationCenter.instanceMuse().postNotification("test", paramMap);
### 反注册监听
#### 调用示例
MuseNotificationCenter.instanceMuse().unregisterObserver(this);

## 注意事项
MuseNotificationBus 只处理各组件模块见得通讯，与其他主流的事件总线库不同，MuseNotificationBus 所有的操作都在UI线程完成，相当于Handler 的sendMessage，与handlerMessage 的升级版。
回调的方法不能出现重载的情况，否则可能会导致无法被调用。

# 迭代计划
version 1.1
使用线程池实现postNotification(Runnable)
