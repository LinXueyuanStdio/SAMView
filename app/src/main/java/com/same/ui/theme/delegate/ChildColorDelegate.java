package com.same.ui.theme.delegate;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.SpannedString;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.same.lib.checkbox.CheckBox;
import com.same.lib.core.LineProgressView;
import com.same.lib.core.RadialProgressView;
import com.same.lib.core.SimpleTextView;
import com.same.lib.drawable.CombinedDrawable;
import com.same.lib.drawable.DrawableManager;
import com.same.lib.lottie.RLottieImageView;
import com.same.lib.radiobutton.RadioButton;
import com.same.ui.theme.span.TypefaceSpan;
import com.same.lib.theme.ColorApply;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeDescription;

import java.lang.reflect.Field;
import java.util.HashMap;

import static com.same.lib.theme.ThemeDescription.FLAG_BACKGROUND;
import static com.same.lib.theme.ThemeDescription.FLAG_BACKGROUNDFILTER;
import static com.same.lib.theme.ThemeDescription.FLAG_CELLBACKGROUNDCOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_CHECKBOX;
import static com.same.lib.theme.ThemeDescription.FLAG_CHECKBOXCHECK;
import static com.same.lib.theme.ThemeDescription.FLAG_CHECKTAG;
import static com.same.lib.theme.ThemeDescription.FLAG_DRAWABLESELECTEDSTATE;
import static com.same.lib.theme.ThemeDescription.FLAG_FASTSCROLL;
import static com.same.lib.theme.ThemeDescription.FLAG_IMAGECOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_LINKCOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_PROGRESSBAR;
import static com.same.lib.theme.ThemeDescription.FLAG_SELECTOR;
import static com.same.lib.theme.ThemeDescription.FLAG_SELECTORWHITE;
import static com.same.lib.theme.ThemeDescription.FLAG_SERVICEBACKGROUND;
import static com.same.lib.theme.ThemeDescription.FLAG_TEXTCOLOR;
import static com.same.lib.theme.ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class ChildColorDelegate implements ColorApply.ColorDelegate {
    @Override
    public void apply(ThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        View viewToInvalidate = description.viewToInvalidate;
        Class[] listClasses = description.listClasses;
        if (listClasses != null) {
            if (viewToInvalidate instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) viewToInvalidate;
                int count = viewGroup.getChildCount();
                for (int a = 0; a < count; a++) {
                    processViewColor(description, viewGroup.getChildAt(a), color);
                }
            }
            processViewColor(description, viewToInvalidate, color);
        }
    }

    private static void processViewColor(ThemeDescription description,View child, int color) {
        View viewToInvalidate = description.viewToInvalidate;
        Class[] listClasses = description.listClasses;
        String[] listClassesFieldName = description.listClassesFieldName;
        int changeFlags = description.changeFlags;
        HashMap<String, Boolean> notFoundCachedFields = description.notFoundCachedFields;
        HashMap<String, Field> cachedFields = description.cachedFields;
        for (int b = 0; b < listClasses.length; b++) {
            if (listClasses[b].isInstance(child)) {
                child.invalidate();
                boolean passedCheck;
                if ((changeFlags & FLAG_CHECKTAG) == 0 || description.checkTag(description.currentKey, child)) {
                    passedCheck = true;
                    child.invalidate();
                    if (listClassesFieldName == null && (changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                        Drawable drawable = child.getBackground();
                        if (drawable != null) {
                            if ((changeFlags & FLAG_CELLBACKGROUNDCOLOR) != 0) {
                                if (drawable instanceof CombinedDrawable) {
                                    Drawable back = ((CombinedDrawable) drawable).getBackground();
                                    if (back instanceof ColorDrawable) {
                                        ((ColorDrawable) back).setColor(color);
                                    }
                                }
                            } else {
                                if (drawable instanceof CombinedDrawable) {
                                    drawable = ((CombinedDrawable) drawable).getIcon();
                                } else if (drawable instanceof StateListDrawable || Build.VERSION.SDK_INT >= 21 && drawable instanceof RippleDrawable) {
                                    DrawableManager.setSelectorDrawableColor(drawable, color, (changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                                }
                                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                            }
                        }
                    } else if ((changeFlags & FLAG_CELLBACKGROUNDCOLOR) != 0) {
                        child.setBackgroundColor(color);
                    } else if ((changeFlags & FLAG_TEXTCOLOR) != 0) {
                        if (child instanceof TextView) {
                            ((TextView) child).setTextColor(color);
                        }
                    } else if ((changeFlags & FLAG_SERVICEBACKGROUND) != 0) {
                        Drawable background = child.getBackground();
                        if (background != null) {
                            background.setColorFilter(Theme.colorFilter);
                        }
                    } else if ((changeFlags & FLAG_SELECTOR) != 0) {
                        child.setBackgroundDrawable(DrawableManager.getSelectorDrawable(false));
                    } else if ((changeFlags & FLAG_SELECTORWHITE) != 0) {
                        child.setBackgroundDrawable(DrawableManager.getSelectorDrawable(true));
                    }
                } else {
                    passedCheck = false;
                }
                if (listClassesFieldName != null) {
                    String key = listClasses[b] + "_" + listClassesFieldName[b];
                    if (notFoundCachedFields != null && notFoundCachedFields.containsKey(key)) {
                        continue;
                    }
                    try {
                        Field field = cachedFields.get(key);
                        if (field == null) {
                            field = listClasses[b].getDeclaredField(listClassesFieldName[b]);
                            if (field != null) {
                                field.setAccessible(true);
                                cachedFields.put(key, field);
                            }
                        }
                        if (field != null) {
                            Object object = field.get(child);
                            if (object != null) {
                                if (!passedCheck && object instanceof View && !description.checkTag(description.currentKey, (View) object)) {
                                    continue;
                                }
                                if (object instanceof View) {
                                    ((View) object).invalidate();
                                }
                                if (description.lottieLayerName != null && object instanceof RLottieImageView) {
                                    ((RLottieImageView) object).setLayerColor(description.lottieLayerName + ".**", color);
                                }
                                if ((changeFlags & FLAG_USEBACKGROUNDDRAWABLE) != 0 && object instanceof View) {
                                    object = ((View) object).getBackground();
                                }
                                if ((changeFlags & FLAG_BACKGROUND) != 0 && object instanceof View) {
                                    View view = (View) object;
                                    view.setBackgroundColor(color);
                                } else if (object instanceof SimpleTextView) {
                                    if ((changeFlags & FLAG_LINKCOLOR) != 0) {
                                        ((SimpleTextView) object).setLinkTextColor(color);
                                    } else {
                                        ((SimpleTextView) object).setTextColor(color);
                                    }
                                } else if (object instanceof TextView) {
                                    TextView textView = (TextView) object;
                                    if ((changeFlags & FLAG_IMAGECOLOR) != 0) {
                                        Drawable[] drawables = textView.getCompoundDrawables();
                                        if (drawables != null) {
                                            for (int a = 0; a < drawables.length; a++) {
                                                if (drawables[a] != null) {
                                                    drawables[a].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                                }
                                            }
                                        }
                                    } else if ((changeFlags & FLAG_LINKCOLOR) != 0) {
                                        textView.getPaint().linkColor = color;
                                        textView.invalidate();
                                    } else if ((changeFlags & FLAG_FASTSCROLL) != 0) {
                                        CharSequence text = textView.getText();
                                        if (text instanceof SpannedString) {
                                            TypefaceSpan[] spans = ((SpannedString) text).getSpans(0, text.length(), TypefaceSpan.class);
                                            if (spans != null && spans.length > 0) {
                                                for (int i = 0; i < spans.length; i++) {
                                                    spans[i].setColor(color);
                                                }
                                            }
                                        }
                                    } else {
                                        textView.setTextColor(color);
                                    }
                                } else if (object instanceof ImageView) {
                                    ImageView imageView = (ImageView) object;
                                    Drawable drawable = imageView.getDrawable();
                                    if (drawable instanceof CombinedDrawable) {
                                        if ((changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                            ((CombinedDrawable) drawable).getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                        } else {
                                            ((CombinedDrawable) drawable).getIcon().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                        }
                                    } else {
                                        imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                    }
                                } else if (object instanceof Drawable) {
                                    if (object instanceof CombinedDrawable) {
                                        if ((changeFlags & FLAG_BACKGROUNDFILTER) != 0) {
                                            ((CombinedDrawable) object).getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                        } else {
                                            ((CombinedDrawable) object).getIcon().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                        }
                                    } else if (object instanceof StateListDrawable || Build.VERSION.SDK_INT >= 21 && object instanceof RippleDrawable) {
                                        DrawableManager.setSelectorDrawableColor((Drawable) object, color, (changeFlags & FLAG_DRAWABLESELECTEDSTATE) != 0);
                                    } else if (object instanceof GradientDrawable) {
                                        ((GradientDrawable) object).setColor(color);
                                    } else {
                                        ((Drawable) object).setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                                    }
                                } else if (object instanceof CheckBox) {
                                    if ((changeFlags & FLAG_CHECKBOX) != 0) {
                                        ((CheckBox) object).setBackgroundColor(color);
                                    } else if ((changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                        ((CheckBox) object).setCheckColor(color);
                                    }
                                } else if (object instanceof Integer) {
                                    field.set(child, color);
                                } else if (object instanceof RadioButton) {
                                    if ((changeFlags & FLAG_CHECKBOX) != 0) {
                                        ((RadioButton) object).setBackgroundColor(color);
                                        ((RadioButton) object).invalidate();
                                    } else if ((changeFlags & FLAG_CHECKBOXCHECK) != 0) {
                                        ((RadioButton) object).setCheckedColor(color);
                                        ((RadioButton) object).invalidate();
                                    }
                                } else if (object instanceof TextPaint) {
                                    if ((changeFlags & FLAG_LINKCOLOR) != 0) {
                                        ((TextPaint) object).linkColor = color;
                                    } else {
                                        ((TextPaint) object).setColor(color);
                                    }
                                } else if (object instanceof LineProgressView) {
                                    if ((changeFlags & FLAG_PROGRESSBAR) != 0) {
                                        ((LineProgressView) object).setProgressColor(color);
                                    } else {
                                        ((LineProgressView) object).setBackColor(color);
                                    }
                                } else if (object instanceof RadialProgressView) {
                                    ((RadialProgressView) object).setProgressColor(color);
                                } else if (object instanceof Paint) {
                                    ((Paint) object).setColor(color);
                                    child.invalidate();
                                }
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        notFoundCachedFields.put(key, true);
                    }
                }
            }
        }
    }

}
