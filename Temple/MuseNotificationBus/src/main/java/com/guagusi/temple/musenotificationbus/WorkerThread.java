package com.guagusi.temple.musenotificationbus;

/**
 * Created by liangwenhua on 15/11/14.
 * construct 运行在线程中，将结果在 finished(Object) 中回调。也可以直接用 getValue() 等待结果返回
 */
public abstract class WorkerThread {
    private Object mValue;
    private ThreadVar mThreadVar;

    public WorkerThread() {
        Runnable worker = new Runnable() {
            @Override
            public void run() {
                Object value = null;
                try {
                    value = construct();
                    setValue(value);
                } finally {
                    mThreadVar.clear();
                }
                finished(value);
            }
        };
        Thread thread = new Thread(worker);
        mThreadVar = new ThreadVar(thread);
    }

    /**
     * 线程中实际运行
     * @return
     */
    public abstract Object construct();

    /**
     * 启动线程
     */
    public void start() {
        Thread thread = mThreadVar.get();
        if(thread != null) {
            thread.start();
        }
    }

    /**
     * 线程挂起
     */
    public void interrupt() {
        Thread thread = mThreadVar.get();
        if(thread != null) {
            thread.interrupt();
        }
    }

    /**
     * 线程结束时调用。可实现回调
     * @param value 运行结果
     */
    public void finished(Object value) {}

    private synchronized void setValue(Object value) {
        mValue = value;
    }

    /**
     * 阻塞当前线程，直到线程结束有结果返回
     * @return
     */
    public synchronized Object getValue() {
        while(true) {
            Thread thread = mThreadVar.get();
            if (thread != null) {
                return getValue();
            }
            try {
                thread.join();  // 让调用改方法的thread完成run方法里面的东西后， 在执行join()方法后面的代码
            } catch (InterruptedException e) {
                interrupt();
                return null;
            }
        }
    }

    /**
     * Thread 引用同步管理
     */
    private static class ThreadVar {
        private Thread mThread;

        public ThreadVar(Thread thread) {
            mThread = thread;
        }

        public synchronized Thread get() {
            return mThread;
        }

        public synchronized void set(Thread thread) {
            mThread = thread;
        }

        public synchronized void clear() {
            mThread = null;
        }
    }
}
