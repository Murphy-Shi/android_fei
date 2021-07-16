package com.feilib.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/7/16 9:57 上午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/7/16 9:57 上午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyDataStorage {
    private static final String TAG = "MyDataStorage";
    private Activity mActivity;
    private static final String DEFAULT = "default";

    MyDataStorage(Activity activity) {
        this.mActivity = activity;
    }

    //使用默认组存入k-v
    public void sharedSave(String key, String value) {
        sharedPreferencesSave(DEFAULT, key, value);
    }

    //使用默认组获取v
    public String sharedGet(String key) {
        return sharedPreferencesGet(DEFAULT, key);
    }

    //使用填入的组存储k-v
    public void sharedSave(String group, String key, String value) {
        sharedPreferencesSave(group, key, value);
    }

    //使用填入的组获取v
    public String sharedGet(String group, String key) {
        return sharedPreferencesGet(group, key);
    }

    //获取默认组全部元素
    public Map sharedGetAll() {
        return sharedPreferencesGetAll(DEFAULT);
    }

    //获取填入组全部元素
    public Map sharedGetAll(String group) {
        return sharedPreferencesGetAll(group);
    }


    /**
     * Context.MODE_PRIVATE：为默认操作模式,代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容
     * Context.MODE_APPEND：模式会检查文件是否存在,存在就往文件追加内容,否则就创建新文件.
     * Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件.
     * MODE_WORLD_READABLE：表示当前文件可以被其他应用读取. ----危险方法，可能会造成应用崩溃
     * MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入. ----危险方法，可能会造成应用崩溃
     */
    private void sharedPreferencesSave(String group, String key, String value) {
        if (mActivity == null || key == null || value == null) {
            Log.e(TAG, "传入的Activity、key、value不能为null");
            return;
        }
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(group, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    private String sharedPreferencesGet(String group, String key) {
        if (mActivity == null || key == null || "".equals(key)) {
            Log.e(TAG, "传入的Activity、key、value不能为null");
            return "";
        }
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(group, Activity.MODE_PRIVATE);
        return sharedPreferences.getString("key", "");
    }

    private Map sharedPreferencesGetAll(String group) {
        if (mActivity == null) {
            Log.e(TAG, "传入的Activity不能为null");
            return new HashMap();
        }
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(group, Activity.MODE_PRIVATE);
        return sharedPreferences.getAll();
    }
}
