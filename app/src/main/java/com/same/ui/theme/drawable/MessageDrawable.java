package com.same.ui.theme.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;

import com.same.lib.base.SharedConfig;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.lib.util.Space;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/26
 * @description null
 * @usage null
 */
public class MessageDrawable extends Drawable {

    private LinearGradient gradientShader;
    private int currentBackgroundHeight;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint selectedPaint;
    private int currentColor;
    private int currentGradientColor;

    private RectF rect = new RectF();
    private Matrix matrix = new Matrix();
    private int currentType;
    private boolean isSelected;
    private Path path;

    private Rect backupRect = new Rect();

    private final boolean isOut;

    private int topY;
    private boolean isTopNear;
    private boolean isBottomNear;

    private int[] currentShadowDrawableRadius = new int[]{-1, -1, -1, -1};
    private Drawable[] shadowDrawable = new Drawable[4];
    private int[] shadowDrawableColor = new int[]{0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff};

    private int[][] currentBackgroundDrawableRadius = new int[][]{
            {-1, -1, -1, -1},
            {-1, -1, -1, -1}};
    private Drawable[][] backgroundDrawable = new Drawable[2][4];
    private int[][] backgroundDrawableColor = new int[][]{
            {0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff},
            {0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff}};

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_MEDIA = 1;
    public static final int TYPE_PREVIEW = 2;

    private int alpha;

    public MessageDrawable(int type, boolean out, boolean selected) {
        super();
        isOut = out;
        currentType = type;
        isSelected = selected;
        path = new Path();
        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        alpha = 255;
    }

    public boolean hasGradient() {
        return gradientShader != null && Theme.shouldDrawGradientIcons;
    }

