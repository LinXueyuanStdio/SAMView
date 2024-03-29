package com.same.ui.page.language;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.same.lib.base.AndroidUtilities;
import com.same.lib.core.ActionBar;
import com.same.lib.core.ActionBarMenu;
import com.same.lib.core.ActionBarMenuItem;
import com.same.lib.core.ThemeDescription;
import com.same.lib.drawable.DrawableManager;
import com.same.lib.helper.LayoutHelper;
import com.same.lib.listview.LinearLayoutManager;
import com.same.lib.listview.RecyclerView;
import com.same.lib.same.page.IsSecondHomePage;
import com.same.lib.same.theme.CommonTheme;
import com.same.lib.same.theme.dialog.AlertDialog;
import com.same.lib.same.view.EmptyTextProgressView;
import com.same.lib.same.view.RecyclerListView;
import com.same.lib.theme.KeyHub;
import com.same.lib.theme.MyThemeDescription;
import com.same.lib.theme.Theme;
import com.same.lib.theme.wrap.BaseThemePage;
import com.same.lib.util.UIThread;
import com.same.ui.R;
import com.same.ui.lang.MyLang;
import com.same.ui.page.language.cell.LanguageCell;
import com.same.ui.page.language.cell.ShadowSectionCell;
import com.timecat.component.locale.LocaleInfo;
import com.timecat.component.locale.MLang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2020/8/27
 * @description null
 * @usage null
 */
public class LanguageSelectPage extends BaseThemePage implements IsSecondHomePage {

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ListAdapter searchListViewAdapter;
    private EmptyTextProgressView emptyView;

    private boolean searchWas;
    private boolean searching;

    private Timer searchTimer;
    private ArrayList<LocaleInfo> searchResult;
    private ArrayList<LocaleInfo> sortedLanguages;
    private ArrayList<LocaleInfo> unofficialLanguages;

