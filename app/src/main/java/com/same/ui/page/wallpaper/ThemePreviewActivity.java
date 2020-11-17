//package com.same.ui.page.wallpaper;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.animation.StateListAnimator;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.res.Configuration;
//import android.database.DataSetObserver;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Outline;
//import android.graphics.Paint;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffColorFilter;
//import android.graphics.Rect;
//import android.graphics.Shader;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.GradientDrawable;
//import android.os.Build;
//import android.os.SystemClock;
//import android.text.TextPaint;
//import android.text.TextUtils;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewOutlineProvider;
//import android.view.ViewTreeObserver;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.same.lib.anim.CubicBezierInterpolator;
//import com.same.lib.core.ActionBar;
//import com.same.lib.core.ActionBarMenu;
//import com.same.lib.core.ActionBarMenuItem;
//import com.same.lib.core.BasePage;
//import com.same.lib.drawable.BackDrawable;
//import com.same.lib.drawable.BackgroundGradientDrawable;
//import com.same.lib.drawable.CombinedDrawable;
//import com.same.lib.drawable.MenuDrawable;
//import com.same.lib.helper.LayoutHelper;
//import com.same.lib.listview.DefaultItemAnimator;
//import com.same.lib.listview.LinearLayoutManager;
//import com.same.lib.listview.RecyclerView;
//import com.same.lib.theme.OverrideWallpaperInfo;
//import com.same.lib.theme.Theme;
//import com.same.lib.theme.ThemeAccent;
//import com.same.lib.theme.ThemeDescription;
//import com.same.lib.theme.ThemeInfo;
//import com.same.lib.theme.ThemeManager;
//import com.same.lib.theme.WallPaper;
//import com.same.lib.util.AndroidUtilities;
//import com.same.lib.util.NotificationCenter;
//import com.same.lib.util.SharedConfig;
//import com.same.ui.R;
//import com.same.ui.lang.MyLang;
//import com.same.ui.view.BackupImageView;
//import com.same.ui.view.RecyclerListView;
//import com.same.ui.view.WallpaperCheckBoxView;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import androidx.viewpager.widget.PagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//
///**
// * @author 林学渊
// * @email linxy59@mail2.sysu.edu.cn
// * @date 2020/11/17
// * @description null
// * @usage null
// */
//public class ThemePreviewActivity extends BasePage {
//
//    public static final int SCREEN_TYPE_PREVIEW = 0;
//    public static final int SCREEN_TYPE_ACCENT_COLOR = 1;
//    public static final int SCREEN_TYPE_CHANGE_BACKGROUND = 2;
//
//    private final int screenType;
//    private boolean useDefaultThemeForButtons = true;
//
//    private ActionBarMenuItem dropDownContainer;
//    private ActionBarMenuItem saveItem;
//    private TextView dropDown;
//    private int colorType = 1;
//
//    private Drawable sheetDrawable;
//
//    private long watchForKeyboardEndTime;
//    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
//
//    private int lastPickedColor;
//    private int lastPickedColorNum;
//    private Runnable applyColorAction = () -> {
//        applyColorScheduled = false;
//        applyColor(lastPickedColor, lastPickedColorNum);
//    };
//    private boolean applyColorScheduled;
//
//    private View dotsContainer;
//    private FrameLayout saveButtonsContainer;
//    private TextView doneButton;
//    private TextView cancelButton;
//
//    private ThemeInfo applyingTheme;
//    private boolean nightTheme;
//    private boolean editingTheme;
//    private boolean deleteOnCancel;
//    private List<ThemeDescription> themeDescriptions;
//
//    private ViewPager viewPager;
//
//    private FrameLayout frameLayout;
//
//    private FrameLayout page1;
//    private RecyclerListView listView;
//    private ImageView floatingButton;
//
//    private ActionBar actionBar2;
//    private FrameLayout page2;
//    private RecyclerListView listView2;
//    private BackupImageView backgroundImage;
//    private FrameLayout buttonsContainer;
//    private AnimatorSet motionAnimation;
//    private RadialProgress2 radialProgress;
//    private WallpaperCheckBoxView[] checkBoxView;
//    private int backgroundColor;
//    private int backgroundGradientColor;
//    private int patternColor;
//    private float currentIntensity = 0.5f;
//
//    private final PorterDuff.Mode blendMode = PorterDuff.Mode.SRC_IN;
//
//    private int TAG;
//
//    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
//    private WallpaperParallaxEffect parallaxEffect;
//    private Bitmap blurredBitmap;
//    private Bitmap originalBitmap;
//    private float parallaxScale = 1.0f;
//
//    private Object currentWallpaper;
//    private Bitmap currentWallpaperBitmap;
//
//    private boolean isMotion;
//    private boolean isBlurred;
//
//    private boolean progressVisible;
//
//    private String imageFilter = "640_360";
//    private int maxWallpaperSize = 1920;
//
//    private WallpaperActivityDelegate delegate;
//
//    public interface WallpaperActivityDelegate {
//        void didSetNewBackground();
//    }
//
//    public ThemePreviewActivity(Object wallPaper, Bitmap bitmap) {
//        super();
//        screenType = SCREEN_TYPE_CHANGE_BACKGROUND;
//        currentWallpaper = wallPaper;
//        currentWallpaperBitmap = bitmap;
//    }
//
//    public ThemePreviewActivity(ThemeInfo themeInfo) {
//        this(themeInfo, false, SCREEN_TYPE_PREVIEW, false, false);
//    }
//
//    public ThemePreviewActivity(ThemeInfo themeInfo, boolean deleteFile, int screenType, boolean edit, boolean night) {
//        super();
//        this.screenType = screenType;
//        nightTheme = night;
//        applyingTheme = themeInfo;
//        deleteOnCancel = deleteFile;
//        editingTheme = edit;
//        if (accent != null) {
//            isMotion = accent.patternMotion;
//            if (!TextUtils.isEmpty(accent.patternSlug)) {
//                currentIntensity = accent.patternIntensity;
//            }
//            ThemeManager.applyThemeTemporary(applyingTheme, true);
//        }
//        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme);
//    }
//
//    public void setInitialModes(boolean blur, boolean motion) {
//        isBlurred = blur;
//        isMotion = motion;
//    }
//
//    @Override
//    public View createView(Context context) {
//        hasOwnBackground = true;
//
//        page1 = new FrameLayout(context);
//        ActionBarMenu menu = actionBar.createMenu();
//        final ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
//            @Override
//            public void onSearchExpand() {
//
//            }
//
//            @Override
//            public boolean canCollapseSearch() {
//                return true;
//            }
//
//            @Override
//            public void onSearchCollapse() {
//
//            }
//
//            @Override
//            public void onTextChanged(EditText editText) {
//
//            }
//        });
//        item.setSearchFieldHint(MyLang.getString("Search", R.string.Search));
//
//        actionBar.setBackButtonDrawable(new MenuDrawable());
//        actionBar.setAddToContainer(false);
//        actionBar.setTitle(MyLang.getString("ThemePreview", R.string.ThemePreview));
//
//        page1 = new FrameLayout(context) {
//            @Override
//            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//                setMeasuredDimension(widthSize, heightSize);
//
//                measureChildWithMargins(actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
//                int actionBarHeight = actionBar.getMeasuredHeight();
//                if (actionBar.getVisibility() == VISIBLE) {
//                    heightSize -= actionBarHeight;
//                }
//                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
//                layoutParams.topMargin = actionBarHeight;
//                listView.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
//
//                measureChildWithMargins(floatingButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
//            }
//
//            @Override
//            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//                boolean result = super.drawChild(canvas, child, drawingTime);
//                if (child == actionBar && parentLayout != null) {
//                    parentLayout.drawHeaderShadow(canvas, actionBar.getVisibility() == VISIBLE ? actionBar.getMeasuredHeight() : 0);
//                }
//                return result;
//            }
//        };
//        page1.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
//        page1.addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
//
//        listView = new RecyclerListView(context);
//        listView.setVerticalScrollBarEnabled(true);
//        listView.setItemAnimator(null);
//        listView.setLayoutAnimation(null);
//        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
//        listView.setVerticalScrollbarPosition(MyLang.isRTL ? RecyclerListView.SCROLLBAR_POSITION_LEFT : RecyclerListView.SCROLLBAR_POSITION_RIGHT);
//        listView.setPadding(0, 0, 0, AndroidUtilities.dp(screenType != SCREEN_TYPE_PREVIEW ? 12 : 0));
//        page1.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
//
//        floatingButton = new ImageView(context);
//        floatingButton.setScaleType(ImageView.ScaleType.CENTER);
//
//        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
//        if (Build.VERSION.SDK_INT < 21) {
//            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
//            shadowDrawable.setColorFilter(new PorterDuffColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY));
//            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
//            combinedDrawable.setIconSize(AndroidUtilities.dp(56), AndroidUtilities.dp(56));
//            drawable = combinedDrawable;
//        }
//        floatingButton.setBackgroundDrawable(drawable);
//        floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
//        floatingButton.setImageResource(R.drawable.floating_pencil);
//        if (Build.VERSION.SDK_INT >= 21) {
//            StateListAnimator animator = new StateListAnimator();
//            animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(2), AndroidUtilities.dp(4)).setDuration(200));
//            animator.addState(new int[]{}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(4), AndroidUtilities.dp(2)).setDuration(200));
//            floatingButton.setStateListAnimator(animator);
//            floatingButton.setOutlineProvider(new ViewOutlineProvider() {
//                @SuppressLint("NewApi")
//                @Override
//                public void getOutline(View view, Outline outline) {
//                    outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
//                }
//            });
//        }
//        page1.addView(floatingButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56 : 60, (MyLang.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, MyLang.isRTL ? 14 : 0, 0, MyLang.isRTL ? 0 : 14, 14));
//
//        listView.setAdapter(dialogsAdapter);
//
//        page2 = new FrameLayout(context) {
//
//            private boolean ignoreLayout;
//
//            @Override
//            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//                setMeasuredDimension(widthSize, heightSize);
//
//                if (dropDownContainer != null) {
//                    ignoreLayout = true;
//                    if (!AndroidUtilities.isTablet()) {
//                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) dropDownContainer.getLayoutParams();
//                        layoutParams.topMargin = (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
//                        dropDownContainer.setLayoutParams(layoutParams);
//                    }
//                    if (!AndroidUtilities.isTablet() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                        dropDown.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
//                    } else {
//                        dropDown.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
//                    }
//                    ignoreLayout = false;
//                }
//
//                measureChildWithMargins(actionBar2, widthMeasureSpec, 0, heightMeasureSpec, 0);
//                int actionBarHeight = actionBar2.getMeasuredHeight();
//                if (actionBar2.getVisibility() == VISIBLE) {
//                    heightSize -= actionBarHeight;
//                }
//                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) listView2.getLayoutParams();
//                layoutParams.topMargin = actionBarHeight;
//                listView2.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize - layoutParams.bottomMargin, MeasureSpec.EXACTLY));
//
//                layoutParams = (FrameLayout.LayoutParams) backgroundImage.getLayoutParams();
//                layoutParams.topMargin = actionBarHeight;
//                backgroundImage.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
//
//                if (bottomOverlayChat != null) {
//                    measureChildWithMargins(bottomOverlayChat, widthMeasureSpec, 0, heightMeasureSpec, 0);
//                }
//                for (int a = 0; a < patternLayout.length; a++) {
//                    if (patternLayout[a] != null) {
//                        measureChildWithMargins(patternLayout[a], widthMeasureSpec, 0, heightMeasureSpec, 0);
//                    }
//                }
//            }
//
//            @Override
//            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//                boolean result = super.drawChild(canvas, child, drawingTime);
//                if (child == actionBar2 && parentLayout != null) {
//                    parentLayout.drawHeaderShadow(canvas, actionBar2.getVisibility() == VISIBLE ? (int) (actionBar2.getMeasuredHeight() + actionBar2.getTranslationY()) : 0);
//                }
//                return result;
//            }
//
//            @Override
//            public void requestLayout() {
//                if (ignoreLayout) {
//                    return;
//                }
//                super.requestLayout();
//            }
//        };
//        actionBar2 = createActionBar(context);
//        actionBar2.setBackButtonDrawable(new BackDrawable(false));
//        actionBar2.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
//            @Override
//            public void onItemClick(int id) {
//                if (id == -1) {
//                    if (checkDiscard()) {
//                        cancelThemeApply(false);
//                    }
//                } else if (id == 4) {
//                    if (removeBackgroundOverride) {
//                        ThemeManager.resetCustomWallpaper(false);
//                    }
//                    saveAccentWallpaper();
//                    NotificationCenter.getGlobalInstance().removeObserver(ThemePreviewActivity.this, NotificationCenter.wallpapersDidLoad);
//                    ThemeManager.saveThemeAccents(applyingTheme, true, false, false, true);
//                    ThemeManager.applyPreviousTheme();
//                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, applyingTheme, nightTheme, null, -1);
//                    finishFragment();
//                }
//            }
//        });
//
//        backgroundImage = new BackupImageView(context) {
//
//            private Drawable background;
//
//            @Override
//            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//                parallaxScale = parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
//                if (isMotion) {
//                    setScaleX(parallaxScale);
//                    setScaleY(parallaxScale);
//                }
//                if (radialProgress != null) {
//                    int size = AndroidUtilities.dp(44);
//                    int x = (getMeasuredWidth() - size) / 2;
//                    int y = (getMeasuredHeight() - size) / 2;
//                    radialProgress.setProgressRect(x, y, x + size, y + size);
//                }
//
//                progressVisible = screenType == SCREEN_TYPE_CHANGE_BACKGROUND && getMeasuredWidth() <= getMeasuredHeight();
//            }
//
//            @Override
//            protected void onDraw(Canvas canvas) {
//                if (background instanceof ColorDrawable || background instanceof GradientDrawable) {
//                    background.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
//                    background.draw(canvas);
//                } else if (background instanceof BitmapDrawable) {
//                    BitmapDrawable bitmapDrawable = (BitmapDrawable) background;
//                    if (bitmapDrawable.getTileModeX() == Shader.TileMode.REPEAT) {
//                        canvas.save();
//                        float scale = 2.0f / AndroidUtilities.density;
//                        canvas.scale(scale, scale);
//                        background.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / scale), (int) Math.ceil(getMeasuredHeight() / scale));
//                        background.draw(canvas);
//                        canvas.restore();
//                    } else {
//                        int viewHeight = getMeasuredHeight();
//                        float scaleX = (float) getMeasuredWidth() / (float) background.getIntrinsicWidth();
//                        float scaleY = (float) (viewHeight) / (float) background.getIntrinsicHeight();
//                        float scale = Math.max(scaleX, scaleY);
//                        int width = (int) Math.ceil(background.getIntrinsicWidth() * scale * parallaxScale);
//                        int height = (int) Math.ceil(background.getIntrinsicHeight() * scale * parallaxScale);
//                        int x = (getMeasuredWidth() - width) / 2;
//                        int y = (viewHeight - height) / 2;
//                        background.setBounds(x, y, x + width, y + height);
//                        background.draw(canvas);
//                    }
//                }
//                super.onDraw(canvas);
//                if (progressVisible && radialProgress != null) {
//                    radialProgress.draw(canvas);
//                }
//            }
//
//            @Override
//            public Drawable getBackground() {
//                return background;
//            }
//
//            @Override
//            public void setBackground(Drawable drawable) {
//                background = drawable;
//            }
//
//            @Override
//            public void setAlpha(float alpha) {
//                if (radialProgress != null) {
//                    radialProgress.setOverrideAlpha(alpha);
//                }
//            }
//        };
//        int textsCount = 2;
//        if (currentWallpaper instanceof WallpapersListActivity.FileWallpaper) {
//            WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) currentWallpaper;
//            if (Theme.THEME_BACKGROUND_SLUG.equals(fileWallpaper.slug)) {
//                textsCount = 0;
//            }
//        }
//
//        page2.addView(backgroundImage, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 0, 0, 0, 48));
//        if (screenType == SCREEN_TYPE_CHANGE_BACKGROUND) {
//            backgroundImage.getImageReceiver().setDelegate((imageReceiver, set, thumb, memCache) -> {
//                if (!(currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
//                    Drawable dr = imageReceiver.getDrawable();
//                    if (set && dr != null) {
//                        if (!Theme.hasThemeKey(Theme.key_chat_serviceBackground)) {
//                            Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(dr));
//                        }
//                        listView2.invalidateViews();
//                        if (buttonsContainer != null) {
//                            for (int a = 0, N = buttonsContainer.getChildCount(); a < N; a++) {
//                                buttonsContainer.getChildAt(a).invalidate();
//                            }
//                        }
//                        if (radialProgress != null) {
//                            radialProgress.setColors(Theme.key_chat_serviceBackground, Theme.key_chat_serviceBackground, Theme.key_chat_serviceText, Theme.key_chat_serviceText);
//                        }
//                        if (!thumb && isBlurred && blurredBitmap == null) {
//                            backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
//                            updateBlurred();
//                            backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
//                        }
//                    }
//                }
//            });
//        }
//
//        if (screenType == SCREEN_TYPE_CHANGE_BACKGROUND) {
//            actionBar2.setTitle(MyLang.getString("BackgroundPreview", R.string.BackgroundPreview));
//            if (currentWallpaper instanceof WallPaper) {
//                ActionBarMenu menu2 = actionBar2.createMenu();
//                menu2.addItem(5, R.drawable.ic_share_video);
//            }
//        } else if (screenType == SCREEN_TYPE_ACCENT_COLOR) {
//            ActionBarMenu menu2 = actionBar2.createMenu();
//            saveItem = menu2.addItem(4, MyLang.getString("Save", R.string.Save).toUpperCase());
//
//            dropDownContainer = new ActionBarMenuItem(context, menu2, 0, 0);
//            dropDownContainer.setSubMenuOpenSide(1);
//            dropDownContainer.addSubItem(1, MyLang.getString("ColorPickerMainColor", R.string.ColorPickerMainColor));
//            dropDownContainer.addSubItem(2, MyLang.getString("ColorPickerBackground", R.string.ColorPickerBackground));
//            dropDownContainer.addSubItem(3, MyLang.getString("ColorPickerMyMessages", R.string.ColorPickerMyMessages));
//            dropDownContainer.setAllowCloseAnimation(false);
//            dropDownContainer.setForceSmoothKeyboard(true);
//            actionBar2.addView(dropDownContainer, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT, AndroidUtilities.isTablet() ? 64 : 56, 0, 40, 0));
//            dropDownContainer.setOnClickListener(view -> dropDownContainer.toggleSubMenu());
//
//            dropDown = new TextView(context);
//            dropDown.setGravity(Gravity.LEFT);
//            dropDown.setSingleLine(true);
//            dropDown.setLines(1);
//            dropDown.setMaxLines(1);
//            dropDown.setEllipsize(TextUtils.TruncateAt.END);
//            dropDown.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
//            dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
//            dropDown.setText(MyLang.getString("ColorPickerMainColor", R.string.ColorPickerMainColor));
//            Drawable dropDownDrawable = context.getResources().getDrawable(R.drawable.ic_arrow_drop_down).mutate();
//            dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultTitle), PorterDuff.Mode.MULTIPLY));
//            dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, dropDownDrawable, null);
//            dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4));
//            dropDown.setPadding(0, 0, AndroidUtilities.dp(10), 0);
//            dropDownContainer.addView(dropDown, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 16, 0, 0, 1));
//        } else {
//            String name = applyingTheme.info != null ? applyingTheme.info.title : applyingTheme.getName();
//            int index = name.lastIndexOf(".attheme");
//            if (index >= 0) {
//                name = name.substring(0, index);
//            }
//            actionBar2.setTitle(name);
//            if (applyingTheme.info != null && applyingTheme.info.installs_count > 0) {
//                actionBar2.setSubtitle(MyLang.formatPluralString("ThemeInstallCount", applyingTheme.info.installs_count));
//            } else {
//                actionBar2.setSubtitle(MyLang.formatDateOnline(System.currentTimeMillis() / 1000 - 60 * 60));
//            }
//        }
//
//        listView2 = new RecyclerListView(context) {
//            @Override
//            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
//                boolean result = super.drawChild(canvas, child, drawingTime);
//                return result;
//            }
//
//            @Override
//            protected void onChildPressed(View child, float x, float y, boolean pressed) {
//                if (pressed && child instanceof ChatMessageCell) {
//                    ChatMessageCell messageCell = (ChatMessageCell) child;
//                    if (!messageCell.isInsideBackground(x, y)) {
//                        return;
//                    }
//                }
//                super.onChildPressed(child, x, y, pressed);
//            }
//
//            @Override
//            protected boolean allowSelectChildAtPosition(View child) {
//                RecyclerView.ViewHolder holder = listView2.findContainingViewHolder(child);
//                if (holder != null && holder.getItemViewType() == 2) {
//                    return false;
//                }
//                return super.allowSelectChildAtPosition(child);
//            }
//        };
//        ((DefaultItemAnimator) listView2.getItemAnimator()).setDelayAnimations(false);
//        listView2.setVerticalScrollBarEnabled(true);
//        listView2.setOverScrollMode(RecyclerListView.OVER_SCROLL_NEVER);
//        if (screenType == SCREEN_TYPE_CHANGE_BACKGROUND) {
//            listView2.setPadding(0, AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4 + 48));
//        } else if (screenType == SCREEN_TYPE_ACCENT_COLOR) {
//            listView2.setPadding(0, AndroidUtilities.dp(4), 0, AndroidUtilities.dp(16));
//        } else {
//            listView2.setPadding(0, AndroidUtilities.dp(4), 0, AndroidUtilities.dp(4));
//        }
//        listView2.setClipToPadding(false);
//        listView2.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true));
//        listView2.setVerticalScrollbarPosition(MyLang.isRTL ? RecyclerListView.SCROLLBAR_POSITION_LEFT : RecyclerListView.SCROLLBAR_POSITION_RIGHT);
//        page2.addView(listView2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
//        listView2.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                listView2.invalidateViews();
//            }
//        });
//
//        page2.addView(actionBar2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
//        parallaxEffect = new WallpaperParallaxEffect(context);
//        parallaxEffect.setCallback((offsetX, offsetY) -> {
//            if (!isMotion) {
//                return;
//            }
//            float progress;
//            if (motionAnimation != null) {
//                progress = (backgroundImage.getScaleX() - 1.0f) / (parallaxScale - 1.0f);
//            } else {
//                progress = 1.0f;
//            }
//            backgroundImage.setTranslationX(offsetX * progress);
//            backgroundImage.setTranslationY(offsetY * progress);
//        });
//
//        if (screenType == SCREEN_TYPE_ACCENT_COLOR || screenType == SCREEN_TYPE_CHANGE_BACKGROUND) {
//            radialProgress = new RadialProgress2(backgroundImage);
//            radialProgress.setColors(Theme.key_chat_serviceBackground, Theme.key_chat_serviceBackground, Theme.key_chat_serviceText, Theme.key_chat_serviceText);
//
//            Rect paddings = new Rect();
//            sheetDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
//            sheetDrawable.getPadding(paddings);
//            sheetDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhite), PorterDuff.Mode.MULTIPLY));
//
//            String[] texts = new String[textsCount];
//            int[] textSizes = new int[textsCount];
//            checkBoxView = new WallpaperCheckBoxView[textsCount];
//            int maxTextSize = 0;
//            if (textsCount != 0) {
//                buttonsContainer = new FrameLayout(context);
//                texts[0] = MyLang.getString("BackgroundBlurred", R.string.BackgroundBlurred);
//                texts[1] = MyLang.getString("BackgroundMotion", R.string.BackgroundMotion);
//                TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//                textPaint.setTextSize(AndroidUtilities.dp(14));
//                textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
//                for (int a = 0; a < texts.length; a++) {
//                    textSizes[a] = (int) Math.ceil(textPaint.measureText(texts[a]));
//                    maxTextSize = Math.max(maxTextSize, textSizes[a]);
//                }
//            }
//
//            for (int a = 0; a < textsCount; a++) {
//                final int num = a;
//                checkBoxView[a] = new WallpaperCheckBoxView(context, screenType == SCREEN_TYPE_ACCENT_COLOR || !(currentWallpaper instanceof WallpapersListActivity.ColorWallpaper && a == 0));
//                checkBoxView[a].setBackgroundColor(backgroundColor);
//                checkBoxView[a].setText(texts[a], textSizes[a], maxTextSize);
//
//                if (screenType != SCREEN_TYPE_ACCENT_COLOR) {
//                    checkBoxView[a].setChecked(a == 0 ? isBlurred : isMotion, false);
//                }
//                int width = maxTextSize + AndroidUtilities.dp(14 * 2 + 28);
//                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
//                layoutParams.leftMargin = a == 1 ? width + AndroidUtilities.dp(9) : 0;
//                buttonsContainer.addView(checkBoxView[a], layoutParams);
//                WallpaperCheckBoxView view = checkBoxView[a];
//                checkBoxView[a].setOnClickListener(v -> {
//                    view.setChecked(!view.isChecked(), true);
//                    if (num == 0) {
//                        isBlurred = view.isChecked();
//                        updateBlurred();
//                    } else {
//                        isMotion = view.isChecked();
//                        parallaxEffect.setEnabled(isMotion);
//                        animateMotionChange();
//                    }
//                });
//                if (a == 2) {
//                    checkBoxView[a].setAlpha(0.0f);
//                    checkBoxView[a].setVisibility(View.INVISIBLE);
//                }
//            }
//
//            updateButtonState(false, false);
//            if (!backgroundImage.getImageReceiver().hasBitmapImage()) {
//                page2.setBackgroundColor(0xff000000);
//            }
//
//            if (screenType != SCREEN_TYPE_ACCENT_COLOR && !(currentWallpaper instanceof WallpapersListActivity.ColorWallpaper)) {
//                backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
//                backgroundImage.getImageReceiver().setForceCrossfade(true);
//            }
//        }
//
//        frameLayout = new FrameLayout(context) {
//
//            private int[] loc = new int[2];
//
//            @Override
//            public void invalidate() {
//                super.invalidate();
//                if (page2 != null) {
//                    page2.invalidate();
//                }
//            }
//
//            @Override
//            protected void onDraw(Canvas canvas) {
//                if (!AndroidUtilities.usingHardwareInput) {
//                    getLocationInWindow(loc);
//                    if (Build.VERSION.SDK_INT < 21) {
//                        loc[1] -= AndroidUtilities.statusBarHeight;
//                    }
//                    if (actionBar2.getTranslationY() != loc[1]) {
//                        actionBar2.setTranslationY(-loc[1]);
//                        page2.invalidate();
//                    }
//                    if (SystemClock.elapsedRealtime() < watchForKeyboardEndTime) {
//                        invalidate();
//                    }
//                }
//            }
//        };
//        frameLayout.setWillNotDraw(false);
//        fragmentView = frameLayout;
//        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener = () -> {
//            watchForKeyboardEndTime = SystemClock.elapsedRealtime() + 1500;
//            frameLayout.invalidate();
//        });
//
//        viewPager = new ViewPager(context);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                dotsContainer.invalidate();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//        viewPager.setAdapter(new PagerAdapter() {
//
//            @Override
//            public int getCount() {
//                return screenType != SCREEN_TYPE_PREVIEW ? 1 : 2;
//            }
//
//            @Override
//            public boolean isViewFromObject(View view, Object object) {
//                return object == view;
//            }
//
//            @Override
//            public int getItemPosition(Object object) {
//                return POSITION_UNCHANGED;
//            }
//
//            @Override
//            public Object instantiateItem(ViewGroup container, int position) {
//                View view = position == 0 ? page2 : page1;
//                container.addView(view);
//                return view;
//            }
//
//            @Override
//            public void destroyItem(ViewGroup container, int position, Object object) {
//                container.removeView((View) object);
//            }
//
//            @Override
//            public void unregisterDataSetObserver(DataSetObserver observer) {
//                if (observer != null) {
//                    super.unregisterDataSetObserver(observer);
//                }
//            }
//        });
//        AndroidUtilities.setViewPagerEdgeEffectColor(viewPager, Theme.getColor(Theme.key_actionBarDefault));
//        frameLayout.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 0, 0, 0, screenType == SCREEN_TYPE_PREVIEW ? 48 : 0));
//
//        if (screenType == SCREEN_TYPE_PREVIEW) {
//            View shadow = new View(context);
//            shadow.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
//            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1, Gravity.LEFT | Gravity.BOTTOM);
//            layoutParams.bottomMargin = AndroidUtilities.dp(48);
//            frameLayout.addView(shadow, layoutParams);
//
//            saveButtonsContainer = new FrameLayout(context);
//            saveButtonsContainer.setBackgroundColor(getButtonsColor(Theme.key_windowBackgroundWhite));
//            frameLayout.addView(saveButtonsContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.LEFT | Gravity.BOTTOM));
//
//            dotsContainer = new View(context) {
//
//                private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//
//                @Override
//                protected void onDraw(Canvas canvas) {
//                    int selected = viewPager.getCurrentItem();
//                    paint.setColor(getButtonsColor(Theme.key_chat_fieldOverlayText));
//                    for (int a = 0; a < 2; a++) {
//                        paint.setAlpha(a == selected ? 255 : 127);
//                        canvas.drawCircle(AndroidUtilities.dp(3 + 15 * a), AndroidUtilities.dp(4), AndroidUtilities.dp(3), paint);
//                    }
//                }
//            };
//            saveButtonsContainer.addView(dotsContainer, LayoutHelper.createFrame(22, 8, Gravity.CENTER));
//
//            cancelButton = new TextView(context);
//            cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
//            cancelButton.setTextColor(getButtonsColor(Theme.key_chat_fieldOverlayText));
//            cancelButton.setGravity(Gravity.CENTER);
//            cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(0x0f000000, 0));
//            cancelButton.setPadding(AndroidUtilities.dp(29), 0, AndroidUtilities.dp(29), 0);
//            cancelButton.setText(MyLang.getString("Cancel", R.string.Cancel).toUpperCase());
//            cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
//            saveButtonsContainer.addView(cancelButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
//            cancelButton.setOnClickListener(v -> cancelThemeApply(false));
//
//            doneButton = new TextView(context);
//            doneButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
//            doneButton.setTextColor(getButtonsColor(Theme.key_chat_fieldOverlayText));
//            doneButton.setGravity(Gravity.CENTER);
//            doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(0x0f000000, 0));
//            doneButton.setPadding(AndroidUtilities.dp(29), 0, AndroidUtilities.dp(29), 0);
//            doneButton.setText(MyLang.getString("ApplyTheme", R.string.ApplyTheme).toUpperCase());
//            doneButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
//            saveButtonsContainer.addView(doneButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.RIGHT));
//            doneButton.setOnClickListener(v -> {
//                ThemeInfo previousTheme = ThemeManager.getPreviousTheme();
//                if (previousTheme == null) {
//                    return;
//                }
//                ThemeAccent previousAccent;
//                if (previousTheme != null && previousTheme.prevAccentId >= 0) {
//                    previousAccent = previousTheme.themeAccentsMap.get(previousTheme.prevAccentId);
//                } else {
//                    previousAccent = previousTheme.getAccent(false);
//                }
//                parentLayout.rebuildAllFragmentViews(false, false);
//                ThemeManager.applyThemeFile(getParentActivity(), new File(applyingTheme.pathToFile), applyingTheme.name, applyingTheme.info, false);
//                //TODO 发起请求
//                //                    MessagesController.getInstance(applyingTheme.account).saveTheme(applyingTheme, null, false, false);
//                SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE).edit();
//                editor.putString("lastDayTheme", applyingTheme.getKey());
//                editor.commit();
//                finishFragment();
//                if (screenType == SCREEN_TYPE_PREVIEW) {
//                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didApplyNewTheme, previousTheme, previousAccent, deleteOnCancel);
//                }
//            });
//        }
//
//        themeDescriptions = getThemeDescriptionsInternal();
//        setCurrentImage(true);
//
//        return fragmentView;
//    }
//
//    private void saveAccentWallpaper() {
//        if (accent == null || TextUtils.isEmpty(accent.patternSlug)) {
//            return;
//        }
//        try {
//            File toFile = accent.getPathToWallpaper();
//
//            Drawable background = backgroundImage.getBackground();
//            Bitmap bitmap = backgroundImage.getImageReceiver().getBitmap();
//
//            Bitmap dst = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(dst);
//            background.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
//            background.draw(canvas);
//
//            Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
//            paint.setColorFilter(new PorterDuffColorFilter(patternColor, blendMode));
//            paint.setAlpha((int) (255 * currentIntensity));
//            canvas.drawBitmap(bitmap, 0, 0, paint);
//
//            FileOutputStream stream = new FileOutputStream(toFile);
//            dst.compress(Bitmap.CompressFormat.JPEG, 87, stream);
//            stream.close();
//        } catch (Throwable e) {
//            FileLog.e(e);
//        }
//    }
//
//    private boolean hasChanges(int type) {
//        if (editingTheme) {
//            return false;
//        }
//        if (type == 1 || type == 2) {
//            if (backupBackgroundOverrideColor != 0) {
//                if (backupBackgroundOverrideColor != accent.backgroundOverrideColor) {
//                    return true;
//                }
//            } else {
//                int defaultBackground = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper);
//                int backgroundOverrideColor = (int) accent.backgroundOverrideColor;
//                int currentBackground = backgroundOverrideColor == 0 ? defaultBackground : backgroundOverrideColor;
//                if (currentBackground != defaultBackground) {
//                    return true;
//                }
//            }
//            if (backupBackgroundGradientOverrideColor != 0) {
//                if (backupBackgroundGradientOverrideColor != accent.backgroundGradientOverrideColor) {
//                    return true;
//                }
//            } else {
//                int defaultBackgroundGradient = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to);
//                int backgroundGradientOverrideColor = (int) accent.backgroundGradientOverrideColor;
//                int currentGradient;
//                if (backgroundGradientOverrideColor == 0 && accent.backgroundGradientOverrideColor != 0) {
//                    currentGradient = 0;
//                } else {
//                    currentGradient = backgroundGradientOverrideColor == 0 ? defaultBackgroundGradient : backgroundGradientOverrideColor;
//                }
//                if (currentGradient != defaultBackgroundGradient) {
//                    return true;
//                }
//            }
//            if (accent.backgroundRotation != backupBackgroundRotation) {
//                return true;
//            }
//        }
//        if (type == 1 || type == 3) {
//            if (backupMyMessagesAccentColor != 0) {
//                if (backupMyMessagesAccentColor != accent.myMessagesAccentColor) {
//                    return true;
//                }
//            } else {
//                if (accent.myMessagesAccentColor != 0 && accent.myMessagesAccentColor != accent.accentColor) {
//                    return true;
//                }
//            }
//            if (backupMyMessagesGradientAccentColor != 0) {
//                if (backupMyMessagesGradientAccentColor != accent.myMessagesGradientAccentColor) {
//                    return true;
//                }
//            } else {
//                if (accent.myMessagesGradientAccentColor != 0) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onFragmentCreate() {
//        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
//        if (screenType == SCREEN_TYPE_ACCENT_COLOR) {
//            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
//        }
//        if (screenType != SCREEN_TYPE_PREVIEW || accent != null) {
//            if (SharedConfig.getDevicePerfomanceClass() == SharedConfig.PERFORMANCE_CLASS_LOW) {
//                int w = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
//                int h = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
//                imageFilter = (int) (w / AndroidUtilities.density) + "_" + (int) (h / AndroidUtilities.density) + "_f";
//            } else {
//                imageFilter = (int) (1080 / AndroidUtilities.density) + "_" + (int) (1920 / AndroidUtilities.density) + "_f";
//            }
//            maxWallpaperSize = Math.min(1920, Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y));
//
//            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
//            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
//            TAG = DownloadController.getInstance(currentAccount).generateObserverTag();
//
//            if (patterns == null) {
//                patterns = new ArrayList<>();
//                MessagesStorage.getInstance(currentAccount).getWallpapers();
//            }
//        } else {
//            isMotion = Theme.isWallpaperMotion();
//        }
//
//        return super.onFragmentCreate();
//    }
//
//    @Override
//    public void onFragmentDestroy() {
//        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
//        if (frameLayout != null && onGlobalLayoutListener != null) {
//            frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
//        }
//
//        if (screenType == SCREEN_TYPE_CHANGE_BACKGROUND) {
//            if (blurredBitmap != null) {
//                blurredBitmap.recycle();
//                blurredBitmap = null;
//            }
//            Theme.applyChatServiceMessageColor();
//        } else if (screenType == SCREEN_TYPE_ACCENT_COLOR) {
//            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
//        }
//        if (screenType != SCREEN_TYPE_PREVIEW || accent != null) {
//            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
//            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
//        }
//
//        super.onFragmentDestroy();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (isMotion) {
//            parallaxEffect.setEnabled(true);
//        }
//        AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);
//        AndroidUtilities.removeAdjustResize(getParentActivity(), classGuid);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (isMotion) {
//            parallaxEffect.setEnabled(false);
//        }
//    }
//
//    @Override
//    public boolean isSwipeBackEnabled(MotionEvent event) {
//        return false;
//    }
//
//    private void updateBlurred() {
//        if (isBlurred && blurredBitmap == null) {
//            if (currentWallpaperBitmap != null) {
//                originalBitmap = currentWallpaperBitmap;
//                blurredBitmap = Utilities.blurWallpaper(currentWallpaperBitmap);
//            } else {
//                ImageReceiver imageReceiver = backgroundImage.getImageReceiver();
//                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
//                    originalBitmap = imageReceiver.getBitmap();
//                    blurredBitmap = Utilities.blurWallpaper(imageReceiver.getBitmap());
//                }
//            }
//        }
//        if (isBlurred) {
//            if (blurredBitmap != null) {
//                backgroundImage.setImageBitmap(blurredBitmap);
//            }
//        } else {
//            setCurrentImage(false);
//        }
//    }
//
//    @Override
//    public boolean onBackPressed() {
//        if (!checkDiscard()) {
//            return false;
//        }
//        cancelThemeApply(true);
//        return true;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public void didReceivedNotification(int id, int account, Object... args) {
//        if (id == NotificationCenter.emojiDidLoad) {
//            if (listView == null) {
//                return;
//            }
//            int count = listView.getChildCount();
//            for (int a = 0; a < count; a++) {
//                View child = listView.getChildAt(a);
//                if (child instanceof DialogCell) {
//                    DialogCell cell = (DialogCell) child;
//                    cell.update(0);
//                }
//            }
//        } else if (id == NotificationCenter.didSetNewWallpapper) {
//            if (page2 != null) {
//                setCurrentImage(true);
//            }
//        } else if (id == NotificationCenter.wallpapersNeedReload) {
//            if (currentWallpaper instanceof WallpapersListActivity.FileWallpaper) {
//                WallpapersListActivity.FileWallpaper fileWallpaper = (WallpapersListActivity.FileWallpaper) currentWallpaper;
//                if (fileWallpaper.slug == null) {
//                    fileWallpaper.slug = (String) args[0];
//                }
//            }
//        } else if (id == NotificationCenter.wallpapersDidLoad) {
//            ArrayList<WallPaper> arrayList = (ArrayList<WallPaper>) args[0];
//            long acc = 0;
//            for (int a = 0, N = arrayList.size(); a < N; a++) {
//                WallPaper wallPaper = arrayList.get(a);
//                int high_id = (int) (wallPaper.id >> 32);
//                int lower_id = (int) wallPaper.id;
//                acc = ((acc * 20261) + 0x80000000L + high_id) % 0x80000000L;
//                acc = ((acc * 20261) + 0x80000000L + lower_id) % 0x80000000L;
//            }
//        }
//    }
//
//    private void cancelThemeApply(boolean back) {
//        if (screenType == SCREEN_TYPE_CHANGE_BACKGROUND) {
//            if (!back) {
//                finishFragment();
//            }
//            return;
//        }
//        Theme.applyPreviousTheme();
//        if (screenType == SCREEN_TYPE_ACCENT_COLOR) {
//            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
//            if (editingTheme) {
//                accent.accentColor = backupAccentColor;
//                accent.myMessagesAccentColor = backupMyMessagesAccentColor;
//                accent.myMessagesGradientAccentColor = backupMyMessagesGradientAccentColor;
//                accent.backgroundOverrideColor = backupBackgroundOverrideColor;
//                accent.backgroundGradientOverrideColor = backupBackgroundGradientOverrideColor;
//                accent.backgroundRotation = backupBackgroundRotation;
//            }
//            Theme.saveThemeAccents(applyingTheme, false, true, false, false);
//        } else {
//            if (accent != null) {
//                Theme.saveThemeAccents(applyingTheme, false, deleteOnCancel, false, false);
//            }
//            parentLayout.rebuildAllFragmentViews(false, false);
//            if (deleteOnCancel && applyingTheme.pathToFile != null && !Theme.isThemeInstalled(applyingTheme)) {
//                new File(applyingTheme.pathToFile).delete();
//            }
//        }
//        if (!back) {
//            finishFragment();
//        }
//    }
//
//    private int getButtonsColor(String key) {
//        return useDefaultThemeForButtons ? Theme.getDefaultColor(key) : Theme.getColor(key);
//    }
//
//    private void scheduleApplyColor(int color, int num, boolean applyNow) {
//        if (num == -1) {
//            if (colorType == 1 || colorType == 2) {
//                if (backupBackgroundOverrideColor != 0) {
//                    accent.backgroundOverrideColor = backupBackgroundOverrideColor;
//                } else {
//                    accent.backgroundOverrideColor = 0;
//                }
//                if (backupBackgroundGradientOverrideColor != 0) {
//                    accent.backgroundGradientOverrideColor = backupBackgroundGradientOverrideColor;
//                } else {
//                    accent.backgroundGradientOverrideColor = 0;
//                }
//                accent.backgroundRotation = backupBackgroundRotation;
//                if (colorType == 2) {
//                    int defaultBackground = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper);
//                    int defaultBackgroundGradient = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to);
//                    int backgroundGradientOverrideColor = (int) accent.backgroundGradientOverrideColor;
//                    int backgroundOverrideColor = (int) accent.backgroundOverrideColor;
//                    colorPicker.setColor(backgroundGradientOverrideColor != 0 ? backgroundGradientOverrideColor : defaultBackgroundGradient, 1);
//                    colorPicker.setColor(backgroundOverrideColor != 0 ? backgroundOverrideColor : defaultBackground, 0);
//                }
//            }
//            if (colorType == 1 || colorType == 3) {
//                if (backupMyMessagesAccentColor != 0) {
//                    accent.myMessagesAccentColor = backupMyMessagesAccentColor;
//                } else {
//                    accent.myMessagesAccentColor = 0;
//                }
//                if (backupMyMessagesGradientAccentColor != 0) {
//                    accent.myMessagesGradientAccentColor = backupMyMessagesGradientAccentColor;
//                } else {
//                    accent.myMessagesGradientAccentColor = 0;
//                }
//                if (colorType == 3) {
//                    colorPicker.setColor(accent.myMessagesGradientAccentColor, 1);
//                    colorPicker.setColor(accent.myMessagesAccentColor != 0 ? accent.myMessagesAccentColor : accent.accentColor, 0);
//                }
//            }
//            Theme.refreshThemeColors();
//            listView2.invalidateViews();
//            return;
//        }
//        lastPickedColor = color;
//        lastPickedColorNum = num;
//        if (applyNow) {
//            applyColorAction.run();
//        } else {
//            if (!applyColorScheduled) {
//                applyColorScheduled = true;
//                fragmentView.postDelayed(applyColorAction, 16L);
//            }
//        }
//    }
//
//    private void applyColor(int color, int num) {
//        if (colorType == 1) {
//            accent.accentColor = color;
//            Theme.refreshThemeColors();
//        } else if (colorType == 2) {
//            if (lastPickedColorNum == 0) {
//                accent.backgroundOverrideColor = color;
//            } else {
//                int defaultGradientColor = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to);
//                if (color == 0 && defaultGradientColor != 0) {
//                    accent.backgroundGradientOverrideColor = (1L << 32);
//                } else {
//                    accent.backgroundGradientOverrideColor = color;
//                }
//            }
//            Theme.refreshThemeColors();
//            colorPicker.setHasChanges(hasChanges(colorType));
//        } else if (colorType == 3) {
//            if (lastPickedColorNum == 0) {
//                accent.myMessagesAccentColor = color;
//            } else {
//                accent.myMessagesGradientAccentColor = color;
//            }
//            Theme.refreshThemeColors();
//            listView2.invalidateViews();
//            colorPicker.setHasChanges(hasChanges(colorType));
//        }
//
//        for (int i = 0, size = themeDescriptions.size(); i < size; i++) {
//            ThemeDescription description = themeDescriptions.get(i);
//            description.setColor(Theme.getColor(description.getCurrentKey()), false, false);
//        }
//
//        listView.invalidateViews();
//        listView2.invalidateViews();
//        if (dotsContainer != null) {
//            dotsContainer.invalidate();
//        }
//    }
//
//    private void updateButtonState(boolean ifSame, boolean animated) {
//        Object object;
//        if (selectedPattern != null) {
//            object = selectedPattern;
//        } else {
//        }
//        object = currentWallpaper;
//        if (object instanceof WallPaper || object instanceof MediaController.SearchImage) {
//            if (animated && !progressVisible) {
//                animated = false;
//            }
//            boolean fileExists;
//            File path;
//            int size;
//            String fileName;
//            if (object instanceof WallPaper) {
//                WallPaper wallPaper = (WallPaper) object;
//                fileName = FileLoader.getAttachFileName(wallPaper.document);
//                if (TextUtils.isEmpty(fileName)) {
//                    return;
//                }
//                path = FileLoader.getPathToAttach(wallPaper.document, true);
//                size = wallPaper.document.size;
//            }
//            if (fileExists = path.exists()) {
//                DownloadController.getInstance(currentAccount).removeLoadingFileObserver(this);
//                if (radialProgress != null) {
//                    radialProgress.setProgress(1, animated);
//                    radialProgress.setIcon(MediaActionDrawable.ICON_NONE, ifSame, animated);
//                }
//                backgroundImage.invalidate();
//                if (screenType == SCREEN_TYPE_CHANGE_BACKGROUND) {
//                    if (size != 0) {
//                        actionBar2.setSubtitle(AndroidUtilities.formatFileSize(size));
//                    } else {
//                        actionBar2.setSubtitle(null);
//                    }
//                }
//            } else {
//                DownloadController.getInstance(currentAccount).addLoadingFileObserver(fileName, null, this);
//                if (radialProgress != null) {
//                    boolean isLoading = FileLoader.getInstance(currentAccount).isLoadingFile(fileName);
//                    Float progress = ImageLoader.getInstance().getFileProgress(fileName);
//                    if (progress != null) {
//                        radialProgress.setProgress(progress, animated);
//                    } else {
//                        radialProgress.setProgress(0, animated);
//                    }
//                    radialProgress.setIcon(MediaActionDrawable.ICON_EMPTY, ifSame, animated);
//                }
//                if (screenType == SCREEN_TYPE_CHANGE_BACKGROUND) {
//                    actionBar2.setSubtitle(MyLang.getString("LoadingFullImage", R.string.LoadingFullImage));
//                }
//                backgroundImage.invalidate();
//            }
//            if (selectedPattern == null && buttonsContainer != null) {
//                buttonsContainer.setAlpha(fileExists ? 1.0f : 0.5f);
//            }
//            if (screenType == SCREEN_TYPE_PREVIEW) {
//                doneButton.setEnabled(fileExists);
//                doneButton.setAlpha(fileExists ? 1.0f : 0.5f);
//            } else {
//                saveItem.setEnabled(fileExists);
//                saveItem.setAlpha(fileExists ? 1.0f : 0.5f);
//            }
//        } else {
//            if (radialProgress != null) {
//                radialProgress.setIcon(MediaActionDrawable.ICON_NONE, ifSame, animated);
//            }
//        }
//    }
//
//    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
//        delegate = wallpaperActivityDelegate;
//    }
//
//    private void updateMotionButton() {
//        if (screenType == SCREEN_TYPE_CHANGE_BACKGROUND) {
//            checkBoxView[0].setVisibility(View.VISIBLE);
//            AnimatorSet animatorSet = new AnimatorSet();
//            animatorSet.playTogether(
//                    ObjectAnimator.ofFloat(checkBoxView[2], View.ALPHA, 0.0f),
//                    ObjectAnimator.ofFloat(checkBoxView[0], View.ALPHA, 1.0f));
//            animatorSet.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    checkBoxView[2].setVisibility(View.INVISIBLE);
//                }
//            });
//            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
//            animatorSet.setDuration(200);
//            animatorSet.start();
//        } else {
//            if (checkBoxView[0].isEnabled() == (selectedPattern != null)) {
//                return;
//            }
//            checkBoxView[0].setChecked(false, true);
//            checkBoxView[0].setEnabled(selectedPattern != null);
//
//            if (selectedPattern != null) {
//                checkBoxView[0].setVisibility(View.VISIBLE);
//            }
//            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) checkBoxView[1].getLayoutParams();
//            AnimatorSet animatorSet = new AnimatorSet();
//            int offset = (layoutParams.width + AndroidUtilities.dp(9)) / 2;
//            animatorSet.playTogether(ObjectAnimator.ofFloat(checkBoxView[0], View.ALPHA, 0.0f));
//            animatorSet.playTogether(ObjectAnimator.ofFloat(checkBoxView[0], View.TRANSLATION_X, offset));
//            animatorSet.playTogether(ObjectAnimator.ofFloat(checkBoxView[1], View.TRANSLATION_X, -offset));
//            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
//            animatorSet.setDuration(200);
//            animatorSet.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    checkBoxView[0].setVisibility(View.INVISIBLE);
//                }
//            });
//            animatorSet.start();
//        }
//    }
//
//    private void animateMotionChange() {
//        if (motionAnimation != null) {
//            motionAnimation.cancel();
//        }
//        motionAnimation = new AnimatorSet();
//        if (isMotion) {
//            motionAnimation.playTogether(
//                    ObjectAnimator.ofFloat(backgroundImage, View.SCALE_X, parallaxScale),
//                    ObjectAnimator.ofFloat(backgroundImage, View.SCALE_Y, parallaxScale));
//        } else {
//            motionAnimation.playTogether(
//                    ObjectAnimator.ofFloat(backgroundImage, View.SCALE_X, 1.0f),
//                    ObjectAnimator.ofFloat(backgroundImage, View.SCALE_Y, 1.0f),
//                    ObjectAnimator.ofFloat(backgroundImage, View.TRANSLATION_X, 0.0f),
//                    ObjectAnimator.ofFloat(backgroundImage, View.TRANSLATION_Y, 0.0f));
//        }
//        motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
//        motionAnimation.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                motionAnimation = null;
//            }
//        });
//        motionAnimation.start();
//    }
//
//    private void setBackgroundColor(int color, int num, boolean applyNow) {
//        if (num == 0) {
//            backgroundColor = color;
//        } else {
//            backgroundGradientColor = color;
//        }
//        if (checkBoxView != null) {
//            for (int a = 0; a < checkBoxView.length; a++) {
//                if (checkBoxView[a] != null) {
//                    if (num == 0) {
//                        checkBoxView[a].setBackgroundColor(color);
//                    } else {
//                        checkBoxView[a].setBackgroundGradientColor(color);
//                    }
//                }
//            }
//        }
//        if (backgroundGradientColor != 0) {
//            GradientDrawable gradientDrawable = new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(backgroundRotation), new int[]{backgroundColor, backgroundGradientColor});
//            backgroundImage.setBackground(gradientDrawable);
//            patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(backgroundColor, backgroundGradientColor));
//        } else {
//            backgroundImage.setBackgroundColor(backgroundColor);
//            patternColor = AndroidUtilities.getPatternColor(backgroundColor);
//        }
//
//        if (!Theme.hasThemeKey(Theme.key_chat_serviceBackground)) {
//            Theme.applyChatServiceMessageColor(new int[]{patternColor, patternColor, patternColor, patternColor});
//        }
//        if (backgroundImage != null) {
//            backgroundImage.getImageReceiver().setColorFilter(new PorterDuffColorFilter(patternColor, blendMode));
//            backgroundImage.getImageReceiver().setAlpha(currentIntensity);
//            backgroundImage.invalidate();
//        }
//        if (listView2 != null) {
//            listView2.invalidateViews();
//        }
//        if (buttonsContainer != null) {
//            for (int a = 0, N = buttonsContainer.getChildCount(); a < N; a++) {
//                buttonsContainer.getChildAt(a).invalidate();
//            }
//        }
//        if (radialProgress != null) {
//            radialProgress.setColors(Theme.key_chat_serviceBackground, Theme.key_chat_serviceBackground, Theme.key_chat_serviceText, Theme.key_chat_serviceText);
//        }
//    }
//
//    private void setCurrentImage(boolean setThumb) {
//        if (screenType == SCREEN_TYPE_PREVIEW && accent == null) {
//            backgroundImage.setBackground(Theme.getCachedWallpaper());
//        } else if (screenType == SCREEN_TYPE_CHANGE_BACKGROUND) {
//            if (currentWallpaper instanceof WallPaper) {
//                WallPaper wallPaper = (WallPaper) currentWallpaper;
//                PhotoSize thumb = setThumb ? FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 100) : null;
//                backgroundImage.setImage(ImageLocation.getForDocument(wallPaper.document), imageFilter, ImageLocation.getForDocument(thumb, wallPaper.document), "100_100_b", "jpg", wallPaper.document.size, 1, wallPaper);
//            } else if (currentWallpaper instanceof WallpapersListActivity.FileWallpaper) {
//                if (currentWallpaperBitmap != null) {
//                    backgroundImage.setImageBitmap(currentWallpaperBitmap);
//                } else {
//                    WallpapersListActivity.FileWallpaper wallPaper = (WallpapersListActivity.FileWallpaper) currentWallpaper;
//                    if (wallPaper.originalPath != null) {
//                        backgroundImage.setImage(wallPaper.originalPath.getAbsolutePath(), imageFilter, null);
//                    } else if (wallPaper.path != null) {
//                        backgroundImage.setImage(wallPaper.path.getAbsolutePath(), imageFilter, null);
//                    } else if (Theme.THEME_BACKGROUND_SLUG.equals(wallPaper.slug)) {
//                        backgroundImage.setImageDrawable(Theme.getThemedWallpaper(false, backgroundImage));
//                    } else if (wallPaper.resId != 0) {
//                        backgroundImage.setImageResource(wallPaper.resId);
//                    }
//                }
//            }
//        } else {
//            if (backgroundGradientDisposable != null) {
//                backgroundGradientDisposable.dispose();
//                backgroundGradientDisposable = null;
//            }
//            int defaultBackground = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper);
//            int backgroundOverrideColor = (int) accent.backgroundOverrideColor;
//            int color1 = backgroundOverrideColor != 0 ? backgroundOverrideColor : defaultBackground;
//            int defaultBackgroundGradient = Theme.getDefaultAccentColor(Theme.key_chat_wallpaper_gradient_to);
//            int backgroundGradientOverrideColor = (int) accent.backgroundGradientOverrideColor;
//            int color2;
//            if (backgroundGradientOverrideColor == 0 && accent.backgroundGradientOverrideColor != 0) {
//                color2 = 0;
//            } else {
//                color2 = backgroundGradientOverrideColor != 0 ? backgroundGradientOverrideColor : defaultBackgroundGradient;
//            }
//            if (!TextUtils.isEmpty(accent.patternSlug) && !ThemeManager.hasCustomWallpaper()) {
//                Drawable backgroundDrawable;
//                if (color2 != 0) {
//                    final BackgroundGradientDrawable.Orientation orientation = BackgroundGradientDrawable.getGradientOrientation(accent.backgroundRotation);
//                    final BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(orientation, new int[]{color1, color2});
//                    final BackgroundGradientDrawable.Listener listener = new BackgroundGradientDrawable.ListenerAdapter() {
//                        @Override
//                        public void onSizeReady(int width, int height) {
//                            final boolean isOrientationPortrait = AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y;
//                            final boolean isGradientPortrait = width <= height;
//                            if (isOrientationPortrait == isGradientPortrait) {
//                                backgroundImage.invalidate();
//                            }
//                        }
//                    };
//                    backgroundGradientDisposable = backgroundGradientDrawable.startDithering(BackgroundGradientDrawable.Sizes.ofDeviceScreen(), listener, 100);
//                    backgroundDrawable = backgroundGradientDrawable;
//                } else {
//                    backgroundDrawable = new ColorDrawable(color1);
//                }
//                backgroundImage.setBackground(backgroundDrawable);
//                if (selectedPattern != null) {
//                    backgroundImage.setImage(ImageLocation.getForDocument(selectedPattern.document), imageFilter, null, null, "jpg", selectedPattern.document.size, 1, selectedPattern);
//                }
//            } else {
//                backgroundImage.setBackground(Theme.getCachedWallpaper());
//            }
//            if (color2 == 0) {
//                patternColor = AndroidUtilities.getPatternColor(color1);
//            } else {
//                patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(color1, color2));
//            }
//            if (backgroundImage != null) {
//                backgroundImage.getImageReceiver().setColorFilter(new PorterDuffColorFilter(patternColor, blendMode));
//                backgroundImage.getImageReceiver().setAlpha(currentIntensity);
//                backgroundImage.invalidate();
//            }
//            if (checkBoxView != null) {
//                for (int a = 0; a < checkBoxView.length; a++) {
//                    checkBoxView[a].setBackgroundColor(color1);
//                }
//            }
//        }
//    }
//
//    public static class DialogsAdapter extends RecyclerListView.SelectionAdapter {
//
//        private Context mContext;
//
//        private ArrayList<DialogCell.CustomDialog> dialogs;
//
//        public DialogsAdapter(Context context) {
//            mContext = context;
//            dialogs = new ArrayList<>();
//
//            int date = (int) (System.currentTimeMillis() / 1000);
//            DialogCell.CustomDialog customDialog = new DialogCell.CustomDialog();
//            customDialog.name = MyLang.getString("ThemePreviewDialog1", R.string.ThemePreviewDialog1);
//            customDialog.message = MyLang.getString("ThemePreviewDialogMessage1", R.string.ThemePreviewDialogMessage1);
//            customDialog.id = 0;
//            customDialog.unread_count = 0;
//            customDialog.pinned = true;
//            customDialog.muted = false;
//            customDialog.type = 0;
//            customDialog.date = date;
//            customDialog.verified = false;
//            customDialog.isMedia = false;
//            customDialog.sent = true;
//            dialogs.add(customDialog);
//
//            customDialog = new DialogCell.CustomDialog();
//            customDialog.name = MyLang.getString("ThemePreviewDialog2", R.string.ThemePreviewDialog2);
//            customDialog.message = MyLang.getString("ThemePreviewDialogMessage2", R.string.ThemePreviewDialogMessage2);
//            customDialog.id = 1;
//            customDialog.unread_count = 2;
//            customDialog.pinned = false;
//            customDialog.muted = false;
//            customDialog.type = 0;
//            customDialog.date = date - 60 * 60;
//            customDialog.verified = false;
//            customDialog.isMedia = false;
//            customDialog.sent = false;
//            dialogs.add(customDialog);
//
//            customDialog = new DialogCell.CustomDialog();
//            customDialog.name = MyLang.getString("ThemePreviewDialog3", R.string.ThemePreviewDialog3);
//            customDialog.message = MyLang.getString("ThemePreviewDialogMessage3", R.string.ThemePreviewDialogMessage3);
//            customDialog.id = 2;
//            customDialog.unread_count = 3;
//            customDialog.pinned = false;
//            customDialog.muted = true;
//            customDialog.type = 0;
//            customDialog.date = date - 60 * 60 * 2;
//            customDialog.verified = false;
//            customDialog.isMedia = true;
//            customDialog.sent = false;
//            dialogs.add(customDialog);
//
//            customDialog = new DialogCell.CustomDialog();
//            customDialog.name = MyLang.getString("ThemePreviewDialog4", R.string.ThemePreviewDialog4);
//            customDialog.message = MyLang.getString("ThemePreviewDialogMessage4", R.string.ThemePreviewDialogMessage4);
//            customDialog.id = 3;
//            customDialog.unread_count = 0;
//            customDialog.pinned = false;
//            customDialog.muted = false;
//            customDialog.type = 2;
//            customDialog.date = date - 60 * 60 * 3;
//            customDialog.verified = false;
//            customDialog.isMedia = false;
//            customDialog.sent = false;
//            dialogs.add(customDialog);
//
//            customDialog = new DialogCell.CustomDialog();
//            customDialog.name = MyLang.getString("ThemePreviewDialog5", R.string.ThemePreviewDialog5);
//            customDialog.message = MyLang.getString("ThemePreviewDialogMessage5", R.string.ThemePreviewDialogMessage5);
//            customDialog.id = 4;
//            customDialog.unread_count = 0;
//            customDialog.pinned = false;
//            customDialog.muted = false;
//            customDialog.type = 1;
//            customDialog.date = date - 60 * 60 * 4;
//            customDialog.verified = false;
//            customDialog.isMedia = false;
//            customDialog.sent = true;
//            dialogs.add(customDialog);
//
//            customDialog = new DialogCell.CustomDialog();
//            customDialog.name = MyLang.getString("ThemePreviewDialog6", R.string.ThemePreviewDialog6);
//            customDialog.message = MyLang.getString("ThemePreviewDialogMessage6", R.string.ThemePreviewDialogMessage6);
//            customDialog.id = 5;
//            customDialog.unread_count = 0;
//            customDialog.pinned = false;
//            customDialog.muted = false;
//            customDialog.type = 0;
//            customDialog.date = date - 60 * 60 * 5;
//            customDialog.verified = false;
//            customDialog.isMedia = false;
//            customDialog.sent = false;
//            dialogs.add(customDialog);
//
//            customDialog = new DialogCell.CustomDialog();
//            customDialog.name = MyLang.getString("ThemePreviewDialog7", R.string.ThemePreviewDialog7);
//            customDialog.message = MyLang.getString("ThemePreviewDialogMessage7", R.string.ThemePreviewDialogMessage7);
//            customDialog.id = 6;
//            customDialog.unread_count = 0;
//            customDialog.pinned = false;
//            customDialog.muted = false;
//            customDialog.type = 0;
//            customDialog.date = date - 60 * 60 * 6;
//            customDialog.verified = true;
//            customDialog.isMedia = false;
//            customDialog.sent = false;
//            dialogs.add(customDialog);
//
//            customDialog = new DialogCell.CustomDialog();
//            customDialog.name = MyLang.getString("ThemePreviewDialog8", R.string.ThemePreviewDialog8);
//            customDialog.message = MyLang.getString("ThemePreviewDialogMessage8", R.string.ThemePreviewDialogMessage8);
//            customDialog.id = 0;
//            customDialog.unread_count = 0;
//            customDialog.pinned = false;
//            customDialog.muted = false;
//            customDialog.type = 0;
//            customDialog.date = date - 60 * 60 * 7;
//            customDialog.verified = true;
//            customDialog.isMedia = false;
//            customDialog.sent = false;
//            dialogs.add(customDialog);
//        }
//
//        @Override
//        public int getItemCount() {
//            return dialogs.size();
//        }
//
//        @Override
//        public boolean isEnabled(RecyclerView.ViewHolder holder) {
//            return holder.getItemViewType() != 1;
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//            View view = null;
//            if (viewType == 0) {
//                view = new DialogCell(mContext, false, false);
//            } else if (viewType == 1) {
//                view = new LoadingCell(mContext);
//            }
//            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
//            return new RecyclerListView.Holder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
//            if (viewHolder.getItemViewType() == 0) {
//                DialogCell cell = (DialogCell) viewHolder.itemView;
//                cell.useSeparator = (i != getItemCount() - 1);
//                cell.setDialog(dialogs.get(i));
//            }
//        }
//
//        @Override
//        public int getItemViewType(int i) {
//            if (i == dialogs.size()) {
//                return 1;
//            }
//            return 0;
//        }
//    }
//
//    private List<ThemeDescription> getThemeDescriptionsInternal() {
//        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = () -> {
//            if (dropDownContainer != null) {
//                dropDownContainer.redrawPopup(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
//                dropDownContainer.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), false);
//            }
//            if (sheetDrawable != null) {
//                sheetDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhite), PorterDuff.Mode.MULTIPLY));
//            }
//        };
//
//        List<ThemeDescription> items = new ArrayList<>();
//        items.add(new ThemeDescription(page1, ThemeDescription.FLAG_BACKGROUND, null, null, null, descriptionDelegate, Theme.key_windowBackgroundWhite));
//        items.add(new ThemeDescription(viewPager, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
//
//        items.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
//        items.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
//        items.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
//        items.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
//        items.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
//
//        items.add(new ThemeDescription(actionBar2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
//        items.add(new ThemeDescription(actionBar2, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
//        items.add(new ThemeDescription(actionBar2, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubtitle));
//        items.add(new ThemeDescription(actionBar2, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
//        items.add(new ThemeDescription(actionBar2, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, descriptionDelegate, Theme.key_actionBarDefaultSubmenuBackground));
//        items.add(new ThemeDescription(actionBar2, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, descriptionDelegate, Theme.key_actionBarDefaultSubmenuItem));
//
//        items.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
//        items.add(new ThemeDescription(listView2, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
//
//        items.add(new ThemeDescription(floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon));
//        items.add(new ThemeDescription(floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground));
//        items.add(new ThemeDescription(floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground));
//
//        if (!useDefaultThemeForButtons) {
//            items.add(new ThemeDescription(saveButtonsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
//            items.add(new ThemeDescription(cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
//            items.add(new ThemeDescription(doneButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
//        }
//
//        if (colorPicker != null) {
//            colorPicker.provideThemeDescriptions(items);
//        }
//
//        if (patternLayout != null) {
//            for (int a = 0; a < patternLayout.length; a++) {
//                items.add(new ThemeDescription(patternLayout[a], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
//                items.add(new ThemeDescription(patternLayout[a], 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
//            }
//
//            for (int a = 0; a < patternsButtonsContainer.length; a++) {
//                items.add(new ThemeDescription(patternsButtonsContainer[a], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
//                items.add(new ThemeDescription(patternsButtonsContainer[a], 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
//            }
//
//            items.add(new ThemeDescription(bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
//            items.add(new ThemeDescription(bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
//            items.add(new ThemeDescription(bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
//
//            for (int a = 0; a < patternsSaveButton.length; a++) {
//                items.add(new ThemeDescription(patternsSaveButton[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
//            }
//            for (int a = 0; a < patternsCancelButton.length; a++) {
//                items.add(new ThemeDescription(patternsCancelButton[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
//            }
//
//            items.add(new ThemeDescription(intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, null, null, null, Theme.key_player_progressBackground));
//            items.add(new ThemeDescription(intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, null, null, null, Theme.key_player_progress));
//
//            items.add(new ThemeDescription(intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
//
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextIn));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextOut));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyLine));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyLine));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyNameText));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyNameText));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMessageText));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMessageText));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeText));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeText));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText));
//            items.add(new ThemeDescription(listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText));
//        }
//
//        return items;
//    }
//}
