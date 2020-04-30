package com.github.doomsdayrs.apps.shosetsu.domain.model.local

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ExtensionUI
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
 * This class represents a formatter
 */
@Entity(
		tableName = "extensions",
		foreignKeys = [
			ForeignKey(
					entity = RepositoryEntity::class,
					parentColumns = ["id"],
					childColumns = ["repoID"],
					onDelete = ForeignKey.CASCADE
			)
		],
		indices = [Index("repoID")]
)

data class ExtensionEntity(
		@PrimaryKey val id: Int,
		val repoID: Int,
		@NonNull var name: String = "",
		@NonNull val fileName: String = "",
		var imageURL: String? = null,
		var lang: String = "",
		var enabled: Boolean = false,
		var installed: Boolean = false,
		var installedVersion: String? = null,
		var repositoryVersion: String = "0.0.0",
		var md5: String = ""
) : Serializable, Convertible<ExtensionUI> {
	override fun convertTo(): ExtensionUI =
			ExtensionUI(
					id,
					repoID,
					name,
					fileName,
					imageURL,
					lang,
					enabled,
					installed,
					installedVersion,
					repositoryVersion,
					md5
			)
}