package com.same.ui.page.main;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.same.ui.R;
import com.same.ui.intro.IntroActivity;
import com.same.ui.lang.MyLang;
import com.same.ui.page.base.BaseActionBarPage;
import com.same.ui.page.language.LanguageSelectPage;
import com.same.ui.page.theme.ThemePage;
import com.same.ui.page.widget.checkbox.CheckBoxPage;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class MainPage extends BaseActionBarPage {
    @Override
    protected String title() {
        return MyLang.getString("SAMView", R.string.SAMView);
    }

    @Override
    protected void fillInContainerLayout(Context context, LinearLayout containerLayout) {
        containerLayout.addView(createButton(context, "intro", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getParentActivity(), IntroActivity.class);
                getParentActivity().startActivity(intent);
            }
        }));
        containerLayout.addView(createButton(context, "选择语言", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentFragment(new LanguageSelectPage());
            }
        }));
        containerLayout.addView(createButton(context, "选择主题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentFragment(new ThemePage());
            }
        }));
        containerLayout.addView(createButton(context, "CheckBox", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentFragment(new CheckBoxPage());
            }
        }));
    }
}
