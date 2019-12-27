package com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder

import android.app.Activity
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.objects.Update
import com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters.UpdatedChaptersAdapter
import com.google.android.material.chip.Chip
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
class UpdatedNovelHolder(itemView: View, val activity: Activity) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.imageView)
    val title: TextView = itemView.findViewById(R.id.title)
    val chip: Chip = itemView.findViewById(R.id.count)
    val button: ImageButton = itemView.findViewById(R.id.button)
    val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
    val expand: ImageButton = itemView.findViewById(R.id.loadMore)


    private var expanded: Boolean = false
    var novelID: Int = -1
        set(value) {
            val novelCard = Database.DatabaseNovels.getNovel(value)

            if (novelCard.novelURL.isNotEmpty())
                Picasso.get().load(novelCard.imageURL).into(imageView)

            title.text = novelCard.title


            field = value
        }

    var updates: ArrayList<Update> = ArrayList()
        set(value) {
            field = value
            chip.text = value.size.toString()
            updatersAdapter.size = if (updates.size > 20) 5 else updates.size
            updatersAdapter.notifyDataSetChanged()
        }

    var updatersAdapter: UpdatedChaptersAdapter = UpdatedChaptersAdapter(this)

    init {
        button.setOnClickListener {
            if (expanded) {
                button.setImageResource(R.drawable.ic_baseline_expand_more_24)
                recyclerView.visibility = View.GONE
                expand.visibility = View.GONE
            } else {
                button.setImageResource(R.drawable.ic_baseline_expand_less_24)
                recyclerView.visibility = View.VISIBLE

                if (updatersAdapter.size < updates.size)
                    expand.visibility = View.VISIBLE
            }
            expanded = !expanded
        }
        expand.setOnClickListener {
            updatersAdapter.size =
                    if (updatersAdapter.size + 5 >= updates.size) {
                        expand.visibility = View.GONE
                        updates.size
                    } else updatersAdapter.size + 5
            updatersAdapter.notifyDataSetChanged()
        }

        recyclerView.adapter = updatersAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

}