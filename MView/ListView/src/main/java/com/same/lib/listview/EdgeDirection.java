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
@Retention(RetentionPolicy.SOURCE)
@IntDef({RecyclerView.EdgeEffectFactory.DIRECTION_LEFT, RecyclerView.EdgeEffectFactory.DIRECTION_TOP, RecyclerView.EdgeEffectFactory.DIRECTION_RIGHT, RecyclerView.EdgeEffectFactory.DIRECTION_BOTTOM})
public @interface EdgeDirection {}
