package app.shosetsu.android.domain.model.local

import app.shosetsu.lib.Version

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
 * Shosetsu
 *
 * @since 29 / 01 / 2022
 * @author Doomsdayrs
 *
 * Represents what can be installed.
 * This is presented to a user when they want to install an extension
 *
 * @param repoId What repository this belongs to
 * @param repoName The name of the repository
 * @param version The version of the extension in the repository
 */
data class ExtensionInstallOptionEntity(
	val repoId: Int,
	val repoName: String,
	val version: Version
)
