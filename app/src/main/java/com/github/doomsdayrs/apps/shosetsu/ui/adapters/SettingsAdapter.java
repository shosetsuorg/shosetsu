package com.github.doomsdayrs.apps.shosetsu.ui.adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsCard;
import com.github.doomsdayrs.apps.shosetsu.backend.settings.SettingsController;

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

    static class SettingsCardViewHolder extends RecyclerView.ViewHolder {
        final TextView library_card_title;
        final FragmentManager fragmentManager;

        SettingsCardViewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
            super(itemView);
            library_card_title = itemView.findViewById(R.id.recycler_settings_title);
            this.fragmentManager = fragmentManager;
        }

        void setType(Types type) {
            library_card_title.setOnClickListener(new onSettingsClick(type, fragmentManager));
            library_card_title.setText(type.toString());
        }
    }

    static class onSettingsClick implements View.OnClickListener {
        final Types type;
        final FragmentManager fragmentManager;

        onSettingsClick(Types id, FragmentManager fragmentManager) {
            type = id;
            this.fragmentManager = fragmentManager;
        }

        @Override
        public void onClick(View v) {
            switch (type) {
                case VIEW: {
                    Toast.makeText(v.getContext(), "View", Toast.LENGTH_SHORT).show();
                    fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, new viewSettings()).commit();
                }
                break;
                case CREDITS: {
                    Toast.makeText(v.getContext(), "Credits", Toast.LENGTH_SHORT).show();
                }
                break;
                case ADVANCED: {
                    Toast.makeText(v.getContext(), "Advanced", Toast.LENGTH_SHORT).show();
                }
                break;
                case DOWNLOAD: {
                    Toast.makeText(v.getContext(), "Download", Toast.LENGTH_SHORT).show();
                }
                break;
                default: {
                }
            }
        }
    }

    public static class viewSettings extends Fragment {
        CheckBox checkBox;

        public viewSettings() {
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.d("OnCreateView", "ViewSettings");
            View view = inflater.inflate(R.layout.fragment_settings_view, container, false);
            checkBox = view.findViewById(R.id.reader_nightMode_checkbox);
            checkBox.setChecked(!SettingsController.isReaderLightMode());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> SettingsController.swapReaderColor());
            return view;
        }
    }
}
