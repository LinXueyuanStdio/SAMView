package com.same.ui.theme.delegate;

import android.content.Context;
import android.view.View;

import com.same.lib.core.LineProgressView;
import com.same.lib.core.RadialProgressView;
import com.same.lib.theme.ColorApply;
import com.same.lib.theme.ThemeDescription;

import static com.same.lib.theme.ThemeDescription.FLAG_PROGRESSBAR;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class ProgressColorDelegate implements ColorApply.ColorDelegate {
    @Override
    public void apply(ThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        View viewToInvalidate = description.viewToInvalidate;
        int changeFlags = description.changeFlags;
        if (viewToInvalidate instanceof RadialProgressView) {
            ((RadialProgressView) viewToInvalidate).setProgressColor(color);
        } else if (viewToInvalidate instanceof LineProgressView) {
            if ((changeFlags & FLAG_PROGRESSBAR) != 0) {
                ((LineProgressView) viewToInvalidate).setProgressColor(color);
            } else {
                ((LineProgressView) viewToInvalidate).setBackColor(color);
            }
        }
    }
}
