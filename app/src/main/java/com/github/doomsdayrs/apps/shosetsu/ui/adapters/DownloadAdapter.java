package com.github.doomsdayrs.apps.shosetsu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.main.DownloadsFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.viewholders.DownloadItemViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;

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
public class DownloadAdapter extends RecyclerView.Adapter<DownloadItemViewHolder> {

    // This references a static variable

    public final DownloadsFragment downloadsFragment;

    public DownloadAdapter(DownloadsFragment downloadsFragmentA) {
        downloadsFragment = downloadsFragmentA;
    }

    @NonNull
    @Override
    public DownloadItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_download_card, viewGroup, false);
        return new DownloadItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadItemViewHolder downloadItemViewHolder, int i) {
        DownloadItem downloadItem = DownloadsFragment.downloadItems.get(i);
        downloadItemViewHolder.title.setText(downloadItem.chapterURL);
        downloadItemViewHolder.status.setText(downloadItem.getStatus());
    }

    @Override
    public int getItemCount() {
        return DownloadsFragment.downloadItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
