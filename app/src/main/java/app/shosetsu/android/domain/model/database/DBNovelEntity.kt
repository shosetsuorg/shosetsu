package app.shosetsu.android.domain.model.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.shosetsu.common.com.dto.Convertible
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.lib.Novel

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
 * 05 / 12 / 2020
 */
@Entity(tableName = "novels",
		foreignKeys = [
			ForeignKey(
					entity = DBExtensionEntity::class,
					parentColumns = ["id"],
					childColumns = ["formatterID"],
					onDelete = ForeignKey.SET_NULL,
					onUpdate = ForeignKey.CASCADE
			)
		],
		indices = [Index("formatterID")]
)
data class DBNovelEntity(
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
		var status: Novel.Status = Novel.Status.UNKNOWN,
) : Convertible<NovelEntity> {
	override fun convertTo(): NovelEntity = NovelEntity(
			id,
			url,
			formatterID,
			bookmarked,
			loaded,
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
}
