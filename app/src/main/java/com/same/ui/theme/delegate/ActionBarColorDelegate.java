package com.same.ui.theme.delegate;

import android.content.Context;
import android.view.View;

import com.same.lib.core.ActionBar;
import com.same.lib.theme.ColorApply;
import com.same.lib.theme.ThemeDescription;

import static com.same.lib.theme.ThemeDescription.FLAG_AB_AM_BACKGROUND;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_AM_ITEMSCOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_AM_SELECTORCOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_AM_TOPBACKGROUND;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_ITEMSCOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_SEARCH;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_SELECTORCOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_SUBMENUBACKGROUND;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_SUBMENUITEM;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_SUBTITLECOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_AB_TITLECOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_IMAGECOLOR;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class ActionBarColorDelegate implements ColorApply.ColorDelegate {
    @Override
    public void apply(ThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        View viewToInvalidate = description.viewToInvalidate;
        int changeFlags = description.changeFlags;
        if (viewToInvalidate instanceof ActionBar) {
            if ((changeFlags & FLAG_AB_ITEMSCOLOR) != 0) {
                ((ActionBar) viewToInvalidate).setItemsColor(color, false);
            }
            if ((changeFlags & FLAG_AB_TITLECOLOR) != 0) {
                ((ActionBar) viewToInvalidate).setTitleColor(color);
            }
            if ((changeFlags & FLAG_AB_SELECTORCOLOR) != 0) {
                ((ActionBar) viewToInvalidate).setItemsBackgroundColor(color, false);
            }
            if ((changeFlags & FLAG_AB_AM_SELECTORCOLOR) != 0) {
                ((ActionBar) viewToInvalidate).setItemsBackgroundColor(color, true);
            }
            if ((changeFlags & FLAG_AB_AM_ITEMSCOLOR) != 0) {
                ((ActionBar) viewToInvalidate).setItemsColor(color, true);
            }
            if ((changeFlags & FLAG_AB_SUBTITLECOLOR) != 0) {
                ((ActionBar) viewToInvalidate).setSubtitleColor(color);
            }
            if ((changeFlags & FLAG_AB_AM_BACKGROUND) != 0) {
                ((ActionBar) viewToInvalidate).setActionModeColor(color);
            }
            if ((changeFlags & FLAG_AB_AM_TOPBACKGROUND) != 0) {
                ((ActionBar) viewToInvalidate).setActionModeTopColor(color);
            }
            if ((changeFlags & FLAG_AB_SEARCHPLACEHOLDER) != 0) {
                ((ActionBar) viewToInvalidate).setSearchTextColor(color, true);
            }
            if ((changeFlags & FLAG_AB_SEARCH) != 0) {
                ((ActionBar) viewToInvalidate).setSearchTextColor(color, false);
            }
            if ((changeFlags & FLAG_AB_SUBMENUITEM) != 0) {
                ((ActionBar) viewToInvalidate).setPopupItemsColor(color, (changeFlags & FLAG_IMAGECOLOR) != 0, false);
            }
            if ((changeFlags & FLAG_AB_SUBMENUBACKGROUND) != 0) {
                ((ActionBar) viewToInvalidate).setPopupBackgroundColor(color, false);
            }
        }
    }
}
