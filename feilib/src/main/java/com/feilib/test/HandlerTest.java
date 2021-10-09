package com.feilib.test;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/8/18 3:52 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/8/18 3:52 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class HandlerTest{
    MyHandler mMyHandler;

    HandlerTest(Activity activity){
        mMyHandler = new MyHandler(activity);
    }

    private void handleNothing(){

    }

    static class MyHandler extends Handler{
        private WeakReference<Activity> mActivityWeakReference;

        public MyHandler(Activity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = mActivityWeakReference.get();
            if(activity != null){
                //xxxxx处理事件
            }

        }
    }
}
