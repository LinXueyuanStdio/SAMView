package com.same.ui.page.input;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.listview.LinearLayoutManager;
import com.same.lib.same.view.RecyclerListView;
import com.same.lib.theme.wrap.BaseThemePage;
import com.same.lib.theme.wrap.ThemeContainerLayout;
import com.same.ui.page.input.enter.ChatActivityEnterView;
import com.same.ui.page.input.enter.SizeNotifierFrameLayout;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/3/12
 * @description null
 * @usage null
 */
public class InputPage extends BaseThemePage {
    private SizeNotifierFrameLayout contentView;
    private RecyclerListView chatListView;

    private ChatActivityEnterView chatActivityEnterView;
    private ValueAnimator changeBoundAnimator;
    private Animator messageEditTextAnimator;

    protected View topView;
    protected View topLineView;
    private boolean openAnimationEnded;
    private boolean fragmentOpened;
    private boolean fragmentBeginToShow;
    private long openAnimationStartTime;

    private boolean configAnimationsEnabled;

    private boolean waitingForKeyboardOpen;
    private boolean waitingForKeyboardOpenAfterAnimation;
    private boolean wasSendTyping;
    protected boolean shouldAnimateEditTextWithBounds;
    private int animatingContentType = -1;


    private float bottomPanelTranslationY;
    private float bottomPanelTranslationYReverse;

    private boolean wasManualScroll;
    private boolean fixPaddingsInLayout;
    private boolean globalIgnoreLayout;

    protected float topViewEnterProgress;
    protected int animatedTop;
    public ValueAnimator currentTopViewAnimation;

    private int chatActivityEnterViewAnimateFromTop;
    private boolean chatActivityEnterViewAnimateBeforeSending;

    @Override
    public View createView(Context context) {

        fragmentView = new SizeNotifierFrameLayout(context, (ThemeContainerLayout) parentLayout);

        contentView = (SizeNotifierFrameLayout) fragmentView;

        chatListView = new RecyclerListView(context);
        chatListView.setNestedScrollingEnabled(false);
        chatListView.setInstantClick(true);
        chatListView.setDisableHighlightState(true);
        chatListView.setTag(1);
        chatListView.setVerticalScrollBarEnabled(true);
        chatListView.setClipToPadding(false);
        chatListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        chatListView.setLayoutManager(new LinearLayoutManager(context));
        chatListView.setBackgroundColor(Color.RED);
        contentView.addView(chatListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));


