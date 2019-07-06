package com.github.doomsdayrs.apps.shosetsu.ui.viewholders;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.settings.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapterView;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.variables.download.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class ChaptersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public NovelChapter novelChapter;
    public final ImageView moreOptions;

    public final TextView library_card_title;
    public final TextView status;
    public final TextView read;
    public final TextView readTag;
    public final TextView downloadTag;


    public PopupMenu popupMenu;


    public boolean downloaded;

    public NovelFragmentChapters novelFragmentChapters;

    public ChaptersViewHolder(@NonNull View itemView) {
        super(itemView);
        library_card_title = itemView.findViewById(R.id.recycler_novel_chapter_title);
        moreOptions = itemView.findViewById(R.id.recycler_novel_chapter_options);
        status = itemView.findViewById(R.id.recycler_novel_chapter_status);
        read = itemView.findViewById(R.id.recycler_novel_chapter_read);
        readTag = itemView.findViewById(R.id.recycler_novel_chapter_read_tag);
        downloadTag = itemView.findViewById(R.id.recycler_novel_chapter_download);
        itemView.setOnClickListener(this);
        moreOptions.setOnClickListener(this::showPopup);
        if (popupMenu == null) {
            popupMenu = new PopupMenu(moreOptions.getContext(), moreOptions);
            if (popupMenu.getMenu().size()<=0){
                popupMenu.getMenu().add(1, R.id.popup_chapter_menu_bookmark, 1, R.string.bookmark);
                popupMenu.getMenu().add(1, R.id.popup_chapter_menu_download, 2, R.string.download);
            }
        }
    }

    private void showPopup(View itemView) {
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.popup_chapter_menu_bookmark:
                    if (SettingsController.toggleBookmarkChapter(novelChapter.link))
                        library_card_title.setTextColor(itemView.getResources().getColor(R.color.bookmarked));
                    else
                        library_card_title.setTextColor(itemView.getResources().getColor(R.color.black));
                    return true;
                case R.id.popup_chapter_menu_download:
                    if (!downloaded) {
                        DownloadItem downloadItem = new DownloadItem(StaticNovel.formatter, StaticNovel.novelPage.title, novelChapter.chapterNum, StaticNovel.novelURL, novelChapter.link);
                        Download_Manager.addToDownload(downloadItem);
                        downloaded = true;
                    } else {
                        if (Download_Manager.delete(itemView.getContext(), new DownloadItem(StaticNovel.formatter, StaticNovel.novelPage.title, novelChapter.chapterNum, StaticNovel.novelURL, novelChapter.link))) {
                            downloadTag.setVisibility(View.INVISIBLE);
                            downloaded = false;
                        }
                    }
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.inflate(R.menu.popup_chapter_menu);
        popupMenu.show();
    }

    @Override
    public void onClick(View v) {
        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READING);
        Intent intent = new Intent(novelFragmentChapters.getActivity(), NovelFragmentChapterView.class);
        intent.putExtra("title", novelChapter.chapterNum);
        intent.putExtra("chapterURL", novelChapter.link);
        intent.putExtra("novelURL", StaticNovel.novelURL);
        intent.putExtra("formatter", StaticNovel.formatter.getID());
        intent.putExtra("downloaded", downloaded);
        novelFragmentChapters.startActivity(intent);
    }
}
