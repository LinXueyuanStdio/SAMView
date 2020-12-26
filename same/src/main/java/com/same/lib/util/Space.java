package com.same.lib.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/12/26
 * @description null
 * @usage null
 */
public class Space {
    public static int statusBarHeight = 0;
    public static float density = 1;
    public static Point displaySize = new Point();
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static boolean usingHardwareInput = false;

    public static boolean firstConfigurationWas;
    public static boolean incorrectDisplaySizeFix;
    public static boolean isInMultiwindow;

    public static float screenRefreshRate = 60;

    private static Boolean isTablet = null;

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    public static float dpf2(float value) {
        if (value == 0) {
            return 0;
        }
        return density * value;
    }

    public static int getShadowHeight() {
        if (density >= 4.0f) {
            return 3;
        } else if (density >= 2.0f) {
            return 2;
        } else {
            return 1;
        }
    }

    public static float getPixelsInCM(float cm, boolean isX) {
        return (cm / 2.54f) * (isX ? displayMetrics.xdpi : displayMetrics.ydpi);
    }

    public static boolean isTablet() {
        if (isTablet == null) {
            isTablet = false;
        }
        return isTablet;
    }

    public static boolean isSmallTablet() {
        float minSide = Math.min(displaySize.x, displaySize.y) / density;
        return minSide <= 700;
    }

    public static int getOffsetColor(int color1, int color2, float offset, float alpha) {
        int rF = Color.red(color2);
        int gF = Color.green(color2);
        int bF = Color.blue(color2);
        int aF = Color.alpha(color2);
        int rS = Color.red(color1);
        int gS = Color.green(color1);
        int bS = Color.blue(color1);
        int aS = Color.alpha(color1);
        return Color.argb((int) ((aS + (aF - aS) * offset) * alpha), (int) (rS + (rF - rS) * offset), (int) (gS + (gF - gS) * offset), (int) (bS + (bF - bS) * offset));
    }

    public static void setPadding(View view, float padding) {
        final int px = padding != 0 ? dp(padding) : 0;
        view.setPadding(px, px, px, px);
    }

    public static void setPadding(View view, float left, float top, float right, float bottom) {
        view.setPadding(dp(left), dp(top), dp(right), dp(bottom));
    }

    public static void setPaddingRelative(View view, float start, float top, float end, float bottom) {
        setPadding(view, Store.isRTL ? end : start, top, Store.isRTL ? start : end, bottom);
    }

    /**
     * 适配机械键盘、屏幕密度、平板模式
     * @param context
     * @param newConfiguration
     */
    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        try {
            density = context.getResources().getDisplayMetrics().density;
            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            //是否使用 机械键盘
            usingHardwareInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS
                    && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                android.view.Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                    screenRefreshRate = (int) display.getRefreshRate();
                }
            }
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenWidthDp * density);
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenHeightDp * density);
                if (Math.abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
