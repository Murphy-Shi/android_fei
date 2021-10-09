package com.example.fei;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fei.toolInfo.Encrypt;
import com.feilib.designPattern.BuilderTest;
import com.feilib.tool.AppUtils;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    protected Context mContext;
    protected ListView mListView;
    protected String[] mListName = {"权限", "加密", "web","Nothing", "apk下载", "插件", "获取app信息"};
    private String TAG = "MainActivity";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                stayActivity(PerimissionActivity.class);
                break;
            case 1:
                new Encrypt().test(this);
                break;
            case 2:
                stayActivity(WebActivity.class);
                break;
            case 3:
//                DownLoadUtil downLoadUtil = new DownLoadUtil(mContext);
//                downLoadUtil.showDownloadDialog();
//                HashMap hashMap = new HashMap();
//                hashMap.put("aaa", "111");
//                hashMap.put("aaa", "哈哈哈哈");
//                hashMap.put("aaa1", null);
//                getUrl("aaa", hashMap);
                break;
            case 4:
                BuilderTest builderTest = new BuilderTest();
                builderTest.setAge("111")
                        .setName("haha");
                break;
            case 5: //插件
                break;
            case 6: //app信息
                Log.e(TAG, "onItemClick: VersionCode" + AppUtils.getVersionCode(this));
                Log.e(TAG, "onItemClick: VersionName" + AppUtils.getVersionName(this));

                break;
            default:
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;

        mListView = findViewById(R.id.main_lv);

        initListView();
        char[] urlChar = {97, 72, 82, 48, 99, 72, 77, 54, 76, 121, 57, 121, 97, 67, 49, 104, 99, 71, 107, 117, 101, 72, 99, 52, 79, 68, 103, 117, 98, 109, 86, 48, 76, 119, 61, 61, 10};
        String tmp = new String(Base64.decode(new String(urlChar), Base64.DEFAULT));
        Log.i("test", "onCreate: " + tmp);
//        char[] aa = new char[Base64.encodeToString("https://rh-api.ktzszzdj.com/", Base64.DEFAULT)];
    }

    public void logE(String msg) {
        int level = 1;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String methodName = stacks[level].getMethodName();
        String className = stacks[level].getClassName();
        Log.e(className, " " + methodName + "  msg:" + msg);
    }

    public void logI(String msg) {
        int level = 1;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String methodName = stacks[level].getMethodName();
        String className = stacks[level].getClassName();
        Log.i(className, " " + methodName + "  msg:" + msg);
    }

    public void showToat(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void initListView() {
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListName));
        mListView.setOnItemClickListener(this);
    }

    private void stayActivity(Class<?> cls) {
        MainActivity.this.startActivity(new Intent(MainActivity.this, cls));
    }
}