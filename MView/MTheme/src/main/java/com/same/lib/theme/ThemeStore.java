package com.same.lib.theme;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/3/7
 * @description null
 * @usage null
 */
public class ThemeStore {
    public interface Callback {
        void saveTheme(ThemeInfo themeInfo, ThemeAccent accent, boolean nightTheme, boolean b);

        void saveThemeToServer(ThemeInfo themeInfo, ThemeAccent accent);
    }

    private static Callback callback;

    public static void install(Callback callback) {
        ThemeStore.callback = callback;
    }

    public static void saveTheme(ThemeInfo themeInfo, ThemeAccent accent, boolean nightTheme, boolean b) {
        if (callback != null) {
            callback.saveTheme(themeInfo, accent, nightTheme, b);
        }
    }

    public static void saveThemeToServer(ThemeInfo themeInfo, ThemeAccent accent) {
        if (callback != null) {
            callback.saveThemeToServer(themeInfo, accent);
        }
    }
}
