package com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
class MigratingNovelAdapter(private val migrationView: MigrationView) : RecyclerView.Adapter<CompressedHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CompressedHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.catalogue_item_card, viewGroup, false)
        return CompressedHolder(view)
    }

    override fun onBindViewHolder(catalogueHolder: CompressedHolder, i: Int) {
        val novel = migrationView.novels!![i]
        Log.d("BindingItem: ", novel.title)
        val materialCardView: MaterialCardView = catalogueHolder.itemView.findViewById(R.id.materialCardView)
        if (i == migrationView.selection) {
            materialCardView.strokeColor = Color.BLUE
            materialCardView.strokeWidth = Utilities.selectedStrokeWidth
        } else materialCardView.strokeWidth = 0
        Picasso.get().load(novel.imageURL).into(catalogueHolder.image)
        catalogueHolder.title.text = novel.title
        catalogueHolder.itemView.setOnClickListener { view: View? ->
            migrationView.selection = i
            migrationView.secondSelection = -1
            Log.d("Current selection", migrationView.selection.toString())
            migrationView.refresh()
        }
    }

    override fun getItemCount(): Int {
        println(migrationView.novels!!.size)
        return migrationView.novels!!.size
    }

}