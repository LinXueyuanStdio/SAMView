package com.same.lib.theme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.same.lib.util.AndroidUtilities;

import org.json.JSONObject;

import java.io.File;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/26
 * @description null
 * @usage null
 */
public class OverrideWallpaperInfo {
    public String fileName = "";
    public String originalFileName = "";
    public String slug = "";
    public int color;
    public int gradientColor;
    public int rotation;
    public boolean isBlurred;
    public boolean isMotion;
    public float intensity;

    public ThemeInfo parentTheme;
    public ThemeAccent parentAccent;

    public OverrideWallpaperInfo() {

    }

    public OverrideWallpaperInfo(OverrideWallpaperInfo info, ThemeInfo themeInfo, ThemeAccent accent) {
        slug = info.slug;
        color = info.color;
        gradientColor = info.gradientColor;
        rotation = info.rotation;
        isBlurred = info.isBlurred;
        isMotion = info.isMotion;
        intensity = info.intensity;
        parentTheme = themeInfo;
        parentAccent = accent;
        if (!TextUtils.isEmpty(info.fileName)) {
            try {
                File fromFile = new File(AndroidUtilities.getFilesDirFixed(), info.fileName);
                File toFile = new File(AndroidUtilities.getFilesDirFixed(), fileName = parentTheme.generateWallpaperName(parentAccent, false));
                AndroidUtilities.copyFile(fromFile, toFile);
            } catch (Exception e) {
                fileName = "";
                e.printStackTrace();
            }
        } else {
            fileName = "";
        }
        if (!TextUtils.isEmpty(info.originalFileName)) {
            if (!info.originalFileName.equals(info.fileName)) {
                try {
                    File fromFile = new File(AndroidUtilities.getFilesDirFixed(), info.originalFileName);
                    File toFile = new File(AndroidUtilities.getFilesDirFixed(), originalFileName = parentTheme.generateWallpaperName(parentAccent, true));
                    AndroidUtilities.copyFile(fromFile, toFile);
                } catch (Exception e) {
                    originalFileName = "";
                    e.printStackTrace();
                }
            } else {
                originalFileName = fileName;
            }
        } else {
            originalFileName = "";
        }
    }

    public boolean isDefault() {
        return Theme.DEFAULT_BACKGROUND_SLUG.equals(slug);
    }

    public boolean isColor() {
        return Theme.COLOR_BACKGROUND_SLUG.equals(slug);
    }

    public boolean isTheme() {
        return Theme.THEME_BACKGROUND_SLUG.equals(slug);
    }

    public void saveOverrideWallpaper(Context context) {
        if (parentTheme == null || parentAccent == null && parentTheme.overrideWallpaper != this || parentAccent != null && parentAccent.overrideWallpaper != this) {
            return;
        }
        save(context);
    }

    private String getKey() {
        if (parentAccent != null) {
            return parentTheme.name + "_" + parentAccent.id + "_owp";
        } else {
            return parentTheme.name + "_owp";
        }
    }

    void save(Context context) {
        try {
            String key = getKey();
            SharedPreferences themeConfig = context.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = themeConfig.edit();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("wall", fileName);
            jsonObject.put("owall", originalFileName);
            jsonObject.put("pColor", color);
            jsonObject.put("pGrColor", gradientColor);
            jsonObject.put("pGrAngle", rotation);
            jsonObject.put("wallSlug", slug != null ? slug : "");
            jsonObject.put("wBlur", isBlurred);
            jsonObject.put("wMotion", isMotion);
            jsonObject.put("pIntensity", intensity);
            editor.putString(key, jsonObject.toString());
            editor.apply();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    void delete(Context context) {
        String key = getKey();
        SharedPreferences themeConfig = context.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
        themeConfig.edit().remove(key).apply();
        new File(AndroidUtilities.getFilesDirFixed(), fileName).delete();
        new File(AndroidUtilities.getFilesDirFixed(), originalFileName).delete();
    }
}
