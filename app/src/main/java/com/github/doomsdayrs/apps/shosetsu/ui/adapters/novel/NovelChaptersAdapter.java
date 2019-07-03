package com.github.doomsdayrs.apps.shosetsu.ui.adapters.novel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.ui.viewholders.ChaptersViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import java.util.List;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see https://www.gnu.org/licenses/ .
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelChaptersAdapter extends RecyclerView.Adapter<ChaptersViewHolder> {


    private final NovelFragmentChapters novelFragmentChapters;



    public NovelChaptersAdapter(NovelFragmentChapters novelFragmentChapters) {
        this.novelFragmentChapters = novelFragmentChapters;
    }


    @NonNull
    @Override
    public ChaptersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_novel_chapter, viewGroup, false);
        return new ChaptersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChaptersViewHolder chaptersViewHolder, int i) {
        NovelChapter novelChapter = StaticNovel.novelChapters.get(i);

        chaptersViewHolder.novelChapter = novelChapter;
        chaptersViewHolder.library_card_title.setText(novelChapter.chapterNum);
        chaptersViewHolder.novelFragmentChapters = novelFragmentChapters;

        if (!Database.DatabaseChapter.inChapters(novelChapter.link))
            Database.DatabaseChapter.addToChapters(StaticNovel.novelURL, novelChapter);

        if (Database.DatabaseChapter.isBookMarked(novelChapter.link))
            chaptersViewHolder.bookmarked.setImageResource(R.drawable.ic_bookmark_black_24dp);

        if (Database.DatabaseChapter.isSaved(novelChapter.link)) {
            Log.d("In Storage", StaticNovel.novelURL + " " + novelChapter.link + " " + i);
            chaptersViewHolder.download.setImageResource(R.drawable.ic_arrow_drop_down_circle_black_24dp);
            chaptersViewHolder.downloaded = true;
        }

        switch (Database.DatabaseChapter.getStatus(novelChapter.link)) {
            case READING:
                chaptersViewHolder.status.setText(Status.READING.getStatus());
                chaptersViewHolder.readTag.setVisibility(View.VISIBLE);
                chaptersViewHolder.read.setVisibility(View.VISIBLE);
                chaptersViewHolder.read.setText(String.valueOf(Database.DatabaseChapter.getY(novelChapter.link)));
                break;

            case READ:
                chaptersViewHolder.status.setText(Status.READ.getStatus());
                break;

            case UNREAD:
                chaptersViewHolder.status.setText(Status.UNREAD.getStatus());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return StaticNovel.novelChapters.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
