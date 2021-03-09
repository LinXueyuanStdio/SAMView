package com.same.ui.page.theme;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.same.ui.page.base.BaseActionBarPage;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/3/9
 * @description null
 * @usage null
 */
public class TestPage extends BaseActionBarPage {
    @Override
    protected String title() {
        return "test";
    }

    @Override
    protected void fillInContainerLayout(Context context, LinearLayout containerLayout) {
        layoutButtons = new TextView(context);
        layoutButtons.setText("hhh");
        layoutButtons.setBackgroundColor(Color.RED);

        fab = new TextView(context);
        fab.setText("hhh");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMenu();
//                fab.animate().scaleX(0.5f).start();
            }
        });
        containerLayout.addView(fab);
        containerLayout.addView(layoutButtons);
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

            Animator anim = ViewAnimationUtils.createCircularReveal(fab, x, y, startRadius, endRadius);
            anim.setDuration(4000);

            layoutButtons.setVisibility(View.VISIBLE);
            anim.start();

            isOpen = true;

        } else {

            int x = 0;
            int y = 0;

            int startRadius = 1000;
            int endRadius = 0;

            Animator anim = ViewAnimationUtils.createCircularReveal(fab, x, y, startRadius, endRadius);
            anim.setDuration(4000);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    layoutButtons.setVisibility(View.GONE);
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
