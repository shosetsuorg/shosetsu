package com.github.Doomsdayrs.apps.shosetsu.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
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
import android.widget.SearchView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.adapters.catalogue.CatalogueNovelCardsAdapter;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.CatalogueNovelCard;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
public class CatalogueFragement extends Fragment {
    public static ArrayList<CatalogueNovelCard> libraryCards = new ArrayList<>();
    static Formatter formatter;
    static ArrayList<CatalogueNovelCard> searchResults = new ArrayList<>();
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView library_view;
    public int currentMaxPage = 1;
    private SearchView searchView;
    private Context context;
    private boolean firstRun;
    private RecyclerView.Adapter library_Adapter;
    private RecyclerView.LayoutManager library_layoutManager;

    public CatalogueFragement() {
        setHasOptionsMenu(true);
        firstRun = true;
    }

    public void setFormatter(Formatter formatter) {
        CatalogueFragement.formatter = formatter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        library_view = view.findViewById(R.id.fragment_catalogue_recycler);
        swipeRefreshLayout = view.findViewById(R.id.fragment_catalogue_refresh);
        swipeRefreshLayout.setOnRefreshListener(new refresh(this));


        this.context = container.getContext();
        if (savedInstanceState == null) {
            Log.d("Process", "Loading up latest");
            try {
                if (firstRun) {
                    firstRun = false;
                    boolean b = new setLatest().execute().get();
                    if (b)
                        setLibraryCards(libraryCards);
                } else setLibraryCards(libraryCards);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else setLibraryCards(libraryCards);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_library, menu);
        searchView = (SearchView) menu.findItem(R.id.library_search).getActionView();
        searchView.setOnQueryTextListener(new SearchQuery());
        searchView.setOnCloseListener(new SearchClose());
    }


    private void setLibraryCards(ArrayList<CatalogueNovelCard> recycleCards) {
        if (library_view != null) {
            library_view.setHasFixedSize(false);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
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

    static class bottom extends RecyclerView.OnScrollListener {
        CatalogueFragement catalogueFragement;
        boolean running = false;

        public bottom(CatalogueFragement catalogueFragement) {
            this.catalogueFragement = catalogueFragement;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

            if (!running)
                if (!catalogueFragement.library_view.canScrollVertically(1)) {
                    Log.d("CatalogueFragmentLoad", "Getting next page");
                    running = true;
                    catalogueFragement.currentMaxPage++;
                    try {
                        if (new setLatest().execute(catalogueFragement.currentMaxPage).get()) {
                            catalogueFragement.library_view.post(() -> {
                                catalogueFragement.library_Adapter.notifyDataSetChanged();
                                catalogueFragement.library_view.addOnScrollListener(this);
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

    static class setLatest extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... integers) {
            try {
                List<Novel> novels;
                if (integers.length == 0)
                    novels = formatter.parseLatest(formatter.getLatestURL(1));
                else novels = formatter.parseLatest(formatter.getLatestURL(integers[0]));

                for (Novel novel : novels)
                    libraryCards.add(new CatalogueNovelCard(novel.imageURL, novel.title, new URI(novel.link)));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return false;
        }
    }


    class refresh implements SwipeRefreshLayout.OnRefreshListener {
        CatalogueFragement catalogueFragement;

        refresh(CatalogueFragement catalogueFragement) {
            this.catalogueFragement = catalogueFragement;
        }

        @Override
        public void onRefresh() {
            catalogueFragement.swipeRefreshLayout.setRefreshing(true);

            libraryCards = new ArrayList<>();
            try {
                Log.d("CatalogueFragmentRefresh", "Refreshing catalogue data");
                if (new setLatest().execute().get()) {
                    Log.d("CatalogueFragmentRefresh", "Complete");
                    catalogueFragement.library_Adapter.notifyDataSetChanged();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catalogueFragement.swipeRefreshLayout.setRefreshing(false);
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
                searchResults = new querySearch().execute(query).get();
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
            recycleCards.removeIf(recycleCard -> !recycleCard.title.contains(newText));
            setLibraryCards(recycleCards);
            return recycleCards.size() != 0;
        }
    }
}
