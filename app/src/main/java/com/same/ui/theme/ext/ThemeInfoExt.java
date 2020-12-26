package com.same.ui.theme.ext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.theme.BackgroundGradientDrawable;
import com.same.lib.theme.ThemeInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/12/26
 * @description null
 * @usage null
 */
public class ThemeInfoExt {

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
                    }
                    while (options.inSampleSize < scale);
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

    public static boolean createBackground(ThemeInfo themeInfo,  File file, String toPath) {
        try {
            Bitmap bitmap = getScaledBitmap(AndroidUtilities.dp(640), AndroidUtilities.dp(360), file.getAbsolutePath(), null, 0);
            if (bitmap != null && themeInfo.patternBgColor != 0) {
                Bitmap finalBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                Canvas canvas = new Canvas(finalBitmap);
                int patternColor;
                if (themeInfo.patternBgGradientColor != 0) {
                    patternColor = AndroidUtilities.getAverageColor(themeInfo.patternBgColor, themeInfo.patternBgGradientColor);
                    GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(themeInfo.patternBgGradientRotation),
                            new int[]{themeInfo.patternBgColor, themeInfo.patternBgGradientColor});
                    gradientDrawable.setBounds(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());
                    gradientDrawable.draw(canvas);
                } else {
                    patternColor = AndroidUtilities.getPatternColor(themeInfo.patternBgColor);
                    canvas.drawColor(themeInfo.patternBgColor);
                }
                Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
                paint.setColorFilter(new PorterDuffColorFilter(patternColor, PorterDuff.Mode.SRC_IN));
                paint.setAlpha((int) (themeInfo.patternIntensity / 100.0f * 255));
                canvas.drawBitmap(bitmap, 0, 0, paint);
                bitmap = finalBitmap;
                canvas.setBitmap(null);
            }
            if (themeInfo.isBlured) {
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
}
