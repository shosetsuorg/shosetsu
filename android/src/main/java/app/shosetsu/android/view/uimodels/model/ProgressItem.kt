package app.shosetsu.android.view.uimodels.model

import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import com.github.doomsdayrs.apps.shosetsu.R

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
 */

/**
 * shosetsu
 * 02 / 10 / 2020
 */
class ProgressItem : ACatalogNovelUI() {
	override val id: Int = -1
	override val title: String = ""
	override val imageURL: String = ""
	override var bookmarked: Boolean = false
	override var isSelectable: Boolean = false

	override val layoutRes: Int = R.layout.recycler_progress_item
	override val type: Int = R.layout.recycler_progress_item
}