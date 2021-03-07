package com.same.lib.checkbox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;

import java.util.Random;

import androidx.annotation.Keep;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/14
 * @description null
 * @usage null
 */
public class CheckBoxBase {

    private View parentView;
    private Rect bounds = new Rect();
    private RectF rect = new RectF();

    private static Paint paint;
    private static Paint eraser;
    private Paint checkPaint;
    private Paint backgroundPaint;
    private TextPaint textPaint;

    private Path path = new Path();

    private Bitmap drawBitmap;
    private Canvas bitmapCanvas;

    private boolean enabled = true;

    private boolean attachedToWindow;

    private float backgroundAlpha = 1.0f;

    private float progress;
    private ObjectAnimator checkAnimator;

    private boolean isChecked;

    private int checkColor = randomColor();
    private int checkColorDisabled = randomColor();
    private int backgroundColor = randomColor();
    private int background2Color = randomColor();
    private String backgroundColorKey = "key_chat_serviceBackground";
    private String key_dialogBackground = "key_dialogBackground";
    private String key_chat_attachPhotoBackground = "key_chat_attachPhotoBackground";

    private boolean useDefaultCheck;

    private boolean drawUnchecked = true;
    private int drawBackgroundAsArc;

    private float size;

    private String checkedText;

    private ProgressDelegate progressDelegate;

    public interface ProgressDelegate {
        void setProgress(float progress);
    }

    public CheckBoxBase(View parent) {
        this(parent, 21);
    }

    public CheckBoxBase(View parent, int sz) {
        parentView = parent;
        size = sz;
        if (paint == null) {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            eraser = new Paint(Paint.ANTI_ALIAS_FLAG);
            eraser.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        checkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        checkPaint.setStrokeCap(Paint.Cap.ROUND);
        checkPaint.setStyle(Paint.Style.STROKE);
        checkPaint.setStrokeJoin(Paint.Join.ROUND);
        checkPaint.setStrokeWidth(dp(1.9f));

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(dp(1.2f));

        drawBitmap = Bitmap.createBitmap(dp(size), dp(size), Bitmap.Config.ARGB_4444);
        bitmapCanvas = new Canvas(drawBitmap);
    }

    public void onAttachedToWindow() {
        attachedToWindow = true;
    }

    public void onDetachedFromWindow() {
        attachedToWindow = false;
    }

    public void setBounds(int x, int y, int width, int height) {
        bounds.left = x;
        bounds.top = y;
        bounds.right = x + width;
        bounds.bottom = y + height;
    }

    public void setDrawUnchecked(boolean value) {
        drawUnchecked = value;
    }

    @Keep
    public void setProgress(float value) {
        if (progress == value) {
            return;
        }
        progress = value;
        invalidate();
        if (progressDelegate != null) {
            progressDelegate.setProgress(value);
        }
    }

    private void invalidate() {
        if (parentView.getParent() != null) {
            View parent = (View) parentView.getParent();
            parent.invalidate();
        }
        parentView.invalidate();
    }

    public void setProgressDelegate(ProgressDelegate delegate) {
        progressDelegate = delegate;
    }

    @Keep
    public float getProgress() {
        return progress;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setEnabled(boolean value) {
        enabled = value;
    }

    public void setDrawBackgroundAsArc(int type) {
        drawBackgroundAsArc = type;
        if (type == 4 || type == 5) {
            backgroundPaint.setStrokeWidth(dp(1.9f));
            if (type == 5) {
                checkPaint.setStrokeWidth(dp(1.5f));
            }
        } else if (type == 3) {
            backgroundPaint.setStrokeWidth(dp(1.2f));
        } else if (type != 0) {
            backgroundPaint.setStrokeWidth(dp(1.5f));
        }
    }

    private void cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator.cancel();
            checkAnimator = null;
        }
    }

