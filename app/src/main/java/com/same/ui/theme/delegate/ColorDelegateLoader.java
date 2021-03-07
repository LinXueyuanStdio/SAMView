package com.same.ui.theme.delegate;

import com.same.lib.theme.ColorApply;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/11/18
 * @description null
 * @usage null
 */
public class ColorDelegateLoader {
    public static void init() {
        ColorApply.reset();
        ColorApply.install(new ActionBarColorDelegate());
        ColorApply.install(new BackgroundColorDelegate());
        ColorApply.install(new ChildColorDelegate());
        ColorApply.install(new DrawableColorDelegate());
        ColorApply.install(new FlagColorDelegate());
        ColorApply.install(new PaintColorDelegate());
        ColorApply.install(new ProgressColorDelegate());
        ColorApply.install(new ScrollViewColorDelegate());
        ColorApply.install(new SelectorColorDelegate());
        ColorApply.install(new ViewPagerColorDelegate());
    }
}
