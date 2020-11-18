package com.same.lib.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.same.lib.R;
import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.Bitmaps;
import com.same.lib.base.SharedConfig;
import com.same.lib.drawable.DrawableManager;
import com.same.lib.drawable.MessageDrawable;
import com.same.lib.theme.BackgroundGradientDrawable;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.ThemeManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import static com.same.lib.theme.ThemeManager.getThemeFileValues;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class ThemePreview {
    public static int getPreviewColor(HashMap<String, Integer> colors, String key) {
        Integer color = colors.get(key);
        if (color == null) {
            color = ThemeManager.getDefaultColors().get(key);
        }
        return color;
    }

    public static String createThemePreviewImage(Context context, String pathToFile, String wallpaperPath) {
        try {
            String[] wallpaperLink = new String[1];
            HashMap<String, Integer> colors = getThemeFileValues(context, new File(pathToFile), null, wallpaperLink);
            Integer wallpaperFileOffset = colors.get("wallpaperFileOffset");
            Bitmap bitmap = Bitmaps.createBitmap(560, 678, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint();

            int actionBarColor = getPreviewColor(colors, KeyHub.key_actionBarDefault);
            int actionBarIconColor = getPreviewColor(colors, KeyHub.key_actionBarDefaultIcon);
            int messageFieldColor = getPreviewColor(colors, KeyHub.key_chat_messagePanelBackground);
            int messageFieldIconColor = getPreviewColor(colors, KeyHub.key_chat_messagePanelIcons);
            int messageInColor = getPreviewColor(colors, KeyHub.key_chat_inBubble);
            int messageOutColor = getPreviewColor(colors, KeyHub.key_chat_outBubble);
            Integer messageOutGradientColor = colors.get(KeyHub.key_chat_outBubbleGradient);
            Integer backgroundColor = colors.get(KeyHub.key_chat_wallpaper);
            Integer serviceColor = colors.get(KeyHub.key_chat_serviceBackground);
            Integer gradientToColor = colors.get(KeyHub.key_chat_wallpaper_gradient_to);

            Drawable backDrawable = context.getResources().getDrawable(R.drawable.preview_back).mutate();
            DrawableManager.setDrawableColor(backDrawable, actionBarIconColor);
            Drawable otherDrawable = context.getResources().getDrawable(R.drawable.preview_dots).mutate();
            DrawableManager.setDrawableColor(otherDrawable, actionBarIconColor);
            Drawable emojiDrawable = context.getResources().getDrawable(R.drawable.preview_smile).mutate();
            DrawableManager.setDrawableColor(emojiDrawable, messageFieldIconColor);
            Drawable micDrawable = context.getResources().getDrawable(R.drawable.preview_mic).mutate();
            DrawableManager.setDrawableColor(micDrawable, messageFieldIconColor);

            MessageDrawable[] msgDrawable = new MessageDrawable[2];
            for (int a = 0; a < 2; a++) {
                msgDrawable[a] = new MessageDrawable(MessageDrawable.TYPE_PREVIEW, a == 1, false) {
                    @Override
                    protected int getColor(String key) {
                        return getPreviewColor(colors, key);
                    }

                    @Override
                    protected Integer getCurrentColor(String key) {
                        return colors.get(key);
                    }
                };
                DrawableManager.setDrawableColor(msgDrawable[a], a == 0 ? messageInColor : messageOutColor);
            }

            RectF rect = new RectF();
            int quality = 80;
            boolean hasBackground = false;
            if (wallpaperPath != null) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(wallpaperPath, options);
                    if (options.outWidth > 0 && options.outHeight > 0) {
                        float scale = Math.min(options.outWidth / 560.0f, options.outHeight / 560.0f);
                        options.inSampleSize = 1;
                        if (scale > 1.0f) {
                            do {
                                options.inSampleSize *= 2;
                            } while (options.inSampleSize < scale);
                        }
                        options.inJustDecodeBounds = false;
                        Bitmap wallpaper = BitmapFactory.decodeFile(wallpaperPath, options);
                        if (wallpaper != null) {
                            Paint bitmapPaint = new Paint();
                            bitmapPaint.setFilterBitmap(true);
                            scale = Math.min(wallpaper.getWidth() / 560.0f, wallpaper.getHeight() / 560.0f);
                            rect.set(0, 0, wallpaper.getWidth() / scale, wallpaper.getHeight() / scale);
                            rect.offset((bitmap.getWidth() - rect.width()) / 2, (bitmap.getHeight() - rect.height()) / 2);
                            canvas.drawBitmap(wallpaper, null, rect, bitmapPaint);
                            hasBackground = true;
                            if (serviceColor == null) {
                                serviceColor = AndroidUtilities.calcDrawableColor(new BitmapDrawable(wallpaper))[0];
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else if (backgroundColor != null) {
                Drawable wallpaperDrawable;
                if (gradientToColor == null) {
                    wallpaperDrawable = new ColorDrawable(backgroundColor);
                } else {
                    Integer gradientRotation = colors.get(KeyHub.key_chat_wallpaper_gradient_rotation);
                    if (gradientRotation == null) {
                        gradientRotation = 45;
                    }
                    final int[] gradientColors = {backgroundColor, gradientToColor};
                    wallpaperDrawable = BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(context, gradientRotation, gradientColors, bitmap.getWidth(), bitmap.getHeight() - 120);
                    quality = 90;
                }
                wallpaperDrawable.setBounds(0, 120, bitmap.getWidth(), bitmap.getHeight() - 120);
                wallpaperDrawable.draw(canvas);
                if (serviceColor == null) {
                    serviceColor = AndroidUtilities.calcDrawableColor(new ColorDrawable(backgroundColor))[0];
                }
                hasBackground = true;
            } else if (wallpaperFileOffset != null && wallpaperFileOffset >= 0 || !TextUtils.isEmpty(wallpaperLink[0])) {
                FileInputStream stream = null;
                File pathToWallpaper = null;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    if (!TextUtils.isEmpty(wallpaperLink[0])) {
                        pathToWallpaper = new File(AndroidUtilities.getFilesDirFixed(), AndroidUtilities.MD5(wallpaperLink[0]) + ".wp");
                        BitmapFactory.decodeFile(pathToWallpaper.getAbsolutePath(), options);
                    } else {
                        stream = new FileInputStream(pathToFile);
                        stream.getChannel().position(wallpaperFileOffset);
                        BitmapFactory.decodeStream(stream, null, options);
                    }
                    if (options.outWidth > 0 && options.outHeight > 0) {
                        float scale = Math.min(options.outWidth / 560.0f, options.outHeight / 560.0f);
                        options.inSampleSize = 1;
                        if (scale > 1.0f) {
                            do {
                                options.inSampleSize *= 2;
                            } while (options.inSampleSize < scale);
                        }
                        options.inJustDecodeBounds = false;
                        Bitmap wallpaper;
                        if (pathToWallpaper != null) {
                            wallpaper = BitmapFactory.decodeFile(pathToWallpaper.getAbsolutePath(), options);
                        } else {
                            stream.getChannel().position(wallpaperFileOffset);
                            wallpaper = BitmapFactory.decodeStream(stream, null, options);
                        }
                        if (wallpaper != null) {
                            Paint bitmapPaint = new Paint();
                            bitmapPaint.setFilterBitmap(true);
                            scale = Math.min(wallpaper.getWidth() / 560.0f, wallpaper.getHeight() / 560.0f);
                            rect.set(0, 0, wallpaper.getWidth() / scale, wallpaper.getHeight() / scale);
                            rect.offset((bitmap.getWidth() - rect.width()) / 2, (bitmap.getHeight() - rect.height()) / 2);
                            canvas.drawBitmap(wallpaper, null, rect, bitmapPaint);
                            hasBackground = true;
                            if (serviceColor == null) {
                                serviceColor = AndroidUtilities.calcDrawableColor(new BitmapDrawable(wallpaper))[0];
                            }
                        }
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
            if (!hasBackground) {
                BitmapDrawable catsDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.catstile).mutate();
                if (serviceColor == null) {
                    serviceColor = AndroidUtilities.calcDrawableColor(catsDrawable)[0];
                }
                catsDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                catsDrawable.setBounds(0, 120, bitmap.getWidth(), bitmap.getHeight() - 120);
                catsDrawable.draw(canvas);
            }

            paint.setColor(actionBarColor);
            canvas.drawRect(0, 0, bitmap.getWidth(), 120, paint);

            if (backDrawable != null) {
                int x = 13;
                int y = (120 - backDrawable.getIntrinsicHeight()) / 2;
                backDrawable.setBounds(x, y, x + backDrawable.getIntrinsicWidth(), y + backDrawable.getIntrinsicHeight());
                backDrawable.draw(canvas);
            }
            if (otherDrawable != null) {
                int x = bitmap.getWidth() - otherDrawable.getIntrinsicWidth() - 10;
                int y = (120 - otherDrawable.getIntrinsicHeight()) / 2;
                otherDrawable.setBounds(x, y, x + otherDrawable.getIntrinsicWidth(), y + otherDrawable.getIntrinsicHeight());
                otherDrawable.draw(canvas);
            }
            msgDrawable[1].setBounds(161, 216, bitmap.getWidth() - 20, 216 + 92);
            msgDrawable[1].setTop(0, 522, false, false);
            msgDrawable[1].draw(canvas);

            msgDrawable[1].setBounds(161, 430, bitmap.getWidth() - 20, 430 + 92);
            msgDrawable[1].setTop(430, 522, false, false);
            msgDrawable[1].draw(canvas);

            msgDrawable[0].setBounds(20, 323, 399, 323 + 92);
            msgDrawable[0].setTop(323, 522, false, false);
            msgDrawable[0].draw(canvas);

            if (serviceColor != null) {
                int x = (bitmap.getWidth() - 126) / 2;
                int y = 150;
                rect.set(x, y, x + 126, y + 42);
                paint.setColor(serviceColor);
                canvas.drawRoundRect(rect, 21, 21, paint);
            }

            paint.setColor(messageFieldColor);
            canvas.drawRect(0, bitmap.getHeight() - 120, bitmap.getWidth(), bitmap.getHeight(), paint);
            if (emojiDrawable != null) {
                int x = 22;
                int y = bitmap.getHeight() - 120 + (120 - emojiDrawable.getIntrinsicHeight()) / 2;
                emojiDrawable.setBounds(x, y, x + emojiDrawable.getIntrinsicWidth(), y + emojiDrawable.getIntrinsicHeight());
                emojiDrawable.draw(canvas);
            }
            if (micDrawable != null) {
                int x = bitmap.getWidth() - micDrawable.getIntrinsicWidth() - 22;
                int y = bitmap.getHeight() - 120 + (120 - micDrawable.getIntrinsicHeight()) / 2;
                micDrawable.setBounds(x, y, x + micDrawable.getIntrinsicWidth(), y + micDrawable.getIntrinsicHeight());
                micDrawable.draw(canvas);
            }
            canvas.setBitmap(null);

            String fileName = Integer.MIN_VALUE + "_" + SharedConfig.getLastLocalId() + ".jpg";
            final File cacheFile = new File(AndroidUtilities.getCacheDir(context), fileName);
            try {
                FileOutputStream stream = new FileOutputStream(cacheFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                SharedConfig.saveConfig();
                return cacheFile.getAbsolutePath();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
