package com.github.doomsdayrs.apps.shosetsu.ui.downloads.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.DownloadsFragment;
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.viewHolders.DownloadItemView;
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem;

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadItemView> {

    // This references a static variable

    public final DownloadsFragment downloadsFragment;

    public DownloadAdapter(DownloadsFragment downloadsFragmentA) {
        downloadsFragment = downloadsFragmentA;
    }

    @NonNull
    @Override
    public DownloadItemView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_download_card, viewGroup, false);
        return new DownloadItemView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadItemView downloadItemView, int i) {
        DownloadItem downloadItem = downloadsFragment.getDownloadItems().get(i);
        downloadItemView.title.setText(downloadItem.chapterURL);
        downloadItemView.status.setText(downloadItem.getStatus());
    }

    @Override
    public int getItemCount() {
        return downloadsFragment.getDownloadItems().size();
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
