package com.github.doomsdayrs.apps.shosetsu.domain.model.local

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.NovelUI

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
		@PrimaryKey(autoGenerate = true)
		/** ID of this novel */
		var id: Int? = null,

		@NonNull
		/** URL of the novel */
		var url: String,

		/** Source this novel is from */
		val formatterID: Int,

		/** If this novel is in the user's library */
		var bookmarked: Boolean = false,

		/** Says if the data is loaded or now, if it is not it needs to be loaded */
		var loaded: Boolean = false,

		/** What kind of reader is this novel using */
		var readerType: Int = -1,

		@NonNull
		/** The title of the novel */
		var title: String,

		@NonNull
		/** Image URL of the novel */
		var imageURL: String = "",

		/** Description */
		var description: String = "",

		/** Language of the novel */
		var language: String = "",

		/** Genres this novel matches too */
		var genres: Array<String> = arrayOf(),

		/** Authors of this novel */
		var authors: Array<String> = arrayOf(),

		/** Artists who helped with the novel illustration */
		var artists: Array<String> = arrayOf(),

		/** Tags this novel matches, in case genres were not enough*/
		var tags: Array<String> = arrayOf(),

		@NonNull
		/** The publishing status of this novel */
		var status: Novel.Status = Novel.Status.UNKNOWN
) : Convertible<NovelUI> {
	override fun convertTo(): NovelUI = NovelUI(
			id = id!!,
			novelURL = url,
			formatterID = formatterID,
			bookmarked = bookmarked,
			readerType = readerType,
			title = title,
			imageURL = imageURL,
			description = description,
			loaded = loaded,
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

		other as NovelEntity

		if (id != other.id) return false
		if (url != other.url) return false
		if (formatterID != other.formatterID) return false
		if (bookmarked != other.bookmarked) return false
		if (loaded != other.loaded) return false
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
		var result = id ?: 0
		result = 31 * result + url.hashCode()
		result = 31 * result + formatterID
		result = 31 * result + bookmarked.hashCode()
		result = 31 * result + loaded.hashCode()
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