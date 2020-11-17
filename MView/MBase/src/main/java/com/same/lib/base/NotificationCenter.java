package com.same.lib.base;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/24
 * @description null
 * @usage null
 */
public class NotificationCenter {
    public static int stopAllHeavyOperations = 0;
    public static int startAllHeavyOperations = 0;
    public static int didSetNewTheme = 0;
    public static int needCheckSystemBarColors = 0;
    public static int goingToPreviewTheme = 0;
    public static int needSetDayNightTheme = 0;
    public static int didSetNewWallpapper = 0;
    public static int themeAccentListUpdated = 0;

    public static void postNotificationName(int id) {}

    public static void postNotificationName(int id, boolean yes) {}

    public static void postNotificationName(int id, Object info) {}
}
