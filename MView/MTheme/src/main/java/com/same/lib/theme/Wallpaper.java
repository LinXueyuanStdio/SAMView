package com.same.lib.theme;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/26
 * @description null
 * @usage null
 */
public class Wallpaper {

    public long id;
    public int flags;
    public boolean creator;
    public boolean isDefault;
    public boolean pattern;
    public boolean dark;
    public long access_hash;
    public String slug;
    public Document document;
    public WallpaperSettings settings;

    public static class WallpaperSettings {
        public int flags;
        public boolean blur;
        public boolean motion;
        public int background_color;
        public int second_background_color;
        public int intensity;
        public int rotation;
    }
}