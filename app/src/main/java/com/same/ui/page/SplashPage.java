package com.same.ui.page;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.same.lib.core.BasePage;
import com.same.ui.R;
import com.same.ui.TestActivity;


public class SplashPage extends BasePage {

    @Override
    protected int getLayoutId() {
        return R.layout.segment_splash;
    }

    @Override
    protected void initView(View root) {
        super.initView(root);
        root.findViewById(R.id.btn_login_in_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long t1 = System.currentTimeMillis();
                presentFragment(new LoginPage());
                Log.e("time","time=" + (System.currentTimeMillis() - t1));
            }
        });

        root.findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long t1 = System.currentTimeMillis();
                parentLayout.startActivityForResult(new Intent(getParentActivity(),TestActivity.class),100);
                Log.e("time","time2=" + (System.currentTimeMillis() - t1));
            }
        });

    }

    @Override
    public boolean onFragmentCreate() {
        Log.d(TAG,"onFragmentCreate...");
        return super.onFragmentCreate();
    }

    @Override
    public View createView(Context context) {
        Log.d(TAG,"createView...");
        return super.createView(context);
    }

    @Override
    protected void onRemoveFromParent() {
        super.onRemoveFromParent();
        Log.d(TAG,"onRemoveFromParent...");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause...");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume...");
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        Log.d(TAG,"onFragmentDestroy...");
    }
}
