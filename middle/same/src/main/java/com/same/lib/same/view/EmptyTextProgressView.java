package com.same.lib.same.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.same.lib.core.RadialProgressView;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.same.R;
import com.same.lib.theme.KeyHub;
import com.same.lib.util.ColorManager;
import com.same.lib.util.Lang;
import com.same.lib.util.Space;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/27
 * @description null
 * @usage null
 */
public class EmptyTextProgressView extends FrameLayout {

    private TextView textView;
    private RadialProgressView progressBar;
    private boolean inLayout;
    private int showAtPos;

    public EmptyTextProgressView(Context context) {
        super(context);

        progressBar = new RadialProgressView(context);
        progressBar.setVisibility(INVISIBLE);
        addView(progressBar, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setTextColor(ColorManager.getColor(KeyHub.key_emptyListPlaceholder));
        textView.setGravity(Gravity.CENTER);
        textView.setVisibility(INVISIBLE);
        textView.setPadding(Space.dp(20), 0, Space.dp(20), 0);
        textView.setText(Lang.getString(context, "NoResult", R.string.NoResult));
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        setOnTouchListener((v, event) -> true);
    }

    public void showProgress() {
        textView.setVisibility(INVISIBLE);
        progressBar.setVisibility(VISIBLE);
    }

    public void showTextView() {
        textView.setVisibility(VISIBLE);
        progressBar.setVisibility(INVISIBLE);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setProgressBarColor(int color) {
        progressBar.setProgressColor(color);
    }

    public void setTopImage(int resId) {
        if (resId == 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            Drawable drawable = getContext().getResources().getDrawable(resId).mutate();
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ColorManager.getColor(KeyHub.key_emptyListPlaceholder), PorterDuff.Mode.MULTIPLY));
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            textView.setCompoundDrawablePadding(Space.dp(1));
        }
    }

    public void setTextSize(int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public void setShowAtCenter(boolean value) {
        showAtPos = value ? 1 : 0;
    }

    public void setShowAtTop(boolean value) {
        showAtPos = value ? 2 : 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        inLayout = true;
        int width = r - l;
        int height = b - t;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            int x = (width - child.getMeasuredWidth()) / 2;
            int y;
            if (showAtPos == 2) {
                y = (Space.dp(100) - child.getMeasuredHeight()) / 2 + getPaddingTop();
            } else if (showAtPos == 1) {
                y = (height / 2 - child.getMeasuredHeight()) / 2 + getPaddingTop();
            } else {
                y = (height - child.getMeasuredHeight()) / 2 + getPaddingTop();
            }
            child.layout(x, y, x + child.getMeasuredWidth(), y + child.getMeasuredHeight());
        }
        inLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!inLayout) {
            super.requestLayout();
        }
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
