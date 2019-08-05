package com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsCardViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsCard;

import java.util.ArrayList;

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
public class SettingsAdapter extends RecyclerView.Adapter<SettingsCardViewHolder> {
    private final ArrayList<SettingsCard> settingsCards;
    private final FragmentManager fragmentManager;

    public SettingsAdapter(ArrayList<SettingsCard> settingsCards, FragmentManager fragmentManager) {
        this.settingsCards = settingsCards;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public SettingsCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_settings_card, viewGroup, false);
        return new SettingsCardViewHolder(view, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsCardViewHolder settingsCardViewHolder, int i) {
        SettingsCard settingsCard = settingsCards.get(i);
        settingsCardViewHolder.setType(settingsCard.ID);
    }

    @Override
    public int getItemCount() {
        return settingsCards.size();
    }
}
