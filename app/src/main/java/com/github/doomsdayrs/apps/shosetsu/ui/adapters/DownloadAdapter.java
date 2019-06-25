package com.github.doomsdayrs.apps.shosetsu.ui.adapters;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.main.DownloadsFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.viewholders.DownloadItemViewHolder;
import com.github.doomsdayrs.apps.shosetsu.variables.download.DownloadItem;

import java.util.ArrayList;
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
public class DownloadAdapter extends RecyclerView.Adapter<DownloadItemViewHolder> {

    //Reference to the the NovelFragmentChapters array
    private final List<DownloadItem> downloadItems;
    public static List<DownloadItemViewHolder> downloadItemViewHolders = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    public static DownloadsFragment downloadsFragment;

    public DownloadAdapter(List<DownloadItem> downloadItems, DownloadsFragment downloadsFragmentA) {
        this.downloadItems = downloadItems;
        downloadsFragment = downloadsFragmentA;
    }

    public static boolean contains(DownloadItem downloadItem) {
        for (DownloadItemViewHolder downloadItemViewHolder : downloadItemViewHolders)
            if (downloadItemViewHolder.downloadItem.chapterURL.equals(downloadItem.chapterURL))
                return true;
        return false;
    }

    public static DownloadItemViewHolder getHolder(DownloadItem downloadItem) {
        for (DownloadItemViewHolder downloadItemViewHolder : downloadItemViewHolders)
            if (downloadItemViewHolder.downloadItem.chapterURL.equals(downloadItem.chapterURL))
                return downloadItemViewHolder;
        return null;
    }

    public static void progressToggle(DownloadItemViewHolder downloadItemViewHolder) {
        if (downloadsFragment != null)
            if (downloadsFragment.getActivity() != null)
                downloadsFragment.getActivity().runOnUiThread(() -> {
                    if (downloadItemViewHolder.progressBar.getVisibility() == View.VISIBLE)
                        downloadItemViewHolder.progressBar.setVisibility(View.GONE);
                    else downloadItemViewHolder.progressBar.setVisibility(View.VISIBLE);
                });
    }






    @NonNull
    @Override
    public DownloadItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_download_card, viewGroup, false);
        return new DownloadItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadItemViewHolder downloadItemViewHolder, int i) {
        DownloadItem downloadItem = downloadItems.get(i);
        downloadItemViewHolder.downloadItem = downloadItem;
        downloadItemViewHolder.textView.setText(downloadItem.chapterURL);

        if (!contains(downloadItem))
            downloadItemViewHolders.add(downloadItemViewHolder);
    }

    @Override
    public int getItemCount() {
        return downloadItems.size();
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
