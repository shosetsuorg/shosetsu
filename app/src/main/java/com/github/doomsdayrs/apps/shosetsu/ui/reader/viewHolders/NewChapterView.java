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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

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

import java.util.Objects;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterURLFromChapterID;

/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NewChapterView extends Fragment {
    public final NewChapterReader newChapterReader;

    public final String CHAPTER_URL;
    public final int CHAPTER_ID;

    public ScrollView scrollView;
    public boolean bookmarked;
    //public View coverView;
    // public ViewPager2 viewPager2;
    //public NewReader currentReader;
    private TextView textView;


    public boolean ready;
    public String unformattedText;
    public String text;


    @SuppressLint("ClickableViewAccessibility")
    public NewChapterView(NewChapterReader newChapterReader, int chapter_id) {
        this.newChapterReader = newChapterReader;
        //viewPager2 = itemView.findViewById(R.id.viewpager);
        //coverView = itemView.findViewById(R.id.viewCover);
        //coverView.setOnTouchListener((view, motionEvent) -> true);
        CHAPTER_ID = chapter_id;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_chapter_view, container, false);

        scrollView = view.findViewById(R.id.scrollView);
        textView = view.findViewById(R.id.textView);
        textView.setOnClickListener(new NovelFragmentChapterViewHideBar(newChapterReader.toolbar));
        textView.setBackgroundColor(Settings.ReaderTextBackgroundColor);
        textView.setTextColor(Settings.ReaderTextColor);
        textView.setTextSize(Settings.ReaderTextSize);

        updateParent();


        //holder.viewPager2.setUserInputEnabled(false);
        //NewChapterReaderTypeAdapter newChapterReaderTypeAdapter = new NewChapterReaderTypeAdapter(newChapterReader);
        //holder.viewPager2.setAdapter(newChapterReaderTypeAdapter);
        //holder.viewPager2.setCurrentItem(getReaderType(newChapterReader.novelID));

        Log.i("Loading chapter", CHAPTER_URL);
        ready = false;
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
}
