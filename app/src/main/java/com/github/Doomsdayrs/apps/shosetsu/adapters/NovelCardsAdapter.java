package com.github.Doomsdayrs.apps.shosetsu.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.RecycleCard;

import java.util.ArrayList;

public class NovelCardsAdapter extends RecyclerView.Adapter<NovelCardsAdapter.NovelCardsViewHolder> {
    private ArrayList<RecycleCard> recycleCards;

    static class NovelCardsViewHolder extends RecyclerView.ViewHolder {
        public ImageView library_card_image;
        public TextView library_card_title;

        public NovelCardsViewHolder(@NonNull View itemView) {
            super(itemView);
            library_card_image = itemView.findViewById(R.id.catalogue_item_card_image);
            library_card_title = itemView.findViewById(R.id.catalogue_item_card_text);
        }
    }

    public NovelCardsAdapter(ArrayList<RecycleCard> recycleCards){
        this.recycleCards = recycleCards;
    }

    @NonNull
    @Override
    public NovelCardsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_item_card, viewGroup, false);
        return new NovelCardsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NovelCardsViewHolder novelCardsViewHolder, int i) {
        RecycleCard recycleCard = recycleCards.get(i);
        novelCardsViewHolder.library_card_image.setImageResource(recycleCard.libraryImageResource);
        novelCardsViewHolder.library_card_title.setText(recycleCard.libraryText);
    }


    @Override
    public int getItemCount() {
        return recycleCards.size();
    }
}
