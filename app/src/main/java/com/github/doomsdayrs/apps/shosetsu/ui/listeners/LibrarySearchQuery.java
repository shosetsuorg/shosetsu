package com.github.doomsdayrs.apps.shosetsu.ui.listeners;

import android.os.Build;
import android.util.Log;
import android.widget.SearchView;

import com.github.doomsdayrs.apps.shosetsu.ui.main.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import java.util.ArrayList;

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
public class LibrarySearchQuery implements SearchView.OnQueryTextListener {
    private final LibraryFragment libraryFragment;

    public LibrarySearchQuery(LibraryFragment libraryFragment) {
        this.libraryFragment = libraryFragment;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("Library search", newText);
        ArrayList<NovelCard> recycleCards = new ArrayList<>(LibraryFragment.libraryNovelCards);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recycleCards.removeIf(recycleCard -> !recycleCard.title.toLowerCase().contains(newText.toLowerCase()));
        }
        libraryFragment.setLibraryCards(recycleCards);
        return recycleCards.size() != 0;
    }
}
