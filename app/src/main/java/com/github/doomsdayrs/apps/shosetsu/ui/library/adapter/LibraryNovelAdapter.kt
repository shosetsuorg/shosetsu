package com.github.doomsdayrs.apps.shosetsu.ui.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment
import com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders.LibNovelViewHolder
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers.getByID
import com.squareup.picasso.Picasso
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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class LibraryNovelAdapter(private val novelCards: ArrayList<Int>, private val libraryFragment: LibraryFragment) : RecyclerView.Adapter<LibNovelViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): LibNovelViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_novel_card, viewGroup, false)
        return LibNovelViewHolder(view)
    }

    override fun onBindViewHolder(libNovelViewHolder: LibNovelViewHolder, i: Int) {
        val novelCard = DatabaseNovels.getNovel(novelCards[i])
        //Sets values
        run {
            if (novelCard.imageURL.isNotEmpty()) Picasso.get().load(novelCard.imageURL).into(libNovelViewHolder.imageView)
            libNovelViewHolder.libraryFragment = libraryFragment
            libNovelViewHolder.novelCard = novelCard
            libNovelViewHolder.formatter = getByID(novelCard.formatterID)
            libNovelViewHolder.title.text = novelCard.title
        }
        val count = Database.DatabaseChapter.getCountOfChaptersUnread(novelCard.novelID)
        if (count != 0) {
            libNovelViewHolder.chip.visibility = View.VISIBLE
            libNovelViewHolder.chip.text = count.toString()
        } else libNovelViewHolder.chip.visibility = View.INVISIBLE
        if (libraryFragment.selectedNovels.contains(novelCard.novelID)) {
            libNovelViewHolder.materialCardView.strokeWidth = Utilities.selectedStrokeWidth
        } else {
            libNovelViewHolder.materialCardView.strokeWidth = 0
        }
        if (libraryFragment.selectedNovels.size > 0) {
            libNovelViewHolder.itemView.setOnClickListener { libNovelViewHolder.addToSelect() }
        } else {
            libNovelViewHolder.itemView.setOnClickListener(libNovelViewHolder)
        }
    }

    override fun getItemCount(): Int {
        return novelCards.size
    }

}