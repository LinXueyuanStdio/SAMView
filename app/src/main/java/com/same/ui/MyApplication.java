package com.same.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;

import com.same.lib.util.AndroidUtilities;
import com.same.ui.lang.MyLang;

import androidx.annotation.NonNull;

public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        applicationHandler = new Handler(applicationContext.getMainLooper());
        MyLang.init(applicationContext);
        AndroidUtilities.init(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MyLang.onConfigurationChanged(newConfig);
        AndroidUtilities.checkDisplaySize(applicationContext, newConfig);
    }
}
