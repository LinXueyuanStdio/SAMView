package com.same.ui.theme.delegate;

import android.content.Context;
import android.view.View;
import android.widget.ScrollView;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.theme.ColorApply;
import com.same.lib.theme.ThemeDescription;

import static com.same.lib.theme.ThemeDescription.FLAG_LISTGLOWCOLOR;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class ScrollViewColorDelegate implements ColorApply.ColorDelegate {
    @Override
    public void apply(ThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        View viewToInvalidate = description.viewToInvalidate;
        int changeFlags = description.changeFlags;
        if (viewToInvalidate instanceof ScrollView) {
            if ((changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
                AndroidUtilities.setScrollViewEdgeEffectColor((ScrollView) viewToInvalidate, color);
            }
        }
    }
}
