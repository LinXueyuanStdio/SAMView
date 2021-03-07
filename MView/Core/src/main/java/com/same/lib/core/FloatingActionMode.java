package com.same.lib.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import com.same.lib.util.Space;

import java.util.Arrays;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/8/25
 * @description null
 * @usage null
 */
@TargetApi(23)
public final class FloatingActionMode extends ActionMode {

    private static final int MAX_HIDE_DURATION = 3000;
    private static final int MOVING_HIDE_DELAY = 50;
    private final Context mContext;
    private final ActionMode.Callback2 mCallback;
    private final Menu mMenu;
    private final Rect mContentRect;
    private final Rect mContentRectOnScreen;
    private final Rect mPreviousContentRectOnScreen;
    private final int[] mViewPositionOnScreen;
    private final int[] mPreviousViewPositionOnScreen;
    private final int[] mRootViewPositionOnScreen;
    private final Rect mViewRectOnScreen;
    private final Rect mPreviousViewRectOnScreen;
    private final Rect mScreenRect;
    private final View mOriginatingView;
    private final Point mDisplaySize;
    private final int mBottomAllowance;

    private final Runnable mMovingOff = new Runnable() {
        public void run() {
            if (isViewStillActive()) {
                mFloatingToolbarVisibilityHelper.setMoving(false);
                mFloatingToolbarVisibilityHelper.updateToolbarVisibility();
            }
        }
    };

    private final Runnable mHideOff = new Runnable() {
        public void run() {
            if (isViewStillActive()) {
                mFloatingToolbarVisibilityHelper.setHideRequested(false);
                mFloatingToolbarVisibilityHelper.updateToolbarVisibility();
            }
        }
    };

    private FloatingToolbar mFloatingToolbar;
    private FloatingToolbarVisibilityHelper mFloatingToolbarVisibilityHelper;

    public FloatingActionMode(Context context, ActionMode.Callback2 callback, View originatingView, FloatingToolbar floatingToolbar) {
        mContext = context;
        mCallback = callback;

        PopupMenu p = new PopupMenu(context, null);
        mMenu = p.getMenu();
        setType(ActionMode.TYPE_FLOATING);
        p.setOnMenuItemClickListener(menuItem -> mCallback.onActionItemClicked(FloatingActionMode.this, menuItem));
        mContentRect = new Rect();
        mContentRectOnScreen = new Rect();
        mPreviousContentRectOnScreen = new Rect();
        mViewPositionOnScreen = new int[2];
        mPreviousViewPositionOnScreen = new int[2];
        mRootViewPositionOnScreen = new int[2];
        mViewRectOnScreen = new Rect();
        mPreviousViewRectOnScreen = new Rect();
        mScreenRect = new Rect();
        mOriginatingView = originatingView;
        mOriginatingView.getLocationOnScreen(mViewPositionOnScreen);
        mBottomAllowance = Space.dp(20);
        mDisplaySize = new Point();
        setFloatingToolbar(floatingToolbar);
    }

    private void setFloatingToolbar(FloatingToolbar floatingToolbar) {
        mFloatingToolbar = floatingToolbar.setMenu(mMenu).setOnMenuItemClickListener(item -> mCallback.onActionItemClicked(FloatingActionMode.this, item));
        mFloatingToolbarVisibilityHelper = new FloatingToolbarVisibilityHelper(mFloatingToolbar);
        mFloatingToolbarVisibilityHelper.activate();
    }

    @Override
    public void setTitle(CharSequence title) {
    }

    @Override
    public void setTitle(int resId) {
    }

    @Override
    public void setSubtitle(CharSequence subtitle) {
    }

    @Override
    public void setSubtitle(int resId) {
    }

    @Override
    public void setCustomView(View view) {
    }

    @Override
    public void invalidate() {
        mCallback.onPrepareActionMode(this, mMenu);
        invalidateContentRect();
    }

    @Override
    public void invalidateContentRect() {
        mCallback.onGetContentRect(this, mOriginatingView, mContentRect);
        if (mContentRect.left == 0 && mContentRect.right == 0) {
            mContentRect.left = 1;
            mContentRect.right = 1;
        }
        repositionToolbar();
    }

    public void updateViewLocationInWindow() {
        mOriginatingView.getLocationOnScreen(mViewPositionOnScreen);
        mOriginatingView.getRootView().getLocationOnScreen(mRootViewPositionOnScreen);
        mOriginatingView.getGlobalVisibleRect(mViewRectOnScreen);
        mViewRectOnScreen.offset(mRootViewPositionOnScreen[0], mRootViewPositionOnScreen[1]);
        if (!Arrays.equals(mViewPositionOnScreen, mPreviousViewPositionOnScreen) || !mViewRectOnScreen.equals(mPreviousViewRectOnScreen)) {
            repositionToolbar();
            mPreviousViewPositionOnScreen[0] = mViewPositionOnScreen[0];
            mPreviousViewPositionOnScreen[1] = mViewPositionOnScreen[1];
            mPreviousViewRectOnScreen.set(mViewRectOnScreen);
        }
    }

