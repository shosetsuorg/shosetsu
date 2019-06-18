package com.github.doomsdayrs.apps.shosetsu.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
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
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.adapters.catalogue.CatalogueNovelCardsAdapter;
import com.github.doomsdayrs.apps.shosetsu.async.CataloguePageLoader;
import com.github.doomsdayrs.apps.shosetsu.recycleObjects.CatalogueNovelCard;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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
    private static ArrayList<CatalogueNovelCard> libraryCards = new ArrayList<>();
    private static Formatter formatter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView library_view;
    private int currentMaxPage = 1;
    private Context context;
    private boolean firstRun;
    public RecyclerView.Adapter library_Adapter;
    public ProgressBar progressBar;

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
        swipeRefreshLayout.setOnRefreshListener(new refresh(this));
        progressBar = view.findViewById(R.id.fragment_catalogue_progress);

        this.context = Objects.requireNonNull(container).getContext();
        if (savedInstanceState == null) {
            Log.d("Process", "Loading up latest");
            if (firstRun) {
                firstRun = false;
                setLibraryCards(libraryCards);
                new CataloguePageLoader(this, formatter, libraryCards).execute();
            } else setLibraryCards(libraryCards);
        } else setLibraryCards(libraryCards);
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


    private void setLibraryCards(ArrayList<CatalogueNovelCard> recycleCards) {
        if (library_view != null) {
            library_view.setHasFixedSize(false);
            RecyclerView.LayoutManager library_layoutManager;
            if (Objects.requireNonNull(getActivity()).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                library_layoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
            else
                library_layoutManager = new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false);
            library_Adapter = new CatalogueNovelCardsAdapter(recycleCards, getFragmentManager(), formatter);
            library_view.setLayoutManager(library_layoutManager);
            library_view.addOnScrollListener(new bottom(this));
            library_view.setAdapter(library_Adapter);
        }
    }

    static class querySearch extends AsyncTask<String, Void, ArrayList<CatalogueNovelCard>> {
        @Override
        protected ArrayList<CatalogueNovelCard> doInBackground(String... strings) {
            ArrayList<CatalogueNovelCard> result = new ArrayList<>();
            try {
                List<Novel> novels = formatter.search(strings[0]);
                for (Novel novel : novels)
                    result.add(new CatalogueNovelCard(novel.imageURL, novel.title, new URI(novel.link)));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    class bottom extends RecyclerView.OnScrollListener {
        final CatalogueFragment catalogueFragment;
        boolean running = false;

        bottom(CatalogueFragment catalogueFragment) {
            this.catalogueFragment = catalogueFragment;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

            if (!running)
                if (!catalogueFragment.library_view.canScrollVertically(1)) {
                    Log.d("CatalogueFragmentLoad", "Getting next page");
                    running = true;
                    catalogueFragment.currentMaxPage++;
                    try {
                        if (new CataloguePageLoader(catalogueFragment, formatter, libraryCards).execute(catalogueFragment.currentMaxPage).get()) {
                            catalogueFragment.library_view.post(() -> {
                                catalogueFragment.library_Adapter.notifyDataSetChanged();
                                catalogueFragment.library_view.addOnScrollListener(this);
                            });

                            running = false;
                            Log.d("CatalogueFragmentLoad", "Completed");
                        }

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }


    class refresh implements SwipeRefreshLayout.OnRefreshListener {
        final CatalogueFragment catalogueFragment;

        refresh(CatalogueFragment catalogueFragment) {
            this.catalogueFragment = catalogueFragment;
        }

        @Override
        public void onRefresh() {
            catalogueFragment.swipeRefreshLayout.setRefreshing(true);

            libraryCards = new ArrayList<>();
            try {
                Log.d("FragmentRefresh", "Refreshing catalogue data");
                if (new CataloguePageLoader(catalogueFragment, formatter, libraryCards).execute().get()) {
                    Log.d("FragmentRefresh", "Complete");
                    catalogueFragment.library_Adapter.notifyDataSetChanged();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catalogueFragment.swipeRefreshLayout.setRefreshing(false);
        }
    }


    private class SearchClose implements SearchView.OnCloseListener {
        @Override
        public boolean onClose() {
            setLibraryCards(libraryCards);
            return true;
        }
    }

    private class SearchQuery implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            try {
                ArrayList<CatalogueNovelCard> searchResults = new querySearch().execute(query).get();
                setLibraryCards(searchResults);
                return true;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            Log.d("Library search", newText);
            ArrayList<CatalogueNovelCard> recycleCards = new ArrayList<>(libraryCards);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recycleCards.removeIf(recycleCard -> !recycleCard.title.toLowerCase().contains(newText.toLowerCase()));
            } else {
                for (int x = recycleCards.size() - 1; x >= 0; x--) {
                    if (!recycleCards.get(x).title.contains(newText)) {
                        recycleCards.remove(x);
                    }
                }
            }
            setLibraryCards(recycleCards);
            return recycleCards.size() != 0;
        }
    }
}
