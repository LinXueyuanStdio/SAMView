package com.same.lib.theme;

import android.content.Context;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.same.lib.base.NotificationCenter;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.UiThread;

import static com.same.lib.theme.Theme.DEFALT_THEME_ACCENT_ID;
import static com.same.lib.theme.Theme.currentTheme;
import static com.same.lib.theme.Theme.previousTheme;
import static com.same.lib.theme.ThemeManager.saveOtherThemes;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/26
 * @description null
 * @usage null
 */
public class ThemeInfo {
    public String name;
    public String pathToFile;
    public String assetName;

    public int account;

    public Skin info;
    public boolean loaded = true;

    int previewBackgroundColor;
    int previewInColor;
    int previewOutColor;
    public boolean firstAccentIsDefault;
    public boolean previewParsed;
    public boolean themeLoaded = true;

    public int sortIndex;

    public int defaultAccentCount;

    public int accentBaseColor;

    public int currentAccentId;
    public int prevAccentId = -1;
    public SparseArray<ThemeAccent> themeAccentsMap;
    public ArrayList<ThemeAccent> themeAccents;
    public LongSparseArray<ThemeAccent> accentsByThemeId;
    public int lastAccentId = 100;

    String loadingThemeWallpaperName;
    String newPathToWallpaper;

    ThemeInfo() {

    }

    JSONObject getSaveJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("path", pathToFile);
            jsonObject.put("account", account);
            if (info != null) {
                jsonObject.put("info", info.toJson());
            }
            jsonObject.put("loaded", loaded);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCurrentAccentId(int id) {
        currentAccentId = id;
    }

    public void setPreviewInColor(int color) {
        previewInColor = color;
    }

    public void setPreviewOutColor(int color) {
        previewOutColor = color;
    }

    public void setPreviewBackgroundColor(int color) {
        previewBackgroundColor = color;
    }

    public int getPreviewInColor() {
        if (firstAccentIsDefault && currentAccentId == DEFALT_THEME_ACCENT_ID) {
            return 0xffffffff;
        }
        return previewInColor;
    }

    public int getPreviewOutColor() {
        if (firstAccentIsDefault && currentAccentId == DEFALT_THEME_ACCENT_ID) {
            return 0xfff0fee0;
        }
        return previewOutColor;
    }

    public int getPreviewBackgroundColor() {
        if (firstAccentIsDefault && currentAccentId == DEFALT_THEME_ACCENT_ID) {
            return 0xffcfd9e3;
        }
        return previewBackgroundColor;
    }

    boolean isDefaultMainAccent() {
        if (!firstAccentIsDefault) {
            return false;
        }
        if (currentAccentId == DEFALT_THEME_ACCENT_ID) {
            return true;
        }
        ThemeAccent defaultAccent = themeAccentsMap.get(DEFALT_THEME_ACCENT_ID);
        ThemeAccent accent = themeAccentsMap.get(currentAccentId);
        return accent != null && defaultAccent != null && defaultAccent.accentColor == accent.accentColor;
    }

    public boolean hasAccentColors() {
        return defaultAccentCount != 0;
    }

    public boolean isDark() {
        return "Dark Blue".equals(name) || "Night".equals(name);
    }

    public boolean isLight() {
        return pathToFile == null && !isDark();
    }

    public String getKey() {
        if (info != null) {
            return "remote" + info.id;
        }
        return name;
    }

