package com.same.lib.same.theme;

import android.content.Context;
import android.graphics.Paint;

import com.same.lib.theme.AbsTheme;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class DialogTheme extends AbsTheme {
    public static Paint dialogs_onlineCirclePaint;
    public static Paint chat_instantViewRectPaint;

    //region 业务：对话

    /**
     * 对话列表的UI资源
     * @param context 上下文
     */
    public static void createDialogsResources(Context context) {
        CommonTheme.createCommonResources(context);
        if (dialogs_onlineCirclePaint == null) {
            dialogs_onlineCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            chat_instantViewRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            applyDialogsTheme();
        }
    }

    public static void applyDialogsTheme() {
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
