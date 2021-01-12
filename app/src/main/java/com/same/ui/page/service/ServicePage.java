package com.same.ui.page.service;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.same.ui.MainService;
import com.same.ui.R;
import com.same.ui.lang.MyLang;
import com.same.ui.page.base.BaseActionBarPage;
import com.timecat.show.window.WindowAgreement;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/1/12
 * @description null
 * @usage null
 */
public class ServicePage extends BaseActionBarPage {
    @Override
    protected String title() {
        return MyLang.getString("app_name", R.string.app_name);
    }

    @Override
    protected void fillInContainerLayout(Context context, LinearLayout containerLayout) {
        containerLayout.addView(createButton(context, "启动", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowAgreement.show(context, MainService.class, 100);
            }
        }));
    }
}
