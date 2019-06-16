package com.github.Doomsdayrs.apps.shosetsu.adapters.catalogue;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.fragment.CatalogueFragement;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.CatalogueCard;
import com.github.Doomsdayrs.apps.shosetsu.settings.SettingsController;
import com.squareup.picasso.Picasso;

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

public class CatalogueCardsAdapter extends RecyclerView.Adapter<CatalogueCardsAdapter.CatalogueHolder> {
    private ArrayList<CatalogueCard> recycleCards;
    private FragmentManager fragmentManager;
    private Context context;

    public CatalogueCardsAdapter(ArrayList<CatalogueCard> recycleCards, FragmentManager fragmentManager) {
        this.recycleCards = recycleCards;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public CatalogueHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_item_card, viewGroup, false);
        context = viewGroup.getContext();
        return new CatalogueHolder(view, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueHolder catalogueHolder, int i) {

        CatalogueCard catalogueCard = recycleCards.get(i);
        catalogueHolder.setFormatter(catalogueCard.formatter);

        if (catalogueCard.formatter.getImageURL() != null && !catalogueCard.formatter.getImageURL().isEmpty())
            Picasso.get()
                    .load(catalogueCard.formatter.getImageURL())
                    .into(catalogueHolder.library_card_image);
        else
            catalogueHolder.library_card_image.setImageResource(catalogueCard.libraryImageResource);
        catalogueHolder.library_card_title.setText(catalogueCard.libraryText);
    }

    @Override
    public int getItemCount() {
        return recycleCards.size();
    }

    static class CatalogueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView library_card_image;
        public TextView library_card_title;
        public Formatter formatter;
        FragmentManager fragmentManager;

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
            Log.d("ForrmatterSelection", formatter.getName());
            if (SettingsController.isOnline()) {
                CatalogueFragement catalogueFragement = new CatalogueFragement();
                catalogueFragement.setFormatter(formatter);
                setFormatter(formatter);
                fragmentManager.beginTransaction()
                        .addToBackStack("tag")
                        .replace(R.id.fragment_container, catalogueFragement)
                        .commit();
            } else Toast.makeText(v.getContext(), "You are not online", Toast.LENGTH_SHORT).show();
        }
    }
}
