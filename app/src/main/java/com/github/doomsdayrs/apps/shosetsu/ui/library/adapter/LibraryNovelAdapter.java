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
import com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders.LibNovelViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.getNovel;

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
public class LibraryNovelAdapter extends RecyclerView.Adapter<LibNovelViewHolder> {
    private final LibraryFragment libraryFragment;
    private final ArrayList<Integer> novelCards;

    public LibraryNovelAdapter(ArrayList<Integer> novelCards, LibraryFragment libraryFragment) {
        this.libraryFragment = libraryFragment;
        this.novelCards = novelCards;
    }

    @NonNull
    @Override
    public LibNovelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_novel_card, viewGroup, false);
        return new LibNovelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibNovelViewHolder libNovelViewHolder, int i) {
        NovelCard novelCard = getNovel(novelCards.get(i));
        //Sets values
        {
            Picasso.get()
                    .load(novelCard.imageURL)
                    .into(libNovelViewHolder.library_card_image);
            libNovelViewHolder.libraryFragment = libraryFragment;
            libNovelViewHolder.novelCard = novelCard;
            libNovelViewHolder.formatter = DefaultScrapers.getByID(novelCard.formatterID);
            libNovelViewHolder.library_card_title.setText(novelCard.title);

            switch (Settings.themeMode) {
                case 0:
                    libNovelViewHolder.library_card_title.setBackgroundResource(R.color.white_trans);
                    break;
                case 1:
                case 2:
                    libNovelViewHolder.library_card_title.setBackgroundResource(R.color.black_trans);
                    break;
            }
        }

        int count = Database.DatabaseChapter.getCountOfChaptersUnread(novelCard.novelID);
        if (count != 0) {
            libNovelViewHolder.chip.setVisibility(View.VISIBLE);
            libNovelViewHolder.chip.setText(String.valueOf(count));
        } else libNovelViewHolder.chip.setVisibility(View.INVISIBLE);

        if (libraryFragment.contains(novelCard.novelID)) {
            libNovelViewHolder.materialCardView.setStrokeWidth(Utilities.SELECTED_STROKE_WIDTH);
        } else {
            libNovelViewHolder.materialCardView.setStrokeWidth(0);
        }

        if (libraryFragment.selectedNovels.size() > 0) {
            libNovelViewHolder.itemView.setOnClickListener(view -> libNovelViewHolder.addToSelect());
        } else {
            libNovelViewHolder.itemView.setOnClickListener(libNovelViewHolder);
        }
    }

    @Override
    public int getItemCount() {
        return novelCards.size();
    }
}
