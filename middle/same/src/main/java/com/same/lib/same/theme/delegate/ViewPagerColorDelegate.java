package com.same.lib.same.theme.delegate;

import android.content.Context;
import android.view.View;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.theme.ColorApply;
import com.same.lib.theme.MyThemeDescription;

import androidx.viewpager.widget.ViewPager;

import static com.same.lib.theme.MyThemeDescription.FLAG_LISTGLOWCOLOR;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class ViewPagerColorDelegate implements ColorApply.ColorDelegate {
    @Override
    public void apply(MyThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        View viewToInvalidate = description.viewToInvalidate;
        int changeFlags = description.changeFlags;
        if (viewToInvalidate instanceof ViewPager) {
            if ((changeFlags & FLAG_LISTGLOWCOLOR) != 0) {
                AndroidUtilities.setViewPagerEdgeEffectColor((ViewPager) viewToInvalidate, color);
            }
        }
    }
}
