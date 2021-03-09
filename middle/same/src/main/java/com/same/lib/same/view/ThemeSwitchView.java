package com.same.lib.same.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;
import com.same.lib.drawable.DrawableManager;
import com.same.lib.lottie.RLottieDrawable;
import com.same.lib.lottie.RLottieImageView;
import com.same.lib.same.R;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.theme.ThemeManager;
import com.same.lib.util.Lang;
import com.same.lib.util.Space;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/3/8
 * @description null
 * @usage null
 */
public class ThemeSwitchView extends RLottieImageView {

    private volatile boolean switchingTheme;

    public boolean isSwitchingTheme() {
        return switchingTheme;
    }

    public void setSwitchingTheme(boolean switchingTheme) {
        this.switchingTheme = switchingTheme;
    }

    public ThemeSwitchView(Context context) {
        super(context);
        RLottieDrawable sunDrawable = new RLottieDrawable(context, R.raw.sun, "" + R.raw.sun, Space.dp(28), Space.dp(28), true, null);
        if (isCurrentThemeDay(context)) {
            sunDrawable.setCustomEndFrame(36);
        } else {
            sunDrawable.setCustomEndFrame(0);
            sunDrawable.setCurrentFrame(36);
        }
        sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
        sunDrawable.beginApplyLayerColors();
        int color = Theme.getColor(KeyHub.key_actionBarDefaultIcon);
        sunDrawable.setLayerColor("Sunny.**", color);
        sunDrawable.setLayerColor("Path 6.**", color);
        sunDrawable.setLayerColor("Path.**", color);
        sunDrawable.setLayerColor("Path 5.**", color);
        sunDrawable.commitApplyLayerColors();
        setScaleType(ImageView.ScaleType.CENTER);
        setAnimation(sunDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            setBackground(DrawableManager.createSelectorDrawable(Theme.getColor(KeyHub.key_listSelector), 1, AndroidUtilities.dp(17)));
            DrawableManager.setRippleDrawableForceSoftware((RippleDrawable) getBackground());
        }
        setOnClickListener(v -> {
            Log.e("switchingTheme", "switchingTheme = " + switchingTheme);
            if (switchingTheme) {
                return;
            }
            switchingTheme = true;
            Log.e("switchingTheme", "click switchingTheme = " + switchingTheme);
            SharedPreferences preferences = context.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
            String dayThemeName = preferences.getString("lastDayTheme", "Blue");
            if (ThemeManager.getTheme(dayThemeName) == null) {
                dayThemeName = "Blue";
            }
            String nightThemeName = preferences.getString("lastDarkTheme", "Dark Blue");
            if (ThemeManager.getTheme(nightThemeName) == null) {
                nightThemeName = "Dark Blue";
            }
            ThemeInfo themeInfo = ThemeManager.getActiveTheme();
            if (dayThemeName.equals(nightThemeName)) {
                if (themeInfo.isDark() || dayThemeName.equals("Dark Blue") || dayThemeName.equals("Night")) {
                    dayThemeName = "Blue";
                } else {
                    nightThemeName = "Dark Blue";
                }
            }

            boolean toDark = dayThemeName.equals(themeInfo.getKey());
            if (toDark) {
                themeInfo = ThemeManager.getTheme(nightThemeName);
                sunDrawable.setCustomEndFrame(36);
            } else {
                themeInfo = ThemeManager.getTheme(dayThemeName);
                sunDrawable.setCustomEndFrame(0);
            }
            playAnimation();
            if (Theme.selectedAutoNightType != Theme.AUTO_NIGHT_TYPE_NONE) {
                Toast.makeText(context, Lang.getString(context, "AutoNightModeOff", R.string.AutoNightModeOff), Toast.LENGTH_SHORT).show();
                Theme.selectedAutoNightType = Theme.AUTO_NIGHT_TYPE_NONE;
                ThemeManager.saveAutoNightThemeConfig();
                ThemeManager.cancelAutoNightThemeCallbacks();
            }
            sendSwitchThemeEvent(this, themeInfo, toDark);
        });
    }

    private static void sendSwitchThemeEvent(RLottieImageView darkThemeView, ThemeInfo themeInfo, boolean toDark) {
        int[] pos = new int[2];
        darkThemeView.getLocationInWindow(pos);
        pos[0] += darkThemeView.getMeasuredWidth() / 2;
        pos[1] += darkThemeView.getMeasuredHeight() / 2;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false, pos, -1, toDark, darkThemeView);
    }

    private static boolean isCurrentThemeDay(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
        String dayThemeName = preferences.getString("lastDayTheme", "Blue");
        if (ThemeManager.getTheme(dayThemeName) == null) {
            dayThemeName = "Blue";
        }
        String nightThemeName = preferences.getString("lastDarkTheme", "Dark Blue");
        if (ThemeManager.getTheme(nightThemeName) == null) {
            nightThemeName = "Dark Blue";
        }
        ThemeInfo themeInfo = ThemeManager.getActiveTheme();
        if (dayThemeName.equals(nightThemeName) && themeInfo.isDark()) {
            dayThemeName = "Blue";
        }
        return dayThemeName.equals(themeInfo.getKey());
    }
}
