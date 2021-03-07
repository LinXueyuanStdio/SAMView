package com.same.ui.theme.span;

import com.same.lib.theme.ThemeManager;

import java.util.HashMap;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class ThemePreview {
    public static int getPreviewColor(HashMap<String, Integer> colors, String key) {
        Integer color = colors.get(key);
        if (color == null) {
            color = ThemeManager.getDefaultColors().get(key);
        }
        return color;
    }
}
