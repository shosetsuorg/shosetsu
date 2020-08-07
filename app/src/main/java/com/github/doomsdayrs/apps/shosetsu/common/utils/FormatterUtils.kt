package com.github.doomsdayrs.apps.shosetsu.common.utils

import android.content.Context
import com.github.doomsdayrs.apps.shosetsu.common.consts.libraryDirectory
import com.github.doomsdayrs.apps.shosetsu.common.consts.scriptDirectory
import com.github.doomsdayrs.apps.shosetsu.common.consts.sourceFolder
import com.github.doomsdayrs.apps.shosetsu.common.utils.base.IFormatterUtils
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtLibEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import java.io.File


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
 * 18 / 01 / 2020
 * [FormatterUtils] provides a class that handles all basic needs for extensions
 */
class FormatterUtils(
		val context: Context
) : IFormatterUtils {
	companion object {
		private fun splitVersion(version: String): Array<String> =
				version.split(".").toTypedArray()

		/**
		 * @return [Boolean] true if version difference
		 */
		fun compareVersions(ver1: String, ver2: String): Boolean {
			if (ver1 == ver2)
				return false

			val version1 = splitVersion(ver1)
			val version2 = splitVersion(ver2)

			if (version1.size != version2.size)
				return false

			version1.forEachIndexed { index, s ->
				if (version2[index] != s)
					return true
			}
			return false
		}
	}

	/** AbsolutePath of application file directory */
	val ap: String = context.filesDir.absolutePath

	override fun makeLibraryFile(extLibEntity: ExtLibEntity): File =
			makeLibraryFile(extLibEntity.scriptName)

	override fun makeLibraryFile(fileName: String): File {
		val f = File("$ap$sourceFolder$libraryDirectory$fileName.lua")
		f.parentFile?.let { if (!it.exists()) it.mkdirs() }
		return f
	}

	override fun makeFormatterFile(extensionEntity: ExtensionEntity): File =
			makeFormatterFile(extensionEntity.fileName)

	override fun makeFormatterFile(fileName: String): File {
		val f = File("$ap$sourceFolder$scriptDirectory$fileName.lua")
		f.parentFile?.let { if (!it.exists()) it.mkdirs() }
		return f
	}

	/**
	 * Loads the formatters
	 */
	override suspend fun initalize() {
	}
}