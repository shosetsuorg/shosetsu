package com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders;
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
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
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
        if (!libraryFragment.contains(novelCard))
            libraryFragment.selectedNovels.add(novelCard);
        else
            removeFromSelect();

        if (libraryFragment.selectedNovels.size() == 1 || libraryFragment.selectedNovels.size() <= 0)
            libraryFragment.onCreateOptionsMenu(libraryFragment.menu, libraryFragment.getInflater());
        libraryFragment.recyclerView.post(() -> libraryFragment.libraryNovelCardsAdapter.notifyDataSetChanged());
    }

    private void removeFromSelect() {
        if (libraryFragment.contains(novelCard))
            for (int x = 0; x < libraryFragment.selectedNovels.size(); x++)
                if (libraryFragment.selectedNovels.get(x).novelURL.equalsIgnoreCase(novelCard.novelURL)) {
                    libraryFragment.selectedNovels.remove(x);
                    return;
                }
    }

    @Override
    public void onClick(View v) {
        NovelFragment novelFragment = new NovelFragment();
        StaticNovel.formatter = formatter;
        StaticNovel.novelURL = novelCard.novelURL;
        assert libraryFragment.getFragmentManager() != null;
        libraryFragment.getFragmentManager().beginTransaction()
                .addToBackStack("tag")
                .replace(R.id.fragment_container, novelFragment)
                .commit();
    }
}
