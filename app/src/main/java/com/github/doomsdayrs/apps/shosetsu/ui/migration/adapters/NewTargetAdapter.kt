package com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.migration.NewMigrationView
import com.github.doomsdayrs.apps.shosetsu.ui.migration.viewHolders.CompressedHolder
import com.squareup.picasso.Picasso

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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class NewTargetAdapter(val newMigrationView: NewMigrationView) : RecyclerView.Adapter<CompressedHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompressedHolder {
        return CompressedHolder(LayoutInflater.from(newMigrationView).inflate(R.layout.catalogue_item_card, parent, false))
    }

    override fun getItemCount(): Int {
        return newMigrationView.novelResults[newMigrationView.index].size
    }

    override fun onBindViewHolder(holder: CompressedHolder, position: Int) {
        val card = newMigrationView.novelResults[newMigrationView.index][position]
        Picasso.get().load(card.imageURL).into(holder.image)
        holder.title.text = card.title
        Utilities.setBackgroundByTheme(holder.title)
    }

}