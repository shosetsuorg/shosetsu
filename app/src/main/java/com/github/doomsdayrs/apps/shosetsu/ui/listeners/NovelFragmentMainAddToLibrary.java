package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import android.view.View;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentMain;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
public class NovelFragmentMainAddToLibrary implements FloatingActionButton.OnClickListener {
    private final NovelFragmentMain novelFragmentMain;

    public NovelFragmentMainAddToLibrary(NovelFragmentMain novelFragmentMain) {
        this.novelFragmentMain = novelFragmentMain;
    }

    @Override
    public void onClick(View v) {
        if (!novelFragmentMain.inLibrary) {
            if (!Database.DatabaseLibrary.inLibrary(StaticNovel.novelURL))
                Database.DatabaseLibrary.addToLibrary(StaticNovel.formatter.getID(), StaticNovel.novelPage, StaticNovel.novelURL, StaticNovel.status.getA());
            Database.DatabaseLibrary.bookMark(StaticNovel.novelURL);
            novelFragmentMain.inLibrary = true;
            novelFragmentMain.floatingActionButton.setImageResource(R.drawable.ic_add_circle_black_24dp);
        } else {
            Database.DatabaseLibrary.unBookmark(StaticNovel.novelURL);
                novelFragmentMain.inLibrary = false;
                novelFragmentMain.floatingActionButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);

        }
    }
}