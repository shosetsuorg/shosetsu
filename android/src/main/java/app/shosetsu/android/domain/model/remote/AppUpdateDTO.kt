package app.shosetsu.android.domain.model.remote

import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.common.dto.Convertible
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
 */

/**
 * shosetsu
 * 07 / 09 / 2020
 *
 * JSON DTO layer, should be converted to an AppUpdateEntity
 */
@Serializable
data class AppUpdateDTO(
	@SerialName("latestVersion")
	val version: String,
	@SerialName("versionCode")
	val versionCode: Int = -1,
	@SerialName("url")
	val url: String,
	@SerialName("releaseNotes")
	val notes: List<String>,
) : Convertible<AppUpdateEntity> {
	override fun convertTo() = AppUpdateEntity(version, versionCode, url, notes)

	companion object {
		fun fromEntity(appUpdateEntity: AppUpdateEntity) = AppUpdateDTO(
			appUpdateEntity.version,
			appUpdateEntity.versionCode,
			appUpdateEntity.url,
			appUpdateEntity.notes
		)
	}
}