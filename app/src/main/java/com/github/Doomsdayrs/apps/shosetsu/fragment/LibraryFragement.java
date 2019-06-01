package com.github.Doomsdayrs.apps.shosetsu.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Doomsdayrs.apps.shosetsu.NovelCardsAdapter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.RecycleCard;

import java.util.ArrayList;

public class LibraryFragement extends Fragment {
    private ArrayList<RecycleCard> libraryCards = new ArrayList<>();
    private RecyclerView library_view;
    private RecyclerView.Adapter library_Adapter;
    private RecyclerView.LayoutManager library_layoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        libraryCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "dummy"));
        libraryCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "dummy"));
        libraryCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "dummy"));
        libraryCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "dummy"));
        libraryCards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "dummy"));

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        library_view = view.findViewById(R.id.fragment_library_recycler);
        if (library_view!=null) {
            library_view.setHasFixedSize(false);
            library_layoutManager = new GridLayoutManager(container.getContext(),2,GridLayoutManager.VERTICAL,false);
            library_Adapter = new NovelCardsAdapter(libraryCards);
            library_view.setLayoutManager(library_layoutManager);
            library_view.setAdapter(library_Adapter);
        }
        return view;
    }
}
