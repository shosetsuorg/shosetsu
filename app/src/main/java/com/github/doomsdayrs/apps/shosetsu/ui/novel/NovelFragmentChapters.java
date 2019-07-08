package com.github.doomsdayrs.apps.shosetsu.ui.novel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.async.ChapterLoader;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.novel.NovelChaptersAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.NovelFragmentChaptersOnFilter;

import java.util.ArrayList;
import java.util.Objects;

/*
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

/**
 * Displays the chapters the novel contains
 * TODO Check filesystem if the chapter is saved, even if not in DB.
 * TODO Selection mechanic
 */
public class NovelFragmentChapters extends Fragment {
    public static ArrayList<NovelChapter> selectedChapters = new ArrayList<>();

    public static boolean contains(NovelChapter novelChapter) {
        for (NovelChapter n : selectedChapters)
            if (n.link.equalsIgnoreCase(novelChapter.link))
                return true;
        return false;
    }


    public boolean reversed;
    @SuppressLint("StaticFieldLeak")
    public static RecyclerView recyclerView;
    public int currentMaxPage = 1;
    public static NovelChaptersAdapter adapter;
    private Context context;
    public ProgressBar progressBar;
    public SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Constructor
     */
    public NovelFragmentChapters() {
        setHasOptionsMenu(true);
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView = null;
        adapter = null;
    }

    /**
     * Save data of view before destroyed
     *
     * @param outState output save
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("maxPage", currentMaxPage);
    }

    /**
     * Creates view
     *
     * @param inflater           inflater to retrieve objects
     * @param container          container of this fragment
     * @param savedInstanceState save
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "NovelFragmentChapters");
        View view = inflater.inflate(R.layout.fragment_novel_chapters, container, false);
        recyclerView = view.findViewById(R.id.fragment_novel_chapters_recycler);
        progressBar = view.findViewById(R.id.fragment_novel_chapters_progress);
        swipeRefreshLayout = view.findViewById(R.id.fragment_novel_chapters_refresh);

        swipeRefreshLayout.setOnRefreshListener(() -> new ChapterLoader(this).execute(getActivity()));

        if (savedInstanceState != null) {
            currentMaxPage = savedInstanceState.getInt("maxPage");
        }
        setNovels();
        this.context = Objects.requireNonNull(container).getContext();
        return view;
    }

    /**
     * Sets the novel chapters down
     */
    public void setNovels() {
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(false);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            if (Database.DatabaseLibrary.inLibrary(StaticNovel.novelURL)) {
                StaticNovel.novelChapters = Database.DatabaseChapter.getChapters(StaticNovel.novelURL);
            }
            adapter = new NovelChaptersAdapter(this);
            adapter.setHasStableIds(true);
            recyclerView.setLayoutManager(layoutManager);

            //        if (StaticNovel.formatter.isIncrementingChapterList()) {
            //      if (SettingsController.isOnline())
            //        recyclerView.addOnScrollListener(new NovelFragmentChaptersHitBottom(this));

            // else recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //               @Override
            //              public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            //             if (!recyclerView.canScrollVertically(1))
            //                    Toast.makeText(getContext(), "You are offline, impossible to load more", Toast.LENGTH_SHORT).show();
            //   }
            //         });
            //     }
            recyclerView.setAdapter(adapter);
        }
    }

    public Menu menu;

    public MenuInflater getInflater() {
        return new MenuInflater(getContext());
    }

    /**
     * Creates the option menu (on the top toolbar)
     *
     * @param menu     Menu reference to fill
     * @param inflater Object to inflate the menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        menu.clear();
        //TODO Delete all, Delete Selected titles
        //TODO Fix menu items not being full actions
        //TODO give menu items listeners

        if (selectedChapters.size() <= 0) {
            inflater.inflate(R.menu.toolbar_chapters, menu);
            menu.findItem(R.id.chapter_filter).setOnMenuItemClickListener(new NovelFragmentChaptersOnFilter(this));
        } else inflater.inflate(R.menu.toolbar_chapters_selected, menu);

    }
}
