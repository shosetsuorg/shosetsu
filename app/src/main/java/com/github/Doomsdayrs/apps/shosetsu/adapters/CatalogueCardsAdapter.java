package com.github.Doomsdayrs.apps.shosetsu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.fragment.CatalogueFragement;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.CatalogueCard;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.RecycleCard;

import java.util.ArrayList;

public class CatalogueCardsAdapter extends RecyclerView.Adapter<CatalogueCardsAdapter.NovelCardsViewHolder> {
    private ArrayList<CatalogueCard> recycleCards;
    private FragmentManager fragmentManager;
    private Context context;

    public CatalogueCardsAdapter(ArrayList<CatalogueCard> recycleCards, FragmentManager fragmentManager) {
        this.recycleCards = recycleCards;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public NovelCardsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_item_card, viewGroup, false);
        context = viewGroup.getContext();
        return new NovelCardsViewHolder(view, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull NovelCardsViewHolder novelCardsViewHolder, int i) {

        CatalogueCard catalogueCard = recycleCards.get(i);
        novelCardsViewHolder.setFormatter(catalogueCard.formatter);

        if (catalogueCard.formatter.getImageURL() != null && !catalogueCard.formatter.getImageURL().isEmpty())
            Glide.with(context)
                    .asBitmap()
                    .load(catalogueCard.formatter.getImageURL())
                    .into(novelCardsViewHolder.library_card_image);
        else
            novelCardsViewHolder.library_card_image.setImageResource(catalogueCard.libraryImageResource);
        novelCardsViewHolder.library_card_title.setText(catalogueCard.libraryText);
    }

    @Override
    public int getItemCount() {
        return recycleCards.size();
    }

    static class NovelCardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView library_card_image;
        public TextView library_card_title;
        public Formatter formatter;
        FragmentManager fragmentManager;

        public NovelCardsViewHolder(@NonNull View itemView, FragmentManager fragmentManager) {
            super(itemView);
            library_card_image = itemView.findViewById(R.id.catalogue_item_card_image);
            library_card_title = itemView.findViewById(R.id.catalogue_item_card_text);
            this.fragmentManager = fragmentManager;
        }

        public void setFormatter(Formatter formatter) {
            this.formatter = formatter;
            Log.d("FormatterSet", formatter.getName());
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("ForrmatterSelection", formatter.getName());
            CatalogueFragement catalogueFragement = new CatalogueFragement();
            catalogueFragement.setFormatter(formatter);
            setFormatter(formatter);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, catalogueFragement).commit();
        }
    }
}
