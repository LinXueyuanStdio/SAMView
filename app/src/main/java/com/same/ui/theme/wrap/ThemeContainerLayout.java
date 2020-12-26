package com.same.ui.theme.wrap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;

import com.same.lib.core.BasePage;
import com.same.lib.core.ContainerLayout;
import com.same.lib.core.ThemeDescription;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.theme.ThemeManager;

import java.util.ArrayList;

import androidx.annotation.Keep;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/12/26
 * @description null
 * @usage null
 */
public class ThemeContainerLayout extends ContainerLayout {
    private ArrayList<ArrayList<ThemeDescription>> themeAnimatorDescriptions = new ArrayList<>();
    private ArrayList<ThemeDescription> presentingFragmentDescriptions;
    private ArrayList<ThemeDescription.ThemeDescriptionDelegate> themeAnimatorDelegate = new ArrayList<>();
    private AnimatorSet themeAnimatorSet;
    private float themeAnimationValue;
    private boolean animateThemeAfterAnimation;
    private ThemeInfo animateSetThemeAfterAnimation;
    private boolean animateSetThemeNightAfterAnimation;
    private int animateSetThemeAccentIdAfterAnimation;

    public ThemeContainerLayout(Context context) {
        super(context);
    }

    @Override
    protected void checkNeedRebuild() {
        if (rebuildAfterAnimation) {
            rebuildAllFragmentViews(rebuildLastAfterAnimation, showLastAfterAnimation);
            rebuildAfterAnimation = false;
        } else if (animateThemeAfterAnimation) {
            animateThemedValues(animateSetThemeAfterAnimation, animateSetThemeAccentIdAfterAnimation, animateSetThemeNightAfterAnimation, false);
            animateSetThemeAfterAnimation = null;
            animateThemeAfterAnimation = false;
        }
    }

    @Override
    protected void loadDescFor(BasePage fragment) {
        if (themeAnimatorSet != null) {
            presentingFragmentDescriptions = fragment.getThemeDescriptions();
        }
    }

    //region 主题控制
    @Keep
    public void setThemeAnimationValue(float value) {
        themeAnimationValue = value;
        for (int j = 0, N = themeAnimatorDescriptions.size(); j < N; j++) {
            ArrayList<ThemeDescription> descriptions = themeAnimatorDescriptions.get(j);
            int[] startColors = animateStartColors.get(j);
            int[] endColors = animateEndColors.get(j);
            int rE, gE, bE, aE, rS, gS, bS, aS, a, r, g, b;
            for (int i = 0, N2 = descriptions.size(); i < N2; i++) {
                rE = Color.red(endColors[i]);
                gE = Color.green(endColors[i]);
                bE = Color.blue(endColors[i]);
                aE = Color.alpha(endColors[i]);

                rS = Color.red(startColors[i]);
                gS = Color.green(startColors[i]);
                bS = Color.blue(startColors[i]);
                aS = Color.alpha(startColors[i]);

                a = Math.min(255, (int) (aS + (aE - aS) * value));
                r = Math.min(255, (int) (rS + (rE - rS) * value));
                g = Math.min(255, (int) (gS + (gE - gS) * value));
                b = Math.min(255, (int) (bS + (bE - bS) * value));
                int color = Color.argb(a, r, g, b);
                ThemeDescription description = descriptions.get(i);
                Theme.setAnimatedColor(description.getCurrentKey(), color);
                description.applyColor(getContext(), color, false, false);
            }
        }
        for (int j = 0, N = themeAnimatorDelegate.size(); j < N; j++) {
            ThemeDescription.ThemeDescriptionDelegate delegate = themeAnimatorDelegate.get(j);
            if (delegate != null) {
                delegate.didSetColor();
            }
        }
        if (presentingFragmentDescriptions != null) {
            for (int i = 0, N = presentingFragmentDescriptions.size(); i < N; i++) {
                ThemeDescription description = presentingFragmentDescriptions.get(i);
                description.apply(getContext());
            }
        }
    }

    @Keep
    public float getThemeAnimationValue() {
        return themeAnimationValue;
    }

