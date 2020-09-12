package app.shosetsu.android.view.uimodels.model

import app.shosetsu.android.domain.model.base.Convertible
import app.shosetsu.android.domain.model.local.NovelEntity
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

		val formatterID: Int,

		var bookmarked: Boolean,

		var readerType: Int,

		var title: String,

		var imageURL: String,

		var description: String,
		var loaded: Boolean,
		var language: String,

		var genres: Array<String>,
		var authors: Array<String>,
		var artists: Array<String>,
		var tags: Array<String>,

		var status: Novel.Status,
) : Convertible<NovelEntity> {
	override fun convertTo(): NovelEntity = NovelEntity(
			id = id,
			url = novelURL,
			formatterID = formatterID,
			bookmarked = bookmarked,
			loaded = loaded,
			readerType = readerType,
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

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as NovelUI

		if (id != other.id) return false
		if (novelURL != other.novelURL) return false
		if (formatterID != other.formatterID) return false
		if (bookmarked != other.bookmarked) return false
		if (readerType != other.readerType) return false
		if (title != other.title) return false
		if (imageURL != other.imageURL) return false
		if (description != other.description) return false
		if (loaded != other.loaded) return false
		if (language != other.language) return false
		if (!genres.contentEquals(other.genres)) return false
		if (!authors.contentEquals(other.authors)) return false
		if (!artists.contentEquals(other.artists)) return false
		if (!tags.contentEquals(other.tags)) return false
		if (status != other.status) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id
		result = 31 * result + novelURL.hashCode()
		result = 31 * result + formatterID
		result = 31 * result + bookmarked.hashCode()
		result = 31 * result + readerType
		result = 31 * result + title.hashCode()
		result = 31 * result + imageURL.hashCode()
		result = 31 * result + description.hashCode()
		result = 31 * result + loaded.hashCode()
		result = 31 * result + language.hashCode()
		result = 31 * result + genres.contentHashCode()
		result = 31 * result + authors.contentHashCode()
		result = 31 * result + artists.contentHashCode()
		result = 31 * result + tags.contentHashCode()
		result = 31 * result + status.hashCode()
		return result
	}
}