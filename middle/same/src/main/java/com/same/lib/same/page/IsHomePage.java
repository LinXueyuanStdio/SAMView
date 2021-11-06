package com.same.lib.same.page;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/11/6
 * @description null
 * @usage null
 */
public interface IsHomePage {
    default boolean currentStateIsAtHome() {
        return true;
    }
}
