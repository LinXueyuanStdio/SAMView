package com.same.ui.page.base;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.core.BasePage;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeDescription;
import com.same.ui.R;

import java.util.ArrayList;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public abstract class BaseActionBarPage extends BasePage {
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_baseline_menu_24);
        actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setTitle(title());

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(KeyHub.key_windowBackgroundGray));
        fragmentView = frameLayout;

        LinearLayout containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        fillInContainerLayout(context, containerLayout);

        ScrollView scrollView = new ScrollView(getParentActivity());
        scrollView.addView(containerLayout);
        frameLayout.addView(scrollView);

        return fragmentView;
    }

    protected abstract String title();

    protected abstract void fillInContainerLayout(Context context, LinearLayout containerLayout);

    protected Button createButton(Context context, String text, View.OnClickListener clickListener) {
        Button button = new Button(context);
        button.setPadding(20, 20, 20, 20);
        button.setTextSize(18);
        button.setGravity(Gravity.CENTER);
        button.setText(text);
        button.setOnClickListener(clickListener);
        return button;
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> d = new ArrayList<>();

        d.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, KeyHub.key_windowBackgroundGray));

        d.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, KeyHub.key_actionBarDefault));
        d.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, KeyHub.key_actionBarDefaultIcon));
        d.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, KeyHub.key_actionBarDefaultTitle));
        d.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, KeyHub.key_actionBarDefaultSelector));
        d.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, KeyHub.key_actionBarDefaultSearch));
        d.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, KeyHub.key_actionBarDefaultSearchPlaceholder));
        return d;
    }
}