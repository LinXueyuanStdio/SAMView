package com.same.lib.util;

import android.content.Context;
import android.os.Handler;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/12/26
 * @description null
 * @usage null
 */
public class UIThread {
    public static volatile Handler applicationHandler;
    private static volatile boolean applicationInited = false;

    public static void init(Context context) {
        if (applicationInited) {
            return;
        }
        applicationInited = true;
        applicationHandler = new Handler(context.getMainLooper());
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            applicationHandler.post(runnable);
        } else {
            applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        applicationHandler.removeCallbacks(runnable);
    }
}
