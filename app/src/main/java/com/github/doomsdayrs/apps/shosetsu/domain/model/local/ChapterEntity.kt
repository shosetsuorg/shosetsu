package com.github.doomsdayrs.apps.shosetsu.domain.model.local

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterUI

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
@Entity(tableName = "chapters",
		foreignKeys = [
			ForeignKey(
					entity = NovelEntity::class,
					parentColumns = ["id"],
					childColumns = ["novelID"],
					onDelete = ForeignKey.CASCADE
			),
			ForeignKey(
					entity = ExtensionEntity::class,
					parentColumns = ["id"],
					childColumns = ["formatterID"],
					onDelete = ForeignKey.SET_NULL,
					onUpdate = ForeignKey.CASCADE
			)
		],
		indices = [Index("novelID"), Index("url", unique = true), Index("formatterID")]
)
data class ChapterEntity(
		@PrimaryKey(autoGenerate = true)
		var id: Int? = null,

		@NonNull
		val url: String,

		@NonNull
		val novelID: Int,

		val formatterID: Int,

		@NonNull
		var title: String,

		@NonNull
		var releaseDate: String,

		var order: Double,

		var readingPosition: Int = 0,

		var readingStatus: ReadingStatus = ReadingStatus.UNREAD,

		var bookmarked: Boolean = false,

		var isSaved: Boolean = false,

		var savePath: String = ""
) : Convertible<ChapterUI> {


	override fun convertTo(): ChapterUI =
			ChapterUI(
					id!!,
					novelID,
					url,
					formatterID,
					title,
					releaseDate,
					order,
					readingPosition,
					readingStatus,
					bookmarked,
					isSaved,
					savePath
			)
}