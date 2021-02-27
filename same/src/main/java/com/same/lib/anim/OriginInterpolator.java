package com.same.lib.anim;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/12/26
 * @description null
 * @usage null
 */
public class OriginInterpolator {
    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    public static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    public static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
}
