package com.same.ui;

import android.util.Log;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/3/10
 * @description null
 * @usage null
 */
public class FileLog {
    public static void e(Object text) {
        Log.e("log", text+"");
    }
    public static void e(Object text, Object x) {
        Log.e("log", text+" " + x);
    }
    public static void d(Object text) {
        Log.e("log", text+"");
    }
}
