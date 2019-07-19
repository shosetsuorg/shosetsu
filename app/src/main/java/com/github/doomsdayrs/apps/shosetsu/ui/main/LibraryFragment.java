package com.github.doomsdayrs.apps.shosetsu.ui.main;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.LibraryNovelAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.LibrarySearchQuery;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class LibraryFragment extends Fragment {
    public static ArrayList<NovelCard> libraryNovelCards = new ArrayList<>();
    public static ArrayList<NovelCard> selectedNovels = new ArrayList<>();

    public static boolean contains(NovelCard novelCard) {
        for (NovelCard n : selectedNovels)
            if (n.novelURL.equalsIgnoreCase(novelCard.novelURL))
                return true;
        return false;
    }


    private Context context;
    public RecyclerView recyclerView;
    public LibraryNovelAdapter libraryNovelCardsAdapter;

    /**
     * Constructor
     */
    public LibraryFragment() {
        setHasOptionsMenu(true);
    }

    private void readFromDB() {
        libraryNovelCards = Database.DatabaseLibrary.getLibrary();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            libraryNovelCards.sort((novelCard, t1) -> novelCard.title.compareTo(t1.title));
        } else {
            bubbleSortA_Z();
        }
    }

    private void bubbleSortA_Z() {
        for (int i = libraryNovelCards.size() - 1; i > 1; i--) {
            for (int j = 0; j < i; j++) {
                if (libraryNovelCards.get(j).title.compareTo(libraryNovelCards.get(j + 1).title) > 0)
                    swapValues(j, j + 1);
            }
        }
    }

    private void swapValues(int indexOne, int indexTwo) {
        NovelCard novelCard = libraryNovelCards.get(indexOne);
        libraryNovelCards.set(indexOne, libraryNovelCards.get(indexTwo));
        libraryNovelCards.set(indexTwo, novelCard);
    }

    /**
     * Creates view
     *
     * @param inflater           inflates layouts and shiz
     * @param container          container of this fragment
     * @param savedInstanceState save file
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Statics.mainActionBar.setTitle("Library");
        Log.d("OnCreate", "LibraryFragment");
        if (savedInstanceState == null)
            readFromDB();
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        recyclerView = view.findViewById(R.id.fragment_library_recycler);
        this.context = Objects.requireNonNull(container).getContext();
        setLibraryCards(LibraryFragment.libraryNovelCards);
        return view;
    }

    public Menu menu;
    /**
     * Creates the option menu
     * @param menu menu to fill
     * @param inflater inflater of layouts and shiz
     */
    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        this.menu = menu;
        menu.clear();
        if (selectedNovels.size() <= 0) {
            inflater.inflate(R.menu.toolbar_library, menu);
            SearchView searchView = (SearchView) menu.findItem(R.id.library_search).getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new LibrarySearchQuery(this));
                searchView.setOnCloseListener(() -> {
                    setLibraryCards(LibraryFragment.libraryNovelCards);
                    return false;
                });
            }
            menu.findItem(R.id.updater_now).setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(getContext(), "In the future this will start a checking of each novel in this library", Toast.LENGTH_SHORT).show();
                return true;
            });
            menu.findItem(R.id.source_migrate).setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(getContext(), "In the future this will allow you to migrate between sources", Toast.LENGTH_LONG).show();
                return true;
            });
        } else {
            inflater.inflate(R.menu.toolbar_library_selected, menu);
            menu.findItem(R.id.chapter_select_all).setOnMenuItemClickListener(menuItem -> {
                for (NovelCard novelChapter : libraryNovelCards)
                    if (!contains(novelChapter))
                        selectedNovels.add(novelChapter);
                recyclerView.post(() -> libraryNovelCardsAdapter.notifyDataSetChanged());
                return true;
            });
            menu.findItem(R.id.chapter_deselect_all).setOnMenuItemClickListener(menuItem -> {
                selectedNovels = new ArrayList<>();
                recyclerView.post(() -> libraryNovelCardsAdapter.notifyDataSetChanged());
                onCreateOptionsMenu(menu, inflater);
                return true;
            });
            menu.findItem(R.id.remove_from_library).setOnMenuItemClickListener(menuItem -> {
                for (NovelCard novelCard : selectedNovels) {
                    Database.DatabaseLibrary.unBookmark(novelCard.novelURL);
                    libraryNovelCards.remove(novelCard);
                }
                selectedNovels = new ArrayList<>();
                recyclerView.post(() -> libraryNovelCardsAdapter.notifyDataSetChanged());
                return true;
            });
        }

    }

    /**
     * Sets the cards to display
     */
    public void setLibraryCards(ArrayList<NovelCard> novelCards) {
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(false);
            RecyclerView.LayoutManager library_layoutManager;
            if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                library_layoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
            else
                library_layoutManager = new GridLayoutManager(context, 4, RecyclerView.VERTICAL, false);
            libraryNovelCardsAdapter = new LibraryNovelAdapter(novelCards, this);
            recyclerView.setLayoutManager(library_layoutManager);
            recyclerView.setAdapter(libraryNovelCardsAdapter);
        }
    }

    public MenuInflater getInflater() {
        return new MenuInflater(getContext());
    }
}
