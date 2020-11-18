package com.same.ui.page.language.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.ui.R;
import com.same.ui.theme.CommonTheme;
import com.timecat.component.locale.MLang;

import static com.same.lib.base.SharedConfig.isRTL;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/27
 * @description null
 * @usage null
 */
public class LanguageCell extends FrameLayout {

    private TextView textView;
    private TextView textView2;
    private ImageView checkImage;
    private boolean needDivider;
    private MLang.LocaleInfo currentLocale;
    private boolean isDialog;

    public LanguageCell(Context context, boolean dialog) {
        super(context);

        setWillNotDraw(false);
        isDialog = dialog;

        textView = new TextView(context);
        textView.setTextColor(Theme.getColor(dialog ? KeyHub.key_dialogTextBlack : KeyHub.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (MLang.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, MLang.isRTL ? 23 + 48 : 23, (isDialog ? 4 : 7), MLang.isRTL ? 23 : 23 + 48, 0));

        textView2 = new TextView(context);
        textView2.setTextColor(Theme.getColor(dialog ? KeyHub.key_dialogTextGray3 : KeyHub.key_windowBackgroundWhiteGrayText3));
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setGravity((isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(textView2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (MLang.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, MLang.isRTL ? 23 + 48 : 23, (isDialog ? 25 : 29), MLang.isRTL ? 23 : 23 + 48, 0));

        checkImage = new ImageView(context);
        checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(KeyHub.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
        checkImage.setImageResource(R.mipmap.sticker_added);
        addView(checkImage, LayoutHelper.createFrame(19, 14, (MLang.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, 23, 0, 23, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(isDialog ? 50 : 54) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void setLanguage(MLang.LocaleInfo language, String desc, boolean divider) {
        textView.setText(desc != null ? desc : language.name);
        textView2.setText(language.nameEnglish);
        currentLocale = language;
        needDivider = divider;
    }

    public void setValue(String name, String nameEnglish) {
        textView.setText(name);
        textView2.setText(nameEnglish);
        checkImage.setVisibility(INVISIBLE);
        currentLocale = null;
        needDivider = false;
    }

    public MLang.LocaleInfo getCurrentLocale() {
        return currentLocale;
    }

    public void setLanguageSelected(boolean value) {
        checkImage.setVisibility(value ? VISIBLE : INVISIBLE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(MLang.isRTL ? 0 : AndroidUtilities.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (MLang.isRTL ? AndroidUtilities.dp(20) : 0), getMeasuredHeight() - 1, CommonTheme.dividerPaint);
        }
    }
}
