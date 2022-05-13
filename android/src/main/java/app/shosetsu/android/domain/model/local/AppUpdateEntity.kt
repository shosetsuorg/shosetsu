package app.shosetsu.android.domain.model.local

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
 * 19 / 12 / 2020
 */
data class AppUpdateEntity(
	val version: String,
	val versionCode: Int = -1,
	val commit: Int = -1,
	val url: String,
	val archURLs: ArchitectureURLs? = null,
	val notes: List<String>,
)

data class ArchitectureURLs(
	val `armeabi-v7a`: String,
	val `arm64-v8a`: String,
	val x86: String,
	val x86_64: String,
)