package com.same.ui.page.base;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.same.lib.core.BasePage;
import com.same.lib.core.ThemeDescription;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.MyThemeDescription;
import com.same.lib.theme.Theme;
import com.same.lib.theme.wrap.ThemeBasePage;
import com.same.lib.util.Space;
import com.same.ui.R;
import com.same.ui.theme.dialog.AlertDialog;
import com.same.ui.theme.dialog.BottomSheet;

import java.util.ArrayList;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public abstract class BaseActionBarPage extends ThemeBasePage {
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_baseline_menu_24);
        actionBar.setAllowOverlayTitle(false);
        if (Space.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setTitle(title());

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(KeyHub.key_windowBackgroundGray));
        fragmentView = frameLayout;

        LinearLayout containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        fillInContainerLayout(context, containerLayout);

        ScrollView scrollView = new ScrollView(context);
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
        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);
        button.setText(text);
        button.setOnClickListener(clickListener);
        return button;
    }
    protected Button createButton(Context context, String text, BasePage page) {
        return createButton(context, text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentFragment(page);
            }
        });
    }

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
