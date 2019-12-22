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
import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewMarkdownReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewReader;
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewTextReader;

/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class NewChapterReaderTypeAdapter extends RecyclerView.Adapter<NewReader> {
    private final NewChapterReader newChapterReader;

    public NewChapterReaderTypeAdapter(NewChapterReader newChapterReader) {
        this.newChapterReader = newChapterReader;
    }

    @NonNull
    @Override
    public NewReader onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        NewReader newReader;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_reader_text_view, parent, false);
                newReader = new NewTextReader(view, newChapterReader);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chapter_reader_mark_down, parent, false);
                newReader = new NewMarkdownReader(view, newChapterReader);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return newReader;
    }


    @Override
    public void onBindViewHolder(@NonNull NewReader holder, int position) {
        Log.i("LoadingReader", String.valueOf(position));
        //newChapterReader.currentView.currentReader = holder;
        holder.bind();
        newChapterReader.currentView.setUpReader();
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
