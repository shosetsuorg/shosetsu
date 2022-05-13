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
 * The data needed for a user facing extension
 *
 * @param id Universal identification of this extension.
 *
 * @param name Title of the extension,
 * if not installed this will be from the repo with the highest version.
 *
 * @param lang Language of the extension,
 * if not installed this will be from the repo with the highest version.
 *
 * @param imageURL Image content to display as the icon,
 * if not installed this will be from the repo with the highest version.
 *
 * @param isInstalled Is this extension already installed or not,
 * if it is installed then display a settings option & display [installedVersion] to the user.
 *
 * @param installedVersion Version that is currently installed.
 *
 * @param installedRepo Which repository is the extension currently installed from
 *
 * @param installOptions What options are available to install,
 * this is only populated if the extension is not installed.
 *
 * @param updateVersion Version to update to
 *
 * @param isInstalling True if the extension is currently being installed
 */
data class BrowseExtensionEntity(
	val id: Int,
	val name: String,
	val imageURL: String,
	val lang: String,
	val installOptions: List<ExtensionInstallOptionEntity>? = null,
	val isInstalled: Boolean,
	val installedVersion: Version? = null,
	val installedRepo: Int,
	val isUpdateAvailable: Boolean,
	val updateVersion: Version? = null,
	val isInstalling: Boolean
)
