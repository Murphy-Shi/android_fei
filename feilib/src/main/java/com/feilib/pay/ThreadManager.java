package com.feilib.pay;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 启动工作线程
 */
public class ThreadManager {
    private final String TAG = "ThreadManager";

    private static volatile ThreadManager instance;
    private final ExecutorService mChildThread;
    private final Executor mMainThread;


    private ThreadManager(ExecutorService childThread, Executor mainThread) {
        this.mChildThread = childThread;
        this.mMainThread = mainThread;
    }

    public ThreadManager() {
        this(Executors.newFixedThreadPool(3, sThreadFactory), new MainThreadExecutor());
    }

    public static ThreadManager getInstance() {
        if (instance == null) {
            synchronized (ThreadManager.class) {
                if (instance == null) {
                    instance = new ThreadManager();
                }
            }
        }
        return instance;
    }

    public void executeChild(Runnable task) {
        if (task == null) {
            return;
        }
        mChildThread.execute(task);
    }

    public void executeMain(Runnable task) {
        if (task == null) {
            return;
        }
        mChildThread.submit(task);
    }

    public ExecutorService childThread() {
        return mChildThread;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    private final static ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, String.format(Locale.CHINA, "%s-thread-pool#%d", "ThreadManager", mCount.getAndIncrement()));
            Log.d("ThreadManager", "create new Thread：" + thread.getName());
            return thread;
        }
    };
}
