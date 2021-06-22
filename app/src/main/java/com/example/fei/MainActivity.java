package com.example.fei;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
        });
    }
}