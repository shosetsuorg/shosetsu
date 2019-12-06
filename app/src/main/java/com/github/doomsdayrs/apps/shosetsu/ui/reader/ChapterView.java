package com.github.doomsdayrs.apps.shosetsu.ui.reader;
/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.ErrorView;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.ReaderTypeAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.async.ReaderViewLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.readers.Reader;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Objects;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isTapToScroll;
import static com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment.getNextChapter;

/**
 * shosetsu
 * 05 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class ChapterView extends Fragment {
    private ViewPager readerViewPager;
    private final ArrayList<Reader> fragments = new ArrayList<>();


    public ScrollView scrollView;
    public ProgressBar progressBar;
    public String title, chapterURL, unformattedText = null, text = null;
    public int chapterID;
    public ErrorView errorView;
    protected boolean bookmarked, ready = false;

    ChapterReader chapterReader;
    private Chip nextChapter;
    //Tap to scroll
    @SuppressWarnings("FieldCanBeLocal")
    private View scroll_up, scroll_down;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("unformattedText", text);
        outState.putString("text", text);
        outState.putInt("chapterID", chapterID);
        outState.putString("chapterURL", chapterURL);
        outState.putString("title", title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View chapterView = inflater.inflate(R.layout.chapter_reader_view, container, false);
        if (savedInstanceState != null) {
            unformattedText = savedInstanceState.getString("unformattedText");
            title = savedInstanceState.getString("title");
            chapterID = savedInstanceState.getInt("chapterID");
            chapterURL = savedInstanceState.getString("chapterURL");
            text = savedInstanceState.getString("text");
        }

        errorView = new ErrorView(chapterReader, chapterView.findViewById(R.id.network_error), chapterView.findViewById(R.id.error_message), chapterView.findViewById(R.id.error_button));
        progressBar = chapterView.findViewById(R.id.fragment_novel_chapter_view_progress);
        scrollView = chapterView.findViewById(R.id.fragment_novel_scroll);
        readerViewPager = chapterView.findViewById(R.id.readerPager);
        scroll_up = chapterView.findViewById(R.id.scroll_up);
        scroll_down = chapterView.findViewById(R.id.scroll_down);
        nextChapter = chapterView.findViewById(R.id.next_chapter);

        bookmarked = Database.DatabaseChapter.isBookMarked(chapterID);
        if (bookmarked)
            chapterReader.bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);
        else chapterReader.bookmark.setIcon(R.drawable.ic_bookmark_border_black_24dp);


        addBottomListener();
        setViewPager();

        // Scroll up listener
        scroll_up.setOnClickListener(view -> scrollUp());
        // Scroll down listener
        scroll_down.setOnClickListener(view -> scrollDown());
        nextChapter.setOnClickListener(view -> {
            NovelChapter novelChapter = getNextChapter(chapterURL, chapterReader.chapterURLs);
            if (novelChapter != null) {
                if (!novelChapter.link.equalsIgnoreCase(chapterURL)) {
                    title = novelChapter.title;
                    chapterURL = novelChapter.link;
                    loadChapter();
                } else
                    Toast.makeText(chapterReader.getApplicationContext(), "No more chapters!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(chapterReader.getApplicationContext(), "Cannot move to next chapter, Please exit reader", Toast.LENGTH_LONG).show();
            }
            nextChapter.setVisibility(View.GONE);
        });
        loadChapter();

        return chapterView;
    }


    /**
     * Changes the theme of the reader
     * TODO change the scroll position bars color
     */
    public void setUpReader() {
        scrollView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        if (unformattedText != null) {
            StringBuilder replaceSpacing = new StringBuilder("\n");
            for (int x = 0; x < Settings.paragraphSpacing; x++)
                replaceSpacing.append("\n");

            for (int x = 0; x < Settings.indentSize; x++)
                replaceSpacing.append("\t");

            text = unformattedText.replaceAll("\n", replaceSpacing.toString());
            assert selectedReader.getView() != null;
            selectedReader.getView().post(() -> selectedReader.setText(text));
        }
    }

    /**
     * Loads the chapter to be read
     */
    private void loadChapter() {
        ready = false;
        if (Database.DatabaseChapter.isSaved(chapterID)) {
            unformattedText = Objects.requireNonNull(Database.DatabaseChapter.getSavedNovelPassage(chapterID));
            setUpReader();
            scrollView.post(() -> scrollView.scrollTo(0, Database.DatabaseChapter.getY(chapterID)));
            if (chapterReader.getSupportActionBar() != null)
                chapterReader.getSupportActionBar().setTitle(title);
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
     * What to do when scroll hits bottom
     */
    private void bottom() {
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
     * Sets up the hitting bottom listener
     */
    private void addBottomListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> bottom());
        } else {
            scrollView.getViewTreeObserver().addOnScrollChangedListener(this::bottom);
        }
    }

    private void setViewPager() {
        ReaderTypeAdapter pagerAdapter = new ReaderTypeAdapter(getChildFragmentManager(), fragments);
        readerViewPager.setAdapter(pagerAdapter);
    }
}
