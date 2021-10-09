package com.feilib.ui.floatBalll;

import android.content.Context;
import android.view.View;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/9/24 10:52 上午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/9/24 10:52 上午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class FloatBallManager {
    private static FloatBallManager instance;

    public static FloatBallManager getInstance() {
        if (instance == null) {
            synchronized (FloatBallManager.class) {
                if (instance == null) {
                    instance = new FloatBallManager();
                }
            }
        }
        return instance;
    }

    public ConcurrentHashMap<String, FloatBallView> mFloatBallViewHashMap = new ConcurrentHashMap<>();

    public static String tag = "first";


    /**
     * 创建新的float
     */
    public void create(Context context, View.OnClickListener onClickListener, int floatImgId) {
        FloatBallView floatBallView = new FloatBallView(context.getApplicationContext());
        floatBallView.setImageResource(floatImgId);

        mFloatBallViewHashMap.put(tag, floatBallView);

    }


    /**
     * 显示
     */
    public void show() {
        FloatBallView floatBallView = mFloatBallViewHashMap.get(tag);
        if (floatBallView != null) {
            floatBallView.show();
        }
    }


    /**
     * 隐藏悬浮球
     */
    public void hideFloatBall() {
        FloatBallView floatBallView = mFloatBallViewHashMap.get(tag);
        if (floatBallView != null) {
            floatBallView.hide();
        }
    }

    /**
     * 隐藏全部
     */
    public void hideAllFloatBall() {
        for (String key : mFloatBallViewHashMap.keySet()) {
            FloatBallView floatBallView = mFloatBallViewHashMap.get(key);
            if (floatBallView != null) {
                floatBallView.hide();
            }
        }
    }


    /**
     * 销毁
     */
    public void destroy() {
        FloatBallView floatBallView = mFloatBallViewHashMap.get(tag);
        if (floatBallView != null) {
            floatBallView.hide();
        }
    }

    /**
     * 销毁全部
     */
    public void destroyAll() {
        for (String key : mFloatBallViewHashMap.keySet()) {
            FloatBallView floatBallView = mFloatBallViewHashMap.get(key);
            if (floatBallView != null) {
                floatBallView.destroy();
            }
            mFloatBallViewHashMap.remove(key);
        }
    }
}
