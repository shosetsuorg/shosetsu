package com.github.Doomsdayrs.apps.shosetsu.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Doomsdayrs.apps.shosetsu.adapters.NovelCardsAdapter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.RecycleCard;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    private ArrayList<RecycleCard> cards = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public SettingsFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_settings,menu);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        cards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "download"));
        cards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "view"));
        cards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "advanced"));
        cards.add(new RecycleCard(R.drawable.ic_close_black_24dp, "credits"));

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        recyclerView = view.findViewById(R.id.fragment_settings_recycler);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(container.getContext());
            adapter = new NovelCardsAdapter(cards,getFragmentManager());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}
