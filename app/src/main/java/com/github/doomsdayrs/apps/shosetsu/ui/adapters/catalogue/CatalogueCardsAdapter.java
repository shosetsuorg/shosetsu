package com.github.doomsdayrs.apps.shosetsu.ui.adapters.catalogue;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.main.catalogue.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueCard;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.squareup.picasso.Picasso;

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
 * @author github.com/hXtreme
 */
public class CatalogueCardsAdapter extends RecyclerView.Adapter<CatalogueCardsAdapter.CatalogueHolder> {
    public final ArrayList<CatalogueCard> catalogues;
    private final FragmentManager fragmentManager;

    public CatalogueCardsAdapter(ArrayList<CatalogueCard> catalogues, FragmentManager fragmentManager) {
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

    static class CatalogueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView library_card_image;
        final TextView library_card_title;
        Formatter formatter;
        final FragmentManager fragmentManager;

        CatalogueHolder(@NonNull View itemView, FragmentManager fragmentManager) {
            super(itemView);
            library_card_image = itemView.findViewById(R.id.catalogue_item_card_image);
            library_card_title = itemView.findViewById(R.id.catalogue_item_card_text);
            this.fragmentManager = fragmentManager;
        }

        void setFormatter(Formatter formatter) {
            this.formatter = formatter;
            Log.d("FormatterSet", formatter.getName());
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("FormatterSelection", formatter.getName());
            if (SettingsController.INSTANCE.isOnline()) {
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
}
