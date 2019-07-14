package com.github.doomsdayrs.apps.shosetsu.ui.viewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.doomsdayrs.apps.shosetsu.R;

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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class DownloadItemViewHolder extends RecyclerView.ViewHolder {

    public final TextView title;
    public final TextView status;

    public DownloadItemViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.recycler_download_card_title);
        status = itemView.findViewById(R.id.recycler_download_card_status);
    }
}
