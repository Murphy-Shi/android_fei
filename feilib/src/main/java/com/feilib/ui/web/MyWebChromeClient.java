package com.feilib.ui.web;

import android.app.AlertDialog;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/7/7 10:31 上午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/7/7 10:31 上午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyWebChromeClient extends WebChromeClient{

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        MyWebView.sWebListener.onProgress(newProgress);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(view.getContext());
        localBuilder.setMessage(message).setPositiveButton("确定",null);
        localBuilder.setCancelable(false);
        localBuilder.create().show();
        result.confirm();
        return true;
//        return super.onJsAlert(view, url, message, result);
    }
}
