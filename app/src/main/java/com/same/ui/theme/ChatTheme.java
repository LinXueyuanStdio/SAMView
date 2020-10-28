package com.same.ui.theme;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextPaint;

import com.same.lib.AbsTheme;
import com.same.lib.theme.Theme;
import com.same.lib.util.AndroidUtilities;
import com.same.lib.util.SharedConfig;

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

    public static Path[] chat_filePath = new Path[2];

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

        if (!fontsOnly) {
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
    }

    public static void applyChatTheme(boolean fontsOnly) {
        if (chat_msgTextPaint == null) {
            return;
        }

        if ( !fontsOnly) {
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
