package com.same.ui.page.input.enter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;
import com.same.lib.drawable.DrawableManager;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.ui.R;
import com.same.ui.page.input.InputPage;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/3/12
 * @description null
 * @usage null
 */
public class ChatActivityEnterView extends FrameLayout
        implements NotificationCenter.NotificationCenterDelegate,
        SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate {
    private ChatActivityEnterViewDelegate delegate;

    public interface ChatActivityEnterViewDelegate {
        void onMessageSend(CharSequence message, boolean notify, int scheduleDate);

        void needSendTyping();

        void onTextChanged(CharSequence text, boolean bigChange);

        void onTextSelectionChanged(int start, int end);

        void onTextSpansChanged(CharSequence text);

        void onAttachButtonHidden();

        void onAttachButtonShow();

        void onWindowSizeChanged(int size);

        void onStickersTab(boolean opened);

        void onMessageEditEnd(boolean loading);

        void didPressAttachButton();

        void needStartRecordVideo(int state, boolean notify, int scheduleDate);

        void needChangeVideoPreviewState(int state, float seekProgress);

        void onSwitchRecordMode(boolean video);

        void onPreAudioVideoRecord();

        void needStartRecordAudio(int state);

        void needShowMediaBanHint();

        void onStickersExpandedChange();

        void onUpdateSlowModeButton(View button, boolean show, CharSequence time);

        default void scrollToSendingMessage() {

        }

        default void openScheduledMessages() {

        }

        default boolean hasScheduledMessages() {
            return true;
        }

        void onSendLongClick();

        void onAudioVideoInterfaceUpdated();

        default void bottomPanelTranslationYChanged(float translation) {

        }

        default void prepareMessageSending() {

        }

        default void onTrendingStickersShowed(boolean show) {

        }
    }

    private Activity parentActivity;
    private InputPage parentFragment;

    protected View topView;

    protected EditTextCaption messageEditText;
    private boolean ignoreTextChange;
    private int innerTextChange;
    private int lineCount = 1;
    private boolean isInitLineCount;

    private ImageView[] emojiButton = new ImageView[2];
    @SuppressWarnings("FieldCanBeLocal")
    private ImageView emojiButton1;
    @SuppressWarnings("FieldCanBeLocal")
    private ImageView emojiButton2;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private int originalViewHeight;
    private LinearLayout attachLayout;
    private LinearLayout textFieldContainer;
    private FrameLayout sendButtonContainer;
    private FrameLayout doneButtonContainer;
    private ImageView doneButtonImage;
    private AnimatorSet doneButtonAnimation;

    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private int runningAnimationType;

    private AppCompatImageView sendButton;
    private AppCompatImageView actionButton;
    private AppCompatImageView editButton;
    private Drawable sendButtonDrawable;
    private Drawable inactinveSendButtonDrawable;
    private Drawable sendButtonInverseDrawable;

    public ChatActivityEnterView(Activity context, SizeNotifierFrameLayout parent, InputPage fragment) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(false);
        setClipChildren(false);


        parentActivity = context;
        parentFragment = fragment;
        sizeNotifierLayout = parent;
        sizeNotifierLayout.setDelegate(this);


        textFieldContainer = new LinearLayout(context);
        textFieldContainer.setOrientation(LinearLayout.HORIZONTAL);
        textFieldContainer.setClipChildren(false);
        textFieldContainer.setClipToPadding(false);
        textFieldContainer.setPadding(0, AndroidUtilities.dp(1), 0, 0);
        addView(textFieldContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.BOTTOM, 0, 1, 0, 0));

        FrameLayout frameLayout = new FrameLayout(context) {

            @Override
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child == messageEditText) {
                    canvas.save();
                    canvas.clipRect(0, -getTop() - textFieldContainer.getTop() - ChatActivityEnterView.this.getTop(), getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(6));
                    boolean rez = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return rez;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        frameLayout.setClipChildren(false);
        textFieldContainer.addView(frameLayout, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, Gravity.BOTTOM));

        for (int a = 0; a < 2; a++) {
            emojiButton[a] = new AppCompatImageView(context);
            emojiButton[a].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if (Build.VERSION.SDK_INT >= 21) {
                emojiButton[a].setBackgroundDrawable(DrawableManager.createSelectorDrawable(Theme.getColor(KeyHub.key_listSelector)));
            }
            frameLayout.addView(emojiButton[a], LayoutHelper.createFrame(48, 48, Gravity.BOTTOM | Gravity.LEFT, 3, 0, 0, 0));
            emojiButton[a].setOnClickListener(view -> {
                //TODO open emoji board
            });
            if (a == 1) {
                emojiButton[a].setVisibility(INVISIBLE);
                emojiButton[a].setAlpha(0.0f);
                emojiButton[a].setScaleX(0.1f);
                emojiButton[a].setScaleY(0.1f);
                emojiButton2 = emojiButton[a];
            } else {
                emojiButton1 = emojiButton[a];
            }
        }
        messageEditText = new EditTextCaption(context);
        messageEditText.setDelegate(() -> {
            if (delegate != null) {
                delegate.onTextSpansChanged(messageEditText.getText());
            }
        });
        messageEditText.setWindowView(parentActivity.getWindow().getDecorView());
        messageEditText.setAllowTextEntitiesIntersection(true);
        int flags = EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        messageEditText.setImeOptions(flags);
        messageEditText.setInputType(messageEditText.getInputType() | EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        messageEditText.setSingleLine(false);
        messageEditText.setMaxLines(6);
        messageEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        messageEditText.setGravity(Gravity.BOTTOM);
        messageEditText.setPadding(0, AndroidUtilities.dp(11), 0, AndroidUtilities.dp(12));
        messageEditText.setBackgroundDrawable(null);
        //        messageEditText.setTextColor(Theme.getColor(Theme.key_chat_messagePanelText));
        //        messageEditText.setHintColor(Theme.getColor(Theme.key_chat_messagePanelHint));
        //        messageEditText.setHintTextColor(Theme.getColor(Theme.key_chat_messagePanelHint));
        //        messageEditText.setCursorColor(Theme.getColor(Theme.key_chat_messagePanelCursor));
        frameLayout.addView(messageEditText, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM, 52, 0, 50, 0));
        messageEditText.setOnKeyListener(new OnKeyListener() {

            boolean ctrlPressed = false;

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    if (keyEvent.getAction() == 1) {

                    }
                    return true;
                } else if (i == KeyEvent.KEYCODE_ENTER) {
//                    if ((ctrlPressed || sendByEnter) && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                        sendMessage();
//                        return true;
//                    }
                } else if (i == KeyEvent.KEYCODE_CTRL_LEFT || i == KeyEvent.KEYCODE_CTRL_RIGHT) {
                    ctrlPressed = keyEvent.getAction() == KeyEvent.ACTION_DOWN;
                    return true;
                }
                return false;
            }
        });
        messageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            boolean ctrlPressed = false;

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
//                    sendMessage();
//                    return true;
                } else if (keyEvent != null && i == EditorInfo.IME_NULL) {
//                    if ((ctrlPressed || sendByEnter) && keyEvent.getAction() == KeyEvent.ACTION_DOWN && editingMessageObject == null) {
//                        sendMessage();
//                        return true;
//                    }
                }
                return false;
            }
        });
        messageEditText.addTextChangedListener(new TextWatcher() {

            private boolean processChange;
            private boolean nextChangeIsSend;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (lineCount != messageEditText.getLineCount()) {
                    if (!isInitLineCount && messageEditText.getMeasuredWidth() > 0) {
                        onLineCountChanged(lineCount, messageEditText.getLineCount());
                    }
                    lineCount = messageEditText.getLineCount();
                }

                if (innerTextChange == 1) {
                    return;
                }
                checkSendButton(true);
//                CharSequence message = AndroidUtilities.getTrimmedString(charSequence.toString());
                if (delegate != null) {
                    if (!ignoreTextChange) {
                        delegate.onTextChanged(charSequence, before > count + 1 || (count - before) > 2);
                    }
                }
                if (innerTextChange != 2 && (count - before) > 1) {
                    processChange = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (innerTextChange == 0) {
                    if (nextChangeIsSend) {
//                        sendMessage();
                        nextChangeIsSend = false;
                    }
                    if (processChange) {
                        ImageSpan[] spans = editable.getSpans(0, editable.length(), ImageSpan.class);
                        for (int i = 0; i < spans.length; i++) {
                            editable.removeSpan(spans[i]);
                        }
//                        Emoji.replaceEmoji(editable, messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20), false);
                        processChange = false;
                    }
                }
            }
        });


        sendButtonContainer = new FrameLayout(context);
        sendButtonContainer.setClipChildren(false);
        sendButtonContainer.setClipToPadding(false);
        textFieldContainer.addView(sendButtonContainer, LayoutHelper.createLinear(48, 48, Gravity.BOTTOM));

        sendButton = new AppCompatImageView(context);
        sendButton.setImageResource(R.drawable.ic_send);
