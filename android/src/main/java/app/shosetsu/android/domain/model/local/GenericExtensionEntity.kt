package app.shosetsu.android.domain.model.local

import app.shosetsu.lib.ExtensionType
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
 * ====================================================================
 */

/**
 * shosetsu
 * 22 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 *
 * This represents a single extension,
 * with data that is common to both Installed and Repository types
 */
data class GenericExtensionEntity(
	/** Extension ID */
	val id: Int,

	/** Repository extension belongs too*/
	val repoID: Int,

	/** Name of the extension, can be changed */
	val name: String = "",

	/** FileName of the extension */
	val fileName: String = "",

	/** Image URL of the extension*/
	val imageURL: String,

	/** The language of the extension */
	val lang: String = "",

	val version: Version,

	/** MD5 to check against */
	val md5: String = "",

	/**
	 * What language was used to create this extension
	 */
	val type: ExtensionType
)