package com.same.ui.page;

import android.view.View;

import com.same.lib.core.BasePage;
import com.same.ui.R;

public class DetailPage extends BasePage implements View.OnClickListener{
    @Override
    protected int getLayoutId() {
        return R.layout.segment_detail_layout;
    }

    @Override
    protected void initView(View root) {
        super.initView(root);
        root.findViewById(R.id.btn_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                presentFragment(new SplashPage(),true);
                break;
        }
    }
}
