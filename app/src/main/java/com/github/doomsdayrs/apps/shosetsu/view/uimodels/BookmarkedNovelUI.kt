package com.github.doomsdayrs.apps.shosetsu.view.uimodels

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.BookmarkedNovelEntity
import com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders.LibraryItemViewHolder
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.base.BaseRecyclerItem

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
 */

/**
 * shosetsu
 * 06 / 06 / 2020
 *
 * For displaying novels in library (UI) owo
 *
 * @param id of the novel
 * @param title of the novel
 * @param imageURL of the novel
 * @param bookmarked If this novel is bookmarked or not
 * @param unread chapters of this novel
 */
data class BookmarkedNovelUI(
		val id: Int,
		val title: String,
		val imageURL: String,
		var bookmarked: Boolean,
		val unread: Int
) : BaseRecyclerItem<LibraryItemViewHolder>(), Convertible<BookmarkedNovelEntity> {
	lateinit var settings: ShosetsuSettings

	override var identifier: Long
		get() = id.toLong()
		set(value) {}

	override fun convertTo(): BookmarkedNovelEntity =
			BookmarkedNovelEntity(id, title, imageURL, bookmarked, unread)

	override val layoutRes: Int by lazy {
		if (settings.novelCardType == 0)
			R.layout.recycler_novel_card
		else R.layout.recycler_novel_card_compressed
	}

	override val type: Int by lazy {
		settings.novelCardType
	}

	override fun getViewHolder(v: View): LibraryItemViewHolder = LibraryItemViewHolder(v)
}