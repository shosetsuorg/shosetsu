package app.shosetsu.android.domain.model.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.shosetsu.android.domain.model.local.CategoryEntity
import app.shosetsu.android.dto.Convertible

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
 * 08 / 08 / 2022
 */
@Entity(
	tableName = "categories",
)
data class DBCategoryEntity(
	@PrimaryKey(autoGenerate = true)
	/** ID of this category */
	val id: Int? = null,

	/** Name of this category */
	@NonNull
	val name: String,

	/** order of this category */
	@NonNull
	val order: Int,
) : Convertible<CategoryEntity> {
	override fun convertTo(): CategoryEntity = CategoryEntity(
		id,
		name,
		order
	)
}
