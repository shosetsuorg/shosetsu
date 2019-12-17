package com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters;
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

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewChapterView;

import java.util.ArrayList;

/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NewChapterReaderAdapter extends FragmentPagerAdapter {
    private final NewChapterReader newChapterReader;
    private ArrayList<NewChapterView> chapterViews = new ArrayList<>();

    public NewChapterReaderAdapter(@NonNull FragmentManager fm, int behavior, NewChapterReader newChapterReader) {
        super(fm, behavior);
        this.newChapterReader = newChapterReader;
        for (int i : newChapterReader.chapterIDs) {
            NewChapterView newChapterView = new NewChapterView();
            newChapterView.setChapterID(i);
            newChapterView.setNewChapterReader(newChapterReader);
            chapterViews.add(newChapterView);
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return chapterViews.get(position);
    }

    @Override
    public int getCount() {
        Log.i("size", String.valueOf(newChapterReader.chapterIDs.length));
        return newChapterReader.chapterIDs.length;
    }
}
