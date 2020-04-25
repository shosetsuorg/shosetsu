package com.github.doomsdayrs.apps.shosetsu.providers.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.shosetsu.lib.Formatter
import java.io.Serializable

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
 * 22 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Entity(tableName = "downloads",
		foreignKeys = [
			ForeignKey(
					entity = ChapterEntity::class,
					parentColumns = ["id"],
					childColumns = ["chapterID"],
					onDelete = ForeignKey.CASCADE
			),
			ForeignKey(
					entity = ChapterEntity::class,
					parentColumns = ["link"],
					childColumns = ["chapterURL"],
					onDelete = ForeignKey.CASCADE
			)
		],
		indices = [Index("chapterID"), Index("chapterURL")]
)
data class DownloadEntity(
		@PrimaryKey
		val chapterID: Int,
		val chapterURL: String,
		val chapterName: String,
		val novelName: String,
		val formatter: Formatter,
		var status: Int = 0
) : Serializable