    private int dp(float dp) {
        float scale = parentView.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void animateToCheckedState(boolean newCheckedState) {
        checkAnimator = ObjectAnimator.ofFloat(this, "progress", newCheckedState ? 1 : 0);
        checkAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(checkAnimator)) {
                    checkAnimator = null;
                }
                if (!isChecked) {
                    checkedText = null;
                }
            }
        });
        checkAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        checkAnimator.setDuration(200);
        checkAnimator.start();
    }

    public void setColor(int backgroundColor, int checkColor, int edgeColor) {
        this.backgroundColor = backgroundColor;
        this.background2Color = edgeColor;
        this.checkColor = checkColor;
    }

    public void setUseDefaultCheck(boolean value) {
        useDefaultCheck = value;
    }

    public void setBackgroundAlpha(float alpha) {
        backgroundAlpha = alpha;
    }

    public void setNum(int num) {
        if (num >= 0) {
            checkedText = "" + (num + 1);
        } else if (checkAnimator == null) {
            checkedText = null;
        }
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        setChecked(-1, checked, animated);
    }

    public void setChecked(int num, boolean checked, boolean animated) {
        if (num >= 0) {
            checkedText = "" + (num + 1);
            invalidate();
        }
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

    public void draw(Canvas canvas) {
        if (drawBitmap == null) {
            return;
        }

        drawBitmap.eraseColor(0);
        float rad = dp(size / 2);
        float outerRad = rad;
        if (drawBackgroundAsArc != 0 && drawBackgroundAsArc != 11) {
            outerRad -= dp(0.2f);
        }

        float roundProgress = progress >= 0.5f ? 1.0f : progress / 0.5f;

        int cx = bounds.centerX();
        int cy = bounds.centerY();

        if (backgroundColorKey != null) {
            if (drawUnchecked) {
                if (drawBackgroundAsArc == 6 || drawBackgroundAsArc == 7) {
                    paint.setColor(background2Color);
                    backgroundPaint.setColor(checkColor);
                } else if (drawBackgroundAsArc == 10) {
                    backgroundPaint.setColor(background2Color);
                } else {
                    paint.setColor((getServiceMessageColor() & 0x00ffffff) | 0x28000000);
                    backgroundPaint.setColor(checkColor);
                }
            } else {
                backgroundPaint.setColor(getOffsetColor(0x00ffffff, background2Color == 0 ? checkColor : background2Color, progress, backgroundAlpha));
            }
        } else {
            if (drawUnchecked) {
                paint.setColor(Color.argb((int) (25 * backgroundAlpha), 0, 0, 0));
                if (drawBackgroundAsArc == 8) {
                    backgroundPaint.setColor(background2Color);
                } else {
                    backgroundPaint.setColor(getOffsetColor(0xffffffff, checkColor, progress, backgroundAlpha));
                }
            } else {
                backgroundPaint.setColor(getOffsetColor(0x00ffffff, background2Color == 0 ? checkColor : background2Color, progress, backgroundAlpha));
            }
        }

        if (drawUnchecked) {
            if (drawBackgroundAsArc == 8 || drawBackgroundAsArc == 10) {
                canvas.drawCircle(cx, cy, rad - dp(1.5f), backgroundPaint);
            } else if (drawBackgroundAsArc == 6 || drawBackgroundAsArc == 7) {
                canvas.drawCircle(cx, cy, rad - dp(1), paint);
                canvas.drawCircle(cx, cy, rad - dp(1.5f), backgroundPaint);
            } else {
                canvas.drawCircle(cx, cy, rad, paint);
            }
        }
        paint.setColor(checkColor);
        if (drawBackgroundAsArc != 7 && drawBackgroundAsArc != 8 && drawBackgroundAsArc != 9 && drawBackgroundAsArc != 10) {
            if (drawBackgroundAsArc == 0 || drawBackgroundAsArc == 11) {
                canvas.drawCircle(cx, cy, rad, backgroundPaint);
            } else {
                rect.set(cx - outerRad, cy - outerRad, cx + outerRad, cy + outerRad);
                int startAngle;
                int sweepAngle;
                if (drawBackgroundAsArc == 6) {
                    startAngle = 0;
                    sweepAngle = (int) (-360 * progress);
                } else if (drawBackgroundAsArc == 1) {
                    startAngle = -90;
                    sweepAngle = (int) (-270 * progress);
                } else {
                    startAngle = 90;
                    sweepAngle = (int) (270 * progress);
                }

                if (drawBackgroundAsArc == 6) {
                    int color = getColor(key_dialogBackground);
                    int alpha = Color.alpha(color);
                    backgroundPaint.setColor(color);
                    backgroundPaint.setAlpha((int) (alpha * progress));
                    canvas.drawArc(rect, startAngle, sweepAngle, false, backgroundPaint);
                    color = getColor(key_chat_attachPhotoBackground);
                    alpha = Color.alpha(color);
                    backgroundPaint.setColor(color);
                    backgroundPaint.setAlpha((int) (alpha * progress));
                    canvas.drawArc(rect, startAngle, sweepAngle, false, backgroundPaint);
                } else {
                    canvas.drawArc(rect, startAngle, sweepAngle, false, backgroundPaint);
                }
            }
        }

        if (roundProgress > 0) {
            float checkProgress = progress < 0.5f ? 0.0f : (progress - 0.5f) / 0.5f;

            if (drawBackgroundAsArc == 9) {
                paint.setColor(background2Color);
            } else if (drawBackgroundAsArc == 11 || drawBackgroundAsArc == 6 || drawBackgroundAsArc == 7 || drawBackgroundAsArc == 10 || !drawUnchecked && backgroundColorKey != null) {
                paint.setColor(backgroundColor);
            } else {
                paint.setColor(enabled ? checkColor : checkColorDisabled);
            }
            checkPaint.setColor(checkColor);

            rad -= dp(0.5f);
            bitmapCanvas.drawCircle(drawBitmap.getWidth() / 2, drawBitmap.getHeight() / 2, rad, paint);
            bitmapCanvas.drawCircle(drawBitmap.getWidth() / 2, drawBitmap.getWidth() / 2, rad * (1.0f - roundProgress), eraser);
            canvas.drawBitmap(drawBitmap, cx - drawBitmap.getWidth() / 2, cy - drawBitmap.getHeight() / 2, null);

            if (checkProgress != 0) {
                if (checkedText != null) {
                    if (textPaint == null) {
                        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                        textPaint.setTypeface(CheckboxFont.getMediumTypeface(parentView.getContext()));
                    }
                    final float textSize, y;
                    switch (checkedText.length()) {
                        case 0:
                        case 1:
                        case 2:
                            textSize = 14f;
                            y = 18f;
                            break;
                        case 3:
                            textSize = 10f;
                            y = 16.5f;
                            break;
                        default:
                            textSize = 8f;
                            y = 15.75f;
                    }
                    textPaint.setTextSize(dp(textSize));
                    textPaint.setColor(checkColor);
                    canvas.save();
                    canvas.scale(checkProgress, 1.0f, cx, cy);
                    canvas.drawText(checkedText, cx - textPaint.measureText(checkedText) / 2f, dp(y), textPaint);
                    canvas.restore();
                } else {
                    path.reset();

                    float scale = drawBackgroundAsArc == 5 ? 0.8f : 1.0f;
                    float checkSide = dp(9 * scale) * checkProgress;
                    float smallCheckSide = dp(4 * scale) * checkProgress;
                    int x = cx - dp(1.5f);
                    int y = cy + dp(4);
                    float side = (float) Math.sqrt(smallCheckSide * smallCheckSide / 2.0f);
                    path.moveTo(x - side, y - side);
                    path.lineTo(x, y);
                    side = (float) Math.sqrt(checkSide * checkSide / 2.0f);
                    path.lineTo(x + side, y - side);
                    canvas.drawPath(path, checkPaint);
                }
            }
        }
    }

    private int getServiceMessageColor() {
        return Color.RED;
    }

    private int getColor(String key) {
        return randomColor();
    }

    public static int randomColor() {
        int random = new Random().nextInt(360);
        return Color.HSVToColor(new float[]{random, 0.5f, 0.9f});
    }

    public static int getOffsetColor(int color1, int color2, float offset, float alpha) {
        int rF = Color.red(color2);
        int gF = Color.green(color2);
        int bF = Color.blue(color2);
        int aF = Color.alpha(color2);
        int rS = Color.red(color1);
        int gS = Color.green(color1);
        int bS = Color.blue(color1);
        int aS = Color.alpha(color1);
        return Color.argb((int) ((aS + (aF - aS) * offset) * alpha), (int) (rS + (rF - rS) * offset), (int) (gS + (gF - gS) * offset), (int) (bS + (bF - bS) * offset));
    }
}
