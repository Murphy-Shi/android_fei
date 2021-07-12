package com.feilib.ui.web;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import java.util.HashMap;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/7/2 3:28 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/7/2 3:28 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyWebViewUtil {
    private final int TYPE_SCREEN = 0;
    private final int TYPE_PART = 1;
    private final int TYPE_ACTIVITY_AUTO = 2;
    private final int TYPE_ACTIVITY_VERTICAL = 3;
    private final int TYPE_ACTIVITY_HORIZONTAL = 4;

    private static MyWebViewUtil instance;
    public static Activity mActivity;
    public static final String jsName = "android";
    private HashMap<String, String> mParams;
    public boolean isFinishShow;
    public boolean isProgress;

    public static MyWebViewUtil getInstance() {
        if (instance == null) {
            instance = new MyWebViewUtil();
        }
        return instance;
    }

    /**
     * 开启Web多线程，必须要在application onCreate开启
     * Android 9及以上必须设置
     */
    public void setDataDirectorySuffix(Application application){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                String processName = Application.getProcessName();
                if (!application.getPackageName().equals(processName)) {
                    WebView.setDataDirectorySuffix(processName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showScreen(Activity activity, String url) {
        show(activity, TYPE_SCREEN, url, 0, 0);
    }

    public void showPart(Activity activity, String url, int width, int height) {
        show(activity, TYPE_PART, url, width, height);
    }

    public void showActivity(Activity activity, String url) {
        show(activity, TYPE_ACTIVITY_AUTO, url, 0, 0);
    }

    public void showActivityVertical(Activity activity, String url) {
        show(activity, TYPE_ACTIVITY_VERTICAL, url, 0, 0);
    }

    public void showActivityHorizontal(Activity activity, String url) {
        show(activity, TYPE_ACTIVITY_HORIZONTAL, url, 0, 0);
    }

    private void show(Activity activity, int type, String url, int width, int height) {
        mActivity = activity;
        MyWebView sMyWebView = new MyWebView(mActivity);
        sMyWebView.isFinishShow = isFinishShow;
        sMyWebView.isProgress = isProgress;
        sMyWebView.loadUrl(getUrlParams(url));

        switch (type){
            case TYPE_SCREEN:
                sMyWebView.show();
                break;
            case TYPE_PART:
                sMyWebView.show(width, height);
                break;
            case TYPE_ACTIVITY_AUTO:
                new MyWebActivity().startMyWebActivity(activity, url, "");
                break;
            case TYPE_ACTIVITY_VERTICAL:
                new MyWebActivity().startMyWebActivity(activity, url, "1");
                break;
            case TYPE_ACTIVITY_HORIZONTAL:
                new MyWebActivity().startMyWebActivity(activity, url, "0");
                break;
            default:
                break;
        }
    }

    private String getUrlParams(String url) {
        if (mParams == null || TextUtils.isEmpty(url)) {
            return url;
        }

        StringBuilder urlBuilder = new StringBuilder(url);
        for (String key : mParams.keySet()) {
            String symbol = urlBuilder.toString().contains("?") ? "&" : "?";

            urlBuilder.append(symbol)
                    .append(key)
                    .append("=")
                    .append(Uri.encode(mParams.get(key), ":/-![].,%?&="));
        }
        return urlBuilder.toString();
    }

    public void setUrlParams(HashMap<String, String> map) {
        mParams = map;
    }
}
