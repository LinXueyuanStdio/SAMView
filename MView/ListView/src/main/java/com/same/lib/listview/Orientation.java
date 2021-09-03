package com.same.lib.listview;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/3
 * @description null
 * @usage null
 */
@RestrictTo(LIBRARY_GROUP_PREFIX)
@IntDef({RecyclerView.HORIZONTAL, RecyclerView.VERTICAL})
@Retention(RetentionPolicy.SOURCE)
public @interface Orientation {}
