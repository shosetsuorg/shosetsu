package com.github.doomsdayrs.apps.shosetsu.variables

import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.variables.ext.clean

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
 * ====================================================================
 */
/**
 * Shosetsu
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 *
 *
 * These items symbolize download items and their data
 */
@Deprecated("ROOM", replaceWith = ReplaceWith("DownloadEntity"), level = DeprecationLevel.ERROR)
class DownloadItem(val formatter: Formatter, novelName: String, chapterName: String, val chapterID: Int) {

	val novelName: String = novelName.clean()
	val chapterName: String = chapterName.clean()
	val chapterURL: String = DatabaseIdentification.getChapterURLFromChapterID(chapterID)

	//Variables only for download manager
	var status = "Pending"

}