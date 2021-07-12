package com.feilib.ui.web;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/7/2 5:00 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/7/2 5:00 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyWebViewClient extends WebViewClient {
    public static final String IMG_KEY = "http://androidimg";
    private final String TAG = "MyWebViewClient";
    private boolean isLoadSuccess;

    public MyWebViewClient() {
        super();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        MyWebView.sWebListener.onPageStarted(view,url,favicon);
        isLoadSuccess = true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        MyWebView.sWebListener.onPageFinished(view,url);
        MyWebView.sWebListener.onWebStatus(isLoadSuccess);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest webResourceRequest) {
        FileInputStream input;
        String url = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            url = webResourceRequest.getUrl().toString();
        }
        if (url.contains(IMG_KEY)) {
            String imgPath = url.replace(IMG_KEY, "");
            try {
                imgPath = URLDecoder.decode(imgPath, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "shouldInterceptRequest: " + "本地图片路径：" + imgPath.trim());
            try {
                input = new FileInputStream(new File(imgPath.trim()));
                return new WebResourceResponse("image/jpg", "UTF-8", input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return super.shouldInterceptRequest(view, webResourceRequest);
    }


    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        Log.e(TAG, "onReceivedSslError: " + error.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        handler.proceed();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.e(TAG, "获取到webview网页异常：" + error.toString() + ", " + request.toString());
        MyWebView.sWebListener.onWebStatus(false);
        isLoadSuccess = false;
//        loadFailView();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        Log.e(TAG,"获取到webview网页异常：" + errorCode + ", " + description + ", " + failingUrl);
//        loadFailView();
        MyWebView.sWebListener.onWebStatus(false);
        isLoadSuccess = false;
    }
}
