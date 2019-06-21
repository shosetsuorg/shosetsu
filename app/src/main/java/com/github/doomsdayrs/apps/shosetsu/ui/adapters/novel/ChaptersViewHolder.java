package com.github.doomsdayrs.apps.shosetsu.ui.adapters.novel;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.settings.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapterView;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.variables.download.DeleteItem;
import com.github.doomsdayrs.apps.shosetsu.variables.download.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import org.json.JSONException;
import org.json.JSONObject;

/**
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

    NovelChapter novelChapter;
    final TextView library_card_title;
    final ImageView bookmarked;
    final TextView status;
    final TextView read;
    final TextView readTag;
    public final ImageView download;
    boolean downloaded;

    NovelFragmentChapters novelFragmentChapters;

    ChaptersViewHolder(@NonNull View itemView) {
        super(itemView);
        library_card_title = itemView.findViewById(R.id.recycler_novel_chapter_title);
        bookmarked = itemView.findViewById(R.id.recycler_novel_chapter_bookmarked);
        download = itemView.findViewById(R.id.recycler_novel_chapter_download);
        status = itemView.findViewById(R.id.recycler_novel_chapter_status);
        read = itemView.findViewById(R.id.recycler_novel_chapter_read);
        readTag = itemView.findViewById(R.id.recycler_novel_chapter_read_tag);


        itemView.setOnClickListener(this);
        download.setOnClickListener(v -> {
            if (!downloaded) {
                DownloadItem downloadItem = new DownloadItem(NovelChaptersAdapter.formatter, StaticNovel.novelPage.title, novelChapter.chapterNum, novelFragmentChapters.novelURL, novelChapter.link, novelFragmentChapters);
                Download_Manager.addToDownload(downloadItem);
                downloaded = true;
            } else {
                if (Download_Manager.delete(itemView.getContext(),new DeleteItem(NovelChaptersAdapter.formatter, StaticNovel.novelPage.title, novelChapter.chapterNum, novelFragmentChapters.novelURL, novelChapter.link)))
                    download.setImageResource(R.drawable.ic_outline_arrow_drop_down_circle_24px);
                downloaded = false;
            }

        });

        bookmarked.setOnClickListener(v -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("y", 0);
                if (SettingsController.toggleBookmarkChapter(novelChapter.link))
                    bookmarked.setImageResource(R.drawable.ic_bookmark_black_24dp);
                else
                    bookmarked.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Database.setChapterStatus(novelChapter.link, Status.READING);
        Intent intent = new Intent(novelFragmentChapters.getActivity(), NovelFragmentChapterView.class);
        intent.putExtra("title", novelChapter.chapterNum);
        intent.putExtra("url", novelChapter.link);
        intent.putExtra("novelURL", novelFragmentChapters.novelURL);
        intent.putExtra("formatter", NovelChaptersAdapter.formatter.getID());
        intent.putExtra("downloaded", downloaded);
        novelFragmentChapters.startActivity(intent);
    }
}
