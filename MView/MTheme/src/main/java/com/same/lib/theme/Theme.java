package com.same.lib.theme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.Window;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
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

    static Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    static int loadingCurrentTheme;
    static int lastLoadingCurrentThemeTime;
    static boolean[] loadingRemoteThemes = new boolean[1];
    static int[] lastLoadingThemesTime = new int[1];
    static int[] remoteThemesHash = new int[1];

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
    public static PorterDuffColorFilter colorPressedFilter;
    public static PorterDuffColorFilter colorFilter2;
    public static PorterDuffColorFilter colorPressedFilter2;
    static boolean isCustomTheme;
    static int serviceMessageColor;
    static int serviceSelectedMessageColor;
    public static int serviceMessageColorBackup;
    public static int serviceSelectedMessageColorBackup;
    static int serviceMessage2Color;
    static int serviceSelectedMessage2Color;
    public static int currentColor;
    static int currentSelectedColor;

    public static Drawable moveUpDrawable;

    public static Paint dialogs_onlineCirclePaint;

    public static Drawable dialogs_holidayDrawable;
    static int dialogs_holidayDrawableOffsetX;
    static int dialogs_holidayDrawableOffsetY;
    static long lastHolidayCheckTime;
    static boolean canStartHolidayAnimation;

    static HashSet<String> myMessagesColorKeys = new HashSet<>();
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
        defaultColors.put(KeyHub.key_contextProgressInner1, 0xffbfdff6);
        defaultColors.put(KeyHub.key_contextProgressOuter1, 0xff2b96e2);
        defaultColors.put(KeyHub.key_contextProgressInner2, 0xffbfdff6);
        defaultColors.put(KeyHub.key_contextProgressOuter2, 0xffffffff);
        defaultColors.put(KeyHub.key_contextProgressInner3, 0xffb3b3b3);
        defaultColors.put(KeyHub.key_contextProgressOuter3, 0xffffffff);
        defaultColors.put(KeyHub.key_contextProgressInner4, 0xffcacdd0);
        defaultColors.put(KeyHub.key_contextProgressOuter4, 0xff2f3438);
        defaultColors.put(KeyHub.key_fastScrollActive, 0xff52a3db);
        defaultColors.put(KeyHub.key_fastScrollInactive, 0xffc9cdd1);
        defaultColors.put(KeyHub.key_fastScrollText, 0xffffffff);

        defaultColors.put(KeyHub.key_avatar_text, 0xffffffff);

        defaultColors.put(KeyHub.key_avatar_backgroundSaved, 0xff66bffa);
        defaultColors.put(KeyHub.key_avatar_backgroundArchived, 0xffa9b6c1);
        defaultColors.put(KeyHub.key_avatar_backgroundArchivedHidden, 0xff66bffa);
        defaultColors.put(KeyHub.key_avatar_backgroundRed, 0xffe56555);
        defaultColors.put(KeyHub.key_avatar_backgroundOrange, 0xfff28c48);
        defaultColors.put(KeyHub.key_avatar_backgroundViolet, 0xff8e85ee);
        defaultColors.put(KeyHub.key_avatar_backgroundGreen, 0xff76c84d);
        defaultColors.put(KeyHub.key_avatar_backgroundCyan, 0xff5fbed5);
        defaultColors.put(KeyHub.key_avatar_backgroundBlue, 0xff549cdd);
        defaultColors.put(KeyHub.key_avatar_backgroundPink, 0xfff2749a);

        defaultColors.put(KeyHub.key_avatar_backgroundInProfileBlue, 0xff5085b1);
        defaultColors.put(KeyHub.key_avatar_backgroundActionBarBlue, 0xff598fba);
        defaultColors.put(KeyHub.key_avatar_subtitleInProfileBlue, 0xffd7eafa);
        defaultColors.put(KeyHub.key_avatar_actionBarSelectorBlue, 0xff4981ad);
        defaultColors.put(KeyHub.key_avatar_actionBarIconBlue, 0xffffffff);

        defaultColors.put(KeyHub.key_avatar_nameInMessageRed, 0xffca5650);
        defaultColors.put(KeyHub.key_avatar_nameInMessageOrange, 0xffd87b29);
        defaultColors.put(KeyHub.key_avatar_nameInMessageViolet, 0xff4e92cc);
        defaultColors.put(KeyHub.key_avatar_nameInMessageGreen, 0xff50b232);
        defaultColors.put(KeyHub.key_avatar_nameInMessageCyan, 0xff379eb8);
        defaultColors.put(KeyHub.key_avatar_nameInMessageBlue, 0xff4e92cc);
        defaultColors.put(KeyHub.key_avatar_nameInMessagePink, 0xff4e92cc);

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

        defaultColors.put(KeyHub.key_chats_onlineCircle, 0xff4bcb1c);
        defaultColors.put(KeyHub.key_chats_unreadCounter, 0xff4ecc5e);
        defaultColors.put(KeyHub.key_chats_unreadCounterMuted, 0xffc6c9cc);
        defaultColors.put(KeyHub.key_chats_unreadCounterText, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_archiveBackground, 0xff66a9e0);
        defaultColors.put(KeyHub.key_chats_archivePinBackground, 0xff9faab3);
        defaultColors.put(KeyHub.key_chats_archiveIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_archiveText, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_name, 0xff222222);
        defaultColors.put(KeyHub.key_chats_nameArchived, 0xff525252);
        defaultColors.put(KeyHub.key_chats_secretName, 0xff00a60e);
        defaultColors.put(KeyHub.key_chats_secretIcon, 0xff19b126);
        defaultColors.put(KeyHub.key_chats_nameIcon, 0xff242424);
        defaultColors.put(KeyHub.key_chats_pinnedIcon, 0xffa8a8a8);
        defaultColors.put(KeyHub.key_chats_message, 0xff8b8d8f);
        defaultColors.put(KeyHub.key_chats_messageArchived, 0xff919191);
        defaultColors.put(KeyHub.key_chats_message_threeLines, 0xff8e9091);
        defaultColors.put(KeyHub.key_chats_draft, 0xffdd4b39);
        defaultColors.put(KeyHub.key_chats_nameMessage, 0xff3c7eb0);
        defaultColors.put(KeyHub.key_chats_nameMessageArchived, 0xff8b8d8f);
        defaultColors.put(KeyHub.key_chats_nameMessage_threeLines, 0xff424449);
        defaultColors.put(KeyHub.key_chats_nameMessageArchived_threeLines, 0xff5e5e5e);
        defaultColors.put(KeyHub.key_chats_attachMessage, 0xff3c7eb0);
        defaultColors.put(KeyHub.key_chats_actionMessage, 0xff3c7eb0);
        defaultColors.put(KeyHub.key_chats_date, 0xff95999C);
        defaultColors.put(KeyHub.key_chats_pinnedOverlay, 0x08000000);
        defaultColors.put(KeyHub.key_chats_tabletSelectedOverlay, 0x0f000000);
        defaultColors.put(KeyHub.key_chats_sentCheck, 0xff46aa36);
        defaultColors.put(KeyHub.key_chats_sentReadCheck, 0xff46aa36);
        defaultColors.put(KeyHub.key_chats_sentClock, 0xff75bd5e);
        defaultColors.put(KeyHub.key_chats_sentError, 0xffd55252);
        defaultColors.put(KeyHub.key_chats_sentErrorIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_verifiedBackground, 0xff33a8e6);
        defaultColors.put(KeyHub.key_chats_verifiedCheck, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_muteIcon, 0xffbdc1c4);
        defaultColors.put(KeyHub.key_chats_mentionIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_menuBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_menuItemText, 0xff444444);
        defaultColors.put(KeyHub.key_chats_menuItemCheck, 0xff598fba);
        defaultColors.put(KeyHub.key_chats_menuItemIcon, 0xff889198);
        defaultColors.put(KeyHub.key_chats_menuName, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_menuPhone, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_menuPhoneCats, 0xffc2e5ff);
        defaultColors.put(KeyHub.key_chats_menuCloud, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_menuCloudBackgroundCats, 0xff427ba9);
        defaultColors.put(KeyHub.key_chats_actionIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_actionBackground, 0xff65a9e0);
        defaultColors.put(KeyHub.key_chats_actionPressedBackground, 0xff569dd6);
        defaultColors.put(KeyHub.key_chats_actionUnreadIcon, 0xff737373);
        defaultColors.put(KeyHub.key_chats_actionUnreadBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_chats_actionUnreadPressedBackground, 0xfff2f2f2);
        defaultColors.put(KeyHub.key_chats_menuTopBackgroundCats, 0xff598fba);
        defaultColors.put(KeyHub.key_chats_archivePullDownBackground, 0xffc6c9cc);
        defaultColors.put(KeyHub.key_chats_archivePullDownBackgroundActive, 0xff66a9e0);

        defaultColors.put(KeyHub.key_chat_attachMediaBanBackground, 0xff464646);
        defaultColors.put(KeyHub.key_chat_attachMediaBanText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_attachCheckBoxCheck, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_attachCheckBoxBackground, 0xff39b2f7);
        defaultColors.put(KeyHub.key_chat_attachPhotoBackground, 0x0c000000);
        defaultColors.put(KeyHub.key_chat_attachActiveTab, 0xff33a7f5);
        defaultColors.put(KeyHub.key_chat_attachUnactiveTab, 0xff92999e);
        defaultColors.put(KeyHub.key_chat_attachPermissionImage, 0xff333333);
        defaultColors.put(KeyHub.key_chat_attachPermissionMark, 0xffe25050);
        defaultColors.put(KeyHub.key_chat_attachPermissionText, 0xff6f777a);
        defaultColors.put(KeyHub.key_chat_attachEmptyImage, 0xffcccccc);

        defaultColors.put(KeyHub.key_chat_attachGalleryBackground, 0xff459df5);
        defaultColors.put(KeyHub.key_chat_attachGalleryText, 0xff2e8de9);
        defaultColors.put(KeyHub.key_chat_attachGalleryIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_attachAudioBackground, 0xffeb6060);
        defaultColors.put(KeyHub.key_chat_attachAudioText, 0xffde4747);
        defaultColors.put(KeyHub.key_chat_attachAudioIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_attachFileBackground, 0xff34b9f1);
        defaultColors.put(KeyHub.key_chat_attachFileText, 0xff14a8e4);
        defaultColors.put(KeyHub.key_chat_attachFileIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_attachContactBackground, 0xfff2c04b);
        defaultColors.put(KeyHub.key_chat_attachContactText, 0xffdfa000);
        defaultColors.put(KeyHub.key_chat_attachContactIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_attachLocationBackground, 0xff60c255);
        defaultColors.put(KeyHub.key_chat_attachLocationText, 0xff3cab2f);
        defaultColors.put(KeyHub.key_chat_attachLocationIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_attachPollBackground, 0xfff2c04b);
        defaultColors.put(KeyHub.key_chat_attachPollText, 0xffdfa000);
        defaultColors.put(KeyHub.key_chat_attachPollIcon, 0xffffffff);

        defaultColors.put(KeyHub.key_chat_inPollCorrectAnswer, 0xff60c255);
        defaultColors.put(KeyHub.key_chat_outPollCorrectAnswer, 0xff60c255);
        defaultColors.put(KeyHub.key_chat_inPollWrongAnswer, 0xffeb6060);
        defaultColors.put(KeyHub.key_chat_outPollWrongAnswer, 0xffeb6060);

        defaultColors.put(KeyHub.key_chat_status, 0xffd5e8f7);
        defaultColors.put(KeyHub.key_chat_inGreenCall, 0xff00c853);
        defaultColors.put(KeyHub.key_chat_inRedCall, 0xffff4848);
        defaultColors.put(KeyHub.key_chat_outGreenCall, 0xff00c853);
        defaultColors.put(KeyHub.key_chat_shareBackground, 0x66728fa6);
        defaultColors.put(KeyHub.key_chat_shareBackgroundSelected, 0x99728fa6);
        defaultColors.put(KeyHub.key_chat_lockIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_muteIcon, 0xffb1cce3);
        defaultColors.put(KeyHub.key_chat_inBubble, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inBubbleSelected, 0xffecf7fd);
        defaultColors.put(KeyHub.key_chat_inBubbleShadow, 0xff1d3753);
        defaultColors.put(KeyHub.key_chat_outBubble, 0xffefffde);
        defaultColors.put(KeyHub.key_chat_outBubbleGradientSelectedOverlay, 0x14000000);
        defaultColors.put(KeyHub.key_chat_outBubbleSelected, 0xffd9f7c5);
        defaultColors.put(KeyHub.key_chat_outBubbleShadow, 0xff1e750c);
        defaultColors.put(KeyHub.key_chat_inMediaIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inMediaIconSelected, 0xffeff8fe);
        defaultColors.put(KeyHub.key_chat_outMediaIcon, 0xffefffde);
        defaultColors.put(KeyHub.key_chat_outMediaIconSelected, 0xffe1f8cf);
        defaultColors.put(KeyHub.key_chat_messageTextIn, 0xff000000);
        defaultColors.put(KeyHub.key_chat_messageTextOut, 0xff000000);
        defaultColors.put(KeyHub.key_chat_messageLinkIn, 0xff2678b6);
        defaultColors.put(KeyHub.key_chat_messageLinkOut, 0xff2678b6);
        defaultColors.put(KeyHub.key_chat_serviceText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_serviceLink, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_serviceIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_mediaTimeBackground, 0x66000000);
        defaultColors.put(KeyHub.key_chat_outSentCheck, 0xff5db050);
        defaultColors.put(KeyHub.key_chat_outSentCheckSelected, 0xff5db050);
        defaultColors.put(KeyHub.key_chat_outSentCheckRead, 0xff5db050);
        defaultColors.put(KeyHub.key_chat_outSentCheckReadSelected, 0xff5db050);
        defaultColors.put(KeyHub.key_chat_outSentClock, 0xff75bd5e);
        defaultColors.put(KeyHub.key_chat_outSentClockSelected, 0xff75bd5e);
        defaultColors.put(KeyHub.key_chat_inSentClock, 0xffa1aab3);
        defaultColors.put(KeyHub.key_chat_inSentClockSelected, 0xff93bdca);
        defaultColors.put(KeyHub.key_chat_mediaSentCheck, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_mediaSentClock, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inViews, 0xffa1aab3);
        defaultColors.put(KeyHub.key_chat_inViewsSelected, 0xff93bdca);
        defaultColors.put(KeyHub.key_chat_outViews, 0xff6eb257);
        defaultColors.put(KeyHub.key_chat_outViewsSelected, 0xff6eb257);
        defaultColors.put(KeyHub.key_chat_mediaViews, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inMenu, 0xffb6bdc5);
        defaultColors.put(KeyHub.key_chat_inMenuSelected, 0xff98c1ce);
        defaultColors.put(KeyHub.key_chat_outMenu, 0xff91ce7e);
        defaultColors.put(KeyHub.key_chat_outMenuSelected, 0xff91ce7e);
        defaultColors.put(KeyHub.key_chat_mediaMenu, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_outInstant, 0xff55ab4f);
        defaultColors.put(KeyHub.key_chat_outInstantSelected, 0xff489943);
        defaultColors.put(KeyHub.key_chat_inInstant, 0xff3a8ccf);
        defaultColors.put(KeyHub.key_chat_inInstantSelected, 0xff3079b5);
        defaultColors.put(KeyHub.key_chat_sentError, 0xffdb3535);
        defaultColors.put(KeyHub.key_chat_sentErrorIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_selectedBackground, 0x280a90f0);
        defaultColors.put(KeyHub.key_chat_previewDurationText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_previewGameText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inPreviewInstantText, 0xff3a8ccf);
        defaultColors.put(KeyHub.key_chat_outPreviewInstantText, 0xff55ab4f);
        defaultColors.put(KeyHub.key_chat_inPreviewInstantSelectedText, 0xff3079b5);
        defaultColors.put(KeyHub.key_chat_outPreviewInstantSelectedText, 0xff489943);
        defaultColors.put(KeyHub.key_chat_secretTimeText, 0xffe4e2e0);
        defaultColors.put(KeyHub.key_chat_stickerNameText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_botButtonText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_botProgress, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inForwardedNameText, 0xff3886c7);
        defaultColors.put(KeyHub.key_chat_outForwardedNameText, 0xff55ab4f);
        defaultColors.put(KeyHub.key_chat_inPsaNameText, 0xff5a9c39);
        defaultColors.put(KeyHub.key_chat_outPsaNameText, 0xff5a9c39);
        defaultColors.put(KeyHub.key_chat_inViaBotNameText, 0xff3a8ccf);
        defaultColors.put(KeyHub.key_chat_outViaBotNameText, 0xff55ab4f);
        defaultColors.put(KeyHub.key_chat_stickerViaBotNameText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inReplyLine, 0xff599fd8);
        defaultColors.put(KeyHub.key_chat_outReplyLine, 0xff6eb969);
        defaultColors.put(KeyHub.key_chat_stickerReplyLine, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inReplyNameText, 0xff3a8ccf);
        defaultColors.put(KeyHub.key_chat_outReplyNameText, 0xff55ab4f);
        defaultColors.put(KeyHub.key_chat_stickerReplyNameText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inReplyMessageText, 0xff000000);
        defaultColors.put(KeyHub.key_chat_outReplyMessageText, 0xff000000);
        defaultColors.put(KeyHub.key_chat_inReplyMediaMessageText, 0xffa1aab3);
        defaultColors.put(KeyHub.key_chat_outReplyMediaMessageText, 0xff65b05b);
        defaultColors.put(KeyHub.key_chat_inReplyMediaMessageSelectedText, 0xff89b4c1);
        defaultColors.put(KeyHub.key_chat_outReplyMediaMessageSelectedText, 0xff65b05b);
        defaultColors.put(KeyHub.key_chat_stickerReplyMessageText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inPreviewLine, 0xff70b4e8);
        defaultColors.put(KeyHub.key_chat_outPreviewLine, 0xff88c97b);
        defaultColors.put(KeyHub.key_chat_inSiteNameText, 0xff3a8ccf);
        defaultColors.put(KeyHub.key_chat_outSiteNameText, 0xff55ab4f);
        defaultColors.put(KeyHub.key_chat_inContactNameText, 0xff4e9ad4);
        defaultColors.put(KeyHub.key_chat_outContactNameText, 0xff55ab4f);
        defaultColors.put(KeyHub.key_chat_inContactPhoneText, 0xff2f3438);
        defaultColors.put(KeyHub.key_chat_inContactPhoneSelectedText, 0xff2f3438);
        defaultColors.put(KeyHub.key_chat_outContactPhoneText, 0xff354234);
        defaultColors.put(KeyHub.key_chat_outContactPhoneSelectedText, 0xff354234);
        defaultColors.put(KeyHub.key_chat_mediaProgress, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inAudioProgress, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_outAudioProgress, 0xffefffde);
        defaultColors.put(KeyHub.key_chat_inAudioSelectedProgress, 0xffeff8fe);
        defaultColors.put(KeyHub.key_chat_outAudioSelectedProgress, 0xffe1f8cf);
        defaultColors.put(KeyHub.key_chat_mediaTimeText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inTimeText, 0xffa1aab3);
        defaultColors.put(KeyHub.key_chat_outTimeText, 0xff70b15c);
        defaultColors.put(KeyHub.key_chat_adminText, 0xffc0c6cb);
        defaultColors.put(KeyHub.key_chat_adminSelectedText, 0xff89b4c1);
        defaultColors.put(KeyHub.key_chat_inTimeSelectedText, 0xff89b4c1);
        defaultColors.put(KeyHub.key_chat_outTimeSelectedText, 0xff70b15c);
        defaultColors.put(KeyHub.key_chat_inAudioPerformerText, 0xff2f3438);
        defaultColors.put(KeyHub.key_chat_inAudioPerformerSelectedText, 0xff2f3438);
        defaultColors.put(KeyHub.key_chat_outAudioPerformerText, 0xff354234);
        defaultColors.put(KeyHub.key_chat_outAudioPerformerSelectedText, 0xff354234);
        defaultColors.put(KeyHub.key_chat_inAudioTitleText, 0xff4e9ad4);
        defaultColors.put(KeyHub.key_chat_outAudioTitleText, 0xff55ab4f);
        defaultColors.put(KeyHub.key_chat_inAudioDurationText, 0xffa1aab3);
        defaultColors.put(KeyHub.key_chat_outAudioDurationText, 0xff65b05b);
        defaultColors.put(KeyHub.key_chat_inAudioDurationSelectedText, 0xff89b4c1);
        defaultColors.put(KeyHub.key_chat_outAudioDurationSelectedText, 0xff65b05b);
        defaultColors.put(KeyHub.key_chat_inAudioSeekbar, 0xffe4eaf0);
        defaultColors.put(KeyHub.key_chat_inAudioCacheSeekbar, 0x3fe4eaf0);
        defaultColors.put(KeyHub.key_chat_outAudioSeekbar, 0xffbbe3ac);
        defaultColors.put(KeyHub.key_chat_outAudioCacheSeekbar, 0x3fbbe3ac);
        defaultColors.put(KeyHub.key_chat_inAudioSeekbarSelected, 0xffbcdee8);
        defaultColors.put(KeyHub.key_chat_outAudioSeekbarSelected, 0xffa9dd96);
        defaultColors.put(KeyHub.key_chat_inAudioSeekbarFill, 0xff72b5e8);
        defaultColors.put(KeyHub.key_chat_outAudioSeekbarFill, 0xff78c272);
        defaultColors.put(KeyHub.key_chat_inVoiceSeekbar, 0xffdee5eb);
        defaultColors.put(KeyHub.key_chat_outVoiceSeekbar, 0xffbbe3ac);
        defaultColors.put(KeyHub.key_chat_inVoiceSeekbarSelected, 0xffbcdee8);
        defaultColors.put(KeyHub.key_chat_outVoiceSeekbarSelected, 0xffa9dd96);
        defaultColors.put(KeyHub.key_chat_inVoiceSeekbarFill, 0xff72b5e8);
        defaultColors.put(KeyHub.key_chat_outVoiceSeekbarFill, 0xff78c272);
        defaultColors.put(KeyHub.key_chat_inFileProgress, 0xffebf0f5);
        defaultColors.put(KeyHub.key_chat_outFileProgress, 0xffdaf5c3);
        defaultColors.put(KeyHub.key_chat_inFileProgressSelected, 0xffcbeaf6);
        defaultColors.put(KeyHub.key_chat_outFileProgressSelected, 0xffc5eca7);
        defaultColors.put(KeyHub.key_chat_inFileNameText, 0xff4e9ad4);
        defaultColors.put(KeyHub.key_chat_outFileNameText, 0xff55ab4f);
        defaultColors.put(KeyHub.key_chat_inFileInfoText, 0xffa1aab3);
        defaultColors.put(KeyHub.key_chat_outFileInfoText, 0xff65b05b);
        defaultColors.put(KeyHub.key_chat_inFileInfoSelectedText, 0xff89b4c1);
        defaultColors.put(KeyHub.key_chat_outFileInfoSelectedText, 0xff65b05b);
        defaultColors.put(KeyHub.key_chat_inFileBackground, 0xffebf0f5);
        defaultColors.put(KeyHub.key_chat_outFileBackground, 0xffdaf5c3);
        defaultColors.put(KeyHub.key_chat_inFileBackgroundSelected, 0xffcbeaf6);
        defaultColors.put(KeyHub.key_chat_outFileBackgroundSelected, 0xffc5eca7);
        defaultColors.put(KeyHub.key_chat_inVenueInfoText, 0xffa1aab3);
        defaultColors.put(KeyHub.key_chat_outVenueInfoText, 0xff65b05b);
        defaultColors.put(KeyHub.key_chat_inVenueInfoSelectedText, 0xff89b4c1);
        defaultColors.put(KeyHub.key_chat_outVenueInfoSelectedText, 0xff65b05b);
        defaultColors.put(KeyHub.key_chat_mediaInfoText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_linkSelectBackground, 0x3362a9e3);
        defaultColors.put(KeyHub.key_chat_textSelectBackground, 0x6662a9e3);
        defaultColors.put(KeyHub.key_chat_emojiPanelBackground, 0xfff0f2f5);
        defaultColors.put(KeyHub.key_chat_emojiPanelBadgeBackground, 0xff4da6ea);
        defaultColors.put(KeyHub.key_chat_emojiPanelBadgeText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_emojiSearchBackground, 0xffe5e9ee);
        defaultColors.put(KeyHub.key_chat_emojiSearchIcon, 0xff94a1af);
        defaultColors.put(KeyHub.key_chat_emojiPanelShadowLine, 0x12000000);
        defaultColors.put(KeyHub.key_chat_emojiPanelEmptyText, 0xff949ba1);
        defaultColors.put(KeyHub.key_chat_emojiPanelIcon, 0xff9da4ab);
        defaultColors.put(KeyHub.key_chat_emojiBottomPanelIcon, 0xff8c9197);
        defaultColors.put(KeyHub.key_chat_emojiPanelIconSelected, 0xff2b97e2);
        defaultColors.put(KeyHub.key_chat_emojiPanelStickerPackSelector, 0xffe2e5e7);
        defaultColors.put(KeyHub.key_chat_emojiPanelStickerPackSelectorLine, 0xff56abf0);
        defaultColors.put(KeyHub.key_chat_emojiPanelBackspace, 0xff8c9197);
        defaultColors.put(KeyHub.key_chat_emojiPanelMasksIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_emojiPanelMasksIconSelected, 0xff62bfe8);
        defaultColors.put(KeyHub.key_chat_emojiPanelTrendingTitle, 0xff222222);
        defaultColors.put(KeyHub.key_chat_emojiPanelStickerSetName, 0xff828b94);
        defaultColors.put(KeyHub.key_chat_emojiPanelStickerSetNameHighlight, 0xff278ddb);
        defaultColors.put(KeyHub.key_chat_emojiPanelStickerSetNameIcon, 0xffb1b6bc);
        defaultColors.put(KeyHub.key_chat_emojiPanelTrendingDescription, 0xff8a8a8a);
        defaultColors.put(KeyHub.key_chat_botKeyboardButtonText, 0xff36474f);
        defaultColors.put(KeyHub.key_chat_botKeyboardButtonBackground, 0xffe4e7e9);
        defaultColors.put(KeyHub.key_chat_botKeyboardButtonBackgroundPressed, 0xffccd1d4);
        defaultColors.put(KeyHub.key_chat_unreadMessagesStartArrowIcon, 0xffa2b5c7);
        defaultColors.put(KeyHub.key_chat_unreadMessagesStartText, 0xff5695cc);
        defaultColors.put(KeyHub.key_chat_unreadMessagesStartBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inFileIcon, 0xffa2b5c7);
        defaultColors.put(KeyHub.key_chat_inFileSelectedIcon, 0xff87b6c5);
        defaultColors.put(KeyHub.key_chat_outFileIcon, 0xff85bf78);
        defaultColors.put(KeyHub.key_chat_outFileSelectedIcon, 0xff85bf78);
        defaultColors.put(KeyHub.key_chat_inLocationBackground, 0xffebf0f5);
        defaultColors.put(KeyHub.key_chat_inLocationIcon, 0xffa2b5c7);
        defaultColors.put(KeyHub.key_chat_outLocationBackground, 0xffdaf5c3);
        defaultColors.put(KeyHub.key_chat_outLocationIcon, 0xff87bf78);
        defaultColors.put(KeyHub.key_chat_inContactBackground, 0xff72b5e8);
        defaultColors.put(KeyHub.key_chat_inContactIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_outContactBackground, 0xff78c272);
        defaultColors.put(KeyHub.key_chat_outContactIcon, 0xffefffde);
        defaultColors.put(KeyHub.key_chat_outBroadcast, 0xff46aa36);
        defaultColors.put(KeyHub.key_chat_mediaBroadcast, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_searchPanelIcons, 0xff676a6f);
        defaultColors.put(KeyHub.key_chat_searchPanelText, 0xff676a6f);
        defaultColors.put(KeyHub.key_chat_secretChatStatusText, 0xff7f7f7f);
        defaultColors.put(KeyHub.key_chat_fieldOverlayText, 0xff3a8ccf);
        defaultColors.put(KeyHub.key_chat_stickersHintPanel, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_replyPanelIcons, 0xff57a8e6);
        defaultColors.put(KeyHub.key_chat_replyPanelClose, 0xff8e959b);
        defaultColors.put(KeyHub.key_chat_replyPanelName, 0xff3a8ccf);
        defaultColors.put(KeyHub.key_chat_replyPanelMessage, 0xff222222);
        defaultColors.put(KeyHub.key_chat_replyPanelLine, 0xffe8e8e8);
        defaultColors.put(KeyHub.key_chat_messagePanelBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_messagePanelText, 0xff000000);
        defaultColors.put(KeyHub.key_chat_messagePanelHint, 0xffa4acb3);
        defaultColors.put(KeyHub.key_chat_messagePanelCursor, 0xff54a1db);
        defaultColors.put(KeyHub.key_chat_messagePanelShadow, 0xff000000);
        defaultColors.put(KeyHub.key_chat_messagePanelIcons, 0xff8e959b);
        defaultColors.put(KeyHub.key_chat_recordedVoicePlayPause, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_recordedVoiceDot, 0xffda564d);
        defaultColors.put(KeyHub.key_chat_recordedVoiceBackground, 0xff5DADE8);
        defaultColors.put(KeyHub.key_chat_recordedVoiceProgress, 0xffB1DEFF);
        defaultColors.put(KeyHub.key_chat_recordedVoiceProgressInner, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_recordVoiceCancel, 0xff3A95D4);
        defaultColors.put(KeyHub.key_chat_recordedVoiceHighlight, 0x64ffffff);
        defaultColors.put(KeyHub.key_chat_messagePanelSend, 0xff62b0eb);
        defaultColors.put(KeyHub.key_chat_messagePanelVoiceLock, 0xffa4a4a4);
        defaultColors.put(KeyHub.key_chat_messagePanelVoiceLockBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_messagePanelVoiceLockShadow, 0xff000000);
        defaultColors.put(KeyHub.key_chat_recordTime, 0xff8e959b);
        defaultColors.put(KeyHub.key_chat_emojiPanelNewTrending, 0xff4da6ea);
        defaultColors.put(KeyHub.key_chat_gifSaveHintText, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_gifSaveHintBackground, 0xcc111111);
        defaultColors.put(KeyHub.key_chat_goDownButton, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_goDownButtonShadow, 0xff000000);
        defaultColors.put(KeyHub.key_chat_goDownButtonIcon, 0xff8e959b);
        defaultColors.put(KeyHub.key_chat_goDownButtonCounter, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_goDownButtonCounterBackground, 0xff4da2e8);
        defaultColors.put(KeyHub.key_chat_messagePanelCancelInlineBot, 0xffadadad);
        defaultColors.put(KeyHub.key_chat_messagePanelVoicePressed, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_messagePanelVoiceBackground, 0xff5DA6DE);
        defaultColors.put(KeyHub.key_chat_messagePanelVoiceDelete, 0xff737373);
        defaultColors.put(KeyHub.key_chat_messagePanelVoiceDuration, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_inlineResultIcon, 0xff5795cc);
        defaultColors.put(KeyHub.key_chat_topPanelBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_topPanelClose, 0xff8c959a);
        defaultColors.put(KeyHub.key_chat_topPanelLine, 0xff6c9fd2);
        defaultColors.put(KeyHub.key_chat_topPanelTitle, 0xff3a8ccf);
        defaultColors.put(KeyHub.key_chat_topPanelMessage, 0xff999999);
        defaultColors.put(KeyHub.key_chat_reportSpam, 0xffcf5957);
        defaultColors.put(KeyHub.key_chat_addContact, 0xff4a82b5);
        defaultColors.put(KeyHub.key_chat_inLoader, 0xff72b5e8);
        defaultColors.put(KeyHub.key_chat_inLoaderSelected, 0xff65abe0);
        defaultColors.put(KeyHub.key_chat_outLoader, 0xff78c272);
        defaultColors.put(KeyHub.key_chat_outLoaderSelected, 0xff6ab564);
        defaultColors.put(KeyHub.key_chat_inLoaderPhoto, 0xffa2b8c8);
        defaultColors.put(KeyHub.key_chat_inLoaderPhotoSelected, 0xffa2b5c7);
        defaultColors.put(KeyHub.key_chat_inLoaderPhotoIcon, 0xfffcfcfc);
        defaultColors.put(KeyHub.key_chat_inLoaderPhotoIconSelected, 0xffebf0f5);
        defaultColors.put(KeyHub.key_chat_outLoaderPhoto, 0xff85bf78);
        defaultColors.put(KeyHub.key_chat_outLoaderPhotoSelected, 0xff7db870);
        defaultColors.put(KeyHub.key_chat_outLoaderPhotoIcon, 0xffdaf5c3);
        defaultColors.put(KeyHub.key_chat_outLoaderPhotoIconSelected, 0xffc0e8a4);
        defaultColors.put(KeyHub.key_chat_mediaLoaderPhoto, 0x66000000);
        defaultColors.put(KeyHub.key_chat_mediaLoaderPhotoSelected, 0x7f000000);
        defaultColors.put(KeyHub.key_chat_mediaLoaderPhotoIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_chat_mediaLoaderPhotoIconSelected, 0xffd9d9d9);
        defaultColors.put(KeyHub.key_chat_secretTimerBackground, 0xcc3e648e);
        defaultColors.put(KeyHub.key_chat_secretTimerText, 0xffffffff);

        defaultColors.put(KeyHub.key_profile_creatorIcon, 0xff3a95d5);
        defaultColors.put(KeyHub.key_profile_actionIcon, 0xff81868a);
        defaultColors.put(KeyHub.key_profile_actionBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_profile_actionPressedBackground, 0xfff2f2f2);
        defaultColors.put(KeyHub.key_profile_verifiedBackground, 0xffb2d6f8);
        defaultColors.put(KeyHub.key_profile_verifiedCheck, 0xff4983b8);
        defaultColors.put(KeyHub.key_profile_title, 0xffffffff);
        defaultColors.put(KeyHub.key_profile_status, 0xffd7eafa);

        defaultColors.put(KeyHub.key_profile_tabText, 0xff878c90);
        defaultColors.put(KeyHub.key_profile_tabSelectedText, 0xff3a95d5);
        defaultColors.put(KeyHub.key_profile_tabSelectedLine, 0xff4fa6e9);
        defaultColors.put(KeyHub.key_profile_tabSelector, 0x0f000000);

        defaultColors.put(KeyHub.key_player_actionBar, 0xffffffff);
        defaultColors.put(KeyHub.key_player_actionBarSelector, 0x0f000000);
        defaultColors.put(KeyHub.key_player_actionBarTitle, 0xff2f3438);
        defaultColors.put(KeyHub.key_player_actionBarTop, 0x99000000);
        defaultColors.put(KeyHub.key_player_actionBarSubtitle, 0xff8a8a8a);
        defaultColors.put(KeyHub.key_player_actionBarItems, 0xff8a8a8a);
        defaultColors.put(KeyHub.key_player_background, 0xffffffff);
        defaultColors.put(KeyHub.key_player_time, 0xff8c9296);
        defaultColors.put(KeyHub.key_player_progressBackground, 0xffe9eff5);
        defaultColors.put(KeyHub.key_player_progressBackground2, 0xffCCD3DB);
        defaultColors.put(KeyHub.key_player_progressCachedBackground, 0xffe9eff5);
        defaultColors.put(KeyHub.key_player_progress, 0xff4b9fe3);
        defaultColors.put(KeyHub.key_player_placeholder, 0xffa8a8a8);
        defaultColors.put(KeyHub.key_player_placeholderBackground, 0xfff0f0f0);
        defaultColors.put(KeyHub.key_player_button, 0xff333333);
        defaultColors.put(KeyHub.key_player_buttonActive, 0xff4ca8ea);

        defaultColors.put(KeyHub.key_sheet_scrollUp, 0xffe1e4e8);
        defaultColors.put(KeyHub.key_sheet_other, 0xffc9cdd3);

        defaultColors.put(KeyHub.key_files_folderIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_files_folderIconBackground, 0xff5dafeb);
        defaultColors.put(KeyHub.key_files_iconText, 0xffffffff);

        defaultColors.put(KeyHub.key_sessions_devicesImage, 0xff969696);

        defaultColors.put(KeyHub.key_passport_authorizeBackground, 0xff45abef);
        defaultColors.put(KeyHub.key_passport_authorizeBackgroundSelected, 0xff409ddb);
        defaultColors.put(KeyHub.key_passport_authorizeText, 0xffffffff);

        defaultColors.put(KeyHub.key_location_sendLocationBackground, 0xff469df6);
        defaultColors.put(KeyHub.key_location_sendLocationIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_location_sendLocationText, 0xff1c8ad8);
        defaultColors.put(KeyHub.key_location_sendLiveLocationBackground, 0xff4fc244);
        defaultColors.put(KeyHub.key_location_sendLiveLocationIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_location_sendLiveLocationText, 0xff36ab24);
        defaultColors.put(KeyHub.key_location_liveLocationProgress, 0xff359fe5);
        defaultColors.put(KeyHub.key_location_placeLocationBackground, 0xff4ca8ea);
        defaultColors.put(KeyHub.key_location_actionIcon, 0xff3a4045);
        defaultColors.put(KeyHub.key_location_actionActiveIcon, 0xff4290e6);
        defaultColors.put(KeyHub.key_location_actionBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_location_actionPressedBackground, 0xfff2f2f2);

        defaultColors.put(KeyHub.key_dialog_liveLocationProgress, 0xff359fe5);

        defaultColors.put(KeyHub.key_calls_callReceivedGreenIcon, 0xff00c853);
        defaultColors.put(KeyHub.key_calls_callReceivedRedIcon, 0xffff4848);

        defaultColors.put(KeyHub.key_featuredStickers_addedIcon, 0xff50a8eb);
        defaultColors.put(KeyHub.key_featuredStickers_buttonProgress, 0xffffffff);
        defaultColors.put(KeyHub.key_featuredStickers_addButton, 0xff50a8eb);
        defaultColors.put(KeyHub.key_featuredStickers_addButtonPressed, 0xff439bde);
        defaultColors.put(KeyHub.key_featuredStickers_removeButtonText, 0xff5093d3);
        defaultColors.put(KeyHub.key_featuredStickers_buttonText, 0xffffffff);
        defaultColors.put(KeyHub.key_featuredStickers_unread, 0xff4da6ea);

        defaultColors.put(KeyHub.key_inappPlayerPerformer, 0xff2f3438);
        defaultColors.put(KeyHub.key_inappPlayerTitle, 0xff2f3438);
        defaultColors.put(KeyHub.key_inappPlayerBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_inappPlayerPlayPause, 0xff62b0eb);
        defaultColors.put(KeyHub.key_inappPlayerClose, 0xffa8a8a8);

        defaultColors.put(KeyHub.key_returnToCallBackground, 0xff44a1e3);
        defaultColors.put(KeyHub.key_returnToCallText, 0xffffffff);

        defaultColors.put(KeyHub.key_sharedMedia_startStopLoadIcon, 0xff36a2ee);
        defaultColors.put(KeyHub.key_sharedMedia_linkPlaceholder, 0xfff0f3f5);
        defaultColors.put(KeyHub.key_sharedMedia_linkPlaceholderText, 0xffb7bec3);
        defaultColors.put(KeyHub.key_sharedMedia_photoPlaceholder, 0xffedf3f7);
        defaultColors.put(KeyHub.key_sharedMedia_actionMode, 0xff4687b3);

        defaultColors.put(KeyHub.key_checkbox, 0xff5ec245);
        defaultColors.put(KeyHub.key_checkboxCheck, 0xffffffff);
        defaultColors.put(KeyHub.key_checkboxDisabled, 0xffb0b9c2);

        defaultColors.put(KeyHub.key_stickers_menu, 0xffb6bdc5);
        defaultColors.put(KeyHub.key_stickers_menuSelector, 0x0f000000);

        defaultColors.put(KeyHub.key_changephoneinfo_image, 0xffb8bfc5);
        defaultColors.put(KeyHub.key_changephoneinfo_image2, 0xff50a7ea);

        defaultColors.put(KeyHub.key_groupcreate_hintText, 0xffa1aab3);
        defaultColors.put(KeyHub.key_groupcreate_cursor, 0xff52a3db);
        defaultColors.put(KeyHub.key_groupcreate_sectionShadow, 0xff000000);
        defaultColors.put(KeyHub.key_groupcreate_sectionText, 0xff7c8288);
        defaultColors.put(KeyHub.key_groupcreate_spanText, 0xff222222);
        defaultColors.put(KeyHub.key_groupcreate_spanBackground, 0xfff2f2f2);
        defaultColors.put(KeyHub.key_groupcreate_spanDelete, 0xffffffff);

        defaultColors.put(KeyHub.key_contacts_inviteBackground, 0xff55be61);
        defaultColors.put(KeyHub.key_contacts_inviteText, 0xffffffff);

        defaultColors.put(KeyHub.key_login_progressInner, 0xffe1eaf2);
        defaultColors.put(KeyHub.key_login_progressOuter, 0xff62a0d0);

        defaultColors.put(KeyHub.key_musicPicker_checkbox, 0xff29b6f7);
        defaultColors.put(KeyHub.key_musicPicker_checkboxCheck, 0xffffffff);
        defaultColors.put(KeyHub.key_musicPicker_buttonBackground, 0xff5cafea);
        defaultColors.put(KeyHub.key_musicPicker_buttonIcon, 0xffffffff);
        defaultColors.put(KeyHub.key_picker_enabledButton, 0xff19a7e8);
        defaultColors.put(KeyHub.key_picker_disabledButton, 0xff999999);
        defaultColors.put(KeyHub.key_picker_badge, 0xff29b6f7);
        defaultColors.put(KeyHub.key_picker_badgeText, 0xffffffff);

        defaultColors.put(KeyHub.key_chat_botSwitchToInlineText, 0xff4391cc);

        defaultColors.put(KeyHub.key_undo_background, 0xea272f38);
        defaultColors.put(KeyHub.key_undo_cancelColor, 0xff85caff);
        defaultColors.put(KeyHub.key_undo_infoColor, 0xffffffff);

        defaultColors.put(KeyHub.key_wallet_blackBackground, 0xff000000);
        defaultColors.put(KeyHub.key_wallet_graySettingsBackground, 0xfff0f0f0);
        defaultColors.put(KeyHub.key_wallet_grayBackground, 0xff292929);
        defaultColors.put(KeyHub.key_wallet_whiteBackground, 0xffffffff);
        defaultColors.put(KeyHub.key_wallet_blackBackgroundSelector, 0x40ffffff);
        defaultColors.put(KeyHub.key_wallet_whiteText, 0xffffffff);
        defaultColors.put(KeyHub.key_wallet_blackText, 0xff222222);
        defaultColors.put(KeyHub.key_wallet_statusText, 0xff808080);
        defaultColors.put(KeyHub.key_wallet_grayText, 0xff777777);
        defaultColors.put(KeyHub.key_wallet_grayText2, 0xff666666);
        defaultColors.put(KeyHub.key_wallet_greenText, 0xff37a818);
        defaultColors.put(KeyHub.key_wallet_redText, 0xffdb4040);
        defaultColors.put(KeyHub.key_wallet_dateText, 0xff999999);
        defaultColors.put(KeyHub.key_wallet_commentText, 0xff999999);
        defaultColors.put(KeyHub.key_wallet_releaseBackground, 0xff307cbb);
        defaultColors.put(KeyHub.key_wallet_pullBackground, 0xff212121);
        defaultColors.put(KeyHub.key_wallet_buttonBackground, 0xff47a1e6);
        defaultColors.put(KeyHub.key_wallet_buttonPressedBackground, 0xff2b8cd6);
        defaultColors.put(KeyHub.key_wallet_buttonText, 0xffffffff);
        defaultColors.put(KeyHub.key_wallet_addressConfirmBackground, 0x0d000000);
        defaultColors.put(KeyHub.key_chat_outTextSelectionHighlight, 0x2E3F9923);
        defaultColors.put(KeyHub.key_chat_inTextSelectionHighlight, 0x5062A9E3);
        defaultColors.put(KeyHub.key_chat_TextSelectionCursor, 0xFF419FE8);

        defaultColors.put(KeyHub.key_statisticChartSignature, 0x7f252529);
        defaultColors.put(KeyHub.key_statisticChartSignatureAlpha, 0x7f252529);
        defaultColors.put(KeyHub.key_statisticChartHintLine, 0x1a182D3B);
        defaultColors.put(KeyHub.key_statisticChartActiveLine, 0x33000000);
        defaultColors.put(KeyHub.key_statisticChartInactivePickerChart, 0x99e2eef9);
        defaultColors.put(KeyHub.key_statisticChartActivePickerChart, 0xd8baccd9);

        defaultColors.put(KeyHub.key_statisticChartRipple, 0x2c7e9db7);
        defaultColors.put(KeyHub.key_statisticChartBackZoomColor, 0xff108BE3);
        defaultColors.put(KeyHub.key_statisticChartCheckboxInactive, 0xffBDBDBD);
        defaultColors.put(KeyHub.key_statisticChartNightIconColor, 0xff8E8E93);
        defaultColors.put(KeyHub.key_statisticChartChevronColor, 0xffD2D5D7);
        defaultColors.put(KeyHub.key_statisticChartHighlightColor, 0x20ececec);
        defaultColors.put(KeyHub.key_statisticChartPopupBackground, 0xffffffff);

        defaultColors.put(KeyHub.key_statisticChartLine_blue, 0xff327FE5);
        defaultColors.put(KeyHub.key_statisticChartLine_green, 0xff61C752);
        defaultColors.put(KeyHub.key_statisticChartLine_red, 0xffE05356);
        defaultColors.put(KeyHub.key_statisticChartLine_golden, 0xffDEBA08);
        defaultColors.put(KeyHub.key_statisticChartLine_lightblue, 0xff58A8ED);
        defaultColors.put(KeyHub.key_statisticChartLine_lightgreen, 0xff8FCF39);
        defaultColors.put(KeyHub.key_statisticChartLine_orange, 0xffE3B727);
        defaultColors.put(KeyHub.key_statisticChartLine_indigo, 0xff7F79F3);
        //endregion

        //region fallbackKeys
        fallbackKeys.put(KeyHub.key_chat_adminText, KeyHub.key_chat_inTimeText);
        fallbackKeys.put(KeyHub.key_chat_adminSelectedText, KeyHub.key_chat_inTimeSelectedText);
        fallbackKeys.put(KeyHub.key_player_progressCachedBackground, KeyHub.key_player_progressBackground);
        fallbackKeys.put(KeyHub.key_chat_inAudioCacheSeekbar, KeyHub.key_chat_inAudioSeekbar);
        fallbackKeys.put(KeyHub.key_chat_outAudioCacheSeekbar, KeyHub.key_chat_outAudioSeekbar);
        fallbackKeys.put(KeyHub.key_chat_emojiSearchBackground, KeyHub.key_chat_emojiPanelStickerPackSelector);
        fallbackKeys.put(KeyHub.key_location_sendLiveLocationIcon, KeyHub.key_location_sendLocationIcon);
        fallbackKeys.put(KeyHub.key_changephoneinfo_image2, KeyHub.key_featuredStickers_addButton);
        fallbackKeys.put(KeyHub.key_graySectionText, KeyHub.key_windowBackgroundWhiteGrayText2);
        fallbackKeys.put(KeyHub.key_chat_inMediaIcon, KeyHub.key_chat_inBubble);
        fallbackKeys.put(KeyHub.key_chat_outMediaIcon, KeyHub.key_chat_outBubble);
        fallbackKeys.put(KeyHub.key_chat_inMediaIconSelected, KeyHub.key_chat_inBubbleSelected);
        fallbackKeys.put(KeyHub.key_chat_outMediaIconSelected, KeyHub.key_chat_outBubbleSelected);
        fallbackKeys.put(KeyHub.key_chats_actionUnreadIcon, KeyHub.key_profile_actionIcon);
        fallbackKeys.put(KeyHub.key_chats_actionUnreadBackground, KeyHub.key_profile_actionBackground);
        fallbackKeys.put(KeyHub.key_chats_actionUnreadPressedBackground, KeyHub.key_profile_actionPressedBackground);
        fallbackKeys.put(KeyHub.key_dialog_inlineProgressBackground, KeyHub.key_windowBackgroundGray);
        fallbackKeys.put(KeyHub.key_dialog_inlineProgress, KeyHub.key_chats_menuItemIcon);
        fallbackKeys.put(KeyHub.key_groupcreate_spanDelete, KeyHub.key_chats_actionIcon);
        fallbackKeys.put(KeyHub.key_sharedMedia_photoPlaceholder, KeyHub.key_windowBackgroundGray);
        fallbackKeys.put(KeyHub.key_chat_attachPollBackground, KeyHub.key_chat_attachAudioBackground);
        fallbackKeys.put(KeyHub.key_chat_attachPollIcon, KeyHub.key_chat_attachAudioIcon);
        fallbackKeys.put(KeyHub.key_chats_onlineCircle, KeyHub.key_windowBackgroundWhiteBlueText);
        fallbackKeys.put(KeyHub.key_windowBackgroundWhiteBlueButton, KeyHub.key_windowBackgroundWhiteValueText);
        fallbackKeys.put(KeyHub.key_windowBackgroundWhiteBlueIcon, KeyHub.key_windowBackgroundWhiteValueText);
        fallbackKeys.put(KeyHub.key_undo_background, KeyHub.key_chat_gifSaveHintBackground);
        fallbackKeys.put(KeyHub.key_undo_cancelColor, KeyHub.key_chat_gifSaveHintText);
        fallbackKeys.put(KeyHub.key_undo_infoColor, KeyHub.key_chat_gifSaveHintText);
        fallbackKeys.put(KeyHub.key_windowBackgroundUnchecked, KeyHub.key_windowBackgroundWhite);
        fallbackKeys.put(KeyHub.key_windowBackgroundChecked, KeyHub.key_windowBackgroundWhite);
        fallbackKeys.put(KeyHub.key_switchTrackBlue, KeyHub.key_switchTrack);
        fallbackKeys.put(KeyHub.key_switchTrackBlueChecked, KeyHub.key_switchTrackChecked);
        fallbackKeys.put(KeyHub.key_switchTrackBlueThumb, KeyHub.key_windowBackgroundWhite);
        fallbackKeys.put(KeyHub.key_switchTrackBlueThumbChecked, KeyHub.key_windowBackgroundWhite);
        fallbackKeys.put(KeyHub.key_windowBackgroundCheckText, KeyHub.key_windowBackgroundWhiteBlackText);
        fallbackKeys.put(KeyHub.key_contextProgressInner4, KeyHub.key_contextProgressInner1);
        fallbackKeys.put(KeyHub.key_contextProgressOuter4, KeyHub.key_contextProgressOuter1);
        fallbackKeys.put(KeyHub.key_switchTrackBlueSelector, KeyHub.key_listSelector);
        fallbackKeys.put(KeyHub.key_switchTrackBlueSelectorChecked, KeyHub.key_listSelector);
        fallbackKeys.put(KeyHub.key_chat_emojiBottomPanelIcon, KeyHub.key_chat_emojiPanelIcon);
        fallbackKeys.put(KeyHub.key_chat_emojiSearchIcon, KeyHub.key_chat_emojiPanelIcon);
        fallbackKeys.put(KeyHub.key_chat_emojiPanelStickerSetNameHighlight, KeyHub.key_windowBackgroundWhiteBlueText4);
        fallbackKeys.put(KeyHub.key_chat_emojiPanelStickerPackSelectorLine, KeyHub.key_chat_emojiPanelIconSelected);
        fallbackKeys.put(KeyHub.key_sharedMedia_actionMode, KeyHub.key_actionBarDefault);
        fallbackKeys.put(KeyHub.key_sheet_scrollUp, KeyHub.key_chat_emojiPanelStickerPackSelector);
        fallbackKeys.put(KeyHub.key_sheet_other, KeyHub.key_player_actionBarItems);
        fallbackKeys.put(KeyHub.key_dialogSearchBackground, KeyHub.key_chat_emojiPanelStickerPackSelector);
        fallbackKeys.put(KeyHub.key_dialogSearchHint, KeyHub.key_chat_emojiPanelIcon);
        fallbackKeys.put(KeyHub.key_dialogSearchIcon, KeyHub.key_chat_emojiPanelIcon);
        fallbackKeys.put(KeyHub.key_dialogSearchText, KeyHub.key_windowBackgroundWhiteBlackText);
        fallbackKeys.put(KeyHub.key_dialogFloatingButton, KeyHub.key_dialogRoundCheckBox);
        fallbackKeys.put(KeyHub.key_dialogFloatingButtonPressed, KeyHub.key_dialogRoundCheckBox);
        fallbackKeys.put(KeyHub.key_dialogFloatingIcon, KeyHub.key_dialogRoundCheckBoxCheck);
        fallbackKeys.put(KeyHub.key_dialogShadowLine, KeyHub.key_chat_emojiPanelShadowLine);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchived, KeyHub.key_actionBarDefault);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchivedSelector, KeyHub.key_actionBarDefaultSelector);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchivedIcon, KeyHub.key_actionBarDefaultIcon);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchivedTitle, KeyHub.key_actionBarDefaultTitle);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchivedSearch, KeyHub.key_actionBarDefaultSearch);
        fallbackKeys.put(KeyHub.key_actionBarDefaultArchivedSearchPlaceholder, KeyHub.key_actionBarDefaultSearchPlaceholder);
        fallbackKeys.put(KeyHub.key_chats_message_threeLines, KeyHub.key_chats_message);
        fallbackKeys.put(KeyHub.key_chats_nameMessage_threeLines, KeyHub.key_chats_nameMessage);
        fallbackKeys.put(KeyHub.key_chats_nameArchived, KeyHub.key_chats_name);
        fallbackKeys.put(KeyHub.key_chats_nameMessageArchived, KeyHub.key_chats_nameMessage);
        fallbackKeys.put(KeyHub.key_chats_nameMessageArchived_threeLines, KeyHub.key_chats_nameMessage);
        fallbackKeys.put(KeyHub.key_chats_messageArchived, KeyHub.key_chats_message);
        fallbackKeys.put(KeyHub.key_avatar_backgroundArchived, KeyHub.key_chats_unreadCounterMuted);
        fallbackKeys.put(KeyHub.key_chats_archiveBackground, KeyHub.key_chats_actionBackground);
        fallbackKeys.put(KeyHub.key_chats_archivePinBackground, KeyHub.key_chats_unreadCounterMuted);
        fallbackKeys.put(KeyHub.key_chats_archiveIcon, KeyHub.key_chats_actionIcon);
        fallbackKeys.put(KeyHub.key_chats_archiveText, KeyHub.key_chats_actionIcon);
        fallbackKeys.put(KeyHub.key_actionBarDefaultSubmenuItemIcon, KeyHub.key_dialogIcon);
        fallbackKeys.put(KeyHub.key_checkboxDisabled, KeyHub.key_chats_unreadCounterMuted);
        fallbackKeys.put(KeyHub.key_chat_status, KeyHub.key_actionBarDefaultSubtitle);
        fallbackKeys.put(KeyHub.key_chat_inGreenCall, KeyHub.key_calls_callReceivedGreenIcon);
        fallbackKeys.put(KeyHub.key_chat_inRedCall, KeyHub.key_calls_callReceivedRedIcon);
        fallbackKeys.put(KeyHub.key_chat_outGreenCall, KeyHub.key_calls_callReceivedGreenIcon);
        fallbackKeys.put(KeyHub.key_actionBarTabActiveText, KeyHub.key_actionBarDefaultTitle);
        fallbackKeys.put(KeyHub.key_actionBarTabUnactiveText, KeyHub.key_actionBarDefaultSubtitle);
        fallbackKeys.put(KeyHub.key_actionBarTabLine, KeyHub.key_actionBarDefaultTitle);
        fallbackKeys.put(KeyHub.key_actionBarTabSelector, KeyHub.key_actionBarDefaultSelector);
        fallbackKeys.put(KeyHub.key_profile_status, KeyHub.key_avatar_subtitleInProfileBlue);
        fallbackKeys.put(KeyHub.key_chats_menuTopBackgroundCats, KeyHub.key_avatar_backgroundActionBarBlue);
        //fallbackKeys.put(KeyHub.key_chat_attachActiveTab, 0xff33a7f5);
        //fallbackKeys.put(KeyHub.key_chat_attachUnactiveTab, 0xff92999e);
        fallbackKeys.put(KeyHub.key_chat_attachPermissionImage, KeyHub.key_dialogTextBlack);
        fallbackKeys.put(KeyHub.key_chat_attachPermissionMark, KeyHub.key_chat_sentError);
        fallbackKeys.put(KeyHub.key_chat_attachPermissionText, KeyHub.key_dialogTextBlack);
        fallbackKeys.put(KeyHub.key_chat_attachEmptyImage, KeyHub.key_emptyListPlaceholder);
        fallbackKeys.put(KeyHub.key_actionBarBrowser, KeyHub.key_actionBarDefault);
        fallbackKeys.put(KeyHub.key_chats_sentReadCheck, KeyHub.key_chats_sentCheck);
        fallbackKeys.put(KeyHub.key_chat_outSentCheckRead, KeyHub.key_chat_outSentCheck);
        fallbackKeys.put(KeyHub.key_chat_outSentCheckReadSelected, KeyHub.key_chat_outSentCheckSelected);
        fallbackKeys.put(KeyHub.key_chats_archivePullDownBackground, KeyHub.key_chats_unreadCounterMuted);
        fallbackKeys.put(KeyHub.key_chats_archivePullDownBackgroundActive, KeyHub.key_chats_actionBackground);
        fallbackKeys.put(KeyHub.key_avatar_backgroundArchivedHidden, KeyHub.key_avatar_backgroundSaved);
        fallbackKeys.put(KeyHub.key_featuredStickers_removeButtonText, KeyHub.key_featuredStickers_addButtonPressed);
        fallbackKeys.put(KeyHub.key_dialogEmptyImage, KeyHub.key_player_time);
        fallbackKeys.put(KeyHub.key_dialogEmptyText, KeyHub.key_player_time);
        fallbackKeys.put(KeyHub.key_location_actionIcon, KeyHub.key_dialogTextBlack);
        fallbackKeys.put(KeyHub.key_location_actionActiveIcon, KeyHub.key_windowBackgroundWhiteBlueText7);
        fallbackKeys.put(KeyHub.key_location_actionBackground, KeyHub.key_dialogBackground);
        fallbackKeys.put(KeyHub.key_location_actionPressedBackground, KeyHub.key_dialogBackgroundGray);
        fallbackKeys.put(KeyHub.key_location_sendLocationText, KeyHub.key_windowBackgroundWhiteBlueText7);
        fallbackKeys.put(KeyHub.key_location_sendLiveLocationText, KeyHub.key_windowBackgroundWhiteGreenText);
        fallbackKeys.put(KeyHub.key_chat_outTextSelectionHighlight, KeyHub.key_chat_textSelectBackground);
        fallbackKeys.put(KeyHub.key_chat_inTextSelectionHighlight, KeyHub.key_chat_textSelectBackground);
        fallbackKeys.put(KeyHub.key_chat_TextSelectionCursor, KeyHub.key_chat_messagePanelCursor);
        fallbackKeys.put(KeyHub.key_chat_inPollCorrectAnswer, KeyHub.key_chat_attachLocationBackground);
        fallbackKeys.put(KeyHub.key_chat_outPollCorrectAnswer, KeyHub.key_chat_attachLocationBackground);
        fallbackKeys.put(KeyHub.key_chat_inPollWrongAnswer, KeyHub.key_chat_attachAudioBackground);
        fallbackKeys.put(KeyHub.key_chat_outPollWrongAnswer, KeyHub.key_chat_attachAudioBackground);

        fallbackKeys.put(KeyHub.key_profile_tabText, KeyHub.key_windowBackgroundWhiteGrayText);
        fallbackKeys.put(KeyHub.key_profile_tabSelectedText, KeyHub.key_windowBackgroundWhiteBlueHeader);
        fallbackKeys.put(KeyHub.key_profile_tabSelectedLine, KeyHub.key_windowBackgroundWhiteBlueHeader);
        fallbackKeys.put(KeyHub.key_profile_tabSelector, KeyHub.key_listSelector);
        fallbackKeys.put(KeyHub.key_statisticChartPopupBackground, KeyHub.key_dialogBackground);

        fallbackKeys.put(KeyHub.key_chat_attachGalleryText, KeyHub.key_chat_attachGalleryBackground);
        fallbackKeys.put(KeyHub.key_chat_attachAudioText, KeyHub.key_chat_attachAudioBackground);
        fallbackKeys.put(KeyHub.key_chat_attachFileText, KeyHub.key_chat_attachFileBackground);
        fallbackKeys.put(KeyHub.key_chat_attachContactText, KeyHub.key_chat_attachContactBackground);
        fallbackKeys.put(KeyHub.key_chat_attachLocationText, KeyHub.key_chat_attachLocationBackground);
        fallbackKeys.put(KeyHub.key_chat_attachPollText, KeyHub.key_chat_attachPollBackground);

        fallbackKeys.put(KeyHub.key_chat_inPsaNameText, KeyHub.key_avatar_nameInMessageGreen);
        fallbackKeys.put(KeyHub.key_chat_outPsaNameText, KeyHub.key_avatar_nameInMessageGreen);
        //endregion

        //region themeAccentExclusionKeys
        themeAccentExclusionKeys.addAll(Arrays.asList(KeyHub.keys_avatar_background));
        themeAccentExclusionKeys.addAll(Arrays.asList(KeyHub.keys_avatar_nameInMessage));
        themeAccentExclusionKeys.add(KeyHub.key_chat_attachFileBackground);
        themeAccentExclusionKeys.add(KeyHub.key_chat_attachGalleryBackground);
        themeAccentExclusionKeys.add(KeyHub.key_chat_attachFileText);
        themeAccentExclusionKeys.add(KeyHub.key_chat_attachGalleryText);
        themeAccentExclusionKeys.add(KeyHub.key_chat_shareBackground);
        themeAccentExclusionKeys.add(KeyHub.key_chat_shareBackgroundSelected);
        //endregion

        //region myMessagesColorKeys
        myMessagesColorKeys.add(KeyHub.key_chat_outGreenCall);
        myMessagesColorKeys.add(KeyHub.key_chat_outBubble);
        myMessagesColorKeys.add(KeyHub.key_chat_outBubbleSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outBubbleShadow);
        myMessagesColorKeys.add(KeyHub.key_chat_outBubbleGradient);
        myMessagesColorKeys.add(KeyHub.key_chat_outSentCheck);
        myMessagesColorKeys.add(KeyHub.key_chat_outSentCheckSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outSentCheckRead);
        myMessagesColorKeys.add(KeyHub.key_chat_outSentCheckReadSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outSentClock);
        myMessagesColorKeys.add(KeyHub.key_chat_outSentClockSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outMediaIcon);
        myMessagesColorKeys.add(KeyHub.key_chat_outMediaIconSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outViews);
        myMessagesColorKeys.add(KeyHub.key_chat_outViewsSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outMenu);
        myMessagesColorKeys.add(KeyHub.key_chat_outMenuSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outInstant);
        myMessagesColorKeys.add(KeyHub.key_chat_outInstantSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outPreviewInstantText);
        myMessagesColorKeys.add(KeyHub.key_chat_outPreviewInstantSelectedText);
        myMessagesColorKeys.add(KeyHub.key_chat_outForwardedNameText);
        myMessagesColorKeys.add(KeyHub.key_chat_outViaBotNameText);
        myMessagesColorKeys.add(KeyHub.key_chat_outReplyLine);
        myMessagesColorKeys.add(KeyHub.key_chat_outReplyNameText);
        myMessagesColorKeys.add(KeyHub.key_chat_outReplyMessageText);
        myMessagesColorKeys.add(KeyHub.key_chat_outReplyMediaMessageText);
        myMessagesColorKeys.add(KeyHub.key_chat_outReplyMediaMessageSelectedText);
        myMessagesColorKeys.add(KeyHub.key_chat_outPreviewLine);
        myMessagesColorKeys.add(KeyHub.key_chat_outSiteNameText);
        myMessagesColorKeys.add(KeyHub.key_chat_outContactNameText);
        myMessagesColorKeys.add(KeyHub.key_chat_outContactPhoneText);
        myMessagesColorKeys.add(KeyHub.key_chat_outContactPhoneSelectedText);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioProgress);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioSelectedProgress);
        myMessagesColorKeys.add(KeyHub.key_chat_outTimeText);
        myMessagesColorKeys.add(KeyHub.key_chat_outTimeSelectedText);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioPerformerText);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioPerformerSelectedText);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioTitleText);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioDurationText);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioDurationSelectedText);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioSeekbar);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioCacheSeekbar);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioSeekbarSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outAudioSeekbarFill);
        myMessagesColorKeys.add(KeyHub.key_chat_outVoiceSeekbar);
        myMessagesColorKeys.add(KeyHub.key_chat_outVoiceSeekbarSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outVoiceSeekbarFill);
        myMessagesColorKeys.add(KeyHub.key_chat_outFileProgress);
        myMessagesColorKeys.add(KeyHub.key_chat_outFileProgressSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outFileNameText);
        myMessagesColorKeys.add(KeyHub.key_chat_outFileInfoText);
        myMessagesColorKeys.add(KeyHub.key_chat_outFileInfoSelectedText);
        myMessagesColorKeys.add(KeyHub.key_chat_outFileBackground);
        myMessagesColorKeys.add(KeyHub.key_chat_outFileBackgroundSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outVenueInfoText);
        myMessagesColorKeys.add(KeyHub.key_chat_outVenueInfoSelectedText);
        myMessagesColorKeys.add(KeyHub.key_chat_outLoader);
        myMessagesColorKeys.add(KeyHub.key_chat_outLoaderSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outLoaderPhoto);
        myMessagesColorKeys.add(KeyHub.key_chat_outLoaderPhotoSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outLoaderPhotoIcon);
        myMessagesColorKeys.add(KeyHub.key_chat_outLoaderPhotoIconSelected);
        myMessagesColorKeys.add(KeyHub.key_chat_outLocationBackground);
        myMessagesColorKeys.add(KeyHub.key_chat_outLocationIcon);
        myMessagesColorKeys.add(KeyHub.key_chat_outContactBackground);
        myMessagesColorKeys.add(KeyHub.key_chat_outContactIcon);
        myMessagesColorKeys.add(KeyHub.key_chat_outFileIcon);
        myMessagesColorKeys.add(KeyHub.key_chat_outFileSelectedIcon);
        myMessagesColorKeys.add(KeyHub.key_chat_outBroadcast);
        myMessagesColorKeys.add(KeyHub.key_chat_messageTextOut);
        myMessagesColorKeys.add(KeyHub.key_chat_messageLinkOut);
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

        for (int a = 0; a < 1; a++) {
            remoteThemesHash[a] = themeConfig.getInt("remoteThemesHash" + (a != 0 ? a : ""), 0);
            lastLoadingThemesTime[a] = themeConfig.getInt("lastLoadingThemesTime" + (a != 0 ? a : ""), 0);
        }

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
                        themeInfo.loadWallpapers(themeConfig);
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
                    info.loadWallpapers(themeConfig);
                    ThemeAccent accent = info.getAccent(false);
                    if (accent != null) {
                        info.overrideWallpaper = accent.overrideWallpaper;
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

        if (preferences.contains("overrideThemeWallpaper") || preferences.contains("selectedBackground2")) {
            boolean override = preferences.getBoolean("overrideThemeWallpaper", false);
            long id = preferences.getLong("selectedBackground2", 1000001);
            if (id == -1 || override && id != -2 && id != 1000001) {
                OverrideWallpaperInfo overrideWallpaper = new OverrideWallpaperInfo();
                overrideWallpaper.color = preferences.getInt("selectedColor", 0);
                overrideWallpaper.slug = preferences.getString("selectedBackgroundSlug", "");
                if (id >= -100 && id <= -1 && overrideWallpaper.color != 0) {
                    overrideWallpaper.slug = COLOR_BACKGROUND_SLUG;
                    overrideWallpaper.fileName = "";
                    overrideWallpaper.originalFileName = "";
                } else {
                    overrideWallpaper.fileName = "wallpaper.jpg";
                    overrideWallpaper.originalFileName = "wallpaper_original.jpg";
                }
                overrideWallpaper.gradientColor = preferences.getInt("selectedGradientColor", 0);
                overrideWallpaper.rotation = preferences.getInt("selectedGradientRotation", 45);
                overrideWallpaper.isBlurred = preferences.getBoolean("selectedBackgroundBlurred", false);
                overrideWallpaper.isMotion = preferences.getBoolean("selectedBackgroundMotion", false);
                overrideWallpaper.intensity = preferences.getFloat("selectedIntensity", 0.5f);
                currentDayTheme.setOverrideWallpaper(overrideWallpaper);
                if (selectedAutoNightType != AUTO_NIGHT_TYPE_NONE) {
                    currentNightTheme.setOverrideWallpaper(overrideWallpaper);
                }
            }
            preferences.edit().remove("overrideThemeWallpaper").remove("selectedBackground2").apply();
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
        applyChatServiceMessageColor(null);
    }

    public static void applyChatServiceMessageColor(int[] custom) {
        Integer serviceColor;
        Integer servicePressedColor;
        serviceMessageColor = serviceMessageColorBackup;
        serviceSelectedMessageColor = serviceSelectedMessageColorBackup;
        if (custom != null && custom.length >= 2) {
            serviceColor = custom[0];
            servicePressedColor = custom[1];
            serviceMessageColor = custom[0];
            serviceSelectedMessageColor = custom[1];
        } else {
            serviceColor = currentColors.get(KeyHub.key_chat_serviceBackground);
            servicePressedColor = currentColors.get(KeyHub.key_chat_serviceBackgroundSelected);
        }
        Integer serviceColor2 = serviceColor;
        Integer servicePressedColor2 = servicePressedColor;

        if (serviceColor == null) {
            serviceColor = serviceMessageColor;
            serviceColor2 = serviceMessage2Color;
        }
        if (servicePressedColor == null) {
            servicePressedColor = serviceSelectedMessageColor;
            servicePressedColor2 = serviceSelectedMessage2Color;
        }
        if (currentColor != serviceColor) {
            colorFilter = new PorterDuffColorFilter(serviceColor, PorterDuff.Mode.MULTIPLY);
            colorFilter2 = new PorterDuffColorFilter(serviceColor2, PorterDuff.Mode.MULTIPLY);
            currentColor = serviceColor;
        }
        if (currentSelectedColor != servicePressedColor) {
            currentSelectedColor = servicePressedColor;
            colorPressedFilter = new PorterDuffColorFilter(servicePressedColor, PorterDuff.Mode.MULTIPLY);
            colorPressedFilter2 = new PorterDuffColorFilter(servicePressedColor2, PorterDuff.Mode.MULTIPLY);
        }
    }
    //endregion

    //region 工具函数
    public static int getDefaultColor(String key) {
        Integer value = defaultColors.get(key);
        if (value == null) {
            if (key.equals(KeyHub.key_chats_menuTopShadow) || key.equals(KeyHub.key_chats_menuTopBackground) || key.equals(KeyHub.key_chats_menuTopShadowCats)) {
                return 0;
            }
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
            boolean useDefault;
            if (myMessagesColorKeys.contains(key)) {
                useDefault = currentTheme.isDefaultMyMessages();
            } else if (KeyHub.key_chat_wallpaper.equals(key) || KeyHub.key_chat_wallpaper_gradient_to.equals(key)) {
                useDefault = false;
            } else {
                useDefault = currentTheme.isDefaultMainAccent();
            }
            if (useDefault) {
                if (key.equals(KeyHub.key_chat_serviceBackground)) {
                    return serviceMessageColor;
                } else if (key.equals(KeyHub.key_chat_serviceBackgroundSelected)) {
                    return serviceSelectedMessageColor;
                }
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
                if (key.equals(KeyHub.key_chat_serviceBackground)) {
                    return serviceMessageColor;
                } else if (key.equals(KeyHub.key_chat_serviceBackgroundSelected)) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(key);
            }
        }
        if (KeyHub.key_windowBackgroundWhite.equals(key) || KeyHub.key_windowBackgroundGray.equals(key)) {
            color |= 0xff000000;
        }
        return color;
    }

    public static void setColor(Context context, String key, int color, boolean useDefault) {
        if (key.equals(KeyHub.key_chat_wallpaper) || key.equals(KeyHub.key_chat_wallpaper_gradient_to) || key.equals(KeyHub.key_windowBackgroundWhite) || key.equals(KeyHub.key_windowBackgroundGray)) {
            color = 0xff000000 | color;
        }

        if (useDefault) {
            currentColors.remove(key);
        } else {
            currentColors.put(key, color);
        }

        switch (key) {
            case KeyHub.key_chat_serviceBackground:
            case KeyHub.key_chat_serviceBackgroundSelected:
                applyChatServiceMessageColor();
                break;
            case KeyHub.key_chat_wallpaper:
            case KeyHub.key_chat_wallpaper_gradient_to:
            case KeyHub.key_chat_wallpaper_gradient_rotation:
                WallpaperManager.reloadWallpaper(context);
                break;
            case KeyHub.key_actionBarDefault:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NotificationCenter.postNotificationName(NotificationCenter.needCheckSystemBarColors);
                }
                break;
            case KeyHub.key_windowBackgroundGray:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationCenter.postNotificationName(NotificationCenter.needCheckSystemBarColors);
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

    static void calcBackgroundColor(Drawable drawable, int save) {
        if (save != 2) {
            int[] result = AndroidUtilities.calcDrawableColor(drawable);
            serviceMessageColor = serviceMessageColorBackup = result[0];
            serviceSelectedMessageColor = serviceSelectedMessageColorBackup = result[1];
            serviceMessage2Color = result[2];
            serviceSelectedMessage2Color = result[3];
        }
    }

    public static int getServiceMessageColor() {
        Integer serviceColor = currentColors.get(KeyHub.key_chat_serviceBackground);
        return serviceColor == null ? serviceMessageColor : serviceColor;
    }
    //endregion
}
