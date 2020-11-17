package com.same.ui.page.theme.cell;

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

import com.same.lib.helper.LayoutHelper;
import com.same.lib.theme.CommonTheme;
import com.same.lib.theme.Theme;
import com.same.lib.base.AndroidUtilities;
import com.same.ui.R;

import static com.same.lib.base.SharedConfig.isRTL;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/29
 * @description null
 * @usage null
 */
public class ThemeTypeCell extends FrameLayout {

    private TextView textView;
    private ImageView checkImage;
    private boolean needDivider;

    public ThemeTypeCell(Context context) {
        super(context);

        setWillNotDraw(false);

        textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, (isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRTL ? 23 + 48 : 21, 0, isRTL ? 21 : 23, 0));

        checkImage = new ImageView(context);
        checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
        checkImage.setImageResource(R.mipmap.sticker_added);
        addView(checkImage, LayoutHelper.createFrame(19, 14, (isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, 23, 0, 23, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void setValue(String name, boolean checked, boolean divider) {
        textView.setText(name);
        checkImage.setVisibility(checked ? VISIBLE : INVISIBLE);
        needDivider = divider;
    }

    public void setTypeChecked(boolean value) {
        checkImage.setVisibility(value ? VISIBLE : INVISIBLE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(isRTL ? 0 : AndroidUtilities.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (isRTL ? AndroidUtilities.dp(20) : 0), getMeasuredHeight() - 1, CommonTheme.dividerPaint);
        }
    }
}
