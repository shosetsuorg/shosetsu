package com.github.Doomsdayrs.apps.shosetsu.fragment;

import android.content.Context;
import android.content.res.Configuration;
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

import com.github.Doomsdayrs.apps.shosetsu.adapters.NovelCardsAdapter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.RecycleCard;

import java.util.ArrayList;

public class LibraryFragement extends Fragment {
    private SearchView searchView;
    private Context context;

    private static ArrayList<RecycleCard> libraryCards = new ArrayList<>();
    private RecyclerView library_view;
    private RecyclerView.Adapter library_Adapter;
    private RecyclerView.LayoutManager library_layoutManager;


    public LibraryFragement() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        libraryCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "a"));
        libraryCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "b"));
        libraryCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "c"));
        libraryCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "d"));
        libraryCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "e"));
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        library_view = view.findViewById(R.id.fragment_library_recycler);
        this.context = container.getContext();
        setLibraryCards(libraryCards);
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


    private void setLibraryCards(ArrayList<RecycleCard> recycleCards) {
        if (library_view != null) {
            library_view.setHasFixedSize(false);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                library_layoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);
            else
                library_layoutManager = new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false);
            library_Adapter = new NovelCardsAdapter(recycleCards);
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
            ArrayList<RecycleCard> recycleCards = new ArrayList<>(libraryCards);
            recycleCards.removeIf(recycleCard -> !recycleCard.libraryText.contains(newText));
            setLibraryCards(recycleCards);
            return recycleCards.size() != 0;
        }
    }

}
