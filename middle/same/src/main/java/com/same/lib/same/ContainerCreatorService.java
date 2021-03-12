package com.same.lib.same;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/3/9
 * @description null
 * @usage null
 */
public interface ContainerCreatorService {
    ContainerCreator create(
            @NonNull Context context,
            @NonNull ContainerCreator.ContextDelegate delegate
    );

    void onCreateView(
            @NonNull FrameLayout frameLayout,
            @NonNull ContainerCreator.ContextDelegate delegate
    );
    //    void onPause();
    //    void onResume();
    //    void onDestroy();
    //    void onPreActivityResult();
    //    void onActivityResult(int requestCode, int resultCode, Intent data);
    //    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    //    void onPreConfigurationChanged(Configuration newConfig);
    //    void onPostConfigurationChanged(Configuration newConfig);
    //    void onBackPressed();
    //    void onLowMemory();
    //    void onActionModeStarted(ActionMode mode);
    //    void onActionModeFinished(ActionMode mode);
    //    void onKeyUp(int keyCode, KeyEvent event);
}
