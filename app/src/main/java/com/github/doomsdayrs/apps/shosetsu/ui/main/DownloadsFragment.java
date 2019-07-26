package com.github.doomsdayrs.apps.shosetsu.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.backend.Download_Manager;
import com.github.doomsdayrs.apps.shosetsu.backend.SettingsController;
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database;
import com.github.doomsdayrs.apps.shosetsu.ui.adapters.DownloadAdapter;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.Statics;

import java.util.ArrayList;
import java.util.List;

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
 * @author github.com/hXtreme
 */
//TODO selection mechanic with options to delete,  pause,  and more
public class DownloadsFragment extends Fragment {
    public static List<DownloadItem> downloadItems = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    private static RecyclerView recyclerView;
    private static DownloadAdapter adapter;

    /**
     * Constructor
     */
    public DownloadsFragment() {
        setHasOptionsMenu(true);
    }


    private static void refreshList() {
        if (DownloadsFragment.adapter != null)
            if (DownloadsFragment.adapter.downloadsFragment != null)
                if (DownloadsFragment.adapter.downloadsFragment.getActivity() != null)
                    DownloadsFragment.adapter.downloadsFragment.getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    public static void removeDownloads(DownloadItem downloadItem) {
        for (int x = 0; x < downloadItems.size(); x++)
            if (downloadItems.get(x).chapterURL.equals(downloadItem.chapterURL)) {
                downloadItems.remove(x);
                return;
            }

        refreshList();
    }

    public static void markError(DownloadItem d) {
        for (DownloadItem downloadItem : downloadItems)
            if (downloadItem.chapterURL.equals(d.chapterURL))
                d.setStatus("Error");
        refreshList();
    }

    public static void toggleProcess(DownloadItem d) {
        for (DownloadItem downloadItem : downloadItems)
            if (downloadItem.chapterURL.equals(d.chapterURL))
                if (downloadItem.getStatus().equals("Pending") || downloadItem.getStatus().equals("Error"))
                    downloadItem.setStatus("Downloading");
                else downloadItem.setStatus("Pending");
        refreshList();
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
        Statics.mainActionBar.setTitle("Downloads");
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);
        recyclerView = view.findViewById(R.id.fragment_downloads_recycler);
        downloadItems = Database.DatabaseDownloads.getDownloadList();
        setDownloads();
        return view;
    }

    /**
     * Sets the novel chapters down
     */
    private void setDownloads() {
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new DownloadAdapter(this);
        adapter.setHasStableIds(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Creates the option menu (on the top toolbar)
     *
     * @param menu     Menu reference to fill
     * @param inflater Object to inflate the menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_downloads, menu);
        MenuItem menuItem = menu.findItem(R.id.toolbar_downloads_pause);
        if (Settings.downloadPaused)
            menuItem.setIcon(R.drawable.ic_pause_circle_filled_black_24dp);

        menuItem.setOnMenuItemClickListener(a -> {
            if (SettingsController.INSTANCE.togglePause())
                a.setIcon(R.drawable.ic_pause_circle_filled_black_24dp);
            else {
                a.setIcon(R.drawable.ic_pause_circle_outline_black_24dp);
                Download_Manager.init();
            }
            return true;
        });

    }

}
