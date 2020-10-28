package com.same.ui.theme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.same.lib.AbsTheme;
import com.same.lib.drawable.CombinedDrawable;
import com.same.lib.drawable.RLottieDrawable;
import com.same.lib.theme.MessageDrawable;
import com.same.lib.theme.Theme;
import com.same.lib.util.AndroidUtilities;
import com.same.lib.util.SharedConfig;
import com.same.ui.R;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class ChatTheme extends AbsTheme {
    static final Object sync = new Object();

    public static Paint chat_docBackPaint;
    public static Paint chat_deleteProgressPaint;
    public static Paint chat_botProgressPaint;
    public static Paint chat_urlPaint;
    public static Paint chat_textSearchSelectionPaint;
    public static Paint chat_instantViewRectPaint;
    public static Paint chat_pollTimerPaint;
    public static Paint chat_replyLinePaint;
    public static Paint chat_msgErrorPaint;
    public static Paint chat_statusPaint;
    public static Paint chat_statusRecordPaint;
    public static Paint chat_actionBackgroundPaint;
    public static Paint chat_timeBackgroundPaint;
    public static Paint chat_composeBackgroundPaint;
    public static Paint chat_radialProgressPaint;
    public static Paint chat_radialProgress2Paint;
    public static TextPaint chat_msgTextPaint;
    public static TextPaint chat_actionTextPaint;
    public static TextPaint chat_msgBotButtonPaint;
    public static TextPaint chat_msgGameTextPaint;
    public static TextPaint chat_msgTextPaintOneEmoji;
    public static TextPaint chat_msgTextPaintTwoEmoji;
    public static TextPaint chat_msgTextPaintThreeEmoji;
    public static TextPaint chat_infoPaint;
    public static TextPaint chat_livePaint;
    public static TextPaint chat_docNamePaint;
    public static TextPaint chat_locationTitlePaint;
    public static TextPaint chat_locationAddressPaint;
    public static TextPaint chat_durationPaint;
    public static TextPaint chat_gamePaint;
    public static TextPaint chat_shipmentPaint;
    public static TextPaint chat_instantViewPaint;
    public static TextPaint chat_audioTimePaint;
    public static TextPaint chat_audioTitlePaint;
    public static TextPaint chat_audioPerformerPaint;
    public static TextPaint chat_botButtonPaint;
    public static TextPaint chat_contactNamePaint;
    public static TextPaint chat_contactPhonePaint;
    public static TextPaint chat_timePaint;
    public static TextPaint chat_adminPaint;
    public static TextPaint chat_namePaint;
    public static TextPaint chat_forwardNamePaint;
    public static TextPaint chat_replyNamePaint;
    public static TextPaint chat_replyTextPaint;
    public static TextPaint chat_contextResult_titleTextPaint;
    public static TextPaint chat_contextResult_descriptionTextPaint;

    public static Drawable chat_msgNoSoundDrawable;
    public static Drawable chat_composeShadowDrawable;
    public static Drawable chat_roundVideoShadow;
    public static MessageDrawable chat_msgInDrawable;
    public static MessageDrawable chat_msgInSelectedDrawable;
    public static MessageDrawable chat_msgOutDrawable;
    public static MessageDrawable chat_msgOutSelectedDrawable;
    public static MessageDrawable chat_msgInMediaDrawable;
    public static MessageDrawable chat_msgInMediaSelectedDrawable;
    public static MessageDrawable chat_msgOutMediaDrawable;
    public static MessageDrawable chat_msgOutMediaSelectedDrawable;

    public static Drawable chat_msgOutCheckDrawable;
    public static Drawable chat_msgOutCheckSelectedDrawable;
    public static Drawable chat_msgOutCheckReadDrawable;
    public static Drawable chat_msgOutCheckReadSelectedDrawable;
    public static Drawable chat_msgOutHalfCheckDrawable;
    public static Drawable chat_msgOutHalfCheckSelectedDrawable;
    public static Drawable chat_msgOutClockDrawable;
    public static Drawable chat_msgOutSelectedClockDrawable;
    public static Drawable chat_msgInClockDrawable;
    public static Drawable chat_msgInSelectedClockDrawable;
    public static Drawable chat_msgMediaCheckDrawable;
    public static Drawable chat_msgMediaHalfCheckDrawable;
    public static Drawable chat_msgMediaClockDrawable;
    public static Drawable chat_msgStickerCheckDrawable;
    public static Drawable chat_msgStickerHalfCheckDrawable;
    public static Drawable chat_msgStickerClockDrawable;
    public static Drawable chat_msgStickerViewsDrawable;
    public static Drawable chat_msgInViewsDrawable;
    public static Drawable chat_msgInViewsSelectedDrawable;
    public static Drawable chat_msgOutViewsDrawable;
    public static Drawable chat_msgOutViewsSelectedDrawable;
    public static Drawable chat_msgMediaViewsDrawable;
    public static Drawable chat_msgInMenuDrawable;
    public static Drawable chat_msgInMenuSelectedDrawable;
    public static Drawable chat_msgOutMenuDrawable;
    public static Drawable chat_msgOutMenuSelectedDrawable;
    public static Drawable chat_msgMediaMenuDrawable;
    public static Drawable chat_msgInInstantDrawable;
    public static Drawable chat_msgOutInstantDrawable;
    public static Drawable chat_msgErrorDrawable;
    public static Drawable chat_muteIconDrawable;
    public static Drawable chat_lockIconDrawable;
    public static Drawable chat_inlineResultFile;
    public static Drawable chat_inlineResultAudio;
    public static Drawable chat_inlineResultLocation;
    public static Drawable chat_redLocationIcon;
    public static Drawable chat_msgOutBroadcastDrawable;
    public static Drawable chat_msgMediaBroadcastDrawable;
    public static Drawable chat_msgOutLocationDrawable;
    public static Drawable chat_msgBroadcastDrawable;
    public static Drawable chat_msgBroadcastMediaDrawable;
    public static Drawable chat_contextResult_shadowUnderSwitchDrawable;
    public static Drawable chat_shareDrawable;
    public static Drawable chat_shareIconDrawable;
    public static Drawable chat_replyIconDrawable;
    public static Drawable chat_goIconDrawable;
    public static Drawable chat_botLinkDrawalbe;
    public static Drawable chat_botInlineDrawable;
    public static Drawable chat_systemDrawable;
    public static Drawable chat_msgInCallDrawable;
    public static Drawable chat_msgInCallSelectedDrawable;
    public static Drawable chat_msgOutCallDrawable;
    public static Drawable chat_msgOutCallSelectedDrawable;
    public static Drawable[] chat_pollCheckDrawable = new Drawable[2];
    public static Drawable[] chat_pollCrossDrawable = new Drawable[2];
    public static Drawable[] chat_pollHintDrawable = new Drawable[2];
    public static Drawable[] chat_psaHelpDrawable = new Drawable[2];

    public static Drawable chat_msgCallUpGreenDrawable;
    public static Drawable chat_msgCallDownRedDrawable;
    public static Drawable chat_msgCallDownGreenDrawable;

    public static Drawable chat_msgAvatarLiveLocationDrawable;
    public static Drawable chat_attachEmptyDrawable;
    public static RLottieDrawable[] chat_attachButtonDrawables = new RLottieDrawable[6];
    public static Drawable[] chat_locationDrawable = new Drawable[2];
    public static Drawable[] chat_contactDrawable = new Drawable[2];
    public static Drawable[] chat_cornerOuter = new Drawable[4];
    public static Drawable[] chat_cornerInner = new Drawable[4];
    public static Drawable[][] chat_fileStatesDrawable = new Drawable[10][2];
    public static CombinedDrawable[][] chat_fileMiniStatesDrawable = new CombinedDrawable[6][2];
    public static Drawable[][] chat_photoStatesDrawables = new Drawable[13][2];

    public static Drawable calllog_msgCallUpRedDrawable;
    public static Drawable calllog_msgCallUpGreenDrawable;
    public static Drawable calllog_msgCallDownRedDrawable;
    public static Drawable calllog_msgCallDownGreenDrawable;

    public static Path[] chat_filePath = new Path[2];
    public static Drawable chat_flameIcon;
    public static Drawable chat_gifIcon;

    //region 业务：聊天

    /**
     * 聊天的UI资源
     * @param context 上下文
     * @param fontsOnly 是否仅加载字体
     */
    public static void createChatResources(Context context, boolean fontsOnly) {
        synchronized (sync) {
            if (chat_msgTextPaint == null) {
                chat_msgTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                chat_msgGameTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                chat_msgTextPaintOneEmoji = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                chat_msgTextPaintTwoEmoji = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                chat_msgTextPaintThreeEmoji = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                chat_msgBotButtonPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                chat_msgBotButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            }
        }

        if (!fontsOnly && chat_msgInDrawable == null) {
            chat_infoPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_docNamePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_docNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_docBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_deleteProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_botProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_botProgressPaint.setStrokeCap(Paint.Cap.ROUND);
            chat_botProgressPaint.setStyle(Paint.Style.STROKE);
            chat_locationTitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_locationTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_locationAddressPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_urlPaint = new Paint();
            chat_textSearchSelectionPaint = new Paint();
            chat_radialProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_radialProgressPaint.setStrokeCap(Paint.Cap.ROUND);
            chat_radialProgressPaint.setStyle(Paint.Style.STROKE);
            chat_radialProgressPaint.setColor(0x9fffffff);
            chat_radialProgress2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_radialProgress2Paint.setStrokeCap(Paint.Cap.ROUND);
            chat_radialProgress2Paint.setStyle(Paint.Style.STROKE);
            chat_audioTimePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            chat_livePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_livePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_audioTitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_audioTitlePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_audioPerformerPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_botButtonPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_botButtonPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_contactNamePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_contactNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_contactPhonePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_durationPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_gamePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_gamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_shipmentPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_timePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            chat_adminPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            chat_namePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            chat_namePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_forwardNamePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            chat_replyNamePaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            chat_replyNamePaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_replyTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            chat_instantViewPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_instantViewPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_instantViewRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_instantViewRectPaint.setStyle(Paint.Style.STROKE);
            chat_instantViewRectPaint.setStrokeCap(Paint.Cap.ROUND);
            chat_pollTimerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_pollTimerPaint.setStyle(Paint.Style.STROKE);
            chat_pollTimerPaint.setStrokeCap(Paint.Cap.ROUND);
            chat_replyLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_msgErrorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_statusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_statusRecordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_statusRecordPaint.setStyle(Paint.Style.STROKE);
            chat_statusRecordPaint.setStrokeCap(Paint.Cap.ROUND);
            chat_actionTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_actionTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_actionBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_timeBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_contextResult_titleTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_contextResult_titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            chat_contextResult_descriptionTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            chat_composeBackgroundPaint = new Paint();

            Resources resources = context.getResources();

            chat_msgNoSoundDrawable = resources.getDrawable(R.drawable.video_muted);

            chat_msgInDrawable = new MessageDrawable(MessageDrawable.TYPE_TEXT, false, false);
            chat_msgInSelectedDrawable = new MessageDrawable(MessageDrawable.TYPE_TEXT, false, true);
            chat_msgOutDrawable = new MessageDrawable(MessageDrawable.TYPE_TEXT, true, false);
            chat_msgOutSelectedDrawable = new MessageDrawable(MessageDrawable.TYPE_TEXT, true, true);
            chat_msgInMediaDrawable = new MessageDrawable(MessageDrawable.TYPE_MEDIA, false, false);
            chat_msgInMediaSelectedDrawable = new MessageDrawable(MessageDrawable.TYPE_MEDIA, false, true);
            chat_msgOutMediaDrawable = new MessageDrawable(MessageDrawable.TYPE_MEDIA, true, false);
            chat_msgOutMediaSelectedDrawable = new MessageDrawable(MessageDrawable.TYPE_MEDIA, true, true);

            chat_msgOutCheckDrawable = resources.getDrawable(R.drawable.msg_check).mutate();
            chat_msgOutCheckSelectedDrawable = resources.getDrawable(R.drawable.msg_check).mutate();
            chat_msgOutCheckReadDrawable = resources.getDrawable(R.drawable.msg_check).mutate();
            chat_msgOutCheckReadSelectedDrawable = resources.getDrawable(R.drawable.msg_check).mutate();
            chat_msgMediaCheckDrawable = resources.getDrawable(R.drawable.msg_check_s).mutate();
            chat_msgStickerCheckDrawable = resources.getDrawable(R.drawable.msg_check_s).mutate();
            chat_msgOutHalfCheckDrawable = resources.getDrawable(R.drawable.msg_halfcheck).mutate();
            chat_msgOutHalfCheckSelectedDrawable = resources.getDrawable(R.drawable.msg_halfcheck).mutate();
            chat_msgMediaHalfCheckDrawable = resources.getDrawable(R.drawable.msg_halfcheck_s).mutate();
            chat_msgStickerHalfCheckDrawable = resources.getDrawable(R.drawable.msg_halfcheck_s).mutate();
            chat_msgOutClockDrawable = resources.getDrawable(R.drawable.msg_clock).mutate();
            chat_msgOutSelectedClockDrawable = resources.getDrawable(R.drawable.msg_clock).mutate();
            chat_msgInClockDrawable = resources.getDrawable(R.drawable.msg_clock).mutate();
            chat_msgInSelectedClockDrawable = resources.getDrawable(R.drawable.msg_clock).mutate();
            chat_msgMediaClockDrawable = resources.getDrawable(R.drawable.msg_clock).mutate();
            chat_msgStickerClockDrawable = resources.getDrawable(R.drawable.msg_clock).mutate();
            chat_msgInViewsDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgInViewsSelectedDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgOutViewsDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgOutViewsSelectedDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgMediaViewsDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgStickerViewsDrawable = resources.getDrawable(R.drawable.msg_views).mutate();
            chat_msgInMenuDrawable = resources.getDrawable(R.drawable.msg_actions).mutate();
            chat_msgInMenuSelectedDrawable = resources.getDrawable(R.drawable.msg_actions).mutate();
            chat_msgOutMenuDrawable = resources.getDrawable(R.drawable.msg_actions).mutate();
            chat_msgOutMenuSelectedDrawable = resources.getDrawable(R.drawable.msg_actions).mutate();
            chat_msgMediaMenuDrawable = resources.getDrawable(R.drawable.video_actions);
            chat_msgInInstantDrawable = resources.getDrawable(R.drawable.msg_instant).mutate();
            chat_msgOutInstantDrawable = resources.getDrawable(R.drawable.msg_instant).mutate();
            chat_msgErrorDrawable = resources.getDrawable(R.drawable.msg_warning);
            chat_muteIconDrawable = resources.getDrawable(R.drawable.list_mute).mutate();
            chat_lockIconDrawable = resources.getDrawable(R.drawable.ic_lock_header);
            chat_msgBroadcastDrawable = resources.getDrawable(R.drawable.broadcast3).mutate();
            chat_msgBroadcastMediaDrawable = resources.getDrawable(R.drawable.broadcast3).mutate();
            chat_msgInCallDrawable = resources.getDrawable(R.drawable.ic_call).mutate();
            chat_msgInCallSelectedDrawable = resources.getDrawable(R.drawable.ic_call).mutate();
            chat_msgOutCallDrawable = resources.getDrawable(R.drawable.ic_call).mutate();
            chat_msgOutCallSelectedDrawable = resources.getDrawable(R.drawable.ic_call).mutate();
            chat_msgCallUpGreenDrawable = resources.getDrawable(R.drawable.ic_call_made_green_18dp).mutate();
            chat_msgCallDownRedDrawable = resources.getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
            chat_msgCallDownGreenDrawable = resources.getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
            for (int a = 0; a < 2; a++) {
                chat_pollCheckDrawable[a] = resources.getDrawable(R.drawable.poll_right).mutate();
                chat_pollCrossDrawable[a] = resources.getDrawable(R.drawable.poll_wrong).mutate();
                chat_pollHintDrawable[a] = resources.getDrawable(R.drawable.smiles_panel_objects).mutate();
                chat_psaHelpDrawable[a] = resources.getDrawable(R.drawable.msg_psa).mutate();
            }

            calllog_msgCallUpRedDrawable = resources.getDrawable(R.drawable.ic_call_made_green_18dp).mutate();
            calllog_msgCallUpGreenDrawable = resources.getDrawable(R.drawable.ic_call_made_green_18dp).mutate();
            calllog_msgCallDownRedDrawable = resources.getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
            calllog_msgCallDownGreenDrawable = resources.getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
            chat_msgAvatarLiveLocationDrawable = resources.getDrawable(R.drawable.livepin).mutate();

            chat_inlineResultFile = resources.getDrawable(R.drawable.bot_file);
            chat_inlineResultAudio = resources.getDrawable(R.drawable.bot_music);
            chat_inlineResultLocation = resources.getDrawable(R.drawable.bot_location);
            chat_redLocationIcon = resources.getDrawable(R.drawable.map_pin).mutate();

            chat_botLinkDrawalbe = resources.getDrawable(R.drawable.bot_link);
            chat_botInlineDrawable = resources.getDrawable(R.drawable.bot_lines);

            chat_systemDrawable = resources.getDrawable(R.drawable.system);

            chat_contextResult_shadowUnderSwitchDrawable = resources.getDrawable(R.drawable.header_shadow).mutate();

            chat_attachButtonDrawables[0] = new RLottieDrawable(R.raw.attach_gallery, "attach_gallery", AndroidUtilities.dp(26), AndroidUtilities.dp(26));
            chat_attachButtonDrawables[1] = new RLottieDrawable(R.raw.attach_music, "attach_music", AndroidUtilities.dp(26), AndroidUtilities.dp(26));
            chat_attachButtonDrawables[2] = new RLottieDrawable(R.raw.attach_file, "attach_file", AndroidUtilities.dp(26), AndroidUtilities.dp(26));
            chat_attachButtonDrawables[3] = new RLottieDrawable(R.raw.attach_contact, "attach_contact", AndroidUtilities.dp(26), AndroidUtilities.dp(26));
            chat_attachButtonDrawables[4] = new RLottieDrawable(R.raw.attach_location, "attach_location", AndroidUtilities.dp(26), AndroidUtilities.dp(26));
            chat_attachButtonDrawables[5] = new RLottieDrawable(R.raw.attach_poll, "attach_poll", AndroidUtilities.dp(26), AndroidUtilities.dp(26));
            chat_attachEmptyDrawable = resources.getDrawable(R.drawable.nophotos3);

            chat_cornerOuter[0] = resources.getDrawable(R.drawable.corner_out_tl);
            chat_cornerOuter[1] = resources.getDrawable(R.drawable.corner_out_tr);
            chat_cornerOuter[2] = resources.getDrawable(R.drawable.corner_out_br);
            chat_cornerOuter[3] = resources.getDrawable(R.drawable.corner_out_bl);

            chat_cornerInner[0] = resources.getDrawable(R.drawable.corner_in_tr);
            chat_cornerInner[1] = resources.getDrawable(R.drawable.corner_in_tl);
            chat_cornerInner[2] = resources.getDrawable(R.drawable.corner_in_br);
            chat_cornerInner[3] = resources.getDrawable(R.drawable.corner_in_bl);

            chat_shareDrawable = resources.getDrawable(R.drawable.share_round);
            chat_shareIconDrawable = resources.getDrawable(R.drawable.share_arrow);
            chat_replyIconDrawable = resources.getDrawable(R.drawable.fast_reply);
            chat_goIconDrawable = resources.getDrawable(R.drawable.message_arrow);

            chat_fileMiniStatesDrawable[0][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.audio_mini_arrow);
            chat_fileMiniStatesDrawable[0][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.audio_mini_arrow);
            chat_fileMiniStatesDrawable[1][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.audio_mini_cancel);
            chat_fileMiniStatesDrawable[1][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.audio_mini_cancel);
            chat_fileMiniStatesDrawable[2][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.audio_mini_arrow);
            chat_fileMiniStatesDrawable[2][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.audio_mini_arrow);
            chat_fileMiniStatesDrawable[3][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.audio_mini_cancel);
            chat_fileMiniStatesDrawable[3][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.audio_mini_cancel);
            chat_fileMiniStatesDrawable[4][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.video_mini_arrow);
            chat_fileMiniStatesDrawable[4][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.video_mini_arrow);
            chat_fileMiniStatesDrawable[5][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.video_mini_cancel);
            chat_fileMiniStatesDrawable[5][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(22), R.drawable.video_mini_cancel);

            int rad = AndroidUtilities.dp(2);
            RectF rect = new RectF();
            chat_filePath[0] = new Path();
            chat_filePath[0].moveTo(AndroidUtilities.dp(7), AndroidUtilities.dp(3));
            chat_filePath[0].lineTo(AndroidUtilities.dp(14), AndroidUtilities.dp(3));
            chat_filePath[0].lineTo(AndroidUtilities.dp(21), AndroidUtilities.dp(10));
            chat_filePath[0].lineTo(AndroidUtilities.dp(21), AndroidUtilities.dp(20));
            rect.set(AndroidUtilities.dp(21) - rad * 2, AndroidUtilities.dp(19) - rad, AndroidUtilities.dp(21), AndroidUtilities.dp(19) + rad);
            chat_filePath[0].arcTo(rect, 0, 90, false);
            chat_filePath[0].lineTo(AndroidUtilities.dp(6), AndroidUtilities.dp(21));
            rect.set(AndroidUtilities.dp(5), AndroidUtilities.dp(19) - rad, AndroidUtilities.dp(5) + rad * 2, AndroidUtilities.dp(19) + rad);
            chat_filePath[0].arcTo(rect, 90, 90, false);
            chat_filePath[0].lineTo(AndroidUtilities.dp(5), AndroidUtilities.dp(4));
            rect.set(AndroidUtilities.dp(5), AndroidUtilities.dp(3), AndroidUtilities.dp(5) + rad * 2, AndroidUtilities.dp(3) + rad * 2);
            chat_filePath[0].arcTo(rect, 180, 90, false);
            chat_filePath[0].close();

            chat_filePath[1] = new Path();
            chat_filePath[1].moveTo(AndroidUtilities.dp(14), AndroidUtilities.dp(5));
            chat_filePath[1].lineTo(AndroidUtilities.dp(19), AndroidUtilities.dp(10));
            chat_filePath[1].lineTo(AndroidUtilities.dp(14), AndroidUtilities.dp(10));
            chat_filePath[1].close();

            chat_flameIcon = resources.getDrawable(R.drawable.burn).mutate();
            chat_gifIcon = resources.getDrawable(R.drawable.msg_round_gif_m).mutate();

            chat_fileStatesDrawable[0][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_play_m);
            chat_fileStatesDrawable[0][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_play_m);
            chat_fileStatesDrawable[1][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[1][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[2][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_load_m);
            chat_fileStatesDrawable[2][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_load_m);
            chat_fileStatesDrawable[3][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_file_s);
            chat_fileStatesDrawable[3][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_file_s);
            chat_fileStatesDrawable[4][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_cancel_m);
            chat_fileStatesDrawable[4][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_cancel_m);
            chat_fileStatesDrawable[5][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_play_m);
            chat_fileStatesDrawable[5][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_play_m);
            chat_fileStatesDrawable[6][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[6][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_pause_m);
            chat_fileStatesDrawable[7][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_load_m);
            chat_fileStatesDrawable[7][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_load_m);
            chat_fileStatesDrawable[8][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_file_s);
            chat_fileStatesDrawable[8][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_file_s);
            chat_fileStatesDrawable[9][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_cancel_m);
            chat_fileStatesDrawable[9][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_round_cancel_m);

            chat_photoStatesDrawables[0][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[0][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[1][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[1][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[2][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_gif_m);
            chat_photoStatesDrawables[2][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_gif_m);
            chat_photoStatesDrawables[3][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_play_m);
            chat_photoStatesDrawables[3][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_play_m);

            chat_photoStatesDrawables[4][0] = chat_photoStatesDrawables[4][1] = resources.getDrawable(R.drawable.burn);
            chat_photoStatesDrawables[5][0] = chat_photoStatesDrawables[5][1] = resources.getDrawable(R.drawable.circle);
            chat_photoStatesDrawables[6][0] = chat_photoStatesDrawables[6][1] = resources.getDrawable(R.drawable.photocheck);

            chat_photoStatesDrawables[7][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[7][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[8][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[8][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[9][0] = resources.getDrawable(R.drawable.doc_big).mutate();
            chat_photoStatesDrawables[9][1] = resources.getDrawable(R.drawable.doc_big).mutate();
            chat_photoStatesDrawables[10][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[10][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_load_m);
            chat_photoStatesDrawables[11][0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[11][1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(48), R.drawable.msg_round_cancel_m);
            chat_photoStatesDrawables[12][0] = resources.getDrawable(R.drawable.doc_big).mutate();
            chat_photoStatesDrawables[12][1] = resources.getDrawable(R.drawable.doc_big).mutate();

            chat_contactDrawable[0] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_contact);
            chat_contactDrawable[1] = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(44), R.drawable.msg_contact);

            chat_locationDrawable[0] = resources.getDrawable(R.drawable.msg_location).mutate();
            chat_locationDrawable[1] = resources.getDrawable(R.drawable.msg_location).mutate();

            chat_composeShadowDrawable = context.getResources().getDrawable(R.drawable.compose_panel_shadow);

            try {
                int bitmapSize = AndroidUtilities.roundMessageSize + AndroidUtilities.dp(6);
                Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint eraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                eraserPaint.setColor(0);
                eraserPaint.setStyle(Paint.Style.FILL);
                eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setShadowLayer(AndroidUtilities.dp(4), 0, 0, 0x5f000000);
                for (int a = 0; a < 2; a++) {
                    canvas.drawCircle(bitmapSize / 2, bitmapSize / 2, AndroidUtilities.roundMessageSize / 2 - AndroidUtilities.dp(1), a == 0 ? paint : eraserPaint);
                }
                try {
                    canvas.setBitmap(null);
                } catch (Exception ignore) {

                }
                chat_roundVideoShadow = new BitmapDrawable(bitmap);
            } catch (Throwable ignore) {

            }

            applyChatTheme(fontsOnly);
        }

        chat_msgTextPaintOneEmoji.setTextSize(AndroidUtilities.dp(28));
        chat_msgTextPaintTwoEmoji.setTextSize(AndroidUtilities.dp(24));
        chat_msgTextPaintThreeEmoji.setTextSize(AndroidUtilities.dp(20));
        chat_msgTextPaint.setTextSize(AndroidUtilities.dp(SharedConfig.fontSize));
        chat_msgGameTextPaint.setTextSize(AndroidUtilities.dp(14));
        chat_msgBotButtonPaint.setTextSize(AndroidUtilities.dp(15));

        if (!fontsOnly && chat_botProgressPaint != null) {
            chat_botProgressPaint.setStrokeWidth(AndroidUtilities.dp(2));
            chat_infoPaint.setTextSize(AndroidUtilities.dp(12));
            chat_docNamePaint.setTextSize(AndroidUtilities.dp(15));
            chat_locationTitlePaint.setTextSize(AndroidUtilities.dp(15));
            chat_locationAddressPaint.setTextSize(AndroidUtilities.dp(13));
            chat_audioTimePaint.setTextSize(AndroidUtilities.dp(12));
            chat_livePaint.setTextSize(AndroidUtilities.dp(12));
            chat_audioTitlePaint.setTextSize(AndroidUtilities.dp(16));
            chat_audioPerformerPaint.setTextSize(AndroidUtilities.dp(15));
            chat_botButtonPaint.setTextSize(AndroidUtilities.dp(15));
            chat_contactNamePaint.setTextSize(AndroidUtilities.dp(15));
            chat_contactPhonePaint.setTextSize(AndroidUtilities.dp(13));
            chat_durationPaint.setTextSize(AndroidUtilities.dp(12));
            chat_timePaint.setTextSize(AndroidUtilities.dp(12));
            chat_adminPaint.setTextSize(AndroidUtilities.dp(13));
            chat_namePaint.setTextSize(AndroidUtilities.dp(14));
            chat_forwardNamePaint.setTextSize(AndroidUtilities.dp(14));
            chat_replyNamePaint.setTextSize(AndroidUtilities.dp(14));
            chat_replyTextPaint.setTextSize(AndroidUtilities.dp(14));
            chat_gamePaint.setTextSize(AndroidUtilities.dp(13));
            chat_shipmentPaint.setTextSize(AndroidUtilities.dp(13));
            chat_instantViewPaint.setTextSize(AndroidUtilities.dp(13));
            chat_instantViewRectPaint.setStrokeWidth(AndroidUtilities.dp(1));
            chat_pollTimerPaint.setStrokeWidth(AndroidUtilities.dp(1.1f));
            chat_statusRecordPaint.setStrokeWidth(AndroidUtilities.dp(2));
            chat_actionTextPaint.setTextSize(AndroidUtilities.dp(Math.max(16, SharedConfig.fontSize) - 2));
            chat_contextResult_titleTextPaint.setTextSize(AndroidUtilities.dp(15));
            chat_contextResult_descriptionTextPaint.setTextSize(AndroidUtilities.dp(13));
            chat_radialProgressPaint.setStrokeWidth(AndroidUtilities.dp(3));
            chat_radialProgress2Paint.setStrokeWidth(AndroidUtilities.dp(2));
        }
    }

    public static void refreshAttachButtonsColors() {
        for (int a = 0; a < chat_attachButtonDrawables.length; a++) {
            if (chat_attachButtonDrawables[a] == null) {
                continue;
            }
            chat_attachButtonDrawables[a].beginApplyLayerColors();
            if (a == 0) {
                chat_attachButtonDrawables[a].setLayerColor("Color_Mount.**", Theme.getNonAnimatedColor(Theme.key_chat_attachGalleryBackground));
                chat_attachButtonDrawables[a].setLayerColor("Color_PhotoShadow.**", Theme.getNonAnimatedColor(Theme.key_chat_attachGalleryBackground));
                chat_attachButtonDrawables[a].setLayerColor("White_Photo.**", Theme.getNonAnimatedColor(Theme.key_chat_attachGalleryIcon));
                chat_attachButtonDrawables[a].setLayerColor("White_BackPhoto.**", Theme.getNonAnimatedColor(Theme.key_chat_attachGalleryIcon));
            } else if (a == 1) {
                chat_attachButtonDrawables[a].setLayerColor("White_Play1.**", Theme.getNonAnimatedColor(Theme.key_chat_attachAudioIcon));
                chat_attachButtonDrawables[a].setLayerColor("White_Play2.**", Theme.getNonAnimatedColor(Theme.key_chat_attachAudioIcon));
            } else if (a == 2) {
                chat_attachButtonDrawables[a].setLayerColor("Color_Corner.**", Theme.getNonAnimatedColor(Theme.key_chat_attachFileBackground));
                chat_attachButtonDrawables[a].setLayerColor("White_List.**", Theme.getNonAnimatedColor(Theme.key_chat_attachFileIcon));
            } else if (a == 3) {
                chat_attachButtonDrawables[a].setLayerColor("White_User1.**", Theme.getNonAnimatedColor(Theme.key_chat_attachContactIcon));
                chat_attachButtonDrawables[a].setLayerColor("White_User2.**", Theme.getNonAnimatedColor(Theme.key_chat_attachContactIcon));
            } else if (a == 4) {
                chat_attachButtonDrawables[a].setLayerColor("Color_Oval.**", Theme.getNonAnimatedColor(Theme.key_chat_attachLocationBackground));
                chat_attachButtonDrawables[a].setLayerColor("White_Pin.**", Theme.getNonAnimatedColor(Theme.key_chat_attachLocationIcon));
            } else if (a == 5) {
                chat_attachButtonDrawables[a].setLayerColor("White_Column 1.**", Theme.getNonAnimatedColor(Theme.key_chat_attachPollIcon));
                chat_attachButtonDrawables[a].setLayerColor("White_Column 2.**", Theme.getNonAnimatedColor(Theme.key_chat_attachPollIcon));
                chat_attachButtonDrawables[a].setLayerColor("White_Column 3.**", Theme.getNonAnimatedColor(Theme.key_chat_attachPollIcon));
            }
            chat_attachButtonDrawables[a].commitApplyLayerColors();
        }

    }

    public static void applyChatTheme(boolean fontsOnly) {
        if (chat_msgTextPaint == null) {
            return;
        }

        if (chat_msgInDrawable != null && !fontsOnly) {
            chat_gamePaint.setColor(Theme.getColor(Theme.key_chat_previewGameText));
            chat_durationPaint.setColor(Theme.getColor(Theme.key_chat_previewDurationText));
            chat_botButtonPaint.setColor(Theme.getColor(Theme.key_chat_botButtonText));
            chat_urlPaint.setColor(Theme.getColor(Theme.key_chat_linkSelectBackground));
            chat_botProgressPaint.setColor(Theme.getColor(Theme.key_chat_botProgress));
            chat_deleteProgressPaint.setColor(Theme.getColor(Theme.key_chat_secretTimeText));
            chat_textSearchSelectionPaint.setColor(Theme.getColor(Theme.key_chat_textSelectBackground));
            chat_msgErrorPaint.setColor(Theme.getColor(Theme.key_chat_sentError));
            chat_statusPaint.setColor(Theme.getColor(Theme.key_chat_status));
            chat_statusRecordPaint.setColor(Theme.getColor(Theme.key_chat_status));
            chat_actionTextPaint.setColor(Theme.getColor(Theme.key_chat_serviceText));
            chat_actionTextPaint.linkColor = Theme.getColor(Theme.key_chat_serviceLink);
            chat_contextResult_titleTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            chat_composeBackgroundPaint.setColor(Theme.getColor(Theme.key_chat_messagePanelBackground));
            chat_timeBackgroundPaint.setColor(Theme.getColor(Theme.key_chat_mediaTimeBackground));

            Theme.setDrawableColorByKey(chat_msgNoSoundDrawable, Theme.key_chat_mediaTimeText);
            Theme.setDrawableColorByKey(chat_msgInDrawable, Theme.key_chat_inBubble);
            Theme.setDrawableColorByKey(chat_msgInSelectedDrawable, Theme.key_chat_inBubbleSelected);
            Theme.setDrawableColorByKey(chat_msgInMediaDrawable, Theme.key_chat_inBubble);
            Theme.setDrawableColorByKey(chat_msgInMediaSelectedDrawable, Theme.key_chat_inBubbleSelected);
            Theme.setDrawableColorByKey(chat_msgOutCheckDrawable, Theme.key_chat_outSentCheck);
            Theme.setDrawableColorByKey(chat_msgOutCheckSelectedDrawable, Theme.key_chat_outSentCheckSelected);
            Theme.setDrawableColorByKey(chat_msgOutCheckReadDrawable, Theme.key_chat_outSentCheckRead);
            Theme.setDrawableColorByKey(chat_msgOutCheckReadSelectedDrawable, Theme.key_chat_outSentCheckReadSelected);
            Theme.setDrawableColorByKey(chat_msgOutHalfCheckDrawable, Theme.key_chat_outSentCheckRead);
            Theme.setDrawableColorByKey(chat_msgOutHalfCheckSelectedDrawable, Theme.key_chat_outSentCheckReadSelected);
            Theme.setDrawableColorByKey(chat_msgOutClockDrawable, Theme.key_chat_outSentClock);
            Theme.setDrawableColorByKey(chat_msgOutSelectedClockDrawable, Theme.key_chat_outSentClockSelected);
            Theme.setDrawableColorByKey(chat_msgInClockDrawable, Theme.key_chat_inSentClock);
            Theme.setDrawableColorByKey(chat_msgInSelectedClockDrawable, Theme.key_chat_inSentClockSelected);
            Theme.setDrawableColorByKey(chat_msgMediaCheckDrawable, Theme.key_chat_mediaSentCheck);
            Theme.setDrawableColorByKey(chat_msgMediaHalfCheckDrawable, Theme.key_chat_mediaSentCheck);
            Theme.setDrawableColorByKey(chat_msgMediaClockDrawable, Theme.key_chat_mediaSentClock);
            Theme.setDrawableColorByKey(chat_msgStickerCheckDrawable, Theme.key_chat_serviceText);
            Theme.setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, Theme.key_chat_serviceText);
            Theme.setDrawableColorByKey(chat_msgStickerClockDrawable, Theme.key_chat_serviceText);
            Theme.setDrawableColorByKey(chat_msgStickerViewsDrawable, Theme.key_chat_serviceText);
            Theme.setDrawableColorByKey(chat_shareIconDrawable, Theme.key_chat_serviceIcon);
            Theme.setDrawableColorByKey(chat_replyIconDrawable, Theme.key_chat_serviceIcon);
            Theme.setDrawableColorByKey(chat_goIconDrawable, Theme.key_chat_serviceIcon);
            Theme.setDrawableColorByKey(chat_botInlineDrawable, Theme.key_chat_serviceIcon);
            Theme.setDrawableColorByKey(chat_botLinkDrawalbe, Theme.key_chat_serviceIcon);
            Theme.setDrawableColorByKey(chat_msgInViewsDrawable, Theme.key_chat_inViews);
            Theme.setDrawableColorByKey(chat_msgInViewsSelectedDrawable, Theme.key_chat_inViewsSelected);
            Theme.setDrawableColorByKey(chat_msgOutViewsDrawable, Theme.key_chat_outViews);
            Theme.setDrawableColorByKey(chat_msgOutViewsSelectedDrawable, Theme.key_chat_outViewsSelected);
            Theme.setDrawableColorByKey(chat_msgMediaViewsDrawable, Theme.key_chat_mediaViews);
            Theme.setDrawableColorByKey(chat_msgInMenuDrawable, Theme.key_chat_inMenu);
            Theme.setDrawableColorByKey(chat_msgInMenuSelectedDrawable, Theme.key_chat_inMenuSelected);
            Theme.setDrawableColorByKey(chat_msgOutMenuDrawable, Theme.key_chat_outMenu);
            Theme.setDrawableColorByKey(chat_msgOutMenuSelectedDrawable, Theme.key_chat_outMenuSelected);
            Theme.setDrawableColorByKey(chat_msgMediaMenuDrawable, Theme.key_chat_mediaMenu);
            Theme.setDrawableColorByKey(chat_msgOutInstantDrawable, Theme.key_chat_outInstant);
            Theme.setDrawableColorByKey(chat_msgInInstantDrawable, Theme.key_chat_inInstant);
            Theme.setDrawableColorByKey(chat_msgErrorDrawable, Theme.key_chat_sentErrorIcon);
            Theme.setDrawableColorByKey(chat_muteIconDrawable, Theme.key_chat_muteIcon);
            Theme.setDrawableColorByKey(chat_lockIconDrawable, Theme.key_chat_lockIcon);
            Theme.setDrawableColorByKey(chat_msgBroadcastDrawable, Theme.key_chat_outBroadcast);
            Theme.setDrawableColorByKey(chat_msgBroadcastMediaDrawable, Theme.key_chat_mediaBroadcast);
            Theme.setDrawableColorByKey(chat_inlineResultFile, Theme.key_chat_inlineResultIcon);
            Theme.setDrawableColorByKey(chat_inlineResultAudio, Theme.key_chat_inlineResultIcon);
            Theme.setDrawableColorByKey(chat_inlineResultLocation, Theme.key_chat_inlineResultIcon);
            Theme.setDrawableColorByKey(chat_msgInCallDrawable, Theme.key_chat_inInstant);
            Theme.setDrawableColorByKey(chat_msgInCallSelectedDrawable, Theme.key_chat_inInstantSelected);
            Theme.setDrawableColorByKey(chat_msgOutCallDrawable, Theme.key_chat_outInstant);
            Theme.setDrawableColorByKey(chat_msgOutCallSelectedDrawable, Theme.key_chat_outInstantSelected);

            Theme.setDrawableColorByKey(chat_msgCallUpGreenDrawable, Theme.key_chat_outGreenCall);
            Theme.setDrawableColorByKey(chat_msgCallDownRedDrawable, Theme.key_chat_inRedCall);
            Theme.setDrawableColorByKey(chat_msgCallDownGreenDrawable, Theme.key_chat_inGreenCall);

            Theme.setDrawableColorByKey(calllog_msgCallUpRedDrawable, Theme.key_calls_callReceivedRedIcon);
            Theme.setDrawableColorByKey(calllog_msgCallUpGreenDrawable, Theme.key_calls_callReceivedGreenIcon);
            Theme.setDrawableColorByKey(calllog_msgCallDownRedDrawable, Theme.key_calls_callReceivedRedIcon);
            Theme.setDrawableColorByKey(calllog_msgCallDownGreenDrawable, Theme.key_calls_callReceivedGreenIcon);

            for (int a = 0; a < 2; a++) {
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a][0], Theme.getColor(Theme.key_chat_outLoader), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a][0], Theme.getColor(Theme.key_chat_outMediaIcon), true);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a][1], Theme.getColor(Theme.key_chat_outLoaderSelected), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[a][1], Theme.getColor(Theme.key_chat_outMediaIconSelected), true);

                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[2 + a][0], Theme.getColor(Theme.key_chat_inLoader), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[2 + a][0], Theme.getColor(Theme.key_chat_inMediaIcon), true);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[2 + a][1], Theme.getColor(Theme.key_chat_inLoaderSelected), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[2 + a][1], Theme.getColor(Theme.key_chat_inMediaIconSelected), true);

                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[4 + a][0], Theme.getColor(Theme.key_chat_mediaLoaderPhoto), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[4 + a][0], Theme.getColor(Theme.key_chat_mediaLoaderPhotoIcon), true);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[4 + a][1], Theme.getColor(Theme.key_chat_mediaLoaderPhotoSelected), false);
                Theme.setCombinedDrawableColor(chat_fileMiniStatesDrawable[4 + a][1], Theme.getColor(Theme.key_chat_mediaLoaderPhotoIconSelected), true);
            }

            for (int a = 0; a < 5; a++) {
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a][0], Theme.getColor(Theme.key_chat_outLoader), false);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a][0], Theme.getColor(Theme.key_chat_outMediaIcon), true);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a][1], Theme.getColor(Theme.key_chat_outLoaderSelected), false);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[a][1], Theme.getColor(Theme.key_chat_outMediaIconSelected), true);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[5 + a][0], Theme.getColor(Theme.key_chat_inLoader), false);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[5 + a][0], Theme.getColor(Theme.key_chat_inMediaIcon), true);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[5 + a][1], Theme.getColor(Theme.key_chat_inLoaderSelected), false);
                Theme.setCombinedDrawableColor(chat_fileStatesDrawable[5 + a][1], Theme.getColor(Theme.key_chat_inMediaIconSelected), true);
            }
            for (int a = 0; a < 4; a++) {
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a][0], Theme.getColor(Theme.key_chat_mediaLoaderPhoto), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a][0], Theme.getColor(Theme.key_chat_mediaLoaderPhotoIcon), true);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a][1], Theme.getColor(Theme.key_chat_mediaLoaderPhotoSelected), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[a][1], Theme.getColor(Theme.key_chat_mediaLoaderPhotoIconSelected), true);
            }
            for (int a = 0; a < 2; a++) {
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[7 + a][0], Theme.getColor(Theme.key_chat_outLoaderPhoto), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[7 + a][0], Theme.getColor(Theme.key_chat_outLoaderPhotoIcon), true);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[7 + a][1], Theme.getColor(Theme.key_chat_outLoaderPhotoSelected), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[7 + a][1], Theme.getColor(Theme.key_chat_outLoaderPhotoIconSelected), true);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[10 + a][0], Theme.getColor(Theme.key_chat_inLoaderPhoto), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[10 + a][0], Theme.getColor(Theme.key_chat_inLoaderPhotoIcon), true);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[10 + a][1], Theme.getColor(Theme.key_chat_inLoaderPhotoSelected), false);
                Theme.setCombinedDrawableColor(chat_photoStatesDrawables[10 + a][1], Theme.getColor(Theme.key_chat_inLoaderPhotoIconSelected), true);
            }

            Theme.setDrawableColorByKey(chat_photoStatesDrawables[9][0], Theme.key_chat_outFileIcon);
            Theme.setDrawableColorByKey(chat_photoStatesDrawables[9][1], Theme.key_chat_outFileSelectedIcon);
            Theme.setDrawableColorByKey(chat_photoStatesDrawables[12][0], Theme.key_chat_inFileIcon);
            Theme.setDrawableColorByKey(chat_photoStatesDrawables[12][1], Theme.key_chat_inFileSelectedIcon);

            Theme.setCombinedDrawableColor(chat_contactDrawable[0], Theme.getColor(Theme.key_chat_inContactBackground), false);
            Theme.setCombinedDrawableColor(chat_contactDrawable[0], Theme.getColor(Theme.key_chat_inContactIcon), true);
            Theme.setCombinedDrawableColor(chat_contactDrawable[1], Theme.getColor(Theme.key_chat_outContactBackground), false);
            Theme.setCombinedDrawableColor(chat_contactDrawable[1], Theme.getColor(Theme.key_chat_outContactIcon), true);

            Theme.setDrawableColor(chat_locationDrawable[0], Theme.getColor(Theme.key_chat_inLocationIcon));
            Theme.setDrawableColor(chat_locationDrawable[1], Theme.getColor(Theme.key_chat_outLocationIcon));

            Theme.setDrawableColor(chat_pollHintDrawable[0], Theme.getColor(Theme.key_chat_inPreviewInstantText));
            Theme.setDrawableColor(chat_pollHintDrawable[1], Theme.getColor(Theme.key_chat_outPreviewInstantText));

            Theme.setDrawableColor(chat_psaHelpDrawable[0], Theme.getColor(Theme.key_chat_inViews));
            Theme.setDrawableColor(chat_psaHelpDrawable[1], Theme.getColor(Theme.key_chat_outViews));

            Theme.setDrawableColorByKey(chat_composeShadowDrawable, Theme.key_chat_messagePanelShadow);

            int color = Theme.getColor(Theme.key_chat_outAudioSeekbarFill);
            if (color == 0xffffffff) {
                color = Theme.getColor(Theme.key_chat_outBubble);
            } else {
                color = 0xffffffff;
            }
            Theme.setDrawableColor(chat_pollCheckDrawable[1], color);
            Theme.setDrawableColor(chat_pollCrossDrawable[1], color);

            Theme.setDrawableColor(chat_attachEmptyDrawable, Theme.getColor(Theme.key_chat_attachEmptyImage));

            Theme.applyChatServiceMessageColor();
            refreshAttachButtonsColors();
        }
    }

    @Override
    public void destroyResources() {

    }

    @Override
    public void reloadAllResources(Context context) {
        if (chat_actionBackgroundPaint != null) {
            chat_actionBackgroundPaint = null;
            super.reloadAllResources(context);
        }
    }

    @Override
    public void createResources(Context context) {
        createChatResources(context, false);
    }

    //endregion

}
