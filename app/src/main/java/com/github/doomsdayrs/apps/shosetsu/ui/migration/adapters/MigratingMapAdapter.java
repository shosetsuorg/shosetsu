package com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.viewHolders.CompressedHolder;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

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
public class MigratingMapAdapter extends RecyclerView.Adapter<CompressedHolder> {
    private final MigrationView migrationView;

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
        Picasso.get().load(novel.getImageURL()).into(holder.image);
        holder.title.setText(novel.getTitle());
        MaterialCardView materialCardView = holder.itemView.findViewById(R.id.materialCardView);

        if (position == migrationView.secondSelection) {
            materialCardView.setStrokeColor(Color.BLUE);
            materialCardView.setStrokeWidth(Utilities.SELECTED_STROKE_WIDTH);
        } else materialCardView.setStrokeWidth(0);

        holder.itemView.setOnClickListener(view -> {
            migrationView.secondSelection = position;
            migrationView.refresh();
        });
    }


    @Override
    public int getItemCount() {
        if (migrationView.novelResults.size() > 0)
            return migrationView.novelResults.get(migrationView.selection).size();
        else return 0;
    }

}
