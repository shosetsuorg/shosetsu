package app.shosetsu.android.domain.model.database

import androidx.annotation.NonNull
import androidx.room.*
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.dto.Convertible
import app.shosetsu.common.enums.ReadingStatus

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
@Entity(
	tableName = "chapters",
	foreignKeys = [
		ForeignKey(
			entity = DBNovelEntity::class,
			parentColumns = ["id"],
			childColumns = ["novelID"],
			onDelete = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = DBExtensionEntity::class,
			parentColumns = ["id"],
			childColumns = ["formatterID"],
			onDelete = ForeignKey.SET_NULL,
			onUpdate = ForeignKey.CASCADE
		)
	],
	indices = [Index("novelID"), Index("url", unique = true), Index("formatterID")]
)
data class DBChapterEntity(
	@PrimaryKey(autoGenerate = true)
	var id: Int? = null,

	@NonNull
	var url: String,

	@NonNull
	val novelID: Int,

	@ColumnInfo(name = "formatterID")
	val extensionID: Int,

	@NonNull
	var title: String,

	@NonNull
	var releaseDate: String,

	var order: Double,

	var readingPosition: Double = 0.0,

	var readingStatus: ReadingStatus = ReadingStatus.UNREAD,

	var bookmarked: Boolean = false,

	var isSaved: Boolean = false,
) : Convertible<ChapterEntity> {
	override fun convertTo(): ChapterEntity = ChapterEntity(
		id,
		url,
		novelID,
		extensionID,
		title,
		releaseDate,
		order,
		readingPosition,
		readingStatus,
		bookmarked,
		isSaved
	)
}
