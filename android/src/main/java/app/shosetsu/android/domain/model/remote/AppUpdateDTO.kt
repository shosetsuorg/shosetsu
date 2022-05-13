package app.shosetsu.android.domain.model.remote

import app.shosetsu.android.domain.model.local.AppUpdateEntity
import app.shosetsu.android.domain.model.local.ArchitectureURLs
import app.shosetsu.android.domain.model.remote.ArchitectureURLsDTO.Companion.toEntity
import app.shosetsu.android.dto.Convertible
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
	@SerialName("versionCode")
	val versionCode: Int = -1,

	@SerialName("latestVersion")
	val version: String,

	@SerialName("commit")
	val commit: Int = -1,

	@SerialName("url")
	val url: String,

	@SerialName("archURLs")
	val archURLs: ArchitectureURLsDTO? = null,

	@SerialName("releaseNotes")
	val notes: List<String>,
) : Convertible<AppUpdateEntity> {
	override fun convertTo() = AppUpdateEntity(
		version = version,
		versionCode = versionCode,
		commit = commit,
		url = url,
		archURLs = archURLs?.convertTo(),
		notes = notes
	)

	companion object {
		fun fromEntity(appUpdateEntity: AppUpdateEntity): AppUpdateDTO {
			return appUpdateEntity.let { (version, versionCode, commit, url, archURLs, notes) ->
				AppUpdateDTO(
					versionCode,
					version,
					commit,
					url,
					archURLs?.toEntity(),
					notes
				)
			}
		}
	}
}

@Serializable
data class ArchitectureURLsDTO(
	val `armeabi-v7a`: String,
	val `arm64-v8a`: String,
	val x86: String,
	val x86_64: String,
) : Convertible<ArchitectureURLs> {
	override fun convertTo(): ArchitectureURLs =
		ArchitectureURLs(`armeabi-v7a`, `arm64-v8a`, x86, x86_64)

	companion object {
		fun ArchitectureURLs.toEntity() =
			ArchitectureURLsDTO(
				`armeabi-v7a`,
				`arm64-v8a`,
				x86,
				x86_64
			)
	}
}