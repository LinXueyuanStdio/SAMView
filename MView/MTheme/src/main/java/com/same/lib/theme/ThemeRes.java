package com.same.lib.theme;

import java.util.ArrayList;
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

    public static void install(AbsTheme absTheme) {
        themes.add(absTheme);
    }
}
