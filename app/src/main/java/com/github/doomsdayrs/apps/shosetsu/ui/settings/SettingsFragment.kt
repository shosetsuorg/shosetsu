package com.github.doomsdayrs.apps.shosetsu.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingsAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.SettingsCard
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Types
import kotlinx.android.synthetic.main.settings.*
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
class SettingsFragment : Fragment(R.layout.settings) {
    private val cards: ArrayList<SettingsCard> = ArrayList()

    init {
        cards.add(SettingsCard(Types.DOWNLOAD))
        cards.add(SettingsCard(Types.VIEW))
        cards.add(SettingsCard(Types.ADVANCED))
        cards.add(SettingsCard(Types.INFO))
        cards.add(SettingsCard(Types.BACKUP))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utilities.setActivityTitle(activity, getString(R.string.settings))
        recyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)
        if (fragmentManager != null) {
            val adapter: RecyclerView.Adapter<*> = SettingsAdapter(cards, fragmentManager!!)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }

}