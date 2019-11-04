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
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.async.ReaderViewLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.NovelFragmentChapterViewHideBar;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.google.android.material.chip.Chip;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import us.feras.mdv.MarkdownView;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.changeIndentSize;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.changeParagraphSpacing;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.demarkMenuItems;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isReaderNightMode;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isTapToScroll;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInBrowser;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInWebview;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setTextSize;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.swapReaderColor;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleBookmarkChapter;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleTapToScroll;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.getReaderType;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.setReaderType;
import static com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel.getNextChapter;
import static com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel.novelID;

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
    int chapterID;


    public boolean ready = false;

    public TextView textView;
    public MarkdownView markdownView;
    int readerType;

    public ScrollView scrollView;


    public ProgressBar progressBar;
    public Chip nextChapter;

    public String title;
    public Formatter formatter;

    public int chapterID;
    public String chapterURL;
    public String unformattedText = null;
    public String text = null;

    private MenuItem bookmark;
    private boolean bookmarked;

    private MenuItem tap_to_scroll;
    // Order of values. Small,Medium,Large
    private MenuItem[] textSizes = new MenuItem[3];

    // Order of values. Non,Small,Medium,Large
    private MenuItem[] paragraphSpaces = new MenuItem[4];

    // Order of values. Non,Small,Medium,Large
    private MenuItem[] indentSpaces = new MenuItem[4];

    // Order of values. Default, Markdown
    private MenuItem[] readers = new MenuItem[2];


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
        outState.putInt("chapterID", chapterID);
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
            bookmarked = Database.DatabaseChapter.isBookMarked(chapterID);
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
            textSizes[0] = menu.findItem(R.id.chapter_view_textSize_small);
            textSizes[1] = menu.findItem(R.id.chapter_view_textSize_medium);
            textSizes[2] = menu.findItem(R.id.chapter_view_textSize_large);

            switch ((int) Settings.ReaderTextSize) {
                default:
                    setTextSize(14);
                case 14:
                    textSizes[0].setChecked(true);
                    break;
                case 17:
                    textSizes[1].setChecked(true);
                    break;
                case 20:
                    textSizes[2].setChecked(true);
                    break;
            }
        }

        // Paragraph Space
        {
            paragraphSpaces[0] = menu.findItem(R.id.chapter_view_paragraphSpace_none);
            paragraphSpaces[1] = menu.findItem(R.id.chapter_view_paragraphSpace_small);
            paragraphSpaces[2] = menu.findItem(R.id.chapter_view_paragraphSpace_medium);
            paragraphSpaces[3] = menu.findItem(R.id.chapter_view_paragraphSpace_large);

            paragraphSpaces[Settings.paragraphSpacing].setChecked(true);
        }

        // Indent Space
        {
            indentSpaces[0] = menu.findItem(R.id.chapter_view_indent_none);
            indentSpaces[1] = menu.findItem(R.id.chapter_view_indent_small);
            indentSpaces[2] = menu.findItem(R.id.chapter_view_indent_medium);
            indentSpaces[3] = menu.findItem(R.id.chapter_view_indent_large);

            indentSpaces[Settings.indentSize].setChecked(true);
        }

        // Reader
        {

            readers[0] = menu.findItem(R.id.reader_0);
            readers[1] = menu.findItem(R.id.reader_1);
            readerType = getReaderType(novelID);

            switch (readerType) {
                case 1:
                    demarkMenuItems(readers, 1, null);
                    break;
                case 0:
                case -1:
                    demarkMenuItems(readers, 0, null);
                    break;
                case -2:
                default:
                    throw new RuntimeException("Invalid chapter?!? How are you reading this without the novel loaded in");
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
            default:
                return false;
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
                demarkMenuItems(indentSpaces, 0, new TextSizeChange());
                return true;
            case R.id.chapter_view_textSize_medium:
                demarkMenuItems(textSizes, 1, new TextSizeChange());
                return true;
            case R.id.chapter_view_textSize_large:
                demarkMenuItems(textSizes, 2, new TextSizeChange());
                return true;

            case R.id.chapter_view_paragraphSpace_none:
                demarkMenuItems(paragraphSpaces, 0, new ParaSpacingChange());
                return true;
            case R.id.chapter_view_paragraphSpace_small:
                demarkMenuItems(paragraphSpaces, 1, new ParaSpacingChange());
                return true;
            case R.id.chapter_view_paragraphSpace_medium:
                demarkMenuItems(paragraphSpaces, 2, new ParaSpacingChange());
                return true;
            case R.id.chapter_view_paragraphSpace_large:
                demarkMenuItems(paragraphSpaces, 3, new ParaSpacingChange());
                return true;

            case R.id.chapter_view_indent_none:
                demarkMenuItems(indentSpaces, 0, new IndentChange());
                return true;
            case R.id.chapter_view_indent_small:
                demarkMenuItems(indentSpaces, 1, new IndentChange());
                return true;
            case R.id.chapter_view_indent_medium:
                demarkMenuItems(indentSpaces, 2, new IndentChange());
                return true;
            case R.id.chapter_view_indent_large:
                demarkMenuItems(indentSpaces, 3, new IndentChange());
                return true;

            case R.id.browser:
                openInBrowser(this, chapterURL);
                return true;
            case R.id.webview:
                openInWebview(this, chapterURL);
                return true;
            case R.id.reader_0:
                demarkMenuItems(readers, 0, new ReaderChange());
                return true;
            case R.id.reader_1:
                demarkMenuItems(readers, 1, new ReaderChange());
                return true;

        }
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

        setContentView(R.layout.chapter_reader);

        if (savedInstanceState != null) {
            unformattedText = savedInstanceState.getString("unformattedText");
            title = savedInstanceState.getString("title");
            chapterID = savedInstanceState.getInt("chapterID");
            chapterURL = savedInstanceState.getString("chapterURL");
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"));
            text = savedInstanceState.getString("text");
        } else {
            chapterID = getIntent().getIntExtra("chapterID", -1);
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

        switch (readerType) {
            case 1:
                markdownView = findViewById(R.id.fragment_novel_chapter_view_markdown);
                markdownView.setOnClickListener(new NovelFragmentChapterViewHideBar(toolbar));
                break;
            case 0:
            case -1:
                textView = findViewById(R.id.fragment_novel_chapter_view_text);
                textView.setOnClickListener(new NovelFragmentChapterViewHideBar(toolbar));
                break;
            case -2:
            default:
                throw new RuntimeException("Invalid chapter?!? How are you reading this without the novel loaded in");
        }


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
                    title = novelChapter.title;
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
     * Changes the theme of the reader
     * TODO change the scroll position bars color
     */
    public void setUpReader() {
        scrollView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        textView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        switch (readerType) {
            case -1:
            case 0:
                textView.setTextColor(Settings.ReaderTextColor);
                textView.setTextSize(Settings.ReaderTextSize);
                break;
            default:
                break;
        }


        if (unformattedText != null) {
            StringBuilder replaceSpacing = new StringBuilder("\n");
            for (int x = 0; x < Settings.paragraphSpacing; x++)
                replaceSpacing.append("\n");

            for (int x = 0; x < Settings.indentSize; x++)
                replaceSpacing.append("\t");

            text = unformattedText.replaceAll("\n", replaceSpacing.toString());

            switch (readerType) {
                case -1:
                case 0:
                    textView.setText(text);
                    break;
                case 1:
                    markdownView.loadMarkdown(text);
            }
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
                    // Log.d("YMAX", String.valueOf(total));
                    // Log.d("YC", String.valueOf(y));
                    // Log.d("YD", String.valueOf((scrollView.getScrollY() / (float) total)));

                    //   Log.d("TY", String.valueOf(textView.getScrollY()));

                    if (chapterURL != null && Database.DatabaseChapter.getStatus(chapterID) != Status.READ)
                        Database.DatabaseChapter.updateY(chapterID, y);
                }
            } else {
                Log.i("Scroll", "Marking chapter as READ");
                if (chapterURL != null) {
                    Database.DatabaseChapter.setChapterStatus(chapterID, Status.READ);
                    Database.DatabaseChapter.updateY(chapterID, 0);
                    nextChapter.setVisibility(View.VISIBLE);
                }
                //TODO Get total word count of passage, then add to a storage counter that memorizes the total (Chapters read, Chapters Unread, Chapters reading, Word count)
            }
    }


    /**
     * Loads the chapter to be read
     */
    public void loadChapter() {
        ready = false;
        if (Database.DatabaseChapter.isSaved(chapterID)) {
            unformattedText = Objects.requireNonNull(Database.DatabaseChapter.getSavedNovelPassage(chapterID));
            setUpReader();
            scrollView.post(() -> scrollView.scrollTo(0, Database.DatabaseChapter.getY(chapterID)));
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(title);
            ready = true;
        } else if (chapterURL != null) {
            unformattedText = "";
            setUpReader();
            new ReaderViewLoader(this).execute();
        }

        Database.DatabaseChapter.setChapterStatus(chapterID, Status.READING);
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
     * Sets up the hitting bottom listener
     */
    public void addBottomListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> bottom());
        } else {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(this::bottom);
        }
    }


    private class TextSizeChange implements Utilities.DemarkAction {
        @Override
        public void action(int spared) {
            int[] a = {14, 17, 20};
            setTextSize(a[spared]);
            setUpReader();
        }
    }

    private class ParaSpacingChange implements Utilities.DemarkAction {
        @Override
        public void action(int spared) {
            changeParagraphSpacing(spared);
            setUpReader();
        }
    }

    private class IndentChange implements Utilities.DemarkAction {
        @Override
        public void action(int spared) {
            changeIndentSize(spared);
            setUpReader();
        }
    }

    private class ReaderChange implements Utilities.DemarkAction {
        @Override
        public void action(int spared) {
            readerType = spared;
            setReaderType(novelID, spared);
            setUpReader();
        }
    }

}
