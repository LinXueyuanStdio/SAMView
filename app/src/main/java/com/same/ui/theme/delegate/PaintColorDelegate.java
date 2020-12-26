package com.same.ui.theme.delegate;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;

import com.same.lib.theme.ColorApply;
import com.same.lib.theme.MyThemeDescription;

import static com.same.lib.theme.MyThemeDescription.FLAG_LINKCOLOR;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class PaintColorDelegate implements ColorApply.ColorDelegate {
    @Override
    public void apply(MyThemeDescription description, Context context, int color, boolean useDefault, boolean save) {
        if (description.paintToUpdate != null) {
            Paint[] paintToUpdate = description.paintToUpdate;
            for (int a = 0; a < paintToUpdate.length; a++) {
                if ((description.changeFlags & FLAG_LINKCOLOR) != 0 && paintToUpdate[a] instanceof TextPaint) {
                    ((TextPaint) paintToUpdate[a]).linkColor = color;
                } else {
                    paintToUpdate[a].setColor(color);
                }
            }
        }
    }
}
