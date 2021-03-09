package com.same.ui.page.main;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.same.ui.R;
import com.same.ui.intro.IntroActivity;
import com.same.ui.lang.MyLang;
import com.same.ui.page.base.BaseActionBarPage;
import com.same.ui.page.language.LanguageSelectPage;
import com.same.ui.page.service.ServicePage;
import com.same.ui.page.theme.TestActivity;
import com.same.ui.page.theme.TestPage;
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
                Activity activity = getParentActivity();
                if (activity == null) { return; }
                Intent intent = new Intent(activity, IntroActivity.class);
                activity.startActivity(intent);
            }
        }));
        containerLayout.addView(createButton(context, "test", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getParentActivity();
                if (activity == null) { return; }
                Intent intent = new Intent(activity, TestActivity.class);
                activity.startActivity(intent);
            }
        }));
        View layoutContent = createButton(context, "t", new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        layoutContent.setBackgroundColor(Color.RED);
//        test.setVisibility(View.INVISIBLE);
        containerLayout.addView(layoutContent);
        containerLayout.addView(createButton(context, "test2", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int x = layoutContent.getRight();
                int y = layoutContent.getBottom();

                int startRadius = 0;
                int endRadius = (int) Math.hypot(containerLayout.getWidth(), containerLayout.getHeight());

                Animator anim = ViewAnimationUtils.createCircularReveal(layoutContent, x, y, startRadius, endRadius);

                layoutContent.setVisibility(View.VISIBLE);
                anim.start();

//                Animator a = ViewAnimationUtils.createCircularReveal(layoutContent, (int)layoutContent.getX(),(int)layoutContent.getY(), 0, 1000);
//                a.setDuration(4000);
//                a.start();
            }
        }));

        layoutButtons = new TextView(context);
        layoutButtons.setText("hhh");
        layoutButtons.setBackgroundColor(Color.RED);

        fab = new TextView(context);
        fab.setText("hhh");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMenu();
            }
        });
        containerLayout.addView(fab);
        containerLayout.addView(layoutButtons);
        layoutButtons.setClipToOutline(true);
        fab.setClipToOutline(true);
        containerLayout.addView(createButton(context, "测试", new TestPage()));
        containerLayout.addView(createButton(context, "选择语言", new LanguageSelectPage()));
        containerLayout.addView(createButton(context, "选择主题", new ThemePage()));
        containerLayout.addView(createButton(context, "CheckBox", new CheckBoxPage()));
        containerLayout.addView(createButton(context, "Service", new ServicePage()));
    }
    private TextView fab;
    private TextView layoutButtons;
    private boolean isOpen = false;
    private void viewMenu() {

        if (!isOpen) {

            int x = 0;
            int y = 0;

            int startRadius = 0;
            int endRadius = 1000;

            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);

            layoutButtons.setVisibility(View.VISIBLE);
            anim.start();

            isOpen = true;

        } else {

            int x = 0;
            int y = 0;

            int startRadius = 1000;
            int endRadius = 0;

            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    layoutButtons.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.start();

            isOpen = false;
        }
    }
}
