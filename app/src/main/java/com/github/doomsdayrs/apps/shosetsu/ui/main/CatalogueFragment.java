package com.github.doomsdayrs.apps.shosetsu.ui.main;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.async.CataloguePageLoader;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.catalogue.CatalogueNovelCardsAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.CatalogueFragmentHitBottom;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.CatalogueRefresh;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.CatalogueSearchQuery;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;

import java.util.ArrayList;
import java.util.Objects;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO fix issue with not loading
public class CatalogueFragment extends Fragment {
    public ArrayList<CatalogueNovelCard> catalogueNovelCards = new ArrayList<>();
    public Formatter formatter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView library_view;
    public int currentMaxPage = 1;
    public boolean isInSearch = false;
    private Context context;

    public CatalogueNovelCardsAdapter catalogueNovelCardsAdapter;
    public ProgressBar bottomProgressBar;

    private boolean dontRefresh = false;
    public boolean isQuery = false;

    public  ConstraintLayout errorView;
    public  TextView errorMessage;
    public  Button errorButton;
    public TextView empty;
    /**
     * Constructor
     */
    public CatalogueFragment() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("list", catalogueNovelCards);
        outState.putInt("formatter", formatter.getID() - 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Resume", "HERE");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Pause", "HERE");
        dontRefresh = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dontRefresh = false;
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
        Log.d("OnCreateView", "CatalogueFragment");
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        {
            library_view = view.findViewById(R.id.fragment_catalogue_recycler);
            swipeRefreshLayout = view.findViewById(R.id.fragment_catalogue_refresh);
            bottomProgressBar = view.findViewById(R.id.fragment_catalogue_progress_bottom);
            errorView = view.findViewById(R.id.network_error);
            errorMessage = view.findViewById(R.id.error_message);
            errorButton = view.findViewById(R.id.error_button);
            empty = view.findViewById(R.id.fragment_catalogue_empty);
        }

        if (savedInstanceState != null) {
            catalogueNovelCards = (ArrayList<CatalogueNovelCard>) savedInstanceState.getSerializable("list");
            formatter = DefaultScrapers.formatters.get(savedInstanceState.getInt("formatter"));
        }
        Statics.mainActionBar.setTitle(formatter.getName());
        swipeRefreshLayout.setOnRefreshListener(new CatalogueRefresh(this));
        this.context = Objects.requireNonNull(container).getContext();


        if (savedInstanceState == null && !dontRefresh) {
            Log.d("Process", "Loading up latest");
            setLibraryCards(catalogueNovelCards);
            if (catalogueNovelCards.size() > 0) {
                catalogueNovelCards = new ArrayList<>();
                catalogueNovelCardsAdapter.notifyDataSetChanged();
            }
            new CataloguePageLoader(this).execute();
        } else
            setLibraryCards(catalogueNovelCards);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_library, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.library_search).getActionView();
        searchView.setOnQueryTextListener(new CatalogueSearchQuery(this));
        searchView.setOnCloseListener(() -> {
            isQuery = false;
            isInSearch = false;
            setLibraryCards(catalogueNovelCards);
            return true;
        });
    }


    public void setLibraryCards(ArrayList<CatalogueNovelCard> recycleCards) {
        if (library_view != null) {
            library_view.setHasFixedSize(false);
            RecyclerView.LayoutManager library_layoutManager;
            if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                library_layoutManager = new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false);
            else
                library_layoutManager = new GridLayoutManager(context, 4, RecyclerView.VERTICAL, false);
            catalogueNovelCardsAdapter = new CatalogueNovelCardsAdapter(recycleCards, getFragmentManager(), formatter);
            library_view.setLayoutManager(library_layoutManager);
            library_view.addOnScrollListener(new CatalogueFragmentHitBottom(this));
            library_view.setAdapter(catalogueNovelCardsAdapter);
        }
    }
}
