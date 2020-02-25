package com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

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
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class LibNovelViewHolder(itemView: View, val router: Router) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val materialCardView: MaterialCardView = itemView.findViewById(R.id.novel_item_card)
    val imageView: ImageView = itemView.findViewById(R.id.image)
    val title: TextView = itemView.findViewById(R.id.title)
    val chip: Chip = itemView.findViewById(R.id.novel_item_left_to_read)

    lateinit var libraryFragment: LibraryController
    lateinit var formatter: Formatter
    lateinit var novelCard: NovelCard

    fun addToSelect() {
        if (!libraryFragment.selectedNovels.contains(novelCard.novelID)) libraryFragment.selectedNovels.add(novelCard.novelID) else removeFromSelect()
        if (libraryFragment.selectedNovels.size <= 0 || libraryFragment.selectedNovels.size == 1)
            libraryFragment.inflater?.let { libraryFragment.activity?.invalidateOptionsMenu() }
        libraryFragment.recyclerView.post { libraryFragment.libraryNovelCardsAdapter?.notifyDataSetChanged() }
    }

    private fun removeFromSelect() {
        if (libraryFragment.selectedNovels.contains(novelCard.novelID)) for (x in libraryFragment.selectedNovels.indices) if (libraryFragment.selectedNovels[x] == novelCard.novelID) {
            libraryFragment.selectedNovels.removeAt(x)
            return
        }
    }

    override fun onClick(v: View) {
        val novelFragment = NovelController()
        novelFragment.formatter = formatter
        novelFragment.novelURL = novelCard.novelURL
        novelFragment.novelID = novelCard.novelID
        router.pushController(novelFragment.withFadeTransaction())
    }

    init {
        chip.setOnClickListener {
            it.context.toast(it.context.getString(R.string.chapters_unread_label) + chip.text)
            itemView.setOnLongClickListener {
                addToSelect()
                true
            }
        }
    }
}