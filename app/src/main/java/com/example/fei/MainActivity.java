package com.example.fei;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fei.toolInfo.Encrypt;
import com.feilib.ui.web.MyWebViewUtil;

import java.util.HashMap;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener{
    protected Context mContext;
    protected ListView mListView;
    protected String[] mListName = {"权限", "加密", "web"};

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                stayActivity(PerimissionActivity.class);
                break;
            case 1:
                new Encrypt().test(this);
                break;
            case 2:
                stayActivity(WebActivity.class);
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

    public void logE(String msg){
        int level = 1;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String methodName = stacks[level].getMethodName();
        String className = stacks[level].getClassName();
        Log.e(className, " " + methodName + "  msg:" + msg);
    }

    public void logI(String msg){
        int level = 1;
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String methodName = stacks[level].getMethodName();
        String className = stacks[level].getClassName();
        Log.i(className, " " + methodName + "  msg:" + msg);
    }

    public void showToat(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void initListView(){
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListName));
        mListView.setOnItemClickListener(this);
    }

    private void stayActivity(Class<?> cls){
        MainActivity.this.startActivity(new Intent(MainActivity.this, cls));
    }
}