package com.same.lib.drawable;

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
    public static int getColor(String key) {
        return randomColor();
    }

    private static int randomColor() {
        int random = new Random().nextInt(360);
        return Color.HSVToColor(new float[]{random, 0.5f, 0.9f});
    }
}