        chatActivityEnterView = new ChatActivityEnterView(getParentActivity(), contentView, this) {

            int lastContentViewHeight;
            int messageEditTextPredrawHeigth;
            int messageEditTextPredrawScrollY;
//
//            @Override
//            public boolean onInterceptTouchEvent(MotionEvent ev) {
//                if (getAlpha() != 1.0f) {
//                    return false;
//                }
//                return super.onInterceptTouchEvent(ev);
//            }
//
//            @Override
//            public boolean onTouchEvent(MotionEvent event) {
//                if (getAlpha() != 1.0f) {
//                    return false;
//                }
//                return super.onTouchEvent(event);
//            }
//
//            @Override
//            public boolean dispatchTouchEvent(MotionEvent ev) {
//                if (getAlpha() != 1.0f) {
//                    return false;
//                }
//                return super.dispatchTouchEvent(ev);
//            }
//
//            @Override
//            protected boolean pannelAnimationEnabled() {
//                if (!openAnimationEnded) {
//                    return false;
//                }
//                return true;
//            }
//
//            @Override
//            public void checkAnimation() {
//                if (actionBar.isActionModeShowed() || reportType >= 0) {
//                    if (messageEditTextAnimator != null) {
//                        messageEditTextAnimator.cancel();
//                    }
//                    if (changeBoundAnimator != null) {
//                        changeBoundAnimator.cancel();
//                    }
//                    chatActivityEnterViewAnimateFromTop = 0;
//                    shouldAnimateEditTextWithBounds = false;
//                } else {
//                    int t = getBackgroundTop();
//                    boolean rez = true;
//                    if (chatActivityEnterViewAnimateFromTop != 0 && t != chatActivityEnterViewAnimateFromTop && lastContentViewHeight == contentView.getMeasuredHeight()) {
//                        int dy = animatedTop + chatActivityEnterViewAnimateFromTop - t;
//                        animatedTop = dy;
//                        if (changeBoundAnimator != null) {
//                            changeBoundAnimator.removeAllListeners();
//                            changeBoundAnimator.cancel();
//                        }
//
//                        chatListView.setTranslationY(dy);
//                        if (topView != null && topView.getVisibility() == View.VISIBLE) {
//                            topView.setTranslationY(animatedTop + (1f - topViewEnterProgress) * topView.getLayoutParams().height);
//                            if (topLineView != null) {
//                                topLineView.setTranslationY(animatedTop);
//                            }
//                        }
//
//                        changeBoundAnimator = ValueAnimator.ofFloat(1f, 0);
//                        changeBoundAnimator.addUpdateListener(a -> {
//                            int v = (int) (dy * (float) a.getAnimatedValue());
//                            animatedTop = v;
//                            if (topView != null && topView.getVisibility() == View.VISIBLE) {
//                                topView.setTranslationY(animatedTop + (1f - topViewEnterProgress) * topView.getLayoutParams().height);
//                                if (topLineView != null) {
//                                    topLineView.setTranslationY(animatedTop);
//                                }
//                            } else {
//                                if (mentionContainer != null) {
//                                    mentionContainer.setTranslationY(v);
//                                }
//                                if (mentiondownButton != null && mentiondownButton.getTag() != null) {
//                                    mentiondownButton.setTranslationY(pagedownButton.getVisibility() != VISIBLE ? v : v - Space.dp(72));
//                                }
//                                if (pagedownButton != null && pagedownButton.getTag() != null) {
//                                    pagedownButton.setTranslationY(v);
//                                }
//                                chatListView.setTranslationY(v);
//                                invalidateChatListViewTopPadding();
//                                invalidateMessagesVisiblePart();
//                            }
//                            invalidate();
//                        });
//                        changeBoundAnimator.addListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                animatedTop = 0;
//                                if (topView != null && topView.getVisibility() == View.VISIBLE) {
//                                    topView.setTranslationY(animatedTop + (1f - topViewEnterProgress) * topView.getLayoutParams().height);
//                                    if (topLineView != null) {
//                                        topLineView.setTranslationY(animatedTop);
//                                    }
//                                } else {
//                                    chatListView.setTranslationY(0);
//                                }
//                                changeBoundAnimator = null;
//                            }
//                        });
//                        changeBoundAnimator.setDuration(200);
//                        if (chatActivityEnterViewAnimateBeforeSending) {
//                            changeBoundAnimator.setStartDelay(20);
//                        }
//                        changeBoundAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
//                        if (!waitingForSendingMessageLoad) {
//                            changeBoundAnimator.start();
//                        }
//                        invalidateChatListViewTopPadding();
//                        invalidateMessagesVisiblePart();
//                        chatActivityEnterViewAnimateFromTop = 0;
//                    } else if (lastContentViewHeight != contentView.getMeasuredHeight()) {
//                        chatActivityEnterViewAnimateFromTop = 0;
//                    }
//                    if (shouldAnimateEditTextWithBounds) {
//                        float dy = (messageEditTextPredrawHeigth - messageEditText.getMeasuredHeight()) + (messageEditTextPredrawScrollY - messageEditText.getScrollY());
//                        messageEditText.setOffsetY(messageEditText.getOffsetY() - dy);
//                        ValueAnimator a = ValueAnimator.ofFloat(messageEditText.getOffsetY(), 0);
//                        a.addUpdateListener(animation -> messageEditText.setOffsetY((float) animation.getAnimatedValue()));
//                        if (messageEditTextAnimator != null) {
//                            messageEditTextAnimator.cancel();
//                        }
//                        messageEditTextAnimator = a;
//                        a.setDuration(200);
//                        a.setStartDelay(chatActivityEnterViewAnimateBeforeSending ? 20 : 0);
//                        a.setInterpolator(CubicBezierInterpolator.DEFAULT);
//                        a.start();
//                        shouldAnimateEditTextWithBounds = false;
//                    }
//                    lastContentViewHeight = contentView.getMeasuredHeight();
//
//                    chatActivityEnterViewAnimateBeforeSending = false;
//                }
//            }
//
            @Override
            protected void onLineCountChanged(int oldLineCount, int newLineCount) {
                if (chatActivityEnterView != null) {
                    shouldAnimateEditTextWithBounds = true;
                    messageEditTextPredrawHeigth = messageEditText.getMeasuredHeight();
                    messageEditTextPredrawScrollY = messageEditText.getScrollY();
                    contentView.invalidate();
                    chatActivityEnterViewAnimateFromTop = chatActivityEnterView.getBackgroundTop();
                }
            }
        };
//        chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() {
//            @Override
//            public void onMessageSend(CharSequence message, boolean notify, int scheduleDate) {
//
//            }
//
//            @Override
//            public void onSwitchRecordMode(boolean video) {
//            }
//
//            @Override
//            public void onPreAudioVideoRecord() {
//            }
//
//            @Override
//            public void onUpdateSlowModeButton(View button, boolean show, CharSequence time) {
//            }
//
//            @Override
//            public void onTextSelectionChanged(int start, int end) {
//            }
//
//            @Override
//            public void onTextChanged(final CharSequence text, boolean bigChange) {
//            }
//
//            @Override
//            public void onTextSpansChanged(CharSequence text) {
//            }
//
//            @Override
//            public void needSendTyping() {
//            }
//
//            @Override
//            public void onAttachButtonHidden() {
//                if (actionBar.isSearchFieldVisible()) {
//                    return;
//                }
//            }
//
//            @Override
//            public void onAttachButtonShow() {
//                if (actionBar.isSearchFieldVisible()) {
//                    return;
//                }
//            }
//
//            @Override
//            public void onMessageEditEnd(boolean loading) {
//
//            }
//
//            @Override
//            public void onWindowSizeChanged(int size) {
//                if (size < Space.dp(72) + ActionBar.getCurrentActionBarHeight()) {
//                    allowStickersPanel = false;
//                    if (stickersPanel.getVisibility() == View.VISIBLE) {
//                        stickersPanel.setVisibility(View.INVISIBLE);
//                    }
//                    if (mentionContainer != null && mentionContainer.getVisibility() == View.VISIBLE) {
//                        mentionContainer.setVisibility(View.INVISIBLE);
//                        updateMessageListAccessibilityVisibility();
//                    }
//                } else {
//                    allowStickersPanel = true;
//                    if (stickersPanel.getVisibility() == View.INVISIBLE) {
//                        stickersPanel.setVisibility(View.VISIBLE);
//                    }
//                    if (mentionContainer != null && mentionContainer.getVisibility() == View.INVISIBLE && (!mentionsAdapter.isBotContext() || (allowContextBotPanel || allowContextBotPanelSecond))) {
//                        mentionContainer.setVisibility(View.VISIBLE);
//                        mentionContainer.setTag(null);
//                        updateMessageListAccessibilityVisibility();
//                    }
//                }
//
//                allowContextBotPanel = !chatActivityEnterView.isPopupShowing();
//                checkContextBotPanel();
//                chatActivityEnterViewAnimateFromTop = 0;
//                chatActivityEnterViewAnimateBeforeSending = false;
//            }
//
//            @Override
//            public void onStickersTab(boolean opened) {
//
//            }
//
//            @Override
//            public void didPressAttachButton() {
//
//            }
//
//            @Override
//            public void needStartRecordVideo(int state, boolean notify, int scheduleDate) {
//
//            }
//
//            @Override
//            public void needChangeVideoPreviewState(int state, float seekProgress) {
//
//            }
//
//            @Override
//            public void needStartRecordAudio(int state) {
//
//            }
//
//            @Override
//            public void needShowMediaBanHint() {
//            }
//
//            @Override
//            public void onStickersExpandedChange() {
//
//            }
//
//            @Override
//            public void scrollToSendingMessage() {
//
//            }
//
//            @Override
//            public boolean hasScheduledMessages() {
//                return false;
//            }
//
//            @Override
//            public void onSendLongClick() {
//
//            }
//
//            @Override
//            public void openScheduledMessages() {
//            }
//
//            @Override
//            public void onAudioVideoInterfaceUpdated() {
//            }
//
//            @Override
//            public void bottomPanelTranslationYChanged(float translation) {
//                if (translation != 0) {
//                    wasManualScroll = true;
//                }
//                bottomPanelTranslationY = chatActivityEnterView.pannelAniamationInProgress() ? chatActivityEnterView.getEmojiPadding() - translation : 0;
//                bottomPanelTranslationYReverse = chatActivityEnterView.pannelAniamationInProgress() ? translation : 0;
//                chatActivityEnterView.setTranslationY(translation);
//
//                translation += chatActivityEnterView.getTopViewTranslation();
//                chatListView.setTranslationY(translation);
//
//                if (pagedownButton != null) {
//                    pagedownButton.setTranslationY(translation);
//                    if (mentiondownButton != null) {
//                        mentiondownButton.setTranslationY(pagedownButton.getVisibility() != View.VISIBLE ? translation : translation - Space.dp(72));
//                    }
//                }
//                invalidateChatListViewTopPadding();
//                invalidateMessagesVisiblePart();
//                updateTextureViewPosition(false);
//                contentView.invalidate();
//                updateBulletinLayout();
//            }
//
//            @Override
//            public void prepareMessageSending() {
//            }
//
//            @Override
//            public void onTrendingStickersShowed(boolean show) {
//
//            }
//        });

        chatActivityEnterView.setBackgroundColor(Color.BLUE);
        chatActivityEnterView.setMinimumHeight(Space.dp(51));
        contentView.addView(chatActivityEnterView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.BOTTOM));

        return fragmentView;
    }
}
