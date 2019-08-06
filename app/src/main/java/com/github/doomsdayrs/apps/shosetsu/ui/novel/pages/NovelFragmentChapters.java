package com.github.doomsdayrs.apps.shosetsu.ui.novel.pages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.StaticNovel;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.ChaptersAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.novel.async.ChapterLoader;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */

/**
 * Displays the chapters the novel contains
 * TODO Check filesystem if the chapter is saved, even if not in DB.
 */
public class NovelFragmentChapters extends Fragment {
    public ArrayList<NovelChapter> selectedChapters = new ArrayList<>();

    public boolean contains(NovelChapter novelChapter) {
        for (NovelChapter n : selectedChapters)
            if (n.link.equalsIgnoreCase(novelChapter.link))
                return true;
        return false;
    }

    private int currentMaxPage = 1;

    private int findMinPosition() {
        int min = StaticNovel.novelChapters.size();
        for (int x = 0; x < StaticNovel.novelChapters.size(); x++)
            if (contains(StaticNovel.novelChapters.get(x)))
                if (x < min)
                    min = x;
        return min;
    }

    public static boolean reversed;
    @SuppressLint("StaticFieldLeak")
    public static RecyclerView recyclerView;

    private int findMaxPosition() {
        int max = -1;
        for (int x = StaticNovel.novelChapters.size() - 1; x >= 0; x--)
            if (contains(StaticNovel.novelChapters.get(x)))
                if (x > max)
                    max = x;
        return max;
    }
    public static ChaptersAdapter adapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public NovelFragment novelFragment;
    public TextView pageCount;
    public FloatingActionButton resumeRead;

    /**
     * Constructor
     */
    public NovelFragmentChapters() {
        setHasOptionsMenu(true);
    }

    public void setNovelFragment(NovelFragment novelFragment) {
        this.novelFragment = novelFragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        reversed = false;
        Log.d("NFChapters", "Destroy");
        recyclerView = null;
        adapter = null;
        if (StaticNovel.novelLoader != null && !StaticNovel.novelLoader.isCancelled()) {
            StaticNovel.novelLoader.setC(false);
            StaticNovel.novelLoader.cancel(true);
        }

        if (StaticNovel.chapterLoader != null && !StaticNovel.chapterLoader.isCancelled()) {
            StaticNovel.chapterLoader.setC(false);
            StaticNovel.chapterLoader.cancel(true);
        }
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
        outState.putSerializable("selChapter", selectedChapters);
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
        if (savedInstanceState != null) {
            selectedChapters = (ArrayList<NovelChapter>) savedInstanceState.getSerializable("selChapter");
        }
        Log.d("NovelFragmentChapters", "Creating");
        View view = inflater.inflate(R.layout.fragment_novel_chapters, container, false);
        recyclerView = view.findViewById(R.id.fragment_novel_chapters_recycler);
        swipeRefreshLayout = view.findViewById(R.id.fragment_novel_chapters_refresh);
        pageCount = view.findViewById(R.id.page_count);
        resumeRead = view.findViewById(R.id.resume);
        resumeRead.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (StaticNovel.chapterLoader != null && !StaticNovel.chapterLoader.isCancelled()) {
                StaticNovel.chapterLoader.setC(false);
                StaticNovel.chapterLoader.cancel(true);
            }
            StaticNovel.chapterLoader = new ChapterLoader(this);
            StaticNovel.chapterLoader.execute(getActivity());
        });

        if (savedInstanceState != null) {
            currentMaxPage = savedInstanceState.getInt("maxPage");
        }
        setNovels();
        onResume();
        resumeRead.setOnClickListener(view1 -> {
            int i = StaticNovel.lastRead();
            if (i != -1 && i != -2)
                Utilities.openChapter(getActivity(), StaticNovel.novelChapters.get(i));
            else
                Toast.makeText(getContext(), "No chapters! How did you even press this!", Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    /**
     * Sets the novel chapters down
     */
    public void setNovels() {
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(false);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

            if (Database.DatabaseLibrary.inLibrary(StaticNovel.novelURL)) {
                StaticNovel.novelChapters = Database.DatabaseChapter.getChapters(StaticNovel.novelURL);
                if (StaticNovel.novelChapters != null && StaticNovel.novelChapters.size() != 0)
                    resumeRead.setVisibility(View.VISIBLE);
            }
            adapter = new ChaptersAdapter(this);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chapter_select_all:
                for (NovelChapter novelChapter : StaticNovel.novelChapters)
                    if (!contains(novelChapter))
                        selectedChapters.add(novelChapter);
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;

            case R.id.chapter_download_selected:
                for (NovelChapter novelChapter : selectedChapters)
                    if (!Database.DatabaseChapter.isSaved(novelChapter.link)) {
                        DownloadItem downloadItem = new DownloadItem(StaticNovel.formatter, StaticNovel.novelPage.title, novelChapter.chapterNum, StaticNovel.novelURL, novelChapter.link);
                        Download_Manager.addToDownload(downloadItem);
                    }
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;

            case R.id.chapter_delete_selected:
                for (NovelChapter novelChapter : selectedChapters)
                    if (Database.DatabaseChapter.isSaved(novelChapter.link))
                        Download_Manager.delete(getContext(), new DownloadItem(StaticNovel.formatter, StaticNovel.novelPage.title, novelChapter.chapterNum, StaticNovel.novelURL, novelChapter.link));
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;

            case R.id.chapter_deselect_all:
                selectedChapters = new ArrayList<>();
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                onCreateOptionsMenu(menu, getInflater());
                return true;

            case R.id.chapter_mark_read:
                for (NovelChapter novelChapter : selectedChapters)
                    if (Database.DatabaseChapter.getStatus(novelChapter.link).getA() != 2)
                        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READ);
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;

            case R.id.chapter_mark_unread:
                for (NovelChapter novelChapter : selectedChapters)
                    if (Database.DatabaseChapter.getStatus(novelChapter.link).getA() != 0)
                        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.UNREAD);
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;

            case R.id.chapter_mark_reading:
                for (NovelChapter novelChapter : selectedChapters)
                    if (Database.DatabaseChapter.getStatus(novelChapter.link).getA() != 0)
                        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READING);
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;

            case R.id.chapter_select_between:
                int min = findMinPosition();
                int max = findMaxPosition();
                for (int x = min; x < max; x++)
                    if (!contains(StaticNovel.novelChapters.get(x)))
                        selectedChapters.add(StaticNovel.novelChapters.get(x));
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;

            case R.id.chapter_filter:
                Collections.reverse(StaticNovel.novelChapters);
                NovelFragmentChapters.reversed = !NovelFragmentChapters.reversed;
                return NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
        }
        return false;
    }

    /**
     * Creates the option menu (on the top toolbar)
     *
     * @param menu     Menu reference to fill
     * @param inflater Object to inflate the menu
     */
    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        this.menu = menu;
        menu.clear();
        if (selectedChapters.size() <= 0)
            inflater.inflate(R.menu.toolbar_chapters, menu);
        else
            inflater.inflate(R.menu.toolbar_chapters_selected, menu);
    }
}
