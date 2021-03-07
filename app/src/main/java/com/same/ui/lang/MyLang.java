package com.same.ui.lang;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.same.lib.util.Font;
import com.same.ui.MyApplication;
import com.same.ui.theme.span.TypefaceSpan;
import com.timecat.component.locale.LangAction;
import com.timecat.component.locale.LocaleInfo;
import com.timecat.component.locale.MLang;
import com.timecat.component.locale.Util;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/24
 * @description 隔离层
 * @usage 直接使用 MyLang.xxx()
 */
public class MyLang {

    private static File filesDir = getFilesDirFixed(getContext());

    private static LangAction action = new MyLangAction();

    public static void init(@NonNull Context applicationContext) {
        try {
            getInstance(applicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveLanguageKeyInLocal(String language) {
        SharedPreferences preferences = getContext().getSharedPreferences("language_locale", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", language);
        editor.apply();
    }

    @Nullable
    public static String loadLanguageKeyInLocal() {
        SharedPreferences preferences = getContext().getSharedPreferences("language_locale", Context.MODE_PRIVATE);
        return preferences.getString("language", null);
    }

    public static void onConfigurationChanged(@NonNull Configuration newConfig) {
        getInstance().onDeviceConfigurationChange(getContext(), newConfig);
    }

    public static Context getContext() {
        return MyApplication.applicationContext;
    }

    public static MLang getInstance() {
        return getInstance(getContext());
    }

    public static MLang getInstance(Context context) {
        return MLang.getInstance(context, filesDir, action);
    }

    public static File getFilesDirFixed(Context context) {
        return Util.getFilesDirFixed(context, "/data/data/com.locale.ui/files");
    }

    public static void loadRemoteLanguages(final Context context, final MLang.FinishLoadCallback callback) {
        getInstance().loadRemoteLanguages(context, callback);
    }

    public static void applyLanguage(Context context, LocaleInfo localeInfo) {
        getInstance().applyLanguage(context, localeInfo);
    }

    public static String getSystemLocaleStringIso639() {
        return getInstance().getSystemLocaleStringIso639();
    }

    public static String getLocaleStringIso639() {
        return getInstance().getLocaleStringIso639();
    }

    public static String getLocaleAlias(String code) {
        return MLang.getLocaleAlias(code);
    }

    public static String getCurrentLanguageName() {
        return getInstance().getCurrentLanguageName(getContext());
    }

    public static String getServerString(String key) {
        return getInstance().getServerString(getContext(), key);
    }

    public static String getString(String key, int res) {
        return getInstance().getString(getContext(), key, res);
    }

    public static String getString(String key) {
        return getInstance().getString(getContext(), key);
    }

    public static String getPluralString(String key, int plural) {
        return getInstance().getPluralString(getContext(), key, plural);
    }

    public static String formatPluralString(String key, int plural) {
        return getInstance().formatPluralString(getContext(), key, plural);
    }

    public static String formatPluralStringComma(String key, int plural) {
        return getInstance().formatPluralStringComma(getContext(), key, plural);
    }

    public static String formatString(String key, int res, Object... args) {
        return getInstance().formatString(getContext(), key, res, args);
    }

    public static String formatTTLString(int ttl) {
        return getInstance().formatTTLString(getContext(), ttl);
    }

    public static String formatStringSimple(String string, Object... args) {
        return getInstance().formatStringSimple(getContext(), string, args);
    }

    public static String formatCallDuration(int duration) {
        return getInstance().formatCallDuration(getContext(), duration);
    }

    public static String formatDateChat(long date) {
        return getInstance().formatDateChat(getContext(), date);
    }

    public static String formatDateChat(long date, boolean checkYear) {
        return getInstance().formatDateChat(getContext(), date, checkYear);
    }

    public static String formatDate(long date) {
        return getInstance().formatDate(getContext(), date);
    }

    public static String formatDateAudio(long date, boolean shortFormat) {
        return getInstance().formatDateAudio(getContext(), date, shortFormat);
    }

    public static String formatDateCallLog(long date) {
        return getInstance().formatDateCallLog(getContext(), date);
    }

    public static String formatLocationUpdateDate(long date, long timeFromServer) {
        return getInstance().formatLocationUpdateDate(getContext(), date, timeFromServer);
    }

    public static String formatLocationLeftTime(int time) {
        return MLang.formatLocationLeftTime(time);
    }

    public static String formatDateOnline(long date) {
        return getInstance().formatDateOnline(getContext(), date);
    }

    public static boolean isRTLCharacter(char ch) {
        return MLang.isRTLCharacter(ch);
    }

    public static String formatSectionDate(long date) {
        return getInstance().formatSectionDate(getContext(), date);
    }

    public static String formatDateForBan(long date) {
        return getInstance().formatDateForBan(getContext(), date);
    }

    public static String stringForMessageListDate(long date) {
        return getInstance().stringForMessageListDate(getContext(), date);
    }

    public static String formatShortNumber(int number, int[] rounded) {
        return MLang.formatShortNumber(number, rounded);
    }

    public static void resetImperialSystemType() {
        MLang.resetImperialSystemType();
    }

    public static String formatDistance(float distance) {
        return getInstance().formatDistance(getContext(), distance);
    }

    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_COLOR = 4;
    public static final int FLAG_TAG_URL = 8;
    public static final int FLAG_TAG_ALL = FLAG_TAG_BR | FLAG_TAG_BOLD | FLAG_TAG_URL;

    public static SpannableStringBuilder replaceTags(Context context, String str) {
        return replaceTags(context, str, FLAG_TAG_ALL);
    }

    public static SpannableStringBuilder replaceTags(Context context, String str, int flag, Object... args) {
        try {
            int start;
            int end;
            StringBuilder stringBuilder = new StringBuilder(str);
            if ((flag & FLAG_TAG_BR) != 0) {
                while ((start = stringBuilder.indexOf("<br>")) != -1) {
                    stringBuilder.replace(start, start + 4, "\n");
                }
                while ((start = stringBuilder.indexOf("<br/>")) != -1) {
                    stringBuilder.replace(start, start + 5, "\n");
                }
            }
            ArrayList<Integer> bolds = new ArrayList<>();
            if ((flag & FLAG_TAG_BOLD) != 0) {
                while ((start = stringBuilder.indexOf("<b>")) != -1) {
                    stringBuilder.replace(start, start + 3, "");
                    end = stringBuilder.indexOf("</b>");
                    if (end == -1) {
                        end = stringBuilder.indexOf("<b>");
                    }
                    stringBuilder.replace(end, end + 4, "");
                    bolds.add(start);
                    bolds.add(end);
                }
                while ((start = stringBuilder.indexOf("**")) != -1) {
                    stringBuilder.replace(start, start + 2, "");
                    end = stringBuilder.indexOf("**");
                    if (end >= 0) {
                        stringBuilder.replace(end, end + 2, "");
                        bolds.add(start);
                        bolds.add(end);
                    }
                }
            }
            if ((flag & FLAG_TAG_URL) != 0) {
                while ((start = stringBuilder.indexOf("**")) != -1) {
                    stringBuilder.replace(start, start + 2, "");
                    end = stringBuilder.indexOf("**");
                    if (end >= 0) {
                        stringBuilder.replace(end, end + 2, "");
                        bolds.add(start);
                        bolds.add(end);
                    }
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (int a = 0; a < bolds.size() / 2; a++) {
                spannableStringBuilder.setSpan(new TypefaceSpan(Font.getMediumTypeface(context)), bolds.get(a * 2), bolds.get(a * 2 + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SpannableStringBuilder(str);
    }
}
