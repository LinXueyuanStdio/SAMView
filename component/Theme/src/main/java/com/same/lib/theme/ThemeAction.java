package com.same.lib.theme;

import android.content.Context;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public interface ThemeAction {
    void destroyResources();

    void createResources(Context context);

    void applyResources(Context context);
}
