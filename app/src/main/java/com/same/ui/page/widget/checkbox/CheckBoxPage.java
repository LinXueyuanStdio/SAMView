package com.same.ui.page.widget.checkbox;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.checkbox.CheckBox;
import com.same.lib.checkbox.CheckBox2;
import com.same.lib.checkbox.CheckBoxSquare;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.radiobutton.RadioButton;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.ui.R;
import com.same.ui.lang.MyLang;
import com.same.ui.page.base.BaseActionBarPage;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class CheckBoxPage extends BaseActionBarPage {
    @Override
    protected String title() {
        return MyLang.getString("CheckBoxPageName", R.string.CheckBoxPageName);
    }

    @Override
    protected void fillInContainerLayout(Context context, LinearLayout containerLayout) {

        CheckBox checkBox = new CheckBox(context, R.drawable.ic_baseline_check_24);
        checkBox.setColor(Theme.getColor(KeyHub.key_checkbox), Theme.getColor(KeyHub.key_checkboxCheck));
        containerLayout.addView(checkBox, LayoutHelper.createFrame(22, 22, Gravity.RIGHT | Gravity.TOP, 0, 2, 2, 0));
        checkBox.setChecked(true, true);
        checkBox.setVisibility(View.VISIBLE);

        Button check = createButton(context, "check", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked(), true);
            }
        });
        containerLayout.addView(check);

        for (int i = 0; i < 10; i++) {
            CheckBox2 checkBox2 = new CheckBox2(context);
            checkBox2.setColor(Theme.getColor(KeyHub.key_checkbox), Theme.getColor(KeyHub.key_checkboxCheck), Theme.getColor(KeyHub.key_checkboxCheck));
            checkBox2.setDrawBackgroundAsArc(i);
            checkBox2.setDrawUnchecked(true);
            containerLayout.addView(checkBox2, LayoutHelper.createFrame(22, 22, Gravity.RIGHT | Gravity.TOP, 0, 2, 2, 0));
            checkBox2.setChecked(true, true);
            checkBox2.setVisibility(View.VISIBLE);

            containerLayout.addView(createButton(context, "check", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox2.setChecked(!checkBox2.isChecked(), true);
                }
            }));
        }

        CheckBoxSquare checkBox3 = new CheckBoxSquare(context, true);
        //        checkBox3.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
        containerLayout.addView(checkBox3, LayoutHelper.createFrame(22, 22, Gravity.RIGHT | Gravity.TOP, 0, 2, 2, 0));
        checkBox3.setChecked(true, true);
        checkBox3.setVisibility(View.VISIBLE);
        containerLayout.addView(createButton(context, "check", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox3.setChecked(!checkBox3.isChecked(), true);
            }
        }));

        RadioButton button = new RadioButton(context);
        button.setSize(AndroidUtilities.dp(20));
        containerLayout.addView(button, LayoutHelper.createFrame(22, 22, Gravity.RIGHT | Gravity.TOP, 0, 2, 2, 0));
        containerLayout.addView(createButton(context, "check", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setChecked(!button.isChecked(), true);
            }
        }));
    }
}
