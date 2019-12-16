package com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders;
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

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.async.NewChapterReaderViewLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.NovelFragmentChapterViewHideBar;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.google.android.material.chip.Chip;

import java.util.Objects;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterURLFromChapterID;

/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NewChapterView extends Fragment {
    public NewChapterReader newChapterReader;

    public String CHAPTER_URL;
    public int CHAPTER_ID;

    public ScrollView scrollView;
    public boolean bookmarked;
    public Chip nextChapter;
    //public View coverView;
    // public ViewPager2 viewPager2;
    //public NewReader currentReader;
    private TextView textView;


    public boolean ready = false;
    public String unformattedText;
    public String text;


    @SuppressLint("ClickableViewAccessibility")
    public NewChapterView() {
        //viewPager2 = itemView.findViewById(R.id.viewpager);
        //coverView = itemView.findViewById(R.id.viewCover);
        //coverView.setOnTouchListener((view, motionEvent) -> true);
    }


    public void setNewChapterReader(NewChapterReader newChapterReader) {
        this.newChapterReader = newChapterReader;
    }

    public void setCHAPTER_ID(int CHAPTER_ID) {
        this.CHAPTER_ID = CHAPTER_ID;
        this.CHAPTER_URL = getChapterURLFromChapterID(CHAPTER_ID);
    }

    private void updateParent() {
        newChapterReader.currentView = this;
        newChapterReader.updateBookmark();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateParent();
        String title = Database.DatabaseChapter.getTitle(CHAPTER_ID);
        Log.i("Setting TITLE", title);
        newChapterReader.toolbar.setTitle(title);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("id", CHAPTER_ID);
        outState.putString("url", CHAPTER_URL);
        outState.putString("text", text);
        outState.putString("unform", unformattedText);
        outState.putBoolean("book", bookmarked);
        outState.putBoolean("ready", ready);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_chapter_view, container, false);
        if (savedInstanceState != null) {
            CHAPTER_ID = savedInstanceState.getInt("id");
            CHAPTER_URL = savedInstanceState.getString("url");
            newChapterReader = (NewChapterReader) getActivity();
            unformattedText = savedInstanceState.getString("unfom");
            text = savedInstanceState.getString("text");
            bookmarked = savedInstanceState.getBoolean("book");
            ready = savedInstanceState.getBoolean("ready");
        }
        scrollView = view.findViewById(R.id.scrollView);
        addBottomListener();

        textView = view.findViewById(R.id.textView);
        textView.setOnClickListener(new NovelFragmentChapterViewHideBar(newChapterReader.toolbar));
        textView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        textView.setTextColor(Settings.ReaderTextColor);
        textView.setTextSize(Settings.ReaderTextSize);

        nextChapter = view.findViewById(R.id.next_chapter);

        nextChapter.setOnClickListener(view1 -> {
            int position = newChapterReader.findCurrentPosition(this.CHAPTER_ID);
            if (position + 1 < newChapterReader.chapterIDs.length) {
                nextChapter.setVisibility(View.GONE);
                newChapterReader.viewPager.setCurrentItem(position + 1);
            } else
                Toast.makeText(newChapterReader.getApplicationContext(), "No more chapters!", Toast.LENGTH_SHORT).show();
            //   Toast.makeText(newChapterReader.getApplicationContext(), "Cannot move to next chapter, Please exit reader", Toast.LENGTH_LONG).show();
        });

        updateParent();


        //holder.viewPager2.setUserInputEnabled(false);
        //NewChapterReaderTypeAdapter newChapterReaderTypeAdapter = new NewChapterReaderTypeAdapter(newChapterReader);
        //holder.viewPager2.setAdapter(newChapterReaderTypeAdapter);
        //holder.viewPager2.setCurrentItem(getReaderType(newChapterReader.novelID));

        Log.i("Loading chapter", CHAPTER_URL);
        ready = false;
        if (savedInstanceState == null) {
            if (Database.DatabaseChapter.isSaved(CHAPTER_ID)) {
                unformattedText = Objects.requireNonNull(Database.DatabaseChapter.getSavedNovelPassage(CHAPTER_ID));
                setUpReader();
                scrollView.post(() -> scrollView.scrollTo(0, Database.DatabaseChapter.getY(CHAPTER_ID)));
                ready = true;
            } else {
                unformattedText = "";
                setUpReader();
                new NewChapterReaderViewLoader(this).execute();
            }
        } else {
            setUpReader();
        }
        Database.DatabaseChapter.setChapterStatus(CHAPTER_ID, Status.READING);
        return view;
    }

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
            if (text.length() > 100)
                Log.d("TextSet", text.substring(0, 100).replace("\n", "\\n"));
            else if (text.length() > 0)
                Log.d("TextSet", text.substring(0, text.length() - 1).replace("\n", "\\n"));
            textView.setText(text);
            // viewPager2.post(() -> currentReader.setText(text));
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

                    if (Database.DatabaseChapter.getStatus(CHAPTER_ID) != Status.READ)
                        Database.DatabaseChapter.updateY(CHAPTER_ID, y);
                }
            } else {
                Log.i("Scroll", "Marking chapter as READ");
                Database.DatabaseChapter.setChapterStatus(CHAPTER_ID, Status.READ);
                Database.DatabaseChapter.updateY(CHAPTER_ID, 0);
                nextChapter.setVisibility(View.VISIBLE);
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
}
