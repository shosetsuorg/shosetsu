package com.github.Doomsdayrs.apps.shosetsu.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
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

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.adapters.CatalogueNovelCardsAdapter;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.CatalogueNovelCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CatalogueFragement extends Fragment {
    private Formatter formatter;
    private SearchView searchView;
    private Context context;

    private static ArrayList<CatalogueNovelCard> libraryCards = new ArrayList<>();
    private static ArrayList<CatalogueNovelCard> searchResults = new ArrayList<>();
    private RecyclerView library_view;
    private RecyclerView.Adapter library_Adapter;
    private RecyclerView.LayoutManager library_layoutManager;

    public CatalogueFragement() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);
        library_view = view.findViewById(R.id.fragment_library_recycler);
        this.context = container.getContext();
        if (savedInstanceState == null) {
            try {
                boolean b = new setLatest().execute().get();
                if (b)
                    setLibraryCards(libraryCards);
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
            library_Adapter = new CatalogueNovelCardsAdapter(context, recycleCards);
            library_view.setLayoutManager(library_layoutManager);
            library_view.setAdapter(library_Adapter);
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

    class querySearch extends AsyncTask<String, Void, ArrayList<CatalogueNovelCard>> {
        @Override
        protected ArrayList<CatalogueNovelCard> doInBackground(String... strings) {
            ArrayList<CatalogueNovelCard> result = new ArrayList<>();
            try {
                List<Novel> novels = formatter.search(strings[0]);
                for (Novel novel : novels)
                    result.add(new CatalogueNovelCard(novel.imageURL, novel.title));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }


    class setLatest extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                List<Novel> novels = formatter.parseLatest(formatter.getLatestURL(1));
                for (Novel novel : novels)
                    libraryCards.add(new CatalogueNovelCard(novel.imageURL, novel.title));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
