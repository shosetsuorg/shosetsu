package com.github.doomsdayrs.apps.shosetsu.ui.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.SettingsAdapter;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsCard;

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
public class SettingsFragment extends Fragment {
    private static final ArrayList<SettingsCard> cards = new ArrayList<>();

    static {
        cards.add(new SettingsCard(Types.DOWNLOAD));
        cards.add(new SettingsCard(Types.VIEW));
        cards.add(new SettingsCard(Types.ADVANCED));
        cards.add(new SettingsCard(Types.CREDITS));
        cards.add(new SettingsCard(Types.BACKUP));
    }

    /**
     * Constructor
     * TODO, Create custom option menu for settings to search specific ones
     */
    public SettingsFragment() {
        // setHasOptionsMenu(true);
    }

    /**
     * Save data of view before destroyed
     *
     * @param outState output save
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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
        Log.d("OnCreateView", "SettingsFragment");
        Statics.mainActionBar.setTitle("Settings");
        View view = inflater.inflate(R.layout.settings, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.settings_recycler);


        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Objects.requireNonNull(container).getContext());
            RecyclerView.Adapter adapter = new SettingsAdapter(cards, getFragmentManager());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}


