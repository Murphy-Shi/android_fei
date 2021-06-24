package com.example.fei;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.feilib.permission.PermissionHeadle;

public class MainActivity extends Activity {
    private Context mContext;
    private final String[] mListName = {"权限"};
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;

        initView();
    }

    private void initView(){
        mListView = findViewById(R.id.main_lv);
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListName));

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            String str = "" + mListName[position];
            Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();

            PermissionHeadle permissionHeadle = new PermissionHeadle(this);
            permissionHeadle.need(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE} );
            permissionHeadle.requestPremission(new PermissionHeadle.Subscribe() {
                @Override
                public void onResult(boolean allGranted, String[] permissions) {
                    Toast.makeText(MainActivity.this, "状态：" + allGranted, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}