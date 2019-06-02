package com.github.Doomsdayrs.apps.shosetsu.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Doomsdayrs.api.novelreader_core.main.DefaultScrapers;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.adapters.CatalogueCardsAdapter;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.CatalogueCard;

import java.util.ArrayList;

public class CataloguesFragment extends Fragment {
    private FragmentManager fragmentManager;
    private ArrayList<CatalogueCard> cards = null;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public CataloguesFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_catalogues, menu);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO Conditional for turning formatters on and off
        //TODO Conditional for languages
        //TODO Conditional for categories, maybe
        if (cards == null) {
            cards = new ArrayList<>();
            for (Formatter formatter : DefaultScrapers.formatters) {
                String imageURL = formatter.getImageURL();
                if (imageURL == null) {
                    cards.add(new CatalogueCard(formatter));
                }
            }
        }
        fragmentManager = getFragmentManager();

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        recyclerView = view.findViewById(R.id.fragment_settings_recycler);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(container.getContext());
            adapter = new CatalogueCardsAdapter(cards,fragmentManager);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

        }
        return view;
    }
}
