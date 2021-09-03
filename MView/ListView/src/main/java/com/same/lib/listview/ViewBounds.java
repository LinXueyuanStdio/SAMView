package com.same.lib.listview;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * The set of flags that can be passed for checking the view boundary conditions.
 * CVS in the flag name indicates the child view, and PV indicates the parent view.\
 * The following S, E indicate a view's start and end points, respectively.
 * GT and LT indicate a strictly greater and less than relationship.
 * Greater than or equal (or less than or equal) can be specified by setting both GT and EQ (or
 * LT and EQ) flags.
 * For instance, setting both {@link #FLAG_CVS_GT_PVS} and {@link #FLAG_CVS_EQ_PVS} indicate the
 * child view's start should be greater than or equal to its parent start.
 */
@IntDef(flag = true, value = {
        ViewBoundsCheck.FLAG_CVS_GT_PVS, ViewBoundsCheck.FLAG_CVS_EQ_PVS, ViewBoundsCheck.FLAG_CVS_LT_PVS,
        ViewBoundsCheck.FLAG_CVS_GT_PVE, ViewBoundsCheck.FLAG_CVS_EQ_PVE, ViewBoundsCheck.FLAG_CVS_LT_PVE,
        ViewBoundsCheck.FLAG_CVE_GT_PVS, ViewBoundsCheck.FLAG_CVE_EQ_PVS, ViewBoundsCheck.FLAG_CVE_LT_PVS,
        ViewBoundsCheck.FLAG_CVE_GT_PVE, ViewBoundsCheck.FLAG_CVE_EQ_PVE, ViewBoundsCheck.FLAG_CVE_LT_PVE
})
@Retention(RetentionPolicy.SOURCE)
public @interface ViewBounds {}