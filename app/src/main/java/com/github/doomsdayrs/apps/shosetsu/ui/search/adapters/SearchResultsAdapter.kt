package com.github.doomsdayrs.apps.shosetsu.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.ui.search.viewHolders.ResultViewHolder
import com.github.doomsdayrs.apps.shosetsu.ui.search.viewHolders.SearchViewHolder
import com.github.doomsdayrs.apps.shosetsu.variables.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Formatters
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
class SearchResultsAdapter(private val searchViewHolder: SearchViewHolder) : RecyclerView.Adapter<ResultViewHolder>() {
    private var intArray: ArrayList<Int> = arrayListOf(-1)
    private var novelArray: List<Array<String>> = arrayListOf()

    constructor(array: ArrayList<Int>, searchViewHolder: SearchViewHolder) : this(searchViewHolder) {
        this.intArray = array
    }

    constructor(array: List<Array<String>>, searchViewHolder: SearchViewHolder) : this(searchViewHolder) {
        novelArray = array
    }

    private fun isWebsiteSearch(): Boolean {
        return intArray.size == 1 && intArray[0] == -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val title: String
        val url: String
        val imageURL: String
        val formatter: Formatter
        val id: Int

        if (isWebsiteSearch()) {
            val novel: Array<String> = novelArray[position]
            title = novel[0]
            url = novel[1]
            imageURL = novel[2]
            formatter = searchViewHolder.formatter
            id = Database.DatabaseIdentification.getNovelIDFromNovelURL(imageURL)
        } else {
            val novel: NovelCard = Database.DatabaseNovels.getNovel(intArray[position])
            title = novel.title
            url = novel.novelURL
            imageURL = novel.imageURL
            formatter = Formatters.getByID(novel.formatterID)
            id = novel.novelID
        }

        if (title.isNotEmpty())
            holder.textView.text = title
        if (imageURL.isNotEmpty())
            Picasso.get().load(imageURL).into(holder.imageView)

        holder.itemView.setOnClickListener {
            val novelFragment = NovelController()
            novelFragment.novelURL = url
            novelFragment.formatter = formatter
            novelFragment.novelID = id
            searchViewHolder.searchController.router.pushController(novelFragment.withFadeTransaction())
        }

    }


    override fun getItemCount(): Int {
        return if (isWebsiteSearch())
            novelArray.size
        else
            intArray.size

    }
}