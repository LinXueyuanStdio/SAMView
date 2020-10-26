package com.same.lib.theme;

import android.app.Activity;
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
import android.util.Base64;

import com.same.lib.R;
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

import static com.same.lib.theme.Theme.*;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/26
 * @description null
 * @usage null
 */
public class ThemeManager {
    //region 主题管理
    public static void applyPreviousTheme() {
        if (previousTheme == null) {
            return;
        }
        hasPreviousTheme = false;
        if (isInNigthMode && currentNightTheme != null) {
            applyTheme(currentNightTheme, true, false, true);
        } else if (!isApplyingAccent) {
            applyTheme(previousTheme, true, false, false);
        }
        isApplyingAccent = false;
        previousTheme = null;
        checkAutoNightThemeConditions();
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

    public static void applyThemeTemporary(ThemeInfo themeInfo, boolean accent) {
        previousTheme = getCurrentTheme();
        hasPreviousTheme = true;
        isApplyingAccent = accent;
        applyTheme(themeInfo, false, false, false);
    }

    public static ThemeInfo fillThemeValues(File file, String themeName, Skin theme) {
        try {
            ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.name = themeName;
            themeInfo.info = theme;
            themeInfo.pathToFile = file.getAbsolutePath();

            String[] wallpaperLink = new String[1];
            getThemeFileValues(new File(themeInfo.pathToFile), null, wallpaperLink);

            if (!TextUtils.isEmpty(wallpaperLink[0])) {
                String ling = wallpaperLink[0];
                themeInfo.pathToWallpaper = new File(AndroidUtilities.getFilesDirFixed(), Utilities.MD5(ling) + ".wp").getAbsolutePath();
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
    public static ThemeInfo applyThemeFile(File file, String themeName, Skin theme, boolean temporary) {
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
                applyThemeTemporary(themeInfo, false);
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
                    applyPreviousTheme();
                    return null;
                }

                previousTheme = null;
                hasPreviousTheme = false;
                isApplyingAccent = false;

                ThemeInfo themeInfo = themesDict.get(key);
                if (themeInfo == null) {
                    themeInfo = new ThemeInfo();
                    themeInfo.name = themeName;
                    themes.add(themeInfo);
                    otherThemes.add(themeInfo);
                    sortThemes();
                } else {
                    themesDict.remove(key);
                }
                themeInfo.info = theme;
                themeInfo.pathToFile = finalFile.getAbsolutePath();
                themesDict.put(themeInfo.getKey(), themeInfo);
                saveOtherThemes(true);

                applyTheme(themeInfo, true, true, false);
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

    public static void applyTheme(ThemeInfo themeInfo) {
        applyTheme(themeInfo, true, true, false);
    }

    public static void applyTheme(ThemeInfo themeInfo, boolean nightTheme) {
        applyTheme(themeInfo, true, true, nightTheme);
    }

    static void applyTheme(ThemeInfo themeInfo, boolean save, boolean removeWallpaperOverride, final boolean nightTheme) {
        if (themeInfo == null) {
            return;
        }
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.destroy();
        }
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
                    currentColorsNoAccent = getThemeFileValues(null, themeInfo.assetName, null);
                } else {
                    currentColorsNoAccent = getThemeFileValues(new File(themeInfo.pathToFile), null, wallpaperLink);
                }
                Integer offset = currentColorsNoAccent.get("wallpaperFileOffset");
                themedWallpaperFileOffset = offset != null ? offset : -1;
                if (!TextUtils.isEmpty(wallpaperLink[0])) {
                    themedWallpaperLink = wallpaperLink[0];
                    String newPathToFile = new File(AndroidUtilities.getFilesDirFixed(), Utilities.MD5(themedWallpaperLink) + ".wp").getAbsolutePath();
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
                    editor.commit();
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
            refreshThemeColors();
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

    public static void refreshThemeColors() {
        currentColors.clear();
        currentColors.putAll(currentColorsNoAccent);
        shouldDrawGradientIcons = true;
        ThemeAccent accent = currentTheme.getAccent(false);
        if (accent != null) {
            shouldDrawGradientIcons = accent.fillAccentColors(currentColorsNoAccent, currentColors);
        }
        applyCommonTheme();
        applyDialogsTheme();
        applyProfileTheme();
        applyChatTheme(false);
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

    public static void onUpdateThemeAccents() {
        refreshThemeColors();
    }

    public static boolean deleteThemeAccent(ThemeInfo theme, ThemeAccent accent, boolean save) {
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
            saveThemeAccents(theme, true, false, false, false);
            if (accent.info != null) {
                //                MessagesController.getInstance(accent.account).saveTheme(theme, accent, current && theme == currentNightTheme, true);
            }
        }
        return current;
    }

    public static void saveThemeAccents(ThemeInfo theme, boolean save, boolean remove, boolean indexOnly, boolean upload) {
        saveThemeAccents(theme, save, remove, indexOnly, upload, false);
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
    public static void saveThemeAccents(ThemeInfo theme, boolean save, boolean remove, boolean indexOnly, boolean upload, boolean migration) {
        if (save) {
            SharedPreferences preferences = SharedConfig.applicationContext().getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (!indexOnly) {
                int N = theme.themeAccents.size();
                int count = Math.max(0, N - theme.defaultAccentCount);
                SerializedData data = new SerializedData(4 * (count * 15 + 2));
                data.writeInt32(5);
                data.writeInt32(count);
                for (int a = 0; a < N; a++) {
                    ThemeAccent accent = theme.themeAccents.get(a);
                    if (accent.id < 100) {
                        continue;
                    }
                    data.writeInt32(accent.id);
                    data.writeInt32(accent.accentColor);
                    data.writeInt32(accent.myMessagesAccentColor);
                    data.writeInt32(accent.myMessagesGradientAccentColor);
                    data.writeInt64(accent.backgroundOverrideColor);
                    data.writeInt64(accent.backgroundGradientOverrideColor);
                    data.writeInt32(accent.backgroundRotation);
                    data.writeInt64(0);
                    data.writeDouble(accent.patternIntensity);
                    data.writeBool(accent.patternMotion);
                    data.writeString(accent.patternSlug);
                    data.writeBool(accent.info != null);
                    if (accent.info != null) {
                        data.writeInt32(accent.account);
                        accent.info.serializeToStream(data);
                    }
                }
                editor.putString("accents_" + theme.assetName, Base64.encodeToString(data.toByteArray(), Base64.NO_WRAP | Base64.NO_PADDING));
                if (!migration) {
                    NotificationCenter.postNotificationName(NotificationCenter.themeAccentListUpdated);
                }
                if (upload) {
                    //                    MessagesController.getInstance(UserConfig.selectedAccount).saveThemeToServer(theme, theme.getAccent(false));
                }
            }
            editor.putInt("accent_current_" + theme.assetName, theme.currentAccentId);
            editor.commit();
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
                refreshThemeColors();
            }
        }
        theme.prevAccentId = -1;
    }

    static void saveOtherThemes(boolean full) {
        saveOtherThemes(full, false);
    }

    static void saveOtherThemes(boolean full, boolean migration) {
        SharedPreferences preferences = SharedConfig.applicationContext().getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
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
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            editor.putInt("remoteThemesHash" + (a != 0 ? a : ""), remoteThemesHash[a]);
            editor.putInt("lastLoadingThemesTime" + (a != 0 ? a : ""), lastLoadingThemesTime[a]);
        }

        editor.putInt("lastLoadingCurrentThemeTime", lastLoadingCurrentThemeTime);
        editor.commit();

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
                saveThemeAccents(info, true, false, false, false, migration);
            }
        }
    }

    public static HashMap<String, Integer> getDefaultColors() {
        return defaultColors;
    }

    public static ThemeInfo getPreviousTheme() {
        return previousTheme;
    }

    public static String getCurrentThemeName() {
        String text = currentDayTheme.getName();
        if (text.toLowerCase().endsWith(".attheme")) {
            text = text.substring(0, text.lastIndexOf('.'));
        }
        return text;
    }

    public static String getCurrentNightThemeName() {
        if (currentNightTheme == null) {
            return "";
        }
        String text = currentNightTheme.getName();
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
            if (ApplicationLoader.mainInterfacePaused || !ApplicationLoader.isScreenOn) {
                return;
            }
            if (lux > MAXIMUM_LUX_BREAKPOINT) {
                lastBrightnessValue = 1.0f;
            } else {
                lastBrightnessValue = (float) Math.ceil(9.9323f * Math.log(lux) + 27.059f) / 100.0f;
            }
            if (lastBrightnessValue <= autoNightBrighnessThreshold) {
                if (!MediaController.getInstance().isRecordingOrListeningByProximity()) {
                    if (switchDayRunnableScheduled) {
                        switchDayRunnableScheduled = false;
                        AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
                    }
                    if (!switchNightRunnableScheduled) {
                        switchNightRunnableScheduled = true;
                        AndroidUtilities.runOnUIThread(switchNightBrightnessRunnable, getAutoNightSwitchThemeDelay());
                    }
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

    public static void checkAutoNightThemeConditions() {
        checkAutoNightThemeConditions(false);
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

    static int needSwitchToTheme() {
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
                sensorManager = (SensorManager) SharedConfig.applicationContext().getSystemService(Context.SENSOR_SERVICE);
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
            Configuration configuration = SharedConfig.applicationContext().getResources().getConfiguration();
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

    public static void checkAutoNightThemeConditions(boolean force) {
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
        int switchToTheme = needSwitchToTheme();
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
                NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentNightTheme, true, null, -1);
                switchingNightTheme = false;
            }
        } else {
            if (currentTheme != currentDayTheme) {
                isInNigthMode = false;
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                switchingNightTheme = true;
                NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentDayTheme, true, null, -1);
                switchingNightTheme = false;
            }
        }
    }

    public static boolean deleteTheme(ThemeInfo themeInfo) {
        if (themeInfo.pathToFile == null) {
            return false;
        }
        boolean currentThemeDeleted = false;
        if (currentTheme == themeInfo) {
            applyTheme(defaultTheme, true, false, false);
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
        saveOtherThemes(true);
        return currentThemeDeleted;
    }

    public static ThemeInfo createNewTheme(String name) {
        ThemeInfo newTheme = new ThemeInfo();
        newTheme.pathToFile = new File(AndroidUtilities.getFilesDirFixed(), "theme" + AndroidUtilities.random.nextLong() + ".attheme").getAbsolutePath();
        newTheme.name = name;
        themedWallpaperLink = getWallpaperUrl(currentTheme.overrideWallpaper);
        saveCurrentTheme(newTheme, true, true, false);
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

    public static void saveCurrentTheme(ThemeInfo themeInfo, boolean finalSave, boolean newTheme, boolean upload) {
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
                        FileOutputStream wallpaperStream = new FileOutputStream(new File(AndroidUtilities.getFilesDirFixed(), Utilities.MD5(wallpaperLink) + ".wp"));
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
                    saveOtherThemes(true);
                    sortThemes();
                }
                currentTheme = themeInfo;
                if (currentTheme != currentNightTheme) {
                    currentDayTheme = currentTheme;
                }
                if (colorsMap == defaultColors) {
                    currentColorsNoAccent.clear();
                    refreshThemeColors();
                }
                SharedPreferences preferences = AndroidUtilities.getGlobalMainSettings();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("theme", currentDayTheme.getKey());
                editor.commit();
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
            if (themeInfo == null || !UserConfig.getInstance(themeInfo.account).isClientActivated()) {
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
            //                        if (!Theme.ThemeInfo.accentEquals(accent, theme.settings)) {
            //                            File file = accent.getPathToWallpaper();
            //                            if (file != null) {
            //                                file.delete();
            //                            }
            //                            Theme.ThemeInfo.fillAccentValues(accent, theme.settings);
            //                            if (currentTheme == themeInfo && currentTheme.currentAccentId == accent.id) {
            //                                refreshThemeColors();
            //                                NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme, currentNightTheme == currentTheme, null, -1);
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
        if (loadingRemoteThemes[currentAccount] || !force && Math.abs(System.currentTimeMillis() / 1000 - lastLoadingThemesTime[currentAccount]) < 60 * 60 || !UserConfig.getInstance(currentAccount).isClientActivated()) {
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
        //                    Theme.ThemeInfo info = themes.get(a);
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
        //                        Theme.ThemeInfo info = themesDict.get(key);
        //                        if (info == null || info.themeAccents == null) {
        //                            continue;
        //                        }
        //                        ThemeAccent accent = info.accentsByThemeId.get(theme.id);
        //                        if (accent != null) {
        //                            if (!Theme.ThemeInfo.accentEquals(accent, theme.settings)) {
        //                                File file = accent.getPathToWallpaper();
        //                                if (file != null) {
        //                                    file.delete();
        //                                }
        //                                Theme.ThemeInfo.fillAccentValues(accent, theme.settings);
        //                                loadPatterns = true;
        //                                added = true;
        //                                if (currentTheme == info && currentTheme.currentAccentId == accent.id) {
        //                                    refreshThemeColors();
        //                                    NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme, currentNightTheme == currentTheme, null, -1);
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
        //                        Theme.ThemeInfo info = themesDict.get(key);
        //                        if (info == null) {
        //                            info = new Theme.ThemeInfo();
        //                            info.account = currentAccount;
        //                            info.pathToFile = new File(AndroidUtilities.getFilesDirFixed(), key + ".attheme").getAbsolutePath();
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
        //                    if (object instanceof Theme.ThemeInfo) {
        //                        Theme.ThemeInfo info = (Theme.ThemeInfo) object;
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
        //                            NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme, currentNightTheme == currentTheme, null, -1);
        //                        }
        //                    }
        //                }
        //                saveOtherThemes(true);
        //                sortThemes();
        //                if (added) {
        //                    NotificationCenter.postNotificationName(NotificationCenter.themeListUpdated);
        //                }
        //                if (loadPatterns) {
        //                    PatternsLoader.createLoader(true);
        //                }
        //            }
        //        }));
    }

    public static String getBaseThemeKey(Skin.SkinSettings settings) {
        if (settings.base_theme instanceof Skin.TL_baseThemeClassic) {
            return "Blue";
        } else if (settings.base_theme instanceof Skin.TL_baseThemeDay) {
            return "Day";
        } else if (settings.base_theme instanceof Skin.TL_baseThemeTinted) {
            return "Dark Blue";
        } else if (settings.base_theme instanceof Skin.TL_baseThemeArctic) {
            return "Arctic Blue";
        } else if (settings.base_theme instanceof Skin.TL_baseThemeNight) {
            return "Night";
        }
        return null;
    }

    public static Skin.BaseTheme getBaseThemeByKey(String key) {
        if ("Blue".equals(key)) {
            return new Skin.TL_baseThemeClassic();
        } else if ("Day".equals(key)) {
            return new Skin.TL_baseThemeDay();
        } else if ("Dark Blue".equals(key)) {
            return new Skin.TL_baseThemeTinted();
        } else if ("Arctic Blue".equals(key)) {
            return new Skin.TL_baseThemeArctic();
        } else if ("Night".equals(key)) {
            return new Skin.TL_baseThemeNight();
        }
        return null;
    }

    public static void setThemeFileReference(Skin info) {
        for (int a = 0, N = themes.size(); a < N; a++) {
            ThemeInfo themeInfo = themes.get(a);
            if (themeInfo.info != null && themeInfo.info.id == info.id) {
                if (themeInfo.info.document != null && info.document != null) {
                    themeInfo.info.document.file_reference = info.document.file_reference;
                    saveOtherThemes(true);
                }
                break;
            }
        }
    }

    public static boolean isThemeInstalled(ThemeInfo themeInfo) {
        return themeInfo != null && themesDict.get(themeInfo.getKey()) != null;
    }

    public static void setThemeUploadInfo(ThemeInfo theme, ThemeAccent accent, Skin info, int account, boolean update) {
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
                    refreshThemeColors();
                    NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme, currentNightTheme == currentTheme, null, -1);
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
        saveOtherThemes(true);
    }

    public static File getAssetFile(String assetName) {
        File file = new File(AndroidUtilities.getFilesDirFixed(), assetName);
        long size;
        try {
            InputStream stream = SharedConfig.applicationContext().getAssets().open(assetName);
            size = stream.available();
            stream.close();
        } catch (Exception e) {
            size = 0;
            e.printStackTrace();
        }
        if (!file.exists() || size != 0 && file.length() != size) {
            try (InputStream in = SharedConfig.applicationContext().getAssets().open(assetName)) {
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

    public static String createThemePreviewImage(String pathToFile, String wallpaperPath) {
        try {
            String[] wallpaperLink = new String[1];
            HashMap<String, Integer> colors = getThemeFileValues(new File(pathToFile), null, wallpaperLink);
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

            Drawable backDrawable = SharedConfig.applicationContext().getResources().getDrawable(R.drawable.preview_back).mutate();
            setDrawableColor(backDrawable, actionBarIconColor);
            Drawable otherDrawable = SharedConfig.applicationContext().getResources().getDrawable(R.drawable.preview_dots).mutate();
            setDrawableColor(otherDrawable, actionBarIconColor);
            Drawable emojiDrawable = SharedConfig.applicationContext().getResources().getDrawable(R.drawable.preview_smile).mutate();
            setDrawableColor(emojiDrawable, messageFieldIconColor);
            Drawable micDrawable = SharedConfig.applicationContext().getResources().getDrawable(R.drawable.preview_mic).mutate();
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
                    wallpaperDrawable = BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(gradientRotation, gradientColors, bitmap.getWidth(), bitmap.getHeight() - 120);
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
                        pathToWallpaper = new File(AndroidUtilities.getFilesDirFixed(), Utilities.MD5(wallpaperLink[0]) + ".wp");
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
                BitmapDrawable catsDrawable = (BitmapDrawable) SharedConfig.applicationContext().getResources().getDrawable(R.drawable.catstile).mutate();
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
            final File cacheFile = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), fileName);
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

    public static HashMap<String, Integer> getThemeFileValues(File file, String assetName, String[] wallpaperLink) {
        FileInputStream stream = null;
        HashMap<String, Integer> stringMap = new HashMap<>();
        try {
            byte[] bytes = new byte[1024];
            int currentPosition = 0;
            if (assetName != null) {
                file = getAssetFile(assetName);
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
