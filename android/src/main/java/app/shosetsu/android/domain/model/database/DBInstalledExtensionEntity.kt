package app.shosetsu.android.domain.model.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.shosetsu.android.domain.model.local.InstalledExtensionEntity
import app.shosetsu.android.dto.Convertible
import app.shosetsu.lib.ExtensionType
import app.shosetsu.lib.Novel
import app.shosetsu.lib.Version

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
	tableName = "installed_extension",
)
data class DBInstalledExtensionEntity(
	/** Extension ID */
	@PrimaryKey
	val id: Int,

	/** Repository extension belongs too*/
	val repoID: Int,

	/** Name of the extension, can be changed */
	@NonNull
	var name: String = "",

	/** FileName of the extension */
	@NonNull
	val fileName: String = "",

	/** Image URL of the extension*/
	var imageURL: String,

	/** The language of the extension */
	val lang: String = "",

	/**
	 * Version currently installed
	 */
	var version: Version,

	/** MD5 to check against */
	var md5: String = "",

	val type: ExtensionType,

	/** If extension is enabled */
	var enabled: Boolean = false,

	/**
	 * The reader type of this extension
	 */
	var chapterType: Novel.ChapterType,
) : Convertible<InstalledExtensionEntity> {
	override fun convertTo(): InstalledExtensionEntity = InstalledExtensionEntity(
		id = id,
		repoID = repoID,
		name = name,
		fileName = fileName,
		imageURL = imageURL,
		lang = lang,
		enabled = enabled,
		version = version,
		chapterType = chapterType,
		md5 = md5,
		type = type
	)
}
