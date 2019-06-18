package com.github.doomsdayrs.apps.shosetsu.ui.main;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.LibraryNovelCardsAdapter;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;

import java.util.ArrayList;
import java.util.Objects;

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
public class LibraryFragment extends Fragment {
    private static ArrayList<NovelCard> libraryCards = new ArrayList<>();
    private Context context;
    private RecyclerView library_view;


    public LibraryFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreate", "LibraryFragment");

        if (savedInstanceState == null) {
            libraryCards = Database.getLibrary();
        }

        View view = inflater.inflate(R.layout.fragment_library, container, false);
        library_view = view.findViewById(R.id.fragment_library_recycler);

        this.context = Objects.requireNonNull(container).getContext();
        setLibraryCards(libraryCards);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_library, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.library_search).getActionView();
        searchView.setOnQueryTextListener(new SearchQuery());
        searchView.setOnCloseListener(new SearchClose());
    }


    private void setLibraryCards(ArrayList<NovelCard> recycleCards) {
        if (library_view != null) {
            library_view.setHasFixedSize(false);
            RecyclerView.LayoutManager library_layoutManager;
            if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                library_layoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
            else
                library_layoutManager = new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false);
            RecyclerView.Adapter library_Adapter = new LibraryNovelCardsAdapter(recycleCards, getFragmentManager());
            library_view.setLayoutManager(library_layoutManager);
            library_view.setAdapter(library_Adapter);
        }
    }

    private class SearchClose implements SearchView.OnCloseListener {
        @Override
        public boolean onClose() {
            setLibraryCards(libraryCards);
            return false;
        }
    }

    private class SearchQuery implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            Log.d("Library search", newText);
            ArrayList<NovelCard> recycleCards = new ArrayList<>(libraryCards);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recycleCards.removeIf(recycleCard -> !recycleCard.title.toLowerCase().contains(newText.toLowerCase()));
            }
            setLibraryCards(recycleCards);
            return recycleCards.size() != 0;
        }
    }

}
