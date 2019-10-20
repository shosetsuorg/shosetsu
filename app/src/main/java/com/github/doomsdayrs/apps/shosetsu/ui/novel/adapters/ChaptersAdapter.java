package com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.viewHolders.ChaptersViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersViewHolder> {
    public static int DefaultTextColor;
    private static boolean set = false;
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
        chaptersViewHolder.library_card_title.setText(novelChapter.title);
        chaptersViewHolder.novelFragmentChapters = novelFragmentChapters;

        //TODO The getNovelID in this method likely will cause slowdowns due to IO
        if (!Database.DatabaseChapter.inChapters(novelChapter.link))
            Database.DatabaseChapter.addToChapters(getNovelIDFromNovelURL(StaticNovel.novelURL), novelChapter);

        if (Database.DatabaseChapter.isBookMarked(novelChapter.link)) {
            chaptersViewHolder.library_card_title.setTextColor(chaptersViewHolder.itemView.getResources().getColor(R.color.bookmarked));
            chaptersViewHolder.popupMenu.getMenu().findItem(R.id.popup_chapter_menu_bookmark).setTitle("UnBookmark");
        } else {
            chaptersViewHolder.popupMenu.getMenu().findItem(R.id.popup_chapter_menu_bookmark).setTitle("Bookmark");
        }


        if (novelFragmentChapters.contains(novelChapter)) {
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
            chaptersViewHolder.downloadTag.setVisibility(View.VISIBLE);
            chaptersViewHolder.popupMenu.getMenu().findItem(R.id.popup_chapter_menu_download).setTitle("Delete");
        } else {
            chaptersViewHolder.popupMenu.getMenu().findItem(R.id.popup_chapter_menu_download).setTitle("Download");
            chaptersViewHolder.downloadTag.setVisibility(View.INVISIBLE);
        }

        switch (Database.DatabaseChapter.getStatus(novelChapter.link)) {

            case READING:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    chaptersViewHolder.constraintLayout.setForeground(new ColorDrawable());
                } else {
                    //TODO Tint for cards before 22
                }
                chaptersViewHolder.status.setText(Status.READING.getStatus());
                chaptersViewHolder.readTag.setVisibility(View.VISIBLE);
                chaptersViewHolder.read.setVisibility(View.VISIBLE);
                chaptersViewHolder.read.setText(String.valueOf(Database.DatabaseChapter.getY(novelChapter.link)));
                break;
            case UNREAD:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    chaptersViewHolder.constraintLayout.setForeground(new ColorDrawable());
                } else {
                    //TODO Tint for cards before 22

                }
                chaptersViewHolder.status.setText(Status.UNREAD.getStatus());
                break;

            case READ:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (novelFragmentChapters.getContext() != null)
                        chaptersViewHolder.constraintLayout.setForeground(new ColorDrawable(ContextCompat.getColor(novelFragmentChapters.getContext(), R.color.shade)));
                } else {
                    //TODO Tint for cards before 22
                }
                chaptersViewHolder.status.setText(Status.READ.getStatus());
                chaptersViewHolder.readTag.setVisibility(View.GONE);
                chaptersViewHolder.read.setVisibility(View.GONE);
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
