package com.github.doomsdayrs.apps.shosetsu.ui.reader;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.async.ReaderViewLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.NovelFragmentChapterViewHideBar;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.google.android.material.chip.Chip;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.changeIndentSize;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.changeParagraphSpacing;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isReaderNightMode;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isTapToScroll;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInBrowser;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInWebview;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setTextSize;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.swapReaderColor;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleBookmarkChapter;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleTapToScroll;
import static com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel.getNextChapter;

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO MarkDown support
public class ChapterReader extends AppCompatActivity {
    public boolean ready = false;


    public ScrollView scrollView;
    public TextView textView;
    public ProgressBar progressBar;
    public Chip nextChapter;

    public String title;
    public Formatter formatter;
    public String chapterURL;
    public String unformattedText = null;
    public String text = null;

    private MenuItem bookmark;
    private boolean bookmarked;

    private MenuItem tap_to_scroll;

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


    //Tap to scroll
    private View scroll_up;
    private View scroll_down;

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
        menu.findItem(R.id.chapter_view_nightMode).setChecked(isReaderNightMode());

        // Bookmark
        {
            bookmark = menu.findItem(R.id.chapter_view_bookmark);
            bookmarked = Database.DatabaseChapter.isBookMarked(chapterURL);
            if (bookmarked)
                bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);

        }

        // Tap To Scroll
        {
            tap_to_scroll = menu.findItem(R.id.tap_to_scroll);
            tap_to_scroll.setChecked(isTapToScroll());
        }
        // Text size
        {
            textSmall = menu.findItem(R.id.chapter_view_textSize_small);
            textMedium = menu.findItem(R.id.chapter_view_textSize_medium);
            textLarge = menu.findItem(R.id.chapter_view_textSize_large);

            switch ((int) Settings.ReaderTextSize) {
                default:
                    setTextSize(14);
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

        // Paragraph Space
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

        // Indent Space
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


        return true;
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
        switch (item.getItemId()) {
            case R.id.chapter_view_nightMode:
                if (!item.isChecked()) {
                    swapReaderColor();
                    setUpReader();
                } else {
                    swapReaderColor();
                    setUpReader();
                }
                item.setChecked(!item.isChecked());
                return true;
            case R.id.tap_to_scroll:
                tap_to_scroll.setChecked(toggleTapToScroll());
                return true;
            case R.id.chapter_view_bookmark:


                bookmarked = toggleBookmarkChapter(chapterURL);
                if (bookmarked)
                    bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);
                else bookmark.setIcon(R.drawable.ic_bookmark_border_black_24dp);
                return true;

            case R.id.chapter_view_textSize_small:
                setTextSize(14);
                setUpReader();

                item.setChecked(true);
                textMedium.setChecked(false);
                textLarge.setChecked(false);
                return true;
            case R.id.chapter_view_textSize_medium:
                setTextSize(17);
                setUpReader();

                item.setChecked(true);
                textSmall.setChecked(false);
                textLarge.setChecked(false);
                return true;
            case R.id.chapter_view_textSize_large:
                setTextSize(20);
                setUpReader();
                item.setChecked(true);
                textSmall.setChecked(false);
                textMedium.setChecked(false);
                return true;

            case R.id.chapter_view_paragraphSpace_none:
                changeParagraphSpacing(0);
                setUpReader();
                pspaceNon.setChecked(true);
                pspaceSmall.setChecked(false);
                pspaceMedium.setChecked(false);
                pspaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_paragraphSpace_small:
                changeParagraphSpacing(1);
                setUpReader();
                pspaceNon.setChecked(false);
                pspaceSmall.setChecked(true);
                pspaceMedium.setChecked(false);
                pspaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_paragraphSpace_medium:
                changeParagraphSpacing(2);
                setUpReader();
                pspaceNon.setChecked(false);
                pspaceSmall.setChecked(false);
                pspaceMedium.setChecked(true);
                pspaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_paragraphSpace_large:
                changeParagraphSpacing(3);
                setUpReader();
                pspaceNon.setChecked(false);
                pspaceSmall.setChecked(false);
                pspaceMedium.setChecked(false);
                pspaceLarge.setChecked(true);
                return true;


            case R.id.chapter_view_indent_none:
                changeIndentSize(0);
                setUpReader();
                ispaceNon.setChecked(true);
                ispaceSmall.setChecked(false);
                ispaceMedium.setChecked(false);
                ispaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_indent_small:
                changeIndentSize(1);
                setUpReader();
                ispaceNon.setChecked(false);
                ispaceSmall.setChecked(true);
                ispaceMedium.setChecked(false);
                ispaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_indent_medium:
                changeIndentSize(2);
                setUpReader();
                ispaceNon.setChecked(false);
                ispaceSmall.setChecked(false);
                ispaceMedium.setChecked(true);
                ispaceLarge.setChecked(false);
                return true;
            case R.id.chapter_view_indent_large:
                changeIndentSize(3);
                setUpReader();
                ispaceNon.setChecked(false);
                ispaceSmall.setChecked(false);
                ispaceMedium.setChecked(false);
                ispaceLarge.setChecked(true);
                return true;
            case R.id.browser:
                openInBrowser(this, chapterURL);
                return true;
            case R.id.webview:
                openInWebview(this, chapterURL);
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

        if (savedInstanceState != null) {
            unformattedText = savedInstanceState.getString("unformattedText");
            title = savedInstanceState.getString("title");
            chapterURL = savedInstanceState.getString("chapterURL");
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"));
            text = savedInstanceState.getString("text");
        } else {
            chapterURL = getIntent().getStringExtra("chapterURL");
            title = getIntent().getStringExtra("title");
            formatter = DefaultScrapers.getByID(getIntent().getIntExtra("formatter", -1));
        }
        Log.i("Reading", chapterURL);

        errorView = findViewById(R.id.network_error);
        errorMessage = findViewById(R.id.error_message);
        errorButton = findViewById(R.id.error_button);
        progressBar = findViewById(R.id.fragment_novel_chapter_view_progress);

        scrollView = findViewById(R.id.fragment_novel_scroll);
        addBottomListener();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = findViewById(R.id.fragment_novel_chapter_view_text);
        textView.setOnClickListener(new NovelFragmentChapterViewHideBar(toolbar));

        // Scroll up listener
        scroll_up = findViewById(R.id.scroll_up);
        scroll_up.setOnClickListener(view -> scrollUp());

        // Scroll down listener
        scroll_down = findViewById(R.id.scroll_down);
        scroll_down.setOnClickListener(view -> scrollDown());

        nextChapter = findViewById(R.id.next_chapter);
        nextChapter.setOnClickListener(view -> {
            NovelChapter novelChapter = getNextChapter(chapterURL);

            if (novelChapter != null) {
                if (!novelChapter.link.equalsIgnoreCase(chapterURL)) {
                    title = novelChapter.chapterNum;
                    chapterURL = novelChapter.link;
                    loadChapter();
                } else
                    Toast.makeText(getApplicationContext(), "No more chapters!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Cannot move to next chapter, Please exit reader", Toast.LENGTH_LONG).show();
            }
            nextChapter.setVisibility(View.GONE);

        });

        loadChapter();
    }

    /**
     * Loads the chapter to be read
     */
    public void loadChapter() {
        ready = false;
        if (Database.DatabaseChapter.isSaved(chapterURL)) {
            unformattedText = Objects.requireNonNull(Database.DatabaseChapter.getSavedNovelPassage(chapterURL));
            setUpReader();
            scrollView.post(() -> scrollView.scrollTo(0, Database.DatabaseChapter.getY(chapterURL)));
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(title);
            ready = true;
        } else if (chapterURL != null) {
            unformattedText = "";
            setUpReader();
            new ReaderViewLoader(this).execute();
        }

        Database.DatabaseChapter.setChapterStatus(chapterURL, Status.READING);
    }


    /**
     * Scrolls the text upwards
     */
    private void scrollUp() {
        if (isTapToScroll()) {
            Log.i("Scroll", "Up");
            int y = scrollView.getScrollY();
            if (y - 100 > 0)
                y -= 100;
            else y = 0;
            scrollView.smoothScrollTo(0, y);
        }
    }

    /**
     * Scrolls the text downwards
     */
    private void scrollDown() {
        if (isTapToScroll()) {
            Log.i("Scroll", "Down");
            int y = scrollView.getScrollY();
            int my = scrollView.getMaxScrollAmount();
            if (y + 100 < my)
                y += 100;
            else y = my;
            scrollView.smoothScrollTo(0, y);
        }
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
     * Sets up the hitting bottom listener
     */
    public void addBottomListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> bottom());
        } else {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(this::bottom);
        }
    }

    /**
     * What to do when scroll hits bottom
     */
    public void bottom() {
        int total = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
        if (ready)
            if ((scrollView.getScrollY() / (float) total) < .99) {
                int y = scrollView.getScrollY();
                if (y % 5 == 0) {
                    Log.d("YMAX", String.valueOf(total));
                    Log.d("YC", String.valueOf(y));
                    Log.d("YD", String.valueOf((scrollView.getScrollY() / (float) total)));
                    Log.d("TY", String.valueOf(textView.getScrollY()));

                    if (chapterURL != null && Database.DatabaseChapter.getStatus(chapterURL) != Status.READ)
                        Database.DatabaseChapter.updateY(chapterURL, y);
                }
            } else {
                Log.i("Scroll", "Marking chapter as READ");
                if (chapterURL != null) {
                    Database.DatabaseChapter.setChapterStatus(chapterURL, Status.READ);
                    Database.DatabaseChapter.updateY(chapterURL, 0);
                    nextChapter.setVisibility(View.VISIBLE);
                }
                //TODO Get total word count of passage, then add to a storage counter that memorizes the total (Chapters read, Chapters Unread, Chapters reading, Word count)
            }
    }


}
