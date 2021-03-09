package com.same.ui.page.theme;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.same.ui.R;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/3/9
 * @description null
 * @usage null
 */
public class TestActivity extends AppCompatActivity {
    private TextView fab;
    private TextView layoutButtons;
    private boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light);
        super.onCreate(savedInstanceState);
        FrameLayout f = new FrameLayout(this);
        LinearLayout f2 = new LinearLayout(this);
        f2.setOrientation(LinearLayout.VERTICAL);
        f.addView(f2);
        setContentView(f);

        layoutButtons = new TextView(this);
        layoutButtons.setText("hhh");
        layoutButtons.setBackgroundColor(Color.RED);

        fab = new TextView(this);
        fab.setText("hhh");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMenu();
            }
        });
        f2.addView(fab);
        f2.addView(layoutButtons);
    }

    private void viewMenu() {

        if (!isOpen) {

            int x = 0;
            int y = 0;

            int startRadius = 0;
            int endRadius = 1000;

            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);
            anim.setDuration(4000);

            layoutButtons.setVisibility(View.VISIBLE);
            anim.start();

            isOpen = true;

        } else {

            int x = 0;
            int y = 0;

            int startRadius = 1000;
            int endRadius = 0;

            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);
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
