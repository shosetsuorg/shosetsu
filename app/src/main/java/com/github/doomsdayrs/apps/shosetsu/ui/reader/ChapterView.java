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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.ErrorView;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.google.android.material.chip.Chip;

/**
 * shosetsu
 * 05 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class ChapterView extends Fragment {
    public ScrollView scrollView;
    public ProgressBar progressBar;
    public String title, chapterURL, unformattedText = null, text = null;
    public int chapterID;
    public ErrorView errorView;
    protected boolean bookmarked;
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
        View view = inflater.inflate(R.layout.chapter_reader_view, container, false);
        if (savedInstanceState != null) {
            unformattedText = savedInstanceState.getString("unformattedText");
            title = savedInstanceState.getString("title");
            chapterID = savedInstanceState.getInt("chapterID");
            chapterURL = savedInstanceState.getString("chapterURL");
            text = savedInstanceState.getString("text");
        }


        bookmarked = Database.DatabaseChapter.isBookMarked(chapterID);
        if (bookmarked)
            chapterReader.bookmark.setIcon(R.drawable.ic_bookmark_black_24dp);
        else chapterReader.bookmark.setIcon(R.drawable.ic_bookmark_border_black_24dp);


        return view;
    }
}
