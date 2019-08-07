package com.github.doomsdayrs.apps.shosetsu.ui.library.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders.LibraryViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;
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
public class LibraryNovelAdapter extends RecyclerView.Adapter<LibraryViewHolder> {
    private final LibraryFragment libraryFragment;
    private final ArrayList<NovelCard> novelCards;

    public LibraryNovelAdapter(ArrayList<NovelCard> novelCards, LibraryFragment libraryFragment) {
        this.libraryFragment = libraryFragment;
        this.novelCards = novelCards;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_novel_card, viewGroup, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder libraryViewHolder, int i) {
        NovelCard novelCard = novelCards.get(i);
        //Sets values
        {
            Picasso.get()
                .load(novelCard.imageURL)
                .into(libraryViewHolder.library_card_image);
            libraryViewHolder.libraryFragment = libraryFragment;
            libraryViewHolder.novelCard = novelCard;
            libraryViewHolder.formatter = DefaultScrapers.getByID(novelCard.formatterID);
            libraryViewHolder.library_card_title.setText(novelCard.title);

            switch (Settings.themeMode) {
                case 0:
                    libraryViewHolder.library_card_title.setBackgroundResource(R.color.white_trans);
                    break;
                case 1:
                case 2:
                    libraryViewHolder.library_card_title.setBackgroundResource(R.color.black_trans);
                    break;
            }
        }

        int count = Database.DatabaseChapter.getCountOfChaptersUnread(novelCard.novelURL);
        if (count != 0) {
            libraryViewHolder.chip.setVisibility(View.VISIBLE);
            libraryViewHolder.chip.setText(String.valueOf(count));
        } else libraryViewHolder.chip.setVisibility(View.INVISIBLE);

        if (libraryFragment.contains(novelCard)) {
            libraryViewHolder.materialCardView.setStrokeWidth(Utilities.SELECTED_STROKE_WIDTH);
        } else {
            libraryViewHolder.materialCardView.setStrokeWidth(0);
        }

        if (libraryFragment.selectedNovels.size() > 0) {
            libraryViewHolder.itemView.setOnClickListener(view -> libraryViewHolder.addToSelect());
        } else {
            libraryViewHolder.itemView.setOnClickListener(libraryViewHolder);
        }
    }

    @Override
    public int getItemCount() {
        return novelCards.size();
    }
}
