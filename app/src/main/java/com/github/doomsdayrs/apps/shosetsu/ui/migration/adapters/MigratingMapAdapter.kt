package com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView
import com.github.doomsdayrs.apps.shosetsu.ui.migration.viewHolders.CompressedHolder
import com.google.android.material.card.MaterialCardView
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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class MigratingMapAdapter(private val migrationView: MigrationView) : RecyclerView.Adapter<CompressedHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CompressedHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.catalogue_item_card, viewGroup, false)
        return CompressedHolder(view)
    }

    override fun onBindViewHolder(holder: CompressedHolder, position: Int) {
        val novel = migrationView.novelResults[migrationView.selection][position]
        Picasso.get().load(novel.imageURL).into(holder.image)
        holder.title.text = novel.title
        val materialCardView: MaterialCardView = holder.itemView.findViewById(R.id.materialCardView)

        if (position == migrationView.secondSelection) {
            materialCardView.strokeColor = Color.BLUE
            materialCardView.strokeWidth = Utilities.SELECTED_STROKE_WIDTH
        } else materialCardView.strokeWidth = 0

        holder.itemView.setOnClickListener {
            migrationView.secondSelection = position
            migrationView.refresh()
        }
    }

    override fun getItemCount(): Int {
        return if (migrationView.novelResults.size > 0) migrationView.novelResults[migrationView.selection].size else 0
    }

}