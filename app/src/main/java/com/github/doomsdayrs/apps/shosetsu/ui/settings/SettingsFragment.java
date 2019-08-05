package com.github.doomsdayrs.apps.shosetsu.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingsAdapter;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsCard;

import java.util.ArrayList;
import java.util.Objects;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
        cards.add(new SettingsCard(Types.INFO));
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


