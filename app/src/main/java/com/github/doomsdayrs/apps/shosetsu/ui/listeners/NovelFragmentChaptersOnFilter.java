package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import android.view.MenuItem;

import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;

import java.util.Collections;

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragmentChaptersOnFilter implements MenuItem.OnMenuItemClickListener {
    private final NovelFragmentChapters novelFragmentChapters;

    public NovelFragmentChaptersOnFilter(NovelFragmentChapters novelFragmentChapters) {
        this.novelFragmentChapters = novelFragmentChapters;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Collections.reverse(StaticNovel.novelChapters);
        novelFragmentChapters.reversed = !novelFragmentChapters.reversed;
        return NovelFragmentChapters.recyclerView.post(() -> {
            NovelFragmentChapters.adapter.notifyDataSetChanged();
        });
    }
}
