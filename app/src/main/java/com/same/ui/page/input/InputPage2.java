package com.same.ui.page.input;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.util.Keyboard;
import com.same.lib.util.Space;
import com.same.ui.page.base.BaseActionBarPage;
import com.same.ui.page.input.enter.EditTextCaption;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/3/12
 * @description null
 * @usage null
 */
public class InputPage2 extends BaseActionBarPage {

    @Override
    protected String title() {
        return "null";
    }

    @Override
    protected void fillInContainerLayout(Context context, LinearLayout containerLayout) {
        EditTextCaption messageEditText = new EditTextCaption(context);
        messageEditText.setFocusable(true);
        messageEditText.setFocusableInTouchMode(true);
        messageEditText.setSingleLine(false);
        messageEditText.setMaxLines(6);
        messageEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        messageEditText.setGravity(Gravity.BOTTOM);
        messageEditText.setPadding(0, Space.dp(11), 0, Space.dp(12));
        messageEditText.setBackgroundDrawable(null);
        containerLayout.addView(messageEditText, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM, 52, 0, 50, 0));
        Keyboard.showKeyboard(messageEditText);
        messageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Keyboard.showKeyboard(messageEditText);
            }
        });
    }
}