    private void repositionToolbar() {
        mContentRectOnScreen.set(mContentRect);
        final ViewParent parent = mOriginatingView.getParent();
        if (parent instanceof ViewGroup) {
            parent.getChildVisibleRect(mOriginatingView, mContentRectOnScreen, null);
            mContentRectOnScreen.offset(mRootViewPositionOnScreen[0], mRootViewPositionOnScreen[1]);
        } else {
            mContentRectOnScreen.offset(mViewPositionOnScreen[0], mViewPositionOnScreen[1]);
        }
        if (isContentRectWithinBounds()) {
            mFloatingToolbarVisibilityHelper.setOutOfBounds(false);
            mContentRectOnScreen.set(Math.max(mContentRectOnScreen.left, mViewRectOnScreen.left), Math.max(mContentRectOnScreen.top, mViewRectOnScreen.top), Math.min(mContentRectOnScreen.right, mViewRectOnScreen.right), Math.min(mContentRectOnScreen.bottom, mViewRectOnScreen.bottom + mBottomAllowance));
            if (!mContentRectOnScreen.equals(mPreviousContentRectOnScreen)) {
                mOriginatingView.removeCallbacks(mMovingOff);
                mFloatingToolbarVisibilityHelper.setMoving(true);
                mOriginatingView.postDelayed(mMovingOff, MOVING_HIDE_DELAY);
                mFloatingToolbar.setContentRect(mContentRectOnScreen);
                mFloatingToolbar.updateLayout();
            }
        } else {
            mFloatingToolbarVisibilityHelper.setOutOfBounds(true);
            mContentRectOnScreen.setEmpty();
        }
        mFloatingToolbarVisibilityHelper.updateToolbarVisibility();
        mPreviousContentRectOnScreen.set(mContentRectOnScreen);
    }

    private boolean isContentRectWithinBounds() {
        mContext.getSystemService(WindowManager.class).getDefaultDisplay().getRealSize(mDisplaySize);
        mScreenRect.set(0, 0, mDisplaySize.x, mDisplaySize.y);
        return intersectsClosed(mContentRectOnScreen, mScreenRect) && intersectsClosed(mContentRectOnScreen, mViewRectOnScreen);
    }

    private static boolean intersectsClosed(Rect a, Rect b) {
        return a.left <= b.right && b.left <= a.right && a.top <= b.bottom && b.top <= a.bottom;
    }

    @Override
    public void hide(long duration) {
        if (duration == ActionMode.DEFAULT_HIDE_DURATION) {
            duration = ViewConfiguration.getDefaultActionModeHideDuration();
        }
        duration = Math.min(MAX_HIDE_DURATION, duration);
        mOriginatingView.removeCallbacks(mHideOff);
        if (duration <= 0) {
            mHideOff.run();
        } else {
            mFloatingToolbarVisibilityHelper.setHideRequested(true);
            mFloatingToolbarVisibilityHelper.updateToolbarVisibility();
            mOriginatingView.postDelayed(mHideOff, duration);
        }
    }

    public void setOutsideTouchable(boolean outsideTouchable, PopupWindow.OnDismissListener onDismiss) {
        mFloatingToolbar.setOutsideTouchable(outsideTouchable, onDismiss);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        mFloatingToolbarVisibilityHelper.setWindowFocused(hasWindowFocus);
        mFloatingToolbarVisibilityHelper.updateToolbarVisibility();
    }

    @Override
    public void finish() {
        reset();
        mCallback.onDestroyActionMode(this);
    }

    @Override
    public Menu getMenu() {
        return mMenu;
    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    @Override
    public CharSequence getSubtitle() {
        return null;
    }

    @Override
    public View getCustomView() {
        return null;
    }

    @Override
    public MenuInflater getMenuInflater() {
        return new MenuInflater(mContext);
    }

    private void reset() {
        mFloatingToolbar.dismiss();
        mFloatingToolbarVisibilityHelper.deactivate();
        mOriginatingView.removeCallbacks(mMovingOff);
        mOriginatingView.removeCallbacks(mHideOff);
    }

    private boolean isViewStillActive() {
        return mOriginatingView.getWindowVisibility() == View.VISIBLE && mOriginatingView.isShown();
    }

    private static final class FloatingToolbarVisibilityHelper {
        private static final long MIN_SHOW_DURATION_FOR_MOVE_HIDE = 500;
        private final FloatingToolbar mToolbar;
        private boolean mHideRequested;
        private boolean mMoving;
        private boolean mOutOfBounds;
        private boolean mWindowFocused = true;
        private boolean mActive;
        private long mLastShowTime;

        public FloatingToolbarVisibilityHelper(FloatingToolbar toolbar) {
            mToolbar = toolbar;
        }

        public void activate() {
            mHideRequested = false;
            mMoving = false;
            mOutOfBounds = false;
            mWindowFocused = true;
            mActive = true;
        }

        public void deactivate() {
            mActive = false;
            mToolbar.dismiss();
        }

        public void setHideRequested(boolean hide) {
            mHideRequested = hide;
        }

        public void setMoving(boolean moving) {
            final boolean showingLongEnough = System.currentTimeMillis() - mLastShowTime > MIN_SHOW_DURATION_FOR_MOVE_HIDE;
            if (!moving || showingLongEnough) {
                mMoving = moving;
            }
        }

        public void setOutOfBounds(boolean outOfBounds) {
            mOutOfBounds = outOfBounds;
        }

        public void setWindowFocused(boolean windowFocused) {
            mWindowFocused = windowFocused;
        }

        public void updateToolbarVisibility() {
            if (!mActive) {
                return;
            }
            if (mHideRequested || mMoving || mOutOfBounds || !mWindowFocused) {
                mToolbar.hide();
            } else {
                mToolbar.show();
                mLastShowTime = System.currentTimeMillis();
            }
        }
    }
}
