package com.same.ui;

import android.app.Application;
import android.content.Context;

import com.same.lib.util.AndroidUtilities;

public class MyApplication extends Application{
    public static Context sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        AndroidUtilities.init(this);
    }
}
