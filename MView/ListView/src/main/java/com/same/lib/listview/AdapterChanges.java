package com.same.lib.listview;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/3
 * @description null
 * @usage null
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * The set of flags that might be passed to
 * {@link #recordPreLayoutInformation(RecyclerView.State, ViewHolder, int, List)}.
 */
@IntDef(flag = true, value = {
        RecyclerView.ItemAnimator.FLAG_CHANGED,
        RecyclerView.ItemAnimator.FLAG_REMOVED,
        RecyclerView.ItemAnimator.FLAG_MOVED,
        RecyclerView.ItemAnimator.FLAG_INVALIDATED,
        RecyclerView.ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT
})
@Retention(RetentionPolicy.SOURCE)
public @interface AdapterChanges {}