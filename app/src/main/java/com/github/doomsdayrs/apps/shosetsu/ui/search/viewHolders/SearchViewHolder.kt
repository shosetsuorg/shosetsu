package com.github.doomsdayrs.apps.shosetsu.ui.search.viewHolders

import android.os.Build
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.getIntLibrary
import com.github.doomsdayrs.apps.shosetsu.ui.search.SearchFragment
import com.github.doomsdayrs.apps.shosetsu.ui.search.adapters.SearchResultsAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.search.async.SearchLoader
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
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
 */ /**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class SearchViewHolder(itemView: View, val searchFragment: SearchFragment) : RecyclerView.ViewHolder(itemView) {
    var query: String = ""
    private var id = -2
    lateinit var formatter: Formatter

    val textView: TextView = itemView.findViewById(R.id.textView)
    val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
    val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

    var searchResultsAdapter: SearchResultsAdapter = SearchResultsAdapter(this)

    init {
        recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
    }

    fun setId(id: Int) {
        this.id = id
        when (id) {
            -2 -> throw RuntimeException("InvalidValue")
            -1 -> {
                textView.setText(R.string.my_library)
                if (!searchFragment.containsData(id)) {
                    val intArray: ArrayList<Int> = getIntLibrary()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intArray.removeIf { novelID: Int? -> !DatabaseNovels.getNovelTitle(novelID!!).toLowerCase().contains(query.toLowerCase(Locale.ROOT)) }
                    } else {
                        for (x in intArray.indices.reversed()) if (!DatabaseNovels.getNovelTitle(intArray[x]).toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) intArray.removeAt(x)
                    }
                    val data: SearchFragment.StoredData = SearchFragment.StoredData(id)
                    data.intArray = intArray
                    searchFragment.array.add(data)
                    searchResultsAdapter = SearchResultsAdapter(intArray, this)
                } else {
                    val data: SearchFragment.StoredData = searchFragment.getData(id)
                    searchResultsAdapter = SearchResultsAdapter(data.intArray as ArrayList<Int>, this)
                }
                setAdapter()
                progressBar.visibility = View.GONE
            }
            else -> {
                formatter = DefaultScrapers.getByID(id)!!
                textView.text = formatter.name
                if (!searchFragment.containsData(id))
                    SearchLoader(this).execute(query)
                else {
                    searchResultsAdapter = SearchResultsAdapter(searchFragment.getData(id).novelArray, this)
                    setAdapter()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    fun setAdapter() {
        recyclerView.adapter = searchResultsAdapter
    }


}