package com.same.ui.intro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.same.lib.theme.Theme;
import com.same.lib.util.Space;

import androidx.viewpager.widget.ViewPager;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class BottomPagesView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progress;
    private int scrollPosition;
    private int currentPage;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private RectF rect = new RectF();
    private float animatedProgress;
    private ViewPager viewPager;
    private int pagesCount;

    private String colorKey;
    private String selectedColorKey;

    public BottomPagesView(Context context, ViewPager pager, int count) {
        super(context);
        viewPager = pager;
        pagesCount = count;
    }

    public void setPageOffset(int position, float offset) {
        progress = offset;
        scrollPosition = position;
        invalidate();
    }

    public void setCurrentPage(int page) {
        currentPage = page;
        invalidate();
    }

    public void setColor(String key, String selectedKey) {
        colorKey = key;
        selectedColorKey = selectedKey;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float d = Space.dp(5);
        if (colorKey != null) {
            paint.setColor((Theme.getColor(colorKey) & 0x00ffffff) | 0xb4000000);
        } else {
            paint.setColor(0xffbbbbbb);
        }
        int x;
        currentPage = viewPager.getCurrentItem();
        for (int a = 0; a < pagesCount; a++) {
            if (a == currentPage) {
                continue;
            }
            x = a * Space.dp(11);
            rect.set(x, 0, x + Space.dp(5), Space.dp(5));
            canvas.drawRoundRect(rect, Space.dp(2.5f), Space.dp(2.5f), paint);
        }
        if (selectedColorKey != null) {
            paint.setColor(Theme.getColor(selectedColorKey));
        } else {
            paint.setColor(0xff2ca5e0);
        }
        x = currentPage * Space.dp(11);
        if (progress != 0) {
            if (scrollPosition >= currentPage) {
                rect.set(x, 0, x + Space.dp(5) + Space.dp(11) * progress, Space.dp(5));
            } else {
                rect.set(x - Space.dp(11) * (1.0f - progress), 0, x + Space.dp(5), Space.dp(5));
            }
        } else {
            rect.set(x, 0, x + Space.dp(5), Space.dp(5));
        }
        canvas.drawRoundRect(rect, Space.dp(2.5f), Space.dp(2.5f), paint);
    }
}

