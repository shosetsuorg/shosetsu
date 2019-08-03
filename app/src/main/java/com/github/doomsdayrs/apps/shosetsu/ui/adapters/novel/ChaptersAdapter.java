package com.github.doomsdayrs.apps.shosetsu.ui.adapters.novel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.ui.viewholders.ChaptersViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersViewHolder> {
    public static int DefaultTextColor;
    public static boolean set = false;
    private final NovelFragmentChapters novelFragmentChapters;


    public ChaptersAdapter(NovelFragmentChapters novelFragmentChapters) {
        this.novelFragmentChapters = novelFragmentChapters;
    }


    @NonNull
    @Override
    public ChaptersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_novel_chapter, viewGroup, false);
        ChaptersViewHolder chaptersViewHolder = new ChaptersViewHolder(view);
        if (!set) {
            DefaultTextColor = chaptersViewHolder.library_card_title.getCurrentTextColor();
            Log.i("TextDefaultColor", String.valueOf(DefaultTextColor));
            set = !set;
        }
        return chaptersViewHolder;
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
            Log.i("TextDefaultColor", String.valueOf(DefaultTextColor));
            chaptersViewHolder.popupMenu.getMenu().findItem(R.id.popup_chapter_menu_bookmark).setTitle("UnBookmark");
        } else {
            chaptersViewHolder.popupMenu.getMenu().findItem(R.id.popup_chapter_menu_bookmark).setTitle("Bookmark");
        }


        if (novelFragmentChapters.contains(novelChapter)) {
            Log.d("SelectedStatus", "Novel Selected: " + novelChapter.link);
            chaptersViewHolder.cardView.setStrokeWidth(Utilities.SELECTED_STROKE_WIDTH);
            chaptersViewHolder.checkBox.setChecked(true);
        } else {
            chaptersViewHolder.cardView.setStrokeWidth(0);
            chaptersViewHolder.checkBox.setChecked(false);
        }

        if (novelFragmentChapters.selectedChapters.size() > 0) {
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
                chaptersViewHolder.readTag.setVisibility(View.GONE);
                chaptersViewHolder.read.setVisibility(View.GONE);
                break;

            case UNREAD:
                chaptersViewHolder.status.setText(Status.UNREAD.getStatus());
                break;
        }

        if (novelFragmentChapters.selectedChapters.size() <= 0)
            chaptersViewHolder.itemView.setOnClickListener(chaptersViewHolder);
        else
            chaptersViewHolder.itemView.setOnClickListener(view -> chaptersViewHolder.addToSelect());
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
