package com.same.lib.checkbox;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.Keep;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/14
 * @description null
 * @usage null
 */
public class CheckBoxSquare extends View {

    private RectF rectF;

    private Bitmap drawBitmap;
    private Canvas drawCanvas;

    private float progress;
    private ObjectAnimator checkAnimator;

    private boolean attachedToWindow;
    private boolean isChecked;
    private boolean isDisabled;
    private boolean isAlert;

    Paint checkboxSquare_backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint checkboxSquare_checkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint checkboxSquare_eraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    int key_dialogCheckboxSquareUnchecked;
    int key_checkboxSquareUnchecked;
    int key_dialogCheckboxSquareBackground;
    int key_checkboxSquareBackground;
    int key_dialogCheckboxSquareDisabled;
    int key_checkboxSquareDisabled;
    int key_dialogCheckboxSquareCheck;
    int key_checkboxSquareCheck;

    private final static float progressBounceDiff = 0.2f;

    public CheckBoxSquare(Context context, boolean alert) {
        super(context);
        rectF = new RectF();
        drawBitmap = Bitmap.createBitmap(dp(18), dp(18), Bitmap.Config.ARGB_4444);
        drawCanvas = new Canvas(drawBitmap);
        isAlert = alert;
        checkboxSquare_checkPaint.setStyle(Paint.Style.STROKE);
        checkboxSquare_checkPaint.setStrokeWidth(dp(2));
        checkboxSquare_checkPaint.setStrokeCap(Paint.Cap.ROUND);
        checkboxSquare_eraserPaint.setColor(0);
        checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Keep
    public void setProgress(float value) {
        if (progress == value) {
            return;
        }
        progress = value;
        invalidate();
    }

    @Keep
    public float getProgress() {
        return progress;
    }

    private void cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator.cancel();
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        checkAnimator = ObjectAnimator.ofFloat(this, "progress", newCheckedState ? 1 : 0);
        checkAnimator.setDuration(300);
        checkAnimator.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        attachedToWindow = false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setChecked(boolean checked, boolean animated) {
        if (checked == isChecked) {
            return;
        }
        isChecked = checked;
        if (attachedToWindow && animated) {
            animateToCheckedState(checked);
        } else {
            cancelCheckAnimator();
            setProgress(checked ? 1.0f : 0.0f);
        }
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
        invalidate();
    }

    public boolean isChecked() {
        return isChecked;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (getVisibility() != VISIBLE) {
            return;
        }

        float checkProgress;
        float bounceProgress;
        int uncheckedColor = isAlert ? key_dialogCheckboxSquareUnchecked : key_checkboxSquareUnchecked;
        int color = isAlert ? key_dialogCheckboxSquareBackground : key_checkboxSquareBackground;
        if (progress <= 0.5f) {
            bounceProgress = checkProgress = progress / 0.5f;
            int rD = (int) ((Color.red(color) - Color.red(uncheckedColor)) * checkProgress);
            int gD = (int) ((Color.green(color) - Color.green(uncheckedColor)) * checkProgress);
            int bD = (int) ((Color.blue(color) - Color.blue(uncheckedColor)) * checkProgress);
            int c = Color.rgb(Color.red(uncheckedColor) + rD, Color.green(uncheckedColor) + gD, Color.blue(uncheckedColor) + bD);
            checkboxSquare_backgroundPaint.setColor(c);
        } else {
            bounceProgress = 2.0f - progress / 0.5f;
            checkProgress = 1.0f;
            checkboxSquare_backgroundPaint.setColor(color);
        }
        if (isDisabled) {
            checkboxSquare_backgroundPaint.setColor(isAlert ? key_dialogCheckboxSquareDisabled : key_checkboxSquareDisabled);
        }
        float bounce = dp(1) * bounceProgress;
        rectF.set(bounce, bounce, dp(18) - bounce, dp(18) - bounce);

        drawBitmap.eraseColor(0);
        drawCanvas.drawRoundRect(rectF, dp(2), dp(2), checkboxSquare_backgroundPaint);

        if (checkProgress != 1) {
            float rad = Math.min(dp(7), dp(7) * checkProgress + bounce);
            rectF.set(dp(2) + rad, dp(2) + rad, dp(16) - rad, dp(16) - rad);
            drawCanvas.drawRect(rectF, checkboxSquare_eraserPaint);
        }

        if (progress > 0.5f) {
            checkboxSquare_checkPaint.setColor(isAlert ? key_dialogCheckboxSquareCheck : key_checkboxSquareCheck);

            int endX = (int) (dp(7) - dp(3) * (1.0f - bounceProgress));
            int endY = (int) (dpf2(13) - dp(3) * (1.0f - bounceProgress));
            drawCanvas.drawLine(dp(7), (int) dpf2(13), endX, endY, checkboxSquare_checkPaint);

            endX = (int) (dpf2(7) + dp(7) * (1.0f - bounceProgress));
            endY = (int) (dpf2(13) - dp(7) * (1.0f - bounceProgress));
            drawCanvas.drawLine((int) dpf2(7), (int) dpf2(13), endX, endY, checkboxSquare_checkPaint);
        }
        canvas.drawBitmap(drawBitmap, 0, 0, null);
    }

    public float dpf2(float value) {
        if (value == 0) {
            return 0;
        }
        float density = getResources().getDisplayMetrics().density;
        return density * value;
    }

    private int dp(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}

