package com.github.doomsdayrs.apps.shosetsu.ui.search.viewHolders

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
 *//*

class SearchViewHolder(itemView: View, val searchController: SearchController)
	: RecyclerView.ViewHolder(itemView) {
	var query: String = ""
	private var id = -2
	lateinit var formatter: Formatter

	val textView: TextView = itemView.findViewById(R.id.title)
	val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
	val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

	var searchResultsAdapter: SearchResultsAdapter = SearchResultsAdapter(this)

	init {
		recyclerView.layoutManager = LinearLayoutManager(
				itemView.context,
				LinearLayoutManager.HORIZONTAL,
				false
		)
	}

	fun setId(id: Int) {
		this.id = id
		when (id) {
			-2 -> throw RuntimeException("InvalidValue")
			-1 -> {
				textView.setText(R.string.my_library)
				if (!searchController.containsData(id)) {
					val intArray: List<Int> =
							searchController.viewModel.search(query).map { it.id }
					val data = StoredData(id)
					data.intArray = intArray
					searchController.array.add(data)
					searchResultsAdapter = SearchResultsAdapter(
							intArray as ArrayList<Int>,
							this
					)
				} else {
					val data: StoredData = searchController.getData(id)
					searchResultsAdapter = SearchResultsAdapter(
							data.intArray as ArrayList<Int>,
							this
					)
				}
				setAdapter()
				progressBar.visibility = View.GONE
			}
			else -> {
				//TODO finix
				//formatter = FormattersRepository.getByID(id)
				//	textView.text = formatter.name
				if (!searchController.containsData(id))
					SearchLoader(this).execute(query)
				else {
					searchResultsAdapter = SearchResultsAdapter(
							searchController.getData(id).novelArray,
							this
					)
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
		*/