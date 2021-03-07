package com.same.lib.checkbox;

import android.content.Context;
import android.graphics.Typeface;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/12/26
 * @description null
 * @usage null
 */
public class CheckboxFont {
    public interface IFont {
        Typeface getTypeface(Context context, String assetPath);
    }

    private static IFont font;

    public static void install(IFont font) {
        CheckboxFont.font = font;
    }

    public static Typeface getMediumTypeface(Context context) {
        return getTypeface(context, "fonts/rmedium.ttf");
    }

    public static Typeface getTypeface(Context context, String assetPath) {
        return font.getTypeface(context, assetPath);
    }
}
