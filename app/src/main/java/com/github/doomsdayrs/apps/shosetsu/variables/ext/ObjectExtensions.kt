package com.github.doomsdayrs.apps.shosetsu.variables.ext

import android.content.SharedPreferences
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager.getChapterText
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.variables.HandledReturns

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
 * 08 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */


fun SharedPreferences.Editor.putString(prefKeys: Utilities.PrefKeys, string: String?): SharedPreferences.Editor {
    return putString(prefKeys.toString(), string)
}

fun SharedPreferences.Editor.putInt(prefKeys: Utilities.PrefKeys, int: Int): SharedPreferences.Editor {
    return putInt(prefKeys.toString(), int)
}


fun SharedPreferences.getInt(prefKeys: Utilities.PrefKeys, default: Int = 0): Int {
    return getInt(prefKeys.toString(), default)
}


/**
 * Gets the novel from local storage
 *
 * @param chapterID novelURL of the chapter
 * @return String of passage
 */
fun Database.DatabaseChapter.getSavedNovelPassage(chapterID: Int): HandledReturns<String> {
    return getChapterText(getSavedNovelPath(chapterID))
}