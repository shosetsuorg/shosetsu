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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.NewChapterView;

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
        return new NewChapterView(view);
    }


    @Override
    public void onBindViewHolder(@NonNull NewChapterView holder, int position) {
        holder.textView.setText("ChapterID: " + newChapterReader.chapterIDs[position]);
    }


    @Override
    public int getItemCount() {
        return newChapterReader.chapterIDs.length;
    }
}
