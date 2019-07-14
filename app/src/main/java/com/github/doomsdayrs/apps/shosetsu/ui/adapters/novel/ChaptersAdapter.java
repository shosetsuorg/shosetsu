package com.github.doomsdayrs.apps.shosetsu.ui.adapters.novel;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.ui.viewholders.ChaptersViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
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
public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersViewHolder> {


    private final NovelFragmentChapters novelFragmentChapters;


    public ChaptersAdapter(NovelFragmentChapters novelFragmentChapters) {
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

        if (Database.DatabaseChapter.isBookMarked(novelChapter.link)) {
            chaptersViewHolder.library_card_title.setTextColor(chaptersViewHolder.itemView.getResources().getColor(R.color.bookmarked));
            chaptersViewHolder.popupMenu.getMenu().findItem(R.id.popup_chapter_menu_bookmark).setTitle("UnBookmark");
        } else
            chaptersViewHolder.popupMenu.getMenu().findItem(R.id.popup_chapter_menu_bookmark).setTitle("Bookmark");


        if (NovelFragmentChapters.contains(novelChapter)) {
            Log.d("SelectedStatus", "Novel Selected: " + novelChapter.link);
            chaptersViewHolder.cardView.setStrokeColor(Color.BLUE);
            chaptersViewHolder.checkBox.setChecked(true);
        } else {
            chaptersViewHolder.cardView.setStrokeColor(Color.WHITE);
            chaptersViewHolder.checkBox.setChecked(false);
        }

        if (NovelFragmentChapters.selectedChapters.size() > 0) {
            chaptersViewHolder.checkBox.setVisibility(View.VISIBLE);
        } else chaptersViewHolder.checkBox.setVisibility(View.GONE);


        if (Database.DatabaseChapter.isSaved(novelChapter.link)) {
            Log.d("In Storage", StaticNovel.novelURL + " " + novelChapter.link + " " + i);
            chaptersViewHolder.downloadTag.setVisibility(View.VISIBLE);
            chaptersViewHolder.popupMenu.getMenu().findItem(R.id.popup_chapter_menu_download).setTitle("Delete");
        } else {
            chaptersViewHolder.popupMenu.getMenu().findItem(R.id.popup_chapter_menu_download).setTitle("Download");
            chaptersViewHolder.downloadTag.setVisibility(View.INVISIBLE);
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

        if (NovelFragmentChapters.selectedChapters.size() <= 0)
            chaptersViewHolder.itemView.setOnClickListener(chaptersViewHolder);
        else chaptersViewHolder.itemView.setOnClickListener(view -> chaptersViewHolder.addToSelect());
    }

    @Override
    public int getItemCount() {
        if (StaticNovel.novelChapters != null)
            return StaticNovel.novelChapters.size();
        else return 0;
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
