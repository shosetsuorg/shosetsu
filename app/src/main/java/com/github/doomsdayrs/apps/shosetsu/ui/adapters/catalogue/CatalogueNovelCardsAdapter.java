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
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard;
import com.squareup.picasso.Picasso;

import java.util.List;

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
            if (recycleCard.imageURL != null)
                Picasso.get().load(recycleCard.imageURL).into(novelCardsViewHolder.library_card_image);
            else novelCardsViewHolder.library_card_image.setVisibility(View.GONE);
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
            novelFragment.fragmentManager = fragmentManager;
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
