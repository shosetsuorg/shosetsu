package com.github.doomsdayrs.apps.shosetsu.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.search.viewHolders.ResultViewHolder
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard
import com.squareup.picasso.Picasso

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */ /**
 * shosetsu
 * 20 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class SearchResultsAdapter() : RecyclerView.Adapter<ResultViewHolder>() {
    var intArray: ArrayList<Int> = arrayListOf(-1)

    constructor(intArray: ArrayList<Int>) : this() {
        this.intArray = intArray
    }

    private fun isWebsiteSearch(): Boolean {
        return intArray.size == 1 && intArray[0] == -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        if (isWebsiteSearch()) {
            TODO("Loader")
        } else {
            val novel: NovelCard = Database.DatabaseNovels.getNovel(intArray[position])
            Picasso.get()
                    .load(novel.imageURL)
                    .into(holder.imageView)
            holder.textView.text = novel.title
        }
    }

    override fun getItemCount(): Int {
        return if (isWebsiteSearch()) {
            //TODO Loader
            0
        } else {
            intArray.size
        }
    }
}