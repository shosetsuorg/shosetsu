package com.github.Doomsdayrs.apps.shosetsu.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.Types;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.SettingsCard;

import java.util.ArrayList;

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
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsCardViewHolder> {
    private ArrayList<SettingsCard> settingsCards;

    public SettingsAdapter(ArrayList<SettingsCard> settingsCards) {
        this.settingsCards = settingsCards;
    }

    @NonNull
    @Override
    public SettingsCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_settings_card, viewGroup, false);
        return new SettingsCardViewHolder(view);
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

    static class SettingsCardViewHolder extends RecyclerView.ViewHolder {
        TextView library_card_title;
        Types type;

        SettingsCardViewHolder(@NonNull View itemView) {
            super(itemView);
            library_card_title = itemView.findViewById(R.id.recycler_settings_title);
        }

        public void setType(Types type) {
            this.type = type;
            library_card_title.setOnClickListener(new onSettingsClick(type));
            library_card_title.setText(type.toString());
        }
    }

    static class onSettingsClick implements View.OnClickListener {
        Types type;

        onSettingsClick(Types id) {
            type = id;
        }

        @Override
        public void onClick(View v) {
            switch (type) {
                case VIEW: {
                    Toast.makeText(v.getContext(), "View", Toast.LENGTH_SHORT).show();
                }
                break;
                case CREDITS: {
                    Toast.makeText(v.getContext(), "credits", Toast.LENGTH_SHORT).show();
                }
                break;
                case ADVANCED: {
                    Toast.makeText(v.getContext(), "advanced", Toast.LENGTH_SHORT).show();
                }
                break;
                case DOWNLOAD: {
                    Toast.makeText(v.getContext(), "download", Toast.LENGTH_SHORT).show();
                }
                break;
                default: {
                }
            }
        }
    }

}
