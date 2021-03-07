package com.same.lib.theme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuffColorFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.Window;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static com.same.lib.theme.ThemeManager.applyDayNightThemeMaybe;
import static com.same.lib.theme.ThemeManager.applyTheme;
import static com.same.lib.theme.ThemeManager.changeColorAccent;
import static com.same.lib.theme.ThemeManager.getTempHsv;
import static com.same.lib.theme.ThemeManager.needSwitchToTheme;
import static com.same.lib.theme.ThemeManager.saveOtherThemes;
import static com.same.lib.theme.ThemeManager.sortThemes;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/8/25
 * @description null
 * @usage null
 * 主题管理
 */
public class Theme {

    public static final String DEFAULT_BACKGROUND_SLUG = "d";
    public static final String THEME_BACKGROUND_SLUG = "t";
    public static final String COLOR_BACKGROUND_SLUG = "c";

    //region field

    public static final int ACTION_BAR_PHOTO_VIEWER_COLOR = 0x7f000000;
    public static final int ACTION_BAR_MEDIA_PICKER_COLOR = 0xff333333;
    public static final int ACTION_BAR_VIDEO_EDIT_COLOR = 0xff000000;
    public static final int ACTION_BAR_PLAYER_COLOR = 0xffffffff;
    public static final int ACTION_BAR_PICKER_SELECTOR_COLOR = 0xff3d3d3d;
    public static final int ACTION_BAR_WHITE_SELECTOR_COLOR = 0x40ffffff;
    public static final int ACTION_BAR_AUDIO_SELECTOR_COLOR = 0x2f000000;
    public static final int ARTICLE_VIEWER_MEDIA_PROGRESS_COLOR = 0xffffffff;

    public static final int AUTO_NIGHT_TYPE_NONE = 0;
    public static final int AUTO_NIGHT_TYPE_SCHEDULED = 1;
    public static final int AUTO_NIGHT_TYPE_AUTOMATIC = 2;
    public static final int AUTO_NIGHT_TYPE_SYSTEM = 3;

    static final int LIGHT_SENSOR_THEME_SWITCH_DELAY = 1800;
    static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_DELAY = 12000;
    static final int LIGHT_SENSOR_THEME_SWITCH_NEAR_THRESHOLD = 12000;
    static SensorManager sensorManager;
    static Sensor lightSensor;
    static boolean lightSensorRegistered;
    static float lastBrightnessValue = 1.0f;
    static long lastThemeSwitchTime;
    static boolean switchDayRunnableScheduled;
    static boolean switchNightRunnableScheduled;
    static Runnable switchDayBrightnessRunnable = new Runnable() {
        @Override
        public void run() {
            switchDayRunnableScheduled = false;
            applyDayNightThemeMaybe(false);
        }
    };
    static Runnable switchNightBrightnessRunnable = new Runnable() {
        @Override
        public void run() {
            switchNightRunnableScheduled = false;
            applyDayNightThemeMaybe(true);
        }
    };

    public static int DEFALT_THEME_ACCENT_ID = 99;
    public static int selectedAutoNightType = AUTO_NIGHT_TYPE_NONE;
    public static boolean autoNightScheduleByLocation;
    public static float autoNightBrighnessThreshold = 0.25f;
    public static int autoNightDayStartTime = 22 * 60;
    public static int autoNightDayEndTime = 8 * 60;
    public static int autoNightSunsetTime = 22 * 60;
    public static int autoNightLastSunCheckDay = -1;
    public static int autoNightSunriseTime = 8 * 60;
    public static String autoNightCityName = "";
    public static double autoNightLocationLatitude = 10000;
    public static double autoNightLocationLongitude = 10000;

    public static ArrayList<ThemeInfo> themes;
    static ArrayList<ThemeInfo> otherThemes;
    static HashMap<String, ThemeInfo> themesDict;
    static ThemeInfo currentTheme;
    static ThemeInfo currentNightTheme;
    static ThemeInfo currentDayTheme;
    static ThemeInfo defaultTheme;
    static ThemeInfo previousTheme;
    static boolean hasPreviousTheme;
    static boolean isApplyingAccent;
    static boolean switchingNightTheme;
    static boolean isInNigthMode;

    static int switchNightThemeDelay;
    static long lastDelayUpdateTime;

    public static PorterDuffColorFilter colorFilter;
    static boolean isCustomTheme;

    static HashMap<String, Integer> defaultColors = new HashMap<>();
    static HashMap<String, String> fallbackKeys = new HashMap<>();
    static HashSet<String> themeAccentExclusionKeys = new HashSet<>();
    static HashMap<String, Integer> currentColorsNoAccent;
    public static HashMap<String, Integer> currentColors;
    static HashMap<String, Integer> animatingColors;
    public static boolean shouldDrawGradientIcons;

    static ThreadLocal<float[]> hsvTemp1Local = new ThreadLocal<>();
    static ThreadLocal<float[]> hsvTemp2Local = new ThreadLocal<>();
    static ThreadLocal<float[]> hsvTemp3Local = new ThreadLocal<>();
    static ThreadLocal<float[]> hsvTemp4Local = new ThreadLocal<>();
    static ThreadLocal<float[]> hsvTemp5Local = new ThreadLocal<>();
    //endregion

