package app.shosetsu.android.view.uimodels.model.library

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
 * 30 / 01 / 2021
 */
data class ComfyBookmarkedNovelUI(
	override val id: Int,
	override val title: String,
	override val imageURL: String,
	override var bookmarked: Boolean,
	override val unread: Int,
	override val genres: List<String>,
	override val authors: List<String>,
	override val artists: List<String>,
	override val tags: List<String>
) : ABookmarkedNovelUI() {

	override val layoutRes: Int
		get() = R.layout.recycler_novel_card_comfy

	override val type: Int
		get() = R.layout.recycler_novel_card_comfy
}
