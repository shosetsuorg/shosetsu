package com.github.doomsdayrs.apps.shosetsu.ui.main;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.async.CataloguePageLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.catalogue.CatalogueNovelCardsAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.CatalogueFragmentHitBottom;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.CatalogueRefresh;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.CatalogueSearchQuery;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;

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
public class CatalogueFragment extends Fragment {
    public static ArrayList<CatalogueNovelCard> catalogueNovelCards = new ArrayList<>();
    public static Formatter formatter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView library_view;
    public int currentMaxPage = 1;
    private Context context;
    private boolean firstRun;
    public RecyclerView.Adapter library_Adapter;
    public ProgressBar progressBar;
    public ProgressBar bottomProgressBar;

    public CatalogueFragment() {
        setHasOptionsMenu(true);
        firstRun = true;
    }

    public void setFormatter(Formatter formatter) {
        CatalogueFragment.formatter = formatter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "CatalogueFragment");
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        library_view = view.findViewById(R.id.fragment_catalogue_recycler);
        swipeRefreshLayout = view.findViewById(R.id.fragment_catalogue_refresh);
        swipeRefreshLayout.setOnRefreshListener(new CatalogueRefresh(this));
        progressBar = view.findViewById(R.id.fragment_catalogue_progress);
        bottomProgressBar = view.findViewById(R.id.fragment_catalogue_progress_bottom);

        this.context = Objects.requireNonNull(container).getContext();
        if (savedInstanceState == null) {
            Log.d("Process", "Loading up latest");
            if (firstRun) {
                firstRun = false;
                setLibraryCards(catalogueNovelCards);
                new CataloguePageLoader(this).execute();
            } else setLibraryCards(catalogueNovelCards);
        } else setLibraryCards(catalogueNovelCards);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_library, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.library_search).getActionView();
        searchView.setOnQueryTextListener(new CatalogueSearchQuery(this));
        searchView.setOnCloseListener(() -> {
            setLibraryCards(catalogueNovelCards);
            return true;
        });
    }


    public void setLibraryCards(ArrayList<CatalogueNovelCard> recycleCards) {
        if (library_view != null) {
            library_view.setHasFixedSize(false);
            RecyclerView.LayoutManager library_layoutManager;
            if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                library_layoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
            else
                library_layoutManager = new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false);
            library_Adapter = new CatalogueNovelCardsAdapter(recycleCards, getFragmentManager(), formatter);
            library_view.setLayoutManager(library_layoutManager);
            library_view.addOnScrollListener(new CatalogueFragmentHitBottom(this));
            library_view.setAdapter(library_Adapter);
        }
    }
}
