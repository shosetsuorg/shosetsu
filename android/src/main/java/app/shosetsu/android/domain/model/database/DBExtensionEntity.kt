package app.shosetsu.android.domain.model.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.dto.Convertible
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
	tableName = "extensions",
	foreignKeys = [
		ForeignKey(
			entity = DBRepositoryEntity::class,
			parentColumns = ["id"],
			childColumns = ["repoID"],
			onDelete = ForeignKey.NO_ACTION
		)
	],
	indices = [Index("repoID")]
)

data class DBExtensionEntity(
	/** Extension ID */
	@PrimaryKey val id: Int,

	/** Repository extension belongs too*/
	val repoID: Int,

	/** Name of the extension, can be changed */
	@NonNull var name: String = "",

	/** FileName of the extension */
	@NonNull val fileName: String = "",

	/** Image URL of the extension*/
	var imageURL: String? = null,

	/** The language of the extension */
	val lang: String = "",

	/** If extension is enabled */
	var enabled: Boolean = false,

	/** If extension is installed*/
	var installed: Boolean = false,

	/** Version currently installed */
	var installedVersion: Version? = null,

	/** Version in repository*/
	var repositoryVersion: Version = Version(0, 0, 0),

	/**
	 * The reader type of this extension
	 */
	var chapterType: Novel.ChapterType,

	/** MD5 to check against */
	var md5: String = "",

	val type: ExtensionType
) : Convertible<ExtensionEntity> {
	override fun convertTo(): ExtensionEntity = ExtensionEntity(
		id = id,
		repoID = repoID,
		name = name,
		fileName = fileName,
		imageURL = imageURL,
		lang = lang,
		enabled = enabled,
		installed = installed,
		installedVersion = installedVersion,
		repositoryVersion = repositoryVersion,
		chapterType = chapterType,
		md5 = md5,
		type = type
	)
}
