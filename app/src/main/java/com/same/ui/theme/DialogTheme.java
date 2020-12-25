package com.same.ui.theme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.drawable.DrawableManager;
import com.same.lib.font.FontManager;
import com.same.lib.theme.AbsTheme;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.ui.R;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class DialogTheme extends AbsTheme {

    public static Paint dialogs_tabletSeletedPaint;
    public static Paint dialogs_pinnedPaint;
    public static Paint dialogs_countPaint;
    public static Paint dialogs_errorPaint;
    public static Paint dialogs_countGrayPaint;
    public static TextPaint[] dialogs_namePaint;
    public static TextPaint[] dialogs_nameEncryptedPaint;
    public static TextPaint dialogs_searchNamePaint;
    public static TextPaint dialogs_searchNameEncryptedPaint;
    public static TextPaint[] dialogs_messagePaint;
    public static TextPaint dialogs_messageNamePaint;
    public static TextPaint[] dialogs_messagePrintingPaint;
    public static TextPaint dialogs_timePaint;
    public static TextPaint dialogs_countTextPaint;
    public static TextPaint dialogs_archiveTextPaint;
    public static TextPaint dialogs_onlinePaint;
    public static TextPaint dialogs_offlinePaint;
    public static Drawable dialogs_checkDrawable;
    public static Drawable dialogs_checkReadDrawable;
    public static Drawable dialogs_halfCheckDrawable;
    public static Drawable dialogs_clockDrawable;
    public static Drawable dialogs_errorDrawable;
    public static Drawable dialogs_reorderDrawable;
    public static Drawable dialogs_lockDrawable;
    public static Drawable dialogs_groupDrawable;
    public static Drawable dialogs_broadcastDrawable;
    public static Drawable dialogs_botDrawable;
    public static Drawable dialogs_muteDrawable;
    public static Drawable dialogs_verifiedDrawable;
    public static Drawable dialogs_verifiedCheckDrawable;
    public static Drawable dialogs_pinnedDrawable;
    public static Drawable dialogs_mentionDrawable;

    //region 业务：对话

    /**
     * 对话列表的UI资源
     * @param context 上下文
     */
    public static void createDialogsResources(Context context) {
        CommonTheme.createCommonResources(context);
        if (dialogs_namePaint == null) {
            Resources resources = context.getResources();

            dialogs_namePaint = new TextPaint[2];
            dialogs_nameEncryptedPaint = new TextPaint[2];
            dialogs_messagePaint = new TextPaint[2];
            dialogs_messagePrintingPaint = new TextPaint[2];
            for (int a = 0; a < 2; a++) {
                dialogs_namePaint[a] = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
                dialogs_namePaint[a].setTypeface(FontManager.getMediumTypeface(context));
                dialogs_nameEncryptedPaint[a] = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
                dialogs_nameEncryptedPaint[a].setTypeface(FontManager.getMediumTypeface(context));
                dialogs_messagePaint[a] = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
                dialogs_messagePrintingPaint[a] = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            }
            dialogs_searchNamePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            dialogs_searchNamePaint.setTypeface(FontManager.getMediumTypeface(context));
            dialogs_searchNameEncryptedPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            dialogs_searchNameEncryptedPaint.setTypeface(FontManager.getMediumTypeface(context));
            dialogs_messageNamePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            dialogs_messageNamePaint.setTypeface(FontManager.getMediumTypeface(context));
            dialogs_timePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            dialogs_countTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            dialogs_countTextPaint.setTypeface(FontManager.getMediumTypeface(context));
            dialogs_archiveTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            dialogs_archiveTextPaint.setTypeface(FontManager.getMediumTypeface(context));
            dialogs_onlinePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            dialogs_offlinePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);

            dialogs_tabletSeletedPaint = new Paint();
            dialogs_pinnedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Theme.dialogs_onlineCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            dialogs_countPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            dialogs_countGrayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            dialogs_errorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            Theme.moveUpDrawable = resources.getDrawable(R.drawable.preview_open);

            applyDialogsTheme();
        }

        dialogs_messageNamePaint.setTextSize(AndroidUtilities.dp(14));
        dialogs_timePaint.setTextSize(AndroidUtilities.dp(13));
        dialogs_countTextPaint.setTextSize(AndroidUtilities.dp(13));
        dialogs_archiveTextPaint.setTextSize(AndroidUtilities.dp(13));
        dialogs_onlinePaint.setTextSize(AndroidUtilities.dp(15));
        dialogs_offlinePaint.setTextSize(AndroidUtilities.dp(15));
        dialogs_searchNamePaint.setTextSize(AndroidUtilities.dp(16));
        dialogs_searchNameEncryptedPaint.setTextSize(AndroidUtilities.dp(16));
    }

    public static void applyDialogsTheme() {
        if (dialogs_namePaint == null) {
            return;
        }
        for (int a = 0; a < 2; a++) {
            dialogs_namePaint[a].setColor(Theme.getColor(KeyHub.key_chats_name));
            dialogs_nameEncryptedPaint[a].setColor(Theme.getColor(KeyHub.key_chats_secretName));
            dialogs_messagePaint[a].setColor(dialogs_messagePaint[a].linkColor = Theme.getColor(KeyHub.key_chats_message));
            dialogs_messagePrintingPaint[a].setColor(Theme.getColor(KeyHub.key_chats_actionMessage));
        }
        dialogs_searchNamePaint.setColor(Theme.getColor(KeyHub.key_chats_name));
        dialogs_searchNameEncryptedPaint.setColor(Theme.getColor(KeyHub.key_chats_secretName));
        dialogs_messageNamePaint.setColor(dialogs_messageNamePaint.linkColor = Theme.getColor(KeyHub.key_chats_nameMessage_threeLines));
        dialogs_tabletSeletedPaint.setColor(Theme.getColor(KeyHub.key_chats_tabletSelectedOverlay));
        dialogs_pinnedPaint.setColor(Theme.getColor(KeyHub.key_chats_pinnedOverlay));
        dialogs_timePaint.setColor(Theme.getColor(KeyHub.key_chats_date));
        dialogs_countTextPaint.setColor(Theme.getColor(KeyHub.key_chats_unreadCounterText));
        dialogs_archiveTextPaint.setColor(Theme.getColor(KeyHub.key_chats_archiveText));
        dialogs_countPaint.setColor(Theme.getColor(KeyHub.key_chats_unreadCounter));
        dialogs_countGrayPaint.setColor(Theme.getColor(KeyHub.key_chats_unreadCounterMuted));
        dialogs_errorPaint.setColor(Theme.getColor(KeyHub.key_chats_sentError));
        dialogs_onlinePaint.setColor(Theme.getColor(KeyHub.key_windowBackgroundWhiteBlueText3));
        dialogs_offlinePaint.setColor(Theme.getColor(KeyHub.key_windowBackgroundWhiteGrayText3));

        DrawableManager.setDrawableColorByKey(dialogs_lockDrawable, KeyHub.key_chats_secretIcon);
        DrawableManager.setDrawableColorByKey(dialogs_checkDrawable, KeyHub.key_chats_sentCheck);
        DrawableManager.setDrawableColorByKey(dialogs_checkReadDrawable, KeyHub.key_chats_sentReadCheck);
        DrawableManager.setDrawableColorByKey(dialogs_halfCheckDrawable, KeyHub.key_chats_sentReadCheck);
        DrawableManager.setDrawableColorByKey(dialogs_clockDrawable, KeyHub.key_chats_sentClock);
        DrawableManager.setDrawableColorByKey(dialogs_errorDrawable, KeyHub.key_chats_sentErrorIcon);
        DrawableManager.setDrawableColorByKey(dialogs_groupDrawable, KeyHub.key_chats_nameIcon);
        DrawableManager.setDrawableColorByKey(dialogs_broadcastDrawable, KeyHub.key_chats_nameIcon);
        DrawableManager.setDrawableColorByKey(dialogs_botDrawable, KeyHub.key_chats_nameIcon);
        DrawableManager.setDrawableColorByKey(dialogs_pinnedDrawable, KeyHub.key_chats_pinnedIcon);
        DrawableManager.setDrawableColorByKey(dialogs_reorderDrawable, KeyHub.key_chats_pinnedIcon);
        DrawableManager.setDrawableColorByKey(dialogs_muteDrawable, KeyHub.key_chats_muteIcon);
        DrawableManager.setDrawableColorByKey(dialogs_mentionDrawable, KeyHub.key_chats_mentionIcon);
        DrawableManager.setDrawableColorByKey(dialogs_verifiedDrawable, KeyHub.key_chats_verifiedBackground);
        DrawableManager.setDrawableColorByKey(dialogs_verifiedCheckDrawable, KeyHub.key_chats_verifiedCheck);
        DrawableManager.setDrawableColorByKey(Theme.dialogs_holidayDrawable, KeyHub.key_actionBarDefaultTitle);
    }

    @Override
    public void destroyResources() {

    }

    @Override
    public void createResources(Context context) {
        createDialogsResources(context);
    }

    @Override
    public void applyResources(Context context) {
        applyDialogsTheme();
    }
    //endregion

}
