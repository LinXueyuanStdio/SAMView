package com.same.ui.page.theme;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.same.lib.core.AlertDialog;
import com.same.lib.core.BasePage;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.theme.ThemeManager;
import com.same.lib.util.AndroidUtilities;
import com.same.lib.util.NotificationCenter;
import com.same.ui.R;
import com.same.ui.lang.MyLang;
import com.same.ui.page.language.LanguageSelectPage;

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

        return super.onFragmentCreate();
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_baseline_arrow_back_ios_24);
        actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setTitle(MyLang.getString("AutoNightTheme", R.string.AutoNightTheme));

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        fragmentView = frameLayout;

        LinearLayout containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(LinearLayout.VERTICAL);

        Button language = new Button(context);
        language.setPadding(20, 20, 20, 20);
        language.setTextSize(18);
        language.setGravity(Gravity.CENTER);
        language.setText("选择语言");
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentFragment(new LanguageSelectPage());
            }
        });
        containerLayout.addView(language);

        Button create = new Button(context);
        create.setPadding(20, 20, 20, 20);
        create.setTextSize(18);
        create.setGravity(Gravity.CENTER);
        create.setText("创建新主题");
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(MyLang.getString("NewTheme", R.string.NewTheme));
                builder.setMessage(MyLang.getString("CreateNewThemeAlert", R.string.CreateNewThemeAlert));
                builder.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
                builder.setPositiveButton(MyLang.getString("CreateTheme", R.string.CreateTheme), (dialog, which) -> {
                    if (parentLayout != null) {
                        ThemeInfo themeInfo = ThemeManager.createNewTheme(getParentActivity(),"新主题的名字");
                        ThemeManager.applyTheme(getParentActivity(), themeInfo);
                        parentLayout.rebuildAllFragmentViews(true, true);
                        new ThemeEditorView().show(getParentActivity(), themeInfo);
                    }
                });
                showDialog(builder.create());
            }
        });
        containerLayout.addView(create);

        Button reset = new Button(context);
        reset.setPadding(20, 20, 20, 20);
        reset.setTextSize(18);
        reset.setGravity(Gravity.CENTER);
        reset.setText("重置主题");
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getParentActivity());
                builder1.setTitle(MyLang.getString("ThemeResetToDefaultsTitle", R.string.ThemeResetToDefaultsTitle));
                builder1.setMessage(MyLang.getString("ThemeResetToDefaultsText", R.string.ThemeResetToDefaultsText));
                builder1.setPositiveButton(MyLang.getString("Reset", R.string.Reset), (dialogInterface, i) -> {
                        ThemeInfo themeInfo = ThemeManager.getTheme("Blue");
                        ThemeInfo currentTheme = ThemeManager.getCurrentTheme();
                        if (themeInfo != currentTheme) {
                            themeInfo.setCurrentAccentId(Theme.DEFALT_THEME_ACCENT_ID);
                            ThemeManager.saveThemeAccents(getParentActivity(), themeInfo, true, false, true, false);
                        } else if (themeInfo.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
                            NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme);
                    }
                });
                builder1.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder1.create();
                showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }
        });
        containerLayout.addView(reset);

        frameLayout.addView(containerLayout);

        return fragmentView;
    }
}
