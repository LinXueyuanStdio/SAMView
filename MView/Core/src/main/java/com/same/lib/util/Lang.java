package com.same.lib.util;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/12/26
 * @description null
 * @usage null
 */
public class Lang {
    public interface ILang {
        String getString(String key, @StringRes int string);
        String getTranslitString(String src);
    }

    private static ILang lang;

    public static void install(ILang lang) {
        Lang.lang = lang;
    }

    public static String getString(Context context, String key, @StringRes int string) {
        if (lang != null) {
            return lang.getString(key, string);
        } else {
            return context.getString(string);
        }
    }
    @Nullable
    public static String getTranslitString(Context context, String src) {
        if (lang != null) {
            return lang.getTranslitString(src);
        } else {
            return null;
        }
    }
}
