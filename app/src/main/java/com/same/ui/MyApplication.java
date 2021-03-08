package com.same.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.checkbox.CheckboxFont;
import com.same.lib.font.FontManager;
import com.same.lib.intro.IntroLoader;
import com.same.lib.lottie.NativeLoader;
import com.same.lib.same.theme.delegate.ColorDelegateLoader;
import com.same.lib.theme.Theme;
import com.same.lib.util.ColorManager;
import com.same.lib.util.Font;
import com.same.lib.util.Lang;
import com.same.lib.util.Space;
import com.same.lib.util.UIThread;
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
        NativeLoader.initNativeLibs(this);
        IntroLoader.initNativeLibs(this);
        MyLang.init(applicationContext);
        ColorDelegateLoader.init();
        AndroidUtilities.init(this);
        UIThread.init(this);
        ColorManager.install(new ColorManager.ColorEngine() {
            @Override
            public int getColor(String key) {
                return Theme.getColor(key);
            }
        });
        Lang.install(new Lang.ILang() {
            @Override
            public String getString(String key, int string) {
                return MyLang.getString(key, string);
            }

            @Override
            public String getTranslitString(String src) {
                return MyLang.getInstance().getTranslitString(src);
            }

            @Override
            public String formatPluralString(String key, int plural) {
                return MyLang.formatPluralString(key, plural);
            }

            @Override
            public String formatString(String key, int res, Object... args) {
                return MyLang.formatString(key, res, args);
            }
        });
        Font.install((context, assetPath) -> FontManager.getMediumTypeface(context));
        CheckboxFont.install((context, assetPath) -> FontManager.getMediumTypeface(context));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MyLang.onConfigurationChanged(newConfig);
        AndroidUtilities.checkDisplaySize(applicationContext, newConfig);
        Space.checkDisplaySize(applicationContext, newConfig);
        Theme.onConfigurationChanged(applicationContext, newConfig);
    }
}
