package com.same.lib.same.theme.cell;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;
import com.same.lib.core.BasePage;
import com.same.lib.drawable.DrawableManager;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.listview.LinearLayoutManager;
import com.same.lib.listview.RecyclerView;
import com.same.lib.radiobutton.RadioButton;
import com.same.lib.same.R;
import com.same.lib.same.theme.CommonTheme;
import com.same.lib.same.theme.DialogTheme;
import com.same.lib.same.theme.span.ThemeName;
import com.same.lib.same.view.RecyclerListView;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeAccent;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.theme.ThemeManager;
import com.same.lib.util.Space;
import com.same.lib.util.Store;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Keep;


/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/29
 * @description null
 * @usage null
 */
public class ThemesHorizontalListCell extends RecyclerListView {
    public final static int THEME_TYPE_BASIC = 0;
    public final static int THEME_TYPE_NIGHT = 1;
    public final static int THEME_TYPE_OTHER = 2;

    private static byte[] bytes = new byte[1024];

    private boolean drawDivider;
    private LinearLayoutManager horizontalLayoutManager;
    private HashMap<String, ThemeInfo> loadingThemes = new HashMap<>();
    private ThemeInfo prevThemeInfo;
    private ThemesListAdapter adapter;

    private ArrayList<ThemeInfo> darkThemes;
    private ArrayList<ThemeInfo> defaultThemes;
    private int currentType;
    private int prevCount;

