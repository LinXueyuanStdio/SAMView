//package com.same.ui.page.theme;
//
//import android.Manifest;
//import android.animation.ObjectAnimator;
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.drawable.Drawable;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.os.Build;
//import android.text.TextUtils;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//
//import com.same.lib.core.ActionBar;
//import com.same.lib.core.ActionBarMenu;
//import com.same.lib.core.ActionBarMenuItem;
//import com.same.lib.core.AlertDialog;
//import com.same.lib.core.BasePage;
//import com.same.lib.helper.LayoutHelper;
//import com.same.lib.theme.Theme;
//import com.same.lib.theme.ThemeAccent;
//import com.same.lib.theme.ThemeDescription;
//import com.same.lib.theme.ThemeInfo;
//import com.same.lib.theme.ThemeManager;
//import com.same.lib.util.AndroidUtilities;
//import com.same.lib.util.NotificationCenter;
//import com.same.lib.util.SharedConfig;
//import com.same.ui.BuildConfig;
//import com.same.ui.R;
//import com.same.ui.lang.MyLang;
//import com.same.ui.page.language.cell.ShadowSectionCell;
//import com.same.ui.page.theme.cell.ThemeTypeCell;
//import com.same.ui.page.theme.cell.ThemesHorizontalListCell;
//import com.same.ui.view.RecyclerListView;
//import com.timecat.component.locale.time.SunDate;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//
//import androidx.annotation.Keep;
//import androidx.core.content.FileProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import static com.same.lib.util.SharedConfig.isRTL;
//
///**
// * @author 林学渊
// * @email linxy59@mail2.sysu.edu.cn
// * @date 2020/10/28
// * @description null
// * @usage null
// */
//public class ThemePage2 extends BasePage {
//
//    public final static int THEME_TYPE_BASIC = 0;
//    public final static int THEME_TYPE_NIGHT = 1;
//    public final static int THEME_TYPE_OTHER = 2;
//
//    private ListAdapter listAdapter;
//    private RecyclerListView listView;
//    @SuppressWarnings("FieldCanBeLocal")
//    private LinearLayoutManager layoutManager;
//    private ThemesHorizontalListCell themesHorizontalListCell;
//
//    private ArrayList<ThemeInfo> darkThemes = new ArrayList<>();
//    private ArrayList<ThemeInfo> defaultThemes = new ArrayList<>();
//    private int currentType;
//
//    private ThemeInfo sharingTheme;
//    private ThemeAccent sharingAccent;
//    private AlertDialog sharingProgressDialog;
//    private ActionBarMenuItem menuItem;
//
//    boolean hasThemeAccents;
//
//    private int backgroundRow;
//    private int textSizeHeaderRow;
//    private int textSizeRow;
//    private int settingsRow;
//    private int customTabsRow;
//    private int directShareRow;
//    private int raiseToSpeakRow;
//    private int sendByEnterRow;
//    private int saveToGalleryRow;
//    private int distanceRow;
//    private int enableAnimationsRow;
//    private int settings2Row;
//    private int stickersRow;
//    private int stickersSection2Row;
//
//    private int emojiRow;
//    private int contactsReimportRow;
//    private int contactsSortRow;
//
//    private int nightThemeRow;
//    private int nightDisabledRow;
//    private int nightScheduledRow;
//    private int nightAutomaticRow;
//    private int nightSystemDefaultRow;
//    private int nightTypeInfoRow;
//    private int scheduleHeaderRow;
//    private int scheduleLocationRow;
//    private int scheduleUpdateLocationRow;
//    private int scheduleLocationInfoRow;
//    private int scheduleFromRow;
//    private int scheduleToRow;
//    private int scheduleFromToInfoRow;
//    private int automaticHeaderRow;
//    private int automaticBrightnessRow;
//    private int automaticBrightnessInfoRow;
//    private int preferedHeaderRow;
//    private int newThemeInfoRow;
//    private int themeHeaderRow;
//    private int bubbleRadiusHeaderRow;
//    private int bubbleRadiusRow;
//    private int bubbleRadiusInfoRow;
//    private int chatListHeaderRow;
//    private int chatListRow;
//    private int chatListInfoRow;
//    private int themeListRow;
//    private int themeAccentListRow;
//    private int themeInfoRow;
//
//    private int rowCount;
//
//    private boolean updatingLocation;
//
//    private int previousUpdatedType;
//    private boolean previousByLocation;
//
//    private final static int create_theme = 1;
//    private final static int share_theme = 2;
//    private final static int edit_theme = 3;
//    private final static int reset_settings = 4;
//
//    public ThemePage2(int type) {
//        super();
//        currentType = type;
//        updateRows(true);
//    }
//
//    private boolean setBubbleRadius(int size, boolean layout) {
//        if (size != SharedConfig.bubbleRadius) {
//            SharedConfig.bubbleRadius = size;
//            SharedPreferences preferences = AndroidUtilities.getGlobalMainSettings();
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putInt("bubbleRadius", SharedConfig.bubbleRadius);
//            editor.commit();
//
//            RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(textSizeRow);
//            if (holder != null && holder.itemView instanceof TextSizeCell) {
//                TextSizeCell cell = (TextSizeCell) holder.itemView;
//                ChatMessageCell[] cells = cell.messagesCell.getCells();
//                for (int a = 0; a < cells.length; a++) {
//                    cells[a].getMessageObject().resetLayout();
//                    cells[a].requestLayout();
//                }
//                cell.invalidate();
//            }
//
//            holder = listView.findViewHolderForAdapterPosition(bubbleRadiusRow);
//            if (holder != null && holder.itemView instanceof BubbleRadiusCell) {
//                BubbleRadiusCell cell = (BubbleRadiusCell) holder.itemView;
//                if (layout) {
//                    cell.requestLayout();
//                } else {
//                    cell.invalidate();
//                }
//            }
//
//            updateMenuItem();
//            return true;
//        }
//        return false;
//    }
//
//    private boolean setFontSize(int size) {
//        if (size != SharedConfig.fontSize) {
//            SharedConfig.fontSize = size;
//            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putInt("fons_size", SharedConfig.fontSize);
//            editor.commit();
//            Theme.chat_msgTextPaint.setTextSize(Space.dp(SharedConfig.fontSize));
//
//            RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(textSizeRow);
//            if (holder != null && holder.itemView instanceof TextSizeCell) {
//                TextSizeCell cell = (TextSizeCell) holder.itemView;
//                ChatMessageCell[] cells = cell.messagesCell.getCells();
//                for (int a = 0; a < cells.length; a++) {
//                    cells[a].getMessageObject().resetLayout();
//                    cells[a].requestLayout();
//                }
//            }
//            updateMenuItem();
//            return true;
//        }
//        return false;
//    }
//
//    private void updateRows(boolean notify) {
//        int oldRowCount = rowCount;
//
//        int prevThemeAccentListRow = themeAccentListRow;
//
//        rowCount = 0;
//        emojiRow = -1;
//        contactsReimportRow = -1;
//        contactsSortRow = -1;
//        scheduleLocationRow = -1;
//        scheduleUpdateLocationRow = -1;
//        scheduleLocationInfoRow = -1;
//        nightDisabledRow = -1;
//        nightScheduledRow = -1;
//        nightAutomaticRow = -1;
//        nightSystemDefaultRow = -1;
//        nightTypeInfoRow = -1;
//        scheduleHeaderRow = -1;
//        nightThemeRow = -1;
//        newThemeInfoRow = -1;
//        scheduleFromRow = -1;
//        scheduleToRow = -1;
//        scheduleFromToInfoRow = -1;
//        themeListRow = -1;
//        themeAccentListRow = -1;
//        themeInfoRow = -1;
//        preferedHeaderRow = -1;
//        automaticHeaderRow = -1;
//        automaticBrightnessRow = -1;
//        automaticBrightnessInfoRow = -1;
//        textSizeHeaderRow = -1;
//        themeHeaderRow = -1;
//        bubbleRadiusHeaderRow = -1;
//        bubbleRadiusRow = -1;
//        bubbleRadiusInfoRow = -1;
//        chatListHeaderRow = -1;
//        chatListRow = -1;
//        chatListInfoRow = -1;
//
//        textSizeRow = -1;
//        backgroundRow = -1;
//        settingsRow = -1;
//        customTabsRow = -1;
//        directShareRow = -1;
//        enableAnimationsRow = -1;
//        raiseToSpeakRow = -1;
//        sendByEnterRow = -1;
//        saveToGalleryRow = -1;
//        distanceRow = -1;
//        settings2Row = -1;
//        stickersRow = -1;
//        stickersSection2Row = -1;
//
//        defaultThemes.clear();
//        darkThemes.clear();
//        for (int a = 0, N = Theme.themes.size(); a < N; a++) {
//            ThemeInfo themeInfo = Theme.themes.get(a);
//            if (currentType != THEME_TYPE_BASIC) {
//                if (themeInfo.isLight() || themeInfo.info != null && themeInfo.info.document == null) {
//                    continue;
//                }
//            }
//            if (themeInfo.pathToFile != null) {
//                darkThemes.add(themeInfo);
//            } else {
//                defaultThemes.add(themeInfo);
//            }
//        }
//        Collections.sort(defaultThemes, (o1, o2) -> Integer.compare(o1.sortIndex, o2.sortIndex));
//
//        if (currentType == THEME_TYPE_BASIC) {
//            textSizeHeaderRow = rowCount++;
//            textSizeRow = rowCount++;
//            backgroundRow = rowCount++;
//            newThemeInfoRow = rowCount++;
//            themeHeaderRow = rowCount++;
//            themeListRow = rowCount++;
//            hasThemeAccents = ThemeManager.getCurrentTheme().hasAccentColors();
//            if (themesHorizontalListCell != null) {
//                themesHorizontalListCell.setDrawDivider(hasThemeAccents);
//            }
//            if (hasThemeAccents) {
//                themeAccentListRow = rowCount++;
//            }
//            themeInfoRow = rowCount++;
//
//            bubbleRadiusHeaderRow = rowCount++;
//            bubbleRadiusRow = rowCount++;
//            bubbleRadiusInfoRow = rowCount++;
//
//            chatListHeaderRow = rowCount++;
//            chatListRow = rowCount++;
//            chatListInfoRow = rowCount++;
//
//            settingsRow = rowCount++;
//            nightThemeRow = rowCount++;
//            customTabsRow = rowCount++;
//            directShareRow = rowCount++;
//            enableAnimationsRow = rowCount++;
//            emojiRow = rowCount++;
//            raiseToSpeakRow = rowCount++;
//            sendByEnterRow = rowCount++;
//            saveToGalleryRow = rowCount++;
//            distanceRow = rowCount++;
//            settings2Row = rowCount++;
//            stickersRow = rowCount++;
//            stickersSection2Row = rowCount++;
//        } else {
//            nightDisabledRow = rowCount++;
//            nightScheduledRow = rowCount++;
//            nightAutomaticRow = rowCount++;
//            if (Build.VERSION.SDK_INT >= 29) {
//                nightSystemDefaultRow = rowCount++;
//            }
//            nightTypeInfoRow = rowCount++;
//            if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SCHEDULED) {
//                scheduleHeaderRow = rowCount++;
//                scheduleLocationRow = rowCount++;
//                if (Theme.autoNightScheduleByLocation) {
//                    scheduleUpdateLocationRow = rowCount++;
//                    scheduleLocationInfoRow = rowCount++;
//                } else {
//                    scheduleFromRow = rowCount++;
//                    scheduleToRow = rowCount++;
//                    scheduleFromToInfoRow = rowCount++;
//                }
//            } else if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_AUTOMATIC) {
//                automaticHeaderRow = rowCount++;
//                automaticBrightnessRow = rowCount++;
//                automaticBrightnessInfoRow = rowCount++;
//            }
//            if (Theme.selectedAutoNightType != Theme.AUTO_NIGHT_TYPE_NONE) {
//                preferedHeaderRow = rowCount++;
//                themeListRow = rowCount++;
//                hasThemeAccents = ThemeManager.getCurrentNightTheme().hasAccentColors();
//                if (themesHorizontalListCell != null) {
//                    themesHorizontalListCell.setDrawDivider(hasThemeAccents);
//                }
//                if (hasThemeAccents) {
//                    themeAccentListRow = rowCount++;
//                }
//                themeInfoRow = rowCount++;
//            }
//        }
//
//        if (themesHorizontalListCell != null) {
//            themesHorizontalListCell.notifyDataSetChanged(listView.getWidth());
//        }
//
//        if (listAdapter != null) {
//            if (currentType != THEME_TYPE_NIGHT || previousUpdatedType == Theme.selectedAutoNightType || previousUpdatedType == -1) {
//                if (notify || previousUpdatedType == -1) {
//                    listAdapter.notifyDataSetChanged();
//                } else {
//                    if (prevThemeAccentListRow == -1 && themeAccentListRow != -1) {
//                        listAdapter.notifyItemInserted(themeAccentListRow);
//                    } else if (prevThemeAccentListRow != -1 && themeAccentListRow == -1) {
//                        listAdapter.notifyItemRemoved(prevThemeAccentListRow);
//                    } else if (themeAccentListRow != -1) {
//                        listAdapter.notifyItemChanged(themeAccentListRow);
//                    }
//                }
//            } else {
//                int start = nightTypeInfoRow + 1;
//                if (previousUpdatedType != Theme.selectedAutoNightType) {
//                    for (int a = 0; a < 4; a++) {
//                        RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForAdapterPosition(a);
//                        if (holder == null || !(holder.itemView instanceof ThemeTypeCell)) {
//                            continue;
//                        }
//                        ((ThemeTypeCell) holder.itemView).setTypeChecked(a == Theme.selectedAutoNightType);
//                    }
//
//                    if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_NONE) {
//                        listAdapter.notifyItemRangeRemoved(start, oldRowCount - start);
//                    } else if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SCHEDULED) {
//                        if (previousUpdatedType == Theme.AUTO_NIGHT_TYPE_NONE) {
//                            listAdapter.notifyItemRangeInserted(start, rowCount - start);
//                        } else if (previousUpdatedType == Theme.AUTO_NIGHT_TYPE_AUTOMATIC) {
//                            listAdapter.notifyItemRangeRemoved(start, 3);
//                            listAdapter.notifyItemRangeInserted(start, Theme.autoNightScheduleByLocation ? 4 : 5);
//                        } else if (previousUpdatedType == Theme.AUTO_NIGHT_TYPE_SYSTEM) {
//                            listAdapter.notifyItemRangeInserted(start, Theme.autoNightScheduleByLocation ? 4 : 5);
//                        }
//                    } else if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_AUTOMATIC) {
//                        if (previousUpdatedType == Theme.AUTO_NIGHT_TYPE_NONE) {
//                            listAdapter.notifyItemRangeInserted(start, rowCount - start);
//                        } else if (previousUpdatedType == Theme.AUTO_NIGHT_TYPE_SCHEDULED) {
//                            listAdapter.notifyItemRangeRemoved(start, Theme.autoNightScheduleByLocation ? 4 : 5);
//                            listAdapter.notifyItemRangeInserted(start, 3);
//                        } else if (previousUpdatedType == Theme.AUTO_NIGHT_TYPE_SYSTEM) {
//                            listAdapter.notifyItemRangeInserted(start, 3);
//                        }
//                    } else if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SYSTEM) {
//                        if (previousUpdatedType == Theme.AUTO_NIGHT_TYPE_NONE) {
//                            listAdapter.notifyItemRangeInserted(start, rowCount - start);
//                        } else if (previousUpdatedType == Theme.AUTO_NIGHT_TYPE_AUTOMATIC) {
//                            listAdapter.notifyItemRangeRemoved(start, 3);
//                        } else if (previousUpdatedType == Theme.AUTO_NIGHT_TYPE_SCHEDULED) {
//                            listAdapter.notifyItemRangeRemoved(start, Theme.autoNightScheduleByLocation ? 4 : 5);
//                        }
//                    }
//                } else {
//                    if (previousByLocation != Theme.autoNightScheduleByLocation) {
//                        listAdapter.notifyItemRangeRemoved(start + 2, Theme.autoNightScheduleByLocation ? 3 : 2);
//                        listAdapter.notifyItemRangeInserted(start + 2, Theme.autoNightScheduleByLocation ? 2 : 3);
//                    }
//                }
//            }
//        }
//        if (currentType == THEME_TYPE_NIGHT) {
//            previousByLocation = Theme.autoNightScheduleByLocation;
//            previousUpdatedType = Theme.selectedAutoNightType;
//        }
//        updateMenuItem();
//    }
//
//    @Override
//    public boolean onFragmentCreate() {
//        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
//        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
//        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeListUpdated);
//        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeAccentListUpdated);
//        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
//        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needShareTheme);
//        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
//        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadedToServer);
//        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadError);
//        if (currentType == THEME_TYPE_BASIC) {
//            Theme.loadRemoteThemes(currentAccount, true);
//            Theme.checkCurrentRemoteTheme(true);
//        }
//        return super.onFragmentCreate();
//    }
//
//    @Override
//    public void onFragmentDestroy() {
//        super.onFragmentDestroy();
//        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
//        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
//        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.themeListUpdated);
//        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.themeAccentListUpdated);
//        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
//        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needShareTheme);
//        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
//        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadedToServer);
//        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadError);
//        ThemeManager.saveAutoNightThemeConfig();
//    }
//
//    @Override
//    public void didReceivedNotification(int id, int account, Object... args) {
//        if (id == NotificationCenter.locationPermissionGranted) {
//            updateSunTime(null, true);
//        } else if (id == NotificationCenter.didSetNewWallpapper || id == NotificationCenter.emojiDidLoad) {
//            if (listView != null) {
//                listView.invalidateViews();
//            }
//        } else if (id == NotificationCenter.themeAccentListUpdated) {
//            if (listAdapter != null && themeAccentListRow != -1) {
//                listAdapter.notifyItemChanged(themeAccentListRow, new Object());
//            }
//        } else if (id == NotificationCenter.themeListUpdated) {
//            updateRows(true);
//        } else if (id == NotificationCenter.themeUploadedToServer) {
//            ThemeInfo themeInfo = (ThemeInfo) args[0];
//            ThemeAccent accent = (ThemeAccent) args[1];
//            if (themeInfo == sharingTheme && accent == sharingAccent) {
//                String link = "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/addtheme/" + (accent != null ? accent.info.slug : themeInfo.info.slug);
//                showDialog(new ShareAlert(getParentActivity(), null, link, false, link, false));
//                if (sharingProgressDialog != null) {
//                    sharingProgressDialog.dismiss();
//                }
//            }
//        } else if (id == NotificationCenter.themeUploadError) {
//            ThemeInfo themeInfo = (ThemeInfo) args[0];
//            ThemeAccent accent = (ThemeAccent) args[1];
//            if (themeInfo == sharingTheme && accent == sharingAccent && sharingProgressDialog == null) {
//                sharingProgressDialog.dismiss();
//            }
//        } else if (id == NotificationCenter.needShareTheme) {
//            if (getParentActivity() == null || isPaused) {
//                return;
//            }
//            sharingTheme = (ThemeInfo) args[0];
//            sharingAccent = (ThemeAccent) args[1];
//            sharingProgressDialog = new AlertDialog(getParentActivity(), 3);
//            sharingProgressDialog.setCanCacnel(true);
//            showDialog(sharingProgressDialog, dialog -> {
//                sharingProgressDialog = null;
//                sharingTheme = null;
//                sharingAccent = null;
//            });
//        } else if (id == NotificationCenter.needSetDayNightTheme) {
//            updateMenuItem();
//        }
//    }
//
//    @Override
//    public View createView(Context context) {
//        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
//        actionBar.setAllowOverlayTitle(false);
//        if (AndroidUtilities.isTablet()) {
//            actionBar.setOccupyStatusBar(false);
//        }
//        if (currentType == THEME_TYPE_BASIC) {
//            actionBar.setTitle(MyLang.getString("ChatSettings", R.string.ChatSettings));
//            ActionBarMenu menu = actionBar.createMenu();
//            menuItem = menu.addItem(0, R.drawable.ic_ab_other);
//            menuItem.setContentDescription(MyLang.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
//            menuItem.addSubItem(share_theme, R.drawable.msg_share, MyLang.getString("ShareTheme", R.string.ShareTheme));
//            menuItem.addSubItem(edit_theme, R.drawable.msg_edit, MyLang.getString("EditThemeColors", R.string.EditThemeColors));
//            menuItem.addSubItem(create_theme, R.drawable.menu_palette, MyLang.getString("CreateNewThemeMenu", R.string.CreateNewThemeMenu));
//            menuItem.addSubItem(reset_settings, R.drawable.msg_reset, MyLang.getString("ThemeResetToDefaults", R.string.ThemeResetToDefaults));
//        } else {
//            actionBar.setTitle(MyLang.getString("AutoNightTheme", R.string.AutoNightTheme));
//        }
//
//        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
//            @Override
//            public void onItemClick(int id) {
//                if (id == -1) {
//                    finishFragment();
//                } else if (id == create_theme) {
//                    if (getParentActivity() == null) {
//                        return;
//                    }
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
//                    builder.setTitle(MyLang.getString("NewTheme", R.string.NewTheme));
//                    builder.setMessage(MyLang.getString("CreateNewThemeAlert", R.string.CreateNewThemeAlert));
//                    builder.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
//                    builder.setPositiveButton(MyLang.getString("CreateTheme", R.string.CreateTheme), (dialog, which) -> AlertsCreator.createThemeCreateDialog(ThemeActivity.this, 0, null, null));
//                    showDialog(builder.create());
//                } else if (id == share_theme) {
//                    ThemeInfo currentTheme = ThemeManager.getCurrentTheme();
//                    ThemeAccent accent = currentTheme.getAccent(false);
//                    if (accent.info == null) {
//                        //                        MessagesController.getInstance(currentAccount).saveThemeToServer(accent.parentTheme, accent);
//                        NotificationCenter.postNotificationName(NotificationCenter.needShareTheme, accent.parentTheme, accent);
//                    } else {
//                        String link = "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/addtheme/" + accent.info.slug;
//                        showDialog(new ShareAlert(getParentActivity(), null, link, false, link, false));
//                    }
//                } else if (id == edit_theme) {
//                    ThemeInfo currentTheme = ThemeManager.getCurrentTheme();
//                    ThemeAccent accent = currentTheme.getAccent(false);
//                    presentFragment(new ThemePreviewActivity(currentTheme, false, ThemePreviewActivity.SCREEN_TYPE_ACCENT_COLOR, accent.id >= 100, currentType == THEME_TYPE_NIGHT));
//                } else if (id == reset_settings) {
//                    if (getParentActivity() == null) {
//                        return;
//                    }
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getParentActivity());
//                    builder1.setTitle(MyLang.getString("ThemeResetToDefaultsTitle", R.string.ThemeResetToDefaultsTitle));
//                    builder1.setMessage(MyLang.getString("ThemeResetToDefaultsText", R.string.ThemeResetToDefaultsText));
//                    builder1.setPositiveButton(MyLang.getString("Reset", R.string.Reset), (dialogInterface, i) -> {
//                        boolean changed = false;
//                        if (setFontSize(AndroidUtilities.isTablet() ? 18 : 16)) {
//                            changed = true;
//                        }
//                        if (setBubbleRadius(10, true)) {
//                            changed = true;
//                        }
//                        if (changed) {
//                            listAdapter.notifyItemChanged(textSizeRow, new Object());
//                            listAdapter.notifyItemChanged(bubbleRadiusRow, new Object());
//                        }
//                        if (themesHorizontalListCell != null) {
//                            ThemeInfo themeInfo = Theme.getTheme("Blue");
//                            ThemeInfo currentTheme = Theme.getCurrentTheme();
//                            if (themeInfo != currentTheme) {
//                                themeInfo.setCurrentAccentId(Theme.DEFALT_THEME_ACCENT_ID);
//                                Theme.saveThemeAccents(themeInfo, true, false, true, false);
//                                themesHorizontalListCell.selectTheme(themeInfo);
//                                themesHorizontalListCell.smoothScrollToPosition(0);
//                            } else if (themeInfo.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
//                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme, currentType == THEME_TYPE_NIGHT, null, Theme.DEFALT_THEME_ACCENT_ID);
//                                listAdapter.notifyItemChanged(themeAccentListRow);
//                            }
//                        }
//                    });
//                    builder1.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
//                    AlertDialog alertDialog = builder1.create();
//                    showDialog(alertDialog);
//                    TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//                    if (button != null) {
//                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
//                    }
//                }
//            }
//        });
//
//        listAdapter = new ListAdapter(context);
//
//        FrameLayout frameLayout = new FrameLayout(context);
//        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
//        fragmentView = frameLayout;
//
//        listView = new RecyclerListView(context);
//        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//        listView.setVerticalScrollBarEnabled(false);
//        listView.setAdapter(listAdapter);
//        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
//        listView.setOnItemClickListener((view, position, x, y) -> {
//            if (position == nightThemeRow) {
//                if (isRTL && x <= Space.dp(76) || !isRTL && x >= view.getMeasuredWidth() - Space.dp(76)) {
//                    NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
//                    if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_NONE) {
//                        Theme.selectedAutoNightType = Theme.AUTO_NIGHT_TYPE_AUTOMATIC;
//                        checkCell.setChecked(true);
//                    } else {
//                        Theme.selectedAutoNightType = Theme.AUTO_NIGHT_TYPE_NONE;
//                        checkCell.setChecked(false);
//                    }
//                    Theme.saveAutoNightThemeConfig();
//                    Theme.checkAutoNightThemeConditions(true);
//                    boolean enabled = Theme.selectedAutoNightType != Theme.AUTO_NIGHT_TYPE_NONE;
//                    String value = enabled ? Theme.getCurrentNightThemeName() : MyLang.getString("AutoNightThemeOff", R.string.AutoNightThemeOff);
//                    if (enabled) {
//                        String type;
//                        if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SCHEDULED) {
//                            type = MyLang.getString("AutoNightScheduled", R.string.AutoNightScheduled);
//                        } else if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SYSTEM) {
//                            type = MyLang.getString("AutoNightSystemDefault", R.string.AutoNightSystemDefault);
//                        } else {
//                            type = MyLang.getString("AutoNightAdaptive", R.string.AutoNightAdaptive);
//                        }
//                        value = type + " " + value;
//                    }
//                    checkCell.setTextAndValueAndCheck(MyLang.getString("AutoNightTheme", R.string.AutoNightTheme), value, enabled, true);
//                } else {
//                    presentFragment(new ThemeActivity(THEME_TYPE_NIGHT));
//                }
//            } else if (position == nightDisabledRow) {
//                if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_NONE) {
//                    return;
//                }
//                Theme.selectedAutoNightType = Theme.AUTO_NIGHT_TYPE_NONE;
//                updateRows(true);
//                ThemeManager.checkAutoNightThemeConditions(getParentActivity());
//            } else if (position == nightScheduledRow) {
//                if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SCHEDULED) {
//                    return;
//                }
//                Theme.selectedAutoNightType = Theme.AUTO_NIGHT_TYPE_SCHEDULED;
//                if (Theme.autoNightScheduleByLocation) {
//                    updateSunTime(null, true);
//                }
//                updateRows(true);
//                ThemeManager.checkAutoNightThemeConditions(getParentActivity());
//            } else if (position == nightAutomaticRow) {
//                if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_AUTOMATIC) {
//                    return;
//                }
//                Theme.selectedAutoNightType = Theme.AUTO_NIGHT_TYPE_AUTOMATIC;
//                updateRows(true);
//                ThemeManager.checkAutoNightThemeConditions(getParentActivity());
//            } else if (position == nightSystemDefaultRow) {
//                if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SYSTEM) {
//                    return;
//                }
//                Theme.selectedAutoNightType = Theme.AUTO_NIGHT_TYPE_SYSTEM;
//                updateRows(true);
//                ThemeManager.checkAutoNightThemeConditions(getParentActivity());
//            }
//        });
//
//        return fragmentView;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (listAdapter != null) {
//            updateRows(true);
//        }
//    }
//
//    @Override
//    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
//        if (isOpen) {
//            AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);
//            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), classGuid);
//        }
//    }
//
//    private void updateMenuItem() {
//        if (menuItem == null) {
//            return;
//        }
//        ThemeInfo themeInfo = ThemeManager.getCurrentTheme();
//        ThemeAccent accent = themeInfo.getAccent(false);
//        if (themeInfo.themeAccents != null && !themeInfo.themeAccents.isEmpty() && accent != null && accent.id >= 100) {
//            menuItem.showSubItem(share_theme);
//            menuItem.showSubItem(edit_theme);
//        } else {
//            menuItem.hideSubItem(share_theme);
//            menuItem.hideSubItem(edit_theme);
//        }
//        int fontSize = AndroidUtilities.isTablet() ? 18 : 16;
//        ThemeInfo currentTheme = ThemeManager.getCurrentTheme();
//        if (SharedConfig.fontSize != fontSize || SharedConfig.bubbleRadius != 10 || !currentTheme.firstAccentIsDefault || currentTheme.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
//            menuItem.showSubItem(reset_settings);
//        } else {
//            menuItem.hideSubItem(reset_settings);
//        }
//    }
//
//    private void updateSunTime(Location lastKnownLocation, boolean forceUpdate) {
//        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService(Context.LOCATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= 23) {
//            Activity activity = getParentActivity();
//            if (activity != null) {
//                if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
//                    return;
//                }
//            }
//        }
//        if (getParentActivity() != null) {
//            if (!getParentActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
//                return;
//            }
//            try {
//                LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService(Context.LOCATION_SERVICE);
//                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
//                    builder.setTitle(MyLang.getString("GpsDisabledAlertTitle", R.string.GpsDisabledAlertTitle));
//                    builder.setMessage(MyLang.getString("GpsDisabledAlertText", R.string.GpsDisabledAlertText));
//                    builder.setPositiveButton(MyLang.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), (dialog, id) -> {
//                        if (getParentActivity() == null) {
//                            return;
//                        }
//                        try {
//                            getParentActivity().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        } catch (Exception ignore) {
//
//                        }
//                    });
//                    builder.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
//                    showDialog(builder.create());
//                    return;
//                }
//            } catch (Exception e) {
//                FileLog.e(e);
//            }
//        }
//        try {
//            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (lastKnownLocation == null) {
//                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            }
//            if (lastKnownLocation == null) {
//                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//            }
//        } catch (Exception e) {
//            FileLog.e(e);
//        }
//        if (lastKnownLocation == null || forceUpdate) {
//            startLocationUpdate();
//            if (lastKnownLocation == null) {
//                return;
//            }
//        }
//        Theme.autoNightLocationLatitude = lastKnownLocation.getLatitude();
//        Theme.autoNightLocationLongitude = lastKnownLocation.getLongitude();
//        int[] time = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
//        Theme.autoNightSunriseTime = time[0];
//        Theme.autoNightSunsetTime = time[1];
//        Theme.autoNightCityName = null;
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        Theme.autoNightLastSunCheckDay = calendar.get(Calendar.DAY_OF_MONTH);
//        AndroidUtilities.globalQueue.postRunnable(() -> {
//            String name;
//            try {
//                Geocoder gcd = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault());
//                List<Address> addresses = gcd.getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
//                if (addresses.size() > 0) {
//                    name = addresses.get(0).getLocality();
//                } else {
//                    name = null;
//                }
//            } catch (Exception ignore) {
//                name = null;
//            }
//            final String nameFinal = name;
//            AndroidUtilities.runOnUIThread(() -> {
//                Theme.autoNightCityName = nameFinal;
//                if (Theme.autoNightCityName == null) {
//                    Theme.autoNightCityName = String.format("(%.06f, %.06f)", Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
//                }
//                ThemeManager.saveAutoNightThemeConfig();
//                if (listView != null) {
//                    RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForAdapterPosition(scheduleUpdateLocationRow);
//                    if (holder != null && holder.itemView instanceof TextSettingsCell) {
//                        ((TextSettingsCell) holder.itemView).setTextAndValue(MyLang.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
//                    }
//                }
//            });
//        });
//        RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForAdapterPosition(scheduleLocationInfoRow);
//        if (Theme.autoNightScheduleByLocation && Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SCHEDULED) {
//            Theme.checkAutoNightThemeConditions();
//        }
//    }
//
//    private void startLocationUpdate() {
//        if (updatingLocation) {
//            return;
//        }
//        updatingLocation = true;
//        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService(Context.LOCATION_SERVICE);
//        try {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, gpsLocationListener);
//        } catch (Exception e) {
//            FileLog.e(e);
//        }
//        try {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, networkLocationListener);
//        } catch (Exception e) {
//            FileLog.e(e);
//        }
//    }
//
//    private void stopLocationUpdate() {
//        updatingLocation = false;
//        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService(Context.LOCATION_SERVICE);
//        locationManager.removeUpdates(gpsLocationListener);
//        locationManager.removeUpdates(networkLocationListener);
//    }
//
//    private static class InnerAccentView extends View {
//
//        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        private ObjectAnimator checkAnimator;
//        private float checkedState;
//        private ThemeInfo currentTheme;
//        private ThemeAccent currentAccent;
//
//        InnerAccentView(Context context) {
//            super(context);
//        }
//
//        void setThemeAndColor(ThemeInfo themeInfo, ThemeAccent accent) {
//            currentTheme = themeInfo;
//            currentAccent = accent;
//            updateCheckedState(false);
//        }
//
//        void updateCheckedState(boolean animate) {
//            boolean checked = currentTheme.currentAccentId == currentAccent.id;
//
//            if (checkAnimator != null) {
//                checkAnimator.cancel();
//            }
//
//            if (animate) {
//                checkAnimator = ObjectAnimator.ofFloat(this, "checkedState", checked ? 1f : 0f);
//                checkAnimator.setDuration(200);
//                checkAnimator.start();
//            } else {
//                setCheckedState(checked ? 1f : 0f);
//            }
//        }
//
//        @Keep
//        public void setCheckedState(float state) {
//            checkedState = state;
//            invalidate();
//        }
//
//        @Keep
//        public float getCheckedState() {
//            return checkedState;
//        }
//
//        @Override
//        protected void onAttachedToWindow() {
//            super.onAttachedToWindow();
//            updateCheckedState(false);
//        }
//
//        @Override
//        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//            super.onMeasure(MeasureSpec.makeMeasureSpec(Space.dp(62), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Space.dp(62), MeasureSpec.EXACTLY));
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            float radius = Space.dp(20);
//
//            float cx = 0.5f * getMeasuredWidth();
//            float cy = 0.5f * getMeasuredHeight();
//
//            paint.setColor(currentAccent.accentColor);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeWidth(Space.dp(3));
//            paint.setAlpha(Math.round(255f * checkedState));
//            canvas.drawCircle(cx, cy, radius - 0.5f * paint.getStrokeWidth(), paint);
//
//            paint.setAlpha(255);
//            paint.setStyle(Paint.Style.FILL);
//            canvas.drawCircle(cx, cy, radius - Space.dp(5) * checkedState, paint);
//
//            if (checkedState != 0) {
//                paint.setColor(0xffffffff);
//                paint.setAlpha(Math.round(255f * checkedState));
//                canvas.drawCircle(cx, cy, Space.dp(2), paint);
//                canvas.drawCircle(cx - Space.dp(7) * checkedState, cy, Space.dp(2), paint);
//                canvas.drawCircle(cx + Space.dp(7) * checkedState, cy, Space.dp(2), paint);
//            }
//
//            if (currentAccent.myMessagesAccentColor != 0 && checkedState != 1) {
//                paint.setColor(currentAccent.myMessagesAccentColor);
//                canvas.drawCircle(cx, cy, Space.dp(8) * (1.0f - checkedState), paint);
//            }
//        }
//    }
//
//
//    private static class InnerCustomAccentView extends View {
//        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        private int[] colors = new int[7];
//
//        InnerCustomAccentView(Context context) {
//            super(context);
//        }
//
//        private void setTheme(ThemeInfo themeInfo) {
//            if (themeInfo.defaultAccentCount >= 8) {
//                colors = new int[]{themeInfo.getAccentColor(6), themeInfo.getAccentColor(4), themeInfo.getAccentColor(7), themeInfo.getAccentColor(2), themeInfo.getAccentColor(0), themeInfo.getAccentColor(5), themeInfo.getAccentColor(3)};
//            } else {
//                colors = new int[7];
//            }
//        }
//
//        @Override
//        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//            super.onMeasure(
//                    MeasureSpec.makeMeasureSpec(Space.dp(62), MeasureSpec.EXACTLY),
//                    MeasureSpec.makeMeasureSpec(Space.dp(62), MeasureSpec.EXACTLY)
//            );
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            float centerX = 0.5f * getMeasuredWidth();
//            float centerY = 0.5f * getMeasuredHeight();
//
//            float radSmall = Space.dp(5);
//            float radRing = Space.dp(20) - radSmall;
//
//            paint.setStyle(Paint.Style.FILL);
//
//            paint.setColor(colors[0]);
//            canvas.drawCircle(centerX, centerY, radSmall, paint);
//
//            double angle = 0.0;
//            for (int a = 0; a < 6; a++) {
//                float cx = centerX + radRing * (float) Math.sin(angle);
//                float cy = centerY - radRing * (float) Math.cos(angle);
//
//                paint.setColor(colors[a + 1]);
//                canvas.drawCircle(cx, cy, radSmall, paint);
//
//                angle += Math.PI / 3;
//            }
//        }
//    }
//
//    private class ThemeAccentsListAdapter extends RecyclerListView.SelectionAdapter {
//
//        private Context mContext;
//        private ThemeInfo currentTheme;
//        private ArrayList<ThemeAccent> themeAccents;
//
//        ThemeAccentsListAdapter(Context context) {
//            mContext = context;
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public void notifyDataSetChanged() {
//            currentTheme = currentType == THEME_TYPE_NIGHT ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
//            themeAccents = new ArrayList<>(currentTheme.themeAccents);
//            super.notifyDataSetChanged();
//        }
//
//        @Override
//        public boolean isEnabled(RecyclerView.ViewHolder holder) {
//            return false;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return position == getItemCount() - 1 ? 1 : 0;
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            switch (viewType) {
//                case 0: {
//                    return new RecyclerListView.Holder(new InnerAccentView(mContext));
//                }
//                case 1:
//                default: {
//                    return new RecyclerListView.Holder(new InnerCustomAccentView(mContext));
//                }
//            }
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            switch (getItemViewType(position)) {
//                case 0: {
//                    InnerAccentView view = (InnerAccentView) holder.itemView;
//                    view.setThemeAndColor(currentTheme, themeAccents.get(position));
//                    break;
//                }
//                case 1: {
//                    InnerCustomAccentView view = (InnerCustomAccentView) holder.itemView;
//                    view.setTheme(currentTheme);
//                    break;
//                }
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return themeAccents.isEmpty() ? 0 : themeAccents.size() + 1;
//        }
//
//        private int findCurrentAccent() {
//            return themeAccents.indexOf(currentTheme.getAccent(false));
//        }
//    }
//
//    private class ListAdapter extends RecyclerListView.SelectionAdapter {
//
//        private Context mContext;
//        private boolean first = true;
//
//        public ListAdapter(Context context) {
//            mContext = context;
//        }
//
//        @Override
//        public int getItemCount() {
//            return rowCount;
//        }
//
//        @Override
//        public boolean isEnabled(RecyclerView.ViewHolder holder) {
//            int type = holder.getItemViewType();
//            return type == 0 || type == 1 || type == 4 || type == 7 || type == 10 || type == 11 || type == 12;
//        }
//
//        private void showOptionsForTheme(ThemeInfo themeInfo) {
//            if (getParentActivity() == null || themeInfo.info != null && !themeInfo.themeLoaded || currentType == THEME_TYPE_NIGHT) {
//                return;
//            }
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
//            CharSequence[] items;
//            int[] icons;
//            boolean hasDelete;
//            if (themeInfo.pathToFile == null) {
//                hasDelete = false;
//                items = new CharSequence[]{
//                        null,
//                        MyLang.getString("ExportTheme", R.string.ExportTheme)
//                };
//                icons = new int[]{
//                        0,
//                        R.drawable.msg_shareout
//                };
//            } else {
//                hasDelete = themeInfo.info == null || !themeInfo.info.isDefault;
//                items = new CharSequence[]{
//                        MyLang.getString("ShareFile", R.string.ShareFile),
//                        MyLang.getString("ExportTheme", R.string.ExportTheme),
//                        themeInfo.info == null || !themeInfo.info.isDefault && themeInfo.info.creator ? MyLang.getString("Edit", R.string.Edit) : null,
//                        themeInfo.info != null && themeInfo.info.creator ? MyLang.getString("ThemeSetUrl", R.string.ThemeSetUrl) : null,
//                        hasDelete ? MyLang.getString("Delete", R.string.Delete) : null};
//                icons = new int[]{
//                        R.drawable.msg_share,
//                        R.drawable.msg_shareout,
//                        R.drawable.msg_edit,
//                        R.drawable.msg_link,
//                        R.drawable.msg_delete
//                };
//            }
//            builder.setItems(items, icons, (dialog, which) -> {
//                if (getParentActivity() == null) {
//                    return;
//                }
//                if (which == 0) {
//                    if (themeInfo.info == null) {
//                        //                        MessagesController.getInstance(themeInfo.account).saveThemeToServer(themeInfo, null);
//                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, themeInfo, null);
//                    } else {
//                        String link = "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/addtheme/" + themeInfo.info.slug;
//                        showDialog(new ShareAlert(getParentActivity(), null, link, false, link, false));
//                    }
//                } else if (which == 1) {
//                    File currentFile;
//                    if (themeInfo.pathToFile == null && themeInfo.assetName == null) {
//                        StringBuilder result = new StringBuilder();
//                        for (HashMap.Entry<String, Integer> entry : Theme.getDefaultColors().entrySet()) {
//                            result.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
//                        }
//                        currentFile = new File(ApplicationLoader.getFilesDirFixed(), "default_theme.attheme");
//                        FileOutputStream stream = null;
//                        try {
//                            stream = new FileOutputStream(currentFile);
//                            stream.write(AndroidUtilities.getStringBytes(result.toString()));
//                        } catch (Exception e) {
//                            FileLog.e(e);
//                        } finally {
//                            try {
//                                if (stream != null) {
//                                    stream.close();
//                                }
//                            } catch (Exception e) {
//                                FileLog.e(e);
//                            }
//                        }
//                    } else if (themeInfo.assetName != null) {
//                        currentFile = Theme.getAssetFile(themeInfo.assetName);
//                    } else {
//                        currentFile = new File(themeInfo.pathToFile);
//                    }
//                    String name = themeInfo.name;
//                    if (!name.endsWith(".attheme")) {
//                        name += ".attheme";
//                    }
//                    File finalFile = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE), FileLoader.fixFileName(name));
//                    try {
//                        if (!AndroidUtilities.copyFile(currentFile, finalFile)) {
//                            return;
//                        }
//                        Intent intent = new Intent(Intent.ACTION_SEND);
//                        intent.setType("text/xml");
//                        if (Build.VERSION.SDK_INT >= 24) {
//                            try {
//                                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getParentActivity(), BuildConfig.APPLICATION_ID + ".provider", finalFile));
//                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            } catch (Exception ignore) {
//                                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(finalFile));
//                            }
//                        } else {
//                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(finalFile));
//                        }
//                        startActivityForResult(Intent.createChooser(intent, MyLang.getString("ShareFile", R.string.ShareFile)), 500);
//                    } catch (Exception e) {
//                        FileLog.e(e);
//                    }
//                } else if (which == 2) {
//                    if (parentLayout != null) {
//                        Theme.applyTheme(themeInfo);
//                        parentLayout.rebuildAllFragmentViews(true, true);
//                        new ThemeEditorView().show(getParentActivity(), themeInfo);
//                    }
//                } else if (which == 3) {
//                    presentFragment(new ThemeSetUrlActivity(themeInfo, null, false));
//                } else {
//                    if (getParentActivity() == null) {
//                        return;
//                    }
//                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getParentActivity());
//                    builder1.setTitle(MyLang.getString("DeleteThemeTitle", R.string.DeleteThemeTitle));
//                    builder1.setMessage(MyLang.getString("DeleteThemeAlert", R.string.DeleteThemeAlert));
//                    builder1.setPositiveButton(MyLang.getString("Delete", R.string.Delete), (dialogInterface, i) -> {
//                        //                        MessagesController.getInstance(themeInfo.account).saveTheme(themeInfo, null, themeInfo == Theme.getCurrentNightTheme(), true);
//                        if (Theme.deleteTheme(themeInfo)) {
//                            parentLayout.rebuildAllFragmentViews(true, true);
//                        }
//                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated);
//                    });
//                    builder1.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
//                    AlertDialog alertDialog = builder1.create();
//                    showDialog(alertDialog);
//                    TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//                    if (button != null) {
//                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
//                    }
//                }
//            });
//            AlertDialog alertDialog = builder.create();
//            showDialog(alertDialog);
//            if (hasDelete) {
//                alertDialog.setItemColor(alertDialog.getItemsCount() - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
//            }
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view;
//            switch (viewType) {
//                case 1:
//                    view = new TextSettingsCell(mContext);
//                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    break;
//                case 2:
//                    view = new TextInfoPrivacyCell(mContext);
//                    view.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
//                    break;
//                case 3:
//                    view = new ShadowSectionCell(mContext);
//                    break;
//                case 4:
//                    view = new ThemeTypeCell(mContext);
//                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    break;
//                case 5:
//                    view = new HeaderCell(mContext);
//                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    break;
//                case 6:
//                    view = new BrightnessControlCell(mContext) {
//                        @Override
//                        protected void didChangedValue(float value) {
//                            int oldValue = (int) (Theme.autoNightBrighnessThreshold * 100);
//                            int newValue = (int) (value * 100);
//                            Theme.autoNightBrighnessThreshold = value;
//                            if (oldValue != newValue) {
//                                RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForAdapterPosition(automaticBrightnessInfoRow);
//                                if (holder != null) {
//                                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
//                                    cell.setText(LocaleController.formatString("AutoNightBrightnessInfo", R.string.AutoNightBrightnessInfo, (int) (100 * Theme.autoNightBrighnessThreshold)));
//                                }
//                                Theme.checkAutoNightThemeConditions(true);
//                            }
//                        }
//                    };
//                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    break;
//                case 7:
//                    view = new TextCheckCell(mContext);
//                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    break;
//                case 8:
//                    view = new TextSizeCell(mContext);
//                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    break;
//                case 9:
//                    view = new ChatListCell(mContext) {
//                        @Override
//                        protected void didSelectChatType(boolean threeLines) {
//                            SharedConfig.setUseThreeLinesLayout(threeLines);
//                        }
//                    };
//                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    break;
//                case 10:
//                    view = new NotificationsCheckCell(mContext, 21, 64, false);
//                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    break;
//                case 11:
//                    first = true;
//                    themesHorizontalListCell = new ThemesHorizontalListCell(mContext, currentType, defaultThemes, darkThemes) {
//                        @Override
//                        protected void showOptionsForTheme(ThemeInfo themeInfo) {
//                            listAdapter.showOptionsForTheme(themeInfo);
//                        }
//
//                        @Override
//                        protected void presentFragment(BaseFragment fragment) {
//                            ThemeActivity.this.presentFragment(fragment);
//                        }
//
//                        @Override
//                        protected void updateRows() {
//                            ThemeActivity.this.updateRows(false);
//                        }
//                    };
//                    themesHorizontalListCell.setDrawDivider(hasThemeAccents);
//                    themesHorizontalListCell.setFocusable(false);
//                    view = themesHorizontalListCell;
//                    view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Space.dp(148)));
//                    break;
//                case 12: {
//                    RecyclerListView accentsListView = new TintRecyclerListView(mContext) {
//                        @Override
//                        public boolean onInterceptTouchEvent(MotionEvent e) {
//                            if (getParent() != null && getParent().getParent() != null) {
//                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
//                            }
//                            return super.onInterceptTouchEvent(e);
//                        }
//                    };
//                    accentsListView.setFocusable(false);
//                    accentsListView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    accentsListView.setItemAnimator(null);
//                    accentsListView.setLayoutAnimation(null);
//                    accentsListView.setPadding(Space.dp(11), 0, Space.dp(11), 0);
//                    accentsListView.setClipToPadding(false);
//                    LinearLayoutManager accentsLayoutManager = new LinearLayoutManager(mContext);
//                    accentsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//                    accentsListView.setLayoutManager(accentsLayoutManager);
//                    ThemeAccentsListAdapter accentsAdapter = new ThemeAccentsListAdapter(mContext);
//                    accentsListView.setAdapter(accentsAdapter);
//                    accentsListView.setOnItemClickListener((view1, position) -> {
//                        ThemeInfo currentTheme = currentType == THEME_TYPE_NIGHT ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
//                        if (position == accentsAdapter.getItemCount() - 1) {
//                            presentFragment(new ThemePreviewActivity(currentTheme, false, ThemePreviewActivity.SCREEN_TYPE_ACCENT_COLOR, false, currentType == THEME_TYPE_NIGHT));
//                        } else {
//                            ThemeAccent accent = accentsAdapter.themeAccents.get(position);
//
//                            if (!TextUtils.isEmpty(accent.patternSlug) && accent.id != Theme.DEFALT_THEME_ACCENT_ID) {
//                                Theme.PatternsLoader.createLoader(false);
//                            }
//
//                            if (currentTheme.currentAccentId != accent.id) {
//                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme, currentType == THEME_TYPE_NIGHT, null, accent.id);
//                            } else {
//                                presentFragment(new ThemePreviewActivity(currentTheme, false, ThemePreviewActivity.SCREEN_TYPE_ACCENT_COLOR, accent.id >= 100, currentType == THEME_TYPE_NIGHT));
//                            }
//                        }
//
//                        int left = view1.getLeft();
//                        int right = view1.getRight();
//                        int extra = Space.dp(52);
//                        if (left - extra < 0) {
//                            accentsListView.smoothScrollBy(left - extra, 0);
//                        } else if (right + extra > accentsListView.getMeasuredWidth()) {
//                            accentsListView.smoothScrollBy(right + extra - accentsListView.getMeasuredWidth(), 0);
//                        }
//
//                        int count = accentsListView.getChildCount();
//                        for (int a = 0; a < count; a++) {
//                            View child = accentsListView.getChildAt(a);
//                            if (child instanceof InnerAccentView) {
//                                ((InnerAccentView) child).updateCheckedState(true);
//                            }
//                        }
//                    });
//                    accentsListView.setOnItemLongClickListener((view12, position) -> {
//                        if (position < 0 || position >= accentsAdapter.themeAccents.size()) {
//                            return false;
//                        }
//                        ThemeAccent accent = accentsAdapter.themeAccents.get(position);
//                        if (accent.id >= 100) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
//                            CharSequence[] items = new CharSequence[]{
//                                    MyLang.getString("OpenInEditor", R.string.OpenInEditor),
//                                    MyLang.getString("ShareTheme", R.string.ShareTheme),
//                                    accent.info != null && accent.info.creator ? MyLang.getString("ThemeSetUrl", R.string.ThemeSetUrl) : null,
//                                    MyLang.getString("DeleteTheme", R.string.DeleteTheme)
//                            };
//                            int[] icons = new int[]{
//                                    R.drawable.msg_edit,
//                                    R.drawable.msg_share,
//                                    R.drawable.msg_link,
//                                    R.drawable.msg_delete
//                            };
//                            builder.setItems(items, icons, (dialog, which) -> {
//                                if (getParentActivity() == null) {
//                                    return;
//                                }
//                                if (which == 0) {
//                                    AlertsCreator.createThemeCreateDialog(ThemeActivity.this, which == 1 ? 2 : 1, accent.parentTheme, accent);
//                                } else if (which == 1) {
//                                    if (accent.info == null) {
//                                        //                                        MessagesController.getInstance(currentAccount).saveThemeToServer(accent.parentTheme, accent);
//                                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, accent.parentTheme, accent);
//                                    } else {
//                                        String link = "https://" + MessagesController.getInstance(currentAccount).linkPrefix + "/addtheme/" + accent.info.slug;
//                                        showDialog(new ShareAlert(getParentActivity(), null, link, false, link, false));
//                                    }
//                                } else if (which == 2) {
//                                    presentFragment(new ThemeSetUrlActivity(accent.parentTheme, accent, false));
//                                } else if (which == 3) {
//                                    if (getParentActivity() == null) {
//                                        return;
//                                    }
//                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getParentActivity());
//                                    builder1.setTitle(MyLang.getString("DeleteThemeTitle", R.string.DeleteThemeTitle));
//                                    builder1.setMessage(MyLang.getString("DeleteThemeAlert", R.string.DeleteThemeAlert));
//                                    builder1.setPositiveButton(MyLang.getString("Delete", R.string.Delete), (dialogInterface, i) -> {
//                                        if (Theme.deleteThemeAccent(accentsAdapter.currentTheme, accent, true)) {
//                                            Theme.refreshThemeColors();
//                                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, Theme.getActiveTheme(), currentType == THEME_TYPE_NIGHT, null, -1);
//                                        }
//                                    });
//                                    builder1.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
//                                    AlertDialog alertDialog = builder1.create();
//                                    showDialog(alertDialog);
//                                    TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
//                                    if (button != null) {
//                                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
//                                    }
//                                }
//                            });
//                            AlertDialog alertDialog = builder.create();
//                            showDialog(alertDialog);
//                            alertDialog.setItemColor(alertDialog.getItemsCount() - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
//                            return true;
//                        }
//                        return false;
//                    });
//
//                    view = accentsListView;
//                    view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Space.dp(62)));
//                    break;
//                }
//                case 13:
//                default:
//                    view = new BubbleRadiusCell(mContext);
//                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//                    break;
//            }
//            return new RecyclerListView.Holder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            switch (holder.getItemViewType()) {
//                case 1: {
//                    TextSettingsCell cell = (TextSettingsCell) holder.itemView;
//                    if (position == nightThemeRow) {
//                        if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_NONE || Theme.getCurrentNightTheme() == null) {
//                            cell.setTextAndValue(MyLang.getString("AutoNightTheme", R.string.AutoNightTheme), MyLang.getString("AutoNightThemeOff", R.string.AutoNightThemeOff), false);
//                        } else {
//                            cell.setTextAndValue(MyLang.getString("AutoNightTheme", R.string.AutoNightTheme), Theme.getCurrentNightThemeName(), false);
//                        }
//                    } else if (position == scheduleFromRow) {
//                        int currentHour = Theme.autoNightDayStartTime / 60;
//                        int currentMinute = (Theme.autoNightDayStartTime - currentHour * 60);
//                        cell.setTextAndValue(MyLang.getString("AutoNightFrom", R.string.AutoNightFrom), String.format("%02d:%02d", currentHour, currentMinute), true);
//                    } else if (position == scheduleToRow) {
//                        int currentHour = Theme.autoNightDayEndTime / 60;
//                        int currentMinute = (Theme.autoNightDayEndTime - currentHour * 60);
//                        cell.setTextAndValue(MyLang.getString("AutoNightTo", R.string.AutoNightTo), String.format("%02d:%02d", currentHour, currentMinute), false);
//                    } else if (position == scheduleUpdateLocationRow) {
//                        cell.setTextAndValue(MyLang.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
//                    } else if (position == contactsSortRow) {
//                        String value;
//                        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
//                        int sort = preferences.getInt("sortContactsBy", 0);
//                        if (sort == 0) {
//                            value = MyLang.getString("Default", R.string.Default);
//                        } else if (sort == 1) {
//                            value = MyLang.getString("FirstName", R.string.SortFirstName);
//                        } else {
//                            value = MyLang.getString("LastName", R.string.SortLastName);
//                        }
//                        cell.setTextAndValue(MyLang.getString("SortBy", R.string.SortBy), value, true);
//                    } else if (position == backgroundRow) {
//                        cell.setText(MyLang.getString("ChangeChatBackground", R.string.ChangeChatBackground), false);
//                    } else if (position == contactsReimportRow) {
//                        cell.setText(MyLang.getString("ImportContacts", R.string.ImportContacts), true);
//                    } else if (position == stickersRow) {
//                        cell.setText(MyLang.getString("StickersAndMasks", R.string.StickersAndMasks), false);
//                    } else if (position == distanceRow) {
//                        String value;
//                        if (SharedConfig.distanceSystemType == 0) {
//                            value = MyLang.getString("DistanceUnitsAutomatic", R.string.DistanceUnitsAutomatic);
//                        } else if (SharedConfig.distanceSystemType == 1) {
//                            value = MyLang.getString("DistanceUnitsKilometers", R.string.DistanceUnitsKilometers);
//                        } else {
//                            value = MyLang.getString("DistanceUnitsMiles", R.string.DistanceUnitsMiles);
//                        }
//                        cell.setTextAndValue(MyLang.getString("DistanceUnits", R.string.DistanceUnits), value, false);
//                    }
//                    break;
//                }
//                case 3: {
//                    if (position == stickersSection2Row || position == nightTypeInfoRow && themeInfoRow == -1 || position == themeInfoRow && nightTypeInfoRow != -1) {
//                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
//                    } else {
//                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
//                    }
//                    break;
//                }
//                case 4: {
//                    ThemeTypeCell typeCell = (ThemeTypeCell) holder.itemView;
//                    if (position == nightDisabledRow) {
//                        typeCell.setValue(MyLang.getString("AutoNightDisabled", R.string.AutoNightDisabled), Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_NONE, true);
//                    } else if (position == nightScheduledRow) {
//                        typeCell.setValue(MyLang.getString("AutoNightScheduled", R.string.AutoNightScheduled), Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SCHEDULED, true);
//                    } else if (position == nightAutomaticRow) {
//                        typeCell.setValue(MyLang.getString("AutoNightAdaptive", R.string.AutoNightAdaptive), Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_AUTOMATIC, nightSystemDefaultRow != -1);
//                    } else if (position == nightSystemDefaultRow) {
//                        typeCell.setValue(MyLang.getString("AutoNightSystemDefault", R.string.AutoNightSystemDefault), Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SYSTEM, false);
//                    }
//                    break;
//                }
//                case 5: {
//                    HeaderCell headerCell = (HeaderCell) holder.itemView;
//                    if (position == scheduleHeaderRow) {
//                        headerCell.setText(MyLang.getString("AutoNightSchedule", R.string.AutoNightSchedule));
//                    } else if (position == automaticHeaderRow) {
//                        headerCell.setText(MyLang.getString("AutoNightBrightness", R.string.AutoNightBrightness));
//                    } else if (position == preferedHeaderRow) {
//                        headerCell.setText(MyLang.getString("AutoNightPreferred", R.string.AutoNightPreferred));
//                    } else if (position == settingsRow) {
//                        headerCell.setText(MyLang.getString("SETTINGS", R.string.SETTINGS));
//                    } else if (position == themeHeaderRow) {
//                        headerCell.setText(MyLang.getString("ColorTheme", R.string.ColorTheme));
//                    } else if (position == textSizeHeaderRow) {
//                        headerCell.setText(MyLang.getString("TextSizeHeader", R.string.TextSizeHeader));
//                    } else if (position == chatListHeaderRow) {
//                        headerCell.setText(MyLang.getString("ChatList", R.string.ChatList));
//                    } else if (position == bubbleRadiusHeaderRow) {
//                        headerCell.setText(MyLang.getString("BubbleRadius", R.string.BubbleRadius));
//                    }
//                    break;
//                }
//                case 11: {
//                    if (first) {
//                        themesHorizontalListCell.scrollToCurrentTheme(listView.getMeasuredWidth(), false);
//                        first = false;
//                    }
//                    break;
//                }
//                case 12: {
//                    RecyclerListView accentsList = (RecyclerListView) holder.itemView;
//                    ThemeAccentsListAdapter adapter = (ThemeAccentsListAdapter) accentsList.getAdapter();
//                    adapter.notifyDataSetChanged();
//                    int pos = adapter.findCurrentAccent();
//                    if (pos == -1) {
//                        pos = adapter.getItemCount() - 1;
//                    }
//                    if (pos != -1) {
//                        ((LinearLayoutManager) accentsList.getLayoutManager()).scrollToPositionWithOffset(pos, listView.getMeasuredWidth() / 2 - Space.dp(42));
//                    }
//                    break;
//                }
//            }
//        }
//
//        @Override
//        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
//            int type = holder.getItemViewType();
//            if (type == 4) {
//                ((ThemeTypeCell) holder.itemView).setTypeChecked(holder.getAdapterPosition() == Theme.selectedAutoNightType);
//            }
//            if (type != 2 && type != 3) {
//                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//            }
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            if (position == scheduleFromRow || position == distanceRow ||
//                    position == scheduleToRow || position == scheduleUpdateLocationRow || position == backgroundRow ||
//                    position == contactsReimportRow || position == contactsSortRow || position == stickersRow) {
//                return 1;
//            } else if (position == automaticBrightnessInfoRow || position == scheduleLocationInfoRow) {
//                return 2;
//            } else if (position == themeInfoRow || position == nightTypeInfoRow || position == scheduleFromToInfoRow ||
//                    position == stickersSection2Row || position == settings2Row || position == newThemeInfoRow ||
//                    position == chatListInfoRow || position == bubbleRadiusInfoRow) {
//                return 3;
//            } else if (position == nightDisabledRow || position == nightScheduledRow || position == nightAutomaticRow || position == nightSystemDefaultRow) {
//                return 4;
//            } else if (position == scheduleHeaderRow || position == automaticHeaderRow || position == preferedHeaderRow ||
//                    position == settingsRow || position == themeHeaderRow || position == textSizeHeaderRow ||
//                    position == chatListHeaderRow || position == bubbleRadiusHeaderRow) {
//                return 5;
//            } else if (position == automaticBrightnessRow) {
//                return 6;
//            } else if (position == scheduleLocationRow || position == enableAnimationsRow || position == sendByEnterRow ||
//                    position == saveToGalleryRow || position == raiseToSpeakRow || position == customTabsRow ||
//                    position == directShareRow || position == emojiRow) {
//                return 7;
//            } else if (position == textSizeRow) {
//                return 8;
//            } else if (position == chatListRow) {
//                return 9;
//            } else if (position == nightThemeRow) {
//                return 10;
//            } else if (position == themeListRow) {
//                return 11;
//            } else if (position == themeAccentListRow) {
//                return 12;
//            } else if (position == bubbleRadiusRow) {
//                return 13;
//            }
//            return 1;
//        }
//    }
//
//    private static abstract class TintRecyclerListView extends RecyclerListView {
//        TintRecyclerListView(Context context) {
//            super(context);
//        }
//    }
//
//    @Override
//    public ArrayList<ThemeDescription> getThemeDescriptions() {
//        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
//
//        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class,
//                                                                                                                    TextCheckCell.class,
//                                                                                                                    HeaderCell.class,
//                                                                                                                    BrightnessControlCell.class,
//                                                                                                                    ThemeTypeCell.class,
//                                                                                                                    TextSizeCell.class,
//                                                                                                                    BubbleRadiusCell.class,
//                                                                                                                    ChatListCell.class,
//                                                                                                                    NotificationsCheckCell.class,
//                                                                                                                    ThemesHorizontalListCell.class,
//                                                                                                                    TintRecyclerListView.class}, null, null, null, Theme.key_windowBackgroundWhite));
//        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
//
//        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
//        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
//        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
//        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
//        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
//        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
//        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
//        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
//
//        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
//
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
//
//        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
//
//        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
//
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));
//
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
//
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));
//
//        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon));
//        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, Theme.key_player_progressBackground));
//        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, Theme.key_player_progress));
//
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon));
//
//        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, null, null, null, Theme.key_player_progress));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, null, null, null, Theme.key_player_progressBackground));
//
//        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, null, null, null, Theme.key_player_progress));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, null, null, null, Theme.key_player_progressBackground));
//
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ChatListCell.class}, null, null, null, Theme.key_radioBackground));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{ChatListCell.class}, null, null, null, Theme.key_radioBackgroundChecked));
//
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, Theme.chat_msgInDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleShadow));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubbleShadow));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_messageTextIn));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_messageTextOut));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyLine));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyLine));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyNameText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyNameText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyMessageText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyMessageText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inTimeText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outTimeText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText));
//        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSizeCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText));
//
//        return themeDescriptions;
//    }
//}
