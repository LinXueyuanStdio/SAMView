package com.same.lib.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;

import com.same.lib.AbsTheme;
import com.same.lib.R;
import com.same.lib.drawable.BackgroundGradientDrawable;
import com.same.lib.helper.Bitmaps;
import com.same.lib.util.AndroidUtilities;
import com.same.lib.util.NotificationCenter;
import com.same.lib.util.SharedConfig;
import com.timecat.component.locale.time.SunDate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import static com.same.lib.theme.Theme.AUTO_NIGHT_TYPE_AUTOMATIC;
import static com.same.lib.theme.Theme.AUTO_NIGHT_TYPE_NONE;
import static com.same.lib.theme.Theme.AUTO_NIGHT_TYPE_SCHEDULED;
import static com.same.lib.theme.Theme.AUTO_NIGHT_TYPE_SYSTEM;
import static com.same.lib.theme.Theme.DEFALT_THEME_ACCENT_ID;
import static com.same.lib.theme.Theme.DEFAULT_BACKGROUND_SLUG;
import static com.same.lib.theme.Theme.LIGHT_SENSOR_THEME_SWITCH_DELAY;
import static com.same.lib.theme.Theme.LIGHT_SENSOR_THEME_SWITCH_NEAR_DELAY;
import static com.same.lib.theme.Theme.LIGHT_SENSOR_THEME_SWITCH_NEAR_THRESHOLD;
import static com.same.lib.theme.Theme.autoNightBrighnessThreshold;
import static com.same.lib.theme.Theme.autoNightCityName;
import static com.same.lib.theme.Theme.autoNightDayEndTime;
import static com.same.lib.theme.Theme.autoNightDayStartTime;
import static com.same.lib.theme.Theme.autoNightLastSunCheckDay;
import static com.same.lib.theme.Theme.autoNightLocationLatitude;
import static com.same.lib.theme.Theme.autoNightLocationLongitude;
import static com.same.lib.theme.Theme.autoNightScheduleByLocation;
import static com.same.lib.theme.Theme.autoNightSunriseTime;
import static com.same.lib.theme.Theme.autoNightSunsetTime;
import static com.same.lib.theme.Theme.calcBackgroundColor;
import static com.same.lib.theme.Theme.currentColors;
import static com.same.lib.theme.Theme.currentColorsNoAccent;
import static com.same.lib.theme.Theme.currentDayTheme;
import static com.same.lib.theme.Theme.currentNightTheme;
import static com.same.lib.theme.Theme.currentTheme;
import static com.same.lib.theme.Theme.defaultColors;
import static com.same.lib.theme.Theme.defaultTheme;
import static com.same.lib.theme.Theme.hasPreviousTheme;
import static com.same.lib.theme.Theme.hsvTemp1Local;
import static com.same.lib.theme.Theme.hsvTemp2Local;
import static com.same.lib.theme.Theme.hsvTemp3Local;
import static com.same.lib.theme.Theme.hsvTemp4Local;
import static com.same.lib.theme.Theme.hsvTemp5Local;
import static com.same.lib.theme.Theme.isApplyingAccent;
import static com.same.lib.theme.Theme.isInNigthMode;
import static com.same.lib.theme.Theme.key_actionBarDefault;
import static com.same.lib.theme.Theme.key_actionBarDefaultIcon;
import static com.same.lib.theme.Theme.key_chat_inBubble;
import static com.same.lib.theme.Theme.key_chat_messagePanelBackground;
import static com.same.lib.theme.Theme.key_chat_messagePanelIcons;
import static com.same.lib.theme.Theme.key_chat_outBubble;
import static com.same.lib.theme.Theme.key_chat_outBubbleGradient;
import static com.same.lib.theme.Theme.key_chat_serviceBackground;
import static com.same.lib.theme.Theme.key_chat_wallpaper;
import static com.same.lib.theme.Theme.key_chat_wallpaper_gradient_rotation;
import static com.same.lib.theme.Theme.key_chat_wallpaper_gradient_to;
import static com.same.lib.theme.Theme.lastBrightnessValue;
import static com.same.lib.theme.Theme.lastDelayUpdateTime;
import static com.same.lib.theme.Theme.lastLoadingCurrentThemeTime;
import static com.same.lib.theme.Theme.lastLoadingThemesTime;
import static com.same.lib.theme.Theme.lastThemeSwitchTime;
import static com.same.lib.theme.Theme.lightSensor;
import static com.same.lib.theme.Theme.lightSensorRegistered;
import static com.same.lib.theme.Theme.loadingCurrentTheme;
import static com.same.lib.theme.Theme.loadingRemoteThemes;
import static com.same.lib.theme.Theme.otherThemes;
import static com.same.lib.theme.Theme.previousTheme;
import static com.same.lib.theme.Theme.reloadWallpaper;
import static com.same.lib.theme.Theme.remoteThemesHash;
import static com.same.lib.theme.Theme.selectedAutoNightType;
import static com.same.lib.theme.Theme.sensorManager;
import static com.same.lib.theme.Theme.setDrawableColor;
import static com.same.lib.theme.Theme.shouldDrawGradientIcons;
import static com.same.lib.theme.Theme.switchDayBrightnessRunnable;
import static com.same.lib.theme.Theme.switchDayRunnableScheduled;
import static com.same.lib.theme.Theme.switchNightBrightnessRunnable;
import static com.same.lib.theme.Theme.switchNightRunnableScheduled;
import static com.same.lib.theme.Theme.switchNightThemeDelay;
import static com.same.lib.theme.Theme.switchingNightTheme;
import static com.same.lib.theme.Theme.themedWallpaper;
import static com.same.lib.theme.Theme.themedWallpaperFileOffset;
import static com.same.lib.theme.Theme.themedWallpaperLink;
import static com.same.lib.theme.Theme.themes;
import static com.same.lib.theme.Theme.themesDict;
import static com.same.lib.theme.Theme.wallpaper;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/26
 * @description null
 * @usage null
 */
public class ThemeManager {
    //region 主题管理
    public static void applyPreviousTheme(Context context) {
        if (previousTheme == null) {
            return;
        }
        hasPreviousTheme = false;
        if (isInNigthMode && currentNightTheme != null) {
            applyTheme(context, currentNightTheme, true, false, true);
        } else if (!isApplyingAccent) {
            applyTheme(context, previousTheme, true, false, false);
        }
        isApplyingAccent = false;
        previousTheme = null;
        checkAutoNightThemeConditions(context);
    }

    public static void clearPreviousTheme() {
        if (previousTheme == null) {
            return;
        }
        hasPreviousTheme = false;
        isApplyingAccent = false;
        previousTheme = null;
    }

    static void sortThemes() {
        Collections.sort(themes, (o1, o2) -> {
            if (o1.pathToFile == null && o1.assetName == null) {
                return -1;
            } else if (o2.pathToFile == null && o2.assetName == null) {
                return 1;
            }
            return o1.name.compareTo(o2.name);
        });
    }

    public static void applyThemeTemporary(Context context, ThemeInfo themeInfo, boolean accent) {
        previousTheme = getCurrentTheme();
        hasPreviousTheme = true;
        isApplyingAccent = accent;
        applyTheme(context, themeInfo, false, false, false);
    }

