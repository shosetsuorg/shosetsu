package com.github.Doomsdayrs.apps.shosetsu.adapters.novel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.apps.shosetsu.R;

import java.util.List;

public class NovelChaptersAdapter extends RecyclerView.Adapter<NovelChaptersAdapter.ChaptersViewHolder> {
    private List<NovelChapter> novelChapters;
    private final Formatter formatter;
    private final String URL;


    public NovelChaptersAdapter(Formatter formatter, String url) {
        this.formatter = formatter;
        URL = url;
    }


    public int getCount() {
        return novelChapters.size();
    }

    @NonNull
    @Override
    public ChaptersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_novel_chapters, viewGroup, false);
        return new ChaptersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChaptersViewHolder chaptersViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ChaptersViewHolder extends RecyclerView.ViewHolder {
        TextView library_card_title;

        public ChaptersViewHolder(@NonNull View itemView) {
            super(itemView);
            library_card_title = itemView.findViewById(R.id.textView);
        }
    }
}
