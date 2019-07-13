package com.github.doomsdayrs.apps.shosetsu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.viewholders.SettingsCardViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsCard;

import java.util.ArrayList;

/*
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