//        sendButton.setVisibility(INVISIBLE);
        int color = Theme.getColor(KeyHub.key_actionBarActionModeDefault);
        sendButton.setSoundEffectsEnabled(false);
        sendButton.setScaleX(0.1f);
        sendButton.setScaleY(0.1f);
        sendButton.setAlpha(0.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            sendButton.setBackgroundDrawable(DrawableManager.createSelectorDrawable(Color.argb(24, Color.red(color), Color.green(color), Color.blue(color)), 1));
        }
        sendButtonContainer.addView(sendButton, LayoutHelper.createFrame(48, 48));
        sendButton.setOnClickListener(view -> {

        });
        sendButton.setOnLongClickListener(v -> false);


        actionButton = new AppCompatImageView(context) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (getAlpha() <= 0f) { // for accessibility
                    return false;
                }
                return super.onTouchEvent(event);
            }
        };
        actionButton.setScaleType(ImageView.ScaleType.CENTER);
        actionButton.setImageResource(R.drawable.ic_code);
        actionButton.setVisibility(GONE);
        actionButton.setScaleX(0.1f);
        actionButton.setScaleY(0.1f);
        actionButton.setAlpha(0.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            actionButton.setBackgroundDrawable(DrawableManager.createSelectorDrawable(Theme.getColor(KeyHub.key_listSelector)));
        }
        sendButtonContainer.addView(actionButton, LayoutHelper.createFrame(48, 48));
        actionButton.setOnClickListener(v -> {
        });

        editButton = new AppCompatImageView(context) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (getAlpha() <= 0f) { // for accessibility
                    return false;
                }
                return super.onTouchEvent(event);
            }
        };
        editButton.setScaleType(ImageView.ScaleType.CENTER);
        editButton.setImageResource(R.drawable.ic_edit);
        editButton.setVisibility(GONE);
        editButton.setScaleX(0.1f);
        editButton.setScaleY(0.1f);
        editButton.setAlpha(0.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            editButton.setBackgroundDrawable(DrawableManager.createSelectorDrawable(Theme.getColor(KeyHub.key_listSelector)));
        }
        sendButtonContainer.addView(editButton, LayoutHelper.createFrame(48, 48));
        editButton.setOnClickListener(v -> {
        });
        messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(messageEditText);
    }

    private void checkSendButton(boolean animated) {
        if (sendButton.getVisibility() == VISIBLE) {
            if (animated) {
                if (runningAnimationType == 2) {
                    return;
                }

                if (runningAnimation != null) {
                    runningAnimation.cancel();
                    runningAnimation = null;
                }
                if (runningAnimation2 != null) {
                    runningAnimation2.cancel();
                    runningAnimation2 = null;
                }

                if (attachLayout != null) {
                    attachLayout.setVisibility(VISIBLE);
                    runningAnimation2 = new AnimatorSet();
                    ArrayList<Animator> animators = new ArrayList<>();
                    animators.add(ObjectAnimator.ofFloat(attachLayout, View.ALPHA, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(attachLayout, View.SCALE_X, 1.0f));
                    runningAnimation2.playTogether(animators);
                    runningAnimation2.setDuration(100);
                    runningAnimation2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(runningAnimation2)) {
                                runningAnimation2 = null;
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            if (animation.equals(runningAnimation2)) {
                                runningAnimation2 = null;
                            }
                        }
                    });
                    runningAnimation2.start();
                    updateFieldRight(1);
                    if (getVisibility() == VISIBLE) {
                        delegate.onAttachButtonShow();
                    }
                }

                runningAnimation = new AnimatorSet();
                runningAnimationType = 2;

                ArrayList<Animator> animators = new ArrayList<>();

                animators.add(ObjectAnimator.ofFloat(sendButton, View.SCALE_X, 0.1f));
                animators.add(ObjectAnimator.ofFloat(sendButton, View.SCALE_Y, 0.1f));
                animators.add(ObjectAnimator.ofFloat(sendButton, View.ALPHA, 0.0f));

                runningAnimation.playTogether(animators);
                runningAnimation.setDuration(150);
                runningAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(runningAnimation)) {
                            runningAnimation = null;
                            runningAnimationType = 0;
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        if (animation.equals(runningAnimation)) {
                            runningAnimation = null;
                        }
                    }
                });
                runningAnimation.start();
            } else {
                sendButton.setScaleX(0.1f);
                sendButton.setScaleY(0.1f);
                sendButton.setAlpha(0.0f);
                sendButton.setVisibility(GONE);
                if (attachLayout != null) {
                    if (getVisibility() == VISIBLE) {
                        delegate.onAttachButtonShow();
                    }
                    attachLayout.setAlpha(1.0f);
                    attachLayout.setScaleX(1.0f);
                    attachLayout.setVisibility(VISIBLE);
                    updateFieldRight(1);
                }
            }
        }
    }

    private void updateFieldRight(int attachVisible) {
        if (messageEditText == null) {
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) messageEditText.getLayoutParams();
        int oldRightMargin = layoutParams.rightMargin;
        if (attachVisible == 1) {
            layoutParams.rightMargin = AndroidUtilities.dp(50);
        } else if (attachVisible == 2) {
            if (layoutParams.rightMargin != AndroidUtilities.dp(2)) {
                layoutParams.rightMargin = AndroidUtilities.dp(50);
            }
        } else {
            layoutParams.rightMargin = AndroidUtilities.dp(50);
        }
        if (oldRightMargin != layoutParams.rightMargin) {
            messageEditText.setLayoutParams(layoutParams);
        }
    }
    public int getBackgroundTop() {
        int t = getTop();
        if (topView != null && topView.getVisibility() == View.VISIBLE) {
            t += topView.getLayoutParams().height;
        }
        return t;
    }
    public void setDelegate(ChatActivityEnterViewDelegate chatActivityEnterViewDelegate) {
        delegate = chatActivityEnterViewDelegate;
    }

    protected void onLineCountChanged(int oldLineCount, int newLineCount) {

    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {

    }

    @Override
    public void onSizeChanged(int keyboardHeight, boolean isWidthGreater) {

    }
}
