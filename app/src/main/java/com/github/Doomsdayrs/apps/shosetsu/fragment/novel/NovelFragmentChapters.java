package com.github.Doomsdayrs.apps.shosetsu.fragment.novel;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.Novel;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.adapters.novel.NovelChaptersAdapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
public class NovelFragmentChapters extends Fragment {

    public List<NovelChapter> novelChapters;
    private Formatter formatter;
    private String URL;
    private FragmentManager fragmentManager;
    public RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public int currentMaxPage = 1;
    private Context context;

    public NovelFragmentChapters() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreate", "NovelFragmentChapters");
        View view = inflater.inflate(R.layout.fragment_novel_chapters, container, false);
        recyclerView = view.findViewById(R.id.fragment_novel_chapters_recycler);
        setNovels(novelChapters);
        this.context = container.getContext();
        return view;
    }

    public void setNovels(List<NovelChapter> novels) {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);

        adapter = new NovelChaptersAdapter(novels, fragmentManager, formatter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    static class setLatest extends AsyncTask<Integer, Void, Boolean> {
        NovelFragmentChapters novelFragmentChapters;

        public setLatest(NovelFragmentChapters novelFragmentChapters) {
            this.novelFragmentChapters = novelFragmentChapters;
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            if (novelFragmentChapters.formatter.isIncrementingChapterList())
                try {
                    List<Novel> novels;
                    if (integers.length == 0)
                        novels = novelFragmentChapters.formatter.parseLatest(novelFragmentChapters.formatter.parseNovel(UR);
                    else
                        novels = novelFragmentChapters.formatter.parseLatest(novelFragmentChapters.formatter.getLatestURL(integers[0]));

                    for (Novel novel : novels)
                        novelFragmentChapters.novelChapters.add(new NovelChapter()));
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            return false;
        }
    }

    static class bottom extends RecyclerView.OnScrollListener {
        NovelFragmentChapters novelFragmentChapters;
        boolean running = false;

        public bottom(NovelFragmentChapters novelFragmentChapters) {
            this.novelFragmentChapters = novelFragmentChapters;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

            if (!running)
                if (!novelFragmentChapters.recyclerView.canScrollVertically(1)) {
                    running = true;
                    novelFragmentChapters.currentMaxPage++;
                    try {
                        if (new novelFragmentChapters.setLatest().execute(catalogueFragement.currentMaxPage).get()) {
                            catalogueFragement.setLibraryCards(libraryCards);
                            running = true;
                        }

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }


}
