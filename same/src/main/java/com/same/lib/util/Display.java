package com.same.lib.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.same.lib.theme.Theme;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description 适配机械键盘、屏幕密度、平板模式
 * @usage null
 */
public class Display {
    public static int statusBarHeight = 0;
    public static boolean firstConfigurationWas;
    public static float density = 1;
    public static Point displaySize = new Point();
    public static float screenRefreshRate = 60;
    public static int roundMessageSize;
    public static int roundMessageInset;
    public static boolean incorrectDisplaySizeFix;
    public static Integer photoSize = null;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static int leftBaseline;
    public static boolean usingHardwareInput;
    public static boolean isInMultiwindow;

    public static int getMinTabletSide() {
        if (!AndroidUtilities.isSmallTablet()) {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int leftSide = smallSide * 35 / 100;
            if (leftSide < AndroidUtilities.dp(320)) {
                leftSide = AndroidUtilities.dp(320);
            }
            return smallSide - leftSide;
        } else {
            int smallSide = Math.min(displaySize.x, displaySize.y);
            int maxSide = Math.max(displaySize.x, displaySize.y);
            int leftSide = maxSide * 35 / 100;
            if (leftSide < AndroidUtilities.dp(320)) {
                leftSide = AndroidUtilities.dp(320);
            }
            return Math.min(smallSide, maxSide - leftSide);
        }
    }

    /**
     * 适配机械键盘、屏幕密度、平板模式
     * @param context
     * @param newConfiguration
     */
    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        try {
            float oldDensity = density;
            density = context.getResources().getDisplayMetrics().density;
            float newDensity = density;
            if (firstConfigurationWas && Math.abs(oldDensity - newDensity) > 0.001) {
                Theme.reloadAllResources(context);
            }
            firstConfigurationWas = true;
            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            //是否使用 机械键盘
            usingHardwareInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                android.view.Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                    screenRefreshRate = display.getRefreshRate();
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
            if (roundMessageSize == 0) {
                if (AndroidUtilities.isTablet()) {
                    roundMessageSize = (int) (getMinTabletSide() * 0.6f);
                } else {
                    roundMessageSize = (int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.6f);
                }
                roundMessageInset = AndroidUtilities.dp(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
