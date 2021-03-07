package com.same.lib.theme;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/29
 * @description null
 * @usage null
 */
public class ThemeRes {
    public static List<AbsTheme> themes = new ArrayList<>();

    public static void installAndApply(Context context, AbsTheme... absThemes) {
        themes.addAll(Arrays.asList(absThemes));
        for (AbsTheme absTheme : themes) {
            absTheme.createResources(context);
        }
    }
}
