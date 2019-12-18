package com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsCardViewHolder
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsCard
import java.util.*

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
class SettingsAdapter(private val settingsCards: ArrayList<SettingsCard>, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<SettingsCardViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SettingsCardViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_settings_card, viewGroup, false)
        return SettingsCardViewHolder(view, fragmentManager)
    }

    override fun onBindViewHolder(settingsCardViewHolder: SettingsCardViewHolder, i: Int) {
        val settingsCard = settingsCards[i]
        settingsCardViewHolder.setType(settingsCard.ID)
    }

    override fun getItemCount(): Int {
        return settingsCards.size
    }

}