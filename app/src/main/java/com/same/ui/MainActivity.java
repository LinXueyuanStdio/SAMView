package com.same.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.SharedConfig;
import com.same.lib.core.BasePage;
import com.same.lib.core.ContainerLayout;
import com.same.lib.same.ContainerCreator;
import com.same.lib.same.theme.dialog.AlertDialog;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.util.Space;
import com.same.ui.lang.MyLang;
import com.same.ui.page.main.MainPage;

public class MainActivity extends Activity
        implements ContainerLayout.ActionBarLayoutDelegate, ContainerCreator.ContextDelegate {
    ContainerCreator creator;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region onCreate前
        creator = new ContainerCreator(this, this);
        creator.onPreCreate();
        setTheme(R.style.Theme_TMessages);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                setTaskDescription(new ActivityManager.TaskDescription(null, null, Theme.getColor(KeyHub.key_actionBarDefault) | 0xff000000));
            } catch (Exception ignore) {

            }
            try {
                getWindow().setNavigationBarColor(0xff000000);
            } catch (Exception ignore) {

            }
        }

        getWindow().setBackgroundDrawable(new ColorDrawable(0xffffffff) {
            @Override
            public void setBounds(int left, int top, int right, int bottom) {
                bottom += Space.dp(500);
                super.setBounds(left, top, right, bottom);
            }

            @Override
            public void draw(Canvas canvas) {
                if (SharedConfig.smoothKeyboard) {
                    int color = getColor();
                    int newColor = Theme.getColor(KeyHub.key_windowBackgroundWhite);
                    if (color != newColor) {
                        setColor(newColor);
                    }
                    super.draw(canvas);
                }
            }
        });

        //endregion
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 24) {
            //适配分屏
            AndroidUtilities.isInMultiwindow = isInMultiWindowMode();
        }
        FrameLayout frameLayout = new FrameLayout(this);
        setContentView(frameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        MainPage page = new MainPage();
        creator.onCreateView(frameLayout, page);

        if (Space.isTablet()) {
            //适配平板
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        checkSystemBarColors();

        //适配各种国产 ROM
        try {
            String os1 = Build.DISPLAY;
            String os2 = Build.USER;
            if (os1 != null) {
                os1 = os1.toLowerCase();
            } else {
                os1 = "";
            }
            if (os2 != null) {
                os2 = os1.toLowerCase();
            } else {
                os2 = "";
            }
            if (os1.contains("flyme") || os2.contains("flyme")) {
                AndroidUtilities.incorrectDisplaySizeFix = true;
                final View view = getWindow().getDecorView().getRootView();
                view.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener = () -> {
                    int height = view.getMeasuredHeight();
                    if (Build.VERSION.SDK_INT >= 21) {
                        height -= AndroidUtilities.statusBarHeight;
                    }
                    if (height > Space.dp(100) && height < AndroidUtilities.displaySize.y && height + Space.dp(100) > AndroidUtilities.displaySize.y) {
                        AndroidUtilities.displaySize.y = height;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkSystemBarColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int color = Theme.getColor(KeyHub.key_actionBarDefault, null, true);
            AndroidUtilities.setLightStatusBar(getWindow(), color == Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final Window window = getWindow();
                color = Theme.getColor(KeyHub.key_windowBackgroundGray, null, true);
                if (window.getNavigationBarColor() != color) {
                    window.setNavigationBarColor(color);
                    final float brightness = AndroidUtilities.computePerceivedBrightness(color);
                    AndroidUtilities.setLightNavigationBar(getWindow(), brightness >= 0.721f);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        creator.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        creator.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            if (onGlobalLayoutListener != null) {
                final View view = getWindow().getDecorView().getRootView();
                view.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        creator.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        creator.onPreActivityResult();
        super.onActivityResult(requestCode, resultCode, data);
        creator.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        creator.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showPermissionErrorAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(MyLang.getString("AppName", R.string.AppName));
        builder.setMessage(message);
        builder.setNegativeButton(MyLang.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), (dialog, which) -> {
            try {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + MyLang.getContext().getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        builder.setPositiveButton(MyLang.getString("OK", R.string.OK), null);
        builder.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        creator.onPreConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
        creator.onPostConfigurationChanged(newConfig);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        AndroidUtilities.isInMultiwindow = isInMultiWindowMode;
        creator.checkLayout();
    }

    @Override
    public ContainerLayout getContainerLayout() {
        return creator.getContainerLayout();
    }

    @Override
    public ContainerLayout getRightActionBarLayout() {
        return creator.getRightActionBarLayout();
    }

    @Override
    public ContainerLayout getLayersActionBarLayout() {
        return creator.getLayersActionBarLayout();
    }

    private void didSetNewTheme(Boolean nightTheme) {
        creator.didSetNewTheme(nightTheme);
        if (!nightTheme) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    setTaskDescription(new ActivityManager.TaskDescription(null, null, Theme.getColor(KeyHub.key_actionBarDefault) | 0xff000000));
                } catch (Exception ignore) {

                }
            }
        }
        checkSystemBarColors();
    }

    private void needSetDayNightTheme(ThemeInfo theme, boolean nigthTheme, int[] pos, int accentId) {
        creator.needSetDayNightTheme(theme, nigthTheme, pos, accentId);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            creator.onSaveInstanceState(outState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        creator.onBackPressed();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        creator.onLowMemory();
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        creator.onActionModeStarted(mode);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        creator.onActionModeFinished(mode);
    }

    @Override
    public boolean onPreIme() {
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        creator.onKeyUp(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean needPresentFragment(BasePage fragment, boolean removeLast, boolean forceWithoutAnimation, ContainerLayout layout) {
        return creator.needPresentFragment(fragment, removeLast, forceWithoutAnimation, layout);
    }

    @Override
    public boolean needAddFragmentToStack(BasePage fragment, ContainerLayout layout) {
        return creator.needAddFragmentToStack(fragment, layout);
    }

    @Override
    public boolean needCloseLastFragment(ContainerLayout layout) {
        return creator.needCloseLastFragment(layout);
    }

    @Override
    public void rebuildAllFragments(boolean last) {
        creator.rebuildAllFragments(last);
    }

    @Override
    public void onRebuildAllFragments(ContainerLayout layout, boolean last) {
        creator.onRebuildAllFragments(layout, last);
    }

    @Override
    public Configuration getConfiguration() {
        return getResources().getConfiguration();
    }

    @Override
    public void stopSelf() {
        finish();
    }
}