    static ThemeInfo createWithJson(JSONObject object) {
        if (object == null) {
            return null;
        }
        try {
            ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.name = object.getString("name");
            themeInfo.pathToFile = object.getString("path");
            if (object.has("account")) {
                themeInfo.account = object.getInt("account");
            }
            if (object.has("info")) {
                try {
                    themeInfo.info = (Skin) Skin.fromJson(object.getString("info"));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            if (object.has("loaded")) {
                themeInfo.loaded = object.getBoolean("loaded");
            }
            return themeInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static ThemeInfo createWithString(String string) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        String[] args = string.split("\\|");
        if (args.length != 2) {
            return null;
        }
        ThemeInfo themeInfo = new ThemeInfo();
        themeInfo.name = args[0];
        themeInfo.pathToFile = args[1];
        return themeInfo;
    }

    void setAccentColorOptions(int[] options) {
        setAccentColorOptions(options, null, null, null, null, null, null, null, null);
    }

    void setAccentColorOptions(int[] accent, int[] myMessages, int[] myMessagesGradient, int[] background, int[] backgroundGradient, int[] ids, String[] patternSlugs, int[] patternRotations, int[] patternIntensities) {
        defaultAccentCount = accent.length;
        themeAccents = new ArrayList<>();
        themeAccentsMap = new SparseArray<>();
        accentsByThemeId = new LongSparseArray<>();
        for (int a = 0; a < accent.length; a++) {
            ThemeAccent themeAccent = new ThemeAccent();
            themeAccent.id = ids != null ? ids[a] : a;
            themeAccent.accentColor = accent[a];
            themeAccent.parentTheme = this;
            themeAccentsMap.put(themeAccent.id, themeAccent);
            themeAccents.add(themeAccent);
        }
        accentBaseColor = themeAccentsMap.get(0).accentColor;
    }

    @UiThread
    void loadThemeDocument() {
        loaded = false;
        loadingThemeWallpaperName = null;
        newPathToWallpaper = null;
        addObservers();
        //        FileLoader.getInstance(account).loadFile(info.document, info, 1, 1);TODO 文件加载
    }

    void addObservers() {
        //TODO
        //        NotificationCenter.getInstance(account).addObserver(this, NotificationCenter.fileDidLoad);
        //        NotificationCenter.getInstance(account).addObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    @UiThread
    void removeObservers() {
        //TODO
        //        NotificationCenter.getInstance(account).removeObserver(this, NotificationCenter.fileDidLoad);
        //        NotificationCenter.getInstance(account).removeObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    void onFinishLoadingRemoteTheme(Context context) {
        loaded = true;
        previewParsed = false;
        saveOtherThemes(context, true);
        if (this == currentTheme && previousTheme == null) {
            NotificationCenter.post(NotificationCenter.needSetDayNightTheme, this);
        }
    }

    public static boolean accentEquals(ThemeAccent accent, Skin.SkinSettings settings) {
        return settings.accent_color == accent.accentColor;
    }

    public static void fillAccentValues(ThemeAccent themeAccent, Skin.SkinSettings settings) {
        themeAccent.accentColor = settings.accent_color;
    }

    public ThemeAccent createNewAccent(Skin.SkinSettings settings) {
        ThemeAccent themeAccent = new ThemeAccent();
        fillAccentValues(themeAccent, settings);
        themeAccent.parentTheme = this;
        return themeAccent;
    }

    public ThemeAccent createNewAccent(Skin info, int account) {
        if (info == null) {
            return null;
        }
        ThemeAccent themeAccent = accentsByThemeId.get(info.id);
        if (themeAccent != null) {
            return themeAccent;
        }
        int id = ++lastAccentId;
        themeAccent = createNewAccent(info.settings);
        themeAccent.id = id;
        themeAccent.info = info;
        themeAccent.account = account;
        themeAccentsMap.put(id, themeAccent);
        themeAccents.add(0, themeAccent);
        accentsByThemeId.put(info.id, themeAccent);
        return themeAccent;
    }

    public ThemeAccent getAccent(boolean createNew) {
        if (themeAccents == null) {
            return null;
        }
        ThemeAccent accent = themeAccentsMap.get(currentAccentId);
        if (createNew) {
            int id = ++lastAccentId;
            ThemeAccent themeAccent = new ThemeAccent();
            themeAccent.accentColor = accent.accentColor;
            themeAccent.parentTheme = this;
            prevAccentId = currentAccentId;
            currentAccentId = themeAccent.id = id;
            themeAccentsMap.put(id, themeAccent);
            themeAccents.add(0, themeAccent);
            return themeAccent;
        } else {
            return accent;
        }
    }

    public int getAccentColor(int id) {
        ThemeAccent accent = themeAccentsMap.get(id);
        return accent != null ? accent.accentColor : 0;
    }

    //    @Override TODO 文件加载？
    //    public void didReceivedNotification(int id, int account, Object... args) {
    //        if (id == NotificationCenter.fileDidLoad || id == NotificationCenter.fileDidFailToLoad) {
    //            String location = (String) args[0];
    //            if (info != null && info.document != null) {
    //                if (location.equals(loadingThemeWallpaperName)) {
    //                    loadingThemeWallpaperName = null;
    //                    File file = (File) args[1];
    //                    AndroidUtilities.globalQueue.postRunnable(() -> {
    //                        createBackground(file, newPathToWallpaper);
    //                        AndroidUtilities.runOnUIThread(this::onFinishLoadingRemoteTheme);
    //                    });
    //                } else {
    //                    String name = AndroidUtilities.getAttachFileName(info.document);
    //                    if (location.equals(name)) {
    //                        removeObservers();
    //                        if (id == NotificationCenter.fileDidLoad) {
    //                            File locFile = new File(pathToFile);
    //                            ThemeInfo themeInfo = fillThemeValues(locFile, info.title, info);
    //                            if (themeInfo != null && themeInfo.pathToWallpaper != null) {
    //                                File file = new File(themeInfo.pathToWallpaper);
    //                                if (!file.exists()) {
    //                                    patternBgColor = themeInfo.patternBgColor;
    //                                    patternBgGradientColor = themeInfo.patternBgGradientColor;
    //                                    patternBgGradientRotation = themeInfo.patternBgGradientRotation;
    //                                    isBlured = themeInfo.isBlured;
    //                                    patternIntensity = themeInfo.patternIntensity;
    //                                    newPathToWallpaper = themeInfo.pathToWallpaper;
    //
    //                                    //TODO 发起请求
    //                                    //                                        TLRPC.TL_account_getWallPaper req = new TLRPC.TL_account_getWallPaper();
    //                                    //                                        TLRPC.TL_inputWallPaperSlug inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
    //                                    //                                        inputWallPaperSlug.slug = themeInfo.slug;
    //                                    //                                        req.wallpaper = inputWallPaperSlug;
    //                                    //                                        ConnectionsManager.getInstance(themeInfo.account).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
    //                                    //                                            if (response instanceof WallPaper) {
    //                                    //                                                WallPaper wallPaper = (WallPaper) response;
    //                                    //                                                loadingThemeWallpaperName = FileLoader.getAttachFileName(wallPaper.document);
    //                                    //                                                addObservers();
    //                                    //                                                FileLoader.getInstance(themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
    //                                    //                                            } else {
    //                                    //                                                onFinishLoadingRemoteTheme();
    //                                    //                                            }
    //                                    //                                        }));
    //                                    return;
    //                                }
    //                            }
    //                            onFinishLoadingRemoteTheme();
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //    }
}
