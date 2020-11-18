package com.same.lib.theme;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/24
 * @description null
 * @usage null
 */
public class Skin {
    public int flags;
    public boolean creator;
    public boolean isDefault;
    public long id;
    public long access_hash;
    public String slug;
    public String title;
    public Document document;
    public SkinSettings settings;
    public int installs_count;

    public static class SkinSettings {
        public int flags;
        public BaseTheme base_theme;
        public int accent_color;
        public int message_top_color;
        public int message_bottom_color;
        public Wallpaper wallpaper;
    }

    public String toJson() {
        return "";
    }

    public static Skin fromJson(String json) {
        return new Skin();
    }
}
