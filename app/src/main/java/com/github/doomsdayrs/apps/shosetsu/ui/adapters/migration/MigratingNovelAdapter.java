package com.github.doomsdayrs.apps.shosetsu.ui.adapters.migration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.MigrationView;
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
public class MigratingNovelAdapter extends RecyclerView.Adapter<MigratingNovelAdapter.CatalogueHolder> {

    private final MigrationView migrationView;

    public MigratingNovelAdapter(MigrationView migrationView) {
        this.migrationView = migrationView;
    }

    public void setNovels(ArrayList<Novel> novels) {
        this.migrationView.novels = novels;
    }

    @NonNull
    @Override
    public CatalogueHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_item_card, viewGroup, false);
        return new CatalogueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueHolder catalogueHolder, int i) {
        Novel novel = migrationView.novels.get(i);
        Picasso.get().load(novel.imageURL).into(catalogueHolder.image);
        catalogueHolder.title.setText(novel.title);
    }

    @Override
    public int getItemCount() {
        return migrationView.novels.size();
    }


    static class CatalogueHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title;

        CatalogueHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.novel_item_image);
            title = itemView.findViewById(R.id.textView);
        }
    }
}
