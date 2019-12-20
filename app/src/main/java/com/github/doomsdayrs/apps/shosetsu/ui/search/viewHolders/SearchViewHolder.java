package com.github.doomsdayrs.apps.shosetsu.ui.search.viewHolders;
/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class SearchViewHolder extends RecyclerView.ViewHolder {
    private int id = -2;
    private Formatter formatter;
    private TextView textView;
    private RecyclerView recyclerView;

    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
        recyclerView = itemView.findViewById(R.id.recyclerView);
    }

    public void setId(int id) {
        this.id = id;
        switch (id) {
            case -2:
                throw new RuntimeException("InvalidValue");
            case -1:
                textView.setText(R.string.my_library);
                break;
            default:
                formatter = DefaultScrapers.getByID(id);
                textView.setText(formatter.getName());
                break;
        }
    }
}
