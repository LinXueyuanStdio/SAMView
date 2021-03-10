package com.same.lib.same.theme;

import android.content.res.Configuration;

import com.same.lib.core.ThemeDescription;
import com.same.lib.same.theme.dialog.AlertDialog;
import com.same.lib.same.theme.dialog.BottomSheet;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.MyThemeDescription;
import com.same.lib.theme.wrap.BaseThemePage;

import java.util.ArrayList;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/3/10
 * @description null
 * @usage null
 */
public class BaseSamePage extends BaseThemePage {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (visibleDialog instanceof BottomSheet) {
            ((BottomSheet) visibleDialog).onConfigurationChanged(newConfig);
        }
    }

    @Override
    public ArrayList<ThemeDescription> getAllThemeDescriptions() {
        ArrayList<ThemeDescription> descriptions = getThemeDescriptions();
        if (visibleDialog instanceof BottomSheet) {
            BottomSheet sheet = (BottomSheet) visibleDialog;
            descriptions.addAll(sheet.getThemeDescriptions());
        } else if (visibleDialog instanceof AlertDialog) {
            AlertDialog dialog = (AlertDialog) visibleDialog;
            descriptions.addAll(dialog.getThemeDescriptions());
        }
        return super.getAllThemeDescriptions();
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> d = new ArrayList<>();

        d.add(new MyThemeDescription(fragmentView, MyThemeDescription.FLAG_BACKGROUND, null, null, null, null, KeyHub.key_windowBackgroundGray));

        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_BACKGROUND, null, null, null, null, KeyHub.key_actionBarDefault));
        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, KeyHub.key_actionBarDefaultIcon));
        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, KeyHub.key_actionBarDefaultTitle));
        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, KeyHub.key_actionBarDefaultSelector));
        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_AB_SEARCH, null, null, null, null, KeyHub.key_actionBarDefaultSearch));
        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, KeyHub.key_actionBarDefaultSearchPlaceholder));
        return d;
    }
}
