package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.NovelCardViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;
import com.squareup.picasso.Picasso;

import java.util.List;

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
public class CatalogueAdapter extends RecyclerView.Adapter<NovelCardViewHolder> {
    private List<CatalogueNovelCard> recycleCards;
    private final CatalogueFragment catalogueFragment;
    private final Formatter formatter;

    public CatalogueAdapter(List<CatalogueNovelCard> recycleCards, CatalogueFragment catalogueFragment, Formatter formatter) {
        this.recycleCards = recycleCards;
        this.catalogueFragment = catalogueFragment;
        this.formatter = formatter;
    }

    @NonNull
    @Override
    public NovelCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_novel_card, viewGroup, false);
        NovelCardViewHolder novelCardsViewHolder = new NovelCardViewHolder(view);
        novelCardsViewHolder.catalogueFragment = catalogueFragment;
        novelCardsViewHolder.formatter = formatter;
        return novelCardsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NovelCardViewHolder novelCardsViewHolder, int i) {
        CatalogueNovelCard recycleCard = recycleCards.get(i);
        if (recycleCard != null) {
            novelCardsViewHolder.url = recycleCard.novelURL;
            novelCardsViewHolder.library_card_title.setText(recycleCard.title);
            if (recycleCard.imageURL != null) {
                Picasso.get().load(recycleCard.imageURL).into(novelCardsViewHolder.library_card_image);
                Picasso.get()
                        .load(recycleCard.imageURL)
                        .into(novelCardsViewHolder.library_card_image);
            } else novelCardsViewHolder.library_card_image.setVisibility(View.GONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Database.DatabaseLibrary.isBookmarked(recycleCard.novelURL)) {
                    if (catalogueFragment.getContext() != null)
                        novelCardsViewHolder.constraintLayout.setForeground(new ColorDrawable(ContextCompat.getColor(catalogueFragment.getContext(), R.color.shade)));
                } else novelCardsViewHolder.constraintLayout.setForeground(new ColorDrawable());
            } else {
                //TODO Tint for cards before 22
            }

            switch (Settings.themeMode) {
                case 0:
                    novelCardsViewHolder.library_card_title.setBackgroundResource(R.color.white_trans);
                    break;
                case 1:
                case 2:
                    novelCardsViewHolder.library_card_title.setBackgroundResource(R.color.black_trans);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return recycleCards.size();
    }
}
