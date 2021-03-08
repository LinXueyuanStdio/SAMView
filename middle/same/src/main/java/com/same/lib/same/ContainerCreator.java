package com.same.lib.same;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.same.lib.anim.Easings;
import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;
import com.same.lib.core.BasePage;
import com.same.lib.core.ContainerLayout;
import com.same.lib.core.DrawerLayoutContainer;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.listview.LinearLayoutManager;
import com.same.lib.lottie.RLottieDrawable;
import com.same.lib.lottie.RLottieImageView;
import com.same.lib.same.theme.ChatTheme;
import com.same.lib.same.theme.CommonTheme;
import com.same.lib.same.theme.DialogTheme;
import com.same.lib.same.theme.ProfileTheme;
import com.same.lib.same.theme.ThemeEditorView;
import com.same.lib.same.view.PassCode;
import com.same.lib.same.view.PasscodeView;
import com.same.lib.same.view.RecyclerListView;
import com.same.lib.same.view.SideMenultItemAnimator;
import com.same.lib.same.view.ThemeSwitchView;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.theme.ThemeManager;
import com.same.lib.theme.ThemeRes;
import com.same.lib.theme.wrap.ThemeContainerLayout;
import com.same.lib.util.Keyboard;
import com.same.lib.util.Space;
import com.same.lib.util.UIThread;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/2/27
 * @description null
 * @usage null
 */
public class ContainerCreator implements ContainerLayout.ActionBarLayoutDelegate, ThemeEditorView.ThemeContainer {

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
    public RecyclerListView sideMenu;
    public SideMenultItemAnimator itemAnimator;

    private static ArrayList<BasePage> mainFragmentsStack = new ArrayList<>();
    private static ArrayList<BasePage> layerFragmentsStack = new ArrayList<>();
    private static ArrayList<BasePage> rightFragmentsStack = new ArrayList<>();
    private ActionMode visibleActionMode;
    private DrawerLayoutContainer drawerLayoutContainer;
    private PasscodeView passcodeView;
    private ImageView themeSwitchImageView;
    private View themeSwitchSunView;
    private RLottieDrawable themeSwitchSunDrawable;
    private FrameLayout frameLayout;

