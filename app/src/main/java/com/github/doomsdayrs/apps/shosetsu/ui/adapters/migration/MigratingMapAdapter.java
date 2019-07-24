package com.github.doomsdayrs.apps.shosetsu.ui.adapters.migration;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.MigrationView;
import com.github.doomsdayrs.apps.shosetsu.ui.viewholders.CompressedHolder;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

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
public class MigratingMapAdapter extends RecyclerView.Adapter<CompressedHolder> {
    private MigrationView migrationView;

    public MigratingMapAdapter(MigrationView migrationView) {
        this.migrationView = migrationView;
    }

    @NonNull
    @Override
    public CompressedHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_item_card, viewGroup, false);
        return new CompressedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompressedHolder holder, int position) {
        Novel novel = migrationView.novelResults.get(migrationView.selection).get(position);
        Picasso.get().load(novel.imageURL).into(holder.image);
        holder.title.setText(novel.title);
        MaterialCardView materialCardView = holder.itemView.findViewById(R.id.materialCardView);

        if (position == migrationView.secondSelection) {
            materialCardView.setStrokeColor(Color.BLUE);
            materialCardView.setStrokeWidth(2);
        } else materialCardView.setStrokeWidth(0);

        holder.itemView.setOnClickListener(view -> {
            migrationView.secondSelection = position;
            migrationView.refresh();
        });
    }


    @Override
    public int getItemCount() {
        if (migrationView.novelResults.size()> 0)
            return migrationView.novelResults.get(migrationView.selection).size();
        else return 0;
    }

}
