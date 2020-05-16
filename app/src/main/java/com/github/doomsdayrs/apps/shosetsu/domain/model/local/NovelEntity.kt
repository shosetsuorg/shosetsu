package com.github.doomsdayrs.apps.shosetsu.domain.model.local

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.NovelUI

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
 * 23 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Entity(tableName = "novels",
		foreignKeys = [
			ForeignKey(
					entity = ExtensionEntity::class,
					parentColumns = ["id"],
					childColumns = ["formatterID"],
					onDelete = ForeignKey.SET_NULL,
					onUpdate = ForeignKey.CASCADE
			)
		],
		indices = [Index("formatterID")]
)
data class NovelEntity(
		@PrimaryKey
		val id: Int = -1,

		@NonNull
		val url: String,

		val formatterID: Int,

		var bookmarked: Boolean = false,

		var readerType: Int = -1,

		@NonNull
		var title: String,

		@NonNull
		var imageURL: String = "",

		var description: String? = null,

		var language: String = "",

		var genres: Array<String> = arrayOf(),
		var authors: Array<String> = arrayOf(),
		var artists: Array<String> = arrayOf(),
		var tags: Array<String> = arrayOf(),

		@NonNull
		var status: Novel.Status = Novel.Status.UNKNOWN
) : Convertible<NovelUI> {

	override fun convertTo(): NovelUI = NovelUI(
			id,
			url,
			formatterID,
			bookmarked,
			readerType,
			title,
			imageURL,
			description,
			language,
			genres,
			authors,
			artists,
			tags,
			status
	)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as NovelEntity

		if (id != other.id) return false
		if (bookmarked != other.bookmarked) return false
		if (readerType != other.readerType) return false
		if (title != other.title) return false
		if (imageURL != other.imageURL) return false
		if (description != other.description) return false
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
		result = 31 * result + bookmarked.hashCode()
		result = 31 * result + readerType
		result = 31 * result + title.hashCode()
		result = 31 * result + imageURL.hashCode()
		result = 31 * result + description.hashCode()
		result = 31 * result + language.hashCode()
		result = 31 * result + genres.contentHashCode()
		result = 31 * result + authors.contentHashCode()
		result = 31 * result + artists.contentHashCode()
		result = 31 * result + tags.contentHashCode()
		result = 31 * result + status.hashCode()
		return result
	}
}