    static {
        //region defaultColors
        defaultColors.put(KeyHub.key_dialogBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_dialogBackgroundGray, 0xfff0f0f0);
        defaultColors.put(KeyHub.key_dialogTextBlack, 0xff222222);
        defaultColors.put(KeyHub.key_dialogTextLink, 0xff2678b6);
        defaultColors.put(KeyHub.key_dialogLinkSelection, 0x3362a9e3);
        defaultColors.put(KeyHub.key_dialogTextRed, 0xffcd5a5a);
        defaultColors.put(KeyHub.key_dialogTextRed2, 0xffde3a3a);
        defaultColors.put(KeyHub.key_dialogTextBlue, 0xff2f8cc9);
        defaultColors.put(KeyHub.key_dialogTextBlue2, 0xff3a95d5);
        defaultColors.put(KeyHub.key_dialogTextBlue3, 0xff3ec1f9);
        defaultColors.put(KeyHub.key_dialogTextBlue4, 0xff19a7e8);
        defaultColors.put(KeyHub.key_dialogTextGray, 0xff348bc1);
        defaultColors.put(KeyHub.key_dialogTextGray2, 0xff757575);
        defaultColors.put(KeyHub.key_dialogTextGray3, 0xff999999);
        defaultColors.put(KeyHub.key_dialogTextGray4, 0xffb3b3b3);
        defaultColors.put(KeyHub.key_dialogTextHint, 0xff979797);
        defaultColors.put(KeyHub.key_dialogIcon, 0xff676b70);
        defaultColors.put(KeyHub.key_dialogRedIcon, 0xffe14d4d);
        defaultColors.put(KeyHub.key_dialogGrayLine, 0xffd2d2d2);
        defaultColors.put(KeyHub.key_dialogTopBackground, 0xff6fb2e5);
        defaultColors.put(KeyHub.key_dialogInputField, 0xffdbdbdb);
        defaultColors.put(KeyHub.key_dialogInputFieldActivated, 0xff37a9f0);
        defaultColors.put(KeyHub.key_dialogCheckboxSquareBackground, 0xff43a0df);
        defaultColors.put(KeyHub.key_dialogCheckboxSquareCheck, 0xffffffff);
        defaultColors.put(KeyHub.key_dialogCheckboxSquareUnchecked, 0xff737373);
        defaultColors.put(KeyHub.key_dialogCheckboxSquareDisabled, 0xffb0b0b0);
        defaultColors.put(KeyHub.key_dialogRadioBackground, 0xffb3b3b3);
        defaultColors.put(KeyHub.key_dialogRadioBackgroundChecked, 0xff37a9f0);
        defaultColors.put(KeyHub.key_dialogProgressCircle, 0xff289deb);
        defaultColors.put(KeyHub.key_dialogLineProgress, 0xff527da3);
        defaultColors.put(KeyHub.key_dialogLineProgressBackground, 0xffdbdbdb);
        defaultColors.put(KeyHub.key_dialogButton, 0xff4991cc);
        defaultColors.put(KeyHub.key_dialogButtonSelector, 0x0f000000);
        defaultColors.put(KeyHub.key_dialogScrollGlow, 0xfff5f6f7);
        defaultColors.put(KeyHub.key_dialogRoundCheckBox, 0xff4cb4f5);
        defaultColors.put(KeyHub.key_dialogRoundCheckBoxCheck, 0xffffffff);
        defaultColors.put(KeyHub.key_dialogBadgeBackground, 0xff3ec1f9);
        defaultColors.put(KeyHub.key_dialogBadgeText, 0xffffffff);
        defaultColors.put(KeyHub.key_dialogCameraIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_dialog_inlineProgressBackground, 0xf6f0f2f5);
        defaultColors.put(KeyHub.key_dialog_inlineProgress, 0xff6b7378);
        defaultColors.put(KeyHub.key_dialogSearchBackground, 0xfff2f4f5);
        defaultColors.put(KeyHub.key_dialogSearchHint, 0xff98a0a7);
        defaultColors.put(KeyHub.key_dialogSearchIcon, 0xffa1a8af);
        defaultColors.put(KeyHub.key_dialogSearchText, 0xff222222);
        defaultColors.put(KeyHub.key_dialogFloatingButton, 0xff4cb4f5);
        defaultColors.put(KeyHub.key_dialogFloatingButtonPressed, 0x0f000000);
        defaultColors.put(KeyHub.key_dialogFloatingIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_dialogShadowLine, 0x12000000);
        defaultColors.put(KeyHub.key_dialogEmptyImage, 0xff9fa4a8);
        defaultColors.put(KeyHub.key_dialogEmptyText, 0xff8c9094);

        defaultColors.put(KeyHub.key_windowBackgroundWhite, 0xffffffff);
        defaultColors.put(KeyHub.key_windowBackgroundUnchecked, 0xff9da7b1);
        defaultColors.put(KeyHub.key_windowBackgroundChecked, 0xff579ed9);
        defaultColors.put(KeyHub.key_windowBackgroundCheckText, 0xffffffff);
        defaultColors.put(KeyHub.key_progressCircle, 0xff1c93e3);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGrayIcon, 0xff81868b);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlueText, 0xff4092cd);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlueText2, 0xff3a95d5);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlueText3, 0xff2678b6);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlueText4, 0xff1c93e3);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlueText5, 0xff4c8eca);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlueText6, 0xff3a8ccf);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlueText7, 0xff377aae);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlueButton, 0xff1e88d3);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlueIcon, 0xff379de5);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGreenText, 0xff26972c);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGreenText2, 0xff37a818);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteRedText, 0xffcd5a5a);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteRedText2, 0xffdb5151);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteRedText3, 0xffd24949);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteRedText4, 0xffcf3030);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteRedText5, 0xffed3939);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteRedText6, 0xffff6666);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGrayText, 0xff838c96);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGrayText2, 0xff82868a);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGrayText3, 0xff999999);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGrayText4, 0xff808080);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGrayText5, 0xffa3a3a3);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGrayText6, 0xff757575);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGrayText7, 0xffc6c6c6);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGrayText8, 0xff6d6d72);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteGrayLine, 0xffdbdbdb);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlackText, 0xff222222);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteHintText, 0xffa8a8a8);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteValueText, 0xff3a95d5);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteLinkText, 0xff2678b6);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteLinkSelection, 0x3362a9e3);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteBlueHeader, 0xff3a95d5);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteInputField, 0xffdbdbdb);
        defaultColors.put(KeyHub.key_windowBackgroundWhiteInputFieldActivated, 0xff37a9f0);
        defaultColors.put(KeyHub.key_switchTrack, 0xffb0b5ba);
        defaultColors.put(KeyHub.key_switchTrackChecked, 0xff52ade9);
        defaultColors.put(KeyHub.key_switchTrackBlue, 0xff828e99);
        defaultColors.put(KeyHub.key_switchTrackBlueChecked, 0xff3c88c7);
        defaultColors.put(KeyHub.key_switchTrackBlueThumb, 0xffffffff);
        defaultColors.put(KeyHub.key_switchTrackBlueThumbChecked, 0xffffffff);
        defaultColors.put(KeyHub.key_switchTrackBlueSelector, 0x17404a53);
        defaultColors.put(KeyHub.key_switchTrackBlueSelectorChecked, 0x21024781);
        defaultColors.put(KeyHub.key_switch2Track, 0xfff57e7e);
        defaultColors.put(KeyHub.key_switch2TrackChecked, 0xff52ade9);
        defaultColors.put(KeyHub.key_checkboxSquareBackground, 0xff43a0df);
        defaultColors.put(KeyHub.key_checkboxSquareCheck, 0xffffffff);
        defaultColors.put(KeyHub.key_checkboxSquareUnchecked, 0xff737373);
        defaultColors.put(KeyHub.key_checkboxSquareDisabled, 0xffb0b0b0);
        defaultColors.put(KeyHub.key_listSelector, 0x0f000000);
        defaultColors.put(KeyHub.key_radioBackground, 0xffb3b3b3);
        defaultColors.put(KeyHub.key_radioBackgroundChecked, 0xff37a9f0);
        defaultColors.put(KeyHub.key_windowBackgroundGray, 0xfff0f0f0);
        defaultColors.put(KeyHub.key_windowBackgroundGrayShadow, 0xff000000);
        defaultColors.put(KeyHub.key_emptyListPlaceholder, 0xff959595);
        defaultColors.put(KeyHub.key_divider, 0xffd9d9d9);
        defaultColors.put(KeyHub.key_graySection, 0xfff5f5f5);
        defaultColors.put(KeyHub.key_graySectionText, 0xff82878A);
        defaultColors.put(KeyHub.key_fastScrollActive, 0xff52a3db);
        defaultColors.put(KeyHub.key_fastScrollInactive, 0xffc9cdd1);
        defaultColors.put(KeyHub.key_fastScrollText, 0xffffffff);

        defaultColors.put(KeyHub.key_actionBarDefault, 0xff527da3);
        defaultColors.put(KeyHub.key_actionBarDefaultIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_actionBarActionModeDefault, 0xffffffff);
        defaultColors.put(KeyHub.key_actionBarActionModeDefaultTop, 0x10000000);
        defaultColors.put(KeyHub.key_actionBarActionModeDefaultIcon, 0xff676a6f);
        defaultColors.put(KeyHub.key_actionBarDefaultTitle, 0xffffffff);
        defaultColors.put(KeyHub.key_actionBarDefaultSubtitle, 0xffd5e8f7);
        defaultColors.put(KeyHub.key_actionBarDefaultSelector, 0xff406d94);
        defaultColors.put(KeyHub.key_actionBarWhiteSelector, 0x1d000000);
        defaultColors.put(KeyHub.key_actionBarDefaultSearch, 0xffffffff);
        defaultColors.put(KeyHub.key_actionBarDefaultSearchPlaceholder, 0x88ffffff);
        defaultColors.put(KeyHub.key_actionBarDefaultSubmenuItem, 0xff222222);
        defaultColors.put(KeyHub.key_actionBarDefaultSubmenuItemIcon, 0xff676b70);
        defaultColors.put(KeyHub.key_actionBarDefaultSubmenuBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_actionBarActionModeDefaultSelector, 0xffe2e2e2);
        defaultColors.put(KeyHub.key_actionBarTabActiveText, 0xffffffff);
        defaultColors.put(KeyHub.key_actionBarTabUnactiveText, 0xffd5e8f7);
        defaultColors.put(KeyHub.key_actionBarTabLine, 0xffffffff);
        defaultColors.put(KeyHub.key_actionBarTabSelector, 0xff406d94);

        defaultColors.put(KeyHub.key_actionBarBrowser, 0xffffffff);

        defaultColors.put(KeyHub.key_actionBarDefaultArchived, 0xff6f7a87);
        defaultColors.put(KeyHub.key_actionBarDefaultArchivedSelector, 0xff5e6772);
        defaultColors.put(KeyHub.key_actionBarDefaultArchivedIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_actionBarDefaultArchivedTitle, 0xffffffff);
        defaultColors.put(KeyHub.key_actionBarDefaultArchivedSearch, 0xffffffff);
        defaultColors.put(KeyHub.key_actionBarDefaultArchivedSearchPlaceholder, 0x88ffffff);

        defaultColors.put(KeyHub.key_checkbox, 0xff5ec245);
        defaultColors.put(KeyHub.key_checkboxCheck, 0xffffffff);
        defaultColors.put(KeyHub.key_checkboxDisabled, 0xffb0b9c2);

        //endregion

        //region fallbackKeys
        fallbackKeys.put(KeyHub.key_graySectionText, KeyHub.key_windowBackgroundWhiteGrayText2);
        fallbackKeys.put(KeyHub.key_dialog_inlineProgressBackground, KeyHub.key_windowBackgroundGray);
        fallbackKeys.put(KeyHub.key_windowBackgroundWhiteBlueButton, KeyHub.key_windowBackgroundWhiteValueText);
        fallbackKeys.put(KeyHub.key_windowBackgroundWhiteBlueIcon, KeyHub.key_windowBackgroundWhiteValueText);
        fallbackKeys.put(KeyHub.key_windowBackgroundUnchecked, KeyHub.key_windowBackgroundWhite);
        fallbackKeys.put(KeyHub.key_windowBackgroundChecked, KeyHub.key_windowBackgroundWhite);
        fallbackKeys.put(KeyHub.key_switchTrackBlue, KeyHub.key_switchTrack);
        fallbackKeys.put(KeyHub.key_switchTrackBlueChecked, KeyHub.key_switchTrackChecked);
        fallbackKeys.put(KeyHub.key_switchTrackBlueThumb, KeyHub.key_windowBackgroundWhite);
        fallbackKeys.put(KeyHub.key_switchTrackBlueThumbChecked, KeyHub.key_windowBackgroundWhite);
        fallbackKeys.put(KeyHub.key_windowBackgroundCheckText, KeyHub.key_windowBackgroundWhiteBlackText);
        fallbackKeys.put(KeyHub.key_switchTrackBlueSelector, KeyHub.key_listSelector);
        fallbackKeys.put(KeyHub.key_switchTrackBlueSelectorChecked, KeyHub.key_listSelector);
        fallbackKeys.put(KeyHub.key_dialogSearchText, KeyHub.key_windowBackgroundWhiteBlackText);
        fallbackKeys.put(KeyHub.key_dialogFloatingButton, KeyHub.key_dialogRoundCheckBox);
        fallbackKeys.put(KeyHub.key_dialogFloatingButtonPressed, KeyHub.key_dialogRoundCheckBox);
        fallbackKeys.put(KeyHub.key_dialogFloatingIcon, KeyHub.key_dialogRoundCheckBoxCheck);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchived, KeyHub.key_actionBarDefault);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchivedSelector, KeyHub.key_actionBarDefaultSelector);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchivedIcon, KeyHub.key_actionBarDefaultIcon);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchivedTitle, KeyHub.key_actionBarDefaultTitle);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchivedSearch, KeyHub.key_actionBarDefaultSearch);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchivedSearchPlaceholder, KeyHub.key_actionBarDefaultSearchPlaceholder);
        fallbackKeys.put(KeyHub.key_actionBarDefaultSubmenuItemIcon, KeyHub.key_dialogIcon);
        fallbackKeys.put(KeyHub.key_actionBarTabActiveText, KeyHub.key_actionBarDefaultTitle);
        fallbackKeys.put(KeyHub.key_actionBarTabUnactiveText, KeyHub.key_actionBarDefaultSubtitle);
        fallbackKeys.put(KeyHub.key_actionBarTabLine, KeyHub.key_actionBarDefaultTitle);
        fallbackKeys.put(KeyHub.key_actionBarTabSelector, KeyHub.key_actionBarDefaultSelector);
        fallbackKeys.put(KeyHub.key_actionBarBrowser, KeyHub.key_actionBarDefault);
        //endregion

        //region load theme from share preference
        themes = new ArrayList<>();
        otherThemes = new ArrayList<>();
        themesDict = new HashMap<>();
        currentColorsNoAccent = new HashMap<>();
        currentColors = new HashMap<>();

        SharedPreferences themeConfig = AndroidUtilities.getThemeConfig();

        ThemeInfo themeInfo = new ThemeInfo();
        themeInfo.name = "Blue";
        themeInfo.assetName = "bluebubbles.attheme";
        themeInfo.previewBackgroundColor = 0xff95beec;
        themeInfo.previewInColor = 0xffffffff;
        themeInfo.previewOutColor = 0xffd0e6ff;
        themeInfo.firstAccentIsDefault = true;
        themeInfo.currentAccentId = DEFALT_THEME_ACCENT_ID;
        themeInfo.sortIndex = 1;
        themeInfo.setAccentColorOptions(
                new int[]{0xFF5890C5, 0xFF239853, 0xFFCE5E82, 0xFF7F63C3, 0xFF2491AD, 0xFF299C2F, 0xFF8854B4, 0xFF328ACF, 0xFF43ACC7, 0xFF52AC44, 0xFFCD5F93, 0xFFD28036, 0xFF8366CC, 0xFFCE4E57, 0xFFD3AE40, 0xFF7B88AB},
                new int[]{0xFFB8E18D, 0xFFFAFBCC, 0xFFFFF9DC, 0xFFC14F6E, 0xFFD1BD1B, 0xFFFFFAC9, 0xFFFCF6D8, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0x00000000, 0xFFF2FBC9, 0xFFFBF4DF, 0, 0, 0xFFFDEDB4, 0xFFFCF7B6, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0x00000000, 0xFFDFE2A0, 0xFFE2B991, 0xFFD7C1E9, 0xFFDCD1C0, 0xFFEFB576, 0xFFC0A2D1, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0x00000000, 0xFFC1E1A3, 0xFFEBE2BA, 0xFFE8CDD6, 0xFFE0DFC6, 0xFFECE771, 0xFFDECCDE, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{99, 9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8},
                new String[]{"", "p-pXcflrmFIBAAAAvXYQk-mCwZU", "JqSUrO0-mFIBAAAAWwTvLzoWGQI", "O-wmAfBPSFADAAAA4zINVfD_bro", "RepJ5uE_SVABAAAAr4d0YhgB850", "-Xc-np9y2VMCAAAARKr0yNNPYW0", "dhf9pceaQVACAAAAbzdVo4SCiZA", "", "", "", "", "", "", "", "", ""},
                new int[]{0, 180, 45, 0, 45, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 52, 46, 57, 45, 64, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        );
        themes.add(currentDayTheme = currentTheme = defaultTheme = themeInfo);
        themesDict.put("Blue", themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = "Dark Blue";
        themeInfo.assetName = "darkblue.attheme";
        themeInfo.previewBackgroundColor = 0xff5f6e82;
        themeInfo.previewInColor = 0xff76869c;
        themeInfo.previewOutColor = 0xff82a8e3;
        themeInfo.sortIndex = 3;
        themeInfo.setAccentColorOptions(
                new int[]{0xFF927BD4, 0xFF698AFB, 0xFF23A7F0, 0xFF7B71D1, 0xFF69B955, 0xFF2990EA, 0xFF7082E9, 0xFF66BAED, 0xff3685fa, 0xff46c8ed, 0xff4ab841, 0xffeb7cb1, 0xffee902a, 0xffa281f0, 0xffd34324, 0xffeebd34, 0xff7f8fab, 0xff3581e3},
                new int[]{0xFF9D5C99, 0xFF635545, 0xFF31818B, 0xFFAD6426, 0xFF4A7034, 0xFF335D82, 0xFF36576F, 0xFF597563, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0xFF604DA8, 0xFF685D4C, 0xFF1B6080, 0xFF99354E, 0xFF275D3B, 0xFF317A98, 0xFF376E87, 0xFF5E7370, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0xFF28212E, 0xFF171A22, 0xFF071E1F, 0xFF100F13, 0xFF141D12, 0xFF07121C, 0xFF1E2029, 0xFF020403, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0xFF121013, 0xFF26262E, 0xFF141D26, 0xFF221E24, 0xFF1A2114, 0xFF1C2630, 0xFF141518, 0xFF151C1F, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{11, 12, 13, 14, 15, 16, 17, 18, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                new String[]{"O-wmAfBPSFADAAAA4zINVfD_bro", "RepJ5uE_SVABAAAAr4d0YhgB850", "dk_wwlghOFACAAAAfz9xrxi6euw", "9LW_RcoOSVACAAAAFTk3DTyXN-M", "PllZ-bf_SFAEAAAA8crRfwZiDNg", "-Xc-np9y2VMCAAAARKr0yNNPYW0", "kO4jyq55SFABAAAA0WEpcLfahXk", "CJNyxPMgSVAEAAAAvW9sMwc51cw", "", "", "", "", "", "", "", "", "", ""},
                new int[]{225, 45, 225, 135, 45, 225, 45, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{40, 40, 31, 50, 25, 34, 35, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        );
        themes.add(themeInfo);
        themesDict.put("Dark Blue", currentNightTheme = themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = "Arctic Blue";
        themeInfo.assetName = "arctic.attheme";
        themeInfo.previewBackgroundColor = 0xffe1e9f0;
        themeInfo.previewInColor = 0xffffffff;
        themeInfo.previewOutColor = 0xff6ca1eb;
        themeInfo.sortIndex = 5;
        themeInfo.setAccentColorOptions(
                new int[]{0xFF40B1E2, 0xFF41B05D, 0xFFCE8C20, 0xFF57A3EB, 0xFFDE8534, 0xFFCC6189, 0xFF3490EB, 0xFF43ACC7, 0xFF52AC44, 0xFFCD5F93, 0xFFD28036, 0xFF8366CC, 0xFFCE4E57, 0xFFD3AE40, 0xFF7B88AB},
                new int[]{0xFF319FCA, 0xFF28A359, 0xFF8C5A3F, 0xFF3085D3, 0xFFC95870, 0xFF7871CD, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0xFF4EBEE2, 0xFF6BBC59, 0xFF9E563C, 0xFF48C2D8, 0xFFD87047, 0xFFBE6EAF, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0xFFB4E3F0, 0xFFDDDEAA, 0xFFDACCA1, 0xFFE3F3F3, 0xFFEEE5B0, 0xFFE5DFEC, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0xFFF1FDFC, 0xFFC9E9B6, 0xFFE2E1BE, 0xFFC8E6EE, 0xFFEEBEAA, 0xFFE1C6EC, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{9, 10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8},
                new String[]{"MIo6r0qGSFAFAAAAtL8TsDzNX60", "dhf9pceaQVACAAAAbzdVo4SCiZA", "fqv01SQemVIBAAAApND8LDRUhRU", "p-pXcflrmFIBAAAAvXYQk-mCwZU", "JqSUrO0-mFIBAAAAWwTvLzoWGQI", "F5oWoCs7QFACAAAAgf2bD_mg8Bw", "", "", "", "", "", "", "", "", ""},
                new int[]{315, 315, 225, 315, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{50, 50, 58, 47, 46, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        );
        themes.add(themeInfo);
        themesDict.put("Arctic Blue", themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = "Day";
        themeInfo.assetName = "day.attheme";
        themeInfo.previewBackgroundColor = 0xffffffff;
        themeInfo.previewInColor = 0xffebeef4;
        themeInfo.previewOutColor = 0xff7cb2fe;
        themeInfo.sortIndex = 2;
        themeInfo.setAccentColorOptions(
                new int[]{0xFF56A2C9, 0xFFCC6E83, 0xFFD08E47, 0xFFCC6462, 0xFF867CD2, 0xFF4C91DF, 0xFF57B4D9, 0xFF54B169, 0xFFD9BF3F, 0xFFCC6462, 0xFFCC6E83, 0xFF9B7BD2, 0xFFD79144, 0xFF7B88AB},
                new int[]{0xFF6580DC, 0xFF6C6DD2, 0xFFCB5481, 0xFFC34A4A, 0xFF5C8EDF, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0xFF3EC1D6, 0xFFC86994, 0xFFDBA12F, 0xFFD08E3B, 0xFF51B5CB, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8},
                new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", ""},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        );
        themes.add(themeInfo);
        themesDict.put("Day", themeInfo);

        themeInfo = new ThemeInfo();
        themeInfo.name = "Night";
        themeInfo.assetName = "night.attheme";
        themeInfo.previewBackgroundColor = 0xff535659;
        themeInfo.previewInColor = 0xff747A84;
        themeInfo.previewOutColor = 0xff75A2E6;
        themeInfo.sortIndex = 4;
        themeInfo.setAccentColorOptions(
                new int[]{0xFF6ABE3F, 0xFF8D78E3, 0xFFDE5E7E, 0xFF5977E8, 0xFFDBC11A, 0xff3e88f7, 0xff4ab5d3, 0xff4ab841, 0xffd95576, 0xffe27d2b, 0xff936cda, 0xffd04336, 0xffe8ae1c, 0xff7988a3},
                new int[]{0xFF8A5294, 0xFFB46C1B, 0xFFAF4F6F, 0xFF266E8D, 0xFF744EB7, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0xFF6855BB, 0xFFA53B4A, 0xFF62499C, 0xFF2F919D, 0xFF298B95, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0xFF020702, 0xFF111314, 0xFF040304, 0xFF0B0C0C, 0xFF060607, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{0xFF0F0E10, 0xFF080809, 0xFF050505, 0xFF0E0E10, 0xFF0D0D10, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000},
                new int[]{9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6, 7, 8},
                new String[]{"YIxYGEALQVADAAAAA3QbEH0AowY", "9LW_RcoOSVACAAAAFTk3DTyXN-M", "O-wmAfBPSFADAAAA4zINVfD_bro", "F5oWoCs7QFACAAAAgf2bD_mg8Bw", "-Xc-np9y2VMCAAAARKr0yNNPYW0", "", "", "", "", "", "", "", "", ""},
                new int[]{45, 135, 0, 180, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{34, 47, 52, 48, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        );
        themes.add(themeInfo);
        themesDict.put("Night", themeInfo);
        //endregion

        String themesString = themeConfig.getString("themes2", null);
        if (!TextUtils.isEmpty(themesString)) {
            try {
                JSONArray jsonArray = new JSONArray(themesString);
                for (int a = 0; a < jsonArray.length(); a++) {
                    themeInfo = ThemeInfo.createWithJson(jsonArray.getJSONObject(a));
                    if (themeInfo != null) {
                        otherThemes.add(themeInfo);
                        themes.add(themeInfo);
                        themesDict.put(themeInfo.getKey(), themeInfo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            themesString = themeConfig.getString("themes", null);
            if (!TextUtils.isEmpty(themesString)) {
                String[] themesArr = themesString.split("&");
                for (int a = 0; a < themesArr.length; a++) {
                    themeInfo = ThemeInfo.createWithString(themesArr[a]);
                    if (themeInfo != null) {
                        otherThemes.add(themeInfo);
                        themes.add(themeInfo);
                        themesDict.put(themeInfo.getKey(), themeInfo);
                    }
                }
                saveOtherThemes(AndroidUtilities.applicationContext, true, true);
                themeConfig.edit().remove("themes").apply();
            }
        }

        sortThemes();

        ThemeInfo applyingTheme = null;
        SharedPreferences preferences = AndroidUtilities.getGlobalMainSettings();
        try {
            final ThemeInfo themeDarkBlue = themesDict.get("Dark Blue");

            String theme = preferences.getString("theme", null);
            if ("Default".equals(theme)) {
                applyingTheme = themesDict.get("Blue");
                applyingTheme.currentAccentId = DEFALT_THEME_ACCENT_ID;
            } else if ("Dark".equals(theme)) {
                applyingTheme = themeDarkBlue;
                applyingTheme.currentAccentId = 9;
            } else if (theme != null) {
                applyingTheme = themesDict.get(theme);
                if (applyingTheme != null && !themeConfig.contains("lastDayTheme")) {
                    SharedPreferences.Editor editor = themeConfig.edit();
                    editor.putString("lastDayTheme", applyingTheme.getKey());
                    editor.commit();
                }
            }

            theme = preferences.getString("nighttheme", null);
            if ("Default".equals(theme)) {
                applyingTheme = themesDict.get("Blue");
                applyingTheme.currentAccentId = DEFALT_THEME_ACCENT_ID;
            } else if ("Dark".equals(theme)) {
                currentNightTheme = themeDarkBlue;
                themeDarkBlue.currentAccentId = 9;
            } else if (theme != null) {
                ThemeInfo t = themesDict.get(theme);
                if (t != null) {
                    currentNightTheme = t;
                }
            }

            if (currentNightTheme != null && !themeConfig.contains("lastDarkTheme")) {
                SharedPreferences.Editor editor = themeConfig.edit();
                editor.putString("lastDarkTheme", currentNightTheme.getKey());
                editor.commit();
            }

            SharedPreferences.Editor oldEditor = null;
            SharedPreferences.Editor oldEditorNew = null;
            for (ThemeInfo info : themesDict.values()) {
                if (info.assetName != null && info.accentBaseColor != 0) {
                    String accents = themeConfig.getString("accents_" + info.assetName, null);
                    info.currentAccentId = themeConfig.getInt("accent_current_" + info.assetName, info.firstAccentIsDefault ? DEFALT_THEME_ACCENT_ID : 0);
                    ArrayList<ThemeAccent> newAccents = new ArrayList<>();
                    if (!TextUtils.isEmpty(accents)) {
                        try {
                            ThemeAccentList accentList = ThemeAccentList.fromJson(accents);
                            int count = accentList.count;
                            for (int a = 0; a < count; a++) {
                                ThemeAccent accent = accentList.list.get(a);
                                accent.parentTheme = info;
                                info.themeAccentsMap.put(accent.id, accent);
                                if (accent.info != null) {
                                    info.accentsByThemeId.put(accent.info.id, accent);
                                }
                                newAccents.add(accent);
                                info.lastAccentId = Math.max(info.lastAccentId, accent.id);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    } else {
                        String key = "accent_for_" + info.assetName;
                        int oldAccentColor = preferences.getInt(key, 0);
                        if (oldAccentColor != 0) {
                            if (oldEditor == null) {
                                oldEditor = preferences.edit();
                                oldEditorNew = themeConfig.edit();
                            }
                            oldEditor.remove(key);
                            boolean found = false;
                            for (int a = 0, N = info.themeAccents.size(); a < N; a++) {
                                ThemeAccent accent = info.themeAccents.get(a);
                                if (accent.accentColor == oldAccentColor) {
                                    info.currentAccentId = accent.id;
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                ThemeAccent accent = new ThemeAccent();
                                accent.id = 100;
                                accent.accentColor = oldAccentColor;
                                accent.parentTheme = info;
                                info.themeAccentsMap.put(accent.id, accent);
                                newAccents.add(0, accent);
                                info.currentAccentId = 100;
                                info.lastAccentId = 101;

                                ThemeAccentList accentList = new ThemeAccentList();
                                accentList.version = 5;
                                accentList.count = 1;
                                accentList.list.add(accent);

                                oldEditorNew.putString("accents_" + info.assetName, accentList.toJson());
                            }
                            oldEditorNew.putInt("accent_current_" + info.assetName, info.currentAccentId);
                        }
                    }
                    if (!newAccents.isEmpty()) {
                        Collections.sort(newAccents, (o1, o2) -> {
                            if (o1.id > o2.id) {
                                return -1;
                            } else if (o1.id < o2.id) {
                                return 1;
                            }
                            return 0;
                        });
                        info.themeAccents.addAll(0, newAccents);
                    }
                    if (info.themeAccentsMap != null && info.themeAccentsMap.get(info.currentAccentId) == null) {
                        info.currentAccentId = info.firstAccentIsDefault ? DEFALT_THEME_ACCENT_ID : 0;
                    }
                }
            }
            if (oldEditor != null) {
                oldEditor.commit();
                oldEditorNew.commit();
            }

            selectedAutoNightType = preferences.getInt("selectedAutoNightType", Build.VERSION.SDK_INT >= 29 ? AUTO_NIGHT_TYPE_SYSTEM : AUTO_NIGHT_TYPE_NONE);
            autoNightScheduleByLocation = preferences.getBoolean("autoNightScheduleByLocation", false);
            autoNightBrighnessThreshold = preferences.getFloat("autoNightBrighnessThreshold", 0.25f);
            autoNightDayStartTime = preferences.getInt("autoNightDayStartTime", 22 * 60);
            autoNightDayEndTime = preferences.getInt("autoNightDayEndTime", 8 * 60);
            autoNightSunsetTime = preferences.getInt("autoNightSunsetTime", 22 * 60);
            autoNightSunriseTime = preferences.getInt("autoNightSunriseTime", 8 * 60);
            autoNightCityName = preferences.getString("autoNightCityName", "");
            long val = preferences.getLong("autoNightLocationLatitude3", 10000);
            if (val != 10000) {
                autoNightLocationLatitude = Double.longBitsToDouble(val);
            } else {
                autoNightLocationLatitude = 10000;
            }
            val = preferences.getLong("autoNightLocationLongitude3", 10000);
            if (val != 10000) {
                autoNightLocationLongitude = Double.longBitsToDouble(val);
            } else {
                autoNightLocationLongitude = 10000;
            }
            autoNightLastSunCheckDay = preferences.getInt("autoNightLastSunCheckDay", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (applyingTheme == null) {
            applyingTheme = defaultTheme;
        } else {
            currentDayTheme = applyingTheme;
        }

        int switchToTheme = needSwitchToTheme(AndroidUtilities.applicationContext);
        if (switchToTheme == 2) {
            applyingTheme = currentNightTheme;
        }
        applyTheme(AndroidUtilities.applicationContext, applyingTheme, false, switchToTheme == 2);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ThemeManager.checkAutoNightThemeConditions(AndroidUtilities.applicationContext);
            }
        });
    }

    //region 资源管理
    public static boolean firstConfigurationWas;
    public static float density;

    public static void onConfigurationChanged(Context context, Configuration newConfiguration) {
        float oldDensity = density;
        density = context.getResources().getDisplayMetrics().density;
        float newDensity = density;
        if (firstConfigurationWas && Math.abs(oldDensity - newDensity) > 0.001) {
            Theme.reloadAllResources(context);
        }
        firstConfigurationWas = true;
    }

    public static void reloadAllResources(Context context) {
        for (AbsTheme absTheme : ThemeRes.themes) {
            absTheme.reloadAllResources(context);
        }
    }

    public static void applyChatServiceMessageColor() {
    }
    //endregion

    //region 工具函数
    public static int getDefaultColor(String key) {
        Integer value = defaultColors.get(key);
        if (value == null) {
            return 0xffff0000;
        }
        return value;
    }

    public static boolean hasThemeKey(String key) {
        return currentColors.containsKey(key);
    }

    public static Integer getColorOrNull(String key) {
        Integer color = currentColors.get(key);
        if (color == null) {
            String fallbackKey = fallbackKeys.get(key);
            if (fallbackKey != null) {
                color = currentColors.get(key);
            }
            if (color == null) {
                color = defaultColors.get(key);
            }
        }
        if (color != null && (KeyHub.key_windowBackgroundWhite.equals(key) || KeyHub.key_windowBackgroundGray.equals(key))) {
            color |= 0xff000000;
        }
        return color;
    }

    public static void setAnimatingColor(boolean animating) {
        animatingColors = animating ? new HashMap<>() : null;
    }

    public static boolean isAnimatingColor() {
        return animatingColors != null;
    }

    public static void setAnimatedColor(String key, int value) {
        if (animatingColors == null) {
            return;
        }
        animatingColors.put(key, value);
    }

    public static int getDefaultAccentColor(String key) {
        Integer color = currentColorsNoAccent.get(key);
        if (color != null) {
            ThemeAccent accent = currentTheme.getAccent(false);
            if (accent == null) {
                return 0;
            }
            float[] hsvTemp1 = getTempHsv(1);
            float[] hsvTemp2 = getTempHsv(2);
            Color.colorToHSV(currentTheme.accentBaseColor, hsvTemp1);
            Color.colorToHSV(accent.accentColor, hsvTemp2);
            return changeColorAccent(hsvTemp1, hsvTemp2, color, currentTheme.isDark());
        }
        return 0;
    }

    public static int getNonAnimatedColor(String key) {
        return getColor(key, null, true);
    }

    public static int getColor(String key) {
        return getColor(key, null, false);
    }

    public static int getColor(String key, boolean[] isDefault) {
        return getColor(key, isDefault, false);
    }

    public static int getColor(String key, boolean[] isDefault, boolean ignoreAnimation) {
        if (!ignoreAnimation && animatingColors != null) {
            Integer color = animatingColors.get(key);
            if (color != null) {
                return color;
            }
        }
        if (currentTheme == defaultTheme) {
            boolean useDefault = currentTheme.isDefaultMainAccent();
            if (useDefault) {
                return getDefaultColor(key);
            }
        }
        Integer color = currentColors.get(key);
        if (color == null) {
            String fallbackKey = fallbackKeys.get(key);
            if (fallbackKey != null) {
                color = currentColors.get(fallbackKey);
            }
            if (color == null) {
                if (isDefault != null) {
                    isDefault[0] = true;
                }
                return getDefaultColor(key);
            }
        }
        return color;
    }

    public static void setColor(Context context, String key, int color, boolean useDefault) {
        if (useDefault) {
            currentColors.remove(key);
        } else {
            currentColors.put(key, color);
        }

        switch (key) {
            case KeyHub.key_actionBarDefault:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NotificationCenter.post(NotificationCenter.needCheckSystemBarColors);
                }
                break;
            case KeyHub.key_windowBackgroundGray:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationCenter.post(NotificationCenter.needCheckSystemBarColors);
                }
                break;
        }
    }

    private void checkSystemBarColors(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int color = Theme.getColor(KeyHub.key_actionBarDefault, null, true);
            AndroidUtilities.setLightStatusBar(context.getWindow(), color == Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final Window window = context.getWindow();
                color = Theme.getColor(KeyHub.key_windowBackgroundGray, null, true);
                if (window.getNavigationBarColor() != color) {
                    window.setNavigationBarColor(color);
                    final float brightness = AndroidUtilities.computePerceivedBrightness(color);
                    AndroidUtilities.setLightNavigationBar(context.getWindow(), brightness >= 0.721f);
                }
            }
        }
    }

    public static void setDefaultColor(String key, int color) {
        defaultColors.put(key, color);
    }

    public static boolean isCustomTheme() {
        return isCustomTheme;
    }
    //endregion
}
