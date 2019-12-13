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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.async.NewChapterReaderViewLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewChapterView;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import java.util.Objects;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterURLFromChapterID;
import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.getReaderType;

/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NewChapterReaderAdapter extends RecyclerView.Adapter<NewChapterView> {
    final NewChapterReader newChapterReader;

    public NewChapterReaderAdapter(NewChapterReader newChapterReader) {
        this.newChapterReader = newChapterReader;
    }

    @NonNull
    @Override
    public NewChapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_chapter_view, parent, false);
        return new NewChapterView(newChapterReader, view);
    }


    @Override
    public void onBindViewHolder(@NonNull NewChapterView holder, int position) {
        newChapterReader.currentView = holder;

        final int CHAPTER_ID = newChapterReader.chapterIDs[position];

        holder.setChapterID(CHAPTER_ID);
        holder.setChapterURL(getChapterURLFromChapterID(CHAPTER_ID));

        holder.viewPager2.setUserInputEnabled(false);
        NewChapterReaderTypeAdapter newChapterReaderTypeAdapter = new NewChapterReaderTypeAdapter(newChapterReader);
        holder.viewPager2.setAdapter(newChapterReaderTypeAdapter);
        holder.viewPager2.setCurrentItem(getReaderType(newChapterReader.novelID));

        Log.i("Loading chapter", holder.chapterURL);

        holder.ready = false;
        if (Database.DatabaseChapter.isSaved(CHAPTER_ID)) {
            holder.unformattedText = Objects.requireNonNull(Database.DatabaseChapter.getSavedNovelPassage(CHAPTER_ID));
            holder.setUpReader();
            holder.scrollView.post(() -> holder.scrollView.scrollTo(0, Database.DatabaseChapter.getY(holder.chapterID)));
            //if (chapterReader.getSupportActionBar() != null)
            //   chapterReader.getSupportActionBar().setTitle(title);
            holder.ready = true;
        } else if (holder.chapterURL != null) {
            holder.unformattedText = "";
            holder.setUpReader();
            new NewChapterReaderViewLoader(holder).execute();
        }

        Database.DatabaseChapter.setChapterStatus(CHAPTER_ID, Status.READING);
    }


    @Override
    public int getItemCount() {
        return newChapterReader.chapterIDs.length;
    }
}
