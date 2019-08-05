package com.github.doomsdayrs.apps.shosetsu.ui.library.listener;

import android.os.Build;
import android.util.Log;
import android.widget.SearchView;

import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import java.util.ArrayList;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
        ArrayList<NovelCard> recycleCards = new ArrayList<>(libraryFragment.libraryNovelCards);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recycleCards.removeIf(recycleCard -> !recycleCard.title.toLowerCase().contains(newText.toLowerCase()));
        }
        libraryFragment.setLibraryCards(recycleCards);
        return recycleCards.size() != 0;
    }
}
