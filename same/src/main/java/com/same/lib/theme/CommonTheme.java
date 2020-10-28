package com.same.lib.theme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

import com.same.lib.AbsTheme;
import com.same.lib.R;
import com.same.lib.drawable.RLottieDrawable;
import com.same.lib.util.AndroidUtilities;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class CommonTheme extends AbsTheme {

    public static Paint dividerPaint;
    public static Paint linkSelectionPaint;
    public static Paint checkboxSquare_eraserPaint;
    public static Paint checkboxSquare_checkPaint;
    public static Paint checkboxSquare_backgroundPaint;
    public static Paint avatar_backgroundPaint;

    public static Drawable listSelector;
    public static Drawable[] avatarDrawables = new Drawable[10];
    public static RLottieDrawable dialogs_archiveAvatarDrawable;
    public static RLottieDrawable dialogs_archiveDrawable;
    public static RLottieDrawable dialogs_unarchiveDrawable;
    public static RLottieDrawable dialogs_pinArchiveDrawable;
    public static RLottieDrawable dialogs_unpinArchiveDrawable;
    public static RLottieDrawable dialogs_hidePsaDrawable;

    public static boolean dialogs_archiveDrawableRecolored;
    public static boolean dialogs_hidePsaDrawableRecolored;
    public static boolean dialogs_archiveAvatarDrawableRecolored;

    //region 业务：通用
    public static void createCommonResources(Context context) {
        if (dividerPaint == null) {
            dividerPaint = new Paint();
            dividerPaint.setStrokeWidth(1);

            avatar_backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            checkboxSquare_checkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            checkboxSquare_checkPaint.setStyle(Paint.Style.STROKE);
            checkboxSquare_checkPaint.setStrokeWidth(AndroidUtilities.dp(2));
            checkboxSquare_checkPaint.setStrokeCap(Paint.Cap.ROUND);
            checkboxSquare_eraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            checkboxSquare_eraserPaint.setColor(0);
            checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            checkboxSquare_backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            linkSelectionPaint = new Paint();

            Resources resources = context.getResources();

            avatarDrawables[0] = resources.getDrawable(R.drawable.chats_saved);
            avatarDrawables[1] = resources.getDrawable(R.drawable.ghost);
            avatarDrawables[2] = resources.getDrawable(R.drawable.folders_private);
            avatarDrawables[3] = resources.getDrawable(R.drawable.folders_requests);
            avatarDrawables[4] = resources.getDrawable(R.drawable.folders_group);
            avatarDrawables[5] = resources.getDrawable(R.drawable.folders_channel);
            avatarDrawables[6] = resources.getDrawable(R.drawable.folders_bot);
            avatarDrawables[7] = resources.getDrawable(R.drawable.folders_mute);
            avatarDrawables[8] = resources.getDrawable(R.drawable.folders_read);
            avatarDrawables[9] = resources.getDrawable(R.drawable.folders_archive);


            if (dialogs_archiveAvatarDrawable != null) {
                dialogs_archiveAvatarDrawable.setCallback(null);
                dialogs_archiveAvatarDrawable.recycle();
            }
            if (dialogs_archiveDrawable != null) {
                dialogs_archiveDrawable.recycle();
            }
            if (dialogs_unarchiveDrawable != null) {
                dialogs_unarchiveDrawable.recycle();
            }
            if (dialogs_pinArchiveDrawable != null) {
                dialogs_pinArchiveDrawable.recycle();
            }
            if (dialogs_unpinArchiveDrawable != null) {
                dialogs_unpinArchiveDrawable.recycle();
            }
            if (dialogs_hidePsaDrawable != null) {
                dialogs_hidePsaDrawable.recycle();
            }
            dialogs_archiveAvatarDrawable = new RLottieDrawable(R.raw.chats_archiveavatar, "chats_archiveavatar", AndroidUtilities.dp(36), AndroidUtilities.dp(36), false, null);
            dialogs_archiveDrawable = new RLottieDrawable(R.raw.chats_archive, "chats_archive", AndroidUtilities.dp(36), AndroidUtilities.dp(36));
            dialogs_unarchiveDrawable = new RLottieDrawable(R.raw.chats_unarchive, "chats_unarchive", AndroidUtilities.dp(AndroidUtilities.dp(36)), AndroidUtilities.dp(36));
            dialogs_pinArchiveDrawable = new RLottieDrawable(R.raw.chats_hide, "chats_hide", AndroidUtilities.dp(36), AndroidUtilities.dp(36));
            dialogs_unpinArchiveDrawable = new RLottieDrawable(R.raw.chats_unhide, "chats_unhide", AndroidUtilities.dp(36), AndroidUtilities.dp(36));
            dialogs_hidePsaDrawable = new RLottieDrawable(R.raw.chat_audio_record_delete, "chats_psahide", AndroidUtilities.dp(30), AndroidUtilities.dp(30));

            applyCommonTheme();
        }
    }

    public static void applyCommonTheme() {
        if (dividerPaint == null) {
            return;
        }
        dividerPaint.setColor(Theme.getColor(Theme.key_divider));
        linkSelectionPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));

        for (int a = 0; a < avatarDrawables.length; a++) {
            Theme.setDrawableColorByKey(avatarDrawables[a], Theme.key_avatar_text);
        }

        dialogs_archiveAvatarDrawable.beginApplyLayerColors();
        dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", Theme.getNonAnimatedColor(Theme.key_avatar_backgroundArchived));
        dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", Theme.getNonAnimatedColor(Theme.key_avatar_backgroundArchived));
        dialogs_archiveAvatarDrawable.setLayerColor("Box2.**", Theme.getNonAnimatedColor(Theme.key_avatar_text));
        dialogs_archiveAvatarDrawable.setLayerColor("Box1.**", Theme.getNonAnimatedColor(Theme.key_avatar_text));
        dialogs_archiveAvatarDrawable.commitApplyLayerColors();
        dialogs_archiveAvatarDrawableRecolored = false;
        dialogs_archiveAvatarDrawable.setAllowDecodeSingleFrame(true);

        dialogs_pinArchiveDrawable.beginApplyLayerColors();
        dialogs_pinArchiveDrawable.setLayerColor("Arrow.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_pinArchiveDrawable.setLayerColor("Line.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_pinArchiveDrawable.commitApplyLayerColors();

        dialogs_unpinArchiveDrawable.beginApplyLayerColors();
        dialogs_unpinArchiveDrawable.setLayerColor("Arrow.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_unpinArchiveDrawable.setLayerColor("Line.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_unpinArchiveDrawable.commitApplyLayerColors();

        dialogs_hidePsaDrawable.beginApplyLayerColors();
        dialogs_hidePsaDrawable.setLayerColor("Line 1.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveBackground));
        dialogs_hidePsaDrawable.setLayerColor("Line 2.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveBackground));
        dialogs_hidePsaDrawable.setLayerColor("Line 3.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveBackground));
        dialogs_hidePsaDrawable.setLayerColor("Cup Red.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_hidePsaDrawable.setLayerColor("Box.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_hidePsaDrawable.commitApplyLayerColors();
        dialogs_hidePsaDrawableRecolored = false;

        dialogs_archiveDrawable.beginApplyLayerColors();
        dialogs_archiveDrawable.setLayerColor("Arrow.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveBackground));
        dialogs_archiveDrawable.setLayerColor("Box2.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_archiveDrawable.setLayerColor("Box1.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_archiveDrawable.commitApplyLayerColors();
        dialogs_archiveDrawableRecolored = false;

        dialogs_unarchiveDrawable.beginApplyLayerColors();
        dialogs_unarchiveDrawable.setLayerColor("Arrow1.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_unarchiveDrawable.setLayerColor("Arrow2.**", Theme.getNonAnimatedColor(Theme.key_chats_archivePinBackground));
        dialogs_unarchiveDrawable.setLayerColor("Box2.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_unarchiveDrawable.setLayerColor("Box1.**", Theme.getNonAnimatedColor(Theme.key_chats_archiveIcon));
        dialogs_unarchiveDrawable.commitApplyLayerColors();
    }

    @Override
    public void destroyResources() {

    }

    @Override
    public void createResources(Context context) {
        createCommonResources(context);
    }
    //endregion
}
