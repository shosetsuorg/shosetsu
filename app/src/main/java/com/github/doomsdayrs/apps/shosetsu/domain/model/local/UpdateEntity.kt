package com.github.doomsdayrs.apps.shosetsu.domain.model.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.UpdateUI
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
@Entity(tableName = "updates",
		foreignKeys = [
			ForeignKey(
					entity = ChapterEntity::class,
					parentColumns = ["id"],
					childColumns = ["chapterID"],
					onDelete = ForeignKey.CASCADE
			)
		],
		indices = [Index("chapterID")]
)
data class UpdateEntity(
		@PrimaryKey
		val chapterID: Int,
		val novelID: Int,
		val time: Long
) : Serializable, Convertible<UpdateUI> {
	override fun convertTo(): UpdateUI =
			UpdateUI(chapterID, novelID, time)
}