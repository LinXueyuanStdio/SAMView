package com.same.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.same.lib.anim.Easings;
import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;
import com.same.lib.base.SharedConfig;
import com.same.lib.core.BasePage;
import com.same.lib.core.ContainerLayout;
import com.same.lib.core.DrawerLayoutContainer;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.same.theme.ChatTheme;
import com.same.lib.same.theme.CommonTheme;
import com.same.lib.same.theme.DialogTheme;
import com.same.lib.same.theme.ProfileTheme;
import com.same.lib.same.theme.ThemeEditorView;
import com.same.lib.same.theme.dialog.AlertDialog;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.theme.ThemeManager;
import com.same.lib.theme.ThemeRes;
import com.same.lib.theme.wrap.ThemeContainerLayout;
import com.same.lib.util.Space;
import com.same.ui.lang.MyLang;
import com.same.ui.page.main.MainPage;

import java.util.ArrayList;

public class MainActivity extends Activity
        implements ContainerLayout.ActionBarLayoutDelegate, ThemeEditorView.ThemeContainer {
    /**
     * 主容器
     */
    private ThemeContainerLayout actionBarLayout;
    /**
     * 适配平板的容器
     */
    private ThemeContainerLayout layersActionBarLayout;
    /**
     * 适配平板的容器
     */
    private ThemeContainerLayout rightActionBarLayout;
    private FrameLayout shadowTablet;
    private FrameLayout shadowTabletSide;
    private View backgroundTablet;
    private boolean tabletFullSize;

    private static ArrayList<BasePage> mainFragmentsStack = new ArrayList<>();
    private static ArrayList<BasePage> layerFragmentsStack = new ArrayList<>();
    private static ArrayList<BasePage> rightFragmentsStack = new ArrayList<>();
    private ActionMode visibleActionMode;
    private DrawerLayoutContainer drawerLayoutContainer;
    private ImageView themeSwitchImageView;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private NotificationCenter.NotificationCenterDelegate notificationCenterDelegate = new NotificationCenter.NotificationCenterDelegate() {
        @Override
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.didSetNewTheme) {
                didSetNewTheme((Boolean) args[0]);
            } else if (id == NotificationCenter.needSetDayNightTheme) {
                ThemeInfo theme = (ThemeInfo) args[0];
                boolean nigthTheme = theme.isDark();
                int[] pos = null;
                int accentId = theme.currentAccentId;
                needSetDayNightTheme(theme, nigthTheme, pos, accentId);
            } else if (id == NotificationCenter.needCheckSystemBarColors) {
                checkSystemBarColors();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region onCreate前
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        Space.checkDisplaySize(this, getResources().getConfiguration());
        Theme.onConfigurationChanged(this, getResources().getConfiguration());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        //获取状态栏高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        ThemeRes.installAndApply(this, new CommonTheme(), new DialogTheme(), new ChatTheme(), new ProfileTheme());

        actionBarLayout = new ThemeContainerLayout(this) {
            @Override
            public void setThemeAnimationValue(float value) {
                super.setThemeAnimationValue(value);
                drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(KeyHub.key_windowBackgroundWhite));
            }
        };
        FrameLayout frameLayout = new FrameLayout(this);
        setContentView(frameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= 21) {
            themeSwitchImageView = new ImageView(this);
            themeSwitchImageView.setVisibility(View.GONE);
            frameLayout.addView(themeSwitchImageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        }

        drawerLayoutContainer = new DrawerLayoutContainer(this);
        drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(KeyHub.key_windowBackgroundWhite));
        frameLayout.addView(drawerLayoutContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        if (Space.isTablet()) {
            //适配平板
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            RelativeLayout launchLayout = new RelativeLayout(this) {

                private boolean inLayout;

                @Override
                public void requestLayout() {
                    if (inLayout) {
                        return;
                    }
                    super.requestLayout();
                }

                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    inLayout = true;
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    setMeasuredDimension(width, height);

                    if (!Space.isInMultiwindow && (!Space.isSmallTablet() || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
                        tabletFullSize = false;
                        int leftWidth = width / 100 * 35;
                        if (leftWidth < Space.dp(320)) {
                            leftWidth = Space.dp(320);
                        }
                        actionBarLayout.measure(MeasureSpec.makeMeasureSpec(leftWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                        shadowTabletSide.measure(MeasureSpec.makeMeasureSpec(Space.dp(1), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                        rightActionBarLayout.measure(MeasureSpec.makeMeasureSpec(width - leftWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                    } else {
                        tabletFullSize = true;
                        actionBarLayout.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                    }
                    backgroundTablet.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                    shadowTablet.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                    layersActionBarLayout.measure(MeasureSpec.makeMeasureSpec(Math.min(Space.dp(530), width), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Math.min(Space.dp(528), height), MeasureSpec.EXACTLY));

                    inLayout = false;
                }

                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    int width = r - l;
                    int height = b - t;

                    if (!Space.isInMultiwindow && (!Space.isSmallTablet() || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
                        int leftWidth = width / 100 * 35;
                        if (leftWidth < Space.dp(320)) {
                            leftWidth = Space.dp(320);
                        }
                        shadowTabletSide.layout(leftWidth, 0, leftWidth + shadowTabletSide.getMeasuredWidth(), shadowTabletSide.getMeasuredHeight());
                        actionBarLayout.layout(0, 0, actionBarLayout.getMeasuredWidth(), actionBarLayout.getMeasuredHeight());
                        rightActionBarLayout.layout(leftWidth, 0, leftWidth + rightActionBarLayout.getMeasuredWidth(), rightActionBarLayout.getMeasuredHeight());
                    } else {
                        actionBarLayout.layout(0, 0, actionBarLayout.getMeasuredWidth(), actionBarLayout.getMeasuredHeight());
                    }
                    int x = (width - layersActionBarLayout.getMeasuredWidth()) / 2;
                    int y = (height - layersActionBarLayout.getMeasuredHeight()) / 2;
                    layersActionBarLayout.layout(x, y, x + layersActionBarLayout.getMeasuredWidth(), y + layersActionBarLayout.getMeasuredHeight());
                    backgroundTablet.layout(0, 0, backgroundTablet.getMeasuredWidth(), backgroundTablet.getMeasuredHeight());
                    shadowTablet.layout(0, 0, shadowTablet.getMeasuredWidth(), shadowTablet.getMeasuredHeight());
                }
            };
            drawerLayoutContainer.addView(launchLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            backgroundTablet = new View(this);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            backgroundTablet.setBackgroundDrawable(drawable);
            launchLayout.addView(backgroundTablet, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            launchLayout.addView(actionBarLayout);

            rightActionBarLayout = new ThemeContainerLayout(this);
            rightActionBarLayout.init(rightFragmentsStack);
            rightActionBarLayout.setDelegate(this);
            launchLayout.addView(rightActionBarLayout);

            shadowTabletSide = new FrameLayout(this);
            shadowTabletSide.setBackgroundColor(0x40295274);
            launchLayout.addView(shadowTabletSide);

            shadowTablet = new FrameLayout(this);
            shadowTablet.setVisibility(layerFragmentsStack.isEmpty() ? View.GONE : View.VISIBLE);
            shadowTablet.setBackgroundColor(0x7f000000);
            launchLayout.addView(shadowTablet);
            shadowTablet.setOnTouchListener((v, event) -> {
                if (!actionBarLayout.fragmentsStack.isEmpty() && event.getAction() == MotionEvent.ACTION_UP) {
                    float x = event.getX();
                    float y = event.getY();
                    int[] location = new int[2];
                    layersActionBarLayout.getLocationOnScreen(location);
                    int viewX = location[0];
                    int viewY = location[1];

                    if (layersActionBarLayout.checkTransitionAnimation() || x > viewX && x < viewX + layersActionBarLayout.getWidth() && y > viewY && y < viewY + layersActionBarLayout.getHeight()) {
                        return false;
                    } else {
                        if (!layersActionBarLayout.fragmentsStack.isEmpty()) {
                            for (int a = 0; a < layersActionBarLayout.fragmentsStack.size() - 1; a++) {
                                layersActionBarLayout.removeFragmentFromStack(layersActionBarLayout.fragmentsStack.get(0));
                                a--;
                            }
                            layersActionBarLayout.closeLastFragment(true);
                        }
                        return true;
                    }
                }
                return false;
            });

            shadowTablet.setOnClickListener(v -> {

            });

            layersActionBarLayout = new ThemeContainerLayout(this);
            layersActionBarLayout.setRemoveActionBarExtraHeight(true);
            layersActionBarLayout.setBackgroundView(shadowTablet);
            layersActionBarLayout.setUseAlphaAnimations(true);
            layersActionBarLayout.setBackgroundResource(R.drawable.boxshadow);
            layersActionBarLayout.init(layerFragmentsStack);
            layersActionBarLayout.setDelegate(this);
            layersActionBarLayout.setDrawerLayoutContainer(drawerLayoutContainer);
            layersActionBarLayout.setVisibility(layerFragmentsStack.isEmpty() ? View.GONE : View.VISIBLE);
            launchLayout.addView(layersActionBarLayout);
        } else {
            //手机
            drawerLayoutContainer.addView(actionBarLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        //双向绑定
        drawerLayoutContainer.setParentActionBarLayout(actionBarLayout);
        actionBarLayout.setDrawerLayoutContainer(drawerLayoutContainer);
        actionBarLayout.init(mainFragmentsStack);//使用 main fragment 栈
        actionBarLayout.setDelegate(this);//代理，监听生命周期

        //注册通知中心，使用观察者模式处理应用内部的通知（消息）
        NotificationCenter.getGlobalInstance().addObserver(notificationCenterDelegate, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().addObserver(notificationCenterDelegate, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().addObserver(notificationCenterDelegate, NotificationCenter.needCheckSystemBarColors);

        MainPage page = new MainPage();
        //        actionBarLayout.addFragmentToStack(page);
        actionBarLayout.presentFragment(page);

        checkLayout();
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
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4096);
        actionBarLayout.onPause();
        if (Space.isTablet()) {
            rightActionBarLayout.onPause();
            layersActionBarLayout.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4096);
        actionBarLayout.onResume();
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
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (actionBarLayout.fragmentsStack.size() != 0) {
            BasePage fragment = actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1);
            fragment.onActivityResultFragment(requestCode, resultCode, data);
        }
        if (Space.isTablet()) {
            if (rightActionBarLayout.fragmentsStack.size() != 0) {
                BasePage fragment = rightActionBarLayout.fragmentsStack.get(rightActionBarLayout.fragmentsStack.size() - 1);
                fragment.onActivityResultFragment(requestCode, resultCode, data);
            }
            if (layersActionBarLayout.fragmentsStack.size() != 0) {
                BasePage fragment = layersActionBarLayout.fragmentsStack.get(layersActionBarLayout.fragmentsStack.size() - 1);
                fragment.onActivityResultFragment(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults == null) {
            grantResults = new int[0];
        }
        if (permissions == null) {
            permissions = new String[0];
        }

        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (actionBarLayout.fragmentsStack.size() != 0) {
            BasePage fragment = actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1);
            fragment.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
        if (Space.isTablet()) {
            if (rightActionBarLayout.fragmentsStack.size() != 0) {
                BasePage fragment = rightActionBarLayout.fragmentsStack.get(rightActionBarLayout.fragmentsStack.size() - 1);
                fragment.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            }
            if (layersActionBarLayout.fragmentsStack.size() != 0) {
                BasePage fragment = layersActionBarLayout.fragmentsStack.get(layersActionBarLayout.fragmentsStack.size() - 1);
                fragment.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
            }
        }
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
        AndroidUtilities.checkDisplaySize(this, newConfig);
        Theme.onConfigurationChanged(this, newConfig);
        super.onConfigurationChanged(newConfig);
        checkLayout();
        actionBarLayout.onConfigurationChanged(newConfig);
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.onConfigurationChanged();
        }
        if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SYSTEM) {
            ThemeManager.checkAutoNightThemeConditions(this);
        }
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        AndroidUtilities.isInMultiwindow = isInMultiWindowMode;
        checkLayout();
    }

    @Override
    public ContainerLayout getContainerLayout() {
        return actionBarLayout;
    }

    @Override
    public ContainerLayout getRightActionBarLayout() {
        return rightActionBarLayout;
    }

    @Override
    public ContainerLayout getLayersActionBarLayout() {
        return layersActionBarLayout;
    }

    /**
     * 检查布局
     *
     * 只是为了适配平板布局
     */
    private void checkLayout() {
        if (!Space.isTablet() || rightActionBarLayout == null) {
            return;
        }

        if (!AndroidUtilities.isInMultiwindow && (!AndroidUtilities.isSmallTablet() || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            tabletFullSize = false;
            if (actionBarLayout.fragmentsStack.size() >= 2) {
                for (int a = 1; a < actionBarLayout.fragmentsStack.size(); a++) {
                    BasePage chatFragment = actionBarLayout.fragmentsStack.get(a);
                    chatFragment.onPause();
                    actionBarLayout.fragmentsStack.remove(a);
                    rightActionBarLayout.fragmentsStack.add(chatFragment);
                    a--;
                }
            }
            rightActionBarLayout.setVisibility(rightActionBarLayout.fragmentsStack.isEmpty() ? View.GONE : View.VISIBLE);
            backgroundTablet.setVisibility(rightActionBarLayout.fragmentsStack.isEmpty() ? View.VISIBLE : View.GONE);
            shadowTabletSide.setVisibility(!actionBarLayout.fragmentsStack.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            tabletFullSize = true;
            if (!rightActionBarLayout.fragmentsStack.isEmpty()) {
                for (int a = 0; a < rightActionBarLayout.fragmentsStack.size(); a++) {
                    BasePage chatFragment = rightActionBarLayout.fragmentsStack.get(a);
                    chatFragment.onPause();
                    rightActionBarLayout.fragmentsStack.remove(a);
                    actionBarLayout.fragmentsStack.add(chatFragment);
                    a--;
                }
            }
            shadowTabletSide.setVisibility(View.GONE);
            rightActionBarLayout.setVisibility(View.GONE);
            backgroundTablet.setVisibility(!actionBarLayout.fragmentsStack.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void didSetNewTheme(Boolean nightTheme) {
        if (!nightTheme) {
            //            if (sideMenu != null) {
            //                sideMenu.setBackgroundColor(Theme.getColor(KeyHub.key_chats_menuBackground));
            //                sideMenu.setGlowColor(Theme.getColor(KeyHub.key_chats_menuBackground));
            //                sideMenu.setListSelectorColor(Theme.getColor(KeyHub.key_listSelector));
            //                sideMenu.getAdapter().notifyDataSetChanged();
            //            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    setTaskDescription(new ActivityManager.TaskDescription(null, null, Theme.getColor(KeyHub.key_actionBarDefault) | 0xff000000));
                } catch (Exception ignore) {

                }
            }
        }
        drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(KeyHub.key_windowBackgroundWhite));
        checkSystemBarColors();
    }

    private void needSetDayNightTheme(ThemeInfo theme, boolean nigthTheme, int[] pos, int accentId) {
        boolean instant = false;
        if (Build.VERSION.SDK_INT >= 21 && pos != null) {
            if (themeSwitchImageView.getVisibility() == View.VISIBLE) {
                return;
            }
            try {
                int w = drawerLayoutContainer.getMeasuredWidth();
                int h = drawerLayoutContainer.getMeasuredHeight();
                Bitmap bitmap = Bitmap.createBitmap(drawerLayoutContainer.getMeasuredWidth(), drawerLayoutContainer.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawerLayoutContainer.draw(canvas);
                themeSwitchImageView.setImageBitmap(bitmap);
                themeSwitchImageView.setVisibility(View.VISIBLE);
                float finalRadius = (float) Math.max(Math.sqrt((w - pos[0]) * (w - pos[0]) + (h - pos[1]) * (h - pos[1])), Math.sqrt(pos[0] * pos[0] + (h - pos[1]) * (h - pos[1])));
                Animator anim = ViewAnimationUtils.createCircularReveal(drawerLayoutContainer, pos[0], pos[1], 0, finalRadius);
                anim.setDuration(400);
                anim.setInterpolator(Easings.easeInOutQuad);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        themeSwitchImageView.setImageDrawable(null);
                        themeSwitchImageView.setVisibility(View.GONE);
                    }
                });
                anim.start();
                instant = true;
            } catch (Throwable ignore) {

            }
        }
        actionBarLayout.animateThemedValues(theme, accentId, nigthTheme, instant);
        if (Space.isTablet()) {
            layersActionBarLayout.animateThemedValues(theme, accentId, nigthTheme, instant);
            rightActionBarLayout.animateThemedValues(theme, accentId, nigthTheme, instant);
        }
    }

    public void presentFragment(BasePage fragment) {
        actionBarLayout.presentFragment(fragment);
    }

    public boolean presentFragment(final BasePage fragment, final boolean removeLast, boolean forceWithoutAnimation) {
        return actionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, true, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
            BasePage lastFragment = null;
            if (Space.isTablet()) {
                if (!layersActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = layersActionBarLayout.fragmentsStack.get(layersActionBarLayout.fragmentsStack.size() - 1);
                } else if (!rightActionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = rightActionBarLayout.fragmentsStack.get(rightActionBarLayout.fragmentsStack.size() - 1);
                } else if (!actionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1);
                }
            } else {
                if (!actionBarLayout.fragmentsStack.isEmpty()) {
                    lastFragment = actionBarLayout.fragmentsStack.get(actionBarLayout.fragmentsStack.size() - 1);
                }
            }

            if (lastFragment != null) {
                Bundle args = lastFragment.getArguments();
                if (args != null) {
                    outState.putBundle("args", args);
                    outState.putString("fragment", "group");
                }
                lastFragment.saveSelfArgs(outState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (Space.isTablet()) {
            if (layersActionBarLayout.getVisibility() == View.VISIBLE) {
                layersActionBarLayout.onBackPressed();
            } else {
                boolean cancel = false;
                if (rightActionBarLayout.getVisibility() == View.VISIBLE && !rightActionBarLayout.fragmentsStack.isEmpty()) {
                    BasePage lastFragment = rightActionBarLayout.fragmentsStack.get(rightActionBarLayout.fragmentsStack.size() - 1);
                    cancel = !lastFragment.onBackPressed();
                }
                if (!cancel) {
                    actionBarLayout.onBackPressed();
                }
            }
        } else {
            actionBarLayout.onBackPressed();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (actionBarLayout != null) {
            actionBarLayout.onLowMemory();
            if (Space.isTablet()) {
                rightActionBarLayout.onLowMemory();
                layersActionBarLayout.onLowMemory();
            }
        }
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        visibleActionMode = mode;
        try {
            Menu menu = mode.getMenu();
            if (menu != null) {
                boolean extended = actionBarLayout.extendActionMode(menu);
                if (!extended && Space.isTablet()) {
                    extended = rightActionBarLayout.extendActionMode(menu);
                    if (!extended) {
                        layersActionBarLayout.extendActionMode(menu);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 23 && mode.getType() == ActionMode.TYPE_FLOATING) {
            return;
        }
        actionBarLayout.onActionModeStarted(mode);
        if (Space.isTablet()) {
            rightActionBarLayout.onActionModeStarted(mode);
            layersActionBarLayout.onActionModeStarted(mode);
        }
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        if (visibleActionMode == mode) {
            visibleActionMode = null;
        }
        if (Build.VERSION.SDK_INT >= 23 && mode.getType() == ActionMode.TYPE_FLOATING) {
            return;
        }
        actionBarLayout.onActionModeFinished(mode);
        if (Space.isTablet()) {
            rightActionBarLayout.onActionModeFinished(mode);
            layersActionBarLayout.onActionModeFinished(mode);
        }
    }

    @Override
    public boolean onPreIme() {
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (!mainFragmentsStack.isEmpty() && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            BasePage fragment = mainFragmentsStack.get(mainFragmentsStack.size() - 1);
            if (Space.isTablet() && !rightFragmentsStack.isEmpty()) {
                fragment = rightFragmentsStack.get(rightFragmentsStack.size() - 1);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (Space.isTablet()) {
                if (layersActionBarLayout.getVisibility() == View.VISIBLE && !layersActionBarLayout.fragmentsStack.isEmpty()) {
                    layersActionBarLayout.onKeyUp(keyCode, event);
                } else if (rightActionBarLayout.getVisibility() == View.VISIBLE && !rightActionBarLayout.fragmentsStack.isEmpty()) {
                    rightActionBarLayout.onKeyUp(keyCode, event);
                } else {
                    actionBarLayout.onKeyUp(keyCode, event);
                }
            } else {
                if (actionBarLayout.fragmentsStack.size() == 1) {
                    if (getCurrentFocus() != null) {
                        AndroidUtilities.hideKeyboard(getCurrentFocus());
                    }
                } else {
                    actionBarLayout.onKeyUp(keyCode, event);
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean needPresentFragment(BasePage fragment, boolean removeLast, boolean forceWithoutAnimation, ContainerLayout layout) {
        if (Space.isTablet()) {
            if (layout != layersActionBarLayout) {
                layersActionBarLayout.setVisibility(View.VISIBLE);
                shadowTablet.setBackgroundColor(0x7f000000);
                layersActionBarLayout.presentFragment(fragment, removeLast, forceWithoutAnimation, false, false);
                return false;
            }
            return true;
        } else {
            boolean allow = true;
            if (fragment == null) {
                if (mainFragmentsStack.size() == 1) {
                    allow = false;
                }
            }
            return true;
        }
    }

    @Override
    public boolean needAddFragmentToStack(BasePage fragment, ContainerLayout layout) {
        if (Space.isTablet()) {
            if (layout != layersActionBarLayout) {
                layersActionBarLayout.setVisibility(View.VISIBLE);
                shadowTablet.setBackgroundColor(0x7f000000);
                layersActionBarLayout.addFragmentToStack(fragment);
                return false;
            }
            return true;
        } else {
            boolean allow = true;
            if (fragment == null) {
                if (mainFragmentsStack.size() == 1) {
                    allow = false;
                }
            }
            return true;
        }
    }

    @Override
    public boolean needCloseLastFragment(ContainerLayout layout) {
        if (Space.isTablet()) {
            if (layout == actionBarLayout && layout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            } else if (layout == rightActionBarLayout) {
                if (!tabletFullSize) {
                    backgroundTablet.setVisibility(View.VISIBLE);
                }
            } else if (layout == layersActionBarLayout && actionBarLayout.fragmentsStack.isEmpty() && layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else {
            if (layout.fragmentsStack.size() <= 1) {
                onFinish();
                finish();
                return false;
            }
        }
        return true;
    }

    public void onFinish() {
        NotificationCenter.getGlobalInstance().removeObserver(notificationCenterDelegate, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().removeObserver(notificationCenterDelegate, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().removeObserver(notificationCenterDelegate, NotificationCenter.needCheckSystemBarColors);

    }

    @Override
    public void rebuildAllFragments(boolean last) {
        if (layersActionBarLayout != null) {
            layersActionBarLayout.rebuildAllFragmentViews(last, last);
        } else {
            actionBarLayout.rebuildAllFragmentViews(last, last);
        }
    }

    @Override
    public void onRebuildAllFragments(ContainerLayout layout, boolean last) {
        if (Space.isTablet()) {
            if (layout == layersActionBarLayout) {
                rightActionBarLayout.rebuildAllFragmentViews(last, last);
                actionBarLayout.rebuildAllFragmentViews(last, last);
            }
        }
    }
}
