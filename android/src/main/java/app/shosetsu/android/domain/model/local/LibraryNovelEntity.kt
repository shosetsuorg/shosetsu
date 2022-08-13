package app.shosetsu.android.domain.model.local

import app.shosetsu.lib.Novel

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
 * For displaying novels in library owo
 *
 * @param id of the novel
 * @param title of the novel
 * @param imageURL of the novel
 * @param bookmarked if this novel is bookmarked or not
 * @param unread chapters of this novel
 */
data class LibraryNovelEntity(
	val id: Int,
	val title: String,
	val imageURL: String,
	var bookmarked: Boolean,
	val unread: Int,
	val genres: List<String>,
	val authors: List<String>,
	val artists: List<String>,
	val tags: List<String>,
	val status: Novel.Status,
	val category: Int,
)