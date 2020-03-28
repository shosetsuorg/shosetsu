package com.github.doomsdayrs.apps.shosetsu.variables.ext

import android.app.Activity
import android.database.sqlite.SQLiteException
import android.util.Log
import android.view.ViewGroup
import androidx.core.view.get
import com.github.doomsdayrs.api.shosetsu.services.core.*
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager.getChapterText
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.secondDrawer.*
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.variables.HandledReturns
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

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

/**
 * Gets the novel from local storage
 *
 * @param chapterID novelURL of the chapter
 * @return String of passage
 */
@Throws(MissingResourceException::class)
fun Database.DatabaseChapter.getSavedNovelPassage(chapterID: Int): HandledReturns<String> {
    return getChapterText(getSavedNovelPath(chapterID))
}

fun MissingResourceException.handle(logID: String) = Log.e(logID, "A resource was missing", this)
fun SQLiteException.handle(logID: String) = Log.e(logID, "Database threw an error", this)

fun Activity.readAsset(name: String): String {
    val string = StringBuilder()
    try {
        val reader = BufferedReader(InputStreamReader(assets.open(name)))
        // do reading, usually loop until end of file reading
        var mLine: String? = reader.readLine()
        while (mLine != null) {
            string.append("\n").append(mLine)
            mLine = reader.readLine()
        }
        reader.close()
    } catch (e: IOException) {
        Log.e(javaClass.name, "Failed to read asset of $name", e)
    }
    return string.toString()
}