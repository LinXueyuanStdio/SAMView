package com.same.lib.core;

import android.content.Context;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/12/26
 * @description 主题描述
 * @usage null
 */
public interface ThemeDescription {

    String getCurrentKey();

    String getTitle();

    int getSetColor();

    int getCurrentColor();

    void setDefaultColor(Context context);

    void setPreviousColor(Context context);

    void startEditing();


    void apply(Context context);

    void applyColor(Context context, int color, boolean useDefault, boolean save);

    void applyColor(Context context, int color, boolean useDefault);

    ThemeDescriptionDelegate setDelegateDisabled();

    interface ThemeDescriptionDelegate {
        void didSetColor();
    }
}
