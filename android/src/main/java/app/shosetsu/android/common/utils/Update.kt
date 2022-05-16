package app.shosetsu.android.common.utils

import app.shosetsu.android.domain.model.local.AppUpdateEntity

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
 *
 * @since 08 / 03 / 2022
 * @author Doomsdayrs
 */

/**
 * Attempt to get the proper update url
 */
fun AppUpdateEntity.archURL() =
	if (archURLs != null) {
		when (System.getProperty("os.arch")) {
			"armeabi-v7a" -> archURLs.`armeabi-v7a`
			"arm64-v8a" -> archURLs.`arm64-v8a`
			"x86" -> archURLs.x86
			"x86_64" -> archURLs.x86_64
			else -> url // default to using the universal APK
		}
	} else url