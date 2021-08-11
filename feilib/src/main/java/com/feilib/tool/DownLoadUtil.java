package com.feilib.tool;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.feilib.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/8/4 3:25 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/8/4 3:25 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class DownLoadUtil{
    //  按钮
    private Button button;
    //  上下文
    private Context mContext;
    //  进度条
    private ProgressBar mProgressBar;
    //  对话框
    private Dialog mDownloadDialog;
    //  判断是否停止
    private boolean mIsCancel = false;
    //  进度
    private int mProgress;
    //  文件保存路径
    private String mSavePath;
    //  版本名称
    private String mVersion_name = "1.0";
    //  请求链接
    private String url = "http://a.xzfile.com/apk3/heixiaapk_v1.2.0_downcc.com.apk";

    public DownLoadUtil(Context context) {
        mContext = context;
    }

    /*
     * 显示正在下载对话框
     */
    public void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("下载中");
        View view = LayoutInflater.from(mContext).inflate(R.layout.fei_dialog_progress, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.id_progress);
        builder.setView(view);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 隐藏当前对话框
                dialog.dismiss();
                // 设置下载状态为取消
                mIsCancel = true;
            }
        });

        mDownloadDialog = builder.create();
        mDownloadDialog.show();

        // 下载文件
        downloadAPK();
    }

    /*
     * 开启新线程下载apk文件
     */
    private void downloadAPK() {
        new Thread(() -> {
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String sdPath = mContext.getExternalCacheDir() + "/";
//                      文件保存路径
                    mSavePath = sdPath + "jikedownload";

                    File dir = new File(mSavePath);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    // 下载文件
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    int length = conn.getContentLength();

                    File apkFile = new File(mSavePath, mVersion_name);
                    FileOutputStream fos = new FileOutputStream(apkFile);

                    int count = 0;
                    byte[] buffer = new byte[1024];
                    while (!mIsCancel) {
                        int numread = is.read(buffer);
                        count += numread;
                        // 计算进度条的当前位置
                        mProgress = (int) (((float) count / length) * 100);
                        // 更新进度条
                        mUpdateProgressHandler.sendEmptyMessage(1);

                        // 下载完成
                        if (numread < 0) {
                            mUpdateProgressHandler.sendEmptyMessage(2);
                            break;
                        }
                        fos.write(buffer, 0, numread);
                    }
                    fos.close();
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 接收消息
     */
    private Handler mUpdateProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // 设置进度条
                    mProgressBar.setProgress(mProgress);
                    break;
                case 2:
                    // 隐藏当前下载对话框
                    mDownloadDialog.dismiss();
                    // 安装 APK 文件
                    installAPK();
            }
        }

        ;
    };


    /*
     * 下载到本地后执行安装
     */
    protected void installAPK() {
        File apkFile = new File(mSavePath, mVersion_name);
        if (!apkFile.exists()) {
            return;
        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
////      安装完成后，启动app（源码中少了这句话）
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Uri uri = Uri.parse("file://" + apkFile.toString());
//        intent.setDataAndType(uri, "application/vnd.android.package-archive");
//        mContext.startActivity(intent);
    }

//    public static void installNormal(Context context, String apkPath) {
//        if (!TextUtils.isEmpty(apkPath)) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            String filePath = apkPath;
//            File file = new File(filePath);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (Build.VERSION.SDK_INT >= 24) {//大于7.0使用此方法
//                Uri apkUri = MJFileProvider.getUriForFile(file);///-----ide文件提供者名
//                //添加这一句表示对目标应用临时授权该Uri所代表的文件
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//            } else {
//                // 由于没有在Activity环境下启动Activity,设置下面的标签
//                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//            }
//            context.startActivity(intent);
//        }
//    }


}//calss