    private void addStartDescriptions(ArrayList<ThemeDescription> descriptions) {
        if (descriptions == null) {
            return;
        }
        themeAnimatorDescriptions.add(descriptions);
        int[] startColors = new int[descriptions.size()];
        animateStartColors.add(startColors);
        for (int a = 0, N = descriptions.size(); a < N; a++) {
            ThemeDescription description = descriptions.get(a);
            startColors[a] = description.getSetColor();
            ThemeDescription.ThemeDescriptionDelegate delegate = description.setDelegateDisabled();
            if (delegate != null && !themeAnimatorDelegate.contains(delegate)) {
                themeAnimatorDelegate.add(delegate);
            }
        }
    }

    private void addEndDescriptions(ArrayList<ThemeDescription> descriptions) {
        if (descriptions == null) {
            return;
        }
        int[] endColors = new int[descriptions.size()];
        animateEndColors.add(endColors);
        for (int a = 0, N = descriptions.size(); a < N; a++) {
            endColors[a] = descriptions.get(a).getSetColor();
        }
    }

    public void animateThemedValues(ThemeInfo theme, int accentId, boolean nightTheme, boolean instant) {
        if (transitionAnimationInProgress || startedTracking) {
            animateThemeAfterAnimation = true;
            animateSetThemeAfterAnimation = theme;
            animateSetThemeNightAfterAnimation = nightTheme;
            animateSetThemeAccentIdAfterAnimation = accentId;
            return;
        }
        if (themeAnimatorSet != null) {
            themeAnimatorSet.cancel();
            themeAnimatorSet = null;
        }
        boolean startAnimation = false;
        for (int i = 0; i < 2; i++) {
            BasePage fragment;
            if (i == 0) {
                fragment = getLastFragment();
            } else {
                if (!inPreviewMode && !transitionAnimationPreviewMode || fragmentsStack.size() <= 1) {
                    continue;
                }
                fragment = fragmentsStack.get(fragmentsStack.size() - 2);
            }
            if (fragment != null) {
                startAnimation = true;
                addStartDescriptions(fragment.getAllThemeDescriptions());
                if (i == 0) {
                    if (accentId != -1) {
                        theme.setCurrentAccentId(accentId);
                        ThemeManager.saveThemeAccents(getContext(), theme, true, false, true);
                    }
                    ThemeManager.applyTheme(getContext(), theme, nightTheme);
                }
                addEndDescriptions(fragment.getAllThemeDescriptions());
            }
        }
        if (startAnimation) {
            int count = fragmentsStack.size() - (inPreviewMode || transitionAnimationPreviewMode ? 2 : 1);
            for (int a = 0; a < count; a++) {
                BasePage fragment = fragmentsStack.get(a);
                fragment.clearViews();
                fragment.setParentLayout(this);
            }
            if (instant) {
                setThemeAnimationValue(1.0f);
                themeAnimatorDescriptions.clear();
                animateStartColors.clear();
                animateEndColors.clear();
                themeAnimatorDelegate.clear();
                presentingFragmentDescriptions = null;
                return;
            }
            Theme.setAnimatingColor(true);
            themeAnimatorSet = new AnimatorSet();
            themeAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(themeAnimatorSet)) {
                        themeAnimatorDescriptions.clear();
                        animateStartColors.clear();
                        animateEndColors.clear();
                        themeAnimatorDelegate.clear();
                        Theme.setAnimatingColor(false);
                        presentingFragmentDescriptions = null;
                        themeAnimatorSet = null;
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(themeAnimatorSet)) {
                        themeAnimatorDescriptions.clear();
                        animateStartColors.clear();
                        animateEndColors.clear();
                        themeAnimatorDelegate.clear();
                        Theme.setAnimatingColor(false);
                        presentingFragmentDescriptions = null;
                        themeAnimatorSet = null;
                    }
                }
            });
            themeAnimatorSet.playTogether(ObjectAnimator.ofFloat(this, "themeAnimationValue", 0.0f, 1.0f));
            themeAnimatorSet.setDuration(200);
            themeAnimatorSet.start();
        }
    }
    //endregion

}
