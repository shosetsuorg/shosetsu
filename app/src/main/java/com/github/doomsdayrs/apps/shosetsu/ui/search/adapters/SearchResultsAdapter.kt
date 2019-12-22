package com.github.doomsdayrs.apps.shosetsu.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment
import com.github.doomsdayrs.apps.shosetsu.ui.search.viewHolders.ResultViewHolder
import com.github.doomsdayrs.apps.shosetsu.ui.search.viewHolders.SearchViewHolder
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
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
    private var novelArray: List<Novel> = arrayListOf()

    constructor(array: ArrayList<Int>, searchViewHolder: SearchViewHolder) : this(searchViewHolder) {
        this.intArray = array
    }

    constructor(array: List<Novel>, searchViewHolder: SearchViewHolder) : this(searchViewHolder) {
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
        val title: String?
        val url: String?
        val imageURL: String?
        val formatter: Formatter?
        val id: Int

        if (isWebsiteSearch()) {
            val novel: Novel = novelArray[position]
            title = novel.title
            url = novel.link
            imageURL = novel.imageURL
            formatter = searchViewHolder.formatter
            id = Database.DatabaseIdentification.getNovelIDFromNovelURL(imageURL)
        } else {
            val novel: NovelCard = Database.DatabaseNovels.getNovel(intArray[position])
            title = novel.title
            url = novel.novelURL
            imageURL = novel.imageURL
            formatter = DefaultScrapers.getByID(novel.formatterID)
            id = novel.novelID
        }

        if (title != null)
            holder.textView.text = title
        if (imageURL != null)
            Picasso.get()
                    .load(imageURL)
                    .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val novelFragment = NovelFragment()
            novelFragment.novelURL = url
            novelFragment.formatter = formatter
            novelFragment.novelID = id
            (searchViewHolder.searchFragment.activity as MainActivity).transitionView(novelFragment)
        }

        when (Settings.themeMode) {
            0 -> holder.textView.setBackgroundResource(R.color.white_trans)
            1, 2 -> holder.textView.setBackgroundResource(R.color.black_trans)
        }
    }


    override fun getItemCount(): Int {
        return if (isWebsiteSearch())
            novelArray.size
        else
            intArray.size

    }
}