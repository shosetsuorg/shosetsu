package com.github.Doomsdayrs.apps.shosetsu.adapters.novel;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.fragment.novel.NovelFragmentChapterView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NovelChaptersAdapter extends RecyclerView.Adapter<NovelChaptersAdapter.ChaptersViewHolder> {
    private FragmentManager fragmentManager;
    private List<NovelChapter> novelChapters;
    private static Formatter formatter;


    public NovelChaptersAdapter(List<NovelChapter> novels, FragmentManager fragmentManager, Formatter formatter) {
        this.novelChapters = novels;
        this.fragmentManager = fragmentManager;
        this.formatter = formatter;
    }


    @NonNull
    @Override
    public ChaptersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_novel_chapter, viewGroup, false);
        return new ChaptersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChaptersViewHolder chaptersViewHolder, int i) {
        NovelChapter novelChapter = novelChapters.get(i);
        chaptersViewHolder.novelChapter = novelChapter;
        chaptersViewHolder.fragmentManager = fragmentManager;
        chaptersViewHolder.library_card_title.setText(novelChapter.chapterNum);
    }

    @Override
    public int getItemCount() {
        return novelChapters.size();
    }

    class ChaptersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        NovelChapter novelChapter;
        FragmentManager fragmentManager;
        TextView library_card_title;

        ChaptersViewHolder(@NonNull View itemView) {
            super(itemView);
            library_card_title = itemView.findViewById(R.id.recycler_novel_chapter_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //NovelFragmentChapterView novelFragmentChapterView = new NovelFragmentChapterView();
            //novelFragmentChapterView.setFormatter(formatter);
            //novelFragmentChapterView.setURL(novelChapter.link);

            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.fragment_novel_chapter_view);
            TextView textView = dialog.findViewById(R.id.fragment_novel_chapter_view_text);
            try {
                textView.setText(new getNovel().execute(novelChapter.link).get().replaceAll("\n","\n\n"));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dialog.show();

            //fragmentManager.beginTransaction().addToBackStack("tag").replace(R.id.fragment_container, novelFragmentChapterView).commit();
        }
    }

    static class getNovel extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return formatter.getNovelPassage(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