    public static ThemeInfo fillThemeValues(Context context, File file, String themeName, Skin theme) {
        try {
            ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.name = themeName;
            themeInfo.info = theme;
            themeInfo.pathToFile = file.getAbsolutePath();
            themeInfo.account = 0;

            String[] wallpaperLink = new String[1];
            getThemeFileValues(context, new File(themeInfo.pathToFile), null, wallpaperLink);

            if (!TextUtils.isEmpty(wallpaperLink[0])) {
                String ling = wallpaperLink[0];
                themeInfo.pathToWallpaper = new File(AndroidUtilities.getFilesDirFixed(), AndroidUtilities.MD5(ling) + ".wp").getAbsolutePath();
                try {
                    Uri data = Uri.parse(ling);
                    themeInfo.slug = data.getQueryParameter("slug");
                    String mode = data.getQueryParameter("mode");
                    if (mode != null) {
                        mode = mode.toLowerCase();
                        String[] modes = mode.split(" ");
                        if (modes != null && modes.length > 0) {
                            for (int a = 0; a < modes.length; a++) {
                                if ("blur".equals(modes[a])) {
                                    themeInfo.isBlured = true;
                                } else if ("motion".equals(modes[a])) {
                                    themeInfo.isMotion = true;
                                }
                            }
                        }
                    }
                    String intensity = data.getQueryParameter("intensity");
                    if (!TextUtils.isEmpty(intensity)) {
                        try {
                            String bgColor = data.getQueryParameter("bg_color");
                            if (!TextUtils.isEmpty(bgColor)) {
                                themeInfo.patternBgColor = Integer.parseInt(bgColor, 16) | 0xff000000;
                                if (bgColor.length() > 6) {
                                    themeInfo.patternBgGradientColor = Integer.parseInt(bgColor.substring(7), 16) | 0xff000000;
                                }
                            }
                        } catch (Exception ignore) {

                        }
                        try {
                            String rotation = data.getQueryParameter("rotation");
                            if (!TextUtils.isEmpty(rotation)) {
                                themeInfo.patternBgGradientRotation = AndroidUtilities.parseInt(rotation);
                            }
                        } catch (Exception ignore) {

                        }

                        if (!TextUtils.isEmpty(intensity)) {
                            themeInfo.patternIntensity = AndroidUtilities.parseInt(intensity);
                        }
                        if (themeInfo.patternIntensity == 0) {
                            themeInfo.patternIntensity = 50;
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                themedWallpaperLink = null;
            }

            return themeInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 应用主题文件
     * @param file 主题文件
     * @param themeName 主题名字 xxx.attheme 文件名
     * @param theme Skin
     * @param temporary 暂时的
     * @return Theme.ThemeInfo
     */
    public static ThemeInfo applyThemeFile(Context context, File file, String themeName, Skin theme, boolean temporary) {
        try {
            if (!themeName.toLowerCase().endsWith(".attheme")) {
                themeName += ".attheme";
            }
            if (temporary) {
                NotificationCenter.postNotificationName(NotificationCenter.goingToPreviewTheme);
                ThemeInfo themeInfo = new ThemeInfo();
                themeInfo.name = themeName;
                themeInfo.info = theme;
                themeInfo.pathToFile = file.getAbsolutePath();
                themeInfo.account = 0;
                applyThemeTemporary(context, themeInfo, false);
                return themeInfo;
            } else {
                String key;
                File finalFile;
                if (theme != null) {
                    key = "remote" + theme.id;
                    finalFile = new File(AndroidUtilities.getFilesDirFixed(), key + ".attheme");
                } else {
                    key = themeName;
                    finalFile = new File(AndroidUtilities.getFilesDirFixed(), key);
                }
                if (!AndroidUtilities.copyFile(file, finalFile)) {
                    applyPreviousTheme(context);
                    return null;
                }

                previousTheme = null;
                hasPreviousTheme = false;
                isApplyingAccent = false;

                ThemeInfo themeInfo = themesDict.get(key);
                if (themeInfo == null) {
                    themeInfo = new ThemeInfo();
                    themeInfo.name = themeName;
                    themeInfo.account = 0;
                    themes.add(themeInfo);
                    otherThemes.add(themeInfo);
                    sortThemes();
                } else {
                    themesDict.remove(key);
                }
                themeInfo.info = theme;
                themeInfo.pathToFile = finalFile.getAbsolutePath();
                themesDict.put(themeInfo.getKey(), themeInfo);
                saveOtherThemes(context, true);

                applyTheme(context, themeInfo, true, true, false);
                return themeInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ThemeInfo getTheme(String key) {
        return themesDict.get(key);
    }

    public static void applyTheme(Context context, ThemeInfo themeInfo) {
        applyTheme(context, themeInfo, true, true, false);
    }

    public static void applyTheme(Context context, ThemeInfo themeInfo, boolean nightTheme) {
        applyTheme(context, themeInfo, true, true, nightTheme);
    }

    static void applyTheme(Context context, ThemeInfo themeInfo, boolean save, boolean removeWallpaperOverride, final boolean nightTheme) {
        if (themeInfo == null) {
            return;
        }
        AndroidUtilities.destroyThemeEditor();
        try {
            if (themeInfo.pathToFile != null || themeInfo.assetName != null) {
                if (!nightTheme && save) {
                    SharedPreferences preferences = AndroidUtilities.getGlobalMainSettings();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("theme", themeInfo.getKey());
                    editor.commit();
                }
                String[] wallpaperLink = new String[1];
                if (themeInfo.assetName != null) {
                    currentColorsNoAccent = getThemeFileValues(context, null, themeInfo.assetName, null);
                } else {
                    currentColorsNoAccent = getThemeFileValues(context, new File(themeInfo.pathToFile), null, wallpaperLink);
                }
                Integer offset = currentColorsNoAccent.get("wallpaperFileOffset");
                themedWallpaperFileOffset = offset != null ? offset : -1;
                if (!TextUtils.isEmpty(wallpaperLink[0])) {
                    themedWallpaperLink = wallpaperLink[0];
                    String newPathToFile = new File(AndroidUtilities.getFilesDirFixed(), AndroidUtilities.MD5(themedWallpaperLink) + ".wp").getAbsolutePath();
                    try {
                        if (themeInfo.pathToWallpaper != null && !themeInfo.pathToWallpaper.equals(newPathToFile)) {
                            new File(themeInfo.pathToWallpaper).delete();
                        }
                    } catch (Exception ignore) {

                    }
                    themeInfo.pathToWallpaper = newPathToFile;
                    try {
                        Uri data = Uri.parse(themedWallpaperLink);
                        themeInfo.slug = data.getQueryParameter("slug");

                        String mode = data.getQueryParameter("mode");
                        if (mode != null) {
                            mode = mode.toLowerCase();
                            String[] modes = mode.split(" ");
                            if (modes != null && modes.length > 0) {
                                for (int a = 0; a < modes.length; a++) {
                                    if ("blur".equals(modes[a])) {
                                        themeInfo.isBlured = true;
                                    } else if ("motion".equals(modes[a])) {
                                        themeInfo.isMotion = true;
                                    }
                                }
                            }
                        }
                        int intensity = AndroidUtilities.parseInt(data.getQueryParameter("intensity"));
                        themeInfo.patternBgGradientRotation = 45;
                        try {
                            String bgColor = data.getQueryParameter("bg_color");
                            if (!TextUtils.isEmpty(bgColor)) {
                                themeInfo.patternBgColor = Integer.parseInt(bgColor.substring(0, 6), 16) | 0xff000000;
                                if (bgColor.length() > 6) {
                                    themeInfo.patternBgGradientColor = Integer.parseInt(bgColor.substring(7), 16) | 0xff000000;
                                }
                            }
                        } catch (Exception ignore) {

                        }
                        try {
                            String rotation = data.getQueryParameter("rotation");
                            if (!TextUtils.isEmpty(rotation)) {
                                themeInfo.patternBgGradientRotation = AndroidUtilities.parseInt(rotation);
                            }
                        } catch (Exception ignore) {

                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if (themeInfo.pathToWallpaper != null) {
                            new File(themeInfo.pathToWallpaper).delete();
                        }
                    } catch (Exception ignore) {

                    }
                    themeInfo.pathToWallpaper = null;
                    themedWallpaperLink = null;
                }
            } else {
                if (!nightTheme && save) {
                    SharedPreferences preferences = AndroidUtilities.getGlobalMainSettings();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("theme");
                    editor.apply();
                }
                currentColorsNoAccent.clear();
                themedWallpaperFileOffset = 0;
                themedWallpaperLink = null;
                wallpaper = null;
                themedWallpaper = null;
            }
            if (!nightTheme && previousTheme == null) {
                currentDayTheme = themeInfo;
                if (isCurrentThemeNight()) {
                    switchNightThemeDelay = 2000;
                    lastDelayUpdateTime = SystemClock.elapsedRealtime();
                }
            }
            currentTheme = themeInfo;
            refreshThemeColors(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (previousTheme == null && save && !switchingNightTheme) {
            //            MessagesController.getInstance(themeInfo.account).saveTheme(themeInfo, themeInfo.getAccent(false), nightTheme, false);
        }
    }

    static boolean useBlackText(int color1, int color2) {
        float r1 = Color.red(color1) / 255.0f;
        float r2 = Color.red(color2) / 255.0f;
        float g1 = Color.green(color1) / 255.0f;
        float g2 = Color.green(color2) / 255.0f;
        float b1 = Color.blue(color1) / 255.0f;
        float b2 = Color.blue(color2) / 255.0f;
        float r = (r1 * 0.5f + r2 * 0.5f);
        float g = (g1 * 0.5f + g2 * 0.5f);
        float b = (b1 * 0.5f + b2 * 0.5f);

        float lightness = 0.2126f * r + 0.7152f * g + 0.0722f * b;
        float lightness2 = 0.2126f * r1 + 0.7152f * g1 + 0.0722f * b1;
        return lightness > 0.705f || lightness2 > 0.705f;
    }

    public static void refreshThemeColors(Context context) {
        currentColors.clear();
        currentColors.putAll(currentColorsNoAccent);
        shouldDrawGradientIcons = true;
        ThemeAccent accent = currentTheme.getAccent(false);
        if (accent != null) {
            shouldDrawGradientIcons = accent.fillAccentColors(currentColorsNoAccent, currentColors);
        }
        reloadWallpaper(context);
        for (AbsTheme absTheme:ThemeRes.themes) {
            absTheme.applyResources(context);
        }
        AndroidUtilities.runOnUIThread(() -> NotificationCenter.postNotificationName(NotificationCenter.didSetNewTheme, false));
    }

    public static int changeColorAccent(ThemeInfo themeInfo, int accent, int color) {
        if (accent == 0 || themeInfo.accentBaseColor == 0 || accent == themeInfo.accentBaseColor || themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID) {
            return color;
        }
        float[] hsvTemp3 = getTempHsv(3);
        float[] hsvTemp4 = getTempHsv(4);

        Color.colorToHSV(themeInfo.accentBaseColor, hsvTemp3);
        Color.colorToHSV(accent, hsvTemp4);
        return changeColorAccent(hsvTemp3, hsvTemp4, color, themeInfo.isDark());
    }

    static float[] getTempHsv(int num) {
        ThreadLocal<float[]> local;
        switch (num) {
            case 1:
                local = hsvTemp1Local;
                break;
            case 2:
                local = hsvTemp2Local;
                break;
            case 3:
                local = hsvTemp3Local;
                break;
            case 4:
                local = hsvTemp4Local;
                break;
            case 5:
            default:
                local = hsvTemp5Local;
                break;
        }
        float[] hsvTemp = local.get();
        if (hsvTemp == null) {
            hsvTemp = new float[3];
            local.set(hsvTemp);
        }
        return hsvTemp;
    }

    static int getAccentColor(float[] baseHsv, int baseColor, int elementColor) {
        float[] hsvTemp3 = getTempHsv(3);
        float[] hsvTemp4 = getTempHsv(4);
        Color.colorToHSV(baseColor, hsvTemp3);
        Color.colorToHSV(elementColor, hsvTemp4);

        float dist = Math.min(1.5f * hsvTemp3[1] / baseHsv[1], 1f);

        hsvTemp3[0] = hsvTemp4[0] - hsvTemp3[0] + baseHsv[0];
        hsvTemp3[1] = hsvTemp4[1] * baseHsv[1] / hsvTemp3[1];
        hsvTemp3[2] = (hsvTemp4[2] / hsvTemp3[2] + dist - 1f) * baseHsv[2] / dist;
        if (hsvTemp3[2] < 0.3f) {
            return elementColor;
        }
        return Color.HSVToColor(255, hsvTemp3);
    }

    public static int changeColorAccent(int color) {
        ThemeAccent accent = currentTheme.getAccent(false);
        return changeColorAccent(currentTheme, accent != null ? accent.accentColor : 0, color);
    }

    public static int changeColorAccent(float[] baseHsv, float[] accentHsv, int color, boolean isDarkTheme) {
        float[] colorHsv = getTempHsv(5);
        Color.colorToHSV(color, colorHsv);

        final float diffH = Math.min(Math.abs(colorHsv[0] - baseHsv[0]), Math.abs(colorHsv[0] - baseHsv[0] - 360f));
        if (diffH > 30f) {
            return color;
        }

        float dist = Math.min(1.5f * colorHsv[1] / baseHsv[1], 1f);

        colorHsv[0] = colorHsv[0] + accentHsv[0] - baseHsv[0];
        colorHsv[1] = colorHsv[1] * accentHsv[1] / baseHsv[1];
        colorHsv[2] = colorHsv[2] * (1f - dist + dist * accentHsv[2] / baseHsv[2]);

        int newColor = Color.HSVToColor(Color.alpha(color), colorHsv);

        float origBrightness = AndroidUtilities.computePerceivedBrightness(color);
        float newBrightness = AndroidUtilities.computePerceivedBrightness(newColor);

        // We need to keep colors lighter in dark themes and darker in light themes
        boolean needRevertBrightness = isDarkTheme ? origBrightness > newBrightness : origBrightness < newBrightness;

        if (needRevertBrightness) {
            float amountOfNew = 0.6f;
            float fallbackAmount = (1f - amountOfNew) * origBrightness / newBrightness + amountOfNew;
            newColor = changeBrightness(newColor, fallbackAmount);
        }

        return newColor;
    }

    static int changeBrightness(int color, float amount) {
        int r = (int) (Color.red(color) * amount);
        int g = (int) (Color.green(color) * amount);
        int b = (int) (Color.blue(color) * amount);

        r = r < 0 ? 0 : Math.min(r, 255);
        g = g < 0 ? 0 : Math.min(g, 255);
        b = b < 0 ? 0 : Math.min(b, 255);
        return Color.argb(Color.alpha(color), r, g, b);
    }

    public static void onUpdateThemeAccents(Context context) {
        refreshThemeColors(context);
    }

    public static boolean deleteThemeAccent(Context context, ThemeInfo theme, ThemeAccent accent, boolean save) {
        if (accent == null || theme == null || theme.themeAccents == null) {
            return false;
        }
        boolean current = accent.id == theme.currentAccentId;
        File wallpaperFile = accent.getPathToWallpaper();
        if (wallpaperFile != null) {
            wallpaperFile.delete();
        }
        theme.themeAccentsMap.remove(accent.id);
        theme.themeAccents.remove(accent);
        if (accent.info != null) {
            theme.accentsByThemeId.remove(accent.info.id);
        }
        if (accent.overrideWallpaper != null) {
            accent.overrideWallpaper.delete();
        }
        if (current) {
            ThemeAccent themeAccent = theme.themeAccents.get(0);
            theme.setCurrentAccentId(themeAccent.id);
        }
        if (save) {
            saveThemeAccents(context, theme, true, false, false, false);
            if (accent.info != null) {
                //                MessagesController.getInstance(accent.account).saveTheme(theme, accent, current && theme == currentNightTheme, true);
            }
        }
        return current;
    }

    public static void saveThemeAccents(Context context, ThemeInfo theme, boolean save, boolean remove, boolean indexOnly, boolean upload) {
        saveThemeAccents(context, theme, save, remove, indexOnly, upload, false);
    }

    /**
     * TODO
     * @param theme
     * @param save
     * @param remove
     * @param indexOnly
     * @param upload
     * @param migration
     */
    public static void saveThemeAccents(Context context, ThemeInfo theme, boolean save, boolean remove, boolean indexOnly, boolean upload, boolean migration) {
        if (save) {
            SharedPreferences preferences = AndroidUtilities.getThemeConfig();
            SharedPreferences.Editor editor = preferences.edit();
            if (!indexOnly) {
                int N = theme.themeAccents.size();
                int count = Math.max(0, N - theme.defaultAccentCount);
                ThemeAccentList themeAccentList = new ThemeAccentList();
                themeAccentList.version = 5;
                themeAccentList.count = count;
                for (int a = 0; a < N; a++) {
                    ThemeAccent accent = theme.themeAccents.get(a);
                    if (accent.id < 100) {
                        continue;
                    }
                    themeAccentList.list.add(accent);
                }
                editor.putString("accents_" + theme.assetName, themeAccentList.toJson());
                if (!migration) {
                    NotificationCenter.postNotificationName(NotificationCenter.themeAccentListUpdated);
                }
                if (upload) {
                    //                    MessagesController.getInstance(0).saveThemeToServer(theme, theme.getAccent(false));
                }
            }
            editor.putInt("accent_current_" + theme.assetName, theme.currentAccentId);
            editor.apply();
        } else {
            if (theme.prevAccentId != -1) {
                if (remove) {
                    ThemeAccent accent = theme.themeAccentsMap.get(theme.currentAccentId);
                    theme.themeAccentsMap.remove(accent.id);
                    theme.themeAccents.remove(accent);
                    if (accent.info != null) {
                        theme.accentsByThemeId.remove(accent.info.id);
                    }
                }
                theme.currentAccentId = theme.prevAccentId;
                ThemeAccent accent = theme.getAccent(false);
                if (accent != null) {
                    theme.overrideWallpaper = accent.overrideWallpaper;
                } else {
                    theme.overrideWallpaper = null;
                }
            }
            if (currentTheme == theme) {
                refreshThemeColors(context);
            }
        }
        theme.prevAccentId = -1;
    }

    static void saveOtherThemes(Context context, boolean full) {
        saveOtherThemes(context, full, false);
    }

    static void saveOtherThemes(Context context, boolean full, boolean migration) {
        SharedPreferences preferences =AndroidUtilities.getThemeConfig();
        SharedPreferences.Editor editor = preferences.edit();
        if (full) {
            JSONArray array = new JSONArray();
            for (int a = 0; a < otherThemes.size(); a++) {
                JSONObject jsonObject = otherThemes.get(a).getSaveJson();
                if (jsonObject != null) {
                    array.put(jsonObject);
                }
            }
            editor.putString("themes2", array.toString());
        }
        for (int a = 0; a < 1; a++) {
            editor.putInt("remoteThemesHash" + (a != 0 ? a : ""), remoteThemesHash[a]);
            editor.putInt("lastLoadingThemesTime" + (a != 0 ? a : ""), lastLoadingThemesTime[a]);
        }

        editor.putInt("lastLoadingCurrentThemeTime", lastLoadingCurrentThemeTime);
        editor.apply();

        if (full) {
            for (int b = 0; b < 5; b++) {
                String key;
                switch (b) {
                    case 0:
                        key = "Blue";
                        break;
                    case 1:
                        key = "Dark Blue";
                        break;
                    case 2:
                        key = "Arctic Blue";
                        break;
                    case 3:
                        key = "Day";
                        break;
                    case 4:
                    default:
                        key = "Night";
                        break;
                }
                ThemeInfo info = themesDict.get(key);
                if (info == null || info.themeAccents == null || info.themeAccents.isEmpty()) {
                    continue;
                }
                saveThemeAccents(context, info, true, false, false, false, migration);
            }
        }
    }

    public static HashMap<String, Integer> getDefaultColors() {
        return defaultColors;
    }

    public static ThemeInfo getPreviousTheme() {
        return previousTheme;
    }

    public static String getCurrentThemeName(Context context) {
        String text = currentDayTheme.getName(context);
        if (text.toLowerCase().endsWith(".attheme")) {
            text = text.substring(0, text.lastIndexOf('.'));
        }
        return text;
    }

    public static String getCurrentNightThemeName(Context context) {
        if (currentNightTheme == null) {
            return "";
        }
        String text = currentNightTheme.getName(context);
        if (text.toLowerCase().endsWith(".attheme")) {
            text = text.substring(0, text.lastIndexOf('.'));
        }
        return text;
    }

    public static ThemeInfo getCurrentTheme() {
        return currentDayTheme != null ? currentDayTheme : defaultTheme;
    }

    public static ThemeInfo getCurrentNightTheme() {
        return currentNightTheme;
    }

    public static boolean isCurrentThemeNight() {
        return currentTheme == currentNightTheme;
    }

    public static ThemeInfo getActiveTheme() {
        return currentTheme;
    }

    static long getAutoNightSwitchThemeDelay() {
        long newTime = SystemClock.elapsedRealtime();
        if (Math.abs(lastThemeSwitchTime - newTime) >= LIGHT_SENSOR_THEME_SWITCH_NEAR_THRESHOLD) {
            return LIGHT_SENSOR_THEME_SWITCH_DELAY;
        }
        return LIGHT_SENSOR_THEME_SWITCH_NEAR_DELAY;
    }

    static final float MAXIMUM_LUX_BREAKPOINT = 500.0f;
    static SensorEventListener ambientSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            if (lux <= 0) {
                lux = 0.1f;
            }
            if (AndroidUtilities.mainInterfacePaused || !AndroidUtilities.isScreenOn) {
                return;
            }
            if (lux > MAXIMUM_LUX_BREAKPOINT) {
                lastBrightnessValue = 1.0f;
            } else {
                lastBrightnessValue = (float) Math.ceil(9.9323f * Math.log(lux) + 27.059f) / 100.0f;
            }
            if (lastBrightnessValue <= autoNightBrighnessThreshold) {
                if (switchDayRunnableScheduled) {
                    switchDayRunnableScheduled = false;
                    AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
                }
                if (!switchNightRunnableScheduled) {
                    switchNightRunnableScheduled = true;
                    AndroidUtilities.runOnUIThread(switchNightBrightnessRunnable, getAutoNightSwitchThemeDelay());
                }
            } else {
                if (switchNightRunnableScheduled) {
                    switchNightRunnableScheduled = false;
                    AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
                }
                if (!switchDayRunnableScheduled) {
                    switchDayRunnableScheduled = true;
                    AndroidUtilities.runOnUIThread(switchDayBrightnessRunnable, getAutoNightSwitchThemeDelay());
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public static void setCurrentNightTheme(ThemeInfo theme) {
        boolean apply = currentTheme == currentNightTheme;
        currentNightTheme = theme;
        if (apply) {
            applyDayNightThemeMaybe(true);
        }
    }

    public static void checkAutoNightThemeConditions(Context context) {
        checkAutoNightThemeConditions(context, false);
    }

    public static void cancelAutoNightThemeCallbacks() {
        if (selectedAutoNightType != AUTO_NIGHT_TYPE_AUTOMATIC) {
            if (switchNightRunnableScheduled) {
                switchNightRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
            }
            if (switchDayRunnableScheduled) {
                switchDayRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
            }
            if (lightSensorRegistered) {
                lastBrightnessValue = 1.0f;
                sensorManager.unregisterListener(ambientSensorListener, lightSensor);
                lightSensorRegistered = false;
            }
        }
    }

    public static void saveAutoNightThemeConfig() {
        SharedPreferences.Editor editor = AndroidUtilities.getGlobalMainSettings().edit();
        editor.putInt("selectedAutoNightType", selectedAutoNightType);
        editor.putBoolean("autoNightScheduleByLocation", autoNightScheduleByLocation);
        editor.putFloat("autoNightBrighnessThreshold", autoNightBrighnessThreshold);
        editor.putInt("autoNightDayStartTime", autoNightDayStartTime);
        editor.putInt("autoNightDayEndTime", autoNightDayEndTime);
        editor.putInt("autoNightSunriseTime", autoNightSunriseTime);
        editor.putString("autoNightCityName", autoNightCityName);
        editor.putInt("autoNightSunsetTime", autoNightSunsetTime);
        editor.putLong("autoNightLocationLatitude3", Double.doubleToRawLongBits(autoNightLocationLatitude));
        editor.putLong("autoNightLocationLongitude3", Double.doubleToRawLongBits(autoNightLocationLongitude));
        editor.putInt("autoNightLastSunCheckDay", autoNightLastSunCheckDay);
        if (currentNightTheme != null) {
            editor.putString("nighttheme", currentNightTheme.getKey());
        } else {
            editor.remove("nighttheme");
        }
        editor.commit();
    }

    static int needSwitchToTheme(Context context) {
        if (selectedAutoNightType == AUTO_NIGHT_TYPE_SCHEDULED) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int time = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
            int timeStart;
            int timeEnd;
            if (autoNightScheduleByLocation) {
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                if (autoNightLastSunCheckDay != day && autoNightLocationLatitude != 10000 && autoNightLocationLongitude != 10000) {
                    int[] t = SunDate.calculateSunriseSunset(autoNightLocationLatitude, autoNightLocationLongitude);
                    autoNightSunriseTime = t[0];
                    autoNightSunsetTime = t[1];
                    autoNightLastSunCheckDay = day;
                    saveAutoNightThemeConfig();
                }
                timeStart = autoNightSunsetTime;
                timeEnd = autoNightSunriseTime;
            } else {
                timeStart = autoNightDayStartTime;
                timeEnd = autoNightDayEndTime;
            }
            if (timeStart < timeEnd) {
                if (timeStart <= time && time <= timeEnd) {
                    return 2;
                } else {
                    return 1;
                }
            } else {
                if (timeStart <= time && time <= 24 * 60 || 0 <= time && time <= timeEnd) {
                    return 2;
                } else {
                    return 1;
                }
            }
        } else if (selectedAutoNightType == AUTO_NIGHT_TYPE_AUTOMATIC) {
            if (lightSensor == null) {
                sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            }
            if (!lightSensorRegistered && lightSensor != null && ambientSensorListener != null) {
                sensorManager.registerListener(ambientSensorListener, lightSensor, 500000);
                lightSensorRegistered = true;
            }
            if (lastBrightnessValue <= autoNightBrighnessThreshold) {
                if (!switchNightRunnableScheduled) {
                    return 2;
                }
            } else {
                if (!switchDayRunnableScheduled) {
                    return 1;
                }
            }
        } else if (selectedAutoNightType == AUTO_NIGHT_TYPE_SYSTEM) {
            Configuration configuration = context.getResources().getConfiguration();
            int currentNightMode = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    return 1;
                case Configuration.UI_MODE_NIGHT_YES:
                    return 2;
            }
        } else if (selectedAutoNightType == AUTO_NIGHT_TYPE_NONE) {
            return 1;
        }
        return 0;
    }

    public static void checkAutoNightThemeConditions(Context context, boolean force) {
        if (previousTheme != null) {
            return;
        }
        if (!force && switchNightThemeDelay > 0) {
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - lastDelayUpdateTime;
            lastDelayUpdateTime = newTime;
            switchNightThemeDelay -= dt;
            if (switchNightThemeDelay > 0) {
                return;
            }
        }
        if (force) {
            if (switchNightRunnableScheduled) {
                switchNightRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
            }
            if (switchDayRunnableScheduled) {
                switchDayRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
            }
        }
        cancelAutoNightThemeCallbacks();
        int switchToTheme = needSwitchToTheme(context);
        if (switchToTheme != 0) {
            applyDayNightThemeMaybe(switchToTheme == 2);
        }
        if (force) {
            lastThemeSwitchTime = 0;
        }
    }

    static void applyDayNightThemeMaybe(boolean night) {
        if (previousTheme != null) {
            return;
        }

        if (night) {
            if (currentTheme != currentNightTheme) {
                isInNigthMode = true;
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                switchingNightTheme = true;
                NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentNightTheme);
                switchingNightTheme = false;
            }
        } else {
            if (currentTheme != currentDayTheme) {
                isInNigthMode = false;
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                switchingNightTheme = true;
                NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentDayTheme);
                switchingNightTheme = false;
            }
        }
    }

    public static boolean deleteTheme(Context context, ThemeInfo themeInfo) {
        if (themeInfo.pathToFile == null) {
            return false;
        }
        boolean currentThemeDeleted = false;
        if (currentTheme == themeInfo) {
            applyTheme(context, defaultTheme, true, false, false);
            currentThemeDeleted = true;
        }
        if (themeInfo == currentNightTheme) {
            currentNightTheme = themesDict.get("Dark Blue");
        }

        themeInfo.removeObservers();
        otherThemes.remove(themeInfo);
        themesDict.remove(themeInfo.name);
        if (themeInfo.overrideWallpaper != null) {
            themeInfo.overrideWallpaper.delete();
        }
        themes.remove(themeInfo);
        File file = new File(themeInfo.pathToFile);
        file.delete();
        saveOtherThemes(context, true);
        return currentThemeDeleted;
    }

    public static ThemeInfo createNewTheme(Context context, String name) {
        ThemeInfo newTheme = new ThemeInfo();
        newTheme.pathToFile = new File(AndroidUtilities.getFilesDirFixed(), "theme" + AndroidUtilities.random.nextLong() + ".attheme").getAbsolutePath();
        newTheme.name = name;
        themedWallpaperLink = getWallpaperUrl(currentTheme.overrideWallpaper);
        newTheme.account = 0;
        saveCurrentTheme(context, newTheme, true, true, false);
        return newTheme;
    }

    static String getWallpaperUrl(OverrideWallpaperInfo wallpaperInfo) {
        if (wallpaperInfo == null || TextUtils.isEmpty(wallpaperInfo.slug) || wallpaperInfo.slug.equals(DEFAULT_BACKGROUND_SLUG)) {
            return null;
        }
        StringBuilder modes = new StringBuilder();
        if (wallpaperInfo.isBlurred) {
            modes.append("blur");
        }
        if (wallpaperInfo.isMotion) {
            if (modes.length() > 0) {
                modes.append("+");
            }
            modes.append("motion");
        }
        String wallpaperLink;
        if (wallpaperInfo.color == 0) {
            wallpaperLink = "https://attheme.org?slug=" + wallpaperInfo.slug;
        } else {
            String color = String.format("%02x%02x%02x", (byte) (wallpaperInfo.color >> 16) & 0xff, (byte) (wallpaperInfo.color >> 8) & 0xff, (byte) (wallpaperInfo.color & 0xff)).toLowerCase();
            String color2 = wallpaperInfo.gradientColor != 0 ? String.format("%02x%02x%02x", (byte) (wallpaperInfo.gradientColor >> 16) & 0xff, (byte) (wallpaperInfo.gradientColor >> 8) & 0xff, (byte) (wallpaperInfo.gradientColor & 0xff)).toLowerCase() : null;
            if (color2 != null) {
                color += "-" + color2;
                color += "&rotation=" + wallpaperInfo.rotation;
            }
            wallpaperLink = "https://attheme.org?slug=" + wallpaperInfo.slug + "&intensity=" + (int) (wallpaperInfo.intensity * 100) + "&bg_color=" + color;
        }
        if (modes.length() > 0) {
            wallpaperLink += "&mode=" + modes.toString();
        }
        return wallpaperLink;
    }

    public static boolean hasCustomWallpaper() {
        return isApplyingAccent && currentTheme.overrideWallpaper != null;
    }

    public static void resetCustomWallpaper(Context context, boolean temporary) {
        if (temporary) {
            isApplyingAccent = false;
            reloadWallpaper(context);
        } else {
            currentTheme.setOverrideWallpaper(null);
        }
    }

    public static void saveCurrentTheme(Context context, ThemeInfo themeInfo, boolean finalSave, boolean newTheme, boolean upload) {
        String wallpaperLink;
        OverrideWallpaperInfo wallpaperInfo = themeInfo.overrideWallpaper;
        if (wallpaperInfo != null) {
            wallpaperLink = getWallpaperUrl(wallpaperInfo);
        } else {
            wallpaperLink = themedWallpaperLink;
        }

        Drawable wallpaperToSave = newTheme ? wallpaper : themedWallpaper;
        if (newTheme && wallpaperToSave != null) {
            themedWallpaper = wallpaper;
        }
        ThemeAccent accent = currentTheme.getAccent(false);
        HashMap<String, Integer> colorsMap = currentTheme.firstAccentIsDefault && accent.id == DEFALT_THEME_ACCENT_ID ? defaultColors : currentColors;

        StringBuilder result = new StringBuilder();
        if (colorsMap != defaultColors) {
            int outBubbleColor = accent != null ? accent.myMessagesAccentColor : 0;
            int outBubbleGradient = accent != null ? accent.myMessagesGradientAccentColor : 0;
            if (outBubbleColor != 0 && outBubbleGradient != 0) {
                colorsMap.put(key_chat_outBubble, outBubbleColor);
                colorsMap.put(key_chat_outBubbleGradient, outBubbleGradient);
            }
        }
        for (HashMap.Entry<String, Integer> entry : colorsMap.entrySet()) {
            String key = entry.getKey();
            if (wallpaperToSave instanceof BitmapDrawable || wallpaperLink != null) {
                if (key_chat_wallpaper.equals(key) || key_chat_wallpaper_gradient_to.equals(key)) {
                    continue;
                }
            }
            result.append(key).append("=").append(entry.getValue()).append("\n");
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(themeInfo.pathToFile);
            if (result.length() == 0 && !(wallpaperToSave instanceof BitmapDrawable) && TextUtils.isEmpty(wallpaperLink)) {
                result.append(' ');
            }
            stream.write(AndroidUtilities.getStringBytes(result.toString()));
            if (!TextUtils.isEmpty(wallpaperLink)) {
                stream.write(AndroidUtilities.getStringBytes("WLS=" + wallpaperLink + "\n"));
                if (newTheme) {
                    try {
                        Bitmap bitmap = ((BitmapDrawable) wallpaperToSave).getBitmap();
                        FileOutputStream wallpaperStream = new FileOutputStream(new File(AndroidUtilities.getFilesDirFixed(), AndroidUtilities.MD5(wallpaperLink) + ".wp"));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 87, wallpaperStream);
                        wallpaperStream.close();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } else if (wallpaperToSave instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) wallpaperToSave).getBitmap();
                if (bitmap != null) {
                    stream.write(new byte[]{'W', 'P', 'S', '\n'});
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    stream.write(new byte[]{'\n', 'W', 'P', 'E', '\n'});
                }
                if (finalSave && !upload) {
                    wallpaper = wallpaperToSave;
                    calcBackgroundColor(wallpaperToSave, 2);
                }
            }
            if (!upload) {
                if (themesDict.get(themeInfo.getKey()) == null) {
                    themes.add(themeInfo);
                    themesDict.put(themeInfo.getKey(), themeInfo);
                    otherThemes.add(themeInfo);
                    saveOtherThemes(context, true);
                    sortThemes();
                }
                currentTheme = themeInfo;
                if (currentTheme != currentNightTheme) {
                    currentDayTheme = currentTheme;
                }
                if (colorsMap == defaultColors) {
                    currentColorsNoAccent.clear();
                    refreshThemeColors(context);
                }
                SharedPreferences preferences = AndroidUtilities.getGlobalMainSettings();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("theme", currentDayTheme.getKey());
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (finalSave) {
            //            MessagesController.getInstance(themeInfo.account).saveThemeToServer(themeInfo, themeInfo.getAccent(false));
        }
    }

    public static void checkCurrentRemoteTheme(boolean force) {
        if (loadingCurrentTheme != 0 || !force && Math.abs(System.currentTimeMillis() / 1000 - lastLoadingCurrentThemeTime) < 60 * 60) {
            return;
        }
        for (int a = 0; a < 2; a++) {
            ThemeInfo themeInfo = a == 0 ? currentDayTheme : currentNightTheme;
            if (themeInfo == null || !AndroidUtilities.isClientActivated(themeInfo.account)) {
                continue;
            }
            ThemeAccent accent = themeInfo.getAccent(false);
            Skin info;
            int account;
            if (themeInfo.info != null) {
                info = themeInfo.info;
                account = themeInfo.account;
            } else if (accent != null && accent.info != null) {
                info = accent.info;
                account = 0;
            } else {
                continue;
            }
            if (info == null || info.document == null) {
                continue;
            }

            loadingCurrentTheme++;
            //TODO 发起请求
            //            TLRPC.TL_account_getTheme req = new TLRPC.TL_account_getTheme();
            //            req.document_id = info.document.id;
            //            req.format = "android";
            //            TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
            //            inputTheme.access_hash = info.access_hash;
            //            inputTheme.id = info.id;
            //            req.theme = inputTheme;
            //            ConnectionsManager.getInstance(account).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            //                loadingCurrentTheme--;
            //                boolean changed = false;
            //                if (response instanceof Skin) {
            //                    Skin theme = (Skin) response;
            //                    if (accent != null && theme.settings != null) {
            //                        if (!ThemeInfo.accentEquals(accent, theme.settings)) {
            //                            File file = accent.getPathToWallpaper();
            //                            if (file != null) {
            //                                file.delete();
            //                            }
            //                            ThemeInfo.fillAccentValues(accent, theme.settings);
            //                            if (currentTheme == themeInfo && currentTheme.currentAccentId == accent.id) {
            //                                refreshThemeColors();
            //                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme, currentNightTheme == currentTheme, null, -1);
            //                            }
            //                            PatternsLoader.createLoader(true);
            //                            changed = true;
            //                        }
            //                        accent.patternMotion = theme.settings.wallpaper != null && theme.settings.wallpaper.settings != null && theme.settings.wallpaper.settings.motion;
            //                    } else if (theme.document != null && theme.document.id != info.document.id) {
            //                        if (accent != null) {
            //                            accent.info = theme;
            //                        } else {
            //                            themeInfo.info = theme;
            //                            themeInfo.loadThemeDocument();
            //                        }
            //                        changed = true;
            //                    }
            //                }
            //                if (loadingCurrentTheme == 0) {
            //                    lastLoadingCurrentThemeTime = (int) (System.currentTimeMillis() / 1000);
            //                    saveOtherThemes(changed);
            //                }
            //            }));
        }
    }

    public static void loadRemoteThemes(final int currentAccount, boolean force) {
        if (loadingRemoteThemes[currentAccount] || !force && Math.abs(System.currentTimeMillis() / 1000 - lastLoadingThemesTime[currentAccount]) < 60 * 60 || !AndroidUtilities.isClientActivated(currentAccount)) {
            return;
        }
        loadingRemoteThemes[currentAccount] = true;
        //TODO 发起请求
        //        TLRPC.TL_account_getThemes req = new TLRPC.TL_account_getThemes();
        //        req.format = "android";
        //        req.hash = remoteThemesHash[currentAccount];
        //        ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
        //            loadingRemoteThemes[currentAccount] = false;
        //            if (response instanceof TLRPC.TL_account_themes) {
        //                TLRPC.TL_account_themes res = (TLRPC.TL_account_themes) response;
        //                remoteThemesHash[currentAccount] = res.hash;
        //                lastLoadingThemesTime[currentAccount] = (int) (System.currentTimeMillis() / 1000);
        //                ArrayList<Object> oldServerThemes = new ArrayList<>();
        //                for (int a = 0, N = themes.size(); a < N; a++) {
        //                    ThemeInfo info = themes.get(a);
        //                    if (info.info != null && info.account == currentAccount) {
        //                        oldServerThemes.add(info);
        //                    } else if (info.themeAccents != null) {
        //                        for (int b = 0; b < info.themeAccents.size(); b++) {
        //                            ThemeAccent accent = info.themeAccents.get(b);
        //                            if (accent.info != null && accent.account == currentAccount) {
        //                                oldServerThemes.add(accent);
        //                            }
        //                        }
        //                    }
        //                }
        //                boolean loadPatterns = false;
        //                boolean added = false;
        //                for (int a = 0, N = res.themes.size(); a < N; a++) {
        //                    TLRPC.Theme t = res.themes.get(a);
        //                    if (!(t instanceof Skin)) {
        //                        continue;
        //                    }
        //                    Skin theme = (Skin) t;
        //                    if (theme.settings != null) {
        //                        String key = getBaseThemeKey(theme.settings);
        //                        if (key == null) {
        //                            continue;
        //                        }
        //                        ThemeInfo info = themesDict.get(key);
        //                        if (info == null || info.themeAccents == null) {
        //                            continue;
        //                        }
        //                        ThemeAccent accent = info.accentsByThemeId.get(theme.id);
        //                        if (accent != null) {
        //                            if (!ThemeInfo.accentEquals(accent, theme.settings)) {
        //                                File file = accent.getPathToWallpaper();
        //                                if (file != null) {
        //                                    file.delete();
        //                                }
        //                                ThemeInfo.fillAccentValues(accent, theme.settings);
        //                                loadPatterns = true;
        //                                added = true;
        //                                if (currentTheme == info && currentTheme.currentAccentId == accent.id) {
        //                                    refreshThemeColors();
        //                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme, currentNightTheme == currentTheme, null, -1);
        //                                }
        //                            }
        //                            accent.patternMotion = theme.settings.wallpaper != null && theme.settings.wallpaper.settings != null && theme.settings.wallpaper.settings.motion;
        //                            oldServerThemes.remove(accent);
        //                        } else {
        //                            accent = info.createNewAccent(theme, currentAccount);
        //                            if (!TextUtils.isEmpty(accent.patternSlug)) {
        //                                loadPatterns = true;
        //                            }
        //                        }
        //                    } else {
        //                        String key = "remote" + theme.id;
        //                        ThemeInfo info = themesDict.get(key);
        //                        if (info == null) {
        //                            info = new ThemeInfo();
        //                            info.account = currentAccount;
        //                            info.pathToFile = new File(ApplicationLoader.getFilesDirFixed(), key + ".attheme").getAbsolutePath();
        //                            themes.add(info);
        //                            otherThemes.add(info);
        //                            added = true;
        //                        } else {
        //                            oldServerThemes.remove(info);
        //                        }
        //                        info.name = theme.title;
        //                        info.info = theme;
        //                        themesDict.put(info.getKey(), info);
        //                    }
        //                }
        //                for (int a = 0, N = oldServerThemes.size(); a < N; a++) {
        //                    Object object = oldServerThemes.get(a);
        //                    if (object instanceof ThemeInfo) {
        //                        ThemeInfo info = (ThemeInfo) object;
        //                        info.removeObservers();
        //                        otherThemes.remove(info);
        //                        themesDict.remove(info.name);
        //                        if (info.overrideWallpaper != null) {
        //                            info.overrideWallpaper.delete();
        //                        }
        //                        themes.remove(info);
        //                        File file = new File(info.pathToFile);
        //                        file.delete();
        //                        boolean isNightTheme = false;
        //                        if (currentDayTheme == info) {
        //                            currentDayTheme = defaultTheme;
        //                        } else if (currentNightTheme == info) {
        //                            currentNightTheme = themesDict.get("Dark Blue");
        //                            isNightTheme = true;
        //                        }
        //                        if (currentTheme == info) {
        //                            applyTheme(isNightTheme ? currentNightTheme : currentDayTheme, true, false, isNightTheme);
        //                        }
        //                    } else if (object instanceof ThemeAccent) {
        //                        ThemeAccent accent = (ThemeAccent) object;
        //                        if (deleteThemeAccent(accent.parentTheme, accent, false) && currentTheme == accent.parentTheme) {
        //                            Theme.refreshThemeColors();
        //                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme, currentNightTheme == currentTheme, null, -1);
        //                        }
        //                    }
        //                }
        //                saveOtherThemes(true);
        //                sortThemes();
        //                if (added) {
        //                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated);
        //                }
        //                if (loadPatterns) {
        //                    PatternsLoader.createLoader(true);
        //                }
        //            }
        //        }));
    }

    public static String getBaseThemeKey(Skin.SkinSettings settings) {
        if (settings.base_theme == BaseTheme.BaseThemeClassic) {
            return "Blue";
        } else if (settings.base_theme == BaseTheme.BaseThemeDay) {
            return "Day";
        } else if (settings.base_theme == BaseTheme.BaseThemeTinted) {
            return "Dark Blue";
        } else if (settings.base_theme == BaseTheme.BaseThemeArctic) {
            return "Arctic Blue";
        } else if (settings.base_theme == BaseTheme.BaseThemeNight) {
            return "Night";
        }
        return null;
    }

    public static BaseTheme getBaseThemeByKey(String key) {
        if ("Blue".equals(key)) {
            return BaseTheme.BaseThemeClassic;
        } else if ("Day".equals(key)) {
            return BaseTheme.BaseThemeDay;
        } else if ("Dark Blue".equals(key)) {
            return BaseTheme.BaseThemeTinted;
        } else if ("Arctic Blue".equals(key)) {
            return BaseTheme.BaseThemeArctic;
        } else if ("Night".equals(key)) {
            return BaseTheme.BaseThemeNight;
        }
        return null;
    }

    public static void setThemeFileReference(Context context, Skin info) {
        for (int a = 0, N = themes.size(); a < N; a++) {
            ThemeInfo themeInfo = themes.get(a);
            if (themeInfo.info != null && themeInfo.info.id == info.id) {
                if (themeInfo.info.document != null && info.document != null) {
                    themeInfo.info.document.file_reference = info.document.file_reference;
                    saveOtherThemes(context, true);
                }
                break;
            }
        }
    }

    public static boolean isThemeInstalled(ThemeInfo themeInfo) {
        return themeInfo != null && themesDict.get(themeInfo.getKey()) != null;
    }

    public static void setThemeUploadInfo(Context context, ThemeInfo theme, ThemeAccent accent, Skin info, int account, boolean update) {
        if (info == null) {
            return;
        }
        if (info.settings != null) {
            if (theme == null) {
                String key = getBaseThemeKey(info.settings);
                if (key == null) {
                    return;
                }
                theme = themesDict.get(key);
                if (theme == null) {
                    return;
                }
                accent = theme.accentsByThemeId.get(info.id);
            }
            if (accent == null) {
                return;
            }
            if (accent.info != null) {
                theme.accentsByThemeId.remove(accent.info.id);
            }
            accent.info = info;
            accent.account = account;
            theme.accentsByThemeId.put(info.id, accent);
            if (!ThemeInfo.accentEquals(accent, info.settings)) {
                File file = accent.getPathToWallpaper();
                if (file != null) {
                    file.delete();
                }
                ThemeInfo.fillAccentValues(accent, info.settings);
                if (currentTheme == theme && currentTheme.currentAccentId == accent.id) {
                    refreshThemeColors(context);
                    NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme);
                }
                PatternsLoader.createLoader(true);
            }
            accent.patternMotion = info.settings.wallpaper != null && info.settings.wallpaper.settings != null && info.settings.wallpaper.settings.motion;
            theme.previewParsed = false;
        } else {
            String key;
            if (theme != null) {
                themesDict.remove(key = theme.getKey());
            } else {
                theme = themesDict.get(key = "remote" + info.id);
            }
            if (theme == null) {
                return;
            }
            theme.info = info;
            theme.name = info.title;
            File oldPath = new File(theme.pathToFile);
            File newPath = new File(AndroidUtilities.getFilesDirFixed(), key + ".attheme");
            if (!oldPath.equals(newPath)) {
                try {
                    AndroidUtilities.copyFile(oldPath, newPath);
                    theme.pathToFile = newPath.getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (update) {
                theme.loadThemeDocument();
            } else {
                theme.previewParsed = false;
            }
            themesDict.put(theme.getKey(), theme);
        }
        saveOtherThemes(context, true);
    }

    public static File getAssetFile(Context context, String assetName) {
        File file = new File(AndroidUtilities.getFilesDirFixed(), assetName);
        long size;
        try {
            InputStream stream = context.getAssets().open(assetName);
            size = stream.available();
            stream.close();
        } catch (Exception e) {
            size = 0;
            e.printStackTrace();
        }
        if (!file.exists() || size != 0 && file.length() != size) {
            try (InputStream in = context.getAssets().open(assetName)) {
                AndroidUtilities.copyFile(in, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static int getPreviewColor(HashMap<String, Integer> colors, String key) {
        Integer color = colors.get(key);
        if (color == null) {
            color = defaultColors.get(key);
        }
        return color;
    }

    public static String createThemePreviewImage(Context context, String pathToFile, String wallpaperPath) {
        try {
            String[] wallpaperLink = new String[1];
            HashMap<String, Integer> colors = getThemeFileValues(context, new File(pathToFile), null, wallpaperLink);
            Integer wallpaperFileOffset = colors.get("wallpaperFileOffset");
            Bitmap bitmap = Bitmaps.createBitmap(560, 678, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint();

            int actionBarColor = getPreviewColor(colors, key_actionBarDefault);
            int actionBarIconColor = getPreviewColor(colors, key_actionBarDefaultIcon);
            int messageFieldColor = getPreviewColor(colors, key_chat_messagePanelBackground);
            int messageFieldIconColor = getPreviewColor(colors, key_chat_messagePanelIcons);
            int messageInColor = getPreviewColor(colors, key_chat_inBubble);
            int messageOutColor = getPreviewColor(colors, key_chat_outBubble);
            Integer messageOutGradientColor = colors.get(key_chat_outBubbleGradient);
            Integer backgroundColor = colors.get(key_chat_wallpaper);
            Integer serviceColor = colors.get(key_chat_serviceBackground);
            Integer gradientToColor = colors.get(key_chat_wallpaper_gradient_to);

            Drawable backDrawable = context.getResources().getDrawable(R.drawable.preview_back).mutate();
            setDrawableColor(backDrawable, actionBarIconColor);
            Drawable otherDrawable = context.getResources().getDrawable(R.drawable.preview_dots).mutate();
            setDrawableColor(otherDrawable, actionBarIconColor);
            Drawable emojiDrawable = context.getResources().getDrawable(R.drawable.preview_smile).mutate();
            setDrawableColor(emojiDrawable, messageFieldIconColor);
            Drawable micDrawable = context.getResources().getDrawable(R.drawable.preview_mic).mutate();
            setDrawableColor(micDrawable, messageFieldIconColor);

            MessageDrawable[] msgDrawable = new MessageDrawable[2];
            for (int a = 0; a < 2; a++) {
                msgDrawable[a] = new MessageDrawable(MessageDrawable.TYPE_PREVIEW, a == 1, false) {
                    @Override
                    protected int getColor(String key) {
                        Integer color = colors.get(key);
                        if (color == null) {
                            color = defaultColors.get(key);
                        }
                        return color;
                    }

                    @Override
                    protected Integer getCurrentColor(String key) {
                        return colors.get(key);
                    }
                };
                setDrawableColor(msgDrawable[a], a == 0 ? messageInColor : messageOutColor);
            }

            RectF rect = new RectF();
            int quality = 80;
            boolean hasBackground = false;
            if (wallpaperPath != null) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(wallpaperPath, options);
                    if (options.outWidth > 0 && options.outHeight > 0) {
                        float scale = Math.min(options.outWidth / 560.0f, options.outHeight / 560.0f);
                        options.inSampleSize = 1;
                        if (scale > 1.0f) {
                            do {
                                options.inSampleSize *= 2;
                            } while (options.inSampleSize < scale);
                        }
                        options.inJustDecodeBounds = false;
                        Bitmap wallpaper = BitmapFactory.decodeFile(wallpaperPath, options);
                        if (wallpaper != null) {
                            Paint bitmapPaint = new Paint();
                            bitmapPaint.setFilterBitmap(true);
                            scale = Math.min(wallpaper.getWidth() / 560.0f, wallpaper.getHeight() / 560.0f);
                            rect.set(0, 0, wallpaper.getWidth() / scale, wallpaper.getHeight() / scale);
                            rect.offset((bitmap.getWidth() - rect.width()) / 2, (bitmap.getHeight() - rect.height()) / 2);
                            canvas.drawBitmap(wallpaper, null, rect, bitmapPaint);
                            hasBackground = true;
                            if (serviceColor == null) {
                                serviceColor = AndroidUtilities.calcDrawableColor(new BitmapDrawable(wallpaper))[0];
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else if (backgroundColor != null) {
                Drawable wallpaperDrawable;
                if (gradientToColor == null) {
                    wallpaperDrawable = new ColorDrawable(backgroundColor);
                } else {
                    Integer gradientRotation = colors.get(key_chat_wallpaper_gradient_rotation);
                    if (gradientRotation == null) {
                        gradientRotation = 45;
                    }
                    final int[] gradientColors = {backgroundColor, gradientToColor};
                    wallpaperDrawable = BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(context, gradientRotation, gradientColors, bitmap.getWidth(), bitmap.getHeight() - 120);
                    quality = 90;
                }
                wallpaperDrawable.setBounds(0, 120, bitmap.getWidth(), bitmap.getHeight() - 120);
                wallpaperDrawable.draw(canvas);
                if (serviceColor == null) {
                    serviceColor = AndroidUtilities.calcDrawableColor(new ColorDrawable(backgroundColor))[0];
                }
                hasBackground = true;
            } else if (wallpaperFileOffset != null && wallpaperFileOffset >= 0 || !TextUtils.isEmpty(wallpaperLink[0])) {
                FileInputStream stream = null;
                File pathToWallpaper = null;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    if (!TextUtils.isEmpty(wallpaperLink[0])) {
                        pathToWallpaper = new File(AndroidUtilities.getFilesDirFixed(), AndroidUtilities.MD5(wallpaperLink[0]) + ".wp");
                        BitmapFactory.decodeFile(pathToWallpaper.getAbsolutePath(), options);
                    } else {
                        stream = new FileInputStream(pathToFile);
                        stream.getChannel().position(wallpaperFileOffset);
                        BitmapFactory.decodeStream(stream, null, options);
                    }
                    if (options.outWidth > 0 && options.outHeight > 0) {
                        float scale = Math.min(options.outWidth / 560.0f, options.outHeight / 560.0f);
                        options.inSampleSize = 1;
                        if (scale > 1.0f) {
                            do {
                                options.inSampleSize *= 2;
                            } while (options.inSampleSize < scale);
                        }
                        options.inJustDecodeBounds = false;
                        Bitmap wallpaper;
                        if (pathToWallpaper != null) {
                            wallpaper = BitmapFactory.decodeFile(pathToWallpaper.getAbsolutePath(), options);
                        } else {
                            stream.getChannel().position(wallpaperFileOffset);
                            wallpaper = BitmapFactory.decodeStream(stream, null, options);
                        }
                        if (wallpaper != null) {
                            Paint bitmapPaint = new Paint();
                            bitmapPaint.setFilterBitmap(true);
                            scale = Math.min(wallpaper.getWidth() / 560.0f, wallpaper.getHeight() / 560.0f);
                            rect.set(0, 0, wallpaper.getWidth() / scale, wallpaper.getHeight() / scale);
                            rect.offset((bitmap.getWidth() - rect.width()) / 2, (bitmap.getHeight() - rect.height()) / 2);
                            canvas.drawBitmap(wallpaper, null, rect, bitmapPaint);
                            hasBackground = true;
                            if (serviceColor == null) {
                                serviceColor = AndroidUtilities.calcDrawableColor(new BitmapDrawable(wallpaper))[0];
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!hasBackground) {
                BitmapDrawable catsDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.catstile).mutate();
                if (serviceColor == null) {
                    serviceColor = AndroidUtilities.calcDrawableColor(catsDrawable)[0];
                }
                catsDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                catsDrawable.setBounds(0, 120, bitmap.getWidth(), bitmap.getHeight() - 120);
                catsDrawable.draw(canvas);
            }

            paint.setColor(actionBarColor);
            canvas.drawRect(0, 0, bitmap.getWidth(), 120, paint);

            if (backDrawable != null) {
                int x = 13;
                int y = (120 - backDrawable.getIntrinsicHeight()) / 2;
                backDrawable.setBounds(x, y, x + backDrawable.getIntrinsicWidth(), y + backDrawable.getIntrinsicHeight());
                backDrawable.draw(canvas);
            }
            if (otherDrawable != null) {
                int x = bitmap.getWidth() - otherDrawable.getIntrinsicWidth() - 10;
                int y = (120 - otherDrawable.getIntrinsicHeight()) / 2;
                otherDrawable.setBounds(x, y, x + otherDrawable.getIntrinsicWidth(), y + otherDrawable.getIntrinsicHeight());
                otherDrawable.draw(canvas);
            }
            msgDrawable[1].setBounds(161, 216, bitmap.getWidth() - 20, 216 + 92);
            msgDrawable[1].setTop(0, 522, false, false);
            msgDrawable[1].draw(canvas);

            msgDrawable[1].setBounds(161, 430, bitmap.getWidth() - 20, 430 + 92);
            msgDrawable[1].setTop(430, 522, false, false);
            msgDrawable[1].draw(canvas);

            msgDrawable[0].setBounds(20, 323, 399, 323 + 92);
            msgDrawable[0].setTop(323, 522, false, false);
            msgDrawable[0].draw(canvas);

            if (serviceColor != null) {
                int x = (bitmap.getWidth() - 126) / 2;
                int y = 150;
                rect.set(x, y, x + 126, y + 42);
                paint.setColor(serviceColor);
                canvas.drawRoundRect(rect, 21, 21, paint);
            }

            paint.setColor(messageFieldColor);
            canvas.drawRect(0, bitmap.getHeight() - 120, bitmap.getWidth(), bitmap.getHeight(), paint);
            if (emojiDrawable != null) {
                int x = 22;
                int y = bitmap.getHeight() - 120 + (120 - emojiDrawable.getIntrinsicHeight()) / 2;
                emojiDrawable.setBounds(x, y, x + emojiDrawable.getIntrinsicWidth(), y + emojiDrawable.getIntrinsicHeight());
                emojiDrawable.draw(canvas);
            }
            if (micDrawable != null) {
                int x = bitmap.getWidth() - micDrawable.getIntrinsicWidth() - 22;
                int y = bitmap.getHeight() - 120 + (120 - micDrawable.getIntrinsicHeight()) / 2;
                micDrawable.setBounds(x, y, x + micDrawable.getIntrinsicWidth(), y + micDrawable.getIntrinsicHeight());
                micDrawable.draw(canvas);
            }
            canvas.setBitmap(null);

            String fileName = Integer.MIN_VALUE + "_" + SharedConfig.getLastLocalId() + ".jpg";
            final File cacheFile = new File(AndroidUtilities.getCacheDir(context), fileName);
            try {
                FileOutputStream stream = new FileOutputStream(cacheFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                SharedConfig.saveConfig();
                return cacheFile.getAbsolutePath();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, Integer> getThemeFileValues(Context context, File file, String assetName, String[] wallpaperLink) {
        FileInputStream stream = null;
        HashMap<String, Integer> stringMap = new HashMap<>();
        try {
            byte[] bytes = new byte[1024];
            int currentPosition = 0;
            if (assetName != null) {
                file = getAssetFile(context, assetName);
            }
            stream = new FileInputStream(file);
            int idx;
            int read;
            boolean finished = false;
            int wallpaperFileOffset = -1;
            while ((read = stream.read(bytes)) != -1) {
                int previousPosition = currentPosition;
                int start = 0;
                for (int a = 0; a < read; a++) {
                    if (bytes[a] == '\n') {
                        int len = a - start + 1;
                        String line = new String(bytes, start, len - 1);
                        if (line.startsWith("WLS=")) {
                            if (wallpaperLink != null && wallpaperLink.length > 0) {
                                wallpaperLink[0] = line.substring(4);
                            }
                        } else if (line.startsWith("WPS")) {
                            wallpaperFileOffset = currentPosition + len;
                            finished = true;
                            break;
                        } else {
                            if ((idx = line.indexOf('=')) != -1) {
                                String key = line.substring(0, idx);
                                String param = line.substring(idx + 1);
                                int value;
                                if (param.length() > 0 && param.charAt(0) == '#') {
                                    try {
                                        value = Color.parseColor(param);
                                    } catch (Exception ignore) {
                                        value = AndroidUtilities.parseInt(param);
                                    }
                                } else {
                                    value = AndroidUtilities.parseInt(param);
                                }
                                stringMap.put(key, value);
                            }
                        }
                        start += len;
                        currentPosition += len;
                    }
                }
                if (previousPosition == currentPosition) {
                    break;
                }
                stream.getChannel().position(currentPosition);
                if (finished) {
                    break;
                }
            }
            stringMap.put("wallpaperFileOffset", wallpaperFileOffset);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stringMap;
    }
    //endregion

}
