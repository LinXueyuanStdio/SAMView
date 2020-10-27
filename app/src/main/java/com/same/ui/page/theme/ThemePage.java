package com.same.ui.page.theme;

import com.same.lib.core.BasePage;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.theme.ThemeManager;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class ThemePage extends BasePage {
    @Override
    public boolean onFragmentCreate() {
        if (parentLayout != null) {
            ThemeInfo themeInfo = Theme.themes.get(0);
            ThemeManager.applyTheme(themeInfo);
            parentLayout.rebuildAllFragmentViews(true, true);
            new ThemeEditorView().show(getParentActivity(), themeInfo);
        }
        return super.onFragmentCreate();
    }
}
