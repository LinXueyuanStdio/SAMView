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
@IntDef({FastScroller.DRAG_X,
         FastScroller.DRAG_Y,
         FastScroller.DRAG_NONE})
@Retention(RetentionPolicy.SOURCE)
public @interface DragState{ }