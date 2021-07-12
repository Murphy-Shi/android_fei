package com.feilib.ui.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/7/2 3:55 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/7/2 3:55 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyWebView {
    private Activity mActivity;
    private WebView mWebView;

    private String TAG = "MyWebView";

    private RelativeLayout bgWebErrorRl;
    private String mUrl;
    private View mView;
    private ProgressBar mProgressBar;
    public boolean isFinishShow;
    public boolean isProgress;
    private int mWidth;
    private int mHeight;

    interface WebListener {
        //web加载状态
        void onWebStatus(boolean isSuccess);

        //web加载进度
        void onProgress(int progress);

        //加载完成
        void onPageFinished(WebView webView, String url);

        //加载开始
        void onPageStarted(WebView view, String url, Bitmap favicon);
    }

    public static WebListener sWebListener;

    public MyWebView(Activity activity) {
        mActivity = activity;
        initView();
        initData();
        initSetting();
    }

    private void initView() {
        mView = LayoutInflater.from(mActivity).inflate(layoutId("fei_webview"), null);

        mWebView = mView.findViewById(ID("fei_wv"));
        bgWebErrorRl = mView.findViewById(ID("bg_web_error_rl"));
        mView.findViewById(ID("bg_btn_refresh")).setOnClickListener(v -> {
            bgWebErrorRl.setVisibility(View.GONE);
            loadUrl(mUrl);
        });
        mProgressBar = mView.findViewById(ID("fei_pb"));

        // 设置背景色
        mWebView.setBackgroundColor(0);
        // 设置填充透明度 范围：0-255
        mWebView.getBackground().setAlpha(0);
    }

    private void initData() {
        sWebListener = new WebListener() {
            @Override
            public void onWebStatus(boolean isSuccess) {
                bgWebErrorRl.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onProgress(int progress) {
                if (isProgress) {
                    mProgressBar.setProgress(progress);
                }
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                mProgressBar.setVisibility(View.GONE);
                if (isFinishShow) {
                    isFinishShow = false;
                    show(mWidth, mHeight);
                    isFinishShow = true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
            }
        };
    }

    public void loadUrl(String url) {
        if (mWebView == null || TextUtils.isEmpty(url)) {
            Log.e("MyWebView", "传入的mWebView和url不能为空");
            return;
        }

        mUrl = url;
        mWebView.loadUrl(url);
    }

    public View getView() {
        return mView;
    }

    public void show() {
        show(0, 0);
    }

    public void show(int width, int height) {
        mWidth = width;
        mHeight = height;
        if (isFinishShow) {
            return;
        }

        if(TextUtils.isEmpty(mUrl)){
            Log.e(TAG, "show: 传入的rurl不能为空");
            return;
        }

        PopupWindow popupWindow;
        if (width == 0 || height == 0) {
            popupWindow = new PopupWindow(getView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        } else {
            popupWindow = new PopupWindow(getView(), width, height, true);
            popupWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(getView());
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void initSetting() {
        //添加js调用事件
        mWebView.addJavascriptInterface(new MyJsInterface(mWebView), MyWebViewUtil.jsName);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());

        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

//        webSettings.setAppCacheEnabled(true);
//        webSettings.setAppCacheMaxSize(yourCacheSize)
//        webSettings.setAppCachePath(yourCacheDirPath)

        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //是否可访问本地文件
        webSettings.setAllowFileAccess(true);
        //将图片调整到适合webview的大小
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //是否隐藏缩放控件
        webSettings.setDisplayZoomControls(false);
        //是否允许访问文件
        webSettings.setAllowFileAccess(true);
        //是否节点缓存
        webSettings.setDomStorageEnabled(true);
        //是否数据缓存
        webSettings.setDatabaseEnabled(true);
        //是否应用缓存
        webSettings.setAppCacheEnabled(true);
        //设置缓存路径
//        webSettings.setAppCachePath(uri);
        //设置编码格式
        webSettings.setDefaultTextEncodingName("UTF-8");
        //是否需要获取焦点
        webSettings.setNeedInitialFocus(true);
        //设置开启定位功能
        webSettings.setGeolocationEnabled(false);
        //是否从网络获取资源
        webSettings.setBlockNetworkLoads(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ApplicationInfo applicationInfo = mWebView.getContext().getApplicationInfo();
            int i = applicationInfo.flags & 2;
            applicationInfo.flags = i;
            if (i != 0) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }

    private int resID(String resType, String resName) {
        Context context = mActivity.getApplicationContext();
        if (context == null) {
            new Exception("请初始化SDK").printStackTrace();
            return 0;
        }
        return context.getResources().getIdentifier(resName, resType, context.getPackageName());
    }

    private int layoutId(String resName) {
        return resID("layout", resName);
    }

    public int ID(String resName) {
        return resID("id", resName);
    }

    protected void onDestroy() {
        if (mWebView != null) {
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }

            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearHistory();
            mWebView.clearView();
            mWebView.removeAllViews();

            try {
                mWebView.destroy();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

}
