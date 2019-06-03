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

import java.util.ArrayList;

public class CatalogueCardsAdapter extends RecyclerView.Adapter<CatalogueCardsAdapter.CatalogueHolder> {
    private ArrayList<CatalogueCard> recycleCards;
    private FragmentManager fragmentManager;
    private Context context;

    public CatalogueCardsAdapter(ArrayList<CatalogueCard> recycleCards, FragmentManager fragmentManager) {
        this.recycleCards = recycleCards;
        this.fragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public CatalogueHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.catalogue_item_card, viewGroup, false);
        context = viewGroup.getContext();
        return new CatalogueHolder(view, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogueHolder catalogueHolder, int i) {

        CatalogueCard catalogueCard = recycleCards.get(i);
        catalogueHolder.setFormatter(catalogueCard.formatter);

        if (catalogueCard.formatter.getImageURL() != null && !catalogueCard.formatter.getImageURL().isEmpty())
            Glide.with(context)
                    .asBitmap()
                    .load(catalogueCard.formatter.getImageURL())
                    .into(catalogueHolder.library_card_image);
        else
            catalogueHolder.library_card_image.setImageResource(catalogueCard.libraryImageResource);
        catalogueHolder.library_card_title.setText(catalogueCard.libraryText);
    }

    @Override
    public int getItemCount() {
        return recycleCards.size();
    }

    static class CatalogueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView library_card_image;
        public TextView library_card_title;
        public Formatter formatter;
        FragmentManager fragmentManager;

        public CatalogueHolder(@NonNull View itemView, FragmentManager fragmentManager) {
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
            fragmentManager.beginTransaction()
                    .addToBackStack("tag")
                    .replace(R.id.fragment_container, catalogueFragement)
                    .commit();
        }
    }
}
