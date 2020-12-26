package com.same.ui.theme.delegate;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;

import com.same.lib.drawable.BackDrawable;
import com.same.lib.drawable.CombinedDrawable;
import com.same.lib.theme.ColorApply;
import com.same.lib.theme.MyThemeDescription;

import static com.same.lib.theme.MyThemeDescription.FLAG_BACKGROUNDFILTER;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class DrawableColorDelegate implements ColorApply.ColorDelegate {
    @Override
    public void apply(MyThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        if (description.drawablesToUpdate != null) {
            Drawable[] drawablesToUpdate = description.drawablesToUpdate;
            for (int a = 0; a < drawablesToUpdate.length; a++) {
                if (drawablesToUpdate[a] == null) {
                    continue;
                }
                if (drawablesToUpdate[a] instanceof BackDrawable) {
                    ((BackDrawable) drawablesToUpdate[a]).setColor(color);
                } else if (drawablesToUpdate[a] instanceof CombinedDrawable) {
                    if ((description.changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                        ((CombinedDrawable) drawablesToUpdate[a]).getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    } else {
                        ((CombinedDrawable) drawablesToUpdate[a]).getIcon().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                } else {
                    drawablesToUpdate[a].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            }
        }
    }
}
