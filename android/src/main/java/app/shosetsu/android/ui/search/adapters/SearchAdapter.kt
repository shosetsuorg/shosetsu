package app.shosetsu.android.ui.search.adapters

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
/*
class SearchAdapter(private val searchController: SearchController)
    : RecyclerView.Adapter<SearchViewHolder>() {
    private val views: ArrayList<Int> = arrayListOf(-1)

    init {
        for (formatter: Formatter in FormatterUtils.formatters)
            views.add(formatter.formatterID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder =
            SearchViewHolder(LayoutInflater.from(parent.context).inflate(
                    R.layout.recycler_search_row,
                    parent,
                    false
            ), searchController)


    override fun getItemCount(): Int = views.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.query = searchController.viewModel.query.value ?: ""
        holder.setId(views[position])
    }
}
*/