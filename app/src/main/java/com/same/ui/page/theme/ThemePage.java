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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.base.NotificationCenter;
import com.same.lib.checkbox.CheckBox;
import com.same.lib.checkbox.CheckBox2;
import com.same.lib.checkbox.CheckBoxSquare;
import com.same.lib.core.AlertDialog;
import com.same.lib.core.BasePage;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.lottie.RLottieImageView;
import com.same.lib.radiobutton.RadioButton;
import com.same.lib.span.ThemeName;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.Theme;
import com.same.lib.theme.ThemeInfo;
import com.same.lib.theme.ThemeManager;
import com.same.ui.BuildConfig;
import com.same.ui.R;
import com.same.ui.lang.MyLang;
import com.same.ui.page.language.LanguageSelectPage;

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
public class ThemePage extends BasePage {
    @Override
    public boolean onFragmentCreate() {

        return super.onFragmentCreate();
    }
    Button currentTheme;
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_baseline_menu_24);
        actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setTitle(MyLang.getString("AutoNightTheme", R.string.Theme));

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(KeyHub.key_windowBackgroundGray));
        fragmentView = frameLayout;

        LinearLayout containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(LinearLayout.VERTICAL);

        containerLayout.addView(createButton(context, "分享主题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getParentActivity(), IntroActivity.class);
                getParentActivity().startActivity(intent);
            }
        }));
        containerLayout.addView(createButton(context, "选择语言", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentFragment(new LanguageSelectPage());
            }
        }));

        containerLayout.addView(createButton(context, "创建新主题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(MyLang.getString("NewTheme", R.string.NewTheme));
                builder.setMessage(MyLang.getString("CreateNewThemeAlert", R.string.CreateNewThemeAlert));
                builder.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
                builder.setPositiveButton(MyLang.getString("CreateTheme", R.string.CreateTheme), (dialog, which) -> {
                    if (parentLayout != null) {
                        ThemeInfo themeInfo = ThemeManager.createNewTheme(getParentActivity(), "新主题的名字");
                        ThemeManager.applyTheme(getParentActivity(), themeInfo);
                        parentLayout.rebuildAllFragmentViews(true, true);
                        new ThemeEditorView().show(getParentActivity(), themeInfo);
                    }
                });
                showDialog(builder.create());
            }
        }));

        containerLayout.addView(createButton(context, "重置主题", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getParentActivity());
                builder1.setTitle(MyLang.getString("ThemeResetToDefaultsTitle", R.string.ThemeResetToDefaultsTitle));
                builder1.setMessage(MyLang.getString("ThemeResetToDefaultsText", R.string.ThemeResetToDefaultsText));
                builder1.setPositiveButton(MyLang.getString("Reset", R.string.Reset), (dialogInterface, i) -> {
                    ThemeInfo themeInfo = ThemeManager.getTheme("Blue");
                    ThemeInfo currentTheme = ThemeManager.getCurrentTheme();
                    if (themeInfo != currentTheme) {
                        themeInfo.setCurrentAccentId(Theme.DEFALT_THEME_ACCENT_ID);
                        ThemeManager.saveThemeAccents(getParentActivity(), themeInfo, true, false, true, false);
                    } else if (themeInfo.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
                        NotificationCenter.postNotificationName(NotificationCenter.needSetDayNightTheme, currentTheme);
                    }
                });
                builder1.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder1.create();
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
                ThemeInfo themeInfo = (ThemeInfo) ThemeManager.getCurrentTheme();
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
                            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getParentActivity(), BuildConfig.APPLICATION_ID + ".provider", finalFile));
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception ignore) {
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
        }));
        currentTheme = createButton(context, ThemeName.getCurrentThemeName(getParentActivity()), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTheme.setText(ThemeName.getCurrentThemeName(getParentActivity()));
            }
        });
        containerLayout.addView(currentTheme);


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

        CheckBox checkBox = new CheckBox(context, R.drawable.ic_baseline_check_24);
        checkBox.setColor(Theme.getColor(KeyHub.key_checkbox), Theme.getColor(KeyHub.key_checkboxCheck));
        containerLayout.addView(checkBox, LayoutHelper.createFrame(22, 22, Gravity.RIGHT | Gravity.TOP, 0, 2, 2, 0));
        checkBox.setChecked(true, true);
        checkBox.setVisibility(View.VISIBLE);

        Button check = createButton(context, "check", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked(), true);
            }
        });
        containerLayout.addView(check);

        for (int i = 0; i < 10; i++) {
            CheckBox2 checkBox2 = new CheckBox2(context);
            checkBox2.setColor(Theme.getColor(KeyHub.key_checkbox), Theme.getColor(KeyHub.key_checkboxCheck), Theme.getColor(KeyHub.key_checkboxCheck));
            checkBox2.setDrawBackgroundAsArc(i);
            checkBox2.setDrawUnchecked(true);
            containerLayout.addView(checkBox2, LayoutHelper.createFrame(22, 22, Gravity.RIGHT | Gravity.TOP, 0, 2, 2, 0));
            checkBox2.setChecked(true, true);
            checkBox2.setVisibility(View.VISIBLE);

            containerLayout.addView(createButton(context, "check", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox2.setChecked(!checkBox2.isChecked(), true);
                }
            }));
        }

        CheckBoxSquare checkBox3 = new CheckBoxSquare(context, true);
//        checkBox3.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
        containerLayout.addView(checkBox3, LayoutHelper.createFrame(22, 22, Gravity.RIGHT | Gravity.TOP, 0, 2, 2, 0));
        checkBox3.setChecked(true, true);
        checkBox3.setVisibility(View.VISIBLE);
        containerLayout.addView(createButton(context, "check", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox3.setChecked(!checkBox3.isChecked(), true);
            }
        }));

        RadioButton button = new RadioButton(context);
        button.setSize(AndroidUtilities.dp(20));
        containerLayout.addView(button, LayoutHelper.createFrame(22, 22, Gravity.RIGHT | Gravity.TOP, 0, 2, 2, 0));
        containerLayout.addView(createButton(context, "check", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setChecked(!button.isChecked(), true);
            }
        }));

        ScrollView scrollView = new ScrollView(getParentActivity());
        scrollView.addView(containerLayout);
        frameLayout.addView(scrollView);

        return fragmentView;
    }

    private Button createButton(Context context, String text, View.OnClickListener clickListener) {
        Button button = new Button(context);
        button.setPadding(20, 20, 20, 20);
        button.setTextSize(18);
        button.setGravity(Gravity.CENTER);
        button.setText(text);
        button.setOnClickListener(clickListener);
        return button;
    }
}
