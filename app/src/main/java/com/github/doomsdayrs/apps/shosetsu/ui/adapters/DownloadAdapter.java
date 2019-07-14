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
