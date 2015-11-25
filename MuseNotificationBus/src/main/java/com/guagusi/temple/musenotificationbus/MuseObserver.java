package com.guagusi.temple.musenotificationbus;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangwenhua on 15/11/13.
 * mParams 最多只能4个
 */
public class MuseObserver {
    private static final String TAG = MuseObserver.class.getSimpleName();

    private int mPriority;
    private Class mClazz;
    private Object mObj;
    private String mName;
    private Method mMethod;
    private LinkedHashMap<String, Class> mParamsMap;
    private boolean mIsDirty;
    private LinkedHashMap mNotificationParams;

    public MuseObserver(int priority, Object obj, String name, String method,
                        LinkedHashMap<String, Class> paramsMap) {
        mPriority = priority;
        mObj = obj;
        mClazz = obj.getClass();
        mName = name;

        Method[] methods = mClazz.getDeclaredMethods();
        for(Method m: methods) {
            if(m.getName().contains(method)) {
                Log.e("MuseObserver", "存在回调方法 " + method);
                // TODO 检查参数是否一致
                mMethod = m;
                mParamsMap = paramsMap;

                if(mParamsMap != null) {
                    Log.e("MuseObserver", "回调方法参数" + mParamsMap.keySet().toString());
                } else {
                    Log.e("MuseObserver", "回调方法参数为空");
                }
                break;
            }
        }

        if(mMethod == null) {
            mIsDirty = true;
        }
    }

    public void invokeCallback() {
        if(!mIsDirty) {
            List<String> keySet = new ArrayList<>();
            if(mNotificationParams != null) {
                Iterator<String> iterator = mNotificationParams.keySet().iterator();
                while(iterator.hasNext()) {
                    keySet.add(iterator.next());
                }
            }

            int len = 0;
            if(mParamsMap != null) {
                len = mParamsMap.size();
                if(len != keySet.size()) {
                    Log.e(TAG, "注册回调方法的参数与实际post的参数不一致");
                    return;
                }
            }
            try {
                switch (len) {
                    case 0:
                        mMethod.invoke(mObj);

                        break;
                    case 1:
                        String key11 = keySet.get(0);
                        if(mParamsMap.containsKey(key11)) {
                            mMethod.invoke(mObj, mNotificationParams.get(key11));
                        }
                        break;
                    case 2:
                        String key21 = keySet.get(0);
                        String key22 = keySet.get(1);
                        if(mNotificationParams.containsKey(key21) && mNotificationParams.containsKey(key22)) {
                            mMethod.invoke(mObj, mNotificationParams.get(key21), mNotificationParams.get(key22));
                        }
                        break;
                    case 3:
                        String key31 = keySet.get(0);
                        String key32 = keySet.get(1);
                        String key33 = keySet.get(2);
                        if(mParamsMap.containsKey(key31) && mParamsMap.containsKey(key32) &&
                                mParamsMap.containsKey(key33)) {
                            mMethod.invoke(mObj, mNotificationParams.get(key31), mNotificationParams.get(key32),
                                    mNotificationParams.get(key33));
                        }
                        break;
                    case 4:
                        String key41 = keySet.get(0);
                        String key42 = keySet.get(1);
                        String key43 = keySet.get(2);
                        String key44 = keySet.get(3);
                        if(mParamsMap.containsKey(key41) && mParamsMap.containsKey(key42) &&
                                mParamsMap.containsKey(key43) && mParamsMap.containsKey(key44)) {
                            mMethod.invoke(mObj, mNotificationParams.get(key41), mNotificationParams.get(key42),
                                    mNotificationParams.get(key43), mNotificationParams.get(key43));
                        }
                        break;
                    default:
                        break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("MuseObserver", "开始处理回调方法...dirty");
        }
    }

    public void setNotificationParams(LinkedHashMap map) {
        mNotificationParams = map;
    }

    public int getPriority() {
        return mPriority;
    }

    public String getName() {
        return mName;
    }

    public Class getClazz() {
        return mClazz;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
