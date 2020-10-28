package com.same.lib.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;

import com.same.lib.R;
import com.same.lib.drawable.BackgroundGradientDrawable;
import com.same.lib.util.AndroidUtilities;
import com.same.lib.util.NotificationCenter;
import com.timecat.component.locale.MLang;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import androidx.annotation.UiThread;

import static com.same.lib.theme.Theme.DEFALT_THEME_ACCENT_ID;
import static com.same.lib.theme.Theme.DEFAULT_BACKGROUND_SLUG;
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
    public String pathToWallpaper;
    public String assetName;
    public String slug;
    public boolean badWallpaper;
    public boolean isBlured;
    public boolean isMotion;
    public int patternBgColor;
    public int patternBgGradientColor;
    public int patternBgGradientRotation = 45;
    public int patternIntensity;

    public int account;

    public Skin info;
    public boolean loaded = true;

    public String uploadingThumb;
    public String uploadingFile;
    //        public TLRPC.InputFile uploadedThumb;
    //        public TLRPC.InputFile uploadedFile;

    int previewBackgroundColor;
    public int previewBackgroundGradientColor;
    public int previewWallpaperOffset;
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

    public OverrideWallpaperInfo overrideWallpaper;

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

    void loadWallpapers(SharedPreferences sharedPreferences) {
        if (themeAccents != null && !themeAccents.isEmpty()) {
            for (int a = 0, N = themeAccents.size(); a < N; a++) {
                ThemeAccent accent = themeAccents.get(a);
                loadOverrideWallpaper(sharedPreferences, accent, name + "_" + accent.id + "_owp");
            }
        } else {
            loadOverrideWallpaper(sharedPreferences, null, name + "_owp");
        }
    }

    void loadOverrideWallpaper(SharedPreferences sharedPreferences, ThemeAccent accent, String key) {
        try {
            String json = sharedPreferences.getString(key,  null);
            if (TextUtils.isEmpty(json)) {
                return;
            }
            JSONObject object = new JSONObject(json);
            OverrideWallpaperInfo wallpaperInfo = new OverrideWallpaperInfo();
            wallpaperInfo.fileName = object.getString("wall");
            wallpaperInfo.originalFileName = object.getString("owall");
            wallpaperInfo.color = object.getInt("pColor");
            wallpaperInfo.gradientColor = object.getInt("pGrColor");
            wallpaperInfo.rotation = object.getInt("pGrAngle");
            wallpaperInfo.slug = object.getString("wallSlug");
            wallpaperInfo.isBlurred = object.getBoolean("wBlur");
            wallpaperInfo.isMotion = object.getBoolean("wMotion");
            wallpaperInfo.intensity = (float) object.getDouble("pIntensity");
            wallpaperInfo.parentTheme = this;
            wallpaperInfo.parentAccent = accent;
            if (accent != null) {
                accent.overrideWallpaper = wallpaperInfo;
            } else {
                overrideWallpaper = wallpaperInfo;
            }
            if (object.has("wallId")) {
                long id = object.getLong("wallId");
                if (id == 1000001) {
                    wallpaperInfo.slug = DEFAULT_BACKGROUND_SLUG;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setOverrideWallpaper(OverrideWallpaperInfo info) {
        if (overrideWallpaper == info) {
            return;
        }
        ThemeAccent accent = getAccent(false);
        if (overrideWallpaper != null) {
            overrideWallpaper.delete();
        }
        if (info != null) {
            info.parentAccent = accent;
            info.parentTheme = this;
            info.save();
        }
        overrideWallpaper = info;
        if (accent != null) {
            accent.overrideWallpaper = info;
        }
    }

    public String getName(Context context) {
        if ("Blue".equals(name)) {
            return MLang.getString(context, "ThemeClassic", R.string.ThemeClassic);
        } else if ("Dark Blue".equals(name)) {
            return MLang.getString(context,"ThemeDark", R.string.ThemeDark);
        } else if ("Arctic Blue".equals(name)) {
            return MLang.getString(context,"ThemeArcticBlue", R.string.ThemeArcticBlue);
        } else if ("Day".equals(name)) {
            return MLang.getString(context,"ThemeDay", R.string.ThemeDay);
        } else if ("Night".equals(name)) {
            return MLang.getString(context,"ThemeNight", R.string.ThemeNight);
        }
        return info != null ? info.title : name;
    }

    public void setCurrentAccentId(int id) {
        currentAccentId = id;
        ThemeAccent accent = getAccent(false);
        if (accent != null) {
            overrideWallpaper = accent.overrideWallpaper;
        }
    }

    public String generateWallpaperName(ThemeAccent accent, boolean original) {
        if (accent == null) {
            accent = getAccent(false);
        }
        if (accent != null) {
            return (original ? name + "_" + accent.id + "_wp_o" : name + "_" + accent.id + "_wp") + AndroidUtilities.random.nextInt() + ".jpg";
        } else {
            return (original ? name + "_wp_o" : name + "_wp") + AndroidUtilities.random.nextInt() + ".jpg";
        }
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

    boolean isDefaultMyMessages() {
        if (!firstAccentIsDefault) {
            return false;
        }
        if (currentAccentId == DEFALT_THEME_ACCENT_ID) {
            return true;
        }
        ThemeAccent defaultAccent = themeAccentsMap.get(DEFALT_THEME_ACCENT_ID);
        ThemeAccent accent = themeAccentsMap.get(currentAccentId);
        if (defaultAccent == null || accent == null) {
            return false;
        }
        return defaultAccent.myMessagesAccentColor == accent.myMessagesAccentColor && defaultAccent.myMessagesGradientAccentColor == accent.myMessagesGradientAccentColor;
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
            if (myMessages != null) {
                themeAccent.myMessagesAccentColor = myMessages[a];
            }
            if (myMessagesGradient != null) {
                themeAccent.myMessagesGradientAccentColor = myMessagesGradient[a];
            }
            if (background != null) {
                themeAccent.backgroundOverrideColor = background[a];
                if (firstAccentIsDefault && themeAccent.id == DEFALT_THEME_ACCENT_ID) {
                    themeAccent.backgroundOverrideColor = 0x100000000L;
                } else {
                    themeAccent.backgroundOverrideColor = background[a];
                }
            }
            if (backgroundGradient != null) {
                if (firstAccentIsDefault && themeAccent.id == DEFALT_THEME_ACCENT_ID) {
                    themeAccent.backgroundGradientOverrideColor = 0x100000000L;
                } else {
                    themeAccent.backgroundGradientOverrideColor = backgroundGradient[a];
                }
            }
            if (patternSlugs != null) {
                themeAccent.patternIntensity = patternIntensities[a] / 100.0f;
                themeAccent.backgroundRotation = patternRotations[a];
                themeAccent.patternSlug = patternSlugs[a];
            }
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

    void onFinishLoadingRemoteTheme() {
        loaded = true;
        previewParsed = false;
        saveOtherThemes(true);
        if (this == currentTheme && previousTheme == null) {
            NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, this);
        }
    }

    public static boolean accentEquals(ThemeAccent accent, Skin.SkinSettings settings) {
        int myMessagesGradientAccentColor = settings.message_top_color;
        if (settings.message_bottom_color == myMessagesGradientAccentColor) {
            myMessagesGradientAccentColor = 0;
        }
        int backgroundOverrideColor = 0;
        long backgroundGradientOverrideColor = 0;
        int backgroundRotation = 0;
        String patternSlug = null;
        float patternIntensity = 0;
        if (settings.wallpaper != null && settings.wallpaper.settings != null) {
            backgroundOverrideColor = settings.wallpaper.settings.background_color;
            if (settings.wallpaper.settings.second_background_color == 0) {
                backgroundGradientOverrideColor = 0x100000000L;
            } else {
                backgroundGradientOverrideColor = settings.wallpaper.settings.second_background_color;
            }
            backgroundRotation = AndroidUtilities.getWallpaperRotation(settings.wallpaper.settings.rotation, false);
            if (settings.wallpaper.pattern) {
                patternSlug = settings.wallpaper.slug;
                patternIntensity = settings.wallpaper.settings.intensity / 100.0f;
            }
        }
        return settings.accent_color == accent.accentColor &&
                settings.message_bottom_color == accent.myMessagesAccentColor &&
                myMessagesGradientAccentColor == accent.myMessagesGradientAccentColor &&
                backgroundOverrideColor == accent.backgroundOverrideColor &&
                backgroundGradientOverrideColor == accent.backgroundGradientOverrideColor &&
                backgroundRotation == accent.backgroundRotation &&
                TextUtils.equals(patternSlug, accent.patternSlug) &&
                Math.abs(patternIntensity - accent.patternIntensity) < 0.001;
    }

    public static void fillAccentValues(ThemeAccent themeAccent, Skin.SkinSettings settings) {
        themeAccent.accentColor = settings.accent_color;
        themeAccent.myMessagesAccentColor = settings.message_bottom_color;
        themeAccent.myMessagesGradientAccentColor = settings.message_top_color;
        if (themeAccent.myMessagesAccentColor == themeAccent.myMessagesGradientAccentColor) {
            themeAccent.myMessagesGradientAccentColor = 0;
        }
        if (settings.wallpaper != null && settings.wallpaper.settings != null) {
            themeAccent.backgroundOverrideColor = settings.wallpaper.settings.background_color;
            if (settings.wallpaper.settings.second_background_color == 0) {
                themeAccent.backgroundGradientOverrideColor = 0x100000000L;
            } else {
                themeAccent.backgroundGradientOverrideColor = settings.wallpaper.settings.second_background_color;
            }
            themeAccent.backgroundRotation = AndroidUtilities.getWallpaperRotation(settings.wallpaper.settings.rotation, false);
            if (settings.wallpaper.pattern) {
                themeAccent.patternSlug = settings.wallpaper.slug;
                themeAccent.patternIntensity = settings.wallpaper.settings.intensity / 100.0f;
                themeAccent.patternMotion = settings.wallpaper.settings.motion;
            }
        }
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
            themeAccent.myMessagesAccentColor = accent.myMessagesAccentColor;
            themeAccent.myMessagesGradientAccentColor = accent.myMessagesGradientAccentColor;
            themeAccent.backgroundOverrideColor = accent.backgroundOverrideColor;
            themeAccent.backgroundGradientOverrideColor = accent.backgroundGradientOverrideColor;
            themeAccent.backgroundRotation = accent.backgroundRotation;
            themeAccent.patternSlug = accent.patternSlug;
            themeAccent.patternIntensity = accent.patternIntensity;
            themeAccent.patternMotion = accent.patternMotion;
            themeAccent.parentTheme = this;
            if (overrideWallpaper != null) {
                themeAccent.overrideWallpaper = new OverrideWallpaperInfo(overrideWallpaper, this, themeAccent);
            }
            prevAccentId = currentAccentId;
            currentAccentId = themeAccent.id = id;
            overrideWallpaper = themeAccent.overrideWallpaper;
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
    public static Bitmap getScaledBitmap(float w, float h, String path, String streamPath, int streamOffset) {
        FileInputStream stream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            if (path != null) {
                BitmapFactory.decodeFile(path, options);
            } else {
                stream = new FileInputStream(streamPath);
                stream.getChannel().position(streamOffset);
                BitmapFactory.decodeStream(stream, null, options);
            }
            if (options.outWidth > 0 && options.outHeight > 0) {
                if (w > h && options.outWidth < options.outHeight) {
                    float temp = w;
                    w = h;
                    h = temp;
                }
                float scale = Math.min(options.outWidth / w, options.outHeight / h);
                options.inSampleSize = 1;
                if (scale > 1.0f) {
                    do {
                        options.inSampleSize *= 2;
                    } while (options.inSampleSize < scale);
                }
                options.inJustDecodeBounds = false;
                Bitmap wallpaper;
                if (path != null) {
                    wallpaper = BitmapFactory.decodeFile(path, options);
                } else {
                    stream.getChannel().position(streamOffset);
                    wallpaper = BitmapFactory.decodeStream(stream, null, options);
                }
                return wallpaper;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public boolean createBackground(File file, String toPath) {
        try {
            Bitmap bitmap = getScaledBitmap(AndroidUtilities.dp(640), AndroidUtilities.dp(360), file.getAbsolutePath(), null, 0);
            if (bitmap != null && patternBgColor != 0) {
                Bitmap finalBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                Canvas canvas = new Canvas(finalBitmap);
                int patternColor;
                if (patternBgGradientColor != 0) {
                    patternColor = AndroidUtilities.getAverageColor(patternBgColor, patternBgGradientColor);
                    GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(patternBgGradientRotation), new int[]{patternBgColor, patternBgGradientColor});
                    gradientDrawable.setBounds(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());
                    gradientDrawable.draw(canvas);
                } else {
                    patternColor = AndroidUtilities.getPatternColor(patternBgColor);
                    canvas.drawColor(patternBgColor);
                }
                Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
                paint.setColorFilter(new PorterDuffColorFilter(patternColor, PorterDuff.Mode.SRC_IN));
                paint.setAlpha((int) (patternIntensity / 100.0f * 255));
                canvas.drawBitmap(bitmap, 0, 0, paint);
                bitmap = finalBitmap;
                canvas.setBitmap(null);
            }
            if (isBlured) {
                bitmap = AndroidUtilities.blurWallpaper(bitmap);
            }
            FileOutputStream stream = new FileOutputStream(toPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
            stream.close();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
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
