package com.same.ui.page.widget.checkbox;

import android.content.Context;
import android.widget.LinearLayout;

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

    }
}
