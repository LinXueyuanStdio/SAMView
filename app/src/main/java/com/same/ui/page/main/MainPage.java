package com.same.ui.page.main;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        final TextView detail = new TextView(context);
        detail.setPadding(20, 20, 20, 20);
        detail.setTextSize(18);
        detail.setGravity(Gravity.CENTER);
        String detailText = "语言详情"
                + "\n"
                + "当前语言设置：" + MyLang.loadLanguageKeyInLocal()
                + "\n"
                + "当前语言的英语名：" + MyLang.getString("LanguageNameInEnglish", R.string.LanguageNameInEnglish)
                + "\n\n本地缺失，云端存在的字符串：\n"
                + MyLang.getString("remote_string_only", R.string.fallback_string)
                + "\n\n本地云端都存在，云端将覆盖本地的字符串：\n"
                + MyLang.getString("local_string", R.string.local_string);
        detail.setText(detailText);
        containerLayout.addView(detail);
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
