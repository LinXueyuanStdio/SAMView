package com.same.lib.theme;

import android.content.Context;
import android.graphics.Color;

import com.same.lib.base.AndroidUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import static com.same.lib.theme.Theme.defaultColors;
import static com.same.lib.theme.Theme.fallbackKeys;
import static com.same.lib.theme.Theme.themeAccentExclusionKeys;
import static com.same.lib.theme.ThemeManager.changeColorAccent;
import static com.same.lib.theme.ThemeManager.getTempHsv;
import static com.same.lib.theme.ThemeManager.getThemeFileValues;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/26
 * @description null
 * @usage null
 */
public class ThemeAccent {
    public int id;
    public ThemeInfo parentTheme;
    public int accentColor;
    public Skin info;
    public int account;

    public boolean fillAccentColors(HashMap<String, Integer> currentColorsNoAccent, HashMap<String, Integer> currentColors) {
        float[] hsvTemp1 = getTempHsv(1);
        float[] hsvTemp2 = getTempHsv(2);

        Color.colorToHSV(parentTheme.accentBaseColor, hsvTemp1);
        Color.colorToHSV(accentColor, hsvTemp2);
        boolean isDarkTheme = parentTheme.isDark();

        if (accentColor != parentTheme.accentBaseColor) {
            HashSet<String> keys = new HashSet<>(currentColorsNoAccent.keySet());
            keys.addAll(defaultColors.keySet());
            keys.removeAll(themeAccentExclusionKeys);

            for (String key : keys) {
                Integer color = currentColorsNoAccent.get(key);
                if (color == null) {
                    String fallbackKey = fallbackKeys.get(key);
                    if (fallbackKey != null && currentColorsNoAccent.get(fallbackKey) != null) {
                        continue;
                    }
                }
                if (color == null) {
                    color = defaultColors.get(key);
                }

                int newColor = changeColorAccent(hsvTemp1, hsvTemp2, color, isDarkTheme);
                if (newColor != color) {
                    currentColors.put(key, newColor);
                }
            }
        }

        return true;
    }

    public File saveToFile(Context context) {
        File dir = AndroidUtilities.getSharingDirectory(context);
        dir.mkdirs();
        File path = new File(dir, String.format(Locale.US, "%s_%d.attheme", parentTheme.getKey(), id));

        HashMap<String, Integer> currentColorsNoAccent = getThemeFileValues(context, null, parentTheme.assetName, null);
        HashMap<String, Integer> currentColors = new HashMap<>(currentColorsNoAccent);
        fillAccentColors(currentColorsNoAccent, currentColors);

        StringBuilder result = new StringBuilder();
        for (HashMap.Entry<String, Integer> entry : currentColors.entrySet()) {
            String key = entry.getKey();
            result.append(key).append("=").append(entry.getValue()).append("\n");
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(path);
            stream.write(AndroidUtilities.getStringBytes(result.toString()));
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
        return path;
    }

    public static ThemeAccent fromJson() {
        return new ThemeAccent();
    }
}
