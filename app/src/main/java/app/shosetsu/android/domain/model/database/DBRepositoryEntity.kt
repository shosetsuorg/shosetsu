package app.shosetsu.android.domain.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.shosetsu.common.com.dto.Convertible
import app.shosetsu.common.domain.model.local.RepositoryEntity

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
@Entity(tableName = "repositories")
data class DBRepositoryEntity(
		@PrimaryKey(autoGenerate = true)
		val id: Int = -1,
		val url: String,
		var name: String,
) : Convertible<RepositoryEntity> {
	override fun convertTo(): RepositoryEntity = RepositoryEntity(
			id,
			url,
			name
	)
}