    private NotificationCenter.NotificationCenterDelegate notificationCenterDelegate = new NotificationCenter.NotificationCenterDelegate() {
        @Override
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.didSetNewTheme) {
                //                didSetNewTheme((Boolean) args[0]);TODO
            } else if (id == NotificationCenter.needSetDayNightTheme) {
                Log.e("needSetDayNightTheme", Arrays.toString(args) + "");
                needSetDayNightTheme(args);
            } else if (id == NotificationCenter.needCheckSystemBarColors) {
                //                checkSystemBarColors();
            }
        }
    };
    private Runnable lockRunnable;

    public interface ContextDelegate extends ThemeEditorView.ThemeContainer {
        Configuration getConfiguration();

        void stopSelf();

        Resources getResources();
    }

    public interface SideMenuPage {
        void setSideMenu(RecyclerListView sideMenu);
    }

    @NonNull
    ContextDelegate delegate;
    @NonNull
    Context context;

    public ContainerCreator(
            @NonNull Context context,
            @NonNull ContextDelegate delegate
    ) {
        this.context = context;
        this.delegate = delegate;
    }

    public void onPreCreate() {
        Configuration configuration = delegate.getConfiguration();
        AndroidUtilities.checkDisplaySize(context, configuration);
        Space.checkDisplaySize(context, configuration);
        Theme.onConfigurationChanged(context, configuration);
    }

    public void onCreateView(@NonNull FrameLayout frameLayout, @NonNull BasePage homePage) {
        this.frameLayout = frameLayout;
        //获取状态栏高度
        int resourceId = delegate.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            AndroidUtilities.statusBarHeight = delegate.getResources().getDimensionPixelSize(resourceId);
            Space.statusBarHeight = AndroidUtilities.statusBarHeight;
        }
        if (PassCode.passcodeHash.length() != 0 && PassCode.appLocked) {
            PassCode.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
        }
        ThemeRes.installAndApply(context, new CommonTheme(), new DialogTheme(), new ChatTheme(), new ProfileTheme());

        actionBarLayout = new ThemeContainerLayout(context) {
            @Override
            public void setThemeAnimationValue(float value) {
                super.setThemeAnimationValue(value);
                drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(KeyHub.key_windowBackgroundWhite));
            }
        };
        if (Build.VERSION.SDK_INT >= 21) {
            themeSwitchImageView = new ImageView(context);
            themeSwitchImageView.setVisibility(View.GONE);
            frameLayout.addView(themeSwitchImageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        }

        drawerLayoutContainer = new DrawerLayoutContainer(context);
        drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(KeyHub.key_windowBackgroundWhite));
        frameLayout.addView(drawerLayoutContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        if (Build.VERSION.SDK_INT >= 21) {
            themeSwitchSunView = new View(context) {
                @Override
                protected void onDraw(Canvas canvas) {
                    if (themeSwitchSunDrawable != null) {
                        themeSwitchSunDrawable.draw(canvas);
                        invalidate();
                    }
                }
            };
            frameLayout.addView(themeSwitchSunView, LayoutHelper.createFrame(48, 48));
            themeSwitchSunView.setVisibility(View.GONE);
        }
        if (Space.isTablet()) {
            //适配平板
            RelativeLayout launchLayout = new RelativeLayout(context) {

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
                    layersActionBarLayout.measure(MeasureSpec.makeMeasureSpec(Math.min(Space.dp(530), width), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(528), height), MeasureSpec.EXACTLY));

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

            backgroundTablet = new View(context);
            BitmapDrawable drawable = (BitmapDrawable) delegate.getResources().getDrawable(R.drawable.catstile);
            drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            backgroundTablet.setBackground(drawable);
            launchLayout.addView(backgroundTablet, LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

            launchLayout.addView(actionBarLayout);

            rightActionBarLayout = new ThemeContainerLayout(context);
            rightActionBarLayout.init(rightFragmentsStack);
            rightActionBarLayout.setDelegate(this);
            launchLayout.addView(rightActionBarLayout);

            shadowTabletSide = new FrameLayout(context);
            shadowTabletSide.setBackgroundColor(0x40295274);
            launchLayout.addView(shadowTabletSide);

            shadowTablet = new FrameLayout(context);
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

            layersActionBarLayout = new ThemeContainerLayout(context);
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

        //侧滑栏
        sideMenu = new RecyclerListView(context) {
            @Override
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                int restore = -1;
                if (itemAnimator != null && itemAnimator.isRunning() && itemAnimator.isAnimatingChild(child)) {
                    restore = canvas.save();
                    canvas.clipRect(0, itemAnimator.getAnimationClipTop(), getMeasuredWidth(), getMeasuredHeight());
                }
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (restore >= 0) {
                    canvas.restoreToCount(restore);
                    invalidate();
                    invalidateViews();
                }
                return result;
            }
        };
        itemAnimator = new SideMenultItemAnimator(sideMenu);
        sideMenu.setItemAnimator(itemAnimator);
        sideMenu.setBackgroundColor(Theme.getColor(KeyHub.key_actionBarActionModeDefaultIcon));
        sideMenu.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        sideMenu.setAllowItemsInteractionDuringAnimation(false);
        //        sideMenu.setAdapter(drawerLayoutAdapter = new DrawerLayoutAdapter(this, itemAnimator));
        drawerLayoutContainer.setDrawerLayout(sideMenu);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) sideMenu.getLayoutParams();
        Point screenSize = getRealScreenSize(context);
        layoutParams.width = Space.isTablet() ? Space.dp(320) : Math.min(Space.dp(320), Math.min(screenSize.x, screenSize.y) - AndroidUtilities.dp(56));
        layoutParams.height = LayoutHelper.MATCH_PARENT;
        sideMenu.setLayoutParams(layoutParams);

        //双向绑定
        drawerLayoutContainer.setParentActionBarLayout(actionBarLayout);
        actionBarLayout.setDrawerLayoutContainer(drawerLayoutContainer);
        actionBarLayout.init(mainFragmentsStack);//使用 main fragment 栈
        actionBarLayout.setDelegate(this);//代理，监听生命周期

        //指纹和密码解锁
        passcodeView = new PasscodeView(context);
        drawerLayoutContainer.addView(passcodeView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        switchToHomePage(homePage, true);

        //注册通知中心，使用观察者模式处理应用内部的通知（消息）
        NotificationCenter.getGlobalInstance().addObserver(notificationCenterDelegate, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().addObserver(notificationCenterDelegate, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().addObserver(notificationCenterDelegate, NotificationCenter.needCheckSystemBarColors);

        if (actionBarLayout.fragmentsStack.isEmpty()) {
            if (homePage instanceof SideMenuPage) {
                ((SideMenuPage) homePage).setSideMenu(sideMenu);
            }
            actionBarLayout.addFragmentToStack(homePage);
            drawerLayoutContainer.setAllowOpenDrawer(true, false);
        } else {
            //任务栈非空，直接从栈中恢复第一个fragment，清空其他fragment
            BasePage fragment = actionBarLayout.fragmentsStack.get(0);
            if (fragment instanceof SideMenuPage) {
                ((SideMenuPage) fragment).setSideMenu(sideMenu);
            }
            boolean allowOpen = true;
            if (Space.isTablet()) {
                allowOpen = actionBarLayout.fragmentsStack.size() <= 1 && layersActionBarLayout.fragmentsStack.isEmpty();
            }
            drawerLayoutContainer.setAllowOpenDrawer(allowOpen, false);
        }
        checkLayout();
        if (PassCode.needShowPasscode(true, true) || PassCode.isWaitingForPasscodeEnter) {
            showPasscodeActivity();
        }
    }

    public void switchToHomePage(@NonNull BasePage homePage, boolean removeAll) {
        if (Space.isTablet()) {
            layersActionBarLayout.removeAllFragments();
            rightActionBarLayout.removeAllFragments();
            if (!tabletFullSize) {
                shadowTabletSide.setVisibility(View.VISIBLE);
                if (rightActionBarLayout.fragmentsStack.isEmpty()) {
                    backgroundTablet.setVisibility(View.VISIBLE);
                }
                rightActionBarLayout.setVisibility(View.GONE);
            }
            layersActionBarLayout.setVisibility(View.GONE);
        }
        if (removeAll) {
            actionBarLayout.removeAllFragments();
        } else {
            actionBarLayout.removeFragmentFromStack(0);
        }
        if (homePage instanceof SideMenuPage) {
            ((SideMenuPage) homePage).setSideMenu(sideMenu);
        }
        actionBarLayout.addFragmentToStack(homePage, 0);
        drawerLayoutContainer.setAllowOpenDrawer(true, false);
        actionBarLayout.showLastFragment();
        if (Space.isTablet()) {
            layersActionBarLayout.showLastFragment();
            rightActionBarLayout.showLastFragment();
        }
    }

    private void showPasscodeActivity() {
        if (passcodeView == null) {
            return;
        }
        PassCode.appLocked = true;
        passcodeView.onShow();
        PassCode.isWaitingForPasscodeEnter = true;
        drawerLayoutContainer.setAllowOpenDrawer(false, false);
        passcodeView.setDelegate(() -> {
            PassCode.isWaitingForPasscodeEnter = false;
            drawerLayoutContainer.setAllowOpenDrawer(true, false);
            actionBarLayout.setVisibility(View.VISIBLE);
            actionBarLayout.showLastFragment();
            if (Space.isTablet()) {
                layersActionBarLayout.showLastFragment();
                rightActionBarLayout.showLastFragment();
                if (layersActionBarLayout.getVisibility() == View.INVISIBLE) {
                    layersActionBarLayout.setVisibility(View.VISIBLE);
                }
                rightActionBarLayout.setVisibility(View.VISIBLE);
            }
        });
        actionBarLayout.setVisibility(View.INVISIBLE);
        if (Space.isTablet()) {
            if (layersActionBarLayout.getVisibility() == View.VISIBLE) {
                layersActionBarLayout.setVisibility(View.INVISIBLE);
            }
            rightActionBarLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void onPasscodePause() {
        if (lockRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(lockRunnable);
            lockRunnable = null;
        }
        if (PassCode.passcodeHash.length() != 0) {
            PassCode.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
            lockRunnable = new Runnable() {
                @Override
                public void run() {
                    if (lockRunnable == this) {
                        if (PassCode.needShowPasscode(true, true)) {
                            showPasscodeActivity();
                        }
                        lockRunnable = null;
                    }
                }
            };
            if (PassCode.appLocked) {
                UIThread.runOnUIThread(lockRunnable, 1000);
            } else if (PassCode.autoLockIn != 0) {
                UIThread.runOnUIThread(lockRunnable, ((long) PassCode.autoLockIn) * 1000 + 1000);
            }
        } else {
            PassCode.lastPauseTime = 0;
        }
        PassCode.saveConfig(context);
    }

    private void onPasscodeResume() {
        if (lockRunnable != null) {
            UIThread.cancelRunOnUIThread(lockRunnable);
            lockRunnable = null;
        }
        if (PassCode.needShowPasscode(true, true)) {
            showPasscodeActivity();
        }
        if (PassCode.lastPauseTime != 0) {
            PassCode.lastPauseTime = 0;
            PassCode.saveConfig(context);
        }
    }

    public static Point getRealScreenSize(Context context) {
        Point size = new Point();
        try {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                windowManager.getDefaultDisplay().getRealSize(size);
            } else {
                try {
                    Method mGetRawW = Display.class.getMethod("getRawWidth");
                    Method mGetRawH = Display.class.getMethod("getRawHeight");
                    size.set((Integer) mGetRawW.invoke(windowManager.getDefaultDisplay()), (Integer) mGetRawH.invoke(windowManager.getDefaultDisplay()));
                } catch (Exception e) {
                    size.set(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public void onPause() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4096);
        onPasscodePause();
        actionBarLayout.onPause();
        if (Space.isTablet()) {
            rightActionBarLayout.onPause();
            layersActionBarLayout.onPause();
        }
        if (passcodeView != null) {
            passcodeView.onPause();
        }
    }

    public void onResume() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4096);

        onPasscodeResume();
        if (passcodeView.getVisibility() != View.VISIBLE) {
            actionBarLayout.onResume();
            if (Space.isTablet()) {
                rightActionBarLayout.onResume();
                layersActionBarLayout.onResume();
            }
        } else {
            actionBarLayout.dismissDialogs();
            if (Space.isTablet()) {
                rightActionBarLayout.dismissDialogs();
                layersActionBarLayout.dismissDialogs();
            }
            passcodeView.onResume();
        }
    }

    public void onDestroy() {
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        editorView.destroy();
    }

    public void onPreActivityResult() {
        if (PassCode.passcodeHash.length() != 0 && PassCode.lastPauseTime != 0) {
            PassCode.lastPauseTime = 0;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.onActivityResult(requestCode, resultCode, data);
        }
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

    public void onPreConfigurationChanged(Configuration newConfig) {
        AndroidUtilities.checkDisplaySize(context, newConfig);
        Theme.onConfigurationChanged(context, newConfig);
    }

    public void onPostConfigurationChanged(Configuration newConfig) {
        checkLayout();
        actionBarLayout.onConfigurationChanged(newConfig);
        ThemeEditorView editorView = ThemeEditorView.getInstance();
        if (editorView != null) {
            editorView.onConfigurationChanged();
        }
        if (Theme.selectedAutoNightType == Theme.AUTO_NIGHT_TYPE_SYSTEM) {
            ThemeManager.checkAutoNightThemeConditions(context);
        }
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
    public void checkLayout() {
        if (!Space.isTablet() || rightActionBarLayout == null) {
            return;
        }

        if (!AndroidUtilities.isInMultiwindow && (!AndroidUtilities.isSmallTablet() || delegate.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            tabletFullSize = false;
            if (actionBarLayout.fragmentsStack.size() >= 2) {
                for (int a = 1; a < actionBarLayout.fragmentsStack.size(); a++) {
                    BasePage chatFragment = actionBarLayout.fragmentsStack.get(a);
                    chatFragment.onPause();
                    actionBarLayout.fragmentsStack.remove(a);
                    rightActionBarLayout.fragmentsStack.add(chatFragment);
                    a--;
                }
                if (passcodeView.getVisibility() != View.VISIBLE) {
                    actionBarLayout.showLastFragment();
                    rightActionBarLayout.showLastFragment();
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
                if (passcodeView.getVisibility() != View.VISIBLE) {
                    actionBarLayout.showLastFragment();
                }
            }
            shadowTabletSide.setVisibility(View.GONE);
            rightActionBarLayout.setVisibility(View.GONE);
            backgroundTablet.setVisibility(!actionBarLayout.fragmentsStack.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    public void didSetPasscode() {
        //        if (PassCode.passcodeHash.length() > 0 && !PassCode.allowScreenCapture) {
        //            try {
        //                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //        } else if (!AndroidUtilities.hasFlagSecureFragment()) {
        //            try {
        //                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //        }
    }

    public void didSetNewTheme(Boolean nightTheme) {
        if (!nightTheme) {
            if (sideMenu != null) {
                sideMenu.setBackgroundColor(Theme.getColor(KeyHub.key_windowBackgroundGray));
                sideMenu.setGlowColor(Theme.getColor(KeyHub.key_windowBackgroundGray));
                sideMenu.setListSelectorColor(Theme.getColor(KeyHub.key_listSelector));
                if (sideMenu.getAdapter() != null) {
                    sideMenu.getAdapter().notifyDataSetChanged();
                }
            }
        }
        drawerLayoutContainer.setBehindKeyboardColor(Theme.getColor(KeyHub.key_windowBackgroundWhite));
    }

    public void needSetDayNightTheme(Object... args) {
        boolean instant = false;
        if (Build.VERSION.SDK_INT >= 21 && args[2] != null) {
            if (themeSwitchImageView.getVisibility() == View.VISIBLE) {
                return;
            }
            try {
                int[] pos = (int[]) args[2];
                boolean toDark = (Boolean) args[4];
                RLottieImageView darkThemeView = (RLottieImageView) args[5];
                int w = drawerLayoutContainer.getMeasuredWidth();
                int h = drawerLayoutContainer.getMeasuredHeight();
                if (!toDark) {
                    darkThemeView.setVisibility(View.INVISIBLE);
                }
                Bitmap bitmap = Bitmap.createBitmap(drawerLayoutContainer.getMeasuredWidth(), drawerLayoutContainer.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawerLayoutContainer.draw(canvas);
                frameLayout.removeView(themeSwitchImageView);
                if (toDark) {
                    frameLayout.addView(themeSwitchImageView, 0, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                    themeSwitchSunView.setVisibility(View.GONE);
                } else {
                    frameLayout.addView(themeSwitchImageView, 1, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
                    themeSwitchSunView.setTranslationX(pos[0] - AndroidUtilities.dp(14));
                    themeSwitchSunView.setTranslationY(pos[1] - AndroidUtilities.dp(14));
                    themeSwitchSunView.setVisibility(View.VISIBLE);
                    themeSwitchSunView.invalidate();
                }
                themeSwitchImageView.setImageBitmap(bitmap);
                themeSwitchImageView.setVisibility(View.VISIBLE);
                themeSwitchSunDrawable = darkThemeView.getAnimatedDrawable();
                float finalRadius = (float) Math.max(Math.sqrt((w - pos[0]) * (w - pos[0]) + (h - pos[1]) * (h - pos[1])), Math.sqrt(pos[0] * pos[0] + (h - pos[1]) * (h - pos[1])));
                Animator anim = ViewAnimationUtils.createCircularReveal(toDark ? drawerLayoutContainer : themeSwitchImageView, pos[0], pos[1], toDark ? 0 : finalRadius, toDark ? finalRadius : 0);
                anim.setDuration(400);
                anim.setInterpolator(Easings.easeInOutQuad);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        themeSwitchImageView.setImageDrawable(null);
                        themeSwitchImageView.setVisibility(View.GONE);
                        themeSwitchSunView.setVisibility(View.GONE);
//                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeAccentListUpdated);
                        if (!toDark) {
                            darkThemeView.setVisibility(View.VISIBLE);
                        }
                        ThemeSwitchView.switchingTheme = false;
                    }
                });
                anim.start();
                instant = true;
            } catch (Throwable e) {
                e.printStackTrace();
                try {
                    themeSwitchImageView.setImageDrawable(null);
                    frameLayout.removeView(themeSwitchImageView);
                    ThemeSwitchView.switchingTheme = false;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        ThemeInfo theme = (ThemeInfo) args[0];
        boolean nigthTheme = (Boolean) args[1];
        int accentId = (Integer) args[3];
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

    public void onSaveInstanceState(Bundle outState) {
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
    }


    public void onBackPressed() {
        if (passcodeView.getVisibility() == View.VISIBLE) {
            delegate.stopSelf();
            return;
        }
        if (drawerLayoutContainer.isDrawerOpened()) {
            drawerLayoutContainer.closeDrawer(false);
        } else if (Space.isTablet()) {
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

    public void onLowMemory() {
        if (actionBarLayout != null) {
            actionBarLayout.onLowMemory();
            if (Space.isTablet()) {
                rightActionBarLayout.onLowMemory();
                layersActionBarLayout.onLowMemory();
            }
        }
    }

    public void onActionModeStarted(ActionMode mode) {
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

    public void onActionModeFinished(ActionMode mode) {
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

    public void onKeyUp(int keyCode, KeyEvent event) {
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
                    if (context instanceof Activity) {
                        View focus = ((Activity) context).getCurrentFocus();
                        if (focus != null) {
                            Keyboard.hideKeyboard(focus);
                        }
                    }
                } else {
                    actionBarLayout.onKeyUp(keyCode, event);
                }
            }
        }
    }

    @Override
    public boolean onPreIme() {
        return false;
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
                delegate.stopSelf();
                return false;
            } else if (layout == rightActionBarLayout) {
                if (!tabletFullSize) {
                    backgroundTablet.setVisibility(View.VISIBLE);
                }
            } else if (layout == layersActionBarLayout && actionBarLayout.fragmentsStack.isEmpty() && layersActionBarLayout.fragmentsStack.size() == 1) {
                onFinish();
                delegate.stopSelf();
                return false;
            }
        } else {
            if (layout.fragmentsStack.size() <= 1) {
                onFinish();
                delegate.stopSelf();
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
