package com.example.fei;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fei.toolInfo.Encrypt;
import com.feilib.tool.DownLoadUtil;
import com.feilib.tool.MyDataStorage;
import com.feilib.tool.ThreadManager;
import com.feilib.ui.web.MyWebViewUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    protected Context mContext;
    protected ListView mListView;
    protected String[] mListName = {"权限", "加密", "web", "apk下载"};
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
                HashMap hashMap = new HashMap();
                hashMap.put("aaa", "111");
                hashMap.put("aaa", "哈哈哈哈");
                hashMap.put("aaa1", null);
                getUrl("aaa", hashMap);
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

    protected String getUrl(String url, Map<String, String> params) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }

        if (params == null) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);
        for (String key : params.keySet()){
            String symbol = sb.toString().contains("?") ? "&" : "?";
            String paramValue = params.get(key);
            String value = paramValue == null ? "" : paramValue;

            sb.append(symbol)
                    .append(key)
                    .append("=")
                    .append(value);
//                    .append(Uri.encode(value), ":/-![].,%?&="));
        }
        return sb.toString();

//
//        while (it.hasNext()) {
//            String key = it.next();
//            String value = params.get(key);
//            if (TextUtils.isEmpty(value)) {
//                value = "";
//            }
//            if (sb == null) {
//                sb = new StringBuffer();
//                if (!url.contains("?")) {
//                    sb.append("?");
//                } else {
//                    sb.append("&");
//                }
//            } else {
//                sb.append("&");
//            }
//            sb.append(key);
//            sb.append("=");
//            sb.append(value);
//        }
//        url += sb.toString();
//        return url;
    }
}