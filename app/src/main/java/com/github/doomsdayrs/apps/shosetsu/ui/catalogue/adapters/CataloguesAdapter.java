package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.CatalogueHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class CataloguesAdapter extends RecyclerView.Adapter<CatalogueHolder> {
    private final ArrayList<CatalogueCard> catalogues;
    private final FragmentManager fragmentManager;

    public CataloguesAdapter(ArrayList<CatalogueCard> catalogues, FragmentManager fragmentManager) {
        this.catalogues = catalogues;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public CatalogueHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_item_card, viewGroup, false);
        return new CatalogueHolder(view, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueHolder catalogueHolder, int i) {

        CatalogueCard catalogueCard = catalogues.get(i);
        catalogueHolder.setFormatter(catalogueCard.formatter);

        if (catalogueCard.formatter.getImageURL() != null && !catalogueCard.formatter.getImageURL().isEmpty())
            Picasso.get()
                    .load(catalogueCard.formatter.getImageURL())
                    .into(catalogueHolder.library_card_image);
        else
            catalogueHolder.library_card_image.setImageResource(catalogueCard.libraryImageResource);
        catalogueHolder.library_card_title.setText(catalogueCard.title);
    }

    @Override
    public int getItemCount() {
        return catalogues.size();
    }

}
