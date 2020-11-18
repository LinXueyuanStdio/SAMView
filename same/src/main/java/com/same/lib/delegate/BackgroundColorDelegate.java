package com.same.lib.delegate;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;

import com.same.lib.core.EditTextBoldCursor;
import com.same.lib.drawable.CombinedDrawable;
import com.same.lib.drawable.DrawableManager;
import com.same.lib.theme.ColorApply;
import com.same.lib.theme.ThemeDescription;

import static com.same.lib.theme.ThemeDescription.FLAG_BACKGROUND;
import static com.same.lib.theme.ThemeDescription.FLAG_BACKGROUNDFILTER;
import static com.same.lib.theme.ThemeDescription.FLAG_CHECKTAG;
import static com.same.lib.theme.ThemeDescription.FLAG_DRAWABLESELECTEDSTATE;
import static com.same.lib.theme.ThemeDescription.FLAG_PROGRESSBAR;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class BackgroundColorDelegate implements ColorApply.ColorDelegate {
    @Override
    public void apply(ThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        View viewToInvalidate = description.viewToInvalidate;
        Class[] listClasses = description.listClasses;
        String[] listClassesFieldName = description.listClassesFieldName;
        int changeFlags = description.changeFlags;
        if (viewToInvalidate != null && listClasses == null && listClassesFieldName == null) {
            if ((changeFlags & FLAG_CHECKTAG) == 0 || description.checkTag(description.currentKey, viewToInvalidate)) {
                if ((changeFlags & FLAG_BACKGROUND) != 0) {
                    viewToInvalidate.setBackgroundColor(color);
                }
                if ((changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                    if ((changeFlags & FLAG_PROGRESSBAR) != 0) {
                        if (viewToInvalidate instanceof EditTextBoldCursor) {
                            ((EditTextBoldCursor) viewToInvalidate).setErrorLineColor(color);
                        }
                    } else {
                        Drawable drawable = viewToInvalidate.getBackground();
                        if (drawable instanceof CombinedDrawable) {
                            if ((changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0) {
                                drawable = ((CombinedDrawable) drawable).getBackground();
                            } else {
                                drawable = ((CombinedDrawable) drawable).getIcon();
                            }
                        }
                        if (drawable != null) {
                            if (drawable instanceof StateListDrawable || Build.VERSION.SDK_INT >= 21 && drawable instanceof RippleDrawable) {
                                DrawableManager.setSelectorDrawableColor(drawable, color, (changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                            } else if (drawable instanceof ShapeDrawable) {
                                ((ShapeDrawable) drawable).getPaint().setColor(color);
                            } else {
                                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                            }
                        }
                    }
                }
            }
        }
    }
}
