package app.shosetsu.android.domain.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.shosetsu.android.domain.model.local.ExtLibEntity
import app.shosetsu.android.dto.Convertible
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
	tableName = "libs",
)
data class DBExtLibEntity(
	@PrimaryKey
	val scriptName: String,
	var version: Version,
	var repoID: Int,
) : Convertible<ExtLibEntity> {
	override fun convertTo(): ExtLibEntity = ExtLibEntity(scriptName, version, repoID)
}
