package com.github.Doomsdayrs.apps.shosetsu.adapters.catalogue;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.fragment.novel.NovelFragment;
import com.github.Doomsdayrs.apps.shosetsu.recycleObjects.NovelCard;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class CatalogueNovelCardsAdapter extends RecyclerView.Adapter<CatalogueNovelCardsAdapter.NovelCardsViewHolder> {
    private FragmentManager fragmentManager;
    static List<NovelCard> recycleCards;
    private Context context;
    private Formatter formatter;

    static class NovelCardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        FragmentManager fragmentManager;
        Formatter formatter;
        ImageView library_card_image;
        TextView library_card_title;
        URI uri;

        NovelCardsViewHolder(@NonNull View itemView) {
            super(itemView);
            library_card_image = itemView.findViewById(R.id.novel_item_image);
            library_card_title = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NovelFragment novelFragment = new NovelFragment();
            novelFragment.setFormatter(formatter);
            novelFragment.setURL(uri.getPath());
            fragmentManager.beginTransaction()
                    .addToBackStack("tag")
                    .replace(R.id.fragment_container, novelFragment)
                    .commit();
        }
    }

    public CatalogueNovelCardsAdapter(Context context, List<NovelCard> recycleCards, FragmentManager fragmentManager, Formatter formatter) {
        this.context = context;
        if (CatalogueNovelCardsAdapter.recycleCards !=null && !CatalogueNovelCardsAdapter.recycleCards.containsAll(recycleCards)){
            CatalogueNovelCardsAdapter.recycleCards = null;
            CatalogueNovelCardsAdapter.recycleCards = recycleCards;
        }
        else if (CatalogueNovelCardsAdapter.recycleCards == null)
            CatalogueNovelCardsAdapter.recycleCards = recycleCards;

        this.fragmentManager = fragmentManager;
        this.formatter = formatter;
    }

    @NonNull
    @Override
    public NovelCardsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_novel_card, viewGroup, false);
        NovelCardsViewHolder novelCardsViewHolder = new NovelCardsViewHolder(view);
        novelCardsViewHolder.fragmentManager = fragmentManager;
        novelCardsViewHolder.formatter = formatter;
        return novelCardsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NovelCardsViewHolder novelCardsViewHolder, int i) {
        NovelCard recycleCard = recycleCards.get(i);
        if (recycleCard != null) {
            try {
                novelCardsViewHolder.uri = new URI(recycleCard.URL);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            novelCardsViewHolder.library_card_title.setText(recycleCard.title);
            Picasso.get().load(recycleCard.libraryImageResource).into(novelCardsViewHolder.library_card_image);
        }

    }


    @Override
    public int getItemCount() {
        return recycleCards.size();
    }
}