    private class ThemesListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        ThemesListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new InnerThemeView(mContext));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            InnerThemeView view = (InnerThemeView) holder.itemView;
            ArrayList<ThemeInfo> arrayList;
            int p = position;
            if (position < defaultThemes.size()) {
                arrayList = defaultThemes;
            } else {
                arrayList = darkThemes;
                p -= defaultThemes.size();
            }
            view.setTheme(arrayList.get(p), position == getItemCount() - 1, position == 0);
        }

        @Override
        public int getItemCount() {
            return prevCount = defaultThemes.size() + darkThemes.size();
        }
    }

    private class InnerThemeView extends FrameLayout {

        private RadioButton button;
        private ThemeInfo themeInfo;
        private RectF rect = new RectF();
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Drawable optionsDrawable;

        private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        private Drawable inDrawable;
        private Drawable outDrawable;
        private boolean isLast;
        private boolean isFirst;

        private float placeholderAlpha;

        private int inColor;
        private int outColor;
        private int backColor;
        private int checkColor;
        private int accentId;

        private int oldInColor;
        private int oldOutColor;
        private int oldBackColor;
        private int oldCheckColor;

        private boolean accentColorChanged;

        private ObjectAnimator accentAnimator;
        private float accentState;
        private final ArgbEvaluator evaluator = new ArgbEvaluator();

        private Drawable backgroundDrawable;
        private Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        private BitmapShader bitmapShader;
        private boolean hasWhiteBackground;
        private Matrix shaderMatrix = new Matrix();

        private Drawable loadingDrawable;
        private int loadingColor;

        private long lastDrawTime;

        private boolean pressed;

        public InnerThemeView(Context context) {
            super(context);
            setWillNotDraw(false);

            inDrawable = context.getResources().getDrawable(R.drawable.minibubble_in).mutate();
            outDrawable = context.getResources().getDrawable(R.drawable.minibubble_out).mutate();

            textPaint.setTextSize(Space.dp(13));

            button = new RadioButton(context);
            button.setSize(Space.dp(20));
            addView(button, LayoutHelper.createFrame(22, 22, Gravity.LEFT | Gravity.TOP, 27, 75, 0, 0));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(Space.dp(76 + (isLast ? 22 : 15) + (isFirst ? 22 : 0)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Space.dp(148), MeasureSpec.EXACTLY));
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (optionsDrawable == null || themeInfo == null || themeInfo.info != null && !themeInfo.themeLoaded || currentType != THEME_TYPE_BASIC) {
                return super.onTouchEvent(event);
            }
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
                float x = event.getX();
                float y = event.getY();
                if (x > rect.centerX() && y < rect.centerY() - Space.dp(10)) {
                    if (action == MotionEvent.ACTION_DOWN) {
                        pressed = true;
                    } else {
                        performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                        showOptionsForTheme(themeInfo);
                    }
                }
                if (action == MotionEvent.ACTION_UP) {
                    pressed = false;
                }
            }
            return pressed;
        }

        private boolean parseTheme() {
            if (themeInfo == null || themeInfo.pathToFile == null) {
                return false;
            }
            boolean finished = false;
            File file = new File(themeInfo.pathToFile);
            try (FileInputStream stream = new FileInputStream(file)) {
                int currentPosition = 0;
                int idx;
                int read;
                int linesRead = 0;
                while ((read = stream.read(bytes)) != -1) {
                    int previousPosition = currentPosition;
                    int start = 0;
                    for (int a = 0; a < read; a++) {
                        if (bytes[a] == '\n') {
                            linesRead++;
                            int len = a - start + 1;
                            String line = new String(bytes, start, len - 1, "UTF-8");
                            start += len;
                            currentPosition += len;
                        }
                    }
                    if (finished || previousPosition == currentPosition) {
                        break;
                    }
                    stream.getChannel().position(currentPosition);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            themeInfo.previewParsed = true;
            return true;
        }

        private void applyTheme() {
            inDrawable.setColorFilter(new PorterDuffColorFilter(themeInfo.getPreviewInColor(), PorterDuff.Mode.MULTIPLY));
            outDrawable.setColorFilter(new PorterDuffColorFilter(themeInfo.getPreviewOutColor(), PorterDuff.Mode.MULTIPLY));
            if (themeInfo.pathToFile == null) {
                updateColors(false);
                optionsDrawable = null;
            } else {
                optionsDrawable = getResources().getDrawable(R.drawable.preview_dots).mutate();
                oldBackColor = backColor = themeInfo.getPreviewBackgroundColor();
            }

            bitmapShader = null;
            backgroundDrawable = null;
            double[] hsv = null;
            if (themeInfo.getPreviewBackgroundColor() != 0) {
                hsv = AndroidUtilities.rgbToHsv(Color.red(themeInfo.getPreviewBackgroundColor()), Color.green(themeInfo.getPreviewBackgroundColor()), Color.blue(themeInfo.getPreviewBackgroundColor()));
            }
            if (hsv != null && hsv[1] <= 0.1f && hsv[2] >= 0.96f) {
                hasWhiteBackground = true;
            } else {
                hasWhiteBackground = false;
            }
            if (themeInfo.getPreviewBackgroundColor() == 0 && themeInfo.previewParsed && backgroundDrawable == null) {
                BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.catstile).mutate();
                bitmapShader = new BitmapShader(drawable.getBitmap(), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                bitmapPaint.setShader(bitmapShader);
                backgroundDrawable = drawable;
            }
            invalidate();
        }

        public void setTheme(ThemeInfo theme, boolean last, boolean first) {
            themeInfo = theme;
            isFirst = first;
            isLast = last;
            accentId = theme.currentAccentId;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) button.getLayoutParams();
            layoutParams.leftMargin = Space.dp(isFirst ? 22 + 27 : 27);
            button.setLayoutParams(layoutParams);
            placeholderAlpha = 0.0f;

            if (themeInfo.pathToFile != null && !themeInfo.previewParsed) {
                themeInfo.setPreviewInColor(Theme.getDefaultColor(KeyHub.key_chat_inBubble));
                themeInfo.setPreviewOutColor(Theme.getDefaultColor(KeyHub.key_chat_outBubble));
                File file = new File(themeInfo.pathToFile);
                boolean fileExists = file.exists();
                boolean parsed = fileExists && parseTheme();
                if ((!parsed || !fileExists) && themeInfo.info != null) {
                    loadingDrawable = getResources().getDrawable(R.drawable.preview_custom).mutate();
                    DrawableManager.setDrawableColor(loadingDrawable, loadingColor = Theme.getColor(KeyHub.key_windowBackgroundWhiteGrayText7));
                }
            }
            applyTheme();
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            ThemeInfo t = currentType == THEME_TYPE_NIGHT ? ThemeManager.getCurrentNightTheme() : ThemeManager.getCurrentTheme();
            button.setChecked(themeInfo == t, false);
        }

        public void updateCurrentThemeCheck() {
            ThemeInfo t = currentType == THEME_TYPE_NIGHT ? ThemeManager.getCurrentNightTheme() : ThemeManager.getCurrentTheme();
            button.setChecked(themeInfo == t, true);
        }

        void updateColors(boolean animate) {
            oldInColor = inColor;
            oldOutColor = outColor;
            oldBackColor = backColor;
            oldCheckColor = checkColor;

            ThemeAccent accent = themeInfo.getAccent(false);

            int accentColor;
            int myAccentColor;
            int backAccent;
            if (accent != null) {
                accentColor = accent.accentColor;
                myAccentColor = accentColor;
                backAccent = accentColor;
            } else {
                accentColor = 0;
                myAccentColor = 0;
                backAccent = 0;
            }
            inColor = ThemeManager.changeColorAccent(themeInfo, accentColor, themeInfo.getPreviewInColor());
            outColor = ThemeManager.changeColorAccent(themeInfo, myAccentColor, themeInfo.getPreviewOutColor());
            backColor = ThemeManager.changeColorAccent(themeInfo, backAccent, themeInfo.getPreviewBackgroundColor());
            checkColor = outColor;
            accentId = themeInfo.currentAccentId;

            if (accentAnimator != null) {
                accentAnimator.cancel();
            }

            if (animate) {
                accentAnimator = ObjectAnimator.ofFloat(this, "accentState", 0f, 1f);
                accentAnimator.setDuration(200);
                accentAnimator.start();
            } else {
                setAccentState(1f);
            }
        }

        @Keep
        public float getAccentState() {
            return accentState;
        }

        @Keep
        public void setAccentState(float state) {
            accentState = state;
            accentColorChanged = true;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (accentId != themeInfo.currentAccentId) {
                updateColors(true);
            }

            int x = isFirst ? Space.dp(22) : 0;
            int y = Space.dp(11);
            rect.set(x, y, x + Space.dp(76), y + Space.dp(97));

            String name = ThemeName.getName(getContext(), themeInfo);
            if (name.toLowerCase().endsWith(".attheme")) {
                name = name.substring(0, name.lastIndexOf('.'));
            }
            int maxWidth = getMeasuredWidth() - Space.dp(isFirst ? 10 : 15) - (isLast ? Space.dp(7) : 0);
            String text = TextUtils.ellipsize(name, textPaint, maxWidth, TextUtils.TruncateAt.END).toString();
            int width = (int) Math.ceil(textPaint.measureText(text));
            textPaint.setColor(Theme.getColor(KeyHub.key_windowBackgroundWhiteBlackText));
            canvas.drawText(text, x + (Space.dp(76) - width) / 2, Space.dp(131), textPaint);

            boolean drawContent = themeInfo.info == null && themeInfo.themeLoaded;

            if (drawContent) {
                paint.setColor(blend(oldBackColor, backColor));

                if (accentColorChanged) {
                    inDrawable.setColorFilter(new PorterDuffColorFilter(blend(oldInColor, inColor), PorterDuff.Mode.MULTIPLY));
                    outDrawable.setColorFilter(new PorterDuffColorFilter(blend(oldOutColor, outColor), PorterDuff.Mode.MULTIPLY));
                    accentColorChanged = false;
                }

                if (backgroundDrawable != null) {
                    if (bitmapShader != null) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) backgroundDrawable;
                        float bitmapW = bitmapDrawable.getBitmap().getWidth();
                        float bitmapH = bitmapDrawable.getBitmap().getHeight();
                        float scaleW = bitmapW / rect.width();
                        float scaleH = bitmapH / rect.height();

                        shaderMatrix.reset();
                        float scale = 1.0f / Math.min(scaleW, scaleH);
                        if (bitmapW / scaleH > rect.width()) {
                            bitmapW /= scaleH;
                            shaderMatrix.setTranslate(x - (bitmapW - rect.width()) / 2, y);
                        } else {
                            bitmapH /= scaleW;
                            shaderMatrix.setTranslate(x, y - (bitmapH - rect.height()) / 2);
                        }
                        shaderMatrix.preScale(scale, scale);
                        bitmapShader.setLocalMatrix(shaderMatrix);
                        canvas.drawRoundRect(rect, Space.dp(6), Space.dp(6), bitmapPaint);
                    } else {
                        backgroundDrawable.setBounds((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
                        backgroundDrawable.draw(canvas);
                    }
                } else {
                    canvas.drawRoundRect(rect, Space.dp(6), Space.dp(6), paint);
                }

                button.setColor(0x66ffffff, 0xffffffff);

                if (themeInfo.accentBaseColor != 0) {
                    if ("Day".equals(themeInfo.name) || "Arctic Blue".equals(themeInfo.name)) {
                        button.setColor(0xffb3b3b3, blend(oldCheckColor, checkColor));
                        DialogTheme.chat_instantViewRectPaint.setColor(0x2bb0b5ba);
                        canvas.drawRoundRect(rect, Space.dp(6), Space.dp(6), DialogTheme.chat_instantViewRectPaint);
                    }
                } else if (hasWhiteBackground) {
                    button.setColor(0xffb3b3b3, themeInfo.getPreviewOutColor());
                    DialogTheme.chat_instantViewRectPaint.setColor(0x2bb0b5ba);
                    canvas.drawRoundRect(rect, Space.dp(6), Space.dp(6), DialogTheme.chat_instantViewRectPaint);
                }

                inDrawable.setBounds(x + Space.dp(6), Space.dp(22), x + Space.dp(6 + 43), Space.dp(22 + 14));
                inDrawable.draw(canvas);

                outDrawable.setBounds(x + Space.dp(27), Space.dp(41), x + Space.dp(27 + 43), Space.dp(41 + 14));
                outDrawable.draw(canvas);

                if (optionsDrawable != null && currentType == THEME_TYPE_BASIC) {
                    x = (int) rect.right - Space.dp(16);
                    y = (int) rect.top + Space.dp(6);
                    optionsDrawable.setBounds(x, y, x + optionsDrawable.getIntrinsicWidth(), y + optionsDrawable.getIntrinsicHeight());
                    optionsDrawable.draw(canvas);
                }
            }

            if (themeInfo.info != null) {
                button.setAlpha(0.0f);
                DialogTheme.chat_instantViewRectPaint.setColor(0x2bb0b5ba);
                canvas.drawRoundRect(rect, Space.dp(6), Space.dp(6), DialogTheme.chat_instantViewRectPaint);
                if (loadingDrawable != null) {
                    int newColor = Theme.getColor(KeyHub.key_windowBackgroundWhiteGrayText7);
                    if (loadingColor != newColor) {
                        DrawableManager.setDrawableColor(loadingDrawable, loadingColor = newColor);
                    }
                    x = (int) (rect.centerX() - loadingDrawable.getIntrinsicWidth() / 2);
                    y = (int) (rect.centerY() - loadingDrawable.getIntrinsicHeight() / 2);
                    loadingDrawable.setBounds(x, y, x + loadingDrawable.getIntrinsicWidth(), y + loadingDrawable.getIntrinsicHeight());
                    loadingDrawable.draw(canvas);
                }
            } else if (themeInfo.info != null && !themeInfo.themeLoaded || placeholderAlpha > 0.0f) {
                button.setAlpha(1.0f - placeholderAlpha);
                paint.setColor(Theme.getColor(KeyHub.key_windowBackgroundGray));
                paint.setAlpha((int) (placeholderAlpha * 255));
                canvas.drawRoundRect(rect, Space.dp(6), Space.dp(6), paint);
                if (loadingDrawable != null) {
                    int newColor = Theme.getColor(KeyHub.key_windowBackgroundWhiteGrayText7);
                    if (loadingColor != newColor) {
                        DrawableManager.setDrawableColor(loadingDrawable, loadingColor = newColor);
                    }
                    x = (int) (rect.centerX() - loadingDrawable.getIntrinsicWidth() / 2);
                    y = (int) (rect.centerY() - loadingDrawable.getIntrinsicHeight() / 2);
                    loadingDrawable.setAlpha((int) (placeholderAlpha * 255));
                    loadingDrawable.setBounds(x, y, x + loadingDrawable.getIntrinsicWidth(), y + loadingDrawable.getIntrinsicHeight());
                    loadingDrawable.draw(canvas);
                }
                if (themeInfo.themeLoaded) {
                    long newTime = SystemClock.elapsedRealtime();
                    long dt = Math.min(17, newTime - lastDrawTime);
                    lastDrawTime = newTime;
                    placeholderAlpha -= dt / 180.0f;
                    if (placeholderAlpha < 0.0f) {
                        placeholderAlpha = 0.0f;
                    }
                    invalidate();
                }
            } else if (button.getAlpha() != 1.0f) {
                button.setAlpha(1.0f);
            }
        }

        private int blend(int color1, int color2) {
            if (accentState == 1.0f) {
                return color2;
            } else {
                return (int) evaluator.evaluate(accentState, color1, color2);
            }
        }
    }

    public ThemesHorizontalListCell(Context context, int type, ArrayList<ThemeInfo> def, ArrayList<ThemeInfo> dark) {
        super(context);

        darkThemes = dark;
        defaultThemes = def;
        currentType = type;

        if (type == THEME_TYPE_OTHER) {
            setBackgroundColor(Theme.getColor(KeyHub.key_dialogBackground));
        } else {
            setBackgroundColor(Theme.getColor(KeyHub.key_windowBackgroundWhite));
        }
        setItemAnimator(null);
        setLayoutAnimation(null);
        horizontalLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        setPadding(0, 0, 0, 0);
        setClipToPadding(false);
        horizontalLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(horizontalLayoutManager);
        setAdapter(adapter = new ThemesListAdapter(context));
        setOnItemClickListener((view1, position) -> {
            selectTheme(((InnerThemeView) view1).themeInfo);
            int left = view1.getLeft();
            int right = view1.getRight();
            if (left < 0) {
                smoothScrollBy(left - Space.dp(8), 0);
            } else if (right > getMeasuredWidth()) {
                smoothScrollBy(right - getMeasuredWidth(), 0);
            }
        });
        setOnItemLongClickListener((view12, position) -> {
            InnerThemeView innerThemeView = (InnerThemeView) view12;
            showOptionsForTheme(innerThemeView.themeInfo);
            return true;
        });
    }

    public void selectTheme(ThemeInfo themeInfo) {
        if (themeInfo.info != null) {
            if (!themeInfo.themeLoaded) {
                return;
            }
        }
        if (currentType != THEME_TYPE_OTHER) {
            SharedPreferences.Editor editor = AndroidUtilities.getThemeConfig().edit();
            editor.putString(currentType == THEME_TYPE_NIGHT || themeInfo.isDark() ? "lastDarkTheme" : "lastDayTheme", themeInfo.getKey());
            editor.commit();
        }
        if (currentType == THEME_TYPE_NIGHT) {
            if (themeInfo == ThemeManager.getCurrentNightTheme()) {
                return;
            }
            ThemeManager.setCurrentNightTheme(themeInfo);
        } else {
            if (themeInfo == ThemeManager.getCurrentTheme()) {
                return;
            }
            NotificationCenter.post(NotificationCenter.needSetDayNightTheme, themeInfo);
        }
        updateRows();

        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof InnerThemeView) {
                ((InnerThemeView) child).updateCurrentThemeCheck();
            }
        }
    }

    public void setDrawDivider(boolean draw) {
        drawDivider = draw;
    }

    public void notifyDataSetChanged(int width) {
        if (prevCount == adapter.getItemCount()) {
            return;
        }
        adapter.notifyDataSetChanged();
        ThemeInfo t = currentType == THEME_TYPE_NIGHT ? ThemeManager.getCurrentNightTheme() : ThemeManager.getCurrentTheme();
        if (prevThemeInfo != t) {
            scrollToCurrentTheme(width, false);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (getParent() != null && getParent().getParent() != null) {
            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawDivider) {
            canvas.drawLine(Store.isRTL ? 0 : Space.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (Store.isRTL ? Space.dp(20) : 0), getMeasuredHeight() - 1, CommonTheme.dividerPaint);
        }
    }

    public static Bitmap getScaledBitmap(float w, float h, String path, String streamPath, int streamOffset) {
        FileInputStream stream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            if (path != null) {
                BitmapFactory.decodeFile(path, options);
            } else {
                stream = new FileInputStream(streamPath);
                stream.getChannel().position(streamOffset);
                BitmapFactory.decodeStream(stream, null, options);
            }
            if (options.outWidth > 0 && options.outHeight > 0) {
                if (w > h && options.outWidth < options.outHeight) {
                    float temp = w;
                    w = h;
                    h = temp;
                }
                float scale = Math.min(options.outWidth / w, options.outHeight / h);
                options.inSampleSize = 1;
                if (scale > 1.0f) {
                    do {
                        options.inSampleSize *= 2;
                    }
                    while (options.inSampleSize < scale);
                }
                options.inJustDecodeBounds = false;
                Bitmap wallpaper;
                if (path != null) {
                    wallpaper = BitmapFactory.decodeFile(path, options);
                } else {
                    stream.getChannel().position(streamOffset);
                    wallpaper = BitmapFactory.decodeStream(stream, null, options);
                }
                return wallpaper;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        invalidateViews();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void checkVisibleTheme(ThemeInfo info) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof InnerThemeView) {
                InnerThemeView view = (InnerThemeView) child;
                if (view.themeInfo == info) {
                    if (view.parseTheme()) {
                        view.themeInfo.themeLoaded = true;
                        view.applyTheme();
                    }
                }
            }
        }
    }

    public void scrollToCurrentTheme(int width, boolean animated) {
        if (width == 0) {
            View parent = (View) getParent();
            if (parent != null) {
                width = parent.getMeasuredWidth();
            }
        }
        if (width == 0) {
            return;
        }
        prevThemeInfo = currentType == THEME_TYPE_NIGHT ? ThemeManager.getCurrentNightTheme() : ThemeManager.getCurrentTheme();
        int index = defaultThemes.indexOf(prevThemeInfo);
        if (index < 0) {
            index = darkThemes.indexOf(prevThemeInfo) + defaultThemes.size();
            if (index < 0) {
                return;
            }
        }
        if (animated) {
            smoothScrollToPosition(index);
        } else {
            horizontalLayoutManager.scrollToPositionWithOffset(index, (width - Space.dp(76)) / 2);
        }
    }

    protected void showOptionsForTheme(ThemeInfo themeInfo) {

    }

    protected void presentFragment(BasePage fragment) {

    }

    protected void updateRows() {

    }
}
