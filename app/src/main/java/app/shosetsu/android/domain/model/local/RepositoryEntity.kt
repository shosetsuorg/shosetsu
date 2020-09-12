package app.shosetsu.android.domain.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.shosetsu.android.domain.model.base.Convertible
import app.shosetsu.android.view.uimodels.model.RepositoryUI
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
@Entity(tableName = "repositories")
data class RepositoryEntity(
		var url: String,
		var name: String,
) : Serializable, Convertible<RepositoryUI> {
	@PrimaryKey(autoGenerate = true)
	var id: Int = 0

	override fun convertTo(): RepositoryUI =
			RepositoryUI(url, name)
}