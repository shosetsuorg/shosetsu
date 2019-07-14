package com.github.doomsdayrs.apps.shosetsu.ui.novel;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.backend.async.NovelFragmentChapterViewLoad;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.NovelFragmentChapterViewHideBar;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO MarkDown support
public class NovelFragmentChapterReader extends AppCompatActivity {
    private String title;
    private ScrollView scrollView;
    public TextView textView;
    public ProgressBar progressBar;
    public Formatter formatter;
    public String chapterURL;
    private String novelURL;
    public String unformattedText = null;
    public String text = null;
    private MenuItem bookmark;

    private MenuItem textSmall;
    private MenuItem textMedium;
    private MenuItem textLarge;

    private MenuItem pspaceNon;
    private MenuItem pspaceSmall;
    private MenuItem pspaceMedium;
    private MenuItem pspaceLarge;



    private MenuItem ispaceNon;
    private MenuItem ispaceSmall;
    private MenuItem ispaceMedium;
    private MenuItem ispaceLarge;
    
    private int a = 0;
    private boolean bookmarked;


    // ERROR SCREEN
    //TODO Handle ERRORs on loading, EVERYWHERE
    public ConstraintLayout errorView;
    public TextView errorMessage;
    public Button errorButton;

    /**
     * Save data of view before destroyed
     *
     * @param outState output save
     */
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("unformattedText", text);
        outState.putString("text", text);
        outState.putString("chapterURL", chapterURL);
        outState.putInt("formatter", formatter.getID());
        outState.putString("novelURL", novelURL);
        outState.putString("title", title);
    }

    /**
     * Creates the option menu (on the top toolbar)
     *
     * @param menu Menu reference to fill
     * @return if made
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.toolbar_chapter_view, menu);
        // Night mode
        menu.findItem(R.id.chapter_view_nightMode).setChecked(!SettingsController.isReaderLightMode());

        // Bookmark
        bookmark = menu.findItem(R.id.chapter_view_bookmark);
        {
            textSmall = menu.findItem(R.id.chapter_view_textSize_small);
            textMedium = menu.findItem(R.id.chapter_view_textSize_medium);
            textLarge = menu.findItem(R.id.chapter_view_textSize_large);

            switch ((int) Settings.ReaderTextSize) {
                default:
                    SettingsController.setTextSize(14);
                case 14:
                    textSmall.setChecked(true);
                    break;
                case 17:
                    textMedium.setChecked(true);
                    break;
                case 20:
                    textLarge.setChecked(true);
                    break;
            }
        }
        {
            pspaceNon = menu.findItem(R.id.chapter_view_paragraphSpace_none);
            pspaceSmall = menu.findItem(R.id.chapter_view_paragraphSpace_small);
            pspaceMedium = menu.findItem(R.id.chapter_view_paragraphSpace_medium);
            pspaceLarge = menu.findItem(R.id.chapter_view_paragraphSpace_large);

            switch (Settings.paragraphSpacing) {
                case 0:
                    pspaceNon.setChecked(true);
                    break;
                case 1:
                    pspaceSmall.setChecked(true);
                    break;
                case 2:
                    pspaceMedium.setChecked(true);
                    break;
                case 3:
                    pspaceLarge.setChecked(true);
                    break;
            }
        }
        {
            ispaceNon = menu.findItem(R.id.chapter_view_indent_none);
            ispaceSmall = menu.findItem(R.id.chapter_view_indent_small);
            ispaceMedium = menu.findItem(R.id.chapter_view_indent_medium);
            ispaceLarge = menu.findItem(R.id.chapter_view_indent_large);

            switch (Settings.indentSize) {
                case 0:
                    ispaceNon.setChecked(true);
                    break;
                case 1:
                    ispaceSmall.setChecked(true);
                    break;
                case 2:
                    ispaceMedium.setChecked(true);
                    break;
                case 3:
                    ispaceLarge.setChecked(true);
                    break;
            }
        }

        bookmarked = Database.DatabaseChapter.isBookMarked(chapterURL);
        if (bookmarked) {
            bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);
            int y = SettingsController.getYBookmark(chapterURL);
            Log.d("Loaded Scroll", Integer.toString(y));
            scrollView.setScrollY(y);
        }
        return true;
    }

    /**
     * Changes the theme of the reader
     * TODO change the scroll position bars color
     */
    public void setUpReader() {
        scrollView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        textView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        textView.setTextColor(Settings.ReaderTextColor);
        textView.setTextSize(Settings.ReaderTextSize);

        if (unformattedText != null) {
            StringBuilder replaceSpacing = new StringBuilder("\n");
            for (int x = 0; x < Settings.paragraphSpacing; x++)
                replaceSpacing.append("\n");

            for (int x = 0; x < Settings.indentSize; x++)
                replaceSpacing.append("\t");
            
            text = unformattedText.replaceAll("\n", replaceSpacing.toString());
            textView.setText(text);
        }
    }


    /**
     * What to do when an menu item is selected
     *
     * @param item item selected
     * @return true if processed
     */
    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        Log.d("item", item.toString());
        System.out.println("Text size" + textView.getTextSize());
        switch (item.getItemId()) {
            case R.id.chapter_view_nightMode:
                if (!item.isChecked()) {
                    SettingsController.swapReaderColor();
                    setUpReader();
                } else {
                    SettingsController.swapReaderColor();
                    setUpReader();
                }
                item.setChecked(!item.isChecked());
                return true;

            case R.id.chapter_view_bookmark:

                int y = scrollView.getScrollY();
                Log.d("ScrollSave", Integer.toString(y));

                bookmarked = SettingsController.toggleBookmarkChapter(chapterURL);
                if (bookmarked)
                    bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);
                else bookmark.setIcon(R.drawable.ic_bookmark_border_black_24dp);
                return true;

            case R.id.chapter_view_textSize_small:
                SettingsController.setTextSize(14);
                setUpReader();

                item.setChecked(true);
                textMedium.setChecked(false);
                textLarge.setChecked(false);
                return true;
            case R.id.chapter_view_textSize_medium:
                SettingsController.setTextSize(17);
                setUpReader();

                item.setChecked(true);
                textSmall.setChecked(false);
                textLarge.setChecked(false);
                return true;
            case R.id.chapter_view_textSize_large:
                SettingsController.setTextSize(20);
                setUpReader();
                item.setChecked(true);
                textSmall.setChecked(false);
                textMedium.setChecked(false);
                return true;

            case R.id.chapter_view_paragraphSpace_none:
                SettingsController.changeParagraphSpacing(0);
                setUpReader();
                pspaceNon.setChecked(true);
                pspaceSmall.setChecked(false);
                pspaceMedium.setChecked(false);
                pspaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_paragraphSpace_small:
                SettingsController.changeParagraphSpacing(1);
                setUpReader();
                pspaceNon.setChecked(false);
                pspaceSmall.setChecked(true);
                pspaceMedium.setChecked(false);
                pspaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_paragraphSpace_medium:
                SettingsController.changeParagraphSpacing(2);
                setUpReader();
                pspaceNon.setChecked(false);
                pspaceSmall.setChecked(false);
                pspaceMedium.setChecked(true);
                pspaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_paragraphSpace_large:
                SettingsController.changeParagraphSpacing(3);
                setUpReader();
                pspaceNon.setChecked(false);
                pspaceSmall.setChecked(false);
                pspaceMedium.setChecked(false);
                pspaceLarge.setChecked(true);
                return true;


            case R.id.chapter_view_indent_none:
                SettingsController.changeIndentSize(0);
                setUpReader();
                ispaceNon.setChecked(true);
                ispaceSmall.setChecked(false);
                ispaceMedium.setChecked(false);
                ispaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_indent_small:
                SettingsController.changeIndentSize(1);
                setUpReader();
                ispaceNon.setChecked(false);
                ispaceSmall.setChecked(true);
                ispaceMedium.setChecked(false);
                ispaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_indent_medium:
                SettingsController.changeIndentSize(2);
                setUpReader();
                ispaceNon.setChecked(false);
                ispaceSmall.setChecked(false);
                ispaceMedium.setChecked(true);
                ispaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_indent_large:
                SettingsController.changeIndentSize(3);
                setUpReader();
                ispaceNon.setChecked(false);
                ispaceSmall.setChecked(false);
                ispaceMedium.setChecked(false);
                ispaceLarge.setChecked(true);
                return true;
        }
        return false;
    }


    /**
     * Create method
     *
     * @param savedInstanceState save object
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreate", "NovelFragmentChapterReader");
        switch (Settings.themeMode) {
            case 0:
                setTheme(R.style.Theme_MaterialComponents_Light_NoActionBar);
                break;
            case 1:
                setTheme(R.style.Theme_MaterialComponents_NoActionBar);
                break;
            case 2:
                setTheme(R.style.ThemeOverlay_MaterialComponents_Dark);
        }
        setContentView(R.layout.fragment_novel_chapter_reader);
        {
            errorView = findViewById(R.id.network_error);
            errorMessage = findViewById(R.id.error_message);
            errorButton = findViewById(R.id.error_button);
            progressBar = findViewById(R.id.fragment_novel_chapter_view_progress);
            scrollView = findViewById(R.id.fragment_novel_scroll);
        }

        {
            novelURL = getIntent().getStringExtra("novelURL");
            title = getIntent().getStringExtra("title");
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            formatter = DefaultScrapers.formatters.get(getIntent().getIntExtra("formatter", -1) - 1);
            /*
             * Chooses the way the way to save the scroll position
             * the before marshmallow version is untested
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> bottom());
            } else {
                scrollView.getViewTreeObserver().addOnScrollChangedListener(this::bottom);
            }
            textView = findViewById(R.id.fragment_novel_chapter_view_text);
            textView.setOnClickListener(new NovelFragmentChapterViewHideBar(toolbar));
        }

        setUpReader();

        if (savedInstanceState != null) {
            unformattedText = savedInstanceState.getString("unformattedText");
            title = savedInstanceState.getString("title");
            chapterURL = savedInstanceState.getString("chapterURL");
            formatter = DefaultScrapers.formatters.get(savedInstanceState.getInt("formatter") - 1);
            text = savedInstanceState.getString("text");
        } else chapterURL = getIntent().getStringExtra("chapterURL");

        Log.d("novelURL", Objects.requireNonNull(chapterURL));

        if (Database.DatabaseChapter.isSaved(chapterURL)) {
            unformattedText = Objects.requireNonNull(Database.DatabaseChapter.getSavedNovelPassage(chapterURL));

        } else if (text == null)
            if (chapterURL != null) {
                new NovelFragmentChapterViewLoad(this).execute();
            }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);

        setUpReader();
    }

    public void bottom() {
        if (scrollView.canScrollVertically(1))
            if (a % 5 == 0) {
                int y = scrollView.getScrollY();
                Log.d("ScrollSave", Integer.toString(y));
                Database.DatabaseChapter.updateY(chapterURL, y);
            } else a++;
        else {
            Database.DatabaseChapter.setChapterStatus(chapterURL, Status.READ);
            //TODO Get total word count of passage, then add to a storage counter that memorizes the total (Chapters read, Chapters Unread, Chapters reading, Word count)
        }
    }
}
