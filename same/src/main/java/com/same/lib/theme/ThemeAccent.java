package com.same.lib.theme;

import android.graphics.Color;
import android.text.TextUtils;

import com.same.lib.util.AndroidUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import static com.same.lib.theme.ThemeManager.*;

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
    public int myMessagesAccentColor;
    public int myMessagesGradientAccentColor;
    public long backgroundOverrideColor;
    public long backgroundGradientOverrideColor;
    public int backgroundRotation = 45;
    public String patternSlug = "";
    public float patternIntensity;
    public boolean patternMotion;

    public Skin info;
    public int account;

    public String pathToFile;
    public String uploadingThumb;
    public String uploadingFile;

    public OverrideWallpaperInfo overrideWallpaper;

    public boolean fillAccentColors(HashMap<String, Integer> currentColorsNoAccent, HashMap<String, Integer> currentColors) {
        boolean isMyMessagesGradientColorsNear = false;

        float[] hsvTemp1 = getTempHsv(1);
        float[] hsvTemp2 = getTempHsv(2);

        Color.colorToHSV(parentTheme.accentBaseColor, hsvTemp1);
        Color.colorToHSV(accentColor, hsvTemp2);
        boolean isDarkTheme = parentTheme.isDark();

        if (accentColor != parentTheme.accentBaseColor) {
            HashSet<String> keys = new HashSet<>(currentColorsNoAccent.keySet());
            keys.addAll(Theme.defaultColors.keySet());
            keys.removeAll(Theme.themeAccentExclusionKeys);

            for (String key : keys) {
                Integer color = currentColorsNoAccent.get(key);
                if (color == null) {
                    String fallbackKey = Theme.fallbackKeys.get(key);
                    if (fallbackKey != null && currentColorsNoAccent.get(fallbackKey) != null) {
                        continue;
                    }
                }
                if (color == null) {
                    color = Theme.defaultColors.get(key);
                }

                int newColor = changeColorAccent(hsvTemp1, hsvTemp2, color, isDarkTheme);
                if (newColor != color) {
                    currentColors.put(key, newColor);
                }
            }
        }
        int myMessagesAccent = myMessagesAccentColor;
        if ((myMessagesAccentColor != 0 || accentColor != 0) && myMessagesGradientAccentColor != 0) {
            int firstColor = myMessagesAccentColor != 0 ? myMessagesAccentColor : accentColor;
            Integer color = currentColorsNoAccent.get(Theme.key_chat_outBubble);
            if (color == null) {
                color = Theme.defaultColors.get(Theme.key_chat_outBubble);
            }
            int newColor = changeColorAccent(hsvTemp1, hsvTemp2, color, isDarkTheme);
            int distance1 = AndroidUtilities.getColorDistance(firstColor, newColor);
            int distance2 = AndroidUtilities.getColorDistance(firstColor, myMessagesGradientAccentColor);
            isMyMessagesGradientColorsNear = distance1 <= 35000 && distance2 <= 35000;
            myMessagesAccent = getAccentColor(hsvTemp1, color, firstColor);
        }

        if (myMessagesAccent != 0 && (parentTheme.accentBaseColor != 0 && myMessagesAccent != parentTheme.accentBaseColor || accentColor != 0 && accentColor != myMessagesAccent)) {
            Color.colorToHSV(myMessagesAccent, hsvTemp2);
            for (String key : Theme.myMessagesColorKeys) {
                Integer color = currentColorsNoAccent.get(key);
                if (color == null) {
                    String fallbackKey = Theme.fallbackKeys.get(key);
                    if (fallbackKey != null && currentColorsNoAccent.get(fallbackKey) != null) {
                        continue;
                    }
                }
                if (color == null) {
                    color = Theme.defaultColors.get(key);
                }
                if (color == null) {
                    continue;
                }
                int newColor = changeColorAccent(hsvTemp1, hsvTemp2, color, isDarkTheme);
                if (newColor != color) {
                    currentColors.put(key, newColor);
                }
            }
        }
        if (!isMyMessagesGradientColorsNear) {
            if (myMessagesGradientAccentColor != 0) {
                int textColor;
                int subTextColor;
                int seekbarColor;
                if (useBlackText(myMessagesAccentColor, myMessagesGradientAccentColor)) {
                    textColor = 0xff000000;
                    subTextColor = 0xff555555;
                    seekbarColor = 0x4d000000;
                } else {
                    textColor = 0xffffffff;
                    subTextColor = 0xffeeeeee;
                    seekbarColor = 0x4dffffff;
                }

                currentColors.put(Theme.key_chat_outAudioProgress, seekbarColor);
                currentColors.put(Theme.key_chat_outAudioSelectedProgress, seekbarColor);
                currentColors.put(Theme.key_chat_outAudioSeekbar, seekbarColor);
                currentColors.put(Theme.key_chat_outAudioCacheSeekbar, seekbarColor);
                currentColors.put(Theme.key_chat_outAudioSeekbarSelected, seekbarColor);
                currentColors.put(Theme.key_chat_outAudioSeekbarFill, textColor);

                currentColors.put(Theme.key_chat_outVoiceSeekbar, seekbarColor);
                currentColors.put(Theme.key_chat_outVoiceSeekbarSelected, seekbarColor);
                currentColors.put(Theme.key_chat_outVoiceSeekbarFill, textColor);

                currentColors.put(Theme.key_chat_messageTextOut, textColor);
                currentColors.put(Theme.key_chat_messageLinkOut, textColor);
                currentColors.put(Theme.key_chat_outForwardedNameText, textColor);
                currentColors.put(Theme.key_chat_outViaBotNameText, textColor);
                currentColors.put(Theme.key_chat_outReplyLine, textColor);
                currentColors.put(Theme.key_chat_outReplyNameText, textColor);

                currentColors.put(Theme.key_chat_outPreviewLine, textColor);
                currentColors.put(Theme.key_chat_outSiteNameText, textColor);
                currentColors.put(Theme.key_chat_outInstant, textColor);
                currentColors.put(Theme.key_chat_outInstantSelected, textColor);
                currentColors.put(Theme.key_chat_outPreviewInstantText, textColor);
                currentColors.put(Theme.key_chat_outPreviewInstantSelectedText, textColor);

                currentColors.put(Theme.key_chat_outViews, textColor);

                currentColors.put(Theme.key_chat_outAudioTitleText, textColor);
                currentColors.put(Theme.key_chat_outFileNameText, textColor);
                currentColors.put(Theme.key_chat_outContactNameText, textColor);

                currentColors.put(Theme.key_chat_outAudioPerformerText, textColor);
                currentColors.put(Theme.key_chat_outAudioPerformerSelectedText, textColor);

                currentColors.put(Theme.key_chat_outSentCheck, textColor);
                currentColors.put(Theme.key_chat_outSentCheckSelected, textColor);

                currentColors.put(Theme.key_chat_outSentCheckRead, textColor);
                currentColors.put(Theme.key_chat_outSentCheckReadSelected, textColor);

                currentColors.put(Theme.key_chat_outSentClock, textColor);
                currentColors.put(Theme.key_chat_outSentClockSelected, textColor);

                currentColors.put(Theme.key_chat_outMenu, textColor);
                currentColors.put(Theme.key_chat_outMenuSelected, textColor);

                currentColors.put(Theme.key_chat_outTimeText, textColor);
                currentColors.put(Theme.key_chat_outTimeSelectedText, textColor);

                currentColors.put(Theme.key_chat_outAudioDurationText, subTextColor);
                currentColors.put(Theme.key_chat_outAudioDurationSelectedText, subTextColor);

                currentColors.put(Theme.key_chat_outContactPhoneText, subTextColor);
                currentColors.put(Theme.key_chat_outContactPhoneSelectedText, subTextColor);

                currentColors.put(Theme.key_chat_outFileInfoText, subTextColor);
                currentColors.put(Theme.key_chat_outFileInfoSelectedText, subTextColor);

                currentColors.put(Theme.key_chat_outVenueInfoText, subTextColor);
                currentColors.put(Theme.key_chat_outVenueInfoSelectedText, subTextColor);

                currentColors.put(Theme.key_chat_outReplyMessageText, textColor);
                currentColors.put(Theme.key_chat_outReplyMediaMessageText, textColor);
                currentColors.put(Theme.key_chat_outReplyMediaMessageSelectedText, textColor);

                currentColors.put(Theme.key_chat_outLoader, textColor);
                currentColors.put(Theme.key_chat_outLoaderSelected, textColor);
                currentColors.put(Theme.key_chat_outFileProgress, myMessagesAccentColor);
                currentColors.put(Theme.key_chat_outFileProgressSelected, myMessagesAccentColor);
                currentColors.put(Theme.key_chat_outMediaIcon, myMessagesAccentColor);
                currentColors.put(Theme.key_chat_outMediaIconSelected, myMessagesAccentColor);
            }
        }
        if (isMyMessagesGradientColorsNear) {
            int outColor = currentColors.get(Theme.key_chat_outLoader);
            if (AndroidUtilities.getColorDistance(0xffffffff, outColor) < 5000) {
                isMyMessagesGradientColorsNear = false;
            }
        }
        if (myMessagesAccentColor != 0 && myMessagesGradientAccentColor != 0) {
            currentColors.put(Theme.key_chat_outBubble, myMessagesAccentColor);
            currentColors.put(Theme.key_chat_outBubbleGradient, myMessagesGradientAccentColor);
        }
        int backgroundOverride = (int) backgroundOverrideColor;
        if (backgroundOverride != 0) {
            currentColors.put(Theme.key_chat_wallpaper, backgroundOverride);
        } else if (backgroundOverrideColor != 0) {
            currentColors.remove(Theme.key_chat_wallpaper);
        }
        int backgroundGradientOverride = (int) backgroundGradientOverrideColor;
        if (backgroundGradientOverride != 0) {
            currentColors.put(Theme.key_chat_wallpaper_gradient_to, backgroundGradientOverride);
        } else if (backgroundGradientOverrideColor != 0) {
            currentColors.remove(Theme.key_chat_wallpaper_gradient_to);
        }
        if (backgroundRotation != 45) {
            currentColors.put(Theme.key_chat_wallpaper_gradient_rotation, backgroundRotation);
        }
        return !isMyMessagesGradientColorsNear;
    }

    public File getPathToWallpaper() {
        return !TextUtils.isEmpty(patternSlug) ? new File(AndroidUtilities.getFilesDirFixed(), String.format(Locale.US, "%s_%d_%s.jpg", parentTheme.getKey(), id, patternSlug)) : null;
    }

    public File saveToFile() {
        File dir = AndroidUtilities.getSharingDirectory();
        dir.mkdirs();
        File path = new File(dir, String.format(Locale.US, "%s_%d.attheme", parentTheme.getKey(), id));

        HashMap<String, Integer> currentColorsNoAccent = getThemeFileValues(null, parentTheme.assetName, null);
        HashMap<String, Integer> currentColors = new HashMap<>(currentColorsNoAccent);
        fillAccentColors(currentColorsNoAccent, currentColors);

        String wallpaperLink = null;

        if (!TextUtils.isEmpty(patternSlug)) {
            StringBuilder modes = new StringBuilder();
            if (patternMotion) {
                modes.append("motion");
            }
            Integer selectedColor = currentColors.get(Theme.key_chat_wallpaper);
            if (selectedColor == null) {
                selectedColor = 0xffffffff;
            }
            Integer selectedGradientColor = currentColors.get(Theme.key_chat_wallpaper_gradient_to);
            if (selectedGradientColor == null) {
                selectedGradientColor = 0;
            }
            Integer selectedGradientRotation = currentColors.get(Theme.key_chat_wallpaper_gradient_rotation);
            if (selectedGradientRotation == null) {
                selectedGradientRotation = 45;
            }
            String color = String.format("%02x%02x%02x", (byte) (selectedColor >> 16) & 0xff, (byte) (selectedColor >> 8) & 0xff, (byte) (selectedColor & 0xff)).toLowerCase();
            String color2 = selectedGradientColor != 0 ? String.format("%02x%02x%02x", (byte) (selectedGradientColor >> 16) & 0xff, (byte) (selectedGradientColor >> 8) & 0xff, (byte) (selectedGradientColor & 0xff)).toLowerCase() : null;
            if (color2 != null) {
                color += "-" + color2;
                color += "&rotation=" + selectedGradientRotation;
            }
            wallpaperLink = "https://attheme.org?slug=" + patternSlug + "&intensity=" + (int) (patternIntensity * 100) + "&bg_color=" + color;
            if (modes.length() > 0) {
                wallpaperLink += "&mode=" + modes.toString();
            }
        }

        StringBuilder result = new StringBuilder();
        for (HashMap.Entry<String, Integer> entry : currentColors.entrySet()) {
            String key = entry.getKey();
            if (wallpaperLink != null) {
                if (Theme.key_chat_wallpaper.equals(key) || Theme.key_chat_wallpaper_gradient_to.equals(key)) {
                    continue;
                }
            }
            result.append(key).append("=").append(entry.getValue()).append("\n");
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(path);
            stream.write(AndroidUtilities.getStringBytes(result.toString()));
            if (!TextUtils.isEmpty(wallpaperLink)) {
                stream.write(AndroidUtilities.getStringBytes("WLS=" + wallpaperLink + "\n"));
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
        return path;
    }

    public static ThemeAccent fromJson(){
        return new ThemeAccent();
    }
}
