package app.shosetsu.android.domain.model.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.shosetsu.common.enums.ReaderType
import app.shosetsu.common.enums.TextSizes

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
 * 03 / 01 / 2021
 */
@Entity(
	tableName = "novel_reader_settings",
	foreignKeys = [
		ForeignKey(
			entity = DBNovelEntity::class,
			parentColumns = ["id"],
			childColumns = ["novelID"],
			onDelete = ForeignKey.CASCADE
		),
	],
	indices = [Index("novelID", unique = true)]
)
data class DBNovelReaderSettingEntity(
	@PrimaryKey
	val novelID: Int,

	// how the reader is set-up
	@NonNull
	var type: ReaderType,

	@NonNull
	var themeChoice: Int,

	@NonNull
	var textSize: TextSizes,

	@NonNull
	var paragraphIndentSize: Int,

	@NonNull
	var paragraphSpacingSize: Int,

	@NonNull
	var volumeScrolling: Boolean,

	@NonNull
	var tapToScroll: Boolean
)
