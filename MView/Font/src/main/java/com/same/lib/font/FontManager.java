package com.same.lib.font;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;

import java.util.Hashtable;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/14
 * @description null
 * @usage null
 */
public class FontManager {
    private static final Hashtable<String, Typeface> typefaceCache = new Hashtable<>();

    public static Typeface getMediumTypeface(Context context) {
        return getTypeface(context, "fonts/rmedium.ttf");
    }

    public static Typeface getItalicTypeface(Context context) {
        return getTypeface(context, "fonts/ritalic.ttf");
    }

    public static Typeface getTypeface(Context context, String assetPath) {
        synchronized (typefaceCache) {
            if (!typefaceCache.containsKey(assetPath)) {
                try {
                    Typeface t;
                    if (Build.VERSION.SDK_INT >= 26) {
                        Typeface.Builder builder = new Typeface.Builder(context.getAssets(), assetPath);
                        if (assetPath.contains("medium")) {
                            builder.setWeight(700);
                        }
                        if (assetPath.contains("italic")) {
                            builder.setItalic(true);
                        }
                        t = builder.build();
                    } else {
                        t = Typeface.createFromAsset(context.getAssets(), assetPath);
                    }
                    typefaceCache.put(assetPath, t);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return typefaceCache.get(assetPath);
        }
    }
}
