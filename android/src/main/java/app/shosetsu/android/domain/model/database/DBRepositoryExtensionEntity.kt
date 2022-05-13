package app.shosetsu.android.domain.model.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import app.shosetsu.android.domain.model.local.GenericExtensionEntity
import app.shosetsu.android.dto.Convertible
import app.shosetsu.lib.ExtensionType
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
 *
 */
@Entity(
	tableName = "repository_extension",
	foreignKeys = [
		/**
		 * We cascade delete the repository version of the extension when the repository is removed.
		 * This ensures data is cleaned up properly.
		 * No entity should relate to this entity.
		 */
		ForeignKey(
			entity = DBRepositoryEntity::class,
			parentColumns = ["id"],
			childColumns = ["repoId"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [
		Index("repoId"),
	],
	/**
	 * Both repoId & id make a primary key.
	 */
	primaryKeys = ["repoId", "id"]
)

data class DBRepositoryExtensionEntity(
	/** Repository extension belongs too*/
	@NonNull
	val repoId: Int,

	/** Extension ID */
	@NonNull
	val id: Int,

	/** Name of the extension, can be changed */
	@NonNull
	var name: String,

	/** FileName of the extension */
	@NonNull
	val fileName: String,

	/** Image URL of the extension*/
	@NonNull
	var imageURL: String,

	/** The language of the extension */
	@NonNull
	val lang: String,

	@NonNull
	val version: Version,

	/** MD5 to check against */
	@NonNull
	var md5: String,

	@NonNull
	val type: ExtensionType
) : Convertible<GenericExtensionEntity> {
	override fun convertTo(): GenericExtensionEntity = GenericExtensionEntity(
		id,
		repoId,
		name,
		fileName,
		imageURL,
		lang,
		version,
		md5,
		type
	)
}