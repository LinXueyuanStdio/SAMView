package com.same.lib.listview;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/3
 * @description null
 * @usage null
 */
@IntDef({
        FastScroller.STATE_HIDDEN,
        FastScroller.STATE_VISIBLE,
        FastScroller.STATE_DRAGGING
})
@Retention(RetentionPolicy.SOURCE)
public @interface State { }
