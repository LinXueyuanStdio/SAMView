package com.same.lib.theme;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.same.lib.drawable.BackgroundGradientDrawable;
import com.same.lib.util.AndroidUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.same.lib.theme.Theme.DEFALT_THEME_ACCENT_ID;
import static com.same.lib.theme.Theme.currentTheme;
import static com.same.lib.theme.Theme.key_chat_wallpaper;
import static com.same.lib.theme.Theme.key_chat_wallpaper_gradient_to;
import static com.same.lib.theme.Theme.loadScreenSizedBitmap;
import static com.same.lib.theme.Theme.reloadWallpaper;
import static com.same.lib.theme.Theme.themesDict;
import static com.same.lib.theme.ThemeManager.changeColorAccent;
import static com.same.lib.theme.ThemeManager.getThemeFileValues;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/26
 * @description null
 * @usage null
 */
public class PatternsLoader {

    static class LoadingPattern {
        public WallPaper pattern;
        public ArrayList<ThemeAccent> accents = new ArrayList<>();
    }

    private int account = 1;
    private HashMap<String, PatternsLoader.LoadingPattern> watingForLoad;
    static PatternsLoader loader;

    public static void createLoader(boolean force) {
        if (loader != null && !force) {
            return;
        }
        ArrayList<ThemeAccent> accentsToLoad = null;
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
            for (int a = 0, N = info.themeAccents.size(); a < N; a++) {
                ThemeAccent accent = info.themeAccents.get(a);
                if (accent.id == DEFALT_THEME_ACCENT_ID || TextUtils.isEmpty(accent.patternSlug)) {
                    continue;
                }
                if (accentsToLoad == null) {
                    accentsToLoad = new ArrayList<>();
                }
                accentsToLoad.add(accent);
            }
        }
        loader = new PatternsLoader(accentsToLoad);
    }

    private PatternsLoader(ArrayList<ThemeAccent> accents) {
        if (accents == null) {
            return;
        }
        AndroidUtilities.globalQueue.postRunnable(() -> {
            ArrayList<String> slugs = null;
            for (int a = 0, N = accents.size(); a < N; a++) {
                ThemeAccent accent = accents.get(a);
                File wallpaper = accent.getPathToWallpaper();
                if (wallpaper != null && wallpaper.exists()) {
                    accents.remove(a);
                    a--;
                    N--;
                    continue;
                }
                if (slugs == null) {
                    slugs = new ArrayList<>();
                }
                if (slugs.contains(accent.patternSlug)) {
                    continue;
                }
                slugs.add(accent.patternSlug);
            }
            if (slugs == null) {
                return;
            }
            //TODO 发起请求
            //                TLRPC.TL_account_getMultiWallPapers req = new TLRPC.TL_account_getMultiWallPapers();
            //                for (int a = 0, N = slugs.size(); a < N; a++) {
            //                    TLRPC.TL_inputWallPaperSlug slug = new TLRPC.TL_inputWallPaperSlug();
            //                    slug.slug = slugs.get(a);
            //                    req.wallpapers.add(slug);
            //                }
            //                ConnectionsManager.getInstance(account).sendRequest(req, (response, error) -> {
            //                    if (response instanceof TLRPC.Vector) {
            //                        TLRPC.Vector res = (TLRPC.Vector) response;
            //                        ArrayList<ThemeAccent> createdAccents = null;
            //                        for (int b = 0, N2 = res.objects.size(); b < N2; b++) {
            //                            TLRPC.WallPaper object = (TLRPC.WallPaper) res.objects.get(b);
            //                            if (!(object instanceof WallPaper)) {
            //                                continue;
            //                            }
            //                            WallPaper wallPaper = (WallPaper) object;
            //                            if (wallPaper.pattern) {
            //                                File patternPath = FileLoader.getPathToAttach(wallPaper.document, true);
            //                                Boolean exists = null;
            //                                Bitmap patternBitmap = null;
            //                                for (int a = 0, N = accents.size(); a < N; a++) {
            //                                    ThemeAccent accent = accents.get(a);
            //                                    if (accent.patternSlug.equals(wallPaper.slug)) {
            //                                        if (exists == null) {
            //                                            exists = patternPath.exists();
            //                                        }
            //                                        if (patternBitmap != null || exists) {
            //                                            patternBitmap = createWallpaperForAccent(patternBitmap, "application/x-tgwallpattern".equals(wallPaper.document.mime_type), patternPath, accent);
            //                                            if (createdAccents == null) {
            //                                                createdAccents = new ArrayList<>();
            //                                            }
            //                                            createdAccents.add(accent);
            //                                        } else {
            //                                            String key = FileLoader.getAttachFileName(wallPaper.document);
            //                                            if (watingForLoad == null) {
            //                                                watingForLoad = new HashMap<>();
            //                                            }
            //                                            LoadingPattern loadingPattern = watingForLoad.get(key);
            //                                            if (loadingPattern == null) {
            //                                                loadingPattern = new LoadingPattern();
            //                                                loadingPattern.pattern = wallPaper;
            //                                                watingForLoad.put(key, loadingPattern);
            //                                            }
            //                                            loadingPattern.accents.add(accent);
            //                                        }
            //                                    }
            //                                }
            //                                if (patternBitmap != null) {
            //                                    patternBitmap.recycle();
            //                                }
            //                            }
            //                        }
            //                        checkCurrentWallpaper(createdAccents, true);
            //                    }
            //                });
        });
    }

    private void checkCurrentWallpaper(ArrayList<ThemeAccent> accents, boolean load) {
        AndroidUtilities.runOnUIThread(() -> checkCurrentWallpaperInternal(accents, load));
    }

    private void checkCurrentWallpaperInternal(ArrayList<ThemeAccent> accents, boolean load) {
        if (accents != null && currentTheme.themeAccents != null && !currentTheme.themeAccents.isEmpty()) {
            if (accents.contains(currentTheme.getAccent(false))) {
                reloadWallpaper();
            }
        }
        if (load) {
            if (watingForLoad != null) {
                //TODO 先注释，后面再填坑
//                NotificationCenter.getInstance(account).addObserver(this, NotificationCenter.fileDidLoad);
//                NotificationCenter.getInstance(account).addObserver(this, NotificationCenter.fileDidFailToLoad);
                for (HashMap.Entry<String, PatternsLoader.LoadingPattern> entry : watingForLoad.entrySet()) {
                    PatternsLoader.LoadingPattern loadingPattern = entry.getValue();
                    //TODO 先注释，后面再填坑
//                    FileLoader.getInstance(account).loadFile(ImageLocation.getForDocument(loadingPattern.pattern.document), "wallpaper", null, 0, 1);
                }
            }
        } else {
            if (watingForLoad == null || watingForLoad.isEmpty()) {
                //TODO 先注释，后面再填坑
//                NotificationCenter.getInstance(account).removeObserver(this, NotificationCenter.fileDidLoad);
//                NotificationCenter.getInstance(account).removeObserver(this, NotificationCenter.fileDidFailToLoad);
            }
        }
    }

    private Bitmap createWallpaperForAccent(Bitmap patternBitmap, boolean svg, File patternPath, ThemeAccent accent) {
        try {
            File toFile = accent.getPathToWallpaper();
            if (toFile == null) {
                return null;
            }
            ThemeInfo themeInfo = accent.parentTheme;
            HashMap<String, Integer> values = getThemeFileValues(null, themeInfo.assetName, null);

            int backgroundAccent = accent.accentColor;

            int backgroundColor = (int) accent.backgroundOverrideColor;
            int backgroundGradientColor = (int) accent.backgroundGradientOverrideColor;
            if (backgroundGradientColor == 0 && accent.backgroundGradientOverrideColor == 0) {
                if (backgroundColor != 0) {
                    backgroundAccent = backgroundColor;
                }
                Integer color = values.get(key_chat_wallpaper_gradient_to);
                if (color != null) {
                    backgroundGradientColor = changeColorAccent(themeInfo, backgroundAccent, color);
                }
            } else {
                backgroundAccent = 0;
            }

            if (backgroundColor == 0) {
                Integer color = values.get(key_chat_wallpaper);
                if (color != null) {
                    backgroundColor = changeColorAccent(themeInfo, backgroundAccent, color);
                }
            }

            Drawable background;
            int patternColor;
            if (backgroundGradientColor != 0) {
                BackgroundGradientDrawable.Orientation orientation = BackgroundGradientDrawable.getGradientOrientation(accent.backgroundRotation);
                background = new BackgroundGradientDrawable(orientation, new int[]{backgroundColor, backgroundGradientColor});
                patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(backgroundColor, backgroundGradientColor));
            } else {
                background = new ColorDrawable(backgroundColor);
                patternColor = AndroidUtilities.getPatternColor(backgroundColor);
            }

            if (patternBitmap == null) {
                //                    if (svg) {
                //                        patternBitmap = SvgHelper.getBitmap(patternPath, AndroidUtilities.dp(360), AndroidUtilities.dp(640), false);
                //                    } else {
                //                    }
                patternBitmap = loadScreenSizedBitmap(new FileInputStream(patternPath), 0);
            }

            Bitmap dst = Bitmap.createBitmap(patternBitmap.getWidth(), patternBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(dst);
            background.setBounds(0, 0, patternBitmap.getWidth(), patternBitmap.getHeight());
            background.draw(canvas);

            Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
            paint.setColorFilter(new PorterDuffColorFilter(patternColor, PorterDuff.Mode.SRC_IN));
            paint.setAlpha((int) (255 * accent.patternIntensity));
            canvas.drawBitmap(patternBitmap, 0, 0, paint);

            FileOutputStream stream = new FileOutputStream(toFile);
            dst.compress(Bitmap.CompressFormat.JPEG, 87, stream);
            stream.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return patternBitmap;
    }

//    @Override TODO 文件加载？
//    public void didReceivedNotification(int id, int account, Object... args) {
//        if (watingForLoad == null) {
//            return;
//        }
//        if (id == NotificationCenter.fileDidLoad) {
//            String location = (String) args[0];
//            PatternsLoader.LoadingPattern loadingPattern = watingForLoad.remove(location);
//            if (loadingPattern != null) {
//                AndroidUtilities.globalQueue.postRunnable(() -> {
//                    ArrayList<ThemeAccent> createdAccents = null;
//                    WallPaper wallPaper = loadingPattern.pattern;
//                    File patternPath = AndroidUtilities.getPathToAttach(wallPaper.document, true);
//                    Bitmap patternBitmap = null;
//                    for (int a = 0, N = loadingPattern.accents.size(); a < N; a++) {
//                        ThemeAccent accent = loadingPattern.accents.get(a);
//                        if (accent.patternSlug.equals(wallPaper.slug)) {
//                            patternBitmap = createWallpaperForAccent(patternBitmap, "application/x-tgwallpattern".equals(wallPaper.document.mime_type), patternPath, accent);
//                            if (createdAccents == null) {
//                                createdAccents = new ArrayList<>();
//                                createdAccents.add(accent);
//                            }
//                        }
//                    }
//                    if (patternBitmap != null) {
//                        patternBitmap.recycle();
//                    }
//                    checkCurrentWallpaper(createdAccents, false);
//                });
//            }
//        } else if (id == NotificationCenter.fileDidFailToLoad) {
//            String location = (String) args[0];
//            if (watingForLoad.remove(location) != null) {
//                checkCurrentWallpaper(null, false);
//            }
//        }
//    }
}
