package com.example.fei;

import android.app.Application;

import com.feilib.tool.MyCrashHandler;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/7/7 1:38 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/7/7 1:38 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MyCrashHandler.getInstance().init(getApplicationContext());
    }
}
