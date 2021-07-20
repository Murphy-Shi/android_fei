package com.feilib.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
    private final Context mContext;
    private static final String DEFAULT = "default";

    MyDataStorage(Context context) {
        this.mContext = context;
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


    //获取raw的资源
    public void getRaw(int id) {
        getResource(id, "", "", null);
    }

    //获取asset的资源
    public void getAsset(String assestName) {
        getResource(0, assestName, "", null);
    }

    //获取文件名资源
    public void getFile(String fileName) {
        getResource(0, "", fileName, null);
    }

    //获取文件名资源
    public void getFile(File file) {
        getResource(0, "", "", file);
    }

    //将内容写入文件
    public void writeFile(String content, String path) {
        writeFileToDevice(content, null, path);
    }

    //将file文件写入
    public void writeFile(File file, String path) {
        writeFileToDevice("", file, path);
    }

    /**
     * Context.MODE_PRIVATE：为默认操作模式,代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容
     * Context.MODE_APPEND：模式会检查文件是否存在,存在就往文件追加内容,否则就创建新文件.
     * Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件.
     * MODE_WORLD_READABLE：表示当前文件可以被其他应用读取. ----危险方法，可能会造成应用崩溃
     * MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入. ----危险方法，可能会造成应用崩溃
     * 当遇到key名称与类型均一样时,则覆盖其值;如果其key值一样，但类型不同，其将插入
     * 4. commit()和apply()的区别
     * (1) commit()有boolen返回值代表插入是否成功，apply()没有。
     * (2) apply()将数据原子操作提交到内存，然后异步提交到磁盘。commit()是同步提交到硬盘。
     */
    private void sharedPreferencesSave(String group, String key, String value) {
        if (mContext == null || key == null || value == null) {
            Log.e(TAG, "传入的Activity、key、value不能为null");
            return;
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(group, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    private String sharedPreferencesGet(String group, String key) {
        if (mContext == null || key == null || "".equals(key)) {
            Log.e(TAG, "传入的Activity、key、value不能为null");
            return "";
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(group, Activity.MODE_PRIVATE);
        return sharedPreferences.getString("key", "");
    }

    private Map sharedPreferencesGetAll(String group) {
        if (mContext == null) {
            Log.e(TAG, "传入的Activity不能为null");
            return new HashMap();
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(group, Activity.MODE_PRIVATE);
        return sharedPreferences.getAll();
    }


    /**
     * Context.MODE_PRIVATE	指定该文件数据只能被本应用程序读、写
     * Context.MODE_WORLD_READABLE	指定该文件数据能被其他应用程序读，但不能写
     * Context.MODE_WORLD_WRITEABLE	指定该文件数据能被其他应用程序读
     * Context.MODE_APPEND	该模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件；
     */
    private StringBuilder getResource(int id, String assetName, String fileName, File file) {
        InputStream inputStream = null;
        FileInputStream fileInputStream = null;
        Reader reader = null;
        BufferedReader bufferedReader = null;
        StringBuilder result = new StringBuilder();

        try {
            if (mContext == null || (id == 0 && "".equals(assetName) && "".equals(fileName) && file == null)) {
                Log.e(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + "传入的context、id、fileName不能为空");
                return result;
            }

            if (id != 0) {
                inputStream = mContext.getResources().openRawResource(id);
            } else if (!"".equals(assetName)) {
                inputStream = mContext.getResources().getAssets().open(assetName);
            } else if (!"".equals(fileName)) {
                fileInputStream = mContext.openFileInput(fileName);

            } else {
                inputStream = new FileInputStream(file);

            }

            if (!"".equals(fileName)) {
                reader = new InputStreamReader(fileInputStream);
            } else {
                reader = new InputStreamReader(inputStream);
            }

            bufferedReader = new BufferedReader(reader);
            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                result.append(tmp);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private void writeFileToDevice(String content, File file, String path) {
        FileOutputStream fileOutputStream = null;
        try {
            if (content == null || "".equals(path) || (file == null && "".equals(content))) {
                Log.e(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + "传入的context、id、fileName不能为空");
                return;
            }

            fileOutputStream = mContext.openFileOutput(path, Context.MODE_PRIVATE);
            if (file != null) {
                fileOutputStream.write(file2byte(file));
            } else {
                fileOutputStream.write(content.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                ;
            }
        }
    }

    //file转byte数组
    public static byte[] file2byte(File tradeFile) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private boolean checkRWPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isRead = mContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED;
            boolean isWrite = mContext.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED;

            return isRead && isWrite;
        }
        return true;
    }

    private void requestRWPermissions(){

    }

    private void writeToSdcard(){

    }
}
