package com.github.doomsdayrs.apps.shosetsu.backend.database.room

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
 * 18 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Entity(
		tableName = "formatters",
		foreignKeys = [
			ForeignKey(
					entity = FRepositoryEntity::class,
					parentColumns = ["id"],
					childColumns = ["repositoryID"],
					onDelete = ForeignKey.NO_ACTION
			)
		]
)
data class FormatterEntity(
		@PrimaryKey
		var formatterID: Int = -1,

		var repositoryID: Int = -1,

		@NonNull
		var name: String = "",

		var enabled: Boolean = false,

		var installed: Boolean = false,

		var internal: Boolean = true,

		@NonNull
		var fileName: String = "") : Serializable

@Entity(tableName = "repositories")
data class FRepositoryEntity(
		@PrimaryKey
		var id: Int = -1,
		var name: String = "",
		var url: String = ""
) : Serializable