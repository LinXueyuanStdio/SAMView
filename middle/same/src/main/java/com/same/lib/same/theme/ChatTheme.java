package com.same.lib.same.theme;

import android.content.Context;

import com.same.lib.theme.AbsTheme;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class ChatTheme extends AbsTheme {
    //region 业务：聊天

    /**
     * 聊天的UI资源
     * @param context 上下文
     * @param fontsOnly 是否仅加载字体
     */
    public static void createChatResources(Context context, boolean fontsOnly) {
        if (!fontsOnly) {
            applyChatTheme(fontsOnly);
        }
    }

    public static void applyChatTheme(boolean fontsOnly) {
    }

    @Override
    public void destroyResources() {

    }

    @Override
    public void createResources(Context context) {
        createChatResources(context, false);
    }

    @Override
    public void applyResources(Context context) {
        applyChatTheme(false);
    }

    //endregion

}
