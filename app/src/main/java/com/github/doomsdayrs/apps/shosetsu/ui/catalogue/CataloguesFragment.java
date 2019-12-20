package com.github.doomsdayrs.apps.shosetsu.ui.catalogue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters.CataloguesAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CataloguesSearchQuery;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueCard;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setActivityTitle;

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO Searching mechanics here
public class CataloguesFragment extends Fragment {
    @Nullable
    private ArrayList<CatalogueCard> cards = null;

    /**
     * Constructor
     */
    public CataloguesFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_catalogues, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.catalogues_search).getActionView();
        searchView.setOnQueryTextListener(new CataloguesSearchQuery(getActivity()));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.catalogues_search:
            case R.id.configure_parsers:
                return true;
            default:
                return false;
        }
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
        Log.d("OnCreateView", "CataloguesFragment");
        setActivityTitle(getActivity(), "Catalogues");
        //TODO Conditional for turning formatter on and off
        // > Conditional for languages
        // > Conditional for categories, maybe
        if (cards == null) {
            cards = DefaultScrapers.getAsCatalogue();
        }
        FragmentManager fragmentManager = getFragmentManager();

        View view = inflater.inflate(R.layout.fragment_catalogues, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.catalogues_recycler);

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Objects.requireNonNull(container).getContext());
            RecyclerView.Adapter adapter = new CataloguesAdapter(cards, fragmentManager);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

}
