package com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.variables.ext.launchAsync
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ILibraryViewModel
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
class LibNovelViewHolder(itemView: View, val router: Router)
	: RecyclerView.ViewHolder(itemView), View.OnClickListener {
	val materialCardView: MaterialCardView = itemView.findViewById(R.id.novel_item_card)
	val imageView: ImageView = itemView.findViewById(R.id.image)
	val title: TextView = itemView.findViewById(R.id.title)
	val chip: Chip = itemView.findViewById(R.id.novel_item_left_to_read)

	private lateinit var libraryController: LibraryController
	lateinit var viewModel: ILibraryViewModel
	lateinit var novelCard: NovelCard
	var formatterID: Int = -1

	fun setLibraryControllerFun(library: LibraryController) {
		libraryController = library
		viewModel = library.viewModel
	}

	fun handleSelection() {
		if (!viewModel.selectedNovels.contains(novelCard.novelID))
			viewModel.selectedNovels.add(novelCard.novelID)
		else removeFromSelect()

		if (viewModel.selectedNovels.size <= 0 || viewModel.selectedNovels.size == 1)
			libraryController.inflater?.let { libraryController.activity?.invalidateOptionsMenu() }

		libraryController.recyclerView?.post { libraryController.adapter?.notifyDataSetChanged() }
	}

	private fun removeFromSelect() {
		if (viewModel.selectedNovels.contains(novelCard.novelID))
			viewModel.selectedNovels.removeAt(viewModel.selectedNovels.indexOf(novelCard.novelID))
	}

	override fun onClick(v: View) =
			router.pushController(NovelController(
					bundleOf(
							NovelController.BUNDLE_URL to novelCard.novelURL,
							NovelController.BUNDLE_FORMATTER to formatterID,
							NovelController.BUNDLE_ID to novelCard.novelID
					)
			).withFadeTransaction())

	init {
		chip.setOnClickListener {
			it.context.toast(it.context.getString(R.string.chapters_unread_label) + chip.text)
		}
		itemView.setOnLongClickListener {
			it.context.launchAsync { handleSelection() }
			true
		}
	}
}