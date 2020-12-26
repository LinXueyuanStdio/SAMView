package com.same.lib.core;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.same.lib.R;
import com.same.lib.anim.CubicBezierInterpolator;
import com.same.lib.util.ColorManager;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.util.KeyHub;
import com.same.lib.util.Keyboard;
import com.same.lib.util.Space;
import com.same.lib.util.Store;
import com.same.lib.util.UIThread;

import java.util.ArrayList;

import androidx.annotation.DrawableRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/23
 * @description 容器 ViewGroup
 * @usage null
 */
public class ContainerLayout extends FrameLayout {

    public interface ActionBarLayoutDelegate {
        boolean onPreIme();

        boolean needPresentFragment(BasePage fragment, boolean removeLast, boolean forceWithoutAnimation, ContainerLayout layout);

        boolean needAddFragmentToStack(BasePage fragment, ContainerLayout layout);

        boolean needCloseLastFragment(ContainerLayout layout);

        void onRebuildAllFragments(ContainerLayout layout, boolean last);
    }

    /**
     * 适配软键盘的容器
     */
    public class LayoutContainer extends AdjustPanFrameLayout {

        private Rect rect = new Rect();
        private boolean isKeyboardVisible;

        public LayoutContainer(Context context) {
            super(context);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child instanceof ActionBar) {
                return super.drawChild(canvas, child, drawingTime);
            } else {
                int actionBarHeight = 0;
                int actionBarY = 0;
                int childCount = getChildCount();
                for (int a = 0; a < childCount; a++) {
                    View view = getChildAt(a);
                    if (view == child) {
                        continue;
                    }
                    if (view instanceof ActionBar && view.getVisibility() == VISIBLE) {
                        if (((ActionBar) view).getCastShadows()) {
                            actionBarHeight = view.getMeasuredHeight();
                            actionBarY = (int) view.getY();
                        }
                        break;
                    }
                }
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (actionBarHeight != 0 && headerShadowDrawable != null) {
                    headerShadowDrawable.setBounds(0, actionBarY + actionBarHeight, getMeasuredWidth(), actionBarY + actionBarHeight + headerShadowDrawable.getIntrinsicHeight());
                    headerShadowDrawable.draw(canvas);
                }
                return result;
            }
        }

