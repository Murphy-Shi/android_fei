package com.feilib.ui.web;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class MyWebActivity extends Activity {
    private static final String TAG = "MyWebActivity";
    private MyWebView mMyWebView;

    public static WebActivityInterface webActivityInterface;



    public interface WebActivityInterface {
        void onCloseActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
    }

    private void init() {
        String orientation = getIntent().getStringExtra("orientationType");
        if (!TextUtils.isEmpty(orientation)) {
            setRequestedOrientation("0".equals(orientation) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        webActivityInterface = this::finish;

        mMyWebView = new MyWebView(this);
        mMyWebView.loadUrl(getIntent().getStringExtra("url"));
        mMyWebView.isFinishShow = false;
        addContentView(mMyWebView.getView(), new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void startMyWebActivity(Activity activity, String url, String orientation) {
        if (activity == null || TextUtils.isEmpty(url)){
            Log.e(TAG, "startBGWebActivity: 传入的Activity和url不能为空");
            return;
        }

        Intent intent = new Intent(activity, MyWebActivity.class);
        intent.putExtra("url", url);
        Log.d("MyWebActivity", "加载地址：" + url);
        intent.putExtra("orientationType", orientation);
        activity.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMyWebView != null) {
            mMyWebView.onDestroy();
        }
        if (webActivityInterface != null) {
            webActivityInterface = null;
        }
    }
}