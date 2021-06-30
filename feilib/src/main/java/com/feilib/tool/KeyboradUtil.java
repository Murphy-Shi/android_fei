package com.feilib.tool;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * @Description: 软键盘遮挡
 * @Author: murphy
 * @CreateDate: 2021/6/25 12:08 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/6/25 12:08 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class KeyboradUtil {
    private  View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private int contentHeight;
    private boolean isfirst = true;
    private int statusBarHeight;

    public static void assistActivity(Activity activity) {
        new KeyboradUtil(activity);
    }

    private KeyboradUtil(Activity activity) {
        try{
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //获取状态栏的高度
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
            FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
            mChildOfContent = content.getChildAt(0);
            //界面出现变动都会调用这个监听事件
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (isfirst) {
                        contentHeight = mChildOfContent.getHeight();//兼容华为等机型
                        isfirst = false;
                    }
                    possiblyResizeChildOfContent();
                }
            });
            frameLayoutParams = (FrameLayout.LayoutParams)
                    mChildOfContent.getLayoutParams();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //重新调整跟布局的高度
    private void possiblyResizeChildOfContent() {
        try {
            int usableHeightNow = computeUsableHeight();
            //当前可见高度和上一次可见高度不一致 布局变动
            if (usableHeightNow != usableHeightPrevious) {
                //int usableHeightSansKeyboard2 = mChildOfContent.getHeight();//兼容华为等机型
                int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
                int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                if (heightDifference > (usableHeightSansKeyboard / 4)) {
                    // keyboard probably just became visible
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        //frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference + statusBarHeight;
                    } else {
                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                    }
                } else {
                    frameLayoutParams.height = contentHeight;
                }
                mChildOfContent.requestLayout();
                usableHeightPrevious = usableHeightNow;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 计算mChildOfContent可见高度 ** @return
     */
    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }
}
