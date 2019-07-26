package com.github.doomsdayrs.apps.shosetsu.ui.adapters.catalogue;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
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
 * @author github.com/hXtreme
 */
public class CatalogueNovelCardsAdapter extends RecyclerView.Adapter<CatalogueNovelCardsAdapter.NovelCardsViewHolder> {
    private List<CatalogueNovelCard> recycleCards;
    private final FragmentManager fragmentManager;
    private final Formatter formatter;

    public CatalogueNovelCardsAdapter(List<CatalogueNovelCard> recycleCards, FragmentManager fragmentManager, Formatter formatter) {
        this.recycleCards = recycleCards;
        this.fragmentManager = fragmentManager;
        this.formatter = formatter;
    }

    @NonNull
    @Override
    public NovelCardsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_novel_card, viewGroup, false);
        NovelCardsViewHolder novelCardsViewHolder = new NovelCardsViewHolder(view);
        novelCardsViewHolder.fragmentManager = fragmentManager;
        novelCardsViewHolder.formatter = formatter;
        return novelCardsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NovelCardsViewHolder novelCardsViewHolder, int i) {
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

    static class NovelCardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        FragmentManager fragmentManager;
        Formatter formatter;
        final ImageView library_card_image;
        final TextView library_card_title;
        String url;

        NovelCardsViewHolder(@NonNull View itemView) {
            super(itemView);
            library_card_image = itemView.findViewById(R.id.novel_item_image);
            library_card_title = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NovelFragment novelFragment = new NovelFragment();
            StaticNovel.formatter = formatter;
            StaticNovel.novelURL = url;

            fragmentManager.beginTransaction()
                    .addToBackStack("tag")
                    .replace(R.id.fragment_container, novelFragment)
                    .commit();
        }

        @Override
        public boolean onLongClick(View view) {
            new add(this).execute(view);
            return true;
        }

        static class add extends AsyncTask<View, Void, Void> {
            NovelCardsViewHolder novelCardsViewHolder;

            public add(NovelCardsViewHolder novelCardsViewHolder) {
                this.novelCardsViewHolder = novelCardsViewHolder;
            }

            @Override
            protected Void doInBackground(View... views) {
                try {
                    if (!Database.DatabaseLibrary.inLibrary(novelCardsViewHolder.url)) {
                        Database.DatabaseLibrary.addToLibrary(novelCardsViewHolder.formatter.getID(), novelCardsViewHolder.formatter.parseNovel(novelCardsViewHolder.url), novelCardsViewHolder.url, com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.getA());
                        if (views[0] != null)
                            views[0].post(() -> Toast.makeText(views[0].getContext(), "Added " + novelCardsViewHolder.library_card_title.getText().toString(), Toast.LENGTH_SHORT).show());
                    }
                    if (Database.DatabaseLibrary.isBookmarked(novelCardsViewHolder.url)) {
                        if (views[0] != null)
                            views[0].post(() -> Toast.makeText(views[0].getContext(), "Already in the library", Toast.LENGTH_SHORT).show());
                    } else {
                        Database.DatabaseLibrary.bookMark(novelCardsViewHolder.url);
                        if (views[0] != null)
                            views[0].post(() -> Toast.makeText(views[0].getContext(), "Added " + novelCardsViewHolder.library_card_title.getText().toString(), Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    if (views[0] != null)
                        views[0].post(() -> Toast.makeText(views[0].getContext(), "Failed to add to library: " + novelCardsViewHolder.library_card_title.getText().toString(), Toast.LENGTH_LONG).show());
                }
                return null;
            }
        }

    }



}
