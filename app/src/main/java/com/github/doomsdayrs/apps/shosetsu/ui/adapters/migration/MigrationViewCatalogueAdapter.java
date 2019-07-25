package com.github.doomsdayrs.apps.shosetsu.ui.adapters.migration;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.MigrationView;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueCard;
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
 */
public class MigrationViewCatalogueAdapter extends RecyclerView.Adapter<MigrationViewCatalogueAdapter.CatalogueHolder> {
    public final ArrayList<CatalogueCard> catalogues;
    public final MigrationView migrationView;

    public MigrationViewCatalogueAdapter(ArrayList<CatalogueCard> catalogues, MigrationView migrationView) {
        this.catalogues = catalogues;
        this.migrationView = migrationView;
    }


    @NonNull
    @Override
    public CatalogueHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_item_card, viewGroup, false);
        return new CatalogueHolder(view, migrationView);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueHolder catalogueHolder, int i) {

        CatalogueCard catalogueCard = catalogues.get(i);
        if (catalogueCard.formatter.getImageURL() != null && !catalogueCard.formatter.getImageURL().isEmpty())
            Picasso.get()
                    .load(catalogueCard.formatter.getImageURL())
                    .into(catalogueHolder.image);
        else
            catalogueHolder.image.setImageResource(catalogueCard.libraryImageResource);
        catalogueHolder.title.setText(catalogueCard.title);

        catalogueHolder.setFormatter(catalogueCard.formatter);
    }

    @Override
    public int getItemCount() {
        return catalogues.size();
    }

    static class CatalogueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView image;
        final TextView title;
        Formatter formatter;
        final MigrationView migrationView;


        CatalogueHolder(@NonNull View itemView, MigrationView migrationView) {
            super(itemView);
            image = itemView.findViewById(R.id.catalogue_item_card_image);
            title = itemView.findViewById(R.id.catalogue_item_card_text);
            itemView.setOnClickListener(this);
            this.migrationView = migrationView;
        }

        public void setFormatter(Formatter formatter) {
            this.formatter = formatter;
        }

        @Override
        public void onClick(View v) {
            Log.d("FormatterSelection", formatter.getName());
            if (SettingsController.INSTANCE.isOnline()) {

                Log.d("Target", String.valueOf(formatter.getID() - 1));
                migrationView.target = formatter.getID() - 1;
                migrationView.targetSelection.setVisibility(View.GONE);
                migrationView.migration.setVisibility(View.VISIBLE);

                //TODO, popup window saying novels rejected because the formatter ID is the same.
                for (int x = migrationView.novels.size() - 1; x >= 0; x--) {
                    if (migrationView.novels.get(x).formatterID == formatter.getID()) {
                        migrationView.novels.remove(x);
                    }
                }

                migrationView.fillData();
            } else Toast.makeText(v.getContext(), "You are not online", Toast.LENGTH_SHORT).show();
        }
    }
}
