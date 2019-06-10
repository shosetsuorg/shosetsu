package com.github.Doomsdayrs.apps.shosetsu.adapters.novel;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.fragment.novel.NovelFragmentChapterView;
import com.github.Doomsdayrs.apps.shosetsu.fragment.novel.NovelFragmentChapters;

import java.io.IOException;
import java.util.List;

/**
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Foobar is distributed in the hope that it will be useful,
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
public class NovelChaptersAdapter extends RecyclerView.Adapter<NovelChaptersAdapter.ChaptersViewHolder> {
    private NovelFragmentChapters novelFragmentChapters;
    private static Formatter formatter;
    private FragmentManager fragmentManager;
    private List<NovelChapter> novelChapters;


    public NovelChaptersAdapter(NovelFragmentChapters novelFragmentChapters, List<NovelChapter> novels, FragmentManager fragmentManager, Formatter formatter) {
        this.novelFragmentChapters = novelFragmentChapters;
        this.novelChapters = novels;
        this.fragmentManager = fragmentManager;
        NovelChaptersAdapter.formatter = formatter;
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
            NovelFragmentChapterView novelFragmentChapterView = new NovelFragmentChapterView();
            novelFragmentChapterView.setFormatter(formatter);
            novelFragmentChapterView.setURL(novelChapter.link);
            Log.d("Transaction", "Chapters > chapter view");
            fragmentManager.beginTransaction()
                    .addToBackStack("tag")
                    .replace(R.id.fragment_novel_chapters_recycler, novelFragmentChapterView)
                    .commit();


            /*Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.fragment_novel_chapter_view);
            TextView textView = dialog.findViewById(R.id.fragment_novel_chapter_view_text);
            try {
                textView.setText(new getNovel().execute(novelChapter.link).get().replaceAll("\n", "\n\n"));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dialog.show();
*/

        }
    }
}
