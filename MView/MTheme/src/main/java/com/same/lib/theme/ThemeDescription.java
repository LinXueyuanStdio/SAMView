package com.same.lib.theme;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/8/25
 * @description 主题描述
 * @usage null
 */
public class ThemeDescription {

    public static int FLAG_BACKGROUND = 0x00000001;
    public static int FLAG_LINKCOLOR = 0x00000002;
    public static int FLAG_TEXTCOLOR = 0x00000004;
    public static int FLAG_IMAGECOLOR = 0x00000008;
    public static int FLAG_CELLBACKGROUNDCOLOR = 0x00000010;
    public static int FLAG_BACKGROUNDFILTER = 0x00000020;
    public static int FLAG_AB_ITEMSCOLOR = 0x00000040;
    public static int FLAG_AB_TITLECOLOR = 0x00000080;
    public static int FLAG_AB_SELECTORCOLOR = 0x00000100;
    public static int FLAG_AB_AM_ITEMSCOLOR = 0x00000200;
    public static int FLAG_AB_SUBTITLECOLOR = 0x00000400;
    public static int FLAG_PROGRESSBAR = 0x00000800;
    public static int FLAG_SELECTOR = 0x00001000;
    public static int FLAG_CHECKBOX = 0x00002000;
    public static int FLAG_CHECKBOXCHECK = 0x00004000;
    public static int FLAG_LISTGLOWCOLOR = 0x00008000;
    public static int FLAG_DRAWABLESELECTEDSTATE = 0x00010000;
    public static int FLAG_USEBACKGROUNDDRAWABLE = 0x00020000;
    public static int FLAG_CHECKTAG = 0x00040000;
    public static int FLAG_SECTIONS = 0x00080000;
    public static int FLAG_AB_AM_BACKGROUND = 0x00100000;
    public static int FLAG_AB_AM_TOPBACKGROUND = 0x00200000;
    public static int FLAG_AB_AM_SELECTORCOLOR = 0x00400000;
    public static int FLAG_HINTTEXTCOLOR = 0x00800000;
    public static int FLAG_CURSORCOLOR = 0x01000000;
    public static int FLAG_FASTSCROLL = 0x02000000;
    public static int FLAG_AB_SEARCHPLACEHOLDER = 0x04000000;
    public static int FLAG_AB_SEARCH = 0x08000000;
    public static int FLAG_SELECTORWHITE = 0x10000000;
    public static int FLAG_SERVICEBACKGROUND = 0x20000000;
    public static int FLAG_AB_SUBMENUITEM = 0x40000000;
    public static int FLAG_AB_SUBMENUBACKGROUND = 0x80000000;

    public View viewToInvalidate;
    public int alphaOverride = -1;
    public Paint[] paintToUpdate;
    public Drawable[] drawablesToUpdate;
    public Class[] listClasses;
    public String currentKey;
    public String lottieLayerName;
    public ThemeDescriptionDelegate delegate;
    public int previousColor;
    public boolean[] previousIsDefault = new boolean[1];
    public int defaultColor;
    public int currentColor;
    public int changeFlags;
    public String[] listClassesFieldName;

    public HashMap<String, Field> cachedFields;
    public HashMap<String, Boolean> notFoundCachedFields;

    public interface ThemeDescriptionDelegate {
        void didSetColor();
    }

    public ThemeDescription(View view, int flags, Class[] classes, Paint[] paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key, Object unused) {
        currentKey = key;
        paintToUpdate = paint;
        drawablesToUpdate = drawables;
        viewToInvalidate = view;
        changeFlags = flags;
        listClasses = classes;
        delegate = themeDescriptionDelegate;
    }

    public ThemeDescription(View view, int flags, Class[] classes, Paint paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        currentKey = key;
        if (paint != null) {
            paintToUpdate = new Paint[]{paint};
        }
        drawablesToUpdate = drawables;
        viewToInvalidate = view;
        changeFlags = flags;
        listClasses = classes;
        delegate = themeDescriptionDelegate;
    }

    public ThemeDescription(View view, int flags, Class[] classes, String[] classesFields, Paint[] paint, Drawable[] drawables, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        this(view, flags, classes, classesFields, paint, drawables, -1, themeDescriptionDelegate, key);
    }

    public ThemeDescription(View view, int flags, Class[] classes, String[] classesFields, Paint[] paint, Drawable[] drawables, int alpha, ThemeDescriptionDelegate themeDescriptionDelegate, String key) {
        currentKey = key;
        paintToUpdate = paint;
        drawablesToUpdate = drawables;
        viewToInvalidate = view;
        changeFlags = flags;
        listClasses = classes;
        listClassesFieldName = classesFields;
        alphaOverride = alpha;
        delegate = themeDescriptionDelegate;
        cachedFields = new HashMap<>();
        notFoundCachedFields = new HashMap<>();
    }

    public ThemeDescription(View view, int flags, Class[] classes, String[] classesFields, String layerName, String key) {
        currentKey = key;
        lottieLayerName = layerName;
        viewToInvalidate = view;
        changeFlags = flags;
        listClasses = classes;
        listClassesFieldName = classesFields;
        cachedFields = new HashMap<>();
        notFoundCachedFields = new HashMap<>();
    }

    public ThemeDescriptionDelegate setDelegateDisabled() {
        ThemeDescriptionDelegate oldDelegate = delegate;
        delegate = null;
        return oldDelegate;
    }

    public void setColor(Context context, int color, boolean useDefault) {
        setColor(context, color, useDefault, true);
    }

    public boolean checkTag(String key, View view) {
        if (key == null || view == null) {
            return false;
        }
        Object viewTag = view.getTag();
        if (viewTag instanceof String) {
            return ((String) viewTag).contains(key);
        }
        return false;
    }

    public void setColor(Context context, int color, boolean useDefault, boolean save) {
        if (save) {
            Theme.setColor(context, currentKey, color, useDefault);
        }
        currentColor = color;
        if (alphaOverride > 0) {
            color = Color.argb(alphaOverride, Color.red(color), Color.green(color), Color.blue(color));
        }
        ColorApply.setColor(this, context, color, useDefault, save);
        if (viewToInvalidate != null && (listClasses == null || listClasses.length == 0)) {
            if ((changeFlags & FLAG_SELECTOR) != 0) {
                viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            } else if ((changeFlags & FLAG_SELECTORWHITE) != 0) {
                viewToInvalidate.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            }
        }
        if (delegate != null) {
            delegate.didSetColor();
        }
        if (viewToInvalidate != null) {
            viewToInvalidate.invalidate();
        }
    }

    public String getCurrentKey() {
        return currentKey;
    }

    public void startEditing() {
        currentColor = previousColor = Theme.getColor(currentKey, previousIsDefault);
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public int getSetColor() {
        return Theme.getColor(currentKey);
    }

    public void setDefaultColor(Context context) {
        setColor(context, Theme.getDefaultColor(currentKey), true);
    }

    public void setPreviousColor(Context context) {
        setColor(context, previousColor, previousIsDefault[0]);
    }

    public String getTitle() {
        return currentKey;
    }
}
