package com.same.lib.core;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.same.lib.drawable.ColorManager;
import com.same.lib.drawable.DrawableManager;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.util.KeyHub;
import com.same.lib.util.Space;
import com.same.lib.util.Store;


/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/8/25
 * @description null
 * @usage null
 */

public class ActionBarMenuSubItem extends FrameLayout {

    private TextView textView;
    private ImageView imageView;

    private int textColor = ColorManager.getColor(KeyHub.key_actionBarDefaultSubmenuItem);
    private int iconColor = ColorManager.getColor(KeyHub.key_actionBarDefaultSubmenuItemIcon);
    private int selectorColor = ColorManager.getColor(KeyHub.key_actionBarButtonSelector);

    public ActionBarMenuSubItem(Context context) {
        super(context);

        setBackground(DrawableManager.createSelectorDrawable(selectorColor, 2));
        setPadding(Space.dp(18), 0, Space.dp(18), 0);

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.MULTIPLY));
        addView(imageView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 40, Gravity.CENTER_VERTICAL | (Store.isRTL ? Gravity.RIGHT : Gravity.LEFT)));

        textView = new TextView(context);
        textView.setLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(textColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (Store.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Space.dp(48), View.MeasureSpec.EXACTLY));
    }

    public void setTextAndIcon(CharSequence text, int icon) {
        textView.setText(text);
        if (icon != 0) {
            imageView.setImageResource(icon);
            imageView.setVisibility(VISIBLE);
            textView.setPadding(Store.isRTL ? 0 : Space.dp(43), 0, Store.isRTL ? Space.dp(43) : 0, 0);
        } else {
            imageView.setVisibility(INVISIBLE);
            textView.setPadding(0, 0, 0, 0);
        }
    }

    public void setColors(int textColor, int iconColor) {
        setTextColor(textColor);
        setIconColor(iconColor);
    }

    public void setTextColor(int textColor) {
        if (this.textColor != textColor) {
            textView.setTextColor(this.textColor = textColor);
        }
    }

    public void setIconColor(int iconColor) {
        if (this.iconColor != iconColor) {
            imageView.setColorFilter(new PorterDuffColorFilter(this.iconColor = iconColor, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setIcon(int resId) {
        imageView.setImageResource(resId);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setSelectorColor(int selectorColor) {
        if (this.selectorColor != selectorColor) {
            setBackground(DrawableManager.createSelectorDrawable(this.selectorColor = selectorColor, 2));
        }
    }
}

