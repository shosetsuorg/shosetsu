package com.github.doomsdayrs.apps.shosetsu.ui.library.listener;

import android.os.Build;
import android.util.Log;
import android.widget.SearchView;

import androidx.annotation.NonNull;

import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment;

import java.util.ArrayList;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.getNovelTitle;

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
 */

/**
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
    public boolean onQueryTextChange(@NonNull String newText) {
        Log.d("Library search", newText);
        ArrayList<Integer> novelIDs = new ArrayList<>(libraryFragment.libraryNovelCards);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            novelIDs.removeIf(novelID -> !getNovelTitle(novelID).toLowerCase().contains(newText.toLowerCase()));
        } else {
            for (int x = novelIDs.size() - 1; x >= 0; x--)
                if (!getNovelTitle(novelIDs.get(x)).toLowerCase().contains(newText.toLowerCase()))
                    novelIDs.remove(x);
        }
        libraryFragment.setLibraryCards(novelIDs);
        return novelIDs.size() != 0;
    }
}
