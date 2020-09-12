package app.shosetsu.android.view.uimodels.model.library

import android.view.View
import app.shosetsu.android.domain.model.base.Convertible
import app.shosetsu.android.domain.model.local.BookmarkedNovelEntity
import app.shosetsu.android.ui.library.viewHolders.LibraryItemViewHolder
import app.shosetsu.android.view.uimodels.base.BaseRecyclerItem

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
 */
abstract class ABookmarkedNovelUI
	: BaseRecyclerItem<LibraryItemViewHolder>(), Convertible<BookmarkedNovelEntity> {

	/** ID of the novel*/
	abstract val id: Int

	/** title of the novel*/
	abstract val title: String

	/** imageURL of the novel*/
	abstract val imageURL: String

	/** If this novel is bookmarked or not*/
	abstract var bookmarked: Boolean

	/** chapters of this novel*/
	abstract val unread: Int

	override var identifier: Long
		get() = id.toLong()
		set(@Suppress("UNUSED_PARAMETER") value) {}

	override fun convertTo(): BookmarkedNovelEntity =
			BookmarkedNovelEntity(id, title, imageURL, bookmarked, unread)

	override fun getViewHolder(v: View): LibraryItemViewHolder = LibraryItemViewHolder(v)
}