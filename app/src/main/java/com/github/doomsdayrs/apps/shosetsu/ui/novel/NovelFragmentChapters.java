package com.github.doomsdayrs.apps.shosetsu.ui.novel;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.Doomsdayrs.api.novelreader_core.services.core.dep.Formatter;
import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.backend.settings.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.novel.NovelChaptersAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.NovelFragmentChaptersHitBottom;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.NovelFragmentChaptersOnFilter;
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers;

import java.util.ArrayList;
import java.util.List;
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
public class NovelFragmentChapters extends Fragment {
    public static List<NovelChapter> novelChapters = new ArrayList<>();
    public boolean reversed;
    @SuppressLint("StaticFieldLeak")
    public static RecyclerView recyclerView;
    public int currentMaxPage = 1;
    public Formatter formatter;
    public String novelURL;
    private FragmentManager fragmentManager;
    public static NovelChaptersAdapter adapter;
    private Context context;
    public ProgressBar progressBar;

    /**
     * Constructor
     */
    public NovelFragmentChapters() {
        setHasOptionsMenu(true);
    }

    /**
     * Sets the fragment manager
     *
     * @param fragmentManager fragment manager
     */
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
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
        outState.putString("novelURL", novelURL);
        outState.putInt("formatter", formatter.getID());
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
        if (savedInstanceState != null) {
            novelURL = savedInstanceState.getString("novelURL");
            formatter = DefaultScrapers.formatters.get(savedInstanceState.getInt("formatter") - 1);
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
            if (Database.DatabaseLibrary.inLibrary(novelURL)) {
                novelChapters = Database.DatabaseChapter.getChapters(novelURL);
            }
            adapter = new NovelChaptersAdapter(this, novelChapters, formatter);
            adapter.setHasStableIds(true);
            recyclerView.setLayoutManager(layoutManager);

            if (SettingsController.isOnline())
                recyclerView.addOnScrollListener(new NovelFragmentChaptersHitBottom(this));

            else recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (!recyclerView.canScrollVertically(1))
                        Toast.makeText(getContext(), "You are offline, impossible to load more", Toast.LENGTH_SHORT).show();
                }
            });

            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Creates the option menu (on the top toolbar)
     *
     * @param menu     Menu reference to fill
     * @param inflater Object to inflate the menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_chapters, menu);
        menu.findItem(R.id.chapter_filter).setOnMenuItemClickListener(new NovelFragmentChaptersOnFilter(this));
    }
}
