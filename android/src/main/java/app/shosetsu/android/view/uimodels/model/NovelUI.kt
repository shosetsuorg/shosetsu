package app.shosetsu.android.view.uimodels.model

import androidx.compose.runtime.Immutable
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.dto.Convertible
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
@Immutable
data class NovelUI(
	val id: Int,

	val novelURL: String,

	val extID: Int,

	val extName: String = "",

	val bookmarked: Boolean,

	val title: String,

	val imageURL: String,

	val description: String,
	val loaded: Boolean,
	val language: String,

	val genres: List<String>,
	val authors: List<String>,
	val artists: List<String>,
	val tags: List<String>,

	val status: Novel.Status,
) : Convertible<NovelEntity> {

	val displayAuthors = authors.joinToString(", ")
	val displayArtists = artists.joinToString(", ")

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