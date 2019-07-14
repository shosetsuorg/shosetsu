package com.github.doomsdayrs.apps.shosetsu.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.main.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.viewholders.LibraryViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
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
            libraryViewHolder.formatter = DefaultScrapers.formatters.get(novelCard.formatterID - 1);
            libraryViewHolder.library_card_title.setText(novelCard.title);
        }

        int count = Database.DatabaseChapter.getCountOfChaptersUnread(novelCard.novelURL);
        if (count != 0) {
            libraryViewHolder.chip.setVisibility(View.VISIBLE);
            libraryViewHolder.chip.setText(String.valueOf(count));
        } else libraryViewHolder.chip.setVisibility(View.INVISIBLE);

        if (LibraryFragment.contains(novelCard)) {
            System.out.println("Is selected");
            libraryViewHolder.materialCardView.setStrokeColor(Color.BLUE);
        } else {
            System.out.println("Is selected");
            libraryViewHolder.materialCardView.setStrokeColor(Color.WHITE);
        }

        if (LibraryFragment.selectedNovels.size() > 0) {
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
