package com.github.doomsdayrs.apps.shosetsu.ui.downloads.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.DownloadsController
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.viewHolders.DownloadItemView

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
class DownloadAdapter(val downloadsController: DownloadsController) : RecyclerView.Adapter<DownloadItemView>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DownloadItemView {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_download_card, viewGroup, false)
        return DownloadItemView(view)
    }

    override fun onBindViewHolder(downloadItemView: DownloadItemView, i: Int) {
        val downloadItem = downloadsController.downloadItems[i]
        downloadItemView.title.text = downloadItem.chapterURL
        downloadItemView.status.text = downloadItem.status
    }

    override fun getItemCount(): Int {
        return downloadsController.downloadItems.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}