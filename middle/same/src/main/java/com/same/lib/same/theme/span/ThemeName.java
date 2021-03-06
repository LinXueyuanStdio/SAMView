package com.same.lib.same.theme.span;

import android.content.Context;

import com.same.lib.theme.Skin;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.theme.ThemeManager;
import com.same.lib.util.Lang;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class ThemeName {
    public static String getName(Context context, ThemeInfo themeInfo) {
        String name = themeInfo.name;
        if ("Blue".equals(name)) {
            return Lang.getString(context, "ThemeClassic", com.same.lib.R.string.ThemeClassic);
        } else if ("Dark Blue".equals(name)) {
            return Lang.getString(context, "ThemeDark", com.same.lib.R.string.ThemeDark);
        } else if ("Arctic Blue".equals(name)) {
            return Lang.getString(context, "ThemeArcticBlue", com.same.lib.R.string.ThemeArcticBlue);
        } else if ("Day".equals(name)) {
            return Lang.getString(context, "ThemeDay", com.same.lib.R.string.ThemeDay);
        } else if ("Night".equals(name)) {
            return Lang.getString(context, "ThemeNight", com.same.lib.R.string.ThemeNight);
        }
        Skin info = themeInfo.info;
        return info != null ? info.title : name;
    }

    public static String getCurrentThemeName(Context context) {
        String text = getName(context, ThemeManager.getCurrentDayTheme());
        if (text.toLowerCase().endsWith(".attheme")) {
            text = text.substring(0, text.lastIndexOf('.'));
        }
        return text;
    }

    public static String getCurrentNightThemeName(Context context) {
        if (ThemeManager.getCurrentNightTheme() == null) {
            return "";
        }
        String text = getName(context, ThemeManager.getCurrentNightTheme());
        if (text.toLowerCase().endsWith(".attheme")) {
            text = text.substring(0, text.lastIndexOf('.'));
        }
        return text;
    }
}
