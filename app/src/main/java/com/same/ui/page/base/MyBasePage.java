package com.same.ui.page.base;

import android.content.Context;

import com.same.lib.core.ActionBar;
import com.same.lib.core.BasePage;
import com.same.lib.util.ColorManager;
import com.same.lib.util.KeyHub;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/1/15
 * @description null
 * @usage null
 */
public class MyBasePage extends BasePage {
    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackgroundColor(ColorManager.getColor(KeyHub.key_actionBarDefault));
        actionBar.setItemsBackgroundColor(ColorManager.getColor(KeyHub.key_actionBarDefaultSelector), false);
        actionBar.setItemsBackgroundColor(ColorManager.getColor(KeyHub.key_actionBarActionModeDefaultSelector), true);
        actionBar.setItemsColor(ColorManager.getColor(KeyHub.key_actionBarDefaultIcon), false);
        actionBar.setItemsColor(ColorManager.getColor(KeyHub.key_actionBarActionModeDefaultIcon), true);
        if (inPreviewMode) {
            actionBar.setOccupyStatusBar(false);
        }
        return actionBar;
    }
}
