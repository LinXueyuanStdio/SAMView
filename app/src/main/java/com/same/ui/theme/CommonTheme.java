package com.same.ui.theme;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.same.lib.theme.AbsTheme;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.lib.util.Space;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class CommonTheme extends AbsTheme {

    public static Paint dividerPaint;
    public static Paint linkSelectionPaint;
    public static Paint checkboxSquare_eraserPaint;
    public static Paint checkboxSquare_checkPaint;
    public static Paint checkboxSquare_backgroundPaint;
    public static Paint avatar_backgroundPaint;

    public static boolean dialogs_archiveDrawableRecolored;
    public static boolean dialogs_hidePsaDrawableRecolored;
    public static boolean dialogs_archiveAvatarDrawableRecolored;

    //region 业务：通用
    public static void createCommonResources(Context context) {
        if (dividerPaint == null) {
            dividerPaint = new Paint();
            dividerPaint.setStrokeWidth(1);

            avatar_backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            checkboxSquare_checkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            checkboxSquare_checkPaint.setStyle(Paint.Style.STROKE);
            checkboxSquare_checkPaint.setStrokeWidth(Space.dp(2));
            checkboxSquare_checkPaint.setStrokeCap(Paint.Cap.ROUND);
            checkboxSquare_eraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            checkboxSquare_eraserPaint.setColor(0);
            checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            checkboxSquare_backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linkSelectionPaint = new Paint();
            applyCommonTheme();
        }
    }

    public static void applyCommonTheme() {
        if (dividerPaint == null) {
            return;
        }
        dividerPaint.setColor(Theme.getColor(KeyHub.key_divider));
        linkSelectionPaint.setColor(Theme.getColor(KeyHub.key_windowBackgroundWhiteLinkSelection));

        dialogs_archiveAvatarDrawableRecolored = false;
        dialogs_hidePsaDrawableRecolored = false;
        dialogs_archiveDrawableRecolored = false;
    }

    @Override
    public void destroyResources() {

    }

    @Override
    public void createResources(Context context) {
        createCommonResources(context);
    }

    @Override
    public void applyResources(Context context) {
        applyCommonTheme();
    }
    //endregion
}
