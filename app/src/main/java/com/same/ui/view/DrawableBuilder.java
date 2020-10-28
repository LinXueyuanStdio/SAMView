package com.same.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.StateSet;

import com.same.lib.theme.Theme;
import com.same.ui.R;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class DrawableBuilder {
    public static Drawable createEditTextDrawable(Context context, boolean alert) {
        Resources resources = context.getResources();
        Drawable defaultDrawable = resources.getDrawable(R.drawable.search_dark).mutate();
        defaultDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(alert ? Theme.key_dialogInputField : Theme.key_windowBackgroundWhiteInputField), PorterDuff.Mode.MULTIPLY));
        Drawable pressedDrawable = resources.getDrawable(R.drawable.search_dark_activated).mutate();
        pressedDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(alert ?Theme. key_dialogInputFieldActivated : Theme.key_windowBackgroundWhiteInputFieldActivated), PorterDuff.Mode.MULTIPLY));
        StateListDrawable stateListDrawable = new StateListDrawable() {
            @Override
            public boolean selectDrawable(int index) {
                if (Build.VERSION.SDK_INT < 21) {
                    Drawable drawable = Theme.getStateDrawable(this, index);
                    ColorFilter colorFilter = null;
                    if (drawable instanceof BitmapDrawable) {
                        colorFilter = ((BitmapDrawable) drawable).getPaint().getColorFilter();
                    } else if (drawable instanceof NinePatchDrawable) {
                        colorFilter = ((NinePatchDrawable) drawable).getPaint().getColorFilter();
                    }
                    boolean result = super.selectDrawable(index);
                    if (colorFilter != null) {
                        drawable.setColorFilter(colorFilter);
                    }
                    return result;
                }
                return super.selectDrawable(index);
            }
        };
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, pressedDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, pressedDrawable);
        stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable);
        return stateListDrawable;
    }

}
