package com.same.ui.page.service;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.same.lib.core.ContainerLayout;
import com.same.ui.ContainerCreator;
import com.same.ui.R;
import com.same.ui.lang.MyLang;
import com.timecat.show.window.StandOutFlags;
import com.timecat.show.window.StandOutWindow;
import com.timecat.show.window.Window;
import com.timecat.show.window.WindowAgreement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/1/12
 * @description null
 * @usage null
 */
public class MainService extends StandOutWindow implements ContainerCreator.ContextDelegate {
    ContainerCreator creator;

    @Override
    public String getAppName() {
        return MyLang.getString("app_name", R.string.app_name);
    }

    public int getAppIcon() {
        return R.drawable.ic_window_menu;
    }

    public String getTitle(int id) {
        return MyLang.getString("app_name", R.string.app_name);
    }

    public String getPersistentNotificationTitle(int id) {
        return MyLang.getString("app_name", R.string.app_name);
    }

    public String getPersistentNotificationMessage(int id) {
        return MyLang.getString("app_name", R.string.app_name);
    }

    public int getHiddenIcon() {
        return R.drawable.ic_window_menu;
    }

    public String getHiddenNotificationTitle(int id) {
        return MyLang.getString("app_name", R.string.app_name);
    }

    public String getHiddenNotificationMessage(int id) {
        return MyLang.getString("app_name", R.string.app_name);
    }

    public Intent getHiddenNotificationIntent(int id) {
        return WindowAgreement.getShowIntent(this, getClass(), id);
    }

    public Animation getShowAnimation(int id) {
        if (isExistingId(id)) {
            return AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        }
        return super.getShowAnimation(id);
    }

    public Animation getHideAnimation(int id) {
        return AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
    }

    @Override
    public StandOutLayoutParams getParams(int i, Window window) {
        return new StandOutLayoutParams(i);
    }

    public int getFlags(int id) {
        return (((StandOutFlags.FLAG_DECORATION_SYSTEM | StandOutFlags.FLAG_BODY_MOVE_ENABLE) | StandOutFlags.FLAG_WINDOW_HIDE_ENABLE) | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP) | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE;
    }

    public List<DropDownListItem> getDropDownItems(int id) {
        return new ArrayList<>();
    }

    @Override
    public void onCreate() {
        creator = new ContainerCreator(this, this);
        creator.onPreCreate();
        super.onCreate();
    }

    @Override
    public void createAndAttachView(int i, FrameLayout frameLayout) {
        creator.onCreateView(frameLayout);
    }

    @Override
    public boolean onShow(int id, Window window) {
        creator.onResume();
        return super.onShow(id, window);
    }

    @Override
    public boolean onHide(int id, Window window) {
        creator.onPause();
        return super.onHide(id, window);
    }

    @Override
    public boolean onClose(int id, Window window) {
        creator.onDestroy();
        return super.onClose(id, window);
    }

    @Override
    public void onDestroy() {
        creator.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        creator.onPreConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
        creator.onPostConfigurationChanged(newConfig);
    }

    @Override
    public Configuration getConfiguration() {
        return getResources().getConfiguration();
    }

    @Override
    public void rebuildAllFragments(boolean last) {
        creator.rebuildAllFragments(last);
    }

    @Override
    public ContainerLayout getContainerLayout() {
        return creator.getContainerLayout();
    }

    @Override
    public ContainerLayout getRightActionBarLayout() {
        return creator.getRightActionBarLayout();
    }

    @Override
    public ContainerLayout getLayersActionBarLayout() {
        return creator.getLayersActionBarLayout();
    }
}
