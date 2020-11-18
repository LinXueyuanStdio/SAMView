package com.same.lib.theme;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/26
 * @description null
 * @usage null
 */
public class ThemeAccentList {
    int version;
    int count;
    List<ThemeAccent> list = new ArrayList<>();

    public static ThemeAccentList fromJson(String json) {
        //        Base64.decode(json, Base64.NO_WRAP | Base64.NO_PADDING);
        return new ThemeAccentList();
    }

    public String toJson() {
        //        Base64.encodeToString(toByteArray(), Base64.NO_WRAP | Base64.NO_PADDING)
        return "";
    }
}
