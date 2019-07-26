package com.github.doomsdayrs.apps.shosetsu.ui.viewholders;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapterReader;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.google.android.material.card.MaterialCardView;

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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 * @author github.com/hXtreme
 */
public class ChaptersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public NovelChapter novelChapter;

    public final ImageView moreOptions;
    public final MaterialCardView cardView;
    public final CheckBox checkBox;
    public final TextView library_card_title;
    public final TextView status;
    public final TextView read;
    public final TextView readTag;
    public final TextView downloadTag;


    public PopupMenu popupMenu;

    public NovelFragmentChapters novelFragmentChapters;

    public ChaptersViewHolder(@NonNull View itemView) {
        super(itemView);
        {
            cardView = itemView.findViewById(R.id.recycler_novel_chapter_card);
            checkBox = itemView.findViewById(R.id.recycler_novel_chapter_selectCheck);
            library_card_title = itemView.findViewById(R.id.recycler_novel_chapter_title);
            moreOptions = itemView.findViewById(R.id.recycler_novel_chapter_options);
            status = itemView.findViewById(R.id.recycler_novel_chapter_status);
            read = itemView.findViewById(R.id.recycler_novel_chapter_read);
            readTag = itemView.findViewById(R.id.recycler_novel_chapter_read_tag);
            downloadTag = itemView.findViewById(R.id.recycler_novel_chapter_download);
        }

        if (popupMenu == null) {
            popupMenu = new PopupMenu(moreOptions.getContext(), moreOptions);
            popupMenu.inflate(R.menu.popup_chapter_menu);
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.popup_chapter_menu_bookmark:
                    if (SettingsController.INSTANCE.toggleBookmarkChapter(novelChapter.link))
                        library_card_title.setTextColor(itemView.getResources().getColor(R.color.bookmarked));
                    else
                        library_card_title.setTextColor(itemView.getResources().getColor(R.color.design_default_color_surface));
                    NovelFragmentChapters.adapter.notifyDataSetChanged();
                    return true;
                case R.id.popup_chapter_menu_download:
                    if (!Database.DatabaseChapter.isSaved(novelChapter.link)) {
                        DownloadItem downloadItem = new DownloadItem(StaticNovel.formatter, StaticNovel.novelPage.title, novelChapter.chapterNum, StaticNovel.novelURL, novelChapter.link);
                        Download_Manager.addToDownload(downloadItem);
                    } else {
                        if (Download_Manager.delete(itemView.getContext(), new DownloadItem(StaticNovel.formatter, StaticNovel.novelPage.title, novelChapter.chapterNum, StaticNovel.novelURL, novelChapter.link))) {
                            downloadTag.setVisibility(View.INVISIBLE);
                        }
                    }
                    NovelFragmentChapters.adapter.notifyDataSetChanged();
                    return true;

                case R.id.popup_chapter_menu_mark_read:
                    Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READ);
                    NovelFragmentChapters.adapter.notifyDataSetChanged();
                    return true;
                case R.id.popup_chapter_menu_mark_unread:
                    Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.UNREAD);
                    NovelFragmentChapters.adapter.notifyDataSetChanged();
                    return true;
                case R.id.popup_chapter_menu_mark_reading:
                    Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READING);
                    NovelFragmentChapters.adapter.notifyDataSetChanged();
                    return true;
                default:
                    return false;
            }
        });


        itemView.setOnLongClickListener(view -> {
            addToSelect();
            return true;
        });
        moreOptions.setOnClickListener(view -> popupMenu.show());
        checkBox.setOnClickListener(view -> addToSelect());
    }

    public void addToSelect() {
        if (!novelFragmentChapters.contains(novelChapter))
            novelFragmentChapters.selectedChapters.add(novelChapter);
        else
            removeFromSelect();
        if (novelFragmentChapters.selectedChapters.size() == 1 || novelFragmentChapters.selectedChapters.size() <= 0)
            novelFragmentChapters.onCreateOptionsMenu(novelFragmentChapters.menu, novelFragmentChapters.getInflater());
        NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
    }

    private void removeFromSelect() {
        if (novelFragmentChapters.contains(novelChapter))
            for (int x = 0; x < novelFragmentChapters.selectedChapters.size(); x++)
                if (novelFragmentChapters.selectedChapters.get(x).link.equalsIgnoreCase(novelChapter.link)) {
                    novelFragmentChapters.selectedChapters.remove(x);
                    return;
                }
    }

    @Override
    public void onClick(View v) {
        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READING);
        Intent intent = new Intent(novelFragmentChapters.getActivity(), NovelFragmentChapterReader.class);
        intent.putExtra("title", novelChapter.chapterNum);
        intent.putExtra("chapterURL", novelChapter.link);
        intent.putExtra("novelURL", StaticNovel.novelURL);
        intent.putExtra("formatter", StaticNovel.formatter.getID());
        novelFragmentChapters.startActivity(intent);
    }
}
