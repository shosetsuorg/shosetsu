package com.github.doomsdayrs.apps.shosetsu.ui.viewholders;
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
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.OnSettingsCardClick;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types;

public class SettingsCardViewHolder extends RecyclerView.ViewHolder {
    final TextView library_card_title;
    final CardView cardView;
    final FragmentManager fragmentManager;

    public SettingsCardViewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
        super(itemView);
        library_card_title = itemView.findViewById(R.id.recycler_settings_title);
        cardView = itemView.findViewById(R.id.settings_card);
        this.fragmentManager = fragmentManager;
    }

    public void setType(Types type) {
        cardView.setOnClickListener(new OnSettingsCardClick(type, fragmentManager));
        library_card_title.setText(type.toString());
    }
}