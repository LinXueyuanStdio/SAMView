package com.same.ui.page.theme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.File;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class WallpaperUpdater {
    public WallpaperUpdater(Context activity, WallpaperUpdaterDelegate wallpaperUpdaterDelegate) {

    }

    public void cleanup() {

    }

    public void showAlert(boolean b) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public interface WallpaperUpdaterDelegate {
        void didSelectWallpaper(File file, Bitmap bitmap, boolean gallery);
        void needOpenColorPicker();
    }
}
