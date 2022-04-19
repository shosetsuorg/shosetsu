package app.shosetsu.android.view.uimodels.model

import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.dto.Convertible
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
 * ====================================================================
 */

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
data class NovelUI(
	val id: Int,

	val novelURL: String,

	val extID: Int,

	var extName: String = "",

	var bookmarked: Boolean,

	var title: String,

	var imageURL: String,

	var description: String,
	var loaded: Boolean,
	var language: String,

	var genres: List<String>,
	var authors: List<String>,
	var artists: List<String>,
	var tags: List<String>,

	var status: Novel.Status,
) : Convertible<NovelEntity> {

	override fun convertTo(): NovelEntity = NovelEntity(
		id = id,
		url = novelURL,
		extensionID = extID,
		bookmarked = bookmarked,
		loaded = loaded,
		title = title,
		imageURL = imageURL,
		description = description,
		language = language,
		genres = genres,
		authors = authors,
		artists = artists,
		tags = tags,
		status = status
	)
}