    @Override
    public boolean onFragmentCreate() {
        fillLanguages();
        MyLang.getInstance().loadRemoteLanguages(MyLang.getContext(), new MLang.FinishLoadCallback() {
            @Override
            public void finishLoad() {
                if (listAdapter != null) {
                    fillLanguages();
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override
    public View createView(@NonNull Context context) {
        searching = false;
        searchWas = false;

        actionBar.setBackButtonImage(R.drawable.ic_baseline_arrow_back_ios_24);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setSupportsHolidayImage(true);
        actionBar.setTitle(MyLang.getString("Language", R.string.Language));

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_baseline_search_24).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {
                searching = true;
            }

            @Override
            public void onSearchCollapse() {
                search(null);
                searching = false;
                searchWas = false;
                if (listView != null) {
                    emptyView.setVisibility(View.GONE);
                    listView.setAdapter(listAdapter);
                }
            }

            @Override
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                search(text);
                if (text.length() != 0) {
                    searchWas = true;
                    if (listView != null) {
                        listView.setAdapter(searchListViewAdapter);
                    }
                }
            }
        });
        item.setSearchFieldHint(MyLang.getString("Search", R.string.Search));

        listAdapter = new ListAdapter(context, false);
        searchListViewAdapter = new ListAdapter(context, true);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(KeyHub.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        emptyView = new EmptyTextProgressView(context);
        emptyView.setText(MyLang.getString("NoResult", R.string.NoResult));
        emptyView.showTextView();
        emptyView.setShowAtCenter(true);
        frameLayout.addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView = new RecyclerListView(context);
        listView.setEmptyView(emptyView);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (getParentActivity() == null || parentLayout == null || !(view instanceof LanguageCell)) {
                return;
            }
            LanguageCell cell = (LanguageCell) view;
            LocaleInfo localeInfo = cell.getCurrentLocale();
            if (localeInfo != null) {
                MyLang.getInstance().applyLanguage(getParentActivity(), localeInfo, true, false, false, true, new MLang.FinishLoadCallback() {
                    @Override
                    public void finishLoad() {
                        parentLayout.rebuildAllFragmentViews(false, false);
                        finishFragment();
                    }
                });
            }
        });

        listView.setOnItemLongClickListener((view, position) -> {
            if (getParentActivity() == null || parentLayout == null || !(view instanceof LanguageCell)) {
                return false;
            }
            LanguageCell cell = (LanguageCell) view;
            LocaleInfo localeInfo = cell.getCurrentLocale();
            if (localeInfo == null || localeInfo.pathToFile == null || localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE) {
                return false;
            }
            final LocaleInfo finalLocaleInfo = localeInfo;
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(MyLang.getString("DeleteLocalizationTitle", R.string.DeleteLocalizationTitle));
            builder.setMessage(MyLang.replaceTags(context, MyLang.formatString("DeleteLocalizationText", R.string.DeleteLocalizationText, localeInfo.name)));
            builder.setPositiveButton(MyLang.getString("Delete", R.string.Delete), (dialogInterface, i) -> {
                if (MyLang.getInstance().deleteLanguage(getParentActivity(), finalLocaleInfo)) {
                    fillLanguages();
                    if (searchResult != null) {
                        searchResult.remove(finalLocaleInfo);
                    }
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    if (searchListViewAdapter != null) {
                        searchListViewAdapter.notifyDataSetChanged();
                    }
                }
            });
            builder.setNegativeButton(MyLang.getString("Cancel", R.string.Cancel), null);
            AlertDialog alertDialog = builder.create();
            showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (button != null) {
                button.setTextColor(Theme.getColor(KeyHub.key_dialogTextRed2));
            }
            return true;
        });

        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    Activity activity = getParentActivity();
                    if (activity == null) { return; }
                    AndroidUtilities.hideKeyboard(activity.getCurrentFocus());
                }
            }
        });

        return fragmentView;
    }

    private void fillLanguages() {
        final LocaleInfo currentLocale = MyLang.getInstance().getCurrentLocaleInfo();
        Comparator<LocaleInfo> comparator = (o, o2) -> {
            if (o == currentLocale) {
                return -1;
            } else if (o2 == currentLocale) {
                return 1;
            } else if (o.serverIndex == o2.serverIndex) {
                return o.name.compareTo(o2.name);
            }
            if (o.serverIndex > o2.serverIndex) {
                return 1;
            } else if (o.serverIndex < o2.serverIndex) {
                return -1;
            }
            return 0;
        };

        sortedLanguages = new ArrayList<>();
        unofficialLanguages = new ArrayList<>(MyLang.getInstance().unofficialLanguages);

        ArrayList<LocaleInfo> arrayList = MyLang.getInstance().languages;
        for (int a = 0, size = arrayList.size(); a < size; a++) {
            LocaleInfo info = arrayList.get(a);
            if (info.serverIndex != Integer.MAX_VALUE) {
                sortedLanguages.add(info);
            } else {
                unofficialLanguages.add(info);
            }
        }
        Collections.sort(sortedLanguages, comparator);
        Collections.sort(unofficialLanguages, comparator);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void search(final String query) {
        if (query == null) {
            searchResult = null;
        } else {
            try {
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            searchTimer = new Timer();
            searchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        searchTimer.cancel();
                        searchTimer = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    processSearch(query);
                }
            }, 100, 300);
        }
    }

    private void processSearch(final String query) {
        AndroidUtilities.searchQueue.postRunnable(() -> {

            String q = query.trim().toLowerCase();
            if (q.length() == 0) {
                updateSearchResults(new ArrayList<>());
                return;
            }
            long time = System.currentTimeMillis();
            ArrayList<LocaleInfo> resultArray = new ArrayList<>();

            for (int a = 0, N = unofficialLanguages.size(); a < N; a++) {
                LocaleInfo c = unofficialLanguages.get(a);
                if (c.name.toLowerCase().startsWith(query) || c.nameEnglish.toLowerCase().startsWith(query)) {
                    resultArray.add(c);
                }
            }

            for (int a = 0, N = sortedLanguages.size(); a < N; a++) {
                LocaleInfo c = sortedLanguages.get(a);
                if (c.name.toLowerCase().startsWith(query) || c.nameEnglish.toLowerCase().startsWith(query)) {
                    resultArray.add(c);
                }
            }

            updateSearchResults(resultArray);
        });
    }

    private void updateSearchResults(final ArrayList<LocaleInfo> arrCounties) {
        UIThread.runOnUIThread(() -> {
            searchResult = arrCounties;
            searchListViewAdapter.notifyDataSetChanged();
        });
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;
        private boolean search;

        public ListAdapter(Context context, boolean isSearch) {
            mContext = context;
            search = isSearch;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override
        public int getItemCount() {
            if (search) {
                if (searchResult == null) {
                    return 0;
                }
                return searchResult.size();
            } else {
                int count = sortedLanguages.size();
                if (count != 0) {
                    count++;
                }
                if (!unofficialLanguages.isEmpty()) {
                    count += unofficialLanguages.size() + 1;
                }
                return count;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0: {
                    view = new LanguageCell(mContext, false);
                    view.setBackgroundColor(Theme.getColor(KeyHub.key_windowBackgroundWhite));
                    break;
                }
                case 1:
                default: {
                    view = new ShadowSectionCell(mContext);
                    break;
                }
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: {
                    LanguageCell textSettingsCell = (LanguageCell) holder.itemView;
                    LocaleInfo localeInfo;
                    boolean last;
                    if (search) {
                        localeInfo = searchResult.get(position);
                        last = position == searchResult.size() - 1;
                    } else if (!unofficialLanguages.isEmpty() && position >= 0 && position < unofficialLanguages.size()) {
                        localeInfo = unofficialLanguages.get(position);
                        last = position == unofficialLanguages.size() - 1;
                    } else {
                        if (!unofficialLanguages.isEmpty()) {
                            position -= unofficialLanguages.size() + 1;
                        }
                        localeInfo = sortedLanguages.get(position);
                        last = position == sortedLanguages.size() - 1;
                    }
                    if (localeInfo.isLocal()) {
                        textSettingsCell.setLanguage(localeInfo, String.format("%1$s (%2$s)", localeInfo.name, MyLang.getString("LanguageCustom", R.string.LanguageCustom)), !last);
                    } else {
                        textSettingsCell.setLanguage(localeInfo, null, !last);
                    }
                    textSettingsCell.setLanguageSelected(localeInfo == MyLang.getInstance().getCurrentLocaleInfo());
                    break;
                }
                case 1: {
                    ShadowSectionCell sectionCell = (ShadowSectionCell) holder.itemView;
                    if (!unofficialLanguages.isEmpty() && position == unofficialLanguages.size()) {
                        sectionCell.setBackgroundDrawable(DrawableManager.getThemedDrawable(mContext, R.drawable.greydivider, KeyHub.key_windowBackgroundGrayShadow));
                    } else {
                        sectionCell.setBackgroundDrawable(DrawableManager.getThemedDrawable(mContext, R.drawable.greydivider_bottom, KeyHub.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int i) {
            if (!unofficialLanguages.isEmpty() && (i == unofficialLanguages.size() || i == unofficialLanguages.size() + sortedLanguages.size() + 1) || unofficialLanguages.isEmpty() && i == sortedLanguages.size()) {
                return 1;
            }
            return 0;
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> d = new ArrayList<>();

        d.add(new MyThemeDescription(fragmentView, MyThemeDescription.FLAG_BACKGROUND, null, null, null, null, KeyHub.key_windowBackgroundGray));

        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_BACKGROUND, null, null, null, null, KeyHub.key_actionBarDefault));
        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, KeyHub.key_actionBarDefaultIcon));
        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, KeyHub.key_actionBarDefaultTitle));
        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, KeyHub.key_actionBarDefaultSelector));
        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_AB_SEARCH, null, null, null, null, KeyHub.key_actionBarDefaultSearch));
        d.add(new MyThemeDescription(actionBar, MyThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, KeyHub.key_actionBarDefaultSearchPlaceholder));

        d.add(new MyThemeDescription(emptyView, MyThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, KeyHub.key_emptyListPlaceholder));

        d.add(new MyThemeDescription(listView, MyThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, null, null, null, KeyHub.key_windowBackgroundWhite));
        d.add(new MyThemeDescription(listView, MyThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, KeyHub.key_actionBarDefault));
        d.add(new MyThemeDescription(listView, MyThemeDescription.FLAG_SELECTOR, null, null, null, null, KeyHub.key_listSelector));
        d.add(new MyThemeDescription(listView, 0, new Class[]{View.class}, CommonTheme.dividerPaint, null, null, KeyHub.key_divider));
        d.add(new MyThemeDescription(listView, MyThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, KeyHub.key_windowBackgroundGrayShadow));
        d.add(new MyThemeDescription(listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, null, null, null, KeyHub.key_windowBackgroundWhiteBlackText));
        d.add(new MyThemeDescription(listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, null, null, null, KeyHub.key_windowBackgroundWhiteGrayText3));

        return d;
    }
}
