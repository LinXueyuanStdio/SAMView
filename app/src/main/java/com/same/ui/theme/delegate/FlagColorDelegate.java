package com.same.ui.theme.delegate;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.same.lib.core.EditTextBoldCursor;
import com.same.lib.core.SimpleTextView;
import com.same.lib.drawable.DrawableManager;
import com.same.lib.theme.ColorApply;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeDescription;

import static com.same.lib.theme.ThemeDescription.FLAG_CHECKTAG;
import static com.same.lib.theme.ThemeDescription.FLAG_CURSORCOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_DRAWABLESELECTEDSTATE;
import static com.same.lib.theme.ThemeDescription.FLAG_HINTTEXTCOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_IMAGECOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_PROGRESSBAR;
import static com.same.lib.theme.ThemeDescription.FLAG_SERVICEBACKGROUND;
import static com.same.lib.theme.ThemeDescription.FLAG_TEXTCOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class FlagColorDelegate implements ColorApply.ColorDelegate {
    @Override
    public void apply(ThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        View viewToInvalidate = description.viewToInvalidate;
        int changeFlags = description.changeFlags;
        if ((changeFlags & FLAG_TEXTCOLOR) != 0) {
            if ((changeFlags & FLAG_CHECKTAG) == 0 || description.checkTag(description.currentKey, viewToInvalidate)) {
                if (viewToInvalidate instanceof TextView) {
                    ((TextView) viewToInvalidate).setTextColor(color);
                } else if (viewToInvalidate instanceof SimpleTextView) {
                    ((SimpleTextView) viewToInvalidate).setTextColor(color);
                }
            }
        }
        if ((changeFlags & FLAG_CURSORCOLOR) != 0) {
            if (viewToInvalidate instanceof EditTextBoldCursor) {
                ((EditTextBoldCursor) viewToInvalidate).setCursorColor(color);
            }
        }
        if ((changeFlags & FLAG_HINTTEXTCOLOR) != 0) {
            if (viewToInvalidate instanceof EditTextBoldCursor) {
                if ((changeFlags & FLAG_PROGRESSBAR) != 0) {
                    ((EditTextBoldCursor) viewToInvalidate).setHeaderHintColor(color);
                } else {
                    ((EditTextBoldCursor) viewToInvalidate).setHintColor(color);
                }
            } else if (viewToInvalidate instanceof EditText) {
                ((EditText) viewToInvalidate).setHintTextColor(color);
            }
        }
        if (viewToInvalidate != null && (changeFlags & FLAG_SERVICEBACKGROUND) != 0) {
            Drawable background = viewToInvalidate.getBackground();
            if (background != null) {
                background.setColorFilter(Theme.colorFilter);
            }
        }
        if ((changeFlags & FLAG_IMAGECOLOR) != 0) {
            if ((changeFlags & FLAG_CHECKTAG) == 0 || description.checkTag(description.currentKey, viewToInvalidate)) {
                if (viewToInvalidate instanceof ImageView) {
                    if ((changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0) {
                        Drawable drawable = ((ImageView) viewToInvalidate).getDrawable();
                        if (drawable instanceof StateListDrawable || Build.VERSION.SDK_INT >= 21 && drawable instanceof RippleDrawable) {
                            DrawableManager.setSelectorDrawableColor(drawable, color, (changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                        }
                    } else {
                        ((ImageView) viewToInvalidate).setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    }
                } else if (viewToInvalidate instanceof SimpleTextView) {
                    SimpleTextView textView = (SimpleTextView) viewToInvalidate;
                    textView.setSideDrawablesColor(color);
                } else if (viewToInvalidate instanceof TextView) {
                    Drawable[] drawables = ((TextView) viewToInvalidate).getCompoundDrawables();
                    if (drawables != null) {
                        for (int a = 0; a < drawables.length; a++) {
                            if (drawables[a] != null) {
                                drawables[a].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                            }
                        }
                    }
                }
            }
        }
    }
}