    public LinearGradient getGradientShader() {
        return gradientShader;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    protected int getColor(String key) {
        return Theme.getColor(key);
    }

    protected Integer getCurrentColor(String key) {
        return Theme.currentColors.get(key);
    }

    public void setTop(int top, int backgroundHeight, boolean topNear, boolean bottomNear) {
        int color;
        Integer gradientColor;
        if (isOut) {
            color = getColor(isSelected ? KeyHub.key_chat_outBubbleSelected : KeyHub.key_chat_outBubble);
            gradientColor = getCurrentColor(KeyHub.key_chat_outBubbleGradient);
        } else {
            color = getColor(isSelected ? KeyHub.key_chat_inBubbleSelected : KeyHub.key_chat_inBubble);
            gradientColor = null;
        }
        if (gradientColor != null) {
            color = getColor(KeyHub.key_chat_outBubble);
        }
        if (gradientColor == null) {
            gradientColor = 0;
        }
        if (gradientColor != 0 && (gradientShader == null || backgroundHeight != currentBackgroundHeight || currentColor != color || currentGradientColor != gradientColor)) {
            gradientShader = new LinearGradient(0, 0, 0, backgroundHeight, new int[]{gradientColor, color}, null, Shader.TileMode.CLAMP);
            paint.setShader(gradientShader);
            currentColor = color;
            currentGradientColor = gradientColor;
            paint.setColor(0xffffffff);
        } else if (gradientColor == 0) {
            if (gradientShader != null) {
                gradientShader = null;
                paint.setShader(null);
            }
            paint.setColor(color);
        }
        currentBackgroundHeight = backgroundHeight;

        topY = top;
        isTopNear = topNear;
        isBottomNear = bottomNear;
    }

    private int dp(float value) {
        if (currentType == TYPE_PREVIEW) {
            return (int) Math.ceil(3 * value);
        } else {
            return Space.dp(value);
        }
    }

    public Paint getPaint() {
        return paint;
    }

    public Drawable[] getShadowDrawables() {
        return shadowDrawable;
    }

    public Drawable getBackgroundDrawable() {
        int newRad = Space.dp(SharedConfig.bubbleRadius);
        int idx;
        if (isTopNear && isBottomNear) {
            idx = 3;
        } else if (isTopNear) {
            idx = 2;
        } else if (isBottomNear) {
            idx = 1;
        } else {
            idx = 0;
        }
        int idx2 = isSelected ? 1 : 0;
        boolean forceSetColor = false;

        boolean drawWithShadow = gradientShader == null && !isSelected;
        int shadowColor = getColor(isOut ? KeyHub.key_chat_outBubbleShadow : KeyHub.key_chat_inBubbleShadow);
        if (currentBackgroundDrawableRadius[idx2][idx] != newRad || (drawWithShadow && shadowDrawableColor[idx] != shadowColor)) {
            currentBackgroundDrawableRadius[idx2][idx] = newRad;
            try {
                Bitmap bitmap = Bitmap.createBitmap(dp(50), dp(40), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                backupRect.set(getBounds());

                if (drawWithShadow) {
                    shadowDrawableColor[idx] = shadowColor;

                    Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

                    LinearGradient gradientShader = new LinearGradient(0, 0, 0, dp(40), new int[]{0x155F6569, 0x295F6569}, null, Shader.TileMode.CLAMP);
                    shadowPaint.setShader(gradientShader);
                    shadowPaint.setColorFilter(new PorterDuffColorFilter(shadowColor, PorterDuff.Mode.MULTIPLY));

                    shadowPaint.setShadowLayer(2, 0, 1, 0xffffffff);
                    if (Space.density > 1) {
                        setBounds(-1, -1, bitmap.getWidth() + 1, bitmap.getHeight() + 1);
                    } else {
                        setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    }
                    draw(canvas, shadowPaint);

                    if (Space.density > 1) {
                        shadowPaint.setColor(0);
                        shadowPaint.setShadowLayer(0, 0, 0, 0);
                        shadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        draw(canvas, shadowPaint);
                    }
                }

                Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                shadowPaint.setColor(0xffffffff);
                setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                draw(canvas, shadowPaint);

                backgroundDrawable[idx2][idx] = new NinePatchDrawable(bitmap, getByteBuffer(bitmap.getWidth() / 2 - 1, bitmap.getWidth() / 2 + 1, bitmap.getHeight() / 2 - 1, bitmap.getHeight() / 2 + 1).array(), new Rect(), null);
                forceSetColor = true;
                setBounds(backupRect);
            } catch (Throwable ignore) {

            }
        }
        int color;
        if (isSelected) {
            color = getColor(isOut ? KeyHub.key_chat_outBubbleSelected : KeyHub.key_chat_inBubbleSelected);
        } else {
            color = getColor(isOut ? KeyHub.key_chat_outBubble : KeyHub.key_chat_inBubble);
        }
        if (backgroundDrawable[idx2][idx] != null && (backgroundDrawableColor[idx2][idx] != color || forceSetColor)) {
            backgroundDrawable[idx2][idx].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            backgroundDrawableColor[idx2][idx] = color;
        }
        return backgroundDrawable[idx2][idx];
    }

    public Drawable getShadowDrawable() {
        if (gradientShader == null && !isSelected) {
            return null;
        }
        int newRad = Space.dp(SharedConfig.bubbleRadius);
        int idx;
        if (isTopNear && isBottomNear) {
            idx = 3;
        } else if (isTopNear) {
            idx = 2;
        } else if (isBottomNear) {
            idx = 1;
        } else {
            idx = 0;
        }
        boolean forceSetColor = false;
        if (currentShadowDrawableRadius[idx] != newRad) {
            currentShadowDrawableRadius[idx] = newRad;
            try {
                Bitmap bitmap = Bitmap.createBitmap(dp(50), dp(40), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

                LinearGradient gradientShader = new LinearGradient(0, 0, 0, dp(40), new int[]{0x155F6569, 0x295F6569}, null, Shader.TileMode.CLAMP);
                shadowPaint.setShader(gradientShader);

                shadowPaint.setShadowLayer(2, 0, 1, 0xffffffff);
                if (Space.density > 1) {
                    setBounds(-1, -1, bitmap.getWidth() + 1, bitmap.getHeight() + 1);
                } else {
                    setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                }
                draw(canvas, shadowPaint);

                if (Space.density > 1) {
                    shadowPaint.setColor(0);
                    shadowPaint.setShadowLayer(0, 0, 0, 0);
                    shadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    draw(canvas, shadowPaint);
                }

                shadowDrawable[idx] = new NinePatchDrawable(bitmap, getByteBuffer(bitmap.getWidth() / 2 - 1, bitmap.getWidth() / 2 + 1, bitmap.getHeight() / 2 - 1, bitmap.getHeight() / 2 + 1).array(), new Rect(), null);
                forceSetColor = true;
            } catch (Throwable ignore) {

            }
        }
        int color = getColor(isOut ? KeyHub.key_chat_outBubbleShadow : KeyHub.key_chat_inBubbleShadow);
        if (shadowDrawable[idx] != null && (shadowDrawableColor[idx] != color || forceSetColor)) {
            shadowDrawable[idx].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            shadowDrawableColor[idx] = color;
        }
        return shadowDrawable[idx];
    }

    private static ByteBuffer getByteBuffer(int x1, int x2, int y1, int y2) {
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 * 7 + 4 * 2 + 4 * 2 + 4 * 9).order(ByteOrder.nativeOrder());
        buffer.put((byte) 0x01);
        buffer.put((byte) 2);
        buffer.put((byte) 2);
        buffer.put((byte) 0x09);

        buffer.putInt(0);
        buffer.putInt(0);

        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);

        buffer.putInt(0);

        buffer.putInt(x1);
        buffer.putInt(x2);

        buffer.putInt(y1);
        buffer.putInt(y2);

        buffer.putInt(0x00000001);
        buffer.putInt(0x00000001);
        buffer.putInt(0x00000001);
        buffer.putInt(0x00000001);
        buffer.putInt(0x00000001);
        buffer.putInt(0x00000001);
        buffer.putInt(0x00000001);
        buffer.putInt(0x00000001);
        buffer.putInt(0x00000001);

        return buffer;
    }

    @Override
    public void draw(Canvas canvas) {
        draw(canvas, null);
    }

    public void draw(Canvas canvas, Paint paintToUse) {
        Rect bounds = getBounds();
        if (paintToUse == null && gradientShader == null) {
            Drawable background = getBackgroundDrawable();
            if (background != null) {
                background.setBounds(bounds);
                background.draw(canvas);
                return;
            }
        }
        int padding = dp(2);
        int rad;
        int nearRad;
        if (currentType == TYPE_PREVIEW) {
            rad = dp(6);
            nearRad = dp(6);
        } else {
            rad = dp(SharedConfig.bubbleRadius);
            nearRad = dp(Math.min(5, SharedConfig.bubbleRadius));
        }
        int smallRad = dp(6);

        Paint p = paintToUse == null ? paint : paintToUse;

        if (paintToUse == null && gradientShader != null) {
            matrix.reset();
            matrix.postTranslate(0, -topY);
            gradientShader.setLocalMatrix(matrix);
        }

        int top = Math.max(bounds.top, 0);
        path.reset();
        if (isOut) {
            if (currentType == TYPE_PREVIEW || paintToUse != null || topY + bounds.bottom - rad < currentBackgroundHeight) {
                if (currentType == TYPE_MEDIA) {
                    path.moveTo(bounds.right - dp(8) - rad, bounds.bottom - padding);
                } else {
                    path.moveTo(bounds.right - dp(2.6f), bounds.bottom - padding);
                }
                path.lineTo(bounds.left + padding + rad, bounds.bottom - padding);
                rect.set(bounds.left + padding, bounds.bottom - padding - rad * 2, bounds.left + padding + rad * 2, bounds.bottom - padding);
                path.arcTo(rect, 90, 90, false);
            } else {
                path.moveTo(bounds.right - dp(8), top - topY + currentBackgroundHeight);
                path.lineTo(bounds.left + padding, top - topY + currentBackgroundHeight);
            }
            if (currentType == TYPE_PREVIEW || paintToUse != null || topY + rad * 2 >= 0) {
                path.lineTo(bounds.left + padding, bounds.top + padding + rad);
                rect.set(bounds.left + padding, bounds.top + padding, bounds.left + padding + rad * 2, bounds.top + padding + rad * 2);
                path.arcTo(rect, 180, 90, false);

                int radToUse = isTopNear ? nearRad : rad;
                if (currentType == TYPE_MEDIA) {
                    path.lineTo(bounds.right - padding - radToUse, bounds.top + padding);
                    rect.set(bounds.right - padding - radToUse * 2, bounds.top + padding, bounds.right - padding, bounds.top + padding + radToUse * 2);
                } else {
                    path.lineTo(bounds.right - dp(8) - radToUse, bounds.top + padding);
                    rect.set(bounds.right - dp(8) - radToUse * 2, bounds.top + padding, bounds.right - dp(8), bounds.top + padding + radToUse * 2);
                }
                path.arcTo(rect, 270, 90, false);
            } else {
                path.lineTo(bounds.left + padding, top - topY - dp(2));
                if (currentType == TYPE_MEDIA) {
                    path.lineTo(bounds.right - padding, top - topY - dp(2));
                } else {
                    path.lineTo(bounds.right - dp(8), top - topY - dp(2));
                }
            }
            if (currentType == TYPE_MEDIA) {
                if (currentType == TYPE_PREVIEW || paintToUse != null || topY + bounds.bottom - rad < currentBackgroundHeight) {
                    int radToUse = isBottomNear ? nearRad : rad;

                    path.lineTo(bounds.right - padding, bounds.bottom - padding - radToUse);
                    rect.set(bounds.right - padding - radToUse * 2, bounds.bottom - padding - radToUse * 2, bounds.right - padding, bounds.bottom - padding);
                    path.arcTo(rect, 0, 90, false);
                } else {
                    path.lineTo(bounds.right - padding, top - topY + currentBackgroundHeight);
                }
            } else {
                if (currentType == TYPE_PREVIEW || paintToUse != null || topY + bounds.bottom - smallRad * 2 < currentBackgroundHeight) {
                    path.lineTo(bounds.right - dp(8), bounds.bottom - padding - smallRad - dp(3));
                    rect.set(bounds.right - dp(8), bounds.bottom - padding - smallRad * 2 - dp(9), bounds.right - dp(7) + smallRad * 2, bounds.bottom - padding - dp(1));
                    path.arcTo(rect, 180, -83, false);
                } else {
                    path.lineTo(bounds.right - dp(8), top - topY + currentBackgroundHeight);
                }
            }
        } else {
            if (currentType == TYPE_PREVIEW || paintToUse != null || topY + bounds.bottom - rad < currentBackgroundHeight) {
                if (currentType == TYPE_MEDIA) {
                    path.moveTo(bounds.left + dp(8) + rad, bounds.bottom - padding);
                } else {
                    path.moveTo(bounds.left + dp(2.6f), bounds.bottom - padding);
                }
                path.lineTo(bounds.right - padding - rad, bounds.bottom - padding);
                rect.set(bounds.right - padding - rad * 2, bounds.bottom - padding - rad * 2, bounds.right - padding, bounds.bottom - padding);
                path.arcTo(rect, 90, -90, false);
            } else {
                path.moveTo(bounds.left + dp(8), top - topY + currentBackgroundHeight);
                path.lineTo(bounds.right - padding, top - topY + currentBackgroundHeight);
            }
            if (currentType == TYPE_PREVIEW || paintToUse != null || topY + rad * 2 >= 0) {
                path.lineTo(bounds.right - padding, bounds.top + padding + rad);
                rect.set(bounds.right - padding - rad * 2, bounds.top + padding, bounds.right - padding, bounds.top + padding + rad * 2);
                path.arcTo(rect, 0, -90, false);

                int radToUse = isTopNear ? nearRad : rad;
                if (currentType == TYPE_MEDIA) {
                    path.lineTo(bounds.left + padding + radToUse, bounds.top + padding);
                    rect.set(bounds.left + padding, bounds.top + padding, bounds.left + padding + radToUse * 2, bounds.top + padding + radToUse * 2);
                } else {
                    path.lineTo(bounds.left + dp(8) + radToUse, bounds.top + padding);
                    rect.set(bounds.left + dp(8), bounds.top + padding, bounds.left + dp(8) + radToUse * 2, bounds.top + padding + radToUse * 2);
                }
                path.arcTo(rect, 270, -90, false);
            } else {
                path.lineTo(bounds.right - padding, top - topY - dp(2));
                if (currentType == TYPE_MEDIA) {
                    path.lineTo(bounds.left + padding, top - topY - dp(2));
                } else {
                    path.lineTo(bounds.left + dp(8), top - topY - dp(2));
                }
            }
            if (currentType == TYPE_MEDIA) {
                if (currentType == TYPE_PREVIEW || paintToUse != null || topY + bounds.bottom - rad < currentBackgroundHeight) {
                    int radToUse = isBottomNear ? nearRad : rad;

                    path.lineTo(bounds.left + padding, bounds.bottom - padding - radToUse);
                    rect.set(bounds.left + padding, bounds.bottom - padding - radToUse * 2, bounds.left + padding + radToUse * 2, bounds.bottom - padding);
                    path.arcTo(rect, 180, -90, false);
                } else {
                    path.lineTo(bounds.left + padding, top - topY + currentBackgroundHeight);
                }
            } else {
                if (currentType == TYPE_PREVIEW || paintToUse != null || topY + bounds.bottom - smallRad * 2 < currentBackgroundHeight) {
                    path.lineTo(bounds.left + dp(8), bounds.bottom - padding - smallRad - dp(3));
                    rect.set(bounds.left + dp(7) - smallRad * 2, bounds.bottom - padding - smallRad * 2 - dp(9), bounds.left + dp(8), bounds.bottom - padding - dp(1));
                    path.arcTo(rect, 0, 83, false);
                } else {
                    path.lineTo(bounds.left + dp(8), top - topY + currentBackgroundHeight);
                }
            }
        }
        path.close();

        canvas.drawPath(path, p);
        if (gradientShader != null && isSelected) {
            selectedPaint.setColor(getColor(KeyHub.key_chat_outBubbleGradientSelectedOverlay));
            canvas.drawPath(path, selectedPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (this.alpha != alpha) {
            this.alpha = alpha;
            paint.setAlpha(alpha);
            if (isOut) {
                selectedPaint.setAlpha((int) (Color.alpha(getColor(KeyHub.key_chat_outBubbleGradientSelectedOverlay)) * (alpha / 255.0f)));
            }
        }
        if (gradientShader == null) {
            Drawable background = getBackgroundDrawable();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (background.getAlpha() != alpha) {
                    background.setAlpha(alpha);
                }
            } else {
                background.setAlpha(alpha);
            }
        }
    }

    @Override
    public void setColorFilter(int color, PorterDuff.Mode mode) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
