package com.same.lib.theme;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/24
 * @description null
 * @usage null
 */
public class Skin {
    public long id;
    public String title;
    public SkinSettings settings;

    public static class SkinSettings {
        public BaseTheme base_theme;
        public int accent_color;
    }

    public String toJson() {
        return "";
    }

    public static Skin fromJson(String json) {
        return new Skin();
    }
}
