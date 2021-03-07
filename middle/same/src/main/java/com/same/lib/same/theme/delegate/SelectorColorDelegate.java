package com.same.lib.same.theme.delegate;

import android.content.Context;
import android.view.View;

import com.same.lib.drawable.DrawableManager;
import com.same.lib.theme.ColorApply;
import com.same.lib.theme.MyThemeDescription;

import static com.same.lib.theme.MyThemeDescription.FLAG_SELECTOR;
import static com.same.lib.theme.MyThemeDescription.FLAG_SELECTORWHITE;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class SelectorColorDelegate implements ColorApply.ColorDelegate {
    @Override
    public void apply(MyThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        View viewToInvalidate = description.viewToInvalidate;
        int changeFlags = description.changeFlags;
        Class[] listClasses = description.listClasses;
        if (viewToInvalidate != null && (listClasses == null || listClasses.length == 0)) {
            if ((changeFlags & FLAG_SELECTOR) != 0) {
                viewToInvalidate.setBackgroundDrawable(DrawableManager.getSelectorDrawable(false));
            } else if ((changeFlags & FLAG_SELECTORWHITE) != 0) {
                viewToInvalidate.setBackgroundDrawable(DrawableManager.getSelectorDrawable(true));
            }
        }
    }
}
