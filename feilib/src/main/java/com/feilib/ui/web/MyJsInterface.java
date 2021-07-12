package com.feilib.ui.web;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * @author ljj
 * @create-time 2019/9/12
 * @desc SDK内部H5的JS与android交互
 */
public class MyJsInterface {
    private WebView webView;
    private Handler handler = new Handler(Looper.getMainLooper());


    // 当前使用照片的webView
    public static WebView webViewUsePhoto;

    public MyJsInterface(WebView webView) {
        this.webView = webView;
    }

    //关闭闪屏页
    @JavascriptInterface
    public void closeSplash(String jsParams) {
    }


    //获取图片
    public static final int REQUEST_CODE_GET_PHOTO = 1888;


    @JavascriptInterface
    public void getPhoto(final int maxCount) {
        handler.post(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int storageState = MyWebViewUtil.mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (storageState != PackageManager.PERMISSION_GRANTED) {
                    MyWebViewUtil.mActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1200);
                    return;
                }
            }
//                BigunImage bigunImage = new BigunImage(WebViewUtil.mActivity);
//                bigunImage.setMaxSelectedPhoto(maxCount);
//                bigunImage.show((ViewGroup) webView.getParent(), new BigunImage.BigunImageListener() {
//                    @Override
//                    public void onSelectImgResult(List<ImageModel> imageModelList) {
//                        try {
//                            JSONObject jsonObject = new JSONObject();
//                            JSONArray urlJsonArray = new JSONArray();
//                            JSONArray base64JsonArray = new JSONArray();
//
//                            if (imageModelList != null) {
//                                for (ImageModel imageModel : imageModelList) {
//                                    urlJsonArray.put(imageModel.getPath());
//                                    BGLog.e(imageModel.getPath());
//                                    //获取缩略图
//                                    final Bitmap thumb = MediaStore.Images.Thumbnails.getThumbnail
//                                            (WebViewUtil.mActivity.getContentResolver(), imageModel.getId(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
//                                    String b64String = BGFileHelper.bitmapToBase64(thumb);
//                                    base64JsonArray.put(b64String);
//                                }
//                                jsonObject.put("imgUrls", urlJsonArray);
//                                jsonObject.put("base64Imgs", base64JsonArray);
//
//                                if (urlJsonArray.length() > 0) {
//                                    webView.loadUrl("javascript:onAcceptPhoto(" + jsonObject + ")");
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

        });
    }


    //图片上传
    @JavascriptInterface
    public void uploadImg(final String params, final String urlApi, final String imgUrl) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                String imgPath = imgUrl.replace(BGWebHelper.IMG_KEY, "");
//                BGFileHelper.uploadImg(params, urlApi, imgPath, new BGSDKListener() {
//                    @Override
//                    public void onFinish(Map<String, Object> map, String errorCode) {
//                        try {
//                            JSONObject jo = new JSONObject(map);
//                            webView.loadUrl("javascript:onUploadImgResult('" + jo.toString() + "')");
//                        } catch (Exception e) {
//                            webView.loadUrl("javascript:onUploadImgResult('" + e.toString() + "')");
//                        }
//                    }
//                });
//            }
//        });
    }

    //图片上传
    @JavascriptInterface
    public void uploadImg(final String params, final String urlApi) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    JSONObject jsonObject = new JSONObject(params);
//                    JSONArray imgs = jsonObject.optJSONArray("imgs");
//                    for (int i = 0; i < imgs.length(); i++) {
//                        String imgUrl = imgs.optString(i);
//                        BGFileHelper.uploadImg("{}", urlApi, imgUrl, new BGSDKListener() {
//                            @Override
//                            public void onFinish(Map<String, Object> map, String errorCode) {
//                                try {
//                                    JSONObject jo = new JSONObject(map);
//                                    webView.loadUrl("javascript:onUploadImgResult('" + jo.toString() + "')");
//                                } catch (Exception e) {
//                                    webView.loadUrl("javascript:onUploadImgResult('" + e.toString() + "')");
//                                }
//                            }
//                        });
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    BGLog.e("uploadImg --- JSON解释错误");
//                }
//            }
//        });
    }


    /**
     * 打开外部链接
     */
    @JavascriptInterface
    public void openUrl(final String url) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    MyWebViewUtil.mActivity.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }



    /**
     * 文字复制
     */
    @JavascriptInterface
    public void copyText(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ClipboardManager clipboardManager = (ClipboardManager) MyWebViewUtil.mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(null, text);
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MyWebViewUtil.mActivity, "复制成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @JavascriptInterface
    public void showToast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyWebViewUtil.mActivity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JavascriptInterface
    public void closeInAndroid() {

    }

    @JavascriptInterface
    public void showAndroidWeb(String url, String orientation){
        if(url != null && !"".equals(url)){
//            new BGWebActivity().startBGWebActivity(url, "1".equals(orientation)? "1" : "0");
        }
    }
}
