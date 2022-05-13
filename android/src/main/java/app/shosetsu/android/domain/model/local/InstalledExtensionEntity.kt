package app.shosetsu.android.domain.model.local

import app.shosetsu.lib.ExtensionType
import app.shosetsu.lib.Novel
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
 * @since 10 / 02 / 2022
 * @author Doomsdayrs
 */
data class InstalledExtensionEntity(
	/** Extension ID */
	val id: Int,

	/** Repository extension belongs too*/
	val repoID: Int,

	/** Name of the extension, can be changed */
	val name: String,

	/** FileName of the extension */
	val fileName: String,

	/** Image URL of the extension*/
	val imageURL: String,

	/** The language of the extension */
	val lang: String,

	/**
	 * Version currently installed
	 */
	val version: Version,

	/** MD5 to check against */
	val md5: String,

	val type: ExtensionType,

	/** If extension is enabled */
	val enabled: Boolean,

	/**
	 * The reader type of this extension
	 */
	val chapterType: Novel.ChapterType,
)
