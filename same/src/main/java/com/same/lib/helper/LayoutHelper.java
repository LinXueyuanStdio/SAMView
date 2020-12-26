package com.same.lib.helper;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.same.lib.util.Space;
import com.same.lib.util.Store;

import androidx.core.view.ViewCompat;

public class LayoutHelper {

    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;

    private static int getSize(float size) {
        return (int) (size < 0 ? size : Space.dp(size));
    }

    //region Gravity

    private static int getAbsoluteGravity(int gravity) {
        return Gravity.getAbsoluteGravity(gravity, Store.isRTL ? ViewCompat.LAYOUT_DIRECTION_RTL : ViewCompat.LAYOUT_DIRECTION_LTR);
    }

    @SuppressLint("RtlHardcoded")
    public static int getAbsoluteGravityStart() {
        return Store.isRTL ? Gravity.RIGHT : Gravity.LEFT;
    }

    @SuppressLint("RtlHardcoded")
    public static int getAbsoluteGravityEnd() {
        return Store.isRTL ? Gravity.LEFT : Gravity.RIGHT;
    }

    //endregion

    //region ScrollView

    public static ScrollView.LayoutParams createScroll(int width, int height, int gravity) {
        return new ScrollView.LayoutParams(getSize(width), getSize(height), gravity);
    }

    public static ScrollView.LayoutParams createScroll(int width, int height, int gravity, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        ScrollView.LayoutParams layoutParams = new ScrollView.LayoutParams(getSize(width), getSize(height), gravity);
        layoutParams.leftMargin = Space.dp(leftMargin);
        layoutParams.topMargin = Space.dp(topMargin);
        layoutParams.rightMargin = Space.dp(rightMargin);
        layoutParams.bottomMargin = Space.dp(bottomMargin);
        return layoutParams;
    }

    //endregion

    //region FrameLayout

    public static FrameLayout.LayoutParams createFrame(int width, float height, int gravity, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getSize(width), getSize(height), gravity);
        layoutParams.setMargins(Space.dp(leftMargin), Space.dp(topMargin), Space.dp(rightMargin), Space.dp(bottomMargin));
        return layoutParams;
    }

    public static FrameLayout.LayoutParams createFrame(int width, int height, int gravity) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height), gravity);
    }

    public static FrameLayout.LayoutParams createFrame(int width, float height) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height));
    }

    public static FrameLayout.LayoutParams createFrame(float width, float height, int gravity) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height), gravity);
    }

    public static FrameLayout.LayoutParams createFrameRelatively(float width, float height, int gravity, float startMargin, float topMargin, float endMargin, float bottomMargin) {
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getSize(width), getSize(height), getAbsoluteGravity(gravity));
        layoutParams.leftMargin = Space.dp(Store.isRTL ? endMargin : startMargin);
        layoutParams.topMargin = Space.dp(topMargin);
        layoutParams.rightMargin = Space.dp(Store.isRTL ? startMargin : endMargin);
        layoutParams.bottomMargin = Space.dp(bottomMargin);
        return layoutParams;
    }

    public static FrameLayout.LayoutParams createFrameRelatively(float width, float height, int gravity) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height), getAbsoluteGravity(gravity));
    }

    //endregion

    //region RelativeLayout

    public static RelativeLayout.LayoutParams createRelative(float width, float height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignParent, int alignRelative, int anchorRelative) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getSize(width), getSize(height));
        if (alignParent >= 0) {
            layoutParams.addRule(alignParent);
        }
        if (alignRelative >= 0 && anchorRelative >= 0) {
            layoutParams.addRule(alignRelative, anchorRelative);
        }
        layoutParams.leftMargin = Space.dp(leftMargin);
        layoutParams.topMargin = Space.dp(topMargin);
        layoutParams.rightMargin = Space.dp(rightMargin);
        layoutParams.bottomMargin = Space.dp(bottomMargin);
        return layoutParams;
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        return createRelative(width, height, leftMargin, topMargin, rightMargin, bottomMargin, -1, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignParent) {
        return createRelative(width, height, leftMargin, topMargin, rightMargin, bottomMargin, alignParent, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(float width, float height, int leftMargin, int topMargin, int rightMargin, int bottomMargin, int alignRelative, int anchorRelative) {
        return createRelative(width, height, leftMargin, topMargin, rightMargin, bottomMargin, -1, alignRelative, anchorRelative);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignParent, int alignRelative, int anchorRelative) {
        return createRelative(width, height, 0, 0, 0, 0, alignParent, alignRelative, anchorRelative);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height) {
        return createRelative(width, height, 0, 0, 0, 0, -1, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignParent) {
        return createRelative(width, height, 0, 0, 0, 0, alignParent, -1, -1);
    }

    public static RelativeLayout.LayoutParams createRelative(int width, int height, int alignRelative, int anchorRelative) {
        return createRelative(width, height, 0, 0, 0, 0, -1, alignRelative, anchorRelative);
    }

    //endregion

    //region LinearLayout

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int gravity, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
        layoutParams.setMargins(Space.dp(leftMargin), Space.dp(topMargin), Space.dp(rightMargin), Space.dp(bottomMargin));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
        layoutParams.setMargins(Space.dp(leftMargin), Space.dp(topMargin), Space.dp(rightMargin), Space.dp(bottomMargin));
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, int gravity, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height));
        layoutParams.setMargins(Space.dp(leftMargin), Space.dp(topMargin), Space.dp(rightMargin), Space.dp(bottomMargin));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height));
        layoutParams.setMargins(Space.dp(leftMargin), Space.dp(topMargin), Space.dp(rightMargin), Space.dp(bottomMargin));
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, int gravity) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height));
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height, float weight) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height), weight);
    }

    public static LinearLayout.LayoutParams createLinear(int width, int height) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height));
    }

    public static LinearLayout.LayoutParams createLinearRelatively(float width, float height, int gravity, float startMargin, float topMargin, float endMargin, float bottomMargin) {
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getSize(width), getSize(height), getAbsoluteGravity(gravity));
        layoutParams.leftMargin = Space.dp(Store.isRTL ? endMargin : startMargin);
        layoutParams.topMargin = Space.dp(topMargin);
        layoutParams.rightMargin = Space.dp(Store.isRTL ? startMargin : endMargin);
        layoutParams.bottomMargin = Space.dp(bottomMargin);
        return layoutParams;
    }

    public static LinearLayout.LayoutParams createLinearRelatively(float width, float height, int gravity) {
        return new LinearLayout.LayoutParams(getSize(width), getSize(height), getAbsoluteGravity(gravity));
    }

    //endregion
}