        @Override
        public boolean hasOverlappingRendering() {
            if (Build.VERSION.SDK_INT >= 28) {
                return true;
            }
            return false;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int count = getChildCount();
            int actionBarHeight = 0;
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof ActionBar) {
                    child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                    actionBarHeight = child.getMeasuredHeight();
                    break;
                }
            }
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (!(child instanceof ActionBar)) {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, actionBarHeight);
                }
            }
            setMeasuredDimension(width, height);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int count = getChildCount();
            int actionBarHeight = 0;
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof ActionBar) {
                    actionBarHeight = child.getMeasuredHeight();
                    child.layout(0, 0, child.getMeasuredWidth(), actionBarHeight);
                    break;
                }
            }
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (!(child instanceof ActionBar)) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) child.getLayoutParams();
                    child.layout(layoutParams.leftMargin, layoutParams.topMargin + actionBarHeight, layoutParams.leftMargin + child.getMeasuredWidth(), layoutParams.topMargin + actionBarHeight + child.getMeasuredHeight());
                }
            }

            View rootView = getRootView();
            getWindowVisibleDisplayFrame(rect);
            int usableViewHeight = rootView.getHeight() - (rect.top != 0 ? Space.statusBarHeight : 0) - Space.getViewInset(rootView);
            isKeyboardVisible = usableViewHeight - (rect.bottom - rect.top) > 0;
            if (waitingForKeyboardCloseRunnable != null && !containerView.isKeyboardVisible && !containerViewBack.isKeyboardVisible) {
                UIThread.cancelRunOnUIThread(waitingForKeyboardCloseRunnable);
                waitingForKeyboardCloseRunnable.run();
                waitingForKeyboardCloseRunnable = null;
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if ((inPreviewMode || transitionAnimationPreviewMode) && (ev.getActionMasked() == MotionEvent.ACTION_DOWN || ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)) {
                return false;
            }
            //
            try {
                return (!inPreviewMode || this != containerView) && super.dispatchTouchEvent(ev);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPanTranslationUpdate(int y) {
            currentPanTranslationY = y;
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof ActionBar) {
                    child.setTranslationY(y);
                } else {
                    if (this == layoutToIgnore) {
                        child.setTranslationY(y);
                    } else {
                        child.setTranslationY(0);
                    }
                }
            }
            if (layoutToIgnore == null && !fragmentsStack.isEmpty()) {
                fragmentsStack.get(fragmentsStack.size() - 1).onPanTranslationUpdate(y);
            }
        }

        @Override
        protected void onTransitionStart() {
            fragmentsStack.get(fragmentsStack.size() - 1).onPanTransitionStart();
        }

        @Override
        protected void onTransitionEnd() {
            fragmentsStack.get(fragmentsStack.size() - 1).onPanTransitionEnd();
            if (layoutToIgnore != null && !transitionAnimationInProgress && !startedTracking) {
                layoutToIgnore = null;
            }
        }
    }

    //region field
    private static Drawable headerShadowDrawable;
    private static Drawable layerShadowDrawable;
    private static Paint scrimPaint;

    private Runnable waitingForKeyboardCloseRunnable;
    private Runnable delayedOpenAnimationRunnable;

    protected boolean inPreviewMode;
    private boolean previewOpenAnimationInProgress;
    private ColorDrawable previewBackgroundDrawable;

    private LayoutContainer containerView;
    private LayoutContainer containerViewBack;
    private DrawerLayoutContainer drawerLayoutContainer;//持有但不addView
    private ActionBar currentActionBar;

    private BasePage newFragment;
    private BasePage oldFragment;

    private AnimatorSet currentAnimation;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(1.5f);
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();

    public float innerTranslationX;

    private int currentPanTranslationY;

    private boolean maybeStartTracking;
    protected boolean startedTracking;
    private int startedTrackingX;
    private int startedTrackingY;
    protected boolean animationInProgress;
    private VelocityTracker velocityTracker;
    private View layoutToIgnore;
    protected boolean beginTrackingSent;
    protected boolean transitionAnimationInProgress;
    protected boolean transitionAnimationPreviewMode;
    protected ArrayList<int[]> animateStartColors = new ArrayList<>();
    protected ArrayList<int[]> animateEndColors = new ArrayList<>();

    public static Drawable moveUpDrawable;

    protected boolean rebuildAfterAnimation;
    protected boolean rebuildLastAfterAnimation;
    protected boolean showLastAfterAnimation;
    private long transitionAnimationStartTime;
    private boolean inActionMode;
    private int startedTrackingPointerId;
    private Runnable onCloseAnimationEndRunnable;
    private Runnable onOpenAnimationEndRunnable;
    private boolean useAlphaAnimations;
    private View backgroundView;
    private boolean removeActionBarExtraHeight;
    private Runnable animationRunnable;

    private float animationProgress;
    private long lastFrameTime;

    private String titleOverlayText;
    private int titleOverlayTextId;
    private Runnable overlayAction;

    private ActionBarLayoutDelegate delegate;
    protected Activity parentActivity;

    /**
     * fragment 栈
     */
    public ArrayList<BasePage> fragmentsStack;
    //endregion

    protected Drawable getDrawableById(@NonNull Context context, @DrawableRes int id) {
        return ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
    }

    public ContainerLayout(Context context) {
        super(context);
        parentActivity = (Activity) context;

        moveUpDrawable = getDrawableById(context, R.drawable.preview_open);
        if (layerShadowDrawable == null) {
            layerShadowDrawable = getDrawableById(context, R.drawable.layer_shadow);
            headerShadowDrawable = getDrawableById(context, R.drawable.header_shadow).mutate();
            scrimPaint = new Paint();
        }
    }

    /**
     * ActionBarLayout
     *   - containerViewBack(LayoutContainer)
     *   - containerView(LayoutContainer)
     * @param stack fragment 栈
     */
    public void init(ArrayList<BasePage> stack) {
        fragmentsStack = stack;
        containerViewBack = new LayoutContainer(parentActivity);
        addView(containerViewBack);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) containerViewBack.getLayoutParams();
        layoutParams.width = LayoutHelper.MATCH_PARENT;
        layoutParams.height = LayoutHelper.MATCH_PARENT;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        containerViewBack.setLayoutParams(layoutParams);

        containerView = new LayoutContainer(parentActivity);
        addView(containerView);
        layoutParams = (FrameLayout.LayoutParams) containerView.getLayoutParams();
        layoutParams.width = LayoutHelper.MATCH_PARENT;
        layoutParams.height = LayoutHelper.MATCH_PARENT;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        containerView.setLayoutParams(layoutParams);

        for (BasePage fragment : fragmentsStack) {
            fragment.setParentLayout(this);
        }
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!fragmentsStack.isEmpty()) {
            for (int a = 0, N = fragmentsStack.size(); a < N; a++) {
                BasePage fragment = fragmentsStack.get(a);
                fragment.onConfigurationChanged(newConfig);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return animationInProgress || checkTransitionAnimation() || onTouchEvent(ev);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        onTouchEvent(null);
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            return delegate != null && delegate.onPreIme() || super.dispatchKeyEventPreIme(event);
        }
        return super.dispatchKeyEventPreIme(event);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int translationX = (int) innerTranslationX + getPaddingRight();
        int clipLeft = getPaddingLeft();
        int clipRight = width + getPaddingLeft();

        if (child == containerViewBack) {
            clipRight = translationX;
        } else if (child == containerView) {
            clipLeft = translationX;
        }

        final int restoreCount = canvas.save();
        if (!transitionAnimationInProgress && !inPreviewMode) {
            canvas.clipRect(clipLeft, 0, clipRight, getHeight());
        }
        if ((inPreviewMode || transitionAnimationPreviewMode) && child == containerView) {
            View view = containerView.getChildAt(0);
            if (view != null) {
                previewBackgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                previewBackgroundDrawable.draw(canvas);
                int x = (getMeasuredWidth() - Space.dp(24)) / 2;
                int y = (int) (view.getTop() + containerView.getTranslationY() - Space.dp(12 + (Build.VERSION.SDK_INT < 21 ? 20 : 0)));
                moveUpDrawable.setBounds(x, y, x + Space.dp(24), y + Space.dp(24));
                moveUpDrawable.draw(canvas);
            }
        }
        final boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreCount);

        if (translationX != 0) {
            if (child == containerView) {
                final float alpha = Math.max(0, Math.min((width - translationX) / (float) Space.dp(20), 1.0f));
                layerShadowDrawable.setBounds(translationX - layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                layerShadowDrawable.setAlpha((int) (0xff * alpha));
                layerShadowDrawable.draw(canvas);
            } else if (child == containerViewBack) {
                float opacity = Math.min(0.8f, (width - translationX) / (float) width);
                if (opacity < 0) {
                    opacity = 0;
                }
                scrimPaint.setColor((int) (((0x99000000 & 0xff000000) >>> 24) * opacity) << 24);
                canvas.drawRect(clipLeft, 0, clipRight, getHeight(), scrimPaint);
            }
        }

        return result;
    }

    private void prepareForMoving(MotionEvent ev) {
        maybeStartTracking = false;
        startedTracking = true;
        layoutToIgnore = containerViewBack;
        startedTrackingX = (int) ev.getX();
        containerViewBack.setVisibility(View.VISIBLE);
        beginTrackingSent = false;

        BasePage lastFragment = fragmentsStack.get(fragmentsStack.size() - 2);
        View fragmentView = lastFragment.fragmentView;
        if (fragmentView == null) {
            fragmentView = lastFragment.createView(parentActivity);
        }
        ViewGroup parent = (ViewGroup) fragmentView.getParent();
        if (parent != null) {
            lastFragment.onRemoveFromParent();
            parent.removeView(fragmentView);
        }
        containerViewBack.addView(fragmentView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragmentView.getLayoutParams();
        layoutParams.width = LayoutHelper.MATCH_PARENT;
        layoutParams.height = LayoutHelper.MATCH_PARENT;
        layoutParams.topMargin = layoutParams.bottomMargin = layoutParams.rightMargin = layoutParams.leftMargin = 0;
        fragmentView.setLayoutParams(layoutParams);
        if (lastFragment.actionBar != null && lastFragment.actionBar.shouldAddToContainer()) {
            parent = (ViewGroup) lastFragment.actionBar.getParent();
            if (parent != null) {
                parent.removeView(lastFragment.actionBar);
            }
            if (removeActionBarExtraHeight) {
                lastFragment.actionBar.setOccupyStatusBar(false);
            }
            containerViewBack.addView(lastFragment.actionBar);
            lastFragment.actionBar.setTitleOverlayText(titleOverlayText, titleOverlayTextId, overlayAction);
        }
        if (!lastFragment.hasOwnBackground && fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(ColorManager.getColor(KeyHub.key_windowBackgroundWhite));
        }
        lastFragment.onResume();
        loadDescFor(lastFragment);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!checkTransitionAnimation() && !inActionMode && !animationInProgress) {
            if (fragmentsStack.size() > 1) {
                if (ev != null && ev.getAction() == MotionEvent.ACTION_DOWN && !startedTracking && !maybeStartTracking) {
                    BasePage currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                    if (!currentFragment.isSwipeBackEnabled(ev)) {
                        return false;
                    }
                    startedTrackingPointerId = ev.getPointerId(0);
                    maybeStartTracking = true;
                    startedTrackingX = (int) ev.getX();
                    startedTrackingY = (int) ev.getY();
                    if (velocityTracker != null) {
                        velocityTracker.clear();
                    }
                } else if (ev != null && ev.getAction() == MotionEvent.ACTION_MOVE && ev.getPointerId(0) == startedTrackingPointerId) {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain();
                    }
                    int dx = Math.max(0, (int) (ev.getX() - startedTrackingX));
                    int dy = Math.abs((int) ev.getY() - startedTrackingY);
                    velocityTracker.addMovement(ev);
                    if (!inPreviewMode && maybeStartTracking && !startedTracking && dx >= Space.getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                        BasePage currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                        if (currentFragment.canBeginSlide()) {
                            prepareForMoving(ev);
                        } else {
                            maybeStartTracking = false;
                        }
                    } else if (startedTracking) {
                        if (!beginTrackingSent) {
                            if (parentActivity.getCurrentFocus() != null) {
                                Keyboard.hideKeyboard(parentActivity.getCurrentFocus());
                            }
                            BasePage currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                            currentFragment.onBeginSlide();
                            beginTrackingSent = true;
                        }
                        containerView.setTranslationX(dx);
                        setInnerTranslationX(dx);
                    }
                } else if (ev != null && ev.getPointerId(0) == startedTrackingPointerId && (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_POINTER_UP)) {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain();
                    }
                    velocityTracker.computeCurrentVelocity(1000);
                    BasePage currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                    if (!inPreviewMode && !transitionAnimationPreviewMode && !startedTracking && currentFragment.isSwipeBackEnabled(ev)) {
                        float velX = velocityTracker.getXVelocity();
                        float velY = velocityTracker.getYVelocity();
                        if (velX >= 3500 && velX > Math.abs(velY) && currentFragment.canBeginSlide()) {
                            prepareForMoving(ev);
                            if (!beginTrackingSent) {
                                if (((Activity) getContext()).getCurrentFocus() != null) {
                                    Keyboard.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                                }
                                beginTrackingSent = true;
                            }
                        }
                    }
                    if (startedTracking) {
                        float x = containerView.getX();
                        AnimatorSet animatorSet = new AnimatorSet();
                        float velX = velocityTracker.getXVelocity();
                        float velY = velocityTracker.getYVelocity();
                        final boolean backAnimation = x < containerView.getMeasuredWidth() / 3.0f && (velX < 3500 || velX < velY);
                        float distToMove;
                        if (!backAnimation) {
                            distToMove = containerView.getMeasuredWidth() - x;
                            animatorSet.playTogether(
                                    ObjectAnimator.ofFloat(containerView, View.TRANSLATION_X, containerView.getMeasuredWidth()),
                                    ObjectAnimator.ofFloat(this, "innerTranslationX", (float) containerView.getMeasuredWidth())
                            );
                        } else {
                            distToMove = x;
                            animatorSet.playTogether(
                                    ObjectAnimator.ofFloat(containerView, View.TRANSLATION_X, 0),
                                    ObjectAnimator.ofFloat(this, "innerTranslationX", 0.0f)
                            );
                        }

                        animatorSet.setDuration(Math.max((int) (200.0f / containerView.getMeasuredWidth() * distToMove), 50));
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                onSlideAnimationEnd(backAnimation);
                            }
                        });
                        animatorSet.start();
                        animationInProgress = true;
                        layoutToIgnore = containerViewBack;
                    } else {
                        maybeStartTracking = false;
                        startedTracking = false;
                        layoutToIgnore = null;
                    }
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        velocityTracker = null;
                    }
                } else if (ev == null) {
                    maybeStartTracking = false;
                    startedTracking = false;
                    layoutToIgnore = null;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        velocityTracker = null;
                    }
                }
            }
            return startedTracking;
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && !checkTransitionAnimation() && !startedTracking && currentActionBar != null) {
            currentActionBar.onMenuButtonPressed();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onBackPressed() {
        if (transitionAnimationPreviewMode || startedTracking || checkTransitionAnimation() || fragmentsStack.isEmpty()) {
            return;
        }
        if (!currentActionBar.isActionModeShowed() && currentActionBar != null && currentActionBar.isSearchFieldVisible) {
            currentActionBar.closeSearchField();
            return;
        }
        BasePage lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
        if (lastFragment.onBackPressed()) {
            if (!fragmentsStack.isEmpty()) {
                closeLastFragment(true);
            }
        }
    }

    public void onLowMemory() {
        for (BasePage fragment : fragmentsStack) {
            fragment.onLowMemory();
        }
    }

    /**
     * 处理动画，继续调用 BasePage.onResume()
     */
    public void onResume() {
        if (transitionAnimationInProgress) {
            if (currentAnimation != null) {
                currentAnimation.cancel();
                currentAnimation = null;
            }
            if (onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd();
            } else if (onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd();
            }
        }
        if (!fragmentsStack.isEmpty()) {
            BasePage lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.onResume();
        }
    }

    /**
     * 继续调用 BasePage.onPause()
     */
    public void onPause() {
        if (!fragmentsStack.isEmpty()) {
            BasePage lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.onPause();
        }
    }

    public void startActivityForResult(final Intent intent, final int requestCode) {
        if (parentActivity == null) {
            return;
        }
        if (transitionAnimationInProgress) {
            if (currentAnimation != null) {
                currentAnimation.cancel();
                currentAnimation = null;
            }
            if (onCloseAnimationEndRunnable != null) {
                onCloseAnimationEnd();
            } else if (onOpenAnimationEndRunnable != null) {
                onOpenAnimationEnd();
            }
            containerView.invalidate();
            if (intent != null) {
                parentActivity.startActivityForResult(intent, requestCode);
            }
        } else {
            if (intent != null) {
                parentActivity.startActivityForResult(intent, requestCode);
            }
        }
    }

    //region 工具方法，不要重写，只管调用
    public void drawHeaderShadow(Canvas canvas, int y) {
        drawHeaderShadow(canvas, 255, y);
    }

    public void drawHeaderShadow(Canvas canvas, int alpha, int y) {
        if (headerShadowDrawable != null) {
            headerShadowDrawable.setAlpha(alpha);
            headerShadowDrawable.setBounds(0, y, getMeasuredWidth(), y + headerShadowDrawable.getIntrinsicHeight());
            headerShadowDrawable.draw(canvas);
        }
    }

    public void dismissDialogs() {
        if (!fragmentsStack.isEmpty()) {
            BasePage lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.dismissCurrentDialog();
        }
    }

    //region 动画控制
    private void onCloseAnimationEnd() {
        if (transitionAnimationInProgress && onCloseAnimationEndRunnable != null) {
            transitionAnimationInProgress = false;
            if (currentPanTranslationY == 0) {
                layoutToIgnore = null;
            }
            transitionAnimationPreviewMode = false;
            transitionAnimationStartTime = 0;
            newFragment = null;
            oldFragment = null;
            Runnable endRunnable = onCloseAnimationEndRunnable;
            onCloseAnimationEndRunnable = null;
            endRunnable.run();
            checkNeedRebuild();
            checkNeedRebuild();
        }
    }

    private void onOpenAnimationEnd() {
        if (transitionAnimationInProgress && onOpenAnimationEndRunnable != null) {
            transitionAnimationInProgress = false;
            if (currentPanTranslationY == 0) {
                layoutToIgnore = null;
            }
            transitionAnimationPreviewMode = false;
            transitionAnimationStartTime = 0;
            newFragment = null;
            oldFragment = null;
            Runnable endRunnable = onOpenAnimationEndRunnable;
            onOpenAnimationEndRunnable = null;
            endRunnable.run();
            checkNeedRebuild();
        }
    }

    private void onSlideAnimationEnd(final boolean backAnimation) {
        if (!backAnimation) {
            if (fragmentsStack.size() < 2) {
                return;
            }
            BasePage lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.onPause();
            lastFragment.onFragmentDestroy();
            lastFragment.setParentLayout(null);
            fragmentsStack.remove(fragmentsStack.size() - 1);

            LayoutContainer temp = containerView;
            containerView = containerViewBack;
            containerViewBack = temp;
            bringContainerViewToFront();

            lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            currentActionBar = lastFragment.actionBar;
            lastFragment.onResume();
            lastFragment.onBecomeFullyVisible();

            layoutToIgnore = containerView;
        } else {
            if (fragmentsStack.size() >= 2) {
                BasePage lastFragment = fragmentsStack.get(fragmentsStack.size() - 2);
                lastFragment.onPause();
                if (lastFragment.fragmentView != null) {
                    ViewGroup parent = (ViewGroup) lastFragment.fragmentView.getParent();
                    if (parent != null) {
                        lastFragment.onRemoveFromParent();
                        parent.removeViewInLayout(lastFragment.fragmentView);
                    }
                }
                if (lastFragment.actionBar != null && lastFragment.actionBar.shouldAddToContainer()) {
                    ViewGroup parent = (ViewGroup) lastFragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeViewInLayout(lastFragment.actionBar);
                    }
                }
            }
            layoutToIgnore = null;
        }
        containerViewBack.setVisibility(View.INVISIBLE);
        startedTracking = false;
        animationInProgress = false;
        containerView.setTranslationX(0);
        containerViewBack.setTranslationX(0);
        setInnerTranslationX(0);
    }

    private void onAnimationEndCheck(boolean byCheck) {
        onCloseAnimationEnd();
        onOpenAnimationEnd();
        if (waitingForKeyboardCloseRunnable != null) {
            UIThread.cancelRunOnUIThread(waitingForKeyboardCloseRunnable);
            waitingForKeyboardCloseRunnable = null;
        }
        if (currentAnimation != null) {
            if (byCheck) {
                currentAnimation.cancel();
            }
            currentAnimation = null;
        }
        if (animationRunnable != null) {
            UIThread.cancelRunOnUIThread(animationRunnable);
            animationRunnable = null;
        }
        setAlpha(1.0f);
        containerView.setAlpha(1.0f);
        containerView.setScaleX(1.0f);
        containerView.setScaleY(1.0f);
        containerViewBack.setAlpha(1.0f);
        containerViewBack.setScaleX(1.0f);
        containerViewBack.setScaleY(1.0f);
    }

    public boolean checkTransitionAnimation() {
        if (transitionAnimationPreviewMode) {
            return false;
        }
        if (transitionAnimationInProgress && transitionAnimationStartTime < System.currentTimeMillis() - 1500) {
            onAnimationEndCheck(true);
        }
        return transitionAnimationInProgress;
    }

    public void resumeDelayedFragmentAnimation() {
        if (delayedOpenAnimationRunnable == null) {
            return;
        }
        UIThread.cancelRunOnUIThread(delayedOpenAnimationRunnable);
        delayedOpenAnimationRunnable.run();
        delayedOpenAnimationRunnable = null;
    }

    private void startLayoutAnimation(final boolean open, final boolean first, final boolean preview) {
        if (first) {
            animationProgress = 0.0f;
            lastFrameTime = System.nanoTime() / 1000000;
        }
        UIThread.runOnUIThread(animationRunnable = new Runnable() {
            @Override
            public void run() {
                if (animationRunnable != this) {
                    return;
                }
                animationRunnable = null;
                if (first) {
                    transitionAnimationStartTime = System.currentTimeMillis();
                }
                long newTime = System.nanoTime() / 1000000;
                long dt = newTime - lastFrameTime;
                if (dt > 18) {
                    dt = 18;
                }
                lastFrameTime = newTime;
                animationProgress += dt / 150.0f;
                if (animationProgress > 1.0f) {
                    animationProgress = 1.0f;
                }
                if (newFragment != null) {
                    newFragment.onTransitionAnimationProgress(true, animationProgress);
                }
                if (oldFragment != null) {
                    oldFragment.onTransitionAnimationProgress(false, animationProgress);
                }
                float interpolated = decelerateInterpolator.getInterpolation(animationProgress);
                if (open) {
                    containerView.setAlpha(interpolated);
                    if (preview) {
                        containerView.setScaleX(0.9f + 0.1f * interpolated);
                        containerView.setScaleY(0.9f + 0.1f * interpolated);
                        previewBackgroundDrawable.setAlpha((int) (0x2e * interpolated));
                        moveUpDrawable.setAlpha((int) (255 * interpolated));
                        containerView.invalidate();
                        invalidate();
                    } else {
                        containerView.setTranslationX(Space.dp(48) * (1.0f - interpolated));
                    }
                } else {
                    containerViewBack.setAlpha(1.0f - interpolated);
                    if (preview) {
                        containerViewBack.setScaleX(0.9f + 0.1f * (1.0f - interpolated));
                        containerViewBack.setScaleY(0.9f + 0.1f * (1.0f - interpolated));
                        previewBackgroundDrawable.setAlpha((int) (0x2e * (1.0f - interpolated)));
                        moveUpDrawable.setAlpha((int) (255 * (1.0f - interpolated)));
                        containerView.invalidate();
                        invalidate();
                    } else {
                        containerViewBack.setTranslationX(Space.dp(48) * interpolated);
                    }
                }
                if (animationProgress < 1) {
                    startLayoutAnimation(open, false, preview);
                } else {
                    onAnimationEndCheck(false);
                }
            }
        });
    }
    //endregion

    //region Fragment控制
    protected void checkNeedRebuild() {
        if (rebuildAfterAnimation) {
            rebuildAllFragmentViews(rebuildLastAfterAnimation, showLastAfterAnimation);
            rebuildAfterAnimation = false;
        }
    }

    public BasePage getLastFragment() {
        if (fragmentsStack.isEmpty()) {
            return null;
        }
        return fragmentsStack.get(fragmentsStack.size() - 1);
    }

    private void presentFragmentInternalRemoveOld(boolean removeLast, final BasePage fragment) {
        if (fragment == null) {
            return;
        }
        fragment.onBecomeFullyHidden();
        fragment.onPause();
        if (removeLast) {
            fragment.onFragmentDestroy();
            fragment.setParentLayout(null);
            fragmentsStack.remove(fragment);
        } else {
            if (fragment.fragmentView != null) {
                ViewGroup parent = (ViewGroup) fragment.fragmentView.getParent();
                if (parent != null) {
                    fragment.onRemoveFromParent();
                    parent.removeViewInLayout(fragment.fragmentView);
                }
            }
            if (fragment.actionBar != null && fragment.actionBar.shouldAddToContainer()) {
                ViewGroup parent = (ViewGroup) fragment.actionBar.getParent();
                if (parent != null) {
                    parent.removeViewInLayout(fragment.actionBar);
                }
            }
        }
        containerViewBack.setVisibility(View.INVISIBLE);
    }

    public boolean presentFragmentAsPreview(BasePage fragment) {
        return presentFragment(fragment, false, false, true, true);
    }

    public boolean presentFragment(BasePage fragment) {
        return presentFragment(fragment, false, false, true, false);
    }

    public boolean presentFragment(BasePage fragment, boolean removeLast) {
        return presentFragment(fragment, removeLast, false, true, false);
    }

    public boolean isInPreviewMode() {
        return inPreviewMode || transitionAnimationPreviewMode;
    }

    /**
     * 显示一个Fragment
     * @param fragment fragment
     * @param removeLast 添加前移除最后一个
     * @param forceWithoutAnimation 强制不用动画
     * @param check
     * @param preview 预览模式
     * @return 是否显示成功
     */
    public boolean presentFragment(final BasePage fragment, final boolean removeLast, boolean forceWithoutAnimation, boolean check, final boolean preview) {
        if (fragment == null
                || checkTransitionAnimation()
                || delegate != null && check && !delegate.needPresentFragment(fragment, removeLast, forceWithoutAnimation, this)
                || !fragment.onFragmentCreate()) {
            return false;
        }
        fragment.setInPreviewMode(preview);
        if (parentActivity.getCurrentFocus() != null && fragment.hideKeyboardOnShow()) {
            Keyboard.hideKeyboard(parentActivity.getCurrentFocus());
        }
        boolean needAnimation = preview || !forceWithoutAnimation && Store.view_animations;

        final BasePage currentFragment = !fragmentsStack.isEmpty() ? fragmentsStack.get(fragmentsStack.size() - 1) : null;

        //添加到容器 containerViewBack
        fragment.setParentLayout(this);
        View fragmentView = fragment.fragmentView;
        if (fragmentView == null) {
            fragmentView = fragment.createView(parentActivity);
        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                fragment.onRemoveFromParent();
                parent.removeView(fragmentView);
            }
        }
        containerViewBack.addView(fragmentView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragmentView.getLayoutParams();
        layoutParams.width = LayoutHelper.MATCH_PARENT;
        layoutParams.height = LayoutHelper.MATCH_PARENT;
        if (preview) {
            layoutParams.rightMargin = layoutParams.leftMargin = Space.dp(8);
            layoutParams.topMargin = layoutParams.bottomMargin = Space.dp(46);
            layoutParams.topMargin += Space.statusBarHeight;
        } else {
            layoutParams.topMargin = layoutParams.bottomMargin = layoutParams.rightMargin = layoutParams.leftMargin = 0;
        }
        fragmentView.setLayoutParams(layoutParams);
        if (fragment.actionBar != null && fragment.actionBar.shouldAddToContainer()) {
            if (removeActionBarExtraHeight) {
                fragment.actionBar.setOccupyStatusBar(false);
            }
            ViewGroup parent = (ViewGroup) fragment.actionBar.getParent();
            if (parent != null) {
                parent.removeView(fragment.actionBar);
            }
            containerViewBack.addView(fragment.actionBar);
            fragment.actionBar.setTitleOverlayText(titleOverlayText, titleOverlayTextId, overlayAction);
        }
        fragmentsStack.add(fragment);
        fragment.onResume();
        currentActionBar = fragment.actionBar;
        if (!fragment.hasOwnBackground && fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(ColorManager.getColor(KeyHub.key_windowBackgroundWhite));
        }

        //侧滑返回功能：双指针沿栈顶移动
        LayoutContainer temp = containerView;
        containerView = containerViewBack;
        containerViewBack = temp;
        containerView.setVisibility(View.VISIBLE);
        setInnerTranslationX(0);
        containerView.setTranslationY(0);

        if (preview) {
            if (Build.VERSION.SDK_INT >= 21) {
                fragmentView.setOutlineProvider(new ViewOutlineProvider() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(0, Space.statusBarHeight, view.getMeasuredWidth(), view.getMeasuredHeight(), Space.dp(6));
                    }
                });
                fragmentView.setClipToOutline(true);
                fragmentView.setElevation(Space.dp(4));
            }
            if (previewBackgroundDrawable == null) {
                previewBackgroundDrawable = new ColorDrawable(0x2e000000);
            }
            previewBackgroundDrawable.setAlpha(0);
            moveUpDrawable.setAlpha(0);
        }

        bringContainerViewToFront();
        if (!needAnimation) {
            presentFragmentInternalRemoveOld(removeLast, currentFragment);
            if (backgroundView != null) {
                backgroundView.setVisibility(VISIBLE);
            }
        }

        //动画
        loadDescFor(fragment);

        if (needAnimation || preview) {
            if (useAlphaAnimations && fragmentsStack.size() == 1) {
                presentFragmentInternalRemoveOld(removeLast, currentFragment);

                transitionAnimationStartTime = System.currentTimeMillis();
                transitionAnimationInProgress = true;
                layoutToIgnore = containerView;
                onOpenAnimationEndRunnable = () -> {
                    if (currentFragment != null) {
                        currentFragment.onTransitionAnimationEnd(false, false);
                    }
                    fragment.onTransitionAnimationEnd(true, false);
                    fragment.onBecomeFullyVisible();
                };
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f, 1.0f));
                if (backgroundView != null) {
                    backgroundView.setVisibility(VISIBLE);
                    animators.add(ObjectAnimator.ofFloat(backgroundView, View.ALPHA, 0.0f, 1.0f));
                }
                if (currentFragment != null) {
                    currentFragment.onTransitionAnimationStart(false, false);
                }
                fragment.onTransitionAnimationStart(true, false);
                currentAnimation = new AnimatorSet();
                currentAnimation.playTogether(animators);
                currentAnimation.setInterpolator(accelerateDecelerateInterpolator);
                currentAnimation.setDuration(200);
                currentAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onAnimationEndCheck(false);
                    }
                });
                currentAnimation.start();
            } else {
                transitionAnimationPreviewMode = preview;
                transitionAnimationStartTime = System.currentTimeMillis();
                transitionAnimationInProgress = true;
                layoutToIgnore = containerView;
                onOpenAnimationEndRunnable = () -> {
                    if (preview) {
                        inPreviewMode = true;
                        transitionAnimationPreviewMode = false;
                        containerView.setScaleX(1.0f);
                        containerView.setScaleY(1.0f);
                    } else {
                        presentFragmentInternalRemoveOld(removeLast, currentFragment);
                        containerView.setTranslationX(0);
                    }
                    if (currentFragment != null) {
                        currentFragment.onTransitionAnimationEnd(false, false);
                    }
                    fragment.onTransitionAnimationEnd(true, false);
                    fragment.onBecomeFullyVisible();
                };
                if (currentFragment != null) {
                    currentFragment.onTransitionAnimationStart(false, false);
                }
                fragment.onTransitionAnimationStart(true, false);
                oldFragment = currentFragment;
                newFragment = fragment;
                AnimatorSet animation = null;
                if (!preview) {
                    animation = fragment.onCustomTransitionAnimation(true, () -> onAnimationEndCheck(false));
                }
                if (animation == null) {
                    containerView.setAlpha(0.0f);
                    if (preview) {
                        containerView.setTranslationX(0.0f);
                        containerView.setScaleX(0.9f);
                        containerView.setScaleY(0.9f);
                    } else {
                        containerView.setTranslationX(48.0f);
                        containerView.setScaleX(1.0f);
                        containerView.setScaleY(1.0f);
                    }
                    if (containerView.isKeyboardVisible || containerViewBack.isKeyboardVisible) {
                        waitingForKeyboardCloseRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (waitingForKeyboardCloseRunnable != this) {
                                    return;
                                }
                                waitingForKeyboardCloseRunnable = null;
                                startLayoutAnimation(true, true, preview);
                            }
                        };
                        UIThread.runOnUIThread(waitingForKeyboardCloseRunnable, 200);
                    } else if (fragment.needDelayOpenAnimation()) {
                        delayedOpenAnimationRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (delayedOpenAnimationRunnable != this) {
                                    return;
                                }
                                delayedOpenAnimationRunnable = null;
                                startLayoutAnimation(true, true, preview);
                            }
                        };
                        UIThread.runOnUIThread(delayedOpenAnimationRunnable, 200);
                    } else {
                        startLayoutAnimation(true, true, preview);
                    }
                } else {
                    containerView.setAlpha(1.0f);
                    containerView.setTranslationX(0.0f);
                    currentAnimation = animation;
                }
            }
        } else {
            if (backgroundView != null) {
                backgroundView.setAlpha(1.0f);
                backgroundView.setVisibility(VISIBLE);
            }
            if (currentFragment != null) {
                currentFragment.onTransitionAnimationStart(false, false);
                currentFragment.onTransitionAnimationEnd(false, false);
            }
            fragment.onTransitionAnimationStart(true, false);
            fragment.onTransitionAnimationEnd(true, false);
            fragment.onBecomeFullyVisible();
        }
        return true;
    }

    public boolean addFragmentToStack(BasePage fragment) {
        return addFragmentToStack(fragment, -1);
    }

    public boolean addFragmentToStack(BasePage fragment, int position) {
        if (delegate != null && !delegate.needAddFragmentToStack(fragment, this) || !fragment.onFragmentCreate()) {
            return false;
        }
        fragment.setParentLayout(this);
        if (position == -1) {
            if (!fragmentsStack.isEmpty()) {
                BasePage previousFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                previousFragment.onPause();
                if (previousFragment.actionBar != null && previousFragment.actionBar.shouldAddToContainer()) {
                    ViewGroup parent = (ViewGroup) previousFragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeView(previousFragment.actionBar);
                    }
                }
                if (previousFragment.fragmentView != null) {
                    ViewGroup parent = (ViewGroup) previousFragment.fragmentView.getParent();
                    if (parent != null) {
                        previousFragment.onRemoveFromParent();
                        parent.removeView(previousFragment.fragmentView);
                    }
                }
            }
            fragmentsStack.add(fragment);
        } else {
            fragmentsStack.add(position, fragment);
        }
        return true;
    }

    private void closeLastFragmentInternalRemoveOld(BasePage fragment) {
        fragment.onPause();
        fragment.onFragmentDestroy();
        fragment.setParentLayout(null);
        fragmentsStack.remove(fragment);
        containerViewBack.setVisibility(View.INVISIBLE);
        bringContainerViewToFront();
    }

    private void bringContainerViewToFront() {
        bringChildToFront(containerView);
        if (getChildCount() > 2) {
            final Bulletin bulletin = Bulletin.find(this);
            if (bulletin != null) {
                bulletin.getLayout().bringToFront();
                bulletin.hide();
            }
        }
    }

    public void movePreviewFragment(float dy) {
        if (!inPreviewMode || transitionAnimationPreviewMode) {
            return;
        }
        float currentTranslation = containerView.getTranslationY();
        float nextTranslation = -dy;
        if (nextTranslation > 0) {
            nextTranslation = 0;
        } else if (nextTranslation < -Space.dp(60)) {
            previewOpenAnimationInProgress = true;
            inPreviewMode = false;
            nextTranslation = 0;

            BasePage prevFragment = fragmentsStack.get(fragmentsStack.size() - 2);
            BasePage fragment = fragmentsStack.get(fragmentsStack.size() - 1);

            if (Build.VERSION.SDK_INT >= 21) {
                fragment.fragmentView.setOutlineProvider(null);
                fragment.fragmentView.setClipToOutline(false);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragment.fragmentView.getLayoutParams();
            layoutParams.topMargin = layoutParams.bottomMargin = layoutParams.rightMargin = layoutParams.leftMargin = 0;
            fragment.fragmentView.setLayoutParams(layoutParams);

            presentFragmentInternalRemoveOld(false, prevFragment);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(fragment.fragmentView, View.SCALE_X, 1.0f, 1.05f, 1.0f),
                    ObjectAnimator.ofFloat(fragment.fragmentView, View.SCALE_Y, 1.0f, 1.05f, 1.0f));
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(new CubicBezierInterpolator(0.42, 0.0, 0.58, 1.0));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    previewOpenAnimationInProgress = false;
                }
            });
            animatorSet.start();
            performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

            fragment.setInPreviewMode(false);
        }
        if (currentTranslation != nextTranslation) {
            containerView.setTranslationY(nextTranslation);
            invalidate();
        }
    }

    public void finishPreviewFragment() {
        if (!inPreviewMode && !transitionAnimationPreviewMode) {
            return;
        }
        if (delayedOpenAnimationRunnable != null) {
            UIThread.cancelRunOnUIThread(delayedOpenAnimationRunnable);
            delayedOpenAnimationRunnable = null;
        }
        closeLastFragment(true);
    }

    /**
     * 关闭最后一个Fragment
     * @param animated 是否显示动画
     */
    public void closeLastFragment(boolean animated) {
        if (delegate != null && !delegate.needCloseLastFragment(this) || checkTransitionAnimation() || fragmentsStack.isEmpty()) {
            return;
        }
        if (parentActivity.getCurrentFocus() != null) {
            Keyboard.hideKeyboard(parentActivity.getCurrentFocus());
        }
        setInnerTranslationX(0);
        boolean needAnimation = inPreviewMode || transitionAnimationPreviewMode || animated && Store.view_animations;
        final BasePage currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
        BasePage previousFragment = null;
        if (fragmentsStack.size() > 1) {
            previousFragment = fragmentsStack.get(fragmentsStack.size() - 2);
        }

        if (previousFragment != null) {
            //存在前一个Fragment
            LayoutContainer temp = containerView;
            containerView = containerViewBack;
            containerViewBack = temp;

            previousFragment.setParentLayout(this);
            View fragmentView = previousFragment.fragmentView;
            if (fragmentView == null) {
                fragmentView = previousFragment.createView(parentActivity);
            }

            if (!inPreviewMode) {
                containerView.setVisibility(View.VISIBLE);
                ViewGroup parent = (ViewGroup) fragmentView.getParent();
                if (parent != null) {
                    previousFragment.onRemoveFromParent();
                    try {
                        parent.removeView(fragmentView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                containerView.addView(fragmentView);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragmentView.getLayoutParams();
                layoutParams.width = LayoutHelper.MATCH_PARENT;
                layoutParams.height = LayoutHelper.MATCH_PARENT;
                layoutParams.topMargin = layoutParams.bottomMargin = layoutParams.rightMargin = layoutParams.leftMargin = 0;
                fragmentView.setLayoutParams(layoutParams);
                if (previousFragment.actionBar != null && previousFragment.actionBar.shouldAddToContainer()) {
                    if (removeActionBarExtraHeight) {
                        previousFragment.actionBar.setOccupyStatusBar(false);
                    }
                    parent = (ViewGroup) previousFragment.actionBar.getParent();
                    if (parent != null) {
                        parent.removeView(previousFragment.actionBar);
                    }
                    containerView.addView(previousFragment.actionBar);
                    previousFragment.actionBar.setTitleOverlayText(titleOverlayText, titleOverlayTextId, overlayAction);
                }
            }

            newFragment = previousFragment;
            oldFragment = currentFragment;
            previousFragment.onTransitionAnimationStart(true, true);
            currentFragment.onTransitionAnimationStart(false, true);
            previousFragment.onResume();
            loadDescFor(previousFragment);
            currentActionBar = previousFragment.actionBar;
            if (!previousFragment.hasOwnBackground && fragmentView.getBackground() == null) {
                fragmentView.setBackgroundColor(ColorManager.getColor(KeyHub.key_windowBackgroundWhite));
            }

            if (!needAnimation) {
                closeLastFragmentInternalRemoveOld(currentFragment);
            }

            if (needAnimation) {
                transitionAnimationStartTime = System.currentTimeMillis();
                transitionAnimationInProgress = true;
                layoutToIgnore = containerView;
                final BasePage previousFragmentFinal = previousFragment;
                onCloseAnimationEndRunnable = () -> {
                    if (inPreviewMode || transitionAnimationPreviewMode) {
                        containerViewBack.setScaleX(1.0f);
                        containerViewBack.setScaleY(1.0f);
                        inPreviewMode = false;
                        transitionAnimationPreviewMode = false;
                    } else {
                        containerViewBack.setTranslationX(0);
                    }
                    closeLastFragmentInternalRemoveOld(currentFragment);
                    currentFragment.onTransitionAnimationEnd(false, true);
                    previousFragmentFinal.onTransitionAnimationEnd(true, true);
                    previousFragmentFinal.onBecomeFullyVisible();
                };
                AnimatorSet animation = null;
                if (!inPreviewMode && !transitionAnimationPreviewMode) {
                    animation = currentFragment.onCustomTransitionAnimation(false, () -> onAnimationEndCheck(false));
                }
                if (animation == null) {
                    if (containerView.isKeyboardVisible || containerViewBack.isKeyboardVisible) {
                        waitingForKeyboardCloseRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (waitingForKeyboardCloseRunnable != this) {
                                    return;
                                }
                                waitingForKeyboardCloseRunnable = null;
                                startLayoutAnimation(false, true, false);
                            }
                        };
                        UIThread.runOnUIThread(waitingForKeyboardCloseRunnable, 200);
                    } else {
                        startLayoutAnimation(false, true, inPreviewMode || transitionAnimationPreviewMode);
                    }
                } else {
                    currentAnimation = animation;
                }
            } else {
                currentFragment.onTransitionAnimationEnd(false, true);
                previousFragment.onTransitionAnimationEnd(true, true);
                previousFragment.onBecomeFullyVisible();
            }
        } else {
            //当前Fragment就是最后一个Fragment
            if (useAlphaAnimations) {
                transitionAnimationStartTime = System.currentTimeMillis();
                transitionAnimationInProgress = true;
                layoutToIgnore = containerView;

                onCloseAnimationEndRunnable = () -> {
                    removeFragmentFromStackInternal(currentFragment);
                    setVisibility(GONE);
                    if (backgroundView != null) {
                        backgroundView.setVisibility(GONE);
                    }
                    if (drawerLayoutContainer != null) {
                        drawerLayoutContainer.setAllowOpenDrawer(true, false);
                    }
                };

                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f, 0.0f));
                if (backgroundView != null) {
                    animators.add(ObjectAnimator.ofFloat(backgroundView, View.ALPHA, 1.0f, 0.0f));
                }

                currentAnimation = new AnimatorSet();
                currentAnimation.playTogether(animators);
                currentAnimation.setInterpolator(accelerateDecelerateInterpolator);
                currentAnimation.setDuration(200);
                currentAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        transitionAnimationStartTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onAnimationEndCheck(false);
                    }
                });
                currentAnimation.start();
            } else {
                removeFragmentFromStackInternal(currentFragment);
                setVisibility(GONE);
                if (backgroundView != null) {
                    backgroundView.setVisibility(GONE);
                }
            }
        }
    }

    /**
     * 显示最后一个Fragment
     */
    public void showLastFragment() {
        if (fragmentsStack.isEmpty()) {
            return;
        }
        for (int a = 0; a < fragmentsStack.size() - 1; a++) {
            BasePage previousFragment = fragmentsStack.get(a);
            if (previousFragment.actionBar != null && previousFragment.actionBar.shouldAddToContainer()) {
                ViewGroup parent = (ViewGroup) previousFragment.actionBar.getParent();
                if (parent != null) {
                    parent.removeView(previousFragment.actionBar);
                }
            }
            if (previousFragment.fragmentView != null) {
                ViewGroup parent = (ViewGroup) previousFragment.fragmentView.getParent();
                if (parent != null) {
                    previousFragment.onPause();
                    previousFragment.onRemoveFromParent();
                    parent.removeView(previousFragment.fragmentView);
                }
            }
        }
        BasePage previousFragment = fragmentsStack.get(fragmentsStack.size() - 1);
        previousFragment.setParentLayout(this);
        View fragmentView = previousFragment.fragmentView;
        if (fragmentView == null) {
            fragmentView = previousFragment.createView(parentActivity);
        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                previousFragment.onRemoveFromParent();
                parent.removeView(fragmentView);
            }
        }
        containerView.addView(fragmentView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        if (previousFragment.actionBar != null && previousFragment.actionBar.shouldAddToContainer()) {
            if (removeActionBarExtraHeight) {
                previousFragment.actionBar.setOccupyStatusBar(false);
            }
            ViewGroup parent = (ViewGroup) previousFragment.actionBar.getParent();
            if (parent != null) {
                parent.removeView(previousFragment.actionBar);
            }
            containerView.addView(previousFragment.actionBar);
            previousFragment.actionBar.setTitleOverlayText(titleOverlayText, titleOverlayTextId, overlayAction);
        }
        previousFragment.onResume();
        currentActionBar = previousFragment.actionBar;
        if (!previousFragment.hasOwnBackground && fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(ColorManager.getColor(KeyHub.key_windowBackgroundWhite));
        }
    }

    private void removeFragmentFromStackInternal(BasePage fragment) {
        fragment.onPause();
        fragment.onFragmentDestroy();
        fragment.setParentLayout(null);
        fragmentsStack.remove(fragment);
    }

    public void removeFragmentFromStack(int num) {
        if (num >= fragmentsStack.size()) {
            return;
        }
        removeFragmentFromStackInternal(fragmentsStack.get(num));
    }

    public void removeFragmentFromStack(BasePage fragment) {
        if (useAlphaAnimations && fragmentsStack.size() == 1 && Space.isTablet()) {
            closeLastFragment(true);
        } else {
            if (delegate != null && fragmentsStack.size() == 1 && Space.isTablet()) {
                delegate.needCloseLastFragment(this);
            }
            removeFragmentFromStackInternal(fragment);
        }
    }

    public void removeAllFragments() {
        for (int a = 0; a < fragmentsStack.size(); a++) {
            removeFragmentFromStackInternal(fragmentsStack.get(a));
            a--;
        }
    }

    public void rebuildAllFragmentViews(boolean last, boolean showLastAfter) {
        if (transitionAnimationInProgress || startedTracking) {
            rebuildAfterAnimation = true;
            rebuildLastAfterAnimation = last;
            showLastAfterAnimation = showLastAfter;
            return;
        }
        int size = fragmentsStack.size();
        if (!last) {
            size--;
        }
        if (inPreviewMode) {
            size--;
        }
        for (int a = 0; a < size; a++) {
            fragmentsStack.get(a).clearViews();
            fragmentsStack.get(a).setParentLayout(this);
        }
        if (delegate != null) {
            delegate.onRebuildAllFragments(this, last);
        }
        if (showLastAfter) {
            showLastFragment();
        }
    }
    //endregion

    protected void loadDescFor(BasePage page) {}

    //region ActionMode控制
    public void onActionModeStarted(Object mode) {
        if (currentActionBar != null) {
            currentActionBar.setVisibility(GONE);
        }
        inActionMode = true;
    }

    public void onActionModeFinished(Object mode) {
        if (currentActionBar != null) {
            currentActionBar.setVisibility(VISIBLE);
        }
        inActionMode = false;
    }
    //endregion
    //endregion

    //region setter and getter
    public void setUseAlphaAnimations(boolean value) {
        useAlphaAnimations = value;
    }

    public void setBackgroundView(View view) {
        backgroundView = view;
    }

    public void setDrawerLayoutContainer(DrawerLayoutContainer layout) {
        drawerLayoutContainer = layout;
    }

    public DrawerLayoutContainer getDrawerLayoutContainer() {
        return drawerLayoutContainer;
    }

    public void setRemoveActionBarExtraHeight(boolean value) {
        removeActionBarExtraHeight = value;
    }

    public boolean isPreviewOpenAnimationInProgress() {
        return previewOpenAnimationInProgress;
    }

    @Keep
    public void setInnerTranslationX(float value) {
        innerTranslationX = value;
        invalidate();
    }

    @Keep
    public float getInnerTranslationX() {
        return innerTranslationX;
    }

    public int getCurrentPanTranslationY() {
        return currentPanTranslationY;
    }

    public void setDelegate(ActionBarLayoutDelegate actionBarLayoutDelegate) {
        delegate = actionBarLayoutDelegate;
    }

    public void setTitleOverlayText(String title, int titleId, Runnable action) {
        titleOverlayText = title;
        titleOverlayTextId = titleId;
        overlayAction = action;
        for (int a = 0; a < fragmentsStack.size(); a++) {
            BasePage fragment = fragmentsStack.get(a);
            if (fragment.actionBar != null) {
                fragment.actionBar.setTitleOverlayText(titleOverlayText, titleOverlayTextId, action);
            }
        }
    }
    //endregion

    //region 功能配置
    public boolean extendActionMode(Menu menu) {
        return !fragmentsStack.isEmpty() && fragmentsStack.get(fragmentsStack.size() - 1).extendActionMode(menu);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
    //endregion
}
