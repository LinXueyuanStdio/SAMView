package com.same.ui.theme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextPaint;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.theme.AbsTheme;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class ProfileTheme extends AbsTheme {

    public static TextPaint profile_aboutTextPaint;

    //region 业务：身份

    /**
     * 身份的UI资源
     * @param context 上下文
     */
    public static void createProfileResources(Context context) {
        profile_aboutTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        Resources resources = context.getResources();
        applyProfileTheme();
        profile_aboutTextPaint.setTextSize(AndroidUtilities.dp(16));
    }

    public static void applyProfileTheme() {
        if (profile_aboutTextPaint == null) {
            return;
        }

        profile_aboutTextPaint.setColor(Theme.getColor(KeyHub.key_windowBackgroundWhiteBlackText));
        profile_aboutTextPaint.linkColor = Theme.getColor(KeyHub.key_windowBackgroundWhiteLinkText);
    }

    static ColorFilter currentShareColorFilter;
    static int currentShareColorFilterColor;
    static ColorFilter currentShareSelectedColorFilter;
    static int currentShareSelectedColorFilterColor;

    public static ColorFilter getShareColorFilter(int color, boolean selected) {
        if (selected) {
            if (currentShareSelectedColorFilter == null || currentShareSelectedColorFilterColor != color) {
                currentShareSelectedColorFilterColor = color;
                currentShareSelectedColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
            return currentShareSelectedColorFilter;
        } else {
            if (currentShareColorFilter == null || currentShareColorFilterColor != color) {
                currentShareColorFilterColor = color;
                currentShareColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
            return currentShareColorFilter;
        }
    }

    @Override
    public void destroyResources() {

    }

    @Override
    public void createResources(Context context) {
createProfileResources(context);
    }

    @Override
    public void applyResources(Context context) {
        applyProfileTheme();
    }
    //endregion

}
