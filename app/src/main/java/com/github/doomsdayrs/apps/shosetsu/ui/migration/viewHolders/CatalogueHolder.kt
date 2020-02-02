package com.github.doomsdayrs.apps.shosetsu.ui.migration.viewHolders

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.migration.MigrationView
import kotlinx.android.synthetic.main.migrate_source_view.*

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
class CatalogueHolder(itemView: View, private val migrationView: MigrationView) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val image: ImageView = itemView.findViewById(R.id.imageView)
    val title: TextView = itemView.findViewById(R.id.textView)
    lateinit var formatter: Formatter


    override fun onClick(v: View) {
        Log.d("FormatterSelection", formatter.name)
        if (Utilities.isOnline) {
            Log.d("Target", formatter.formatterID.toString())
            migrationView.target = formatter.formatterID
            migrationView.target_selection.visibility = View.GONE
            migrationView.migrating.visibility = View.VISIBLE
            //TODO, popup window saying novels rejected because the formatter ID is the same.
            for (x in migrationView.novels.indices.reversed()) {
                if (migrationView.novels[x].formatterID == formatter.formatterID) {
                    migrationView.novels.removeAt(x)
                }
            }
            migrationView.fillData()
        } else Toast.makeText(v.context, v.context.getString(R.string.you_not_online), Toast.LENGTH_SHORT).show()
    }

    init {
        itemView.setOnClickListener(this)
    }
}