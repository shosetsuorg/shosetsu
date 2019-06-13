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
import android.widget.Button;

import com.github.Doomsdayrs.api.novelreader_core.main.DefaultScrapers;
import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelPage;
import com.github.Doomsdayrs.apps.shosetsu.R;
import com.github.Doomsdayrs.apps.shosetsu.adapters.novel.NovelChaptersAdapter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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


    public static List<NovelChapter> novelChapters;
    public boolean reversed;
    public RecyclerView recyclerView;
    public int currentMaxPage = 1;
    Button button;
    private Formatter formatter;
    private String novelURL;
    private FragmentManager fragmentManager;
    private RecyclerView.Adapter adapter;
    private Context context;

    public NovelFragmentChapters() {
        setHasOptionsMenu(true);
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setNovelURL(String novelURL) {
        this.novelURL = novelURL;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", novelURL);
        outState.putInt("formatter", formatter.getID());
        outState.putInt("maxPage", currentMaxPage);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreate", "NovelFragmentChapters");
        View view = inflater.inflate(R.layout.fragment_novel_chapters, container, false);
        recyclerView = view.findViewById(R.id.fragment_novel_chapters_recycler);
        if (savedInstanceState != null) {
            novelURL = savedInstanceState.getString("url");
            formatter = DefaultScrapers.formatters.get(savedInstanceState.getInt("formatter") - 1);
            currentMaxPage = savedInstanceState.getInt("maxPage");
        }

        setNovels(novelChapters);
        this.context = Objects.requireNonNull(container).getContext();
        Log.d("OnCreate", "Complete");
        return view;
    }

    public void setNovels(List<NovelChapter> novels) {
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        adapter = new NovelChaptersAdapter(this, novels, fragmentManager, formatter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new bottom(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_chapters, menu);
        button = (Button) menu.findItem(R.id.chapter_filter).getActionView();
        button.setOnClickListener(new onFilter(this));
    }


    static class onFilter implements View.OnClickListener {
        NovelFragmentChapters novelFragmentChapters;

        public onFilter(NovelFragmentChapters novelFragmentChapters) {
            this.novelFragmentChapters = novelFragmentChapters;
        }

        @Override
        public void onClick(View v) {
            Collections.reverse(novelChapters);
            novelFragmentChapters.reversed = true;
            novelFragmentChapters.recyclerView.post(() -> novelFragmentChapters.adapter.notifyDataSetChanged());
        }
    }


    static class addMore extends AsyncTask<Integer, Void, Boolean> {
        NovelFragmentChapters novelFragmentChapters;

        addMore(NovelFragmentChapters novelFragmentChapters) {
            this.novelFragmentChapters = novelFragmentChapters;
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            if (novelFragmentChapters.formatter.isIncrementingChapterList())
                try {
                    NovelPage novelPage;
                    if (integers.length == 0)
                        novelPage = novelFragmentChapters.formatter.parseNovel("http://novelfull.com/" + novelFragmentChapters.novelURL);
                    else
                        novelPage = novelFragmentChapters.formatter.parseNovel("http://novelfull.com" + novelFragmentChapters.novelURL, integers[0]);
                    //TODO Difference calculation

                    if (!novelPage.novelChapters.get(novelPage.novelChapters.size() - 1).link
                            .equals(novelChapters.get(novelChapters.size() - 1).link))
                        novelChapters.addAll(novelPage.novelChapters);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return false;
        }
    }

    static class bottom extends RecyclerView.OnScrollListener {
        NovelFragmentChapters novelFragmentChapters;
        boolean running = false;

        bottom(NovelFragmentChapters novelFragmentChapters) {
            this.novelFragmentChapters = novelFragmentChapters;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

            if (!running)
                if (!novelFragmentChapters.recyclerView.canScrollVertically(1)) {
                    Log.d("NovelFragmentsScrollLoad", "Loading...");
                    if (novelFragmentChapters.reversed)
                        Collections.reverse(novelChapters);
                    running = true;
                    novelFragmentChapters.currentMaxPage++;
                    try {
                        if (new addMore(novelFragmentChapters).execute(novelFragmentChapters.currentMaxPage).get())
                            novelFragmentChapters.recyclerView.post(() -> novelFragmentChapters.adapter.notifyDataSetChanged());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("NovelFragmentsScrollLoad", "Completed.");
                    running = false;
                    if (novelFragmentChapters.reversed)
                        Collections.reverse(novelChapters);
                }
        }
    }


}
