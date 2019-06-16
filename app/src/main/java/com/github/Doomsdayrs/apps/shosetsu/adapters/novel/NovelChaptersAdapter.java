package com.github.Doomsdayrs.apps.shosetsu.adapters.novel;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.database.Database;
import com.github.Doomsdayrs.apps.shosetsu.fragment.novel.NovelFragmentChapters;
import com.github.Doomsdayrs.apps.shosetsu.settings.SettingsController;

import java.util.List;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelChaptersAdapter extends RecyclerView.Adapter<ChaptersViewHolder> {

    public static Formatter formatter;
    private NovelFragmentChapters novelFragmentChapters;
    private FragmentManager fragmentManager;
    private List<NovelChapter> novelChapters;


    public NovelChaptersAdapter(NovelFragmentChapters novelFragmentChapters, List<NovelChapter> novels, FragmentManager fragmentManager, Formatter formatter) {
        this.novelFragmentChapters = novelFragmentChapters;
        this.novelChapters = novels;
        this.fragmentManager = fragmentManager;
        NovelChaptersAdapter.formatter = formatter;
    }


    @NonNull
    @Override
    public ChaptersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_novel_chapter, viewGroup, false);
        return new ChaptersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChaptersViewHolder chaptersViewHolder, int i) {
        NovelChapter novelChapter = novelChapters.get(i);
        chaptersViewHolder.novelChapter = novelChapter;
        chaptersViewHolder.fragmentManager = fragmentManager;
        chaptersViewHolder.library_card_title.setText(novelChapter.chapterNum);
        chaptersViewHolder.novelFragmentChapters = novelFragmentChapters;

        if (SettingsController.isBookMarked(novelChapter.link))
            chaptersViewHolder.bookmarked.setImageResource(R.drawable.ic_bookmark_black_24dp);

        if (Database.isSaved(novelFragmentChapters.novelURL, novelChapter.link)) {
            chaptersViewHolder.download.setImageResource(R.drawable.ic_arrow_drop_down_circle_black_24dp);
            chaptersViewHolder.downloaded = true;
        }

    }

    @Override
    public int getItemCount() {
        return novelChapters.size();
    }


}
