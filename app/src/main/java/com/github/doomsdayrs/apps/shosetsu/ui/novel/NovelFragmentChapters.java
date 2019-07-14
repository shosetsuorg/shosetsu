package com.github.doomsdayrs.apps.shosetsu.ui.novel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.Doomsdayrs.api.novelreader_core.services.core.objects.NovelChapter;
import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.async.ChapterLoader;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.novel.ChaptersAdapter;
import com.github.doomsdayrs.apps.shosetsu.ui.listeners.NovelFragmentChaptersOnFilter;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/*
 * This file is part of Shosetsu.
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Shosetsu is distributed in the hope that it will be useful,
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
 */
public class NovelFragmentChapters extends Fragment {
    public static ArrayList<NovelChapter> selectedChapters = new ArrayList<>();

    public static boolean contains(NovelChapter novelChapter) {
        for (NovelChapter n : selectedChapters)
            if (n.link.equalsIgnoreCase(novelChapter.link))
                return true;
        return false;
    }

    public static int findMinPosition() {
        int min = StaticNovel.novelChapters.size();
        for (int x = 0; x < StaticNovel.novelChapters.size(); x++)
            if (contains(StaticNovel.novelChapters.get(x)))
                if (x < min)
                    min = x;
        return min;
    }

    public static int findMaxPosition() {
        int max = -1;
        for (int x = StaticNovel.novelChapters.size() - 1; x >= 0; x--)
            if (contains(StaticNovel.novelChapters.get(x)))
                if (x > max)
                    max = x;
        return max;
    }

    public boolean reversed;
    @SuppressLint("StaticFieldLeak")
    public static RecyclerView recyclerView;
    public int currentMaxPage = 1;
    public static ChaptersAdapter adapter;
    private Context context;
    public ProgressBar progressBar;
    public SwipeRefreshLayout swipeRefreshLayout;
    public NovelFragment novelFragment;
    /**
     * Constructor
     */
    public NovelFragmentChapters(NovelFragment novelFragment) {
        setHasOptionsMenu(true);
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

        if (selectedChapters.size() <= 0) {
            inflater.inflate(R.menu.toolbar_chapters, menu);
            menu.findItem(R.id.chapter_filter).setOnMenuItemClickListener(new NovelFragmentChaptersOnFilter(this));
        } else {
            inflater.inflate(R.menu.toolbar_chapters_selected, menu);
            menu.findItem(R.id.chapter_select_all).setOnMenuItemClickListener(menuItem -> {
                for (NovelChapter novelChapter : StaticNovel.novelChapters)
                    if (!contains(novelChapter))
                        selectedChapters.add(novelChapter);
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;
            });
            menu.findItem(R.id.chapter_download_selected).setOnMenuItemClickListener(menuItem -> {
                for (NovelChapter novelChapter : selectedChapters)
                    if (!Database.DatabaseChapter.isSaved(novelChapter.link)) {
                        DownloadItem downloadItem = new DownloadItem(StaticNovel.formatter, StaticNovel.novelPage.title, novelChapter.chapterNum, StaticNovel.novelURL, novelChapter.link);
                        Download_Manager.addToDownload(downloadItem);
                    }
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;
            });
            menu.findItem(R.id.chapter_delete_selected).setOnMenuItemClickListener(menuItem -> {
                for (NovelChapter novelChapter : selectedChapters)
                    if (Database.DatabaseChapter.isSaved(novelChapter.link))
                        Download_Manager.delete(getContext(), new DownloadItem(StaticNovel.formatter, StaticNovel.novelPage.title, novelChapter.chapterNum, StaticNovel.novelURL, novelChapter.link));
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;
            });
            menu.findItem(R.id.chapter_deselect_all).setOnMenuItemClickListener(menuItem -> {
                selectedChapters = new ArrayList<>();
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                onCreateOptionsMenu(menu, inflater);
                return true;
            });
            menu.findItem(R.id.chapter_mark_read).setOnMenuItemClickListener(menuItem -> {
                for (NovelChapter novelChapter : selectedChapters)
                    if (Database.DatabaseChapter.getStatus(novelChapter.link).getA() != 2)
                        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READ);

                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;
            });

            menu.findItem(R.id.chapter_mark_unread).setOnMenuItemClickListener(menuItem -> {
                for (NovelChapter novelChapter : selectedChapters)
                    if (Database.DatabaseChapter.getStatus(novelChapter.link).getA() != 0)
                        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.UNREAD);
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;
            });

            menu.findItem(R.id.chapter_mark_reading).setOnMenuItemClickListener(menuItem -> {
                for (NovelChapter novelChapter : selectedChapters)
                    if (Database.DatabaseChapter.getStatus(novelChapter.link).getA() != 0)
                        Database.DatabaseChapter.setChapterStatus(novelChapter.link, Status.READING);
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;
            });

            menu.findItem(R.id.chapter_select_between).setOnMenuItemClickListener(menuItem -> {
                int min = findMinPosition();
                int max = findMaxPosition();
                for (int x = min; x < max; x++)
                    if (!contains(StaticNovel.novelChapters.get(x)))
                        selectedChapters.add(StaticNovel.novelChapters.get(x));
                NovelFragmentChapters.recyclerView.post(() -> NovelFragmentChapters.adapter.notifyDataSetChanged());
                return true;
            });
        }

    }
}
