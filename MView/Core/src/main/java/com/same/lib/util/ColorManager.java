package com.same.lib.util;

import android.graphics.Color;

import java.util.Random;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/17
 * @description null
 * @usage null
 */
public class ColorManager {
    public interface ColorEngine {
        int getColor(String key);
    }

    private static ColorEngine engine = null;

    public static void install(ColorEngine engine) {
        ColorManager.engine = engine;
    }

    /**
     * @param key from {com.same.lib.util.KeyHub}
     * @return color int of key using your theme engine
     */
    public static int getColor(String key) {
        if (engine == null) {
            return randomColor();
        } else {
            return engine.getColor(key);
        }
    }

    public static int randomColor() {
        int random = new Random().nextInt(360);
        return Color.HSVToColor(new float[]{random, 0.5f, 0.9f});
    }
}
