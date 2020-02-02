package com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_library.*

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
class LibNovelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val materialCardView: MaterialCardView = itemView.findViewById(R.id.novel_item_card)
    val imageView: ImageView = itemView.findViewById(R.id.image)
    val title: TextView = itemView.findViewById(R.id.title)
    val chip: Chip = itemView.findViewById(R.id.novel_item_left_to_read)

    lateinit var libraryFragment: LibraryFragment
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
        val novelFragment = NovelFragment()
        novelFragment.formatter = formatter
        novelFragment.novelURL = novelCard.novelURL
        novelFragment.novelID = novelCard.novelID
        assert(libraryFragment.fragmentManager != null)
        (libraryFragment.activity as MainActivity).transitionView(novelFragment)
    }

    init {
        chip.setOnClickListener { view: View -> Toast.makeText(view.context, libraryFragment.resources.getString(R.string.chapters_unread_label) + chip.text, Toast.LENGTH_SHORT).show() }
        itemView.setOnLongClickListener {
            addToSelect()
            true
        }
    }
}