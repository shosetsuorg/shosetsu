package com.github.Doomsdayrs.apps.shosetsu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.CatalogueNovelCard;

import java.util.ArrayList;
import java.util.List;

public class CatalogueNovelCardsAdapter extends RecyclerView.Adapter<CatalogueNovelCardsAdapter.NovelCardsViewHolder> {
    private List<CatalogueNovelCard> recycleCards;
    private Context context;

    static class NovelCardsViewHolder extends RecyclerView.ViewHolder {
        public ImageView library_card_image;
        public TextView library_card_title;

        public NovelCardsViewHolder(@NonNull View itemView) {
            super(itemView);
            library_card_image = itemView.findViewById(R.id.catalogue_item_card_image);
            library_card_title = itemView.findViewById(R.id.catalogue_item_card_text);
        }
    }

    public CatalogueNovelCardsAdapter(Context context, List<CatalogueNovelCard> recycleCards) {
        this.context = context;
        this.recycleCards = recycleCards;
    }

    @NonNull
    @Override
    public NovelCardsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_item_card, viewGroup, false);
        return new NovelCardsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NovelCardsViewHolder novelCardsViewHolder, int i) {
        CatalogueNovelCard recycleCard = recycleCards.get(i);
        novelCardsViewHolder.library_card_title.setText(recycleCard.title);

        Glide.with(context)
                .asBitmap()
                .load(recycleCard.libraryImageResource)
                .into(novelCardsViewHolder.library_card_image);
    }


    @Override
    public int getItemCount() {
        return recycleCards.size();
    }
}
