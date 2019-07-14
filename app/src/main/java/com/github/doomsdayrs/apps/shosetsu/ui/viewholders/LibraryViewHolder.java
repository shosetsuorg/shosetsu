package com.github.doomsdayrs.apps.shosetsu.ui.viewholders;
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
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.main.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragmentChapters;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

public class LibraryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public MaterialCardView materialCardView;
    public final ImageView library_card_image;
    public final TextView library_card_title;
    public final Chip chip;

    public LibraryFragment libraryFragment;
    public Formatter formatter;
    public NovelCard novelCard;

    public LibraryViewHolder(@NonNull View itemView) {
        super(itemView);
        materialCardView = itemView.findViewById(R.id.novel_item_card);
        library_card_image = itemView.findViewById(R.id.novel_item_image);
        library_card_title = itemView.findViewById(R.id.textView);
        chip = itemView.findViewById(R.id.novel_item_left_to_read);


        itemView.setOnLongClickListener(view -> {
            addToSelect();
            return true;
        });
    }

    public void addToSelect() {
        if (!LibraryFragment.contains(novelCard))
            LibraryFragment.selectedNovels.add(novelCard);
        else
            removeFromSelect();

        if (LibraryFragment.selectedNovels.size() == 1 || LibraryFragment.selectedNovels.size() <= 0)
            libraryFragment.onCreateOptionsMenu(libraryFragment.menu, libraryFragment.getInflater());
        libraryFragment.recyclerView.post(() -> libraryFragment.libraryNovelCardsAdapter.notifyDataSetChanged());
    }

    private void removeFromSelect() {
        if (LibraryFragment.contains(novelCard))
            for (int x = 0; x < LibraryFragment.selectedNovels.size(); x++)
                if (LibraryFragment.selectedNovels.get(x).novelURL.equalsIgnoreCase(novelCard.novelURL)) {
                    LibraryFragment.selectedNovels.remove(x);
                    return;
                }
    }

    @Override
    public void onClick(View v) {
        NovelFragment novelFragment = new NovelFragment();
        StaticNovel.formatter = formatter;
        StaticNovel.novelURL = novelCard.novelURL;
        novelFragment.fragmentManager = libraryFragment.getFragmentManager();
        assert libraryFragment.getFragmentManager() != null;
        libraryFragment.getFragmentManager().beginTransaction()
                .addToBackStack("tag")
                .replace(R.id.fragment_container, novelFragment)
                .commit();
    }
}
