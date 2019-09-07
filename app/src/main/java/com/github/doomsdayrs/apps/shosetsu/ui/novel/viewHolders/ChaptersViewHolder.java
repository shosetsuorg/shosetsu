package com.github.doomsdayrs.apps.shosetsu.ui.novel.viewHolders;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.ChaptersAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.google.android.material.card.MaterialCardView;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openChapter;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInBrowser;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openInWebview;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleBookmarkChapter;

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * Shosetsu
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class ChaptersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public NovelChapter novelChapter;

    private final ImageView moreOptions;
    public final MaterialCardView cardView;
    public final ConstraintLayout constraintLayout;
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
            constraintLayout = itemView.findViewById(R.id.constraint);
            library_card_title = itemView.findViewById(R.id.recycler_novel_chapter_title);
            moreOptions = itemView.findViewById(R.id.more_options);
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
                    if (toggleBookmarkChapter(novelChapter.link))
                        library_card_title.setTextColor(itemView.getResources().getColor(R.color.bookmarked));
                    else {
                        Log.i("SetDefault", String.valueOf(ChaptersAdapter.DefaultTextColor));

                        library_card_title.setTextColor(ChaptersAdapter.DefaultTextColor);
                    }
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
                case R.id.browser:
                    if (novelFragmentChapters.getActivity() != null)
                        openInBrowser(novelFragmentChapters.getActivity(), novelChapter.link);
                    return true;
                case R.id.webview:
                    if (novelFragmentChapters.getActivity() != null)
                        openInWebview(novelFragmentChapters.getActivity(), novelChapter.link);
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
        openChapter(novelFragmentChapters.getActivity(), novelChapter, StaticNovel.novelURL, StaticNovel.formatter.getID());
    }
}
