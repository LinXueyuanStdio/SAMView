package com.same.lib.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;
import com.same.lib.util.UIThread;

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
import static com.same.lib.theme.Theme.lastBrightnessValue;
import static com.same.lib.theme.Theme.lastDelayUpdateTime;
import static com.same.lib.theme.Theme.lastThemeSwitchTime;
import static com.same.lib.theme.Theme.lightSensor;
import static com.same.lib.theme.Theme.lightSensorRegistered;
import static com.same.lib.theme.Theme.otherThemes;
import static com.same.lib.theme.Theme.previousTheme;
import static com.same.lib.theme.Theme.selectedAutoNightType;
import static com.same.lib.theme.Theme.sensorManager;
import static com.same.lib.theme.Theme.shouldDrawGradientIcons;
import static com.same.lib.theme.Theme.switchDayBrightnessRunnable;
import static com.same.lib.theme.Theme.switchDayRunnableScheduled;
import static com.same.lib.theme.Theme.switchNightBrightnessRunnable;
import static com.same.lib.theme.Theme.switchNightRunnableScheduled;
import static com.same.lib.theme.Theme.switchNightThemeDelay;
import static com.same.lib.theme.Theme.switchingNightTheme;
import static com.same.lib.theme.Theme.themes;
import static com.same.lib.theme.Theme.themesDict;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/26
 * @description null
 * @usage null
 */
public class ThemeManager {
    public static void applyPreviousTheme(Context context) {
        if (previousTheme == null) {
            return;
        }
        hasPreviousTheme = false;
        if (isInNigthMode && currentNightTheme != null) {
            applyTheme(context, currentNightTheme, true, true);
        } else if (!isApplyingAccent) {
            applyTheme(context, previousTheme, true, false);
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
        applyTheme(context, themeInfo, false, false);
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

                applyTheme(context, themeInfo, true, false);
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
        applyTheme(context, themeInfo, true, false);
    }

    public static void applyTheme(Context context, ThemeInfo themeInfo, boolean nightTheme) {
        applyTheme(context, themeInfo, true, nightTheme);
    }

    static void applyTheme(Context context, ThemeInfo themeInfo, boolean save, final boolean nightTheme) {
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
            } else {
                if (!nightTheme && save) {
                    SharedPreferences preferences = AndroidUtilities.getGlobalMainSettings();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("theme");
                    editor.apply();
                }
                currentColorsNoAccent.clear();
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
            ThemeStore.saveTheme(themeInfo, themeInfo.getAccent(false), nightTheme, false);
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
        for (AbsTheme absTheme : ThemeRes.themes) {
            absTheme.applyResources(context);
        }
        UIThread.runOnUIThread(() -> NotificationCenter.post(NotificationCenter.didSetNewTheme, false));
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
        theme.themeAccentsMap.remove(accent.id);
        theme.themeAccents.remove(accent);
        if (accent.info != null) {
            theme.accentsByThemeId.remove(accent.info.id);
        }
        if (current) {
            ThemeAccent themeAccent = theme.themeAccents.get(0);
            theme.setCurrentAccentId(themeAccent.id);
        }
        if (save) {
            saveThemeAccents(context, theme, true, false, false);
            if (accent.info != null) {
                ThemeStore.saveTheme(theme, accent, current && theme == currentNightTheme, true);
            }
        }
        return current;
    }

    public static void saveThemeAccents(Context context, ThemeInfo theme, boolean save, boolean remove, boolean indexOnly) {
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
        SharedPreferences preferences = AndroidUtilities.getThemeConfig();
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
                saveThemeAccents(context, info, true, false, false);
            }
        }
    }

    public static HashMap<String, Integer> getDefaultColors() {
        return defaultColors;
    }

    public static ThemeInfo getPreviousTheme() {
        return previousTheme;
    }

    public static ThemeInfo getCurrentTheme() {
        return currentDayTheme != null ? currentDayTheme : defaultTheme;
    }

    public static ThemeInfo getCurrentNightTheme() {
        return currentNightTheme;
    }

    public static ThemeInfo getCurrentDayTheme() {
        return currentDayTheme;
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
                    UIThread.cancelRunOnUIThread(switchDayBrightnessRunnable);
                }
                if (!switchNightRunnableScheduled) {
                    switchNightRunnableScheduled = true;
                    UIThread.runOnUIThread(switchNightBrightnessRunnable, getAutoNightSwitchThemeDelay());
                }
            } else {
                if (switchNightRunnableScheduled) {
                    switchNightRunnableScheduled = false;
                    UIThread.cancelRunOnUIThread(switchNightBrightnessRunnable);
                }
                if (!switchDayRunnableScheduled) {
                    switchDayRunnableScheduled = true;
                    UIThread.runOnUIThread(switchDayBrightnessRunnable, getAutoNightSwitchThemeDelay());
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
                UIThread.cancelRunOnUIThread(switchNightBrightnessRunnable);
            }
            if (switchDayRunnableScheduled) {
                switchDayRunnableScheduled = false;
                UIThread.cancelRunOnUIThread(switchDayBrightnessRunnable);
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
        editor.apply();
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
                UIThread.cancelRunOnUIThread(switchNightBrightnessRunnable);
            }
            if (switchDayRunnableScheduled) {
                switchDayRunnableScheduled = false;
                UIThread.cancelRunOnUIThread(switchDayBrightnessRunnable);
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
                NotificationCenter.post(NotificationCenter.needSetDayNightTheme, currentNightTheme);
                switchingNightTheme = false;
            }
        } else {
            if (currentTheme != currentDayTheme) {
                isInNigthMode = false;
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                switchingNightTheme = true;
                NotificationCenter.post(NotificationCenter.needSetDayNightTheme, currentDayTheme);
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
            applyTheme(context, defaultTheme, true, false);
            currentThemeDeleted = true;
        }
        if (themeInfo == currentNightTheme) {
            currentNightTheme = themesDict.get("Dark Blue");
        }

        themeInfo.removeObservers();
        otherThemes.remove(themeInfo);
        themesDict.remove(themeInfo.name);
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
        newTheme.account = 0;
        saveCurrentTheme(context, newTheme, true, true, false);
        return newTheme;
    }

    public static void saveCurrentTheme(Context context, ThemeInfo themeInfo, boolean finalSave, boolean newTheme, boolean upload) {
        ThemeAccent accent = currentTheme.getAccent(false);
        HashMap<String, Integer> colorsMap = currentTheme.firstAccentIsDefault && accent.id == DEFALT_THEME_ACCENT_ID ? defaultColors : currentColors;

        StringBuilder result = new StringBuilder();
        for (HashMap.Entry<String, Integer> entry : colorsMap.entrySet()) {
            String key = entry.getKey();
            result.append(key).append("=").append(entry.getValue()).append("\n");
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(themeInfo.pathToFile);
            if (result.length() == 0) {
                result.append(' ');
            }
            stream.write(AndroidUtilities.getStringBytes(result.toString()));
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
            ThemeStore.saveThemeToServer(themeInfo, themeInfo.getAccent(false));
        }
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
                ThemeInfo.fillAccentValues(accent, info.settings);
                if (currentTheme == theme && currentTheme.currentAccentId == accent.id) {
                    refreshThemeColors(context);
                    NotificationCenter.post(NotificationCenter.needSetDayNightTheme, currentTheme);
                }
            }
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
}
