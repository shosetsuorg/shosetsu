package com.github.doomsdayrs.apps.shosetsu.ui.migration.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.migration.NewMigrationView
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
class NewTransferalsAdapter(val newMigrationView: NewMigrationView) : RecyclerView.Adapter<NewTransferalsAdapter.Holder>() {

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val textView: TextView = itemView.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(newMigrationView).inflate(R.layout.recycler_novel_card, parent, false))
    }

    override fun getItemCount(): Int {
        return newMigrationView.novelsToTransfer.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val novel = Database.DatabaseNovels.getNovel(newMigrationView.novelsToTransfer[position])
        Picasso.get().load(novel.imageURL).into(holder.imageView)
        holder.textView.text = novel.title
    }
}