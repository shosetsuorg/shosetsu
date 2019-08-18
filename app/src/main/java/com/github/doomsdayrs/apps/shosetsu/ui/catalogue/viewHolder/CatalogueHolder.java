package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder;
/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isOnline;

/**
 * shosetsu
 * 18 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CatalogueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final ImageView library_card_image;
    public final TextView library_card_title;
    final FragmentManager fragmentManager;
    Formatter formatter;

    public CatalogueHolder(@NonNull View itemView, FragmentManager fragmentManager) {
        super(itemView);
        library_card_image = itemView.findViewById(R.id.catalogue_item_card_image);
        library_card_title = itemView.findViewById(R.id.catalogue_item_card_text);
        this.fragmentManager = fragmentManager;
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
        Log.d("FormatterSet", formatter.getName());
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d("FormatterSelection", formatter.getName());
        if (isOnline()) {
            CatalogueFragment catalogueFragment = new CatalogueFragment();
            catalogueFragment.setFormatter(formatter);
            setFormatter(formatter);
            fragmentManager.beginTransaction()
                    .addToBackStack("tag")
                    .replace(R.id.fragment_container, catalogueFragment)
                    .commit();
        } else Toast.makeText(v.getContext(), "You are not online", Toast.LENGTH_SHORT).show();
    }
}
