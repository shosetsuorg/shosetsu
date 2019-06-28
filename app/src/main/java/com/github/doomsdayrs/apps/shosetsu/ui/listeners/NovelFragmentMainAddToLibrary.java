package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentMain;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class NovelFragmentMainAddToLibrary implements FloatingActionButton.OnClickListener {
    final NovelFragmentMain novelFragmentMain;

    public NovelFragmentMainAddToLibrary(NovelFragmentMain novelFragmentMain) {
        this.novelFragmentMain = novelFragmentMain;
    }

    @Override
    public void onClick(View v) {
        if (!novelFragmentMain.inLibrary) {
            Database.DatabaseLibrary.addToLibrary(novelFragmentMain.formatter.getID(), StaticNovel.novelPage, novelFragmentMain.url);
            novelFragmentMain.inLibrary = true;
            novelFragmentMain.floatingActionButton.setImageResource(R.drawable.ic_add_circle_black_24dp);
        } else {
            if (!Database.DatabaseLibrary.removeFromLibrary(novelFragmentMain.url)) {
                novelFragmentMain.inLibrary = false;
                novelFragmentMain.floatingActionButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
            } else
                Toast.makeText(v.getContext(), "Error removing from library", Toast.LENGTH_LONG).show();
        }
    }
}