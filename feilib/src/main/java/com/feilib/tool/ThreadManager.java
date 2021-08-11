package com.feilib.tool;

import android.content.res.Resources;
import android.util.Log;

/**
 * 启动工作线程
 */
public class ThreadManager {
    private final String TAG = "ThreadManager";

    //独立任务，资源不共享
    private void createThread(){
        new Thread(() -> {
            Log.d(TAG, "run: test");
        }).start();
    }

    //资源共享、不用锁机制会造成线程安全
    private void createRunnable(){
        Runnable runnable = () -> {
            Log.d(TAG, "run: test");
        };
        runnable.run();
    }



}
