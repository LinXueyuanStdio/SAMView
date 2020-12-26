package com.same.lib.theme;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class ColorApply {
    private static List<ColorDelegate> delegates = new ArrayList<>();

    public interface ColorDelegate {
        void apply(MyThemeDescription description, Context context, int color, boolean useDefault, boolean save);
    }

    public static void install(ColorDelegate colorDelegate) {
        delegates.add(colorDelegate);
    }

    public static void reset() {
        delegates.clear();
    }

    public static void setColor(MyThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        for (ColorDelegate colorDelegate : delegates) {
            colorDelegate.apply(description, context, color, useDefault, save);
        }
    }
}
