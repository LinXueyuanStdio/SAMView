package com.same.lib.theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;

import java.io.File;
import java.io.FileInputStream;

import static com.same.lib.theme.Theme.DEFALT_THEME_ACCENT_ID;
import static com.same.lib.theme.Theme.DEFAULT_BACKGROUND_SLUG;
import static com.same.lib.theme.Theme.THEME_BACKGROUND_SLUG;
import static com.same.lib.theme.Theme.currentTheme;
import static com.same.lib.theme.Theme.hasPreviousTheme;
import static com.same.lib.theme.ThemeManager.getAssetFile;
import static com.same.lib.theme.ThemeManager.saveCurrentTheme;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class WallpaperManager {
    static final Object wallpaperSync = new Object();

    static Drawable wallpaper;
    static Drawable themedWallpaper;
    static int themedWallpaperFileOffset;
    static String themedWallpaperLink;
    static boolean isWallpaperMotion;
    static boolean isPatternWallpaper;
    private static BackgroundGradientDrawable.Disposable backgroundGradientDisposable;

    static Bitmap loadScreenSizedBitmap(FileInputStream stream, int offset) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 1;
            opts.inJustDecodeBounds = true;
            stream.getChannel().position(offset);
            BitmapFactory.decodeStream(stream, null, opts);
            float photoW = opts.outWidth;
            float photoH = opts.outHeight;
            float scaleFactor;
            int w_filter = AndroidUtilities.dp(360);
            int h_filter = AndroidUtilities.dp(640);
            if (w_filter >= h_filter && photoW > photoH) {
                scaleFactor = Math.max(photoW / w_filter, photoH / h_filter);
            } else {
                scaleFactor = Math.min(photoW / w_filter, photoH / h_filter);
            }
            if (scaleFactor < 1.2f) {
                scaleFactor = 1;
            }
            opts.inJustDecodeBounds = false;
            if (scaleFactor > 1.0f && (photoW > w_filter || photoH > h_filter)) {
                int sample = 1;
                do {
                    sample *= 2;
                }
                while (sample * 2 < scaleFactor);
                opts.inSampleSize = sample;
            } else {
                opts.inSampleSize = (int) scaleFactor;
            }
            stream.getChannel().position(offset);
            return BitmapFactory.decodeStream(stream, null, opts);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception ignore) {

            }
        }
        return null;
    }

    public static boolean isThemeWallpaperPublic() {
        return !TextUtils.isEmpty(themedWallpaperLink);
    }

    public static boolean hasWallpaperFromTheme() {
        if (currentTheme.firstAccentIsDefault && currentTheme.currentAccentId == DEFALT_THEME_ACCENT_ID) {
            return false;
        }
        return Theme.currentColors.containsKey(KeyHub.key_chat_wallpaper) || themedWallpaperFileOffset > 0 || !TextUtils.isEmpty(themedWallpaperLink);
    }

    public static void setThemeWallpaper(Context context, ThemeInfo themeInfo, Bitmap bitmap, File path) {
        Theme.currentColors.remove(KeyHub.key_chat_wallpaper);
        Theme.currentColors.remove(KeyHub.key_chat_wallpaper_gradient_to);
        Theme.currentColors.remove(KeyHub.key_chat_wallpaper_gradient_rotation);
        themedWallpaperLink = null;
        themeInfo.setOverrideWallpaper(null);
        if (bitmap != null) {
            themedWallpaper = new BitmapDrawable(bitmap);
            saveCurrentTheme(context, themeInfo, false, false, false);
            Theme.calcBackgroundColor(themedWallpaper, 0);
            Theme.applyChatServiceMessageColor();
            NotificationCenter.post(NotificationCenter.didSetNewWallpaper);
        } else {
            themedWallpaper = null;
            wallpaper = null;
            saveCurrentTheme(context, themeInfo, false, false, false);
            reloadWallpaper(context);
        }
    }

    public static void reloadWallpaper(Context context) {
        if (backgroundGradientDisposable != null) {
            backgroundGradientDisposable.dispose();
            backgroundGradientDisposable = null;
        }
        wallpaper = null;
        themedWallpaper = null;
        loadWallpaper(context);
    }

    public static void loadWallpaper(Context context) {
        if (wallpaper != null) {
            return;
        }
        boolean defaultTheme = currentTheme.firstAccentIsDefault && currentTheme.currentAccentId == DEFALT_THEME_ACCENT_ID;
        File wallpaperFile;
        boolean wallpaperMotion;
        ThemeAccent accent = currentTheme.getAccent(false);
        if (accent != null && !hasPreviousTheme) {
            wallpaperFile = accent.getPathToWallpaper();
            wallpaperMotion = accent.patternMotion;
        } else {
            wallpaperFile = null;
            wallpaperMotion = false;
        }

        OverrideWallpaperInfo overrideWallpaper = currentTheme.overrideWallpaper;
        AndroidUtilities.searchQueue.postRunnable(() -> {
            synchronized (wallpaperSync) {
                boolean overrideTheme = (!hasPreviousTheme || Theme.isApplyingAccent) && overrideWallpaper != null;
                if (overrideWallpaper != null) {
                    isWallpaperMotion = overrideWallpaper != null && overrideWallpaper.isMotion;
                    isPatternWallpaper = overrideWallpaper != null && overrideWallpaper.color != 0 && !overrideWallpaper.isDefault() && !overrideWallpaper.isColor();
                } else {
                    isWallpaperMotion = currentTheme.isMotion;
                    isPatternWallpaper = currentTheme.patternBgColor != 0;
                }
                if (!overrideTheme) {
                    Integer backgroundColor = defaultTheme ? null : Theme.currentColors.get(KeyHub.key_chat_wallpaper);
                    if (wallpaperFile != null && wallpaperFile.exists()) {
                        try {
                            wallpaper = Drawable.createFromPath(wallpaperFile.getAbsolutePath());
                            isWallpaperMotion = wallpaperMotion;
                            Theme.isCustomTheme = true;
                            isPatternWallpaper = true;
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    } else if (backgroundColor != null) {
                        Integer gradientToColor = Theme.currentColors.get(KeyHub.key_chat_wallpaper_gradient_to);
                        Integer rotation = Theme.currentColors.get(KeyHub.key_chat_wallpaper_gradient_rotation);
                        if (rotation == null) {
                            rotation = 45;
                        }
                        if (gradientToColor == null || gradientToColor.equals(backgroundColor)) {
                            wallpaper = new ColorDrawable(backgroundColor);
                        } else {
                            final int[] colors = {backgroundColor, gradientToColor};
                            final BackgroundGradientDrawable.Orientation orientation = BackgroundGradientDrawable.getGradientOrientation(rotation);
                            final BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(orientation, colors);
                            final BackgroundGradientDrawable.Listener listener = new BackgroundGradientDrawable.ListenerAdapter() {
                                @Override
                                public void onSizeReady(int width, int height) {
                                    final boolean isOrientationPortrait = AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y;
                                    final boolean isGradientPortrait = width <= height;
                                    if (isOrientationPortrait == isGradientPortrait) {
                                        NotificationCenter.post(NotificationCenter.didSetNewWallpaper);
                                    }
                                }
                            };
                            backgroundGradientDisposable = backgroundGradientDrawable.startDithering(BackgroundGradientDrawable.Sizes.ofDeviceScreen(), listener, 100);
                            wallpaper = backgroundGradientDrawable;
                        }
                        Theme.isCustomTheme = true;
                    } else if (themedWallpaperLink != null) {
                        try {
                            File pathToWallpaper = new File(AndroidUtilities.getFilesDirFixed(), AndroidUtilities.MD5(themedWallpaperLink) + ".wp");
                            Bitmap bitmap = loadScreenSizedBitmap(new FileInputStream(pathToWallpaper), 0);
                            if (bitmap != null) {
                                themedWallpaper = wallpaper = new BitmapDrawable(bitmap);
                                Theme.isCustomTheme = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (themedWallpaperFileOffset > 0 && (currentTheme.pathToFile != null || currentTheme.assetName != null)) {
                        try {
                            File file;
                            if (currentTheme.assetName != null) {
                                file = getAssetFile(context, currentTheme.assetName);
                            } else {
                                file = new File(currentTheme.pathToFile);
                            }
                            Bitmap bitmap = loadScreenSizedBitmap(new FileInputStream(file), themedWallpaperFileOffset);
                            if (bitmap != null) {
                                themedWallpaper = wallpaper = new BitmapDrawable(bitmap);
                                Theme.isCustomTheme = true;
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (wallpaper == null) {
                    int selectedColor = overrideWallpaper != null ? overrideWallpaper.color : 0;
                    try {
                        if (overrideWallpaper == null || overrideWallpaper.isDefault()) {
                            wallpaper = context.getResources().getDrawable(R.drawable.background_hd);
                            Theme.isCustomTheme = false;
                        } else if (!overrideWallpaper.isColor() || overrideWallpaper.gradientColor != 0) {
                            if (selectedColor != 0 && !isPatternWallpaper) {
                                if (overrideWallpaper.gradientColor != 0) {
                                    final int[] colors = {selectedColor, overrideWallpaper.gradientColor};
                                    final BackgroundGradientDrawable.Orientation orientation = BackgroundGradientDrawable.getGradientOrientation(overrideWallpaper.rotation);
                                    final BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(orientation, colors);
                                    final BackgroundGradientDrawable.Listener listener = new BackgroundGradientDrawable.ListenerAdapter() {
                                        @Override
                                        public void onSizeReady(int width, int height) {
                                            final boolean isOrientationPortrait = AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y;
                                            final boolean isGradientPortrait = width <= height;
                                            if (isOrientationPortrait == isGradientPortrait) {
                                                NotificationCenter.post(NotificationCenter.didSetNewWallpaper);
                                            }
                                        }
                                    };
                                    backgroundGradientDisposable = backgroundGradientDrawable.startDithering(BackgroundGradientDrawable.Sizes.ofDeviceScreen(), listener, 100);
                                    wallpaper = backgroundGradientDrawable;
                                } else {
                                    wallpaper = new ColorDrawable(selectedColor);
                                }
                            } else {
                                File toFile = new File(AndroidUtilities.getFilesDirFixed(), overrideWallpaper.fileName);
                                if (toFile.exists()) {
                                    Bitmap bitmap = loadScreenSizedBitmap(new FileInputStream(toFile), 0);
                                    if (bitmap != null) {
                                        wallpaper = new BitmapDrawable(bitmap);
                                        Theme.isCustomTheme = true;
                                    }
                                }
                                if (wallpaper == null) {
                                    wallpaper = context.getResources().getDrawable(R.drawable.background_hd);
                                    Theme.isCustomTheme = false;
                                }
                            }
                        }
                    } catch (Throwable throwable) {
                        //ignore
                    }
                    if (wallpaper == null) {
                        if (selectedColor == 0) {
                            selectedColor = -2693905;
                        }
                        wallpaper = new ColorDrawable(selectedColor);
                    }
                }
                Theme.calcBackgroundColor(wallpaper, 1);
                AndroidUtilities.runOnUIThread(() -> {
                    Theme.applyChatServiceMessageColor();
                    NotificationCenter.post(NotificationCenter.didSetNewWallpaper);
                });
            }
        });
    }

    public static Drawable getThemedWallpaper(Context context, boolean thumb, View ownerView) {
        Integer backgroundColor = Theme.currentColors.get(KeyHub.key_chat_wallpaper);
        File file = null;
        int offset = 0;
        if (backgroundColor != null) {
            Integer gradientToColor = Theme.currentColors.get(KeyHub.key_chat_wallpaper_gradient_to);
            Integer rotation = Theme.currentColors.get(KeyHub.key_chat_wallpaper_gradient_rotation);
            if (rotation == null) {
                rotation = 45;
            }
            if (gradientToColor == null) {
                return new ColorDrawable(backgroundColor);
            } else {
                ThemeAccent accent = currentTheme.getAccent(false);
                if (accent != null && !TextUtils.isEmpty(accent.patternSlug) && Theme.previousTheme == null) {
                    File wallpaperFile = accent.getPathToWallpaper();
                    if (wallpaperFile != null && wallpaperFile.exists()) {
                        file = wallpaperFile;
                    }
                }
                if (file == null) {
                    final int[] colors = {backgroundColor, gradientToColor};
                    final GradientDrawable.Orientation orientation = BackgroundGradientDrawable.getGradientOrientation(rotation);
                    final BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(orientation, colors);
                    final BackgroundGradientDrawable.Sizes sizes;
                    if (!thumb) {
                        sizes = BackgroundGradientDrawable.Sizes.ofDeviceScreen();
                    } else {
                        sizes = BackgroundGradientDrawable.Sizes.ofDeviceScreen(BackgroundGradientDrawable.DEFAULT_COMPRESS_RATIO / 4f, BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT);
                    }
                    final BackgroundGradientDrawable.Listener listener;
                    if (ownerView != null) {
                        listener = new BackgroundGradientDrawable.ListenerAdapter() {
                            @Override
                            public void onSizeReady(int width, int height) {
                                if (!thumb) {
                                    final boolean isOrientationPortrait = AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y;
                                    final boolean isGradientPortrait = width <= height;
                                    if (isOrientationPortrait == isGradientPortrait) {
                                        ownerView.invalidate();
                                    }
                                } else {
                                    ownerView.invalidate();
                                }
                            }
                        };
                    } else {
                        listener = null;
                    }
                    backgroundGradientDrawable.startDithering(sizes, listener);
                    return backgroundGradientDrawable;
                }
            }
        } else if (themedWallpaperFileOffset > 0 && (currentTheme.pathToFile != null || currentTheme.assetName != null)) {
            if (currentTheme.assetName != null) {
                file = getAssetFile(context, currentTheme.assetName);
            } else {
                file = new File(currentTheme.pathToFile);
            }
            offset = themedWallpaperFileOffset;
        }
        if (file != null) {
            FileInputStream stream = null;
            try {
                int currentPosition = 0;
                stream = new FileInputStream(file);
                stream.getChannel().position(offset);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                int scaleFactor = 1;
                if (thumb) {
                    opts.inJustDecodeBounds = true;
                    float photoW = opts.outWidth;
                    float photoH = opts.outHeight;
                    int maxWidth = AndroidUtilities.dp(100);
                    while (photoW > maxWidth || photoH > maxWidth) {
                        scaleFactor *= 2;
                        photoW /= 2;
                        photoH /= 2;
                    }
                }
                opts.inJustDecodeBounds = false;
                opts.inSampleSize = scaleFactor;
                Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);
                if (bitmap != null) {
                    return new BitmapDrawable(bitmap);
                }
            } catch (Throwable e) {
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
        }
        return null;
    }

    public static String getSelectedBackgroundSlug() {
        if (currentTheme.overrideWallpaper != null) {
            return currentTheme.overrideWallpaper.slug;
        }
        if (hasWallpaperFromTheme()) {
            return THEME_BACKGROUND_SLUG;
        }
        return DEFAULT_BACKGROUND_SLUG;
    }

    public static Drawable getCachedWallpaper() {
        synchronized (wallpaperSync) {
            if (themedWallpaper != null) {
                return themedWallpaper;
            } else {
                return wallpaper;
            }
        }
    }

    public static Drawable getCachedWallpaperNonBlocking() {
        if (themedWallpaper != null) {
            return themedWallpaper;
        } else {
            return wallpaper;
        }
    }

    public static boolean isWallpaperMotion() {
        return isWallpaperMotion;
    }

    public static boolean isPatternWallpaper() {
        return isPatternWallpaper;
    }
}
