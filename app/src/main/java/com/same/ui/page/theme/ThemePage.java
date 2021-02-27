package com.same.ui.page.theme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.lottie.RLottieImageView;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.theme.ThemeManager;
import com.same.ui.R;
import com.same.ui.lang.MyLang;
import com.same.ui.page.base.BaseActionBarPage;
import com.same.ui.theme.dialog.AlertDialog;
import com.same.ui.theme.span.ThemeName;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import androidx.core.content.FileProvider;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/10/28
 * @description null
 * @usage null
 */
public class ThemePage extends BaseActionBarPage {

    Button currentTheme;

    @Override
    protected String title() {
        return MyLang.getString("Theme", R.string.Theme);
    }

    @Override
    protected void fillInContainerLayout(Context context, LinearLayout containerLayout) {
        containerLayout.addView(createButton(context, "创建新主题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context activity = getContext();
                ThemeEditorView.ThemeContainer container;
                if (activity instanceof ThemeEditorView.ThemeContainer) {
                    container = (ThemeEditorView.ThemeContainer) activity;
                } else return;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(MyLang.getString("NewTheme", R.string.NewTheme));
                builder.setMessage(MyLang.getString("CreateNewThemeAlert", R.string.CreateNewThemeAlert));
                builder.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
                builder.setPositiveButton(MyLang.getString("CreateTheme", R.string.CreateTheme), (dialog, which) -> {
                    if (parentLayout != null) {
                        ThemeInfo themeInfo = ThemeManager.createNewTheme(activity, "新主题的名字");
                        ThemeManager.applyTheme(activity, themeInfo);
                        parentLayout.rebuildAllFragmentViews(true, true);
                        new ThemeEditorView().show(activity, container, themeInfo);
                    }
                });
                AlertDialog dialog =  builder.create();
                dialog.prepareShowInService();
                showDialog(dialog);
            }
        }));

        containerLayout.addView(createButton(context, "重置主题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Activity activity = getParentActivity();
//                if (activity == null) { return; }
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setTitle(MyLang.getString("ThemeResetToDefaultsTitle", R.string.ThemeResetToDefaultsTitle));
                builder1.setMessage(MyLang.getString("ThemeResetToDefaultsText", R.string.ThemeResetToDefaultsText));
                builder1.setPositiveButton(MyLang.getString("Reset", R.string.Reset), (dialogInterface, i) -> {
                    ThemeInfo themeInfo = ThemeManager.getTheme("Blue");
                    ThemeInfo currentTheme = ThemeManager.getCurrentTheme();
                    if (themeInfo != currentTheme) {
                        themeInfo.setCurrentAccentId(Theme.DEFALT_THEME_ACCENT_ID);
                        ThemeManager.saveThemeAccents(context, themeInfo, true, false, true);
                    } else if (themeInfo.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
                        NotificationCenter.post(NotificationCenter.needSetDayNightTheme, currentTheme);
                    }
                });
                builder1.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder1.create();
                alertDialog.prepareShowInService();
                showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (button != null) {
                    button.setTextColor(Theme.getColor(KeyHub.key_dialogTextRed2));
                }
            }
        }));

        containerLayout.addView(createButton(context, "分享主题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    Activity activity = getParentActivity();
                    if (activity != null) {
                        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                            return;
                        }
                    }
                }
                ThemeInfo themeInfo = ThemeManager.getCurrentTheme();
                shareTheme(context, themeInfo);
            }
        }));
        currentTheme = createButton(context, ThemeName.getCurrentThemeName(context), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTheme.setText(ThemeName.getCurrentThemeName(context));
            }
        });
        containerLayout.addView(currentTheme);

        for (ThemeInfo themeInfo : Theme.themes) {
            containerLayout.addView(createButton(context, ThemeName.getName(context, themeInfo), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThemeManager.applyTheme(context, themeInfo);
                    parentLayout.rebuildAllFragmentViews(true, true);
                }
            }));
        }

        RLottieImageView imageView = new RLottieImageView(context);
        imageView.setAnimation(R.raw.filters, 90, 90);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.playAnimation();
        containerLayout.addView(imageView, LayoutHelper.createFrame(90, 90, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 14, 0, 0));
        imageView.setOnClickListener(v -> {
            if (!imageView.isPlaying()) {
                imageView.setProgress(0.0f);
                imageView.playAnimation();
            }
        });
        containerLayout.addView(createButton(context, "check", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageView.isPlaying()) {
                    imageView.setProgress(0.0f);
                    imageView.playAnimation();
                }
            }
        }));

    }

    private void shareTheme(Context context, ThemeInfo themeInfo) {
        File currentFile;
        if (themeInfo.pathToFile == null && themeInfo.assetName == null) {
            StringBuilder result = new StringBuilder();
            for (HashMap.Entry<String, Integer> entry : ThemeManager.getDefaultColors().entrySet()) {
                result.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
            }
            currentFile = new File(AndroidUtilities.getFilesDirFixed(), "default_theme.attheme");
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(currentFile);
                stream.write(AndroidUtilities.getStringBytes(result.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (themeInfo.assetName != null) {
            currentFile = ThemeManager.getAssetFile(context, themeInfo.assetName);
        } else {
            currentFile = new File(themeInfo.pathToFile);
        }
        String name = themeInfo.name;
        if (!name.endsWith(".attheme")) {
            name += ".attheme";
        }
        File finalFile = new File(AndroidUtilities.getFilesDirFixed(), AndroidUtilities.fixFileName(name));
        try {
            if (!AndroidUtilities.copyFile(currentFile, finalFile)) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/xml");
            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "com.same.ui.provider", finalFile));
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(finalFile));
                }
            } else {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(finalFile));
            }
            startActivityForResult(Intent.createChooser(intent, MyLang.getString("ShareFile", R.string.ShareFile)), 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
