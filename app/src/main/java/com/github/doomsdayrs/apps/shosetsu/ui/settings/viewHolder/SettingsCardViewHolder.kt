package com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder

import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.settings.listener.OnSettingsCardClick
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types
import com.google.android.material.card.MaterialCardView

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
 */
/**
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class SettingsCardViewHolder(itemView: View, private val fragmentManager: FragmentManager) : RecyclerView.ViewHolder(itemView) {
    private val libraryCardTitle: TextView = itemView.findViewById(R.id.recycler_settings_title)
    private val cardView: MaterialCardView = itemView.findViewById(R.id.settings_card)

    fun setType(type: Types) {
        cardView.setOnClickListener(OnSettingsCardClick(type, fragmentManager))
        libraryCardTitle.text = when (type.position) {
            0 -> itemView.context.getString(R.string.download)
            1 -> itemView.context.getString(R.string.view)
            2 -> itemView.context.getString(R.string.advanced)
            3 -> itemView.context.getString(R.string.info)
            4 -> itemView.context.getString(R.string.backup)
            else -> itemView.context.getString(R.string.unknown)
        }
    }

}