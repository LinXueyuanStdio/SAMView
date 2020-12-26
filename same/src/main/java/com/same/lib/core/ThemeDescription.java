package com.same.lib.core;

import android.content.Context;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/12/26
 * @description null
 * @usage null
 */
public interface ThemeDescription {
    int getSetColor();

    String getCurrentKey();

    ThemeDescriptionDelegate setDelegateDisabled();

    void apply(Context context);

    void applyColor(Context context, int color, boolean useDefault, boolean save);

    void applyColor(Context context, int color, boolean useDefault);

    interface ThemeDescriptionDelegate {
        void didSetColor();
    }
}
