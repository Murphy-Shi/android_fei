package com.example.fei;

import android.Manifest;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.feilib.permission.PermissionHeadle;

import java.util.ArrayList;

public class PerimissionActivity extends MainActivity implements AdapterView.OnItemClickListener {
    protected String[] mListName = {"发起读写和设备码权限"};

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                String[] permissionStr = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
                new PermissionHeadle(this).need(permissionStr)
                        .requestPremission(new PermissionHeadle.Subscribe() {
                            @Override
                            public void onResult(boolean allGranted, ArrayList<String> grantedList, ArrayList<String> deniedList) {
                                showToat("状态：" + allGranted);
                            }
                        });
                break;
            default:
                break;
        }
    }

    @Override
    protected void initListView() {
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListName));
        mListView.setOnItemClickListener(this);
    }
}