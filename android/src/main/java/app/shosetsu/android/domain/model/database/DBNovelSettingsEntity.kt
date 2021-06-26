package app.shosetsu.android.domain.model.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.dto.Convertible
import app.shosetsu.common.enums.ChapterSortType
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
 * 02 / 01 / 2021
 */
@Entity(
	tableName = "novel_settings",
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
data class DBNovelSettingsEntity(
	@PrimaryKey
	val novelID: Int,

	// how chapters are sorted
	@NonNull
	var sortType: ChapterSortType,

	var showOnlyReadingStatusOf: ReadingStatus?,

	@NonNull
	var showOnlyBookmarked: Boolean = false,

	@NonNull
	var showOnlyDownloaded: Boolean = false,

	@NonNull
	var reverseOrder: Boolean = false,


	) : Convertible<NovelSettingEntity> {
	override fun convertTo(): NovelSettingEntity = NovelSettingEntity(
		novelID,
		sortType,
		showOnlyReadingStatusOf,
		showOnlyBookmarked,
		showOnlyDownloaded,
		reverseOrder,
	)
}