package com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.*
import com.github.doomsdayrs.apps.shosetsu.backend.FormatterUtils
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


/**
 * This class represents a formatter
 */
@Entity(
		tableName = "extensions",
		foreignKeys = [
			ForeignKey(
					entity = RepositoryEntity::class,
					parentColumns = ["id"],
					childColumns = ["repositoryID"],
					onDelete = ForeignKey.CASCADE
			)
		],
		indices = [Index("repositoryID")]
)

data class ExtensionEntity(
		@PrimaryKey val formatterID: Int,
		val repositoryID: Int,
		@NonNull var name: String = "",
		@NonNull val fileName: String = "",
		var imageURL: String? = null,
		var lang: String = "",
		var enabled: Boolean = false,
		var installed: Boolean = false,
		var installedVersion: String? = null,
		var repositoryVersion: String = "0.0.0",
		var md5: String = ""
) : Serializable {
	@Ignore
	fun install(context: Context) {
		FormatterUtils.installExtension(this, context)
	}

	@Ignore
	fun delete(context: Context) =
			FormatterUtils.deleteFormatter(this, context)
}

/**
 * This class represents a library that is installed in system
 */
@Entity(tableName = "libs",
		foreignKeys = [
			ForeignKey(
					entity = RepositoryEntity::class,
					parentColumns = ["id"],
					childColumns = ["repositoryID"],
					onDelete = ForeignKey.CASCADE
			)
		],
		indices = [Index("repositoryID")])
data class ExtensionLibraryEntity(
		@PrimaryKey
		val scriptName: String,
		var version: String,
		var repositoryID: Int
) : Serializable

@Entity(tableName = "repositories")
data class RepositoryEntity(
		var url: String,
		var name: String
) : Serializable {
	@PrimaryKey(autoGenerate = true)
	var id: Int = 0
}

data class CountIDTuple(
		@ColumnInfo(name = "COUNT(*)") val count: Int,
		@ColumnInfo(name = "id") val id: Int
) : Serializable