package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.github.doomsdayrs.apps.shosetsu.backend.async.NovelChaptersLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;

import java.util.Collections;

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragmentChaptersHitBottom extends RecyclerView.OnScrollListener {
    private final NovelFragmentChapters novelFragmentChapters;
    private boolean running = false;

    public NovelFragmentChaptersHitBottom(NovelFragmentChapters novelFragmentChapters) {
        this.novelFragmentChapters = novelFragmentChapters;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

        if (!running)
            if (!NovelFragmentChapters.recyclerView.canScrollVertically(1)) {
                Log.d("ScrollLoad", "Loading...");
                if (novelFragmentChapters.reversed)
                    Collections.reverse(StaticNovel.novelChapters);
                running = true;
                novelFragmentChapters.currentMaxPage++;
                new NovelChaptersLoader(novelFragmentChapters).execute(novelFragmentChapters.currentMaxPage);
                Log.d("ScrollLoad", "Completed.");
                running = false;
                if (novelFragmentChapters.reversed)
                    Collections.reverse(StaticNovel.novelChapters);
            }
    }
}
