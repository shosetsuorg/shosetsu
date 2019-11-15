package com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isOnline;

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
public class MigrationViewCatalogueAdapter extends RecyclerView.Adapter<MigrationViewCatalogueAdapter.CatalogueHolder> {
    private final ArrayList<CatalogueCard> catalogues;
    private final MigrationView migrationView;

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

        void setFormatter(Formatter formatter) {
            this.formatter = formatter;
        }

        @Override
        public void onClick(@NonNull View v) {
            Log.d("FormatterSelection", formatter.getName());
            if (isOnline()) {

                Log.d("Target", String.valueOf(formatter.getID()));
                migrationView.target = formatter.getID();
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
