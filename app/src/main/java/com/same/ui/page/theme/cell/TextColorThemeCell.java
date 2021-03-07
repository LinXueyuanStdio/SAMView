package com.same.ui.page.theme.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.same.lib.helper.LayoutHelper;
import com.same.lib.util.Space;

import static com.same.lib.base.SharedConfig.isRTL;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class TextColorThemeCell extends FrameLayout {

    private TextView textView;
    private boolean needDivider;
    private int currentColor;
    private float alpha = 1.0f;

    private static Paint colorPaint;

    public TextColorThemeCell(Context context) {
        super(context);

        if (colorPaint == null) {
            colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        textView = new TextView(context);
        textView.setTextColor(0xff212121);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        textView.setPadding(0, 0, 0, Space.dp(3));
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, (isRTL ? 21 : 21 + 36), 0, (isRTL ? 21 + 36 : 21), 0));
    }

    @Override
    public void setAlpha(float value) {
        alpha = value;
        invalidate();
    }

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Space.dp(50) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void setTextAndColor(CharSequence text, int color) {
        textView.setText(text);
        currentColor = color;
        setWillNotDraw(!needDivider && currentColor == 0);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentColor != 0) {
            colorPaint.setColor(currentColor);
            colorPaint.setAlpha((int) (255 * alpha));
            canvas.drawCircle(!isRTL ? Space.dp(28) : getMeasuredWidth() - Space.dp(28), getMeasuredHeight() / 2, Space.dp(10), colorPaint);
        }
    }